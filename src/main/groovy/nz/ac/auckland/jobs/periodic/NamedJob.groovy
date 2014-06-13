package nz.ac.auckland.jobs.periodic

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Providing a name for a job allows the use of a property file to control job's configuration.
 * This name should uniquely identify job at the application runtime. It should not have any spaces in it.
 *
 * Job configuration will be looked up under jobs.JOBNAME.* property name. For example, for @NamedJob("bake")
 *   next properties will be expected (but optional):
 *   - jobs.bake.delay
 *   - jobs.bake.initialDelay
 *   - jobs.bake.enabled
 *   - jobs.bake.cron
 *
 * Human friendly name is available as displayName.
 *
 * If you don't want your job to be controlled from properties but you still want to define name,
 *   set propertyConfigurable to false.
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NamedJob {
	String value()
	String displayName() default ""
	boolean propertyConfigurable() default true
}
