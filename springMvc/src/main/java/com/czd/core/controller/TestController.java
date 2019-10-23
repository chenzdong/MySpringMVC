package com.czd.core.controller;

import com.czd.annotation.*;
import com.czd.core.service.TestService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 测试自定义springMvc框架
 *
 * @author: czd
 * @create: 2018/3/9 13:49
 */
@MyController
@MyRequestMapping("/test")
public class TestController {
    @MyAutowired
    TestService service;
    @MyRequestMapping("/doTest")
    public void Test1(HttpServletRequest request,HttpServletResponse response,@MyRequestParam("param") String param){
        System.out.println(param);
        System.out.println(service.getSomething());
        try {
            response.getWriter().write("doTest method success!params:"+param);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @MyRequestMapping("/doTest2")
    public void Test2(HttpServletRequest request,HttpServletResponse response){
        System.out.println(service.getSomething());
        try {
            response.getWriter().write("doTest2 method success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
