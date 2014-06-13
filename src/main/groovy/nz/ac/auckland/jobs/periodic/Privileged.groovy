package nz.ac.auckland.jobs.periodic

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Presence of this annotation indicates that job is privileged (privileged jobs never run simultaneously).
 *
 * For example periodic database update and periodic reindexing should never run together. Both this jobs
 *   should be marked as privileged.
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Privileged {
}
