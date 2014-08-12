package org.project.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.log4j.Logger;

public class ReflectUtils {
	
	private static Logger logger = Logger.getLogger(ReflectUtils.class);

	@SuppressWarnings("unchecked")
	public static <T> Class<T> obtainParameterizedType(Class<?> clazz) {
        Type parameterizedType = clazz.getGenericSuperclass();
        if (!(parameterizedType instanceof ParameterizedType)) {
            parameterizedType = clazz.getSuperclass().getGenericSuperclass();
        }
        if (!(parameterizedType instanceof ParameterizedType)) {
            return null;
        }
        Type[] actualTypeArguments = ((ParameterizedType) parameterizedType).getActualTypeArguments();
        if (actualTypeArguments == null || actualTypeArguments.length == 0) {
            return null;
        }
        return (Class<T>) actualTypeArguments[0];
    }
	
	public static Field obtainFieldByFieldName(Object object, String fieldName) {
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
        	for (Field field : superClass.getDeclaredFields()) {
    			if (fieldName.equals(field.getName())) {
						return field;
    			}
    		}
        }
        return null;
    }

    public static Object obtainValueByFieldName(Object object, String fieldName) {
        Field field = obtainFieldByFieldName(object, fieldName);
        Object value = null;
        try {
		    if(null != field){
		        if (field.isAccessible()) {
		            value = field.get(object);
		        } else {
		            field.setAccessible(true);
		            value = field.get(object);
		            field.setAccessible(false);
		        }
		    }
        } catch (Exception e) {
        	logger.info(e.getMessage(), e);
        }
        return value;
    }

    public static void setValueByFieldName(Object object, String fieldName, Object value) {
    	Field field = obtainFieldByFieldName(object, fieldName);
    	if (null == field) return;
    	try {
	        if (field.isAccessible()) {
	            field.set(object, value);
	        } else {
	            field.setAccessible(true);
	            field.set(object, value);
	            field.setAccessible(false);
	        }
    	} catch (Exception e) {
        	logger.info(e.getMessage(), e);
        }
    }
    
    public static boolean isExistField(Object object, String fieldName) {
    	try {
			for (Field field : object.getClass().getFields()) {
				if (fieldName.equals(field.getName())) {
					return true;
				}
			}
		} catch (Exception e) {
        	logger.info(e.getMessage(), e);
        }
    	return false;
    }
    
    public static boolean isExistMethod(Object object, String methodName) {
    	try {
			for (Method method : object.getClass().getMethods()) {
				if (methodName.equals(method.getName())) {
					return true;
				}
			}
		} catch (Exception e) {
        	logger.info(e.getMessage(), e);
        }
    	return false;
    }
}
