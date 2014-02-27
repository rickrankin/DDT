/*******************************************************************************
 * Copyright (c) 2013, 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.utilbox.concurrency;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

import dtool.tests.CommonTestUtils;

public class ExecutorAgent_Test extends CommonTestUtils {
	
	@Test
	public void testShutdownNow() throws Exception {
		ExecutorAgent agent = new ExecutorAgent("testShutdownNow");
		LatchRunnable firstTask = new LatchRunnable();
		LatchRunnable secondTask = new LatchRunnable();
		
		agent.submit(firstTask);
		Future<?> secondTaskFuture = agent.submit(secondTask);
		
		firstTask.awaitTaskEntry();
		assertTrue(secondTaskFuture.isCancelled() == false);
		
		List<Runnable> cancelledTasks = agent.shutdownNow();
		assertTrue(cancelledTasks.size() == 1);
		
		assertTrue(secondTaskFuture.isCancelled() == true);
		assertTrue(agent.isShutdown());
		Thread.sleep(1);
		assertTrue(agent.isTerminating() == true);
		assertTrue(agent.isTerminated() == false);
		firstTask.releaseAll();
		agent.awaitTermination();
		assertTrue(agent.isShutdown());
		assertTrue(agent.isTerminating() == false);
		assertTrue(agent.isTerminated());
		
		testShutdownNow_Interrupt();
	}
	
	// test that shutdownNow interrupts current task.
	public void testShutdownNow_Interrupt() throws InterruptedException {
		ExecutorAgent agent = new ExecutorAgent("testShutdownNow_Interrupt");
		LatchRunnable firstTask = new LatchRunnable(false);
		agent.submit(firstTask);
		
		agent.submit(new LatchRunnable());
		
		firstTask.awaitTaskEntry();
		
		List<Runnable> cancelledTasks = agent.shutdownNow();
		assertTrue(cancelledTasks.size() == 1);
		
		agent.awaitTermination();
	}
	
	
	@Test
	public void testExceptionHandling() throws Exception { testExceptionHandling$(); }
	public void testExceptionHandling$() throws Exception {
		final LinkedBlockingQueue<Throwable> unexpectedExceptions = new LinkedBlockingQueue<>();
		
		ExecutorAgent agent = new ExecutorAgent("testExceptionHandling") {
			@Override
			protected void handleUnexpectedException(Throwable throwable) {
				if(throwable != null) {
					unexpectedExceptions.add(throwable);
				}
			}
		};
		Future<?> future;
		
		Runnable npeRunnable = new Runnable() {
			@Override
			public void run() {
				throw new NullPointerException(); // a RuntimeException, representing an internal error
			}
		};
		Callable<String> normalTask = new Callable<String>() {
			@Override
			public String call() throws Exception {
				throw new IOException("Some expected exception");
			}
		};
		
		
		future = agent.submit(npeRunnable);
		
		try {
			future.cancel(true);
			future.get();
		} catch (CancellationException ce) {
			// ok
		}
		agent.awaitPendingTasks();
		assertTrue(unexpectedExceptions.size() == 0);
		
		
		checkExceptionHandling(unexpectedExceptions, agent, 
			agent.submit(npeRunnable), NullPointerException.class, false);
		
		checkExceptionHandling(unexpectedExceptions, agent, 
			agent.submit(normalTask), IOException.class, true);
	}
	
	protected void checkExceptionHandling(final LinkedBlockingQueue<Throwable> unexpectedExceptions, 
			ExecutorAgent agent, Future<?> future, Class<? extends Exception> expectedKlass, boolean isExpected) 
					throws InterruptedException {
		
		try {
			future.get();
		} catch (ExecutionException ce) {
			assertTrue(expectedKlass.isInstance(ce.getCause()));
			// ok
		}
		agent.awaitPendingTasks();
		
		if(expectedKlass == null || isExpected) {
			assertTrue(unexpectedExceptions.size() == 0);
			return;
		} else {
			assertTrue(unexpectedExceptions.size() == 1);
			Throwable removed = unexpectedExceptions.remove();
			assertTrue(expectedKlass.isInstance(removed));
		}
	}
	
}