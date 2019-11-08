package com.atgla.log.dw.logger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Demo1Controller {
    @ResponseBody
    @RequestMapping
    public  String testDemo(){
        return "hello";
    }
}
