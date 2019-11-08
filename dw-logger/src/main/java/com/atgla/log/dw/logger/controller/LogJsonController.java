package com.atgla.log.dw.logger.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import com.atgla.log.dw.logger.GmallConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;


@RestController
    public class LogJsonController {

////        @Autowired
////        private KafkaTemplate<String ,String> kafkaTemplate;
//        private static final Logger logger = LoggerFactory.getLogger(LogJsonController.class);
//
//        @PostMapping("/log")
//        public void shipLog(@RequestParam("log") String log){
//            JSONObject logJsonObj = JSON.parseObject(log);
//            logJsonObj.put("ts",System.currentTimeMillis());
//            String logNew = logJsonObj.toJSONString();
//            logger.info(logNew);
//
//        }
//
//        //用于离线数据的处理


//      实时数据处理
       private static final Logger logger = LoggerFactory.getLogger(LogJsonController.class);
       @Autowired
       private KafkaTemplate<String ,String> kafkaTemplate;
        @PostMapping("/log")
        public void getLog(@RequestParam("log") String log){
            JSONObject logJsonObj = JSON.parseObject(log);
            int randomInt = new Random().nextInt(3600 * 1000 * 5);
            logJsonObj.put("ts",System.currentTimeMillis()+randomInt);
            sendKafka(  logJsonObj);
            String logNew = logJsonObj.toJSONString();
            logger.info(logNew);

        }

        //springboot 与kafka进行整合，将接收到的数据转发到kafka中
        public void sendKafka(JSONObject logJsonObj){
            if("startup".equals(logJsonObj.getString("type"))){
                kafkaTemplate.send(GmallConstants.KAFKA_TOPIC_STARTUP,logJsonObj.toJSONString());
            }else {
                kafkaTemplate.send(GmallConstants.KAFKA_TOPIC_EVENT,logJsonObj.toJSONString());
            }
        }



}
