package com.tanshiyang.controller;

import com.tanshiyang.AjaxResult;
import joinery.DataFrame;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/StockIncomeForecast")
public class StockIncomeForecastController {
    @RequestMapping("/")
    public String index() {
        return "StockIncomeForecast/index";
    }

    @RequestMapping("/getFiles")
    @ResponseBody
    private AjaxResult getFiles(String dataType, Integer fileQty) throws IOException {
        if (fileQty == null) {
            fileQty = 365;
        }
        File dir = new File(getDataPath(dataType));
        if (dir.exists()) {
            File[] files = dir.listFiles();
            List<File> fileList = new ArrayList<File>();
            for (File f : files) {
                if (f.isDirectory()) {
                    continue;
                }
                fileList.add(f);
            }

            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return o2.getName().compareTo(o1.getName());
                }
            });

            List<String> resultFileList = new ArrayList<String>();
            for (File f : fileList) {
                if (resultFileList.size() >= fileQty) {
                    break;
                }
                if (f.getName().contains("预测盈利分析")) {
                    resultFileList.add(f.getName());
                }
            }

            AjaxResult result = AjaxResult.success().put("files", resultFileList);
            return result;
        } else {
            AjaxResult result = AjaxResult.error("目录不存在");
            return result;
        }
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

    @RequestMapping("/getFileData")
    @ResponseBody
    public AjaxResult getData(String dataType, String fileName, Integer qty) throws IOException {
        DataFrame<Object> df = DataFrame.readXls(getDataPath(dataType) + fileName);
        Set<Object> columns = df.columns();
        if (qty == null) {
            qty = 5;
        }
        int count = Math.min(qty, df.length() - 1);
        List<String> names = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name = df.col(getColumnIndex(columns, "name")).get(i).toString();
            name = name.replace("预测盈利分析-", "");
            Double value = (Double) df.col(getColumnIndex(columns, "净利较上年")).get(i);
            names.add(name);
            values.add(value);
        }

        HashMap<String, Double> prevValues = getPrevFileData(dataType, fileName);
        return AjaxResult.success().put("names", names)
                .put("values", values)
                .put("prevValues", prevValues);
    }

    private HashMap<String, Double> getPrevFileData(String dataType, String fileName) throws IOException {
        List<String> files = (List<String>) this.getFiles(dataType, 500).get("files");
        String prevFileName = null;
        for (int i = 0; i < files.size(); i++) {
            if (fileName.equals(files.get(i))) {
                if ((i + 1) < files.size()) {
                    prevFileName = files.get(i + 1);
                }
            }
        }
        if (prevFileName == null) {
            return null;
        }
        DataFrame<Object> df = DataFrame.readXls(getDataPath(dataType) + prevFileName);
        Set<Object> columns = df.columns();

        int count = df.length();
        HashMap<String, Double> prevValues = new HashMap<>();
        for (int i = 0; i < count; i++) {
            String name = df.col(getColumnIndex(columns, "name")).get(i).toString();
            name = name.replace("预测盈利分析-", "");
            Double value = (Double) df.col(getColumnIndex(columns, "净利较上年")).get(i);
            prevValues.put(name, value);
        }
        return prevValues;
    }

    private String getDataPath(String dataType) {
        if (dataType.equals("jlr")) {
            return "e:\\onedrive\\OneDrive\\文档\\stock\\盈利预测分析\\";
        } else {
            return "e:\\onedrive\\OneDrive\\文档\\stock\\盈利预测分析-每股收益\\";
        }
    }
}
