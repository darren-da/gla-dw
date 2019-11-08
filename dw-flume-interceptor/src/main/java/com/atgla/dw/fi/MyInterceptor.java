package com.atgla.dw.fi;

import com.google.gson.Gson;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyInterceptor implements Interceptor {
    public static final String SELECTOER_HEADER ="logType";
    private Logger logger = LoggerFactory.getLogger(MyInterceptor.class);
    @Override  //初始化
    public void initialize() {

    }


    @Override  // 根据日志中的type值 来给header上设选择器标签
    public Event intercept(Event event) {
        Gson gson = new Gson();
        String logString = new String(event.getBody());
        if(logString!=null&&logString.length()>0){
            HashMap<String,String> hashMap = gson.fromJson(logString, HashMap.class);
            String type = hashMap.get("type");
            //判断日志类型
            if(type.equals("startup")){
                Map<String, String> headers = event.getHeaders();
                headers.put(SELECTOER_HEADER ,"startup"); //设定头标签
            }else if (type.equals("event")){
                Map<String, String> headers = event.getHeaders();
                headers.put(SELECTOER_HEADER,"event");
            }
        }
        return  event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {

        List<Event> intercepted = new ArrayList<>(events.size());
        for (Event event : events) {
            Event interceptedEvent = intercept(event);
            if (interceptedEvent != null) {
                intercepted.add(interceptedEvent);
            }
        }
        return intercepted;
    }

    @Override
    public void close() {

    }


    /**
     * 通过该静态内部类来创建自定义对象供flume使用，实现Interceptor.Builder接口，并实现其抽象方法
     */
    public static class Builder implements Interceptor.Builder {
        /**
         * 该方法主要用来返回创建的自定义类拦截器对象
         * @return
         */
        @Override
        public Interceptor build() {
            return new MyInterceptor();
        }

        @Override
        public void configure(Context context) {
            //可以通过context得到 flume.conf中设置的参数 ，传递给Interceptor
        }
    }


    public static void main(String[] args) {
        Gson gson = new Gson();
        String logString="{\"area\":\"shanghai\",\"uid\":\"user_995\",\"os\":\"andriod\",\"appid\":\"app007\",\"mid\":\"88a147a9-ecc4-40be-885a-ae67bb9b7b2b\",\"type\":\"startup\",\"vs\":\"1.1.1\",\"ts\":1546780968866}";
        HashMap<String,String> hashMap = gson.fromJson(logString, HashMap.class);

        System.out.println(hashMap.get("type"));

    }

}


