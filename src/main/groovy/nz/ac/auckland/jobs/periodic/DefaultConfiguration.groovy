package nz.ac.auckland.jobs.periodic

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Allows to configure delays and 'availability' of the job. In the presence of NamedJob annotation
 *  this configuration can be overridden in the property file.
 *
 * Hardcoding values 5 and 300, not making them configurable because its not very useful
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultConfiguration {


	boolean enabled() default true


	long initialDelay() default 5l

	/**
	 * A simplest way to define a periodic job (without using cron rules). Sets a delay between job executions
	 *   (between end of previous job and start of next job) in seconds.
	 * Set to negative value if you only want your job to run once.
	 *   Initial delay will still be respected.
	 *
	 * @return delay between job executions (between end of previous job and start of next job) in seconds.
	 */
	long delay() default 300l

	/**
	 * For cron type jobs. If set, delay is ignored
	 */
	String cron() default ""
}
