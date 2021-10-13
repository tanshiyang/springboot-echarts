package com.tanshiyang;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanshiyang.rps.entity.RpsTops;
import com.tanshiyang.rps.mapper.RpsTopsMapper;
import com.tanshiyang.rps.service.IRpsTopsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringbootEchartsApplicationTests {
    @Autowired
    IRpsTopsService rpsTopsService;

    @Autowired
    RpsTopsMapper rpsTopsMapper;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testDao(){
        String topType = "m50";
        List<HashMap<String, Object>> hashMaps = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();

        LambdaQueryWrapper<RpsTops> tradeDatesQueryWrapper = new LambdaQueryWrapper<>();
        tradeDatesQueryWrapper.eq(RpsTops::getTopType, topType);
        tradeDatesQueryWrapper.groupBy(RpsTops::getTradeDate);
        tradeDatesQueryWrapper.orderByAsc(RpsTops::getTradeDate);
        tradeDatesQueryWrapper.select(RpsTops::getTradeDate);
        List<Object> objects = rpsTopsMapper.selectObjs(tradeDatesQueryWrapper);
        for (Object obj : objects) {
            String tradeDate = (String) obj;
            LambdaQueryWrapper<RpsTops> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(RpsTops::getTradeDate, tradeDate);
            lambdaQueryWrapper.eq(RpsTops::getTopType, topType);
            lambdaQueryWrapper.orderByDesc(RpsTops::getTradeDate).orderByDesc(RpsTops::getExtrs);
            lambdaQueryWrapper.last("limit 10");
            List<RpsTops> list = rpsTopsMapper.selectList(lambdaQueryWrapper);
            hashMap.put(tradeDate, list);
        }
        System.out.println(hashMap);
    }
}
