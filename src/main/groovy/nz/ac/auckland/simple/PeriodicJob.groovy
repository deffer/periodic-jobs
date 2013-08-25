package nz.ac.auckland.simple

import groovy.transform.CompileStatic

/**
 * Jobs which are executed periodically.
 * Jobs of different type (for example two instances of PeriodicJob) can run simultaneously
 *   as opposite to QueuedPeriodicJobs.
 */
@CompileStatic
public interface PeriodicJob {

	/**
	 * Method to run
	 * @return
	 */
	Runnable getRunnable()

	/**
	 * Initial delay before first execution in seconds
	 * @return
	 */
	Long getInitialDelay()

	/**
	 * Time between executions (between end of previous job and start of next job) in seconds
	 * @return
	 */
	Long getPeriodicDelay()

	/**
    * If returns true the job will not be scheduled and the onl way to stop it is to cancel it (using its Future)
    * @return false if you don't want this job to be scheduled (usable during tests)
    */
	Boolean isEnabled()
}