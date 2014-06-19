package nz.ac.auckland.jobs.periodic

import java.text.SimpleDateFormat

/**
 * Log of on execution of a job (start and finish times, error if it had one)
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
class ScheduledJobEvent {
	static SimpleDateFormat df = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')

	Date start
	Date finish
	Throwable error
	String getLogMessage(){
		if (error){
			return "${df.format(start)} - job has resulted in ${error.class.simpleName}: ${error.getMessage()} at ${df.format(finish)}"
		}else if (finish == null){
			return "${df.format(start)} - job is still running..."
		}else{
			return "${df.format(start)} - ${df.format(finish)}"
		}
	}
}
