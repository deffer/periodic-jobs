package nz.ac.auckland.simple

/**
 * Jobs which are executed periodically.
 * Jobs of different type (for example two instances of PeriodicJob) can run simultaneously
 *   as opposite to QueuedPeriodicJobs.
 */
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

}