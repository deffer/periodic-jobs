package nz.ac.auckland.jobs.periodic

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public interface Job {

	/**
    * Method to run
    * @return
    */
	Runnable getRunnable()

}