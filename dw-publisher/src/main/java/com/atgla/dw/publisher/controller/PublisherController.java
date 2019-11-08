package com.atgla.dw.publisher.controller;



//http://publisher:8070/realtime-total?date=2019-02-01
//http://publisher:8070/realtime-hour?id=dau&date=2019-02-01


// 总数       [{"id":"dau","name":"新增日活","value":1200},
//            {"id":"new_mid","name":"新增设备","value":233} ]

//  分时统计      {"yesterday":{"11":383,"12":123,"17":88,"19":200 },
//               "today":{"12":38,"13":1233,"17":123,"19":688 }}


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atgla.dw.publisher.service.PublisherService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class PublisherController {

    @Autowired
    PublisherService publisherService;

    @GetMapping("realtime-total")
    public String realtimeHourDate(@RequestParam("date") String date) {
        List<Map> list = new ArrayList<Map>();
        // 日活总数
        int dauTotal = publisherService.getDauTotal(date);
        Map dauMap = new HashMap<String, Object>();
        dauMap.put("id", "dau");
        dauMap.put("name", "新增日活");
        dauMap.put("value", dauTotal);
        list.add(dauMap);

        // 新增用户
        int newMidTotal = publisherService.getNewMidTotal(date);
        Map newMidMap = new HashMap<String, Object>();
        newMidMap.put("id", "new_mid");
        newMidMap.put("name", "新增用户");
        newMidMap.put("value", newMidTotal);
        list.add(newMidMap);

        //交易总额
        Map orderAmountMap = new HashMap();
        orderAmountMap.put("id", "order_amount");
        orderAmountMap.put("name", "新增交易额");
        Double orderAmount = publisherService.getOrderAmount(date);
        orderAmountMap.put("value", orderAmount);
        list.add(orderAmountMap);

        return JSON.toJSONString(list);
    }

    @GetMapping("realtime-hours")
    public String realtimeHourDate(@RequestParam("id") String id,
                                   @RequestParam("date") String date) {

        //日活分时统计

        if ("dau".equals(id)) {
            Map dauHoursToday = publisherService.getDauHours(date);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("today", dauHoursToday);
            String yesterdayDateString = "";
            try {
                Date dateToday = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                Date dateYesterday = DateUtils.addDays(dateToday, -1);
                yesterdayDateString = new SimpleDateFormat("yyyy-MM-dd").format(dateYesterday);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            Map dauHoursYesterday = publisherService.getDauHours(yesterdayDateString);
            jsonObject.put("yesterday", dauHoursYesterday);
            return jsonObject.toJSONString();
        }

        else if ("order_amount".equals(id)){
            Map orderAmountHoursToday=publisherService.getOrderAmountHour(date);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("today",orderAmountHoursToday);


            String yesterdayDateString2 = "";
            try {
                Date dateToday2 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                Date dateYesterday2 = DateUtils.addDays(dateToday2, -1);
                yesterdayDateString2 = new SimpleDateFormat("yyyy-MM-dd").format(dateYesterday2);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            Map orderAmountHoursYesterday=publisherService.getOrderAmountHour(yesterdayDateString2);
            jsonObject.put("yesterday",orderAmountHoursYesterday);
        }
//
//        if ("new_order_totalamount".equals(id)) {
//            String newOrderTotalamountJson = publisherService.getNewOrderTotalAmountHours(date);
//            return newOrderTotalamountJson;
//        }
//        return null;


        return id;
    }



    }


