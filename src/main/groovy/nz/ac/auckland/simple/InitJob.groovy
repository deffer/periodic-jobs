package nz.ac.auckland.simple

/**
 * Init jobs are executed only once and on the same thread as queued periodic jobs.
 */
public interface InitJob {

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
	 * If returns true the job will not be scheduled and the onl way to stop it is to cancel it (using its Future)
	 * @return false if you don't want this job to be scheduled (usable during tests)
	 */
	Boolean isEnabled()
}