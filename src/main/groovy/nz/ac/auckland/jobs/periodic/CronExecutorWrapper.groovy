/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nz.ac.auckland.jobs.periodic

import org.springframework.scheduling.Trigger
import org.springframework.scheduling.support.SimpleTriggerContext


import java.util.concurrent.Delayed
import java.util.concurrent.ExecutionException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Slightly adapted copy of org.springframework.scheduling.ReschedulingRunnable. Cant use original
 *   since it has package visibility.
 *
 * <p>Necessary because a native {@link ScheduledExecutorService} supports
 * delay-driven execution only. The flexibility of the {@link Trigger} interface
 * will be translated onto a delay for the next execution time (repeatedly).
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 */
class CronExecutorWrapper implements ScheduledFuture<Object>, Runnable {
	private final Runnable task
	private final Trigger trigger;

	private final SimpleTriggerContext triggerContext = new SimpleTriggerContext();

	private final ScheduledExecutorService executor;

	private ScheduledFuture currentFuture;
	private Date scheduledExecutionTime;

	private final Object triggerContextMonitor = new Object();


	public CronExecutorWrapper(Runnable task, Trigger trigger, ScheduledExecutorService executor) {
		this.task = task
		this.trigger = trigger;
		this.executor = executor;
	}


	public ScheduledFuture schedule() {
		synchronized (this.triggerContextMonitor) {
			this.scheduledExecutionTime = this.trigger.nextExecutionTime(this.triggerContext);
			if (this.scheduledExecutionTime == null) {
				return null;
			}
			long initialDelay = this.scheduledExecutionTime.getTime() - System.currentTimeMillis();
			this.currentFuture = this.executor.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
			return this;
		}
	}

	public void run() {
		Throwable error
		Date actualExecutionTime = new Date();
		try {
			this.task.run();
		} catch (Throwable ex) {
			error = ex
		}
		Date completionTime = new Date();

		synchronized (this.triggerContextMonitor) {
			this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);
			if (!this.currentFuture.isCancelled()) {
				schedule();
			}
		}
		if (error)
			throw error
	}


	public boolean cancel(boolean mayInterruptIfRunning) {
		synchronized (this.triggerContextMonitor) {
			return this.currentFuture.cancel(mayInterruptIfRunning);
		}
	}

	public boolean isCancelled() {
		synchronized (this.triggerContextMonitor) {
			return this.currentFuture.isCancelled();
		}
	}

	public boolean isDone() {
		synchronized (this.triggerContextMonitor) {
			return this.currentFuture.isDone();
		}
	}

	public Object get() throws InterruptedException, ExecutionException {
		ScheduledFuture curr;
		synchronized (this.triggerContextMonitor) {
			curr = this.currentFuture;
		}
		return curr.get();
	}

	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		ScheduledFuture curr;
		synchronized (this.triggerContextMonitor) {
			curr = this.currentFuture;
		}
		return curr.get(timeout, unit);
	}

	public long getDelay(TimeUnit unit) {
		ScheduledFuture curr;
		synchronized (this.triggerContextMonitor) {
			curr = this.currentFuture;
		}
		return curr.getDelay(unit);
	}

	public int compareTo(Delayed other) {
		if (this == other) {
			return 0;
		}
		long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
		return (diff == 0 ? 0 : ((diff < 0)? -1 : 1));
	}

}
