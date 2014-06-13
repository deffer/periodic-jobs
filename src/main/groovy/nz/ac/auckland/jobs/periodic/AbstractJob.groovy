package nz.ac.auckland.jobs.periodic

/**
 * This is base interface for other jobs.
 *
 * @deprecated switch to using Job with @JobConfiguration annotation
 */
interface AbstractJob{
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