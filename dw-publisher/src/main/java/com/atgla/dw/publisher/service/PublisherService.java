package com.atgla.dw.publisher.service;

import java.util.Map;

public interface PublisherService {

//查詢日活
    public Integer getDauTotal(String date );

    public Integer getNewMidTotal(String  date);



    //查询日活分时统计
    public Map getDauHours(String date );

     //查询当日交易总额
    public Double getOrderAmount(String date);

    //查询当日交易额分时明细

    public Map getOrderAmountHour(String date);




}
