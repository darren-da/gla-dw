package com.atgla.dw.publisher.service.impl;

import com.atgla.dw.publisher.mapper.DauMapper;
import com.atgla.dw.publisher.mapper.OrderMapper;
import com.atgla.dw.publisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class PublisherServiceImpl implements PublisherService {
    @Autowired
    DauMapper dauMapper;
    @Autowired
    OrderMapper orderMapper;

    @Override
    public Integer getDauTotal(String date) {
        return dauMapper.selectDauTotal(date);
    }

    @Override
    public Integer getNewMidTotal(String date) {
        return 0;
    }

    @Override
    public Map getDauHours(String date) {
        HashMap<Object, Object> dauHourMap = new HashMap<>();
        List<Map> dauHourList = dauMapper.selectDauTotalHourMap(date);
        for (Map map:dauHourList){
            dauHourMap.put(map.get("LH"),map.get("CT"));
        }

        return dauHourMap;
    }

    @Override
    public Double getOrderAmount(String date) {
        return orderMapper.selectOrderAmount(date);
    }

    @Override
    public Map getOrderAmountHour(String date) {
        List<Map> mapList = orderMapper.selectOrderAmountHourMap(date);
        Map orderAmountHourMap=new HashMap();
        for (Map map : mapList )

        {    orderAmountHourMap.put(map.get("CREATE_HOUR"),map.get("SUM_AMOUNT"));

        }
        return orderAmountHourMap;
    }


}
