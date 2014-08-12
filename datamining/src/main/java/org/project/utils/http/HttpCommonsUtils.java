package org.project.utils.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpCommonsUtils extends HttpUtils {

	private final static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    
    private static MultiThreadedHttpConnectionManager connectionManager = null;  
  
    private static int connectionTimeOut = 25000;  
  
    private static int socketTimeOut = 25000;  
  
    private static int maxConnectionPerHost = 20;  
  
    private static int maxTotalConnections = 20;  
  
    private static HttpClient client = null;  
  
    static{  
        connectionManager = new MultiThreadedHttpConnectionManager();  
        connectionManager.getParams().setConnectionTimeout(connectionTimeOut);  
        connectionManager.getParams().setSoTimeout(socketTimeOut);  
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionPerHost);  
        connectionManager.getParams().setMaxTotalConnections(maxTotalConnections);  
        client = new HttpClient(connectionManager);  
    }  
    
    /** 
     * POST方式提交数据 
     * @param url  待请求的URL 
     * @param params 要提交的数据 
     * @param enc 编码 
     * @return  响应结果 
     * @throws IOException IO异常 
     */  
    public static String post(String url, Map<String, String> params, String enc) { 
    	String response = EMPTY;          
        PostMethod postMethod = null;  
        try {  
            postMethod = new PostMethod(url);  
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + enc);  
            //将表单的值放入postMethod中  
            Set<String> keySet = params.keySet();  
            for(String key : keySet){  
                String value = params.get(key);  
                postMethod.addParameter(key, value);  
            }             
            //执行postMethod  
            int statusCode = client.executeMethod(postMethod);  
            if(statusCode == HttpStatus.SC_OK) {  
                response = postMethod.getResponseBodyAsString();  
            }else{  
                logger.error("响应状态码 = " + postMethod.getStatusCode());  
            }  
        }catch(HttpException e){  
            logger.error("发生致命的异常，可能是协议不对或者返回的内容有问题", e);  
            e.printStackTrace();  
        }catch(IOException e){  
            logger.error("发生网络异常", e);  
            e.printStackTrace();  
        }finally{  
            if(postMethod != null){  
                postMethod.releaseConnection();  
                postMethod = null;  
            }  
        }  
        return response; 
    }
    
    /** 
     * GET方式提交数据 
     * @param url 待请求的URL 
     * @param enc 编码 
     * @return 响应结果 
     * @throws IOException IO异常 
     */  
    public static String get(String url, String enc){  
    	return get(url, null, enc);
    }
      
    /** 
     * GET方式提交数据 
     * @param url 待请求的URL 
     * @param params 要提交的数据 
     * @param enc 编码 
     * @return 响应结果 
     * @throws IOException IO异常 
     */  
    public static String get(String url, Map<String, String> params, String enc){  
        String response = EMPTY;  
        GetMethod getMethod = null;       
        StringBuffer strtTotalURL = new StringBuffer(EMPTY);  
        if(strtTotalURL.indexOf("?") == -1) {  
          strtTotalURL.append(url).append("?").append(map2url(params, enc));  
        } else {  
            strtTotalURL.append(url).append("&").append(map2url(params, enc));  
        }  
        logger.debug("GET请求URL = \n" + strtTotalURL.toString());  
        try {  
            getMethod = new GetMethod(strtTotalURL.toString());  
            getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + enc);  
            //执行getMethod  
            int statusCode = client.executeMethod(getMethod);  
            if(statusCode == HttpStatus.SC_OK) {  
                response = getMethod.getResponseBodyAsString();  
            }else{  
                logger.debug("响应状态码 = " + getMethod.getStatusCode());  
            }  
        }catch(HttpException e){  
            logger.error("发生致命的异常，可能是协议不对或者返回的内容有问题", e);  
            e.printStackTrace();  
        }catch(IOException e){  
            logger.error("发生网络异常", e);  
            e.printStackTrace();  
        }finally{  
            if(getMethod != null){  
                getMethod.releaseConnection();  
                getMethod = null;  
            }  
        }  
        return response;  
    }     
  
    /** 
     * 据Map生成URL字符串 
     * @param map Map 
     * @param valueEnc URL编码 
     * @return URL 
     */  
    private static String map2url(Map<String, String> map, String valueEnc) {  
        if (null == map || map.keySet().size() == 0) {  
            return (EMPTY);  
        }  
        StringBuffer url = new StringBuffer();  
        Set<String> keys = map.keySet();  
        for (Iterator<String> it = keys.iterator(); it.hasNext();) {  
            String key = it.next();  
            if (map.containsKey(key)) {  
                String val = map.get(key);  
                String str = val != null ? val : EMPTY;  
                try {  
                    str = URLEncoder.encode(str, valueEnc);  
                } catch (UnsupportedEncodingException e) {  
                    e.printStackTrace();  
                }  
                url.append(key).append("=").append(str).append(URL_PARAM_CONNECT_FLAG);  
            }  
        }  
        String strURL = EMPTY;  
        strURL = url.toString();  
        if (URL_PARAM_CONNECT_FLAG.equals(EMPTY + strURL.charAt(strURL.length() - 1))) {  
            strURL = strURL.substring(0, strURL.length() - 1);  
        }  
        return (strURL);  
    }  
}
