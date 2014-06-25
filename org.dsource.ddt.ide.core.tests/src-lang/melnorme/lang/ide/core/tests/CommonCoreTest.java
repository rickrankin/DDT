/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.core.tests;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import melnorme.lang.ide.core.LangNature;
import melnorme.lang.ide.core.tests.utils.ErrorLogListener;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StreamUtil;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.tests.CommonTest;
import melnorme.utilbox.tests.TestsWorkingDir;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.service.datalocation.Location;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/** 
 * Base core test class that adds an exception listener to the platform log. 
 * The ErrorLogListener was the only way I found to detect UI exceptions in SafeRunnable's 
 * when running as plugin test. 
 */
public abstract class CommonCoreTest extends CommonTest {
	
	static {
		initializeWorkingDirToEclipseInstanceLocation();
	}
	
	protected static ErrorLogListener logErrorListener;
	
	@BeforeClass
	public static void setUpExceptionListenerStatic() throws Exception {
		logErrorListener = ErrorLogListener.createAndInstall();
	}
	
	@AfterClass
	public static void checkLogErrorListenerStatic() throws Throwable {
		logErrorListener.checkErrorsAndUninstall();
	}
	
	public static void checkLogErrors_() throws Throwable {
		logErrorListener.checkErrors();
	}
	
	@After
	@Before
	public void checkLogErrors() throws Throwable {
		checkLogErrors_();
	}
	
	private static void initializeWorkingDirToEclipseInstanceLocation() {
		Location instanceLocation = Platform.getInstanceLocation();
		try {
			URI uri = instanceLocation.getURL().toURI();
			String workingDirPath = new File(uri).getAbsolutePath();
			TestsWorkingDir.initWorkingDir(workingDirPath);
		} catch (URISyntaxException e) {
			throw assertFail();
		}
	}
	
	/* ----------------- utilities ----------------- */
	
	public static org.eclipse.core.runtime.Path epath(Path path) {
		return new org.eclipse.core.runtime.Path(path.toString());
	}
	
	public static IProject createAndOpenProject(String name, boolean overwrite) throws CoreException {
		return ResourceUtils.createAndOpenProject(name, overwrite);
	}
	
	public static void deleteProject(String projectName) {
		ResourceUtils.deleteProject_unchecked(projectName);
	}
	
	public static IFolder createFolder(IFolder folder) throws CoreException {
		folder.create(true, true, null);
		return folder;
	}
	
	public static void deleteResource(IResource resource) throws CoreException {
		resource.delete(false, new NullProgressMonitor());
	}
	
	public static void setupLangProject(IProject project) throws CoreException {
		setupLangProject(project, true);
	}
	
	public static void setupLangProject(IProject project, boolean requireWsLock) throws CoreException {
		assertTrue(project.exists());
		if(requireWsLock) {
			ISchedulingRule currentRule = Job.getJobManager().currentRule();
			assertTrue(currentRule != null && currentRule.contains(project));
		}
		EclipseUtils.addNature(project, LangNature.NATURE_ID);
	}
	
	public static String readFileContents(Path path) throws IOException {
		assertTrue(path.isAbsolute());
		return FileUtil.readStringFromFile(path.toFile(), StringUtil.UTF8);
	}
	
	public static String readFileContents(IFile file) throws CoreException, IOException {
		return StreamUtil.readStringFromReader(new InputStreamReader(file.getContents(), StringUtil.UTF8));
	}
	
	public static void writeStringToFile(IProject project, String filePath, String contents) 
			throws CoreException {
		IFile file = project.getFile(filePath);
		writeStringToFile(file, contents);
	}
	
	public static void writeStringToFile(IFile file, String contents) throws CoreException {
		ResourceUtils.writeToFile(file, new ByteArrayInputStream(contents.getBytes(StringUtil.UTF8)));
	}
	
}