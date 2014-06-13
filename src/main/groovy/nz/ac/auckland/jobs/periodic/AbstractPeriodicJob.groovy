package nz.ac.auckland.jobs.periodic

/**
 * This is base interface for other jobs.
 *
 * @deprecated periodic delay is set in @JobConfiguration now
 */
interface AbstractPeriodicJob extends AbstractJob{

	/**
	 * Time between executions (between end of previous job and start of next job) in seconds
	 * @return
	 */
	Long getPeriodicDelay()
}