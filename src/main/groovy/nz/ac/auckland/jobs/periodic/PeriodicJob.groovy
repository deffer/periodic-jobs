package nz.ac.auckland.jobs.periodic

import groovy.transform.CompileStatic

/**
 * Jobs which are executed periodically.
 * Jobs of different type (for example two instances of PeriodicJob) can run simultaneously
 *   as opposite to QueuedPeriodicJobs.
 *
 * @deprecated use Job with @JobConfiguration(delay=33)
 */
@CompileStatic
public interface PeriodicJob extends AbstractPeriodicJob{

}