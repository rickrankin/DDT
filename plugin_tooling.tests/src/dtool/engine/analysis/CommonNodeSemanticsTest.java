/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.analysis;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import melnorme.lang.tooling.BundlePath;
import melnorme.lang.tooling.ast.ASTNodeFinder;
import melnorme.lang.tooling.ast.ASTVisitor;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.util.NodeElementUtil;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement.NotAValueErrorElement;
import melnorme.lang.tooling.engine.ErrorElement.NotFoundErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ConcreteElementResult;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.CommonException;
import java.util.function.Predicate;
import melnorme.utilbox.misc.Location;
import dtool.ast.references.Reference;
import dtool.engine.CommonSemanticsTest;
import dtool.engine.ResolvedModule;
import dtool.engine.StandardLibraryResolution;
import dtool.engine.tests.DefUnitResultsChecker;
import dtool.parser.SourceEquivalenceChecker;

public class CommonNodeSemanticsTest extends CommonSemanticsTest {
	
	public static final BundlePath DEFAULT_TestsBundle = bundlePath(SEMANTICS_TEST_BUNDLES, "defaultBundle");
	
	protected static final String DEFAULT_ModuleName = "_tests";
	public static final Location DEFAULT_TestsBundle_Source = loc(DEFAULT_TestsBundle, "source");
	public static final BundlePath TESTER_TestsBundle = bundlePath(SEMANTICS_TEST_BUNDLES, "tester");
	
	public static final Location DEFAULT_TestsModule = 
			DEFAULT_TestsBundle_Source.resolve_fromValid(DEFAULT_ModuleName + ".d");
	
	protected static ISemanticContext getDefaultTestsModuleContext() throws CommonException {
		return getDefaultTestsModule().getSemanticContext();
	}
	
	protected static ResolvedModule getUpdatedModule(Location filePath) throws CommonException {
		return defaultSemMgr.getUpdatedResolvedModule(filePath, DEFAULT_TestsCompilerInstall, testsDubPath());
	}
	
	protected static ResolvedModule getDefaultTestsModule() throws CommonException {
		return getUpdatedModule(DEFAULT_TestsModule);
	}
	
	protected static ResolvedModule getTesterModule_(String sourcePath) throws CommonException {
		Location filePath = loc(TESTER_TestsBundle, "source").resolve_fromValid(sourcePath);
		return getUpdatedModule(filePath);
	}
	
	protected static ResolvedModule getTesterModule(String sourcePath) {
		try {
			return getTesterModule_(sourcePath);
		} catch (CommonException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	/* -----------------  ----------------- */
	
	protected static ResolvedModule parseModule(String source) throws CommonException {
		return parseModule(source, DEFAULT_TestsModule.path);
	}
	
	protected static ResolvedModule parseModule(String source, Path filePath) throws CommonException {
		// make sure we reparse, even if source is the same. 
		defaultSemMgr.getParseCache().discardEntry(filePath);
		defaultSemMgr.getParseCache().setSourceAndParseModule(filePath, source);
		ResolvedModule updatedModule = getUpdatedModule(Location.create_fromValid(filePath));
		assertTrue(updatedModule.getSource().equals(source));
		return updatedModule;
	}
	
	protected static ResolvedModule parseModule_(String source) {
		try {
			return parseModule(source);
		} catch (CommonException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	protected static ResolvedModule parseModule_(String source, Path filePath) {
		try {
			return parseModule(source, filePath);
		} catch (CommonException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	protected static <T extends ILanguageElement> T findNode(ResolvedModule moduleRes, int offset, Class<T> klass) {
		ASTNode node = ASTNodeFinder.findElement(moduleRes.getModuleNode(), offset);
		return NodeElementUtil.getMatchingParent(node, klass);
	}
	
	/* -----------------  ----------------- */
	
	public static <E extends ILanguageElement> PickedElement<E> parseElement(String source, Class<E> klass) {
		return parseElement(source, "/*M*/", klass);
	}
	
	public static <E extends ILanguageElement> PickedElement<E> parseElement(String source, 
		String offsetSource, Class<E> klass) {
		ResolvedModule resolvedModule = parseModule_(source);
		return pickElement(resolvedModule, offsetSource, klass);
	}
	
	public static <E extends ILanguageElement> PickedElement<E> parseElement(String source, 
		int offset, Class<E> klass) {
		ResolvedModule resolvedModule = parseModule_(source);
		return pickElement(resolvedModule, offset, klass);
	}
	
	public static <E extends ILanguageElement> PickedElement<E> pickElement(ResolvedModule resolvedModule,
			String offsetSource, Class<E> klass) {
		String source = resolvedModule.getParsedModule().source;
		int indexOf = offsetSource == null ? source.length() : source.indexOf(offsetSource);
		assertTrue(indexOf >= 0);
		return pickElement(resolvedModule, indexOf, klass);
	}
	
	public static <E extends ILanguageElement> PickedElement<E> pickElement(ResolvedModule resolvedModule,
			int index, Class<E> klass) {
		E node = findNode(resolvedModule, index, klass);
		assertNotNull(node);
		ISemanticContext context = resolvedModule.getSemanticContext();
		return picked(node, context);
	}
	
	/* ----------------- ----------------- */
	
	public static <E extends ILanguageElement> PickedElement<E> picked(E node, ISemanticContext context) {
		return new PickedElement<>(node, context);
	}
	
	public static <E extends ILanguageElement> PickedElement<E> pickedNative(E node) {
		return new PickedElement<>(node, getDefaultStdLibContext());
	}
	
	protected static StandardLibraryResolution getDefaultStdLibContext() {
		return defaultSemMgr.getUpdatedStdLibResolution(DEFAULT_TestsCompilerInstall);
	}
	
	/* ----------------- more complex pickers ----------------- */
	
	public static PickedElement<INamedElement> parseSourceAndPickFromRefResolving(String source) {
		return parseSourceAndPickFromRefResolving(source, "/*M*/");
	}
	
	public static PickedElement<INamedElement> parseSourceAndPickFromRefResolving(String source, String refMarker) {
		ResolvedModule resolvedModule = parseModule_(source);
		Reference ref = findNode(resolvedModule, resolvedModule.getSource().indexOf(refMarker), Reference.class);
		
		ISemanticContext context = resolvedModule.getSemanticContext();
		INamedElement derivedElement = ref.resolveTargetElement(context);
		return new PickedElement<>(derivedElement, context);
	}
	
	/* ----------------- Helper to test caching ----------------- */
	
	protected static void checkIsSameResolution(ConcreteElementResult resA, ConcreteElementResult resOther) {
		assertTrue(resA == resOther);
	}
	
	/* ----------------- result checkers ----------------- */
	
	public static DefUnitResultsChecker resultsChecker(CommonScopeLookup lookup) {
		return resultsChecker(lookup, true, true, true);
	}
	
	public static DefUnitResultsChecker resultsChecker(CommonScopeLookup lookup, boolean ignoreDummy,
			boolean ignorePrimitives, boolean ignoreObjectModule) {
		DefUnitResultsChecker checker = new DefUnitResultsChecker(lookup.getMatchedElements());
		checker.removeIgnoredDefUnits(ignoreDummy, ignorePrimitives);
		if(ignoreObjectModule) {
			checker.removeStdLibObjectDefUnits();
		}
		return checker;
	}
	
	public static Predicate<INamedElement> namedElementChecker(final String expectedLabel) {
		return new Predicate<INamedElement>() {
			@Override
			public boolean test(INamedElement matchedElement) {
				assertTrue(DefUnitResultsChecker.matchesLabel(matchedElement, expectedLabel));
				return true;
			}
		};
	}
	
	public static void checkElementLabel(INamedElement namedElement, String expectedLabel) {
		assertTrue(DefUnitResultsChecker.matchesLabel(namedElement, expectedLabel));
	}
	
	public static Predicate<INamedElement> notfoundChecker(final String name) {
		return namedElementChecker(expectNotFound(name));
	}
	
	public static  String expectNotFound(String name) {
		return NotFoundErrorElement.NOT_FOUND__Name + ":" + name;
	}
	
	public static String expectNotAValue(String name) {
		return NotAValueErrorElement.ERROR_IS_NOT_A_VALUE + ":" + name;
	}
	
	/* -----------------  ----------------- */
	
	public static class NodeFinderByString extends ASTVisitor {
		
		@SuppressWarnings("unchecked")
		public static <T extends ASTNode> T find(ASTNode node, Class<T> klass, String toStringAsCode) {
			NodeFinderByString nodeFinder = new NodeFinderByString(klass, toStringAsCode);
			node.accept(nodeFinder);
			return (T) nodeFinder.result;
		}
		
		protected final Class<? extends ASTNode> klass;
		protected final String toStringAsCode;
		
		public ASTNode result;

		public NodeFinderByString(Class<? extends ASTNode> klass, String toStringAsCode) {
			this.klass = klass;
			this.toStringAsCode = toStringAsCode;
		}

		@Override
		public boolean preVisit(ASTNode node) {
			if(klass.isInstance(node)) {
				
				if(toStringAsCode == null || SourceEquivalenceChecker.check(node.toStringAsCode(), toStringAsCode)) {
					result = node;
				}
			}
			return result == null;
		}
		
	}

}