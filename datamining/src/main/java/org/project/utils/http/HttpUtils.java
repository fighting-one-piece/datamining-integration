package org.project.utils.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpUtils {
	
	protected final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	/**  定义编码格式 UTF-8*/  
    public static final String ENCODE_UTF8 = "UTF-8";  
    /**  定义编码格式 GBK */  
    public static final String ENCODE_GBK = "GBK"; 
    /**  定义编码格式 GB2312 */  
    public static final String ENCODE_GB2312 = "GB2312"; 
    
    public static final String URL_PARAM_CONNECT_FLAG = "&";  
    
    public static final String EMPTY = "";  
	
}
