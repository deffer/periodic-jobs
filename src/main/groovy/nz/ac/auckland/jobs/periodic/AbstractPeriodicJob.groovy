package nz.ac.auckland.jobs.periodic

/**
 * This is base interface for other jobs.
 */
interface AbstractPeriodicJob extends AbstractJob{

	/**
	 * Time between executions (between end of previous job and start of next job) in seconds
	 * @return
	 */
	Long getPeriodicDelay()
}