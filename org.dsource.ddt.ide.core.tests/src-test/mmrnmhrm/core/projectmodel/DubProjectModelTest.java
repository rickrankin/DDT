package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.MiscUtil.InvalidPathExceptionX;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.junit.Test;

import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubBundleDescription;
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubParserTest;

public class DubProjectModelTest extends DubCommonTest {
	
	/* ************************************ */
	
	public static final String DUB_TEST = "DubTest";
	public static Path DUB_WORKSPACE = DubParserTest.DUB_WORKSPACE;
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		long taskCount = getDubExecutorAgent().getSubmittedTaskCount();
		IScriptProject dubTestProject = createAndOpenDeeProject(DUB_TEST, true);
		
		assertTrue(getDubExecutorAgent().getSubmittedTaskCount() == taskCount); // check no change
		assertTrue(DubProjectModel.getDefault().getBundleInfo(DUB_TEST) == null);
		
		try {
			runBasicTest(dubTestProject);
		} finally {
			dubTestProject.getProject().delete(true, null); // cleanup
		}
	}
	
	public void runBasicTest(IScriptProject dubTestProject) throws Exception {
		
		writeDubJson_AndSync(dubTestProject, "{"+ jsEntry("name", "xptobundle")+ "}");
		checkBundle(dubTestProject, "xptobundle", srcFolders());
		
		writeDubJson_AndSync(dubTestProject, "{"+
				jsEntry("name", "xptobundle")+
				jsEntry("importPaths", jsArray("src", "src-test"))+
				"\"blah\":null}");
		checkBundle(dubTestProject, "xptobundle", srcFolders("src", "src-test"));
		
		
		writeDubJson_AndSync(dubTestProject, 
				readFileContents(DUB_WORKSPACE.resolve("XptoBundle/dub.json")));
		
		checkBundle(dubTestProject, "xptobundle", srcFolders("src", "src-test"), 
				dep(DUB_WORKSPACE.resolve("bar_lib"), "source"),
				dep(DUB_WORKSPACE.resolve("foo_lib"), "src"),
				dep(DUB_WORKSPACE.resolve("foo_lib"), "src2")
				);
		
		
		writeDubJson_AndSync(dubTestProject, "{"+ jsEntry("nameXX", "xptobundle")+ "}");
		checkErrorBundle(dubTestProject, "dub returned non-zero");
		
		writeDubJson_AndSync(dubTestProject, "{"+ jsEntry("name", "xptobundle")+ "}");
		checkBundle(dubTestProject, "xptobundle", srcFolders());
		
	}
	protected void checkErrorBundle(IScriptProject dubProject, String errorMsgStart) throws CoreException {
		DubBundleException error = getDubProjectInfo(dubProject.getElementName()).getError();
		assertTrue(error != null);
		assertTrue(error.getMessage().startsWith(errorMsgStart));
		
		IMarker[] markers = DubProjectModel.getDubErrorMarkers(dubProject.getProject());
		assertTrue(markers.length == 1);
		IMarker errorMarker = markers[0];
		assertTrue(errorMarker.getAttribute(IMarker.MESSAGE, "").startsWith(errorMsgStart));
		assertEquals(errorMarker.getAttribute(IMarker.SEVERITY), IMarker.SEVERITY_ERROR);
	}

	public static String[] srcFolders(String... elems) {
		return elems;
	}
	
	public static DubBundle dep(Path baseLocation, String... elems) throws InvalidPathExceptionX {
		Path[] paths = new Path[elems.length];
		for (int i = 0; i < elems.length; i++) {
			paths[i] = MiscUtil.createPath(elems[i]);
			
		}
		return new DubBundle(baseLocation, "", null, paths, null, null, null);
	}
	
	public final void checkBundle(IScriptProject dubProject, String dubName, String[] srcFolders,
			DubBundle... deps) throws CoreException {
		checkBundle(dubProject, null, dubName, srcFolders, deps);
	}
	
	public void checkBundle(IScriptProject dubProject, String expectedError, String dubName, 
			String[] srcFolders, DubBundle... deps) throws CoreException {
		IProject project = dubProject.getProject();
		
		DubBundleDescription dubBundle = getDubProjectInfo(dubProject.getElementName());
		assertNotNull(dubBundle);
		if(dubBundle.getError() != null) {
			assertFail("Not Null: " + dubBundle.getError());
		}
		
		Path location = Paths.get(project.getLocationURI());
		
		assertAreEqual(dubBundle.getMainBundle().name, dubName);
		assertAreEqual(dubBundle.getMainBundle().location, location);
		assertExceptionContains(dubBundle.getError(), expectedError);
		
		checkRawBuildpath(dubProject.getRawBuildpath(), srcFolders);
		
		checkResolvedBuildpath(dubProject.getResolvedBuildpath(false), srcFolders, deps);
		
		IMarker[] dubErrorMarkers = DubProjectModel.getDubErrorMarkers(project);
		assertTrue(dubErrorMarkers.length == 0);
	}
	
	public static void checkRawBuildpath(IBuildpathEntry[] rawBuildpath, String[] srcFolders) throws ModelException {
		HashSet<String> sourcePaths = hashSet(srcFolders);
		
		for (IBuildpathEntry bpEntry : rawBuildpath) {
			IPath entryPath = bpEntry.getPath();
			if(entryPath.segment(0).equals(ScriptRuntime.INTERPRETER_CONTAINER)) {
				continue;
			}
			
			if((bpEntry.getEntryKind() == IBuildpathEntry.BPE_CONTAINER)) {
				assertTrue(entryPath.toString() .equals(DubBuildpathContainerInitializer.ID));
				continue;
			}
			
			assertTrue(bpEntry.getEntryKind() == IBuildpathEntry.BPE_SOURCE);
			assertTrue(bpEntry.isExternal() == false);
			IPath folderPath = entryPath.removeFirstSegments(1); // Remove project segment
			assertTrue(sourcePaths.remove(folderPath.toString()));
			continue;
		}
		
		// Ensure we matched every entry
		assertTrue(sourcePaths.isEmpty());
	}
	
	public static void checkResolvedBuildpath(IBuildpathEntry[] buildpath, String[] srcFolders, 
			DubBundle[] deps) throws ModelException {
		HashSet<String> sourcePaths = hashSet(srcFolders);
		
		HashSet<Path> depSourcePaths = new HashSet<>();
		for (DubBundle bundleDep : deps) {
			for (Path path : bundleDep.getRawSourceFolders()) {
				Path srcFolderAbsolute = bundleDep.location.resolve(path);
				depSourcePaths.add(srcFolderAbsolute);
			}
		}
		
		for (IBuildpathEntry bpEntry : buildpath) {
			IPath entryPath = EnvironmentPathUtils.getLocalPath(bpEntry.getPath());
			
			if(bpEntry.getEntryKind() == IBuildpathEntry.BPE_LIBRARY && !DubContainer.isDubBuildpathEntry(bpEntry)) {
				String entryPathStr = entryPath.toString();
				assertTrue(
						entryPathStr.endsWith("druntime/import") || 
						entryPathStr.endsWith("phobos") ||
						entryPathStr.startsWith("#special#builtin"));
				continue;
			}
			
			
			if(bpEntry.getEntryKind() == IBuildpathEntry.BPE_LIBRARY) {
				assertTrue(bpEntry.isExternal());
				assertTrue(depSourcePaths.remove(entryPath.toFile().toPath()));
				continue;
			}
			
			assertTrue(bpEntry.getEntryKind() == IBuildpathEntry.BPE_SOURCE);
			assertTrue(bpEntry.isExternal() == false);
			IPath folderPath = entryPath.removeFirstSegments(1); // Remove project segment
			assertTrue(sourcePaths.remove(folderPath.toString()));
			continue;
		}
		
		// Ensure we matched every entry
		assertTrue(sourcePaths.isEmpty());
		assertTrue(depSourcePaths == null || depSourcePaths.isEmpty());
	}
	
}