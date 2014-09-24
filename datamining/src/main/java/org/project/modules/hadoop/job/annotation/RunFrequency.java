package org.project.modules.hadoop.job.annotation;

import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Target({ java.lang.annotation.ElementType.TYPE })
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Inherited
public @interface RunFrequency {
	
	FrequencyType value() default FrequencyType.daily;

	public static enum FrequencyType {
		daily, monthly, quarterly;

		private FrequencyType() {
		}
	}
}
