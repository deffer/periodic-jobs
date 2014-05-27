package nz.ac.auckland.jobs.periodic

import groovy.transform.CompileStatic

/**
 * Init jobs are executed only once and on the same thread as queued periodic jobs.
 */
@CompileStatic
public interface InitJob extends AbstractJob{

}