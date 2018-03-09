package com.czd.core.controller;

import com.czd.annotation.MyController;
import com.czd.annotation.MyRequestMapping;
import com.czd.annotation.MyRequestParam;

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
@MyRequestMapping("/*")
public class TestController {

    @MyRequestMapping("/doTest")
    public void Test1(HttpServletRequest request,HttpServletResponse response,@MyRequestParam("param") String param){
        System.out.println(param);
        try {
            response.getWriter().write("doTest method success!params:"+param);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @MyRequestMapping("/doTest2")
    public void Test2(HttpServletRequest request,HttpServletResponse response,@MyRequestParam("param") String param){
        System.out.println(param);
        try {
            response.getWriter().write("doTest2 method success!params:"+param);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
