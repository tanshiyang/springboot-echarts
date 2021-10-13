package com.tanshiyang.rps.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanshiyang.AjaxResult;
import com.tanshiyang.rps.entity.RpsTops;
import com.tanshiyang.rps.mapper.RpsTopsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tanshiyang
 * @since 2021-10-12
 */
@Controller
@RequestMapping("/rps/rpsTops")
public class RpsTopsController {
    @Autowired
    RpsTopsMapper rpsTopsMapper;

    @RequestMapping("/")
    public String index() {
        return "rps/index";
    }

    @RequestMapping("/getRpsTopsJson")
    @ResponseBody
    private AjaxResult getRpsTopsJson(String topType) {
        List<Object> hashSet = new ArrayList<>();

        LambdaQueryWrapper<RpsTops> tradeDatesQueryWrapper = new LambdaQueryWrapper<>();
        tradeDatesQueryWrapper.eq(RpsTops::getTopType, topType);
        tradeDatesQueryWrapper.groupBy(RpsTops::getTradeDate);
        tradeDatesQueryWrapper.orderByDesc(RpsTops::getTradeDate);
        tradeDatesQueryWrapper.select(RpsTops::getTradeDate);
        tradeDatesQueryWrapper.last("limit 250");
        List<Object> objects = rpsTopsMapper.selectObjs(tradeDatesQueryWrapper);
        for (Object obj : objects) {
            String tradeDate = (String) obj;
            LambdaQueryWrapper<RpsTops> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(RpsTops::getTradeDate, tradeDate);
            lambdaQueryWrapper.eq(RpsTops::getTopType, topType);
            lambdaQueryWrapper.orderByDesc(RpsTops::getTradeDate).orderByDesc(RpsTops::getExtrs);
            lambdaQueryWrapper.last("limit 5");
            lambdaQueryWrapper.select(RpsTops::getTsCode);
            List<Object> list = rpsTopsMapper.selectObjs(lambdaQueryWrapper);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("tradeDate", tradeDate);
            hashMap.put("tscodes", list);
            hashSet.add(hashMap);
        }

        System.out.println(hashSet.size());
        return AjaxResult.success().put("data", hashSet);
    }
}

