package org.project.modules.hadoop.job.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CmdLineParameter {
	
	String header();

	boolean nullValue() default false;
}
