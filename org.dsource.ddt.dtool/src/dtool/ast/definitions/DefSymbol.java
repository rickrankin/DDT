package dtool.ast.definitions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;


/**
 * A Symbol that is the name of a DefUnit, and that knows how to get
 * that DefUnit. Its node parent must be the corresponding DefUnit.
 */
public class DefSymbol extends Symbol {
	
	public DefSymbol(String id, DefUnit parent) {
		this(id);
		setParent(parent);
	}
	
	protected DefSymbol(String id) {
		super(id);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.SYMBOL;
	}
	
	public DefUnit getDefUnit() {
		return (DefUnit) super.getParent();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(name);
	}
}