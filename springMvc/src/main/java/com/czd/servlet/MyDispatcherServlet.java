package com.czd.servlet;

import com.czd.annotation.MyController;
import com.czd.annotation.MyRequestMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * 自定义DispatcherServlet
 *
 * @author: czd
 * @create: 2018/3/9 10:05
 */
public class MyDispatcherServlet extends HttpServlet {

    private Properties properties=new Properties();

    private List<String> classNames=new ArrayList<String>();

    private Map<String,Object> ioc=new HashMap<String, Object>();

    private Map<String,Method> handlerMapping=new HashMap<String, Method>();

    private Map<String,Object> controllerMap=new HashMap<String, Object>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            resp.getWriter().write("500!!Server Exception");
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1 加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //2 初始化所有相关联的类，扫描用户设定的包下面所有的类
        doScanner(properties.getProperty("scanPackage"));
        //3 拿到扫描的类，通过反射机制，实例化，并放在idc容器中（k-v beanName-bean）
        doInstance();
        //4 初始化HandlerMapping(将url和method对应上)
        initHandlerMapping();
    }

    /**
     * 处理请求
     * @param req
     * @param resp
     * @throws Exception
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if(handlerMapping.isEmpty()){
            return;
        }
        String url=req.getRequestURI();
        String contextPath=req.getContextPath();
        url=url.replace(contextPath,"").replaceAll("/+","/");
        if(!this.handlerMapping.containsKey(url)){
            resp.getWriter().write("404 NOT FOUND");
            return;
        }
        Method method=this.handlerMapping.get(url);
        //获取方法的参数列表
        Class<?>[] parameterTypes=method.getParameterTypes();
        //获取请求参数
        Map<String,String[]> parameterMap=req.getParameterMap();
        //保存参数的值
        Object[] paramValues=new Object[parameterTypes.length];
        for (int i = 0; i <parameterTypes.length ; i++) {
            //根据参数名称做处理
            String requestParam=parameterTypes[i].getSimpleName();
            if("HttpServletRequest".equals(requestParam)){
                paramValues[i]=req;
                continue;
            }
            if("HttpServletResponse".equals(requestParam)){
                paramValues[i]=resp;
                continue;
            }
            if("String".equals(requestParam)){
                for(Map.Entry<String,String[]> param:parameterMap.entrySet()){
                    String value=Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                    paramValues[i]=value;
                }

            }
        }
        //利用反射调用
        try {
            method.invoke(this.controllerMap.get(url), paramValues);//obj是method所对应的实例 在ioc容器中
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 加载配置文件
     * @param location
     */
    private  void doLoadConfig(String location){
        InputStream resourceAsStream=this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(null!=resourceAsStream){
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 扫描所有类文件
     * @param packageName
     */
    private void doScanner(String packageName) {
        URL url=this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir= new File(url.getFile());
        for(File file:dir.listFiles()){
            if(file.isDirectory()){
                //递归读取
                doScanner(packageName+"."+file.getName());
            }else{
                String  className=packageName+"."+file.getName().replace(".class","");
                classNames.add(className);

            }
        }
    }

    /**
     * 反射实例加载类
     */
    private void doInstance(){
        if(classNames.isEmpty()){
            return ;
        }
        for (String className:classNames){
            Class<?> clazz= null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //目前只有Mycontroller注释类需要实例化
            if(clazz.isAnnotationPresent(MyController.class)){
                try {
                    ioc.put(toLowerFirstWord(clazz.getSimpleName()), clazz.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }else{
                continue;
            }
        }
    }

    /**
     * url对应的方法和类
     */
    private void initHandlerMapping(){
        if(ioc.isEmpty()){
            return;
        }
        for(Map.Entry<String,Object> entry:ioc.entrySet()){
            Class<? extends Object> clazz=entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(MyController.class)){
                continue;
            }
            //拼接url时，controllert头的url与方法上的url拼接
            String baseUrl="";
            if(clazz.isAnnotationPresent(MyRequestMapping.class)){
                MyRequestMapping annotation=clazz.getAnnotation(MyRequestMapping.class);
                baseUrl=annotation.value();
            }
            Method[] methods=clazz.getMethods();
            for(Method method:methods){
                if(!method.isAnnotationPresent(MyRequestMapping.class)){
                    continue;
                }
                MyRequestMapping annotation=method.getAnnotation(MyRequestMapping.class);
                String url=annotation.value();
                url=(baseUrl+"/"+url).replaceAll("/+","/");
                handlerMapping.put(url,method);
                try {
                    controllerMap.put(url,clazz.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                System.out.println(url+","+method);
            }

        }
    }
    /**
     * 字符串首字母转化为小写
     * @param name
     * @return
     */
    private String toLowerFirstWord(String name){
        char[] charArray=name.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }
}
