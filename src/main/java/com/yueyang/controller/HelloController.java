package com.yueyang.controller;

import com.yueyang.AjaxResult;
import joinery.DataFrame;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * -
 *
 * @author：yueyang
 * @date：Created in 2020/10/29 16:16
 */
@Controller
public class HelloController {

    @RequestMapping("/index")
    public String sayHello() {
        return "allcharts";
    }

    @RequestMapping("/demo")
    public String myDemo() {
        return "demo";
    }

    @RequestMapping("/echarts")
    public String myECharts(Model model, String ts_code, Long end_date) throws IOException {
//        AjaxResult jlrList = getJlrList(ts_code, end_date);
//
//        model.addAttribute("skirt", jlrList.get("dates"));
//        model.addAttribute("nums", jlrList.get("jlr"));
//        model.addAttribute("xse", jlrList.get("xse"));

        return "echarts";
    }

    @RequestMapping("/allcharts")
    public String allCharts(Model model, String period) throws IOException {
        return "allcharts";
    }

    private int getColumnIndex(Set<Object> columns, String columnName) {
        Iterator<Object> iterator = columns.iterator();//新建一个迭代器
        int colIndex = 0;
        while (iterator.hasNext()) {
            colIndex += 1;
            String name = iterator.next().toString();
            if (name.equals(columnName))
                break;
        }
        return colIndex;
    }

    @RequestMapping("/getData")
    @ResponseBody
    public AjaxResult getData(String ts_code, Long end_date) throws IOException {
        AjaxResult jlrList = getJlrList(ts_code, end_date);
        return AjaxResult.success(jlrList);
    }

    @RequestMapping("/getTsCodes")
    @ResponseBody
    private AjaxResult getTsCodes(String period) throws IOException {
        DataFrame<Object> df = DataFrame.readXls("e:\\stocks\\easymony_fina_analyze\\" + period + "-筛选.xls");
        Set<Object> columns = df.columns();
        List<Object> secucode = df.col(getColumnIndex(columns, "SECUCODE"));
        AjaxResult result = AjaxResult.success().put("codes", secucode);
        return result;
    }

    private AjaxResult getJlrList(String ts_code, Long end_date) throws IOException {
        DataFrame<Object> df = DataFrame.readXls("e:\\stocks\\easymony_fina\\" + ts_code + ".xls");
        Set<Object> columns = df.columns();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        DataFrame<Object> select = df.select(new DataFrame.Predicate<Object>() {
            @Override
            public Boolean apply(List<Object> values) {
                int colIndex = getColumnIndex(columns, "REPORTDATE");
                //df.columns.get("REPORTDATE");
                String value = ft.format(values.get(colIndex));
//                String value = ((String) values.get(colIndex)).replace("-", "").replace(" 00:00:00", "");
                if (value.equals("")) {
                    return false;
                }
                return Long.parseLong(value) <= end_date;
            }
        });
        int x = Math.min(8, select.length() - 1);
        List<String> dates = new ArrayList<>();
        List<Double> jlrs = new ArrayList<>();
        List<Double> xses = new ArrayList<>();
        Double pre_jlr = 0.0;
        int nowYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = x; i >= 0; i--) {
            String period = select.col(getColumnIndex(columns, "QDATE")).get(i).toString();
            dates.add(period);
            Double jlr = (Double) select.col(getColumnIndex(columns, "PARENT_NETPROFIT")).get(i);
            Double xse = (Double) select.col(getColumnIndex(columns, "TOTAL_OPERATE_INCOME")).get(i);
//            jlrs.add(jlr);
            xses.add(xse);

            int year = Integer.parseInt(period.substring(0, 4));
            String q = period.substring(4, 6);
            if (!q.equals("Q1")) {
                jlrs.add(jlr - pre_jlr);
            } else {
                jlrs.add(jlr);
            }
            pre_jlr = jlr;
        }

        String noticeDate = ft.format(select.col(getColumnIndex(columns, "NOTICE_DATE")).get(0));

        AjaxResult result = AjaxResult.success()
                .put("noticeDate", noticeDate)
                .put("dates", dates)
                .put("jlr", jlrs)
                .put("xse", xses);
        return result;
    }

    @PostMapping("upload")
    public String fileUpload(@RequestParam("file") MultipartFile srcFile, RedirectAttributes redirectAttributes) {
        if (srcFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "请选择一个文件");
            return "index";
        }
        try {
            File destFile = new File("d:\\temp\\");
            if (!destFile.exists()) {
                destFile = new File("");
            }
            System.out.println("file path:" + destFile.getAbsolutePath());
            SimpleDateFormat sf_ = new SimpleDateFormat("yyyyMMddHHmmss");
            String times = sf_.format(new Date());
            File upload = new File(destFile.getAbsolutePath(), "picture/" + times);
            if (!upload.exists()) {
                upload.mkdirs();
            }
            System.out.println("完整的上传路径：" + upload.getAbsolutePath() + "/" + srcFile);
            byte[] bytes = srcFile.getBytes();
            Path path = Paths.get(upload.getAbsolutePath() + "/" + srcFile.getOriginalFilename());
            Files.write(path, bytes);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String fileName = srcFile.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            String newFileName = uuid + "." + suffixName;
            redirectAttributes.addFlashAttribute("message", "文件上传成功" + newFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "index";
    }
}

