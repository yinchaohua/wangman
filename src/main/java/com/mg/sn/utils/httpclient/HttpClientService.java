package com.mg.sn.utils.httpclient;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class HttpClientService {

    private static final Logger log = LoggerFactory.getLogger(HttpClientService.class);

    /**
     * GET请求
     * @param url  路径
     * @param nameValuePairList  参数
     * @param requestWay  请求类型
     * @return
     */
    public static HttpClientResult sendGet (String url, List<NameValuePair> nameValuePairList, String requestWay) {
        JSONObject jsonObject = null;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        HttpClientResult httpClientResult = new HttpClientResult();

        try {
            //创建HttpClient对象
            if ("https".equals(requestWay)) {
                client = new SSLClient();
            } else {
                client = HttpClients.createDefault();
            }
            //创建URIBuilder
            URIBuilder uriBuilder = new URIBuilder(url);
            //设置参数
            uriBuilder.addParameters(nameValuePairList);
            //创建HttpGet
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            //设置请求头编码
            httpGet.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
            //设置返回编码
            httpGet.setHeader(new BasicHeader("Accept", "text/plain;charset=utf-8"));
            //请求服务
            response = client.execute(httpGet);
            //获取响应码
            int statusCode = response.getStatusLine().getStatusCode();

            //获取返回对象
            HttpEntity entity = response.getEntity();
            //通过EntityUtils获取返回值内容
            String result = EntityUtils.toString(entity, "UTF-8");
            jsonObject = JSONObject.parseObject(result);
            httpClientResult.setStatusCode(statusCode);
            httpClientResult.setObject(jsonObject);
            return httpClientResult;
        } catch (Exception e) {
            log.error("url: {}, message: {}", url, e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                client.close();
                response.close();
            } catch (IOException e) {
                log.error("HttpClientService-line: {}, Exception: {}", 64, "client和response关闭失败");
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 发送POST请求
     * @param url
     * @param header
     * @param nameValuePairList
     * @return JSON或者字符串
     * @throws Exception
     */
    public static HttpClientResult sendPost(String url, HashMap<String, Object> header, List<NameValuePair> nameValuePairList, String requestWay){
        JSONObject jsonObject = null;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        HttpClientResult httpClientResult = new HttpClientResult();
        try{
            //创建HttpClient对象
            if ("https".equals(requestWay)) {
                client = new SSLClient();
            } else {
                client = HttpClients.createDefault();
            }
            //创建一个post对象
            HttpPost post = new HttpPost(url);
            //设置头信息
            setHeader(post, header);
            //包装成一个Entity对象
            if (nameValuePairList != null) {
                StringEntity entity = new UrlEncodedFormEntity(nameValuePairList, "UTF-8");
                //设置请求的内容
                post.setEntity(entity);
            }
            //设置请求的报文头部的编码
            post.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
            //设置请求的报文头部的编码
            post.setHeader(new BasicHeader("Accept", "application/json;charset=utf-8"));
            //执行post请求
            response = client.execute(post);
            //获取响应码
            int statusCode = response.getStatusLine().getStatusCode();

            //通过EntityUitls获取返回内容
            String result = EntityUtils.toString(response.getEntity(),"UTF-8");
            //转换成json,根据合法性返回json或者字符串
            jsonObject = JSONObject.parseObject(result);
            httpClientResult.setStatusCode(statusCode);
            httpClientResult.setObject(jsonObject);
            return httpClientResult;
        }catch (Exception e){
            log.error("url: {}, message：{}", url, e.getMessage());
            e.printStackTrace();
        }finally {
            try {
                client.close();
                response.close();
            } catch (IOException e) {
                log.error("HttpClientService-line: {}, Exception: {}", 64, "client和response关闭失败");
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 组织请求参数
     * @param map  参数
     * @return
     */
    public static List<NameValuePair> getParams(HashMap<String, Object> map){
        //验证参数是否为空
        if (map == null || map.size() == 0) {
            return null;
        }

        List<NameValuePair> nameValuePairList = new ArrayList<>();
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            nameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
        return nameValuePairList;
    }

    public static void setHeader (HttpPost post, HashMap<String, Object> map) {
        if (map == null) {
            return;
        }

        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            post.addHeader(entry.getKey(), entry.getValue().toString());
        }
    }

    public static void main(String[] args) throws Exception{
        String url = "https://api.qinglianyun.com/open/v2/device/listinfo";
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("uid", "10585");
        stringObjectHashMap.put("tt", "1665236353112");
        stringObjectHashMap.put("sign", "F6D79D92FA200312598F14B1F644D5D2");
        stringObjectHashMap.put("userId", "10833");
        stringObjectHashMap.put("userToken", "RDU5N0IzNUFFQ0JENDZCQkE2RUREQzAxQURGNUE5Q0M");
        /**
         * 获取参数对象
         */
        List<NameValuePair> paramsList = HttpClientService.getParams(stringObjectHashMap);
        /**
         * 发送get
         */
//        Object result = HttpClientService.sendGet(url, paramsList,"https");
        /**
         * 发送post
         */
        Object result2 = HttpClientService.sendPost(url,null,  paramsList, "https");

//        System.out.println("GET返回信息：" + result);
        System.out.println("POST返回信息：" + result2);
    }

}
