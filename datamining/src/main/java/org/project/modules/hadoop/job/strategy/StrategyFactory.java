package org.project.modules.hadoop.job.strategy;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class StrategyFactory {

	public static final Logger LOG = Logger.getLogger(StrategyFactory.class);
	  
	  private static Map<Class<?>, JobStrategy> map = new HashMap<Class<?>, JobStrategy>();
	  
	  private static boolean isInterface(Class<?> c, Class<?> cl) {
	    for (Class<?> clazz : c.getInterfaces()) {
	      if (clazz == cl) {
	        return true;
	      }
	    }
	    return false;
	  }
	  
	  public static void define(Class<?> c, JobStrategy strategy) {
	    if (isInterface(c, JobStrategy.class)) {
	      map.put(c, strategy);
	    }
	  }
	  
	  public static JobStrategy get(Class<?> c) {
	    if ((!map.containsKey(c)) && (isInterface(c, JobStrategy.class))) {
	      try {
	        map.put(c, (JobStrategy)c.newInstance());
	      } catch (Exception e) {
	        LOG.error("put strategy into map failed", e);
	      }
	    }
	    return (JobStrategy)map.get(c);
	  }
}
