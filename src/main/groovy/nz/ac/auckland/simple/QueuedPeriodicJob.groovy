package nz.ac.auckland.simple

import groovy.transform.CompileStatic
/**
 * These jobs are put into single thread queue, to make sure they are never executed simultaneously.
 * For example periodic database update and periodic reindexing should never run together
 */
@CompileStatic
interface QueuedPeriodicJob {

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
