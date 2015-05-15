/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.launch;

import java.io.IOException;

import melnorme.lang.ide.core.utils.process.IStartProcessListener;
import melnorme.lang.ide.ui.tools.console.AbstractToolsConsoleHandler;
import melnorme.lang.ide.ui.tools.console.ToolsConsole;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.process.ExternalProcessNotifyingHelper;
import mmrnmhrm.core.engine.IDubProcessListener;
import mmrnmhrm.core.engine.DubProcessManager.IDubOperation;
import mmrnmhrm.ui.DeeUIMessages;

import org.eclipse.core.resources.IProject;

public class DubCommandsConsoleListener extends AbstractToolsConsoleHandler implements IDubProcessListener {
	
	public DubCommandsConsoleListener() {
	}
	
	@Override
	protected String getOperationConsoleName(IProject project) {
		return DeeUIMessages.DUB_CONSOLE_NAME + getProjectNameSuffix(project);
	}
	
	protected String getProjectNameSuffix(IProject project) {
		if(project == null) {
			return " (Global)";
		}
		return " ["+ project.getName() +"]";
	}
	
	@Override
	public void handleDubOperationStarted(IDubOperation dubOperation) {
		final ToolsConsole console = getOperationConsole(dubOperation.getProject(), true);
		try {
			console.infoOut.write("************  " + dubOperation.getOperationName() + "  ************\n");
		} catch (IOException e) {
			return;
		}
		
		dubOperation.addExternalProcessListener(new IStartProcessListener() {
			
			@Override
			public void handleProcessStartResult(ProcessBuilder pb, IProject project,
					ExternalProcessNotifyingHelper processHelper, CommonException ce) {
				
				new ProcessUIConsoleHandler().init(pb, project, "> ", false, processHelper, ce).handle(console);
			}
			
		});
	}
	
	@Override
	public void engineDaemonStart(ProcessBuilder pb, CommonException ce, ExternalProcessNotifyingHelper processHelper) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void engineClientToolStart(ProcessBuilder pb, CommonException ce,
			ExternalProcessNotifyingHelper processHelper) {
		// TODO Auto-generated method stub
	}
	
}