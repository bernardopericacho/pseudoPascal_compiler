package Parser;

/*
 * 0 -> BOOL 1 -> CHAR 2 -> NAT 3 -> INT 4 -> FLOAT 5 -> ARRAY 6 -> RECORD 7 -> POINTER 8 -> ERR
 */

public class TypeTables {

	private static boolean[][] errAsig = {
			{ false, true , true , true , true , true , true , true , true, true },
			{ true , false, true , true , true , true , true , true , true, true },
			{ true , true , false, true , true , true , true , true , true, true },
			{ true , true , false, false, true , true , true , true , true, true },
			{ true , true , false, false, false, true , true , true , true, true },
			{ true , true , true , true , true , false, true , true , true, true },
			{ true , true , true , true , true , true , false, true , true, true },
			{ true , true , true , true , true , true , true , false, true, true },
			{ true , true , true , true , true , true , true , true , true, true },
			{ true , true , true , true , true , true , true , true , true, true }
	};
	
	private static Types[][] tOp0IgualDistinto = {
		{ Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.BOOLEAN, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.BOOLEAN, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.BOOLEAN, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR }
	};
	
	private static Types[][] tOp0 = {
		{ Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.BOOLEAN, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.BOOLEAN, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR }
	};
	
	private static Types[][] tOp1 = {
		{ Types.NATURAL, Types.INTEGER, Types.FLOAT, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.INTEGER, Types.INTEGER, Types.FLOAT, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.FLOAT, Types.FLOAT, Types.FLOAT, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR },
		{ Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR }
	};
	
	//private static Types[][] tOr = { ...
	//private static Types[][] tOp2MultDiv = { ... == tOp1
	//private static Types[][] tOp2Mod = { ...
	//private static Types[][] tAnd = { ... == tOr
	//private static Types[][] tOp3 = { ...

	private static Types[] tOp4ANot = { Types.ERR, Types.ERR, Types.ERR, Types.ERR, 
		Types.BOOLEAN, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR };
	private static Types[] tOp4AMenos = { Types.INTEGER, Types.INTEGER, Types.FLOAT, Types.ERR, 
		Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR };
	
	private static Types[] tOp4NACFloat = { Types.FLOAT, Types.FLOAT, Types.FLOAT, Types.FLOAT, 
		Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR };
	private static Types[] tOp4NACInt = { Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, 
		Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR };
	private static Types[] tOp4NACNat = { Types.NATURAL, Types.ERR, Types.ERR, Types.NATURAL,
		Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR };	
	private static Types[] tOp4NACChar = { Types.CHARACTER, Types.ERR, Types.ERR, 
		Types.CHARACTER, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR, Types.ERR };
	private static Types[] tAbs = { Types.NATURAL, Types.NATURAL, Types.FLOAT, Types.ERR,
		Types.ERR, Types.ERR, Types.ERR, Types.FLOAT, Types.ERR, Types.ERR };

	public TypeTables() {
	}

	public static boolean errAsig(Types toper1, Types toper2) {
		return errAsig[toper1.ordinal()][toper2.ordinal()];
	}
	
	public static Types tipoOp0(Oper op, Types tipoh, Types type) {
		if(op == Oper.IGUAL || op == Oper.DISTINTO)
			return tOp0IgualDistinto[tipoh.ordinal()][type.ordinal()];
		else
			return tOp0[tipoh.ordinal()][type.ordinal()];
	}
	
	public static Types tipoOp1(Types t1, Types t2) {
		return tOp1[t1.ordinal()][t2.ordinal()];
	}
	
	public static Types tipoOr(Types t1, Types t2) {
		if(t1 == Types.BOOLEAN && t2 == Types.BOOLEAN)
			return Types.BOOLEAN;
		else
			return Types.ERR;
	}
	
	public static Types tipoOp2(Oper op, Types t1, Types t2) {
		if(op == Oper.MULT || op == Oper.DIV)
			return tOp1[t1.ordinal()][t2.ordinal()];
		else if(op == Oper.MOD)
			if(t1 == Types.NATURAL && t2 == Types.NATURAL) return Types.NATURAL;
			else if(t1 == Types.INTEGER && t2 == Types.NATURAL) return Types.INTEGER;
			else return Types.ERR;
		else return null;
	}
	
	public static Types tipoAnd(Types t1, Types t2) {
		if(t1 == Types.BOOLEAN && t2 == Types.BOOLEAN)
			return Types.BOOLEAN;
		else
			return Types.ERR;
	}
	
	public static Types tipoOp3(Types t1, Types t2) {
		if(t1 == Types.NATURAL && t2 == Types.NATURAL)
			return Types.NATURAL;
		else
			return Types.ERR;
	}
	
	public static Types tipoOp4A(Oper op, Types t) {
		if(op == Oper.NOT) return tOp4ANot[t.ordinal()];
		else if(op == Oper.INV) return tOp4AMenos[t.ordinal()];
		else return null;
	}
	
	public static Types tipoOp4NA(Oper op, Types t) {
		switch(op) {
			case CFLOAT:return tOp4NACFloat[t.ordinal()];
			case CINT:	return tOp4NACInt[t.ordinal()];
			case CNAT: 	return tOp4NACNat[t.ordinal()];
			case CCHAR: return tOp4NACChar[t.ordinal()];
			default:	return null;
		}
	}
	
	public static Types Abs(Types t) {
		return tAbs[t.ordinal()];
	}
}
