package io.ihankun.framework.spring.server.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class HttpConnectionUtil {

    private static final int HTTP_CONNECT_TIMEOUT=3000;
    private static final int HTTP_READ_TIMEOUT=3000;

    private static HttpURLConnection createConnection(String httpUrl, String httpMethod) throws IOException {
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置请求方式
        connection.setRequestMethod(httpMethod);
        //设置连接超时时间
        connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
        connection.setReadTimeout(HTTP_READ_TIMEOUT);
        return connection;
    }

    /**
     * Http get请求
     * @param httpUrl 连接
     * @return 响应数据
     */
    public static String doGet(String httpUrl){
        //链接
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer result = new StringBuffer();
        try {
            //创建连接
            connection = createConnection(httpUrl,"GET");
            //建立连接
            connection.connect();
            //获取响应数据
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //获取返回的数据
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
            }
        } catch (IOException e) {
            log.error("http get 请求异常,请检查链接地址是否正确，httpUrl:{}",httpUrl,e);
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("",e);
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭远程连接
            connection.disconnect();
        }
        return result.toString();
    }

    /**
     * POST请求
     * @param httpUrl
     * @param param
     * @return
     */
    public static String doPost(String httpUrl, String param) {
        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        StringBuffer result = new StringBuffer();
        try {
            //创建POST请求
            connection = createConnection(httpUrl,"POST");
            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            // 通过连接对象获取一个输出流
            os = connection.getOutputStream();
            os.write(param.getBytes());
            // 通过连接对象获取一个输入流，向远程读取
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
            }
        }catch (Exception e) {
            log.error("http post 请求异常,请检查链接地址是否正确，httpUrl:{}",httpUrl,e);
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("关闭资源BufferedReader异常",e);
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("关闭资源OutputStream异常",e);
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("关闭资源InputStream异常",e);
                }
            }
            // 断开与远程地址url的连接
            connection.disconnect();
        }
        return result.toString();
    }
}
