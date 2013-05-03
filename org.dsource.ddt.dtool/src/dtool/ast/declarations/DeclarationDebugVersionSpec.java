package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.AbstractConditionalDeclaration.VersionSymbol;

/**
 * Debug/Version specification declaration
 */
public class DeclarationDebugVersionSpec extends ASTNeoNode {
	
	public final boolean isDebug;
	public final VersionSymbol value;
	
	public DeclarationDebugVersionSpec(boolean isDebug, VersionSymbol value) {
		this.isDebug = isDebug;
		this.value = parentize(value);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_DEBUG_VERSION_SPEC;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, value);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isDebug ? "debug = " : "version = ");
		cp.appendNode(value);
		cp.append(";");
	}
	
}