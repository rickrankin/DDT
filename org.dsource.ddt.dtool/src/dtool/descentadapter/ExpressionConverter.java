/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import descent.internal.compiler.parser.ArrayLiteralExp;
import descent.internal.compiler.parser.CallExp;
import dtool.DToolBundle;
import dtool.ast.SourceRange;
import dtool.ast.expressions.ExpArrayLiteral;
import dtool.ast.expressions.ExpCall;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class ExpressionConverter  extends BaseDmdConverter {
	
	public static ExpCall createExpCall(CallExp elem, ASTConversionContext convContext) {
		SourceRange sourceRange = DefinitionConverter.sourceRange(elem);
		Expression callee = Expression.convert(elem.e1, convContext); 
		Resolvable[] args = Expression.convertMany(elem.arguments, convContext);
		return new ExpCall(callee, args, sourceRange);
	}
	
	public static ExpArrayLiteral createExpArrayLiteral(ArrayLiteralExp elem, ASTConversionContext convContext) {
		
		Resolvable[] args = Expression.convertMany(elem.elements, convContext);
		
		SourceRange sourceRange = DefinitionConverter.sourceRange(elem);
		if(sourceRange == null && DToolBundle.DMDPARSER_PROBLEMS__BUG41) {
			int last = args.length-1;
			if(last >= 0) {
				assertTrue(args[0].hasNoSourceRangeInfo() == false);
				assertTrue(args[last].hasNoSourceRangeInfo() == false);
				sourceRange = DefinitionConverter.sourceRangeStrict(args[0].getStartPos(), args[last].getEndPos());
			} else {
				// We're screwed, can't estimate a source range...
			}
		}
		
		return new ExpArrayLiteral(args, sourceRange);
	}

}
