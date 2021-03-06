/*******************************************************************************
 * Copyright (c) 2015, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import melnorme.lang.ide.core.utils.CoreExecutors;
import melnorme.lang.ide.core.utils.operation.EclipseJobExecutor;
import melnorme.lang.tooling.common.ops.IOperationMonitor;
import melnorme.utilbox.concurrency.ExecutorTaskAgent;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.core.fntypes.OperationCallable;
import melnorme.utilbox.core.fntypes.OperationResult;
import melnorme.utilbox.misc.Location;

public abstract class DeeEngineOperation<RET> {
	
	protected final DeeLanguageEngine languageEngine;
	
	protected final Location location;
	protected final int offset;
	protected final int timeoutMillis;
	protected final String opName;
	
	public DeeEngineOperation(DeeLanguageEngine languageEngine, Location location, int offset, int timeoutMillis, 
			String opName) {
		this.languageEngine = languageEngine;
		
		this.location = assertNotNull(location);
		this.offset = offset;
		this.timeoutMillis = timeoutMillis;
		this.opName = assertNotNull(opName);
	}
	
	public RET runEngineOperation(final IOperationMonitor om) 
			throws CommonException, OperationCancellation {
		
		if(timeoutMillis <= 0 ) {
			// Run directly
			return doRunEngineOperation(om);
		}
		
		// Use a one-time executor
		ExecutorTaskAgent completionExecutor = CoreExecutors.newExecutorTaskAgent(opName + " - Task Executor");
		try {
			return runEngineOperationWithExecutor(om, completionExecutor);
		} finally {
			completionExecutor.shutdownNow();
		}
	}
	
	protected RET runEngineOperationWithExecutor(final IOperationMonitor om, ExecutorTaskAgent completionExecutor)
			throws CommonException, OperationCancellation {
		Future<RET> future = completionExecutor.submit(new Callable<RET>() {
			@Override
			public RET call() throws CommonException, OperationCancellation {
				OperationCallable<RET> opCallable = () -> doRunEngineOperation(om);
				Function<IOperationMonitor, OperationResult<RET>> op = (_om) -> {
					return OperationResult.callToOpResult(opCallable);
				};
				return new EclipseJobExecutor().startOpFunction("Engine analysis", true, op).awaitResult2().get();
			}
		});
		
		try {
			return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
		} catch(ExecutionException e) {
			if(e.getCause() instanceof OperationCancellation) {
				throw (OperationCancellation) e.getCause(); 
			}
			if(e.getCause() instanceof CommonException) {
				throw (CommonException) e.getCause(); 
			}
			
			throw new CommonException("Error performing " + opName + ".", e.getCause());
		} catch (TimeoutException e) {
			throw new CommonException("Timeout performing " + opName + ".", null);
		} catch (InterruptedException e) {
			throw new CommonException("Interrupted.", e);
		}
	}
	
	protected RET doRunEngineOperation(final IOperationMonitor om) 
			throws CommonException, OperationCancellation {
		// TODO: ensure working copy at location is updated.

		
		return doRunOperationWithWorkingCopy(om);
	}
	
	protected abstract RET doRunOperationWithWorkingCopy(IOperationMonitor om) 
			throws CommonException, OperationCancellation;
	
}