/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.model_elements;


import mmrnmhrm.core.parser.ModuleParsingHandler;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.AbstractSourceElementParser;

import dtool.parser.DeeParserResult.ParsedModule;

public class DeeSourceElementParser extends AbstractSourceElementParser {
	
	public DeeSourceElementParser() {
	}
	
	@Override
	protected String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	public void parseSourceModule(IModuleSource moduleSource) {
		ParsedModule parsedModule = ModuleParsingHandler.parseModule(moduleSource);
		if (parsedModule != null) {
			DeeSourceElementProvider provider = new DeeSourceElementProvider(getRequestor());
			provider.provide(parsedModule);
		}
	}
	
}