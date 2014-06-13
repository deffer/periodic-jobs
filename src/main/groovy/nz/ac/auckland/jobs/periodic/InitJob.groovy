package nz.ac.auckland.jobs.periodic

import groovy.transform.CompileStatic

/**
 * Init jobs are executed only once and on the same thread as queued periodic jobs.
 *
 * @deprecated use @JobConfiguration with negative delay. Ex. @JobConfiguration(delay=-1)
 */
@CompileStatic
public interface InitJob extends AbstractJob{

}