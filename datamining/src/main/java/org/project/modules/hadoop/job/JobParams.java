package org.project.modules.hadoop.job;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.project.modules.hadoop.job.annotation.CmdLineParameter;
import org.project.utils.DateUtils;

public class JobParams {
	public final Logger LOG = Logger.getLogger(JobParams.class);
	  
	  @CmdLineParameter(header="-c")
	  private String clazz = null;
	  
	  @CmdLineParameter(header="-strategy")
	  private String strategyClass = null;
	  
	  @CmdLineParameter(header="-d")
	  private String date = DateUtils.getYesterday();
	  
	  @CmdLineParameter(header="-debug", nullValue=true)
	  private boolean debug = false;
	  
	  @CmdLineParameter(header="-args")
	  private String args = "";
	  
	  @CmdLineParameter(header="-input")
	  private String input = "";
	  
	  @CmdLineParameter(header="-output")
	  private String output = "";
	  
	  @CmdLineParameter(header="-force", nullValue=true)
	  private boolean force = false;
	  
	  private Map<String, String> userDefine = new HashMap<String, String>();
	  
	  private String regex = ",";
	  
	  public JobParams(String args) {
	    if (null == args) {
	      return;
	    }
	    String pureArgs = args;
	    while (pureArgs.contains("  ")) {
	      pureArgs = pureArgs.replace("  ", " ");
	    }
	    parse(pureArgs.split(" "));
	  }
	  
	  public JobParams(String[] args) {
	    parse(args);
	  }
	  
	  public void parse(String[] args) {
	    if ((null == args) || (args.length < 2)) {
	      return;
	    }
	    
	    Map<String, String> paramMap = new HashMap<String, String>();
	    List<String> value = new ArrayList<String>();
	    String key = null;
	    for (String arg : args) {
	      if (arg.startsWith("-")) {
	        if (null != key) {
	          StringBuilder sb = new StringBuilder();
	          for (String v : value) {
	            sb.append(v).append(" ");
	          }
	          paramMap.put(key, sb.toString().trim());
	          key = null;
	          value.clear();
	        }
	        key = arg;
	      } else if (null != key) {
	        value.add(arg);
	      }
	    }
	    if (null != key) {
	      StringBuilder sb = new StringBuilder();
	      for (String v : value) {
	        sb.append(v).append(" ");
	      }
	      paramMap.put(key, sb.toString().trim());
	      key = null;
	      value.clear();
	    }
	    for (Map.Entry<String, String> entry : paramMap.entrySet()) {
	      if (((String)entry.getKey()).startsWith("--")) {
	        this.userDefine.put(((String)entry.getKey()).replace("--", ""), entry.getValue());
	      }
	    }
	    Class<?> clas = getClass();
	    for (Field field : clas.getDeclaredFields()) {
	      CmdLineParameter clp = null;
	      if (null != (clp = (CmdLineParameter) field.getAnnotation(CmdLineParameter.class))) {
	        if ((clp.nullValue()) && (paramMap.containsKey(clp.header()))) {
	          try {
	            field.setBoolean(this, true);
	          } catch (Exception e) {
	            this.LOG.error(e);
	          }
	        } else {
	          try {
	            String str = (String)paramMap.get(clp.header());
	            if (!StringUtils.isBlank(str)) {
	              field.set(this, str);
	            }
	          } catch (Exception e) {
	            this.LOG.error(e);
	          }
	        }
	      }
	    }
	  }
	  
	  public String getClazz() {
	    return this.clazz;
	  }
	  
	  public void setClazz(String clazz) {
	    this.clazz = clazz;
	  }
	  
	  public String getStrategyClass() {
	    return this.strategyClass;
	  }
	  
	  public void setStrategyClass(String strategyClass) {
	    this.strategyClass = strategyClass;
	  }
	  
	  public String getDate() {
	    return this.date;
	  }
	  
	  public void setDate(String date) {
	    this.date = date;
	  }
	  
	  public boolean isDebug() {
	    return this.debug;
	  }
	  
	  public void setDebug(boolean debug) {
	    this.debug = debug;
	  }
	  
	  public boolean isForce() {
	    return this.force;
	  }
	  
	  public String getArgs() {
	    return this.args;
	  }
	  
	  public String[] getArgsArray() {
	    String[] res = null;
	    
	    if (!StringUtils.isBlank(this.args)) {
	      try {
	        res = this.args.split(this.regex);
	      } catch (Exception e) {
	        this.LOG.error(e);
	      }
	    }
	    
	    return null == res ? new String[0] : res;
	  }
	  
	  public void setArgs(String args) {
	    this.args = args;
	  }
	  
	  public String getInput() {
	    return this.input;
	  }
	  
	  public List<String> getInputList() {
	    return splitToList(this.input, this.regex);
	  }
	  
	  public void setInput(String input) {
	    this.input = input;
	  }
	  
	  public String getOutput() {
	    return this.output;
	  }
	  
	  public List<String> getOutputList() {
	    return splitToList(this.output, this.regex);
	  }
	  
	  public void setOutput(String output) {
	    this.output = output;
	  }
	  
	  public String getUserDefineParam(String key, String defValue) {
	    return this.userDefine.get(key) == null ? defValue : (String)this.userDefine.get(key);
	  }
	  
	  public String toString() {
	    StringBuilder userDefineSb = new StringBuilder();
	    for (Map.Entry<String, String> entry : this.userDefine.entrySet()) {
	      userDefineSb.append((String)entry.getKey()).append("=").append((String)entry.getValue()).append(" ");
	    }
	    
	    return new StringBuilder().append("class=").append(this.clazz).append(", date=").append(this.date).append(", args=").append(this.args).append(", input=").append(this.input).append(", output=").append(this.output).append(this.debug ? ", debug" : "").append(this.force ? ", force" : "").append(", userDefine={").append(userDefineSb.toString().trim()).append("}").toString();
	  }
	  
	  private List<String> splitToList(String str, String regex) {
	    if (StringUtils.isBlank(str)) {
	      return new ArrayList<String>();
	    }
	    String[] strs = null;
	    try {
	      strs = str.split(regex);
	    } catch (Exception e) {
	      this.LOG.error(e);
	    }
	    return null == strs ? new ArrayList<String>() : Arrays.asList(strs);
	  }
	  
	  public static void main(String[] args) {
	    System.out.println(Arrays.toString(args));
	    JobParams obj = new JobParams(args);
	    System.out.println(obj.toString());
	    System.out.println(new StringBuilder().append("get product: ").append(obj.getUserDefineParam("product", "default-product")).toString());
	  }
}
