/*******************************************************************************
 * Copyright (c) 2016 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.utilbox.process;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import melnorme.utilbox.concurrency.AbstractRunnableFuture2;
import melnorme.utilbox.concurrency.ICancelMonitor;
import melnorme.utilbox.core.fntypes.Result;
import melnorme.utilbox.misc.StreamUtil;

/* FIXME: integrate with MonitorRunnableFuture */
public abstract class ReaderTask<RET> 
	extends AbstractRunnableFuture2<Result<RET, IOException>> 
{
	
	protected final InputStream is;
	protected final ICancelMonitor cancelMonitor;
	
	public ReaderTask(InputStream is, ICancelMonitor cancelMonitor) {
		this.is = assertNotNull(is);
		this.cancelMonitor = assertNotNull(cancelMonitor);
	}
	
	@Override
	protected Result<RET, IOException> internalInvoke() {
		return Result.callToResult(this::doRun);
	}
	
	public RET doRun() throws IOException {
		// BM: Hum, should we treat an IOException not as an error, but just like an EOF?
		try {
			final int BUFFER_SIZE = 1024;
			byte[] buffer = new byte[BUFFER_SIZE];
			
			int read;
			while((read = is.read(buffer)) != StreamUtil.EOF && !cancelMonitor.isCanceled()) {
				notifyReadChunk2(buffer, 0, read);
			}
			return doGetReturnValue();
		} finally {
			is.close();
		}
	}
	
	protected abstract RET doGetReturnValue();
	
	@SuppressWarnings("unused")
	protected void notifyReadChunk2(byte[] buffer, int offset, int readCount) {
		// Default implementation: do nothing
	}
	
}