/*******************************************************************************
 * Copyright (c) 2011, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.analysis;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.AliasSemantics;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.EArcheType;

/**
 * This class is an alias to an actual module element.
 * Used for performance reasons, to be able to use the proxy in some situations (Content Assist for example)
 * without forcing the referred module to be parsed.
 */
public class ModuleProxy extends AbstractNamedElement {
	
	protected final ISemanticContext context;
	protected final String fullModuleName;
	
	public ModuleProxy(String fullModuleName, ISemanticContext moduleResolver, ILanguageElement parent) {
		this(fullModuleName, moduleResolver, false, parent);
	}
	
	public ModuleProxy(String fullModuleName, ISemanticContext moduleResolver, boolean useFullName, 
			ILanguageElement parent) {
		super(getEffectiveModuleName(fullModuleName, useFullName), parent);
		assertTrue(getName().trim().isEmpty() == false);
		this.fullModuleName = fullModuleName;
		this.context = moduleResolver;
	}
	
	protected static String getEffectiveModuleName(String fullModuleName, boolean usefullName) {
		return usefullName ? fullModuleName : StringUtil.substringAfterLastMatch(fullModuleName, ".");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	@Override
	public String getModuleFullName() {
		return fullModuleName;
	}
	
	@Override
	public String toString() {
		return "module["+getModuleFullName()+"]";
	}
	
	@Override
	public String getFullyQualifiedName() {
		return fullModuleName;
	}
	
	@Override
	public INamedElement getParentNamespace() {
		return null;
	}
	
	@Override
	public INamedElementNode resolveUnderlyingNode() {
		INamedElement module = resolveConcreteElement(); 
		if(module instanceof INamedElementNode) {
			return (INamedElementNode) module;
		}
		return null; 
	}
	
	@Override
	public ElementDoc resolveDDoc() {
		IConcreteNamedElement resolvedModule = resolveConcreteElement(); 
		if(resolvedModule != null) {
			return resolvedModule.resolveDDoc();
		}
		return null;
	}
	
	public IConcreteNamedElement resolveConcreteElement() {
		return resolveConcreteElement(context);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public ISemanticContext getContextForThisElement(ISemanticContext parentContext) {
		return context;
	}
	
	@Override
	protected final NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new AliasSemantics(this, pickedElement) {
			
			{
				assertTrue(context == ModuleProxy.this.context);
			}
			
			@Override
			protected IConcreteNamedElement resolveAliasTarget(ISemanticContext context) {
				return CommonScopeLookup.resolveModule(context, ModuleProxy.this, getModuleFullName());
			}
			
		};
	}
	
}