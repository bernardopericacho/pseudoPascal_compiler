package Parser;

import java.util.ArrayList;

import common.Function;
import common.Instruction;

public class Codigo {
	public static final int longInicio = 4;
	public static final int longPrologo = 13;
	public static final int longEpilogo = 13;
	public static final int longApilaDirRetorno = 5;
	public static final int longInicioPasoParam = 3;
	public static final int longFinPasoParam = 1;
	public static final int longPasoParametro = 4;

	public ArrayList<Instruction> cod;

	public Codigo() {
		cod = new ArrayList<Instruction>();
	}

	public void emite(Instruction i) {
		cod.add(i);
	}

	public void emite(Oper o) {
		switch (o) {
		case AND:
			cod.add(new Instruction(Function.AND));
		case CCHAR:
			cod.add(new Instruction(Function.CCHAR));
		case CFLOAT:
			cod.add(new Instruction(Function.CFLOAT));
		case CINT:
			cod.add(new Instruction(Function.CINT));
		case CNAT:
			cod.add(new Instruction(Function.CNAT));
		case DESPDER:
			cod.add(new Instruction(Function.SHIFTR));
		case DESPIZQ:
			cod.add(new Instruction(Function.SHIFTL));
		case DISTINTO:
			cod.add(new Instruction(Function.NEQUAL));
		case DIV:
			cod.add(new Instruction(Function.DIV));
		case IGUAL:
			cod.add(new Instruction(Function.EQUAL));
		case INV:
			cod.add(new Instruction(Function.INV));
		case MAYOR:
			cod.add(new Instruction(Function.GREATER));
		case MAYORIG:
			cod.add(new Instruction(Function.GREATEREQ));
		case MENOR:
			cod.add(new Instruction(Function.LESS));
		case MENORIG:
			cod.add(new Instruction(Function.LESSEQ));
		case MOD:
			cod.add(new Instruction(Function.MOD));
		case MULT:
			cod.add(new Instruction(Function.MULT));
		case NOT:
			cod.add(new Instruction(Function.NOT));
		case OR:
			cod.add(new Instruction(Function.OR));
		case RESTA:
			cod.add(new Instruction(Function.SUB));
		case SUMA:
			cod.add(new Instruction(Function.ADD));
		}
	}

	public void parchea(int dir, Instruction i) {
		cod.set(dir, i);
	}

	public void parchea(int dir, Object o) {
		cod.get(dir).setArg(o);
	}

	public void parchea(ArrayList<Integer> listav, ArrayList<Integer> listaf,
			int etqv, int etqf) {
		if (listav != null)
		for (int i = 0; i < listav.size(); i++) {
			cod.set(listav.get(i).intValue(), new Instruction(Function.IR_V,
					etqv));
		}
		if (listaf != null)
		for (int i = 0; i < listaf.size(); i++) {
			cod.set(listaf.get(i).intValue(), new Instruction(Function.IR_F,
					etqf));
		}
	}

	public void inicio(int numNiveles, int tamDatos) {
		cod.add(new Instruction(Function.APILA, numNiveles + 1));
		cod.add(new Instruction(Function.DESAPILA_DIR, 1));
		cod.add(new Instruction(Function.APILA, 1 + numNiveles + tamDatos));
		cod.add(new Instruction(Function.DESAPILA_DIR, 0));
	}

	public void prologo(int nivel, int tamDatosLocales) {
		cod.add(new Instruction(Function.APILA_DIR, 0));
		cod.add(new Instruction(Function.APILA, 2));
		cod.add(new Instruction(Function.ADD));
		cod.add(new Instruction(Function.APILA_DIR, 1 + nivel));
		cod.add(new Instruction(Function.DESAPILA_IND));
		cod.add(new Instruction(Function.APILA_DIR, 0));
		cod.add(new Instruction(Function.APILA, 3));
		cod.add(new Instruction(Function.ADD));
		cod.add(new Instruction(Function.DESAPILA_DIR, 1 + nivel));
		cod.add(new Instruction(Function.APILA_DIR, 0));
		cod.add(new Instruction(Function.APILA, tamDatosLocales + 2));
		cod.add(new Instruction(Function.ADD));
		cod.add(new Instruction(Function.DESAPILA_DIR, 0));
	}

	public void epilogo(int nivel) {
		cod.add(new Instruction(Function.APILA_DIR, 1 + nivel));
		cod.add(new Instruction(Function.APILA, 2));
		cod.add(new Instruction(Function.SUB));
		cod.add(new Instruction(Function.APILA_IND));
		cod.add(new Instruction(Function.APILA_DIR, 1 + nivel));
		cod.add(new Instruction(Function.APILA, 3));
		cod.add(new Instruction(Function.SUB));
		cod.add(new Instruction(Function.COPIA));
		cod.add(new Instruction(Function.DESAPILA_DIR, 0));
		cod.add(new Instruction(Function.APILA, 2));
		cod.add(new Instruction(Function.ADD));
		cod.add(new Instruction(Function.APILA_IND));
		cod.add(new Instruction(Function.DESAPILA_DIR, 1 + nivel));
	}

	public void accesoVar(Data idProps) {
		cod.add(new Instruction(Function.APILA_DIR, 1 + idProps.nivel));
		cod.add(new Instruction(Function.APILA, idProps.dir));
		cod.add(new Instruction(Function.ADD));
		if (idProps.clase == Clases.PVAR)
			cod.add(new Instruction(Function.APILA_IND));
	}

	public int longAccesoVar(Data idProps) {
		if (idProps.clase == Clases.PVAR)
			return 4;
		else
			return 3;
	}

	public void apilarDirRetorno(int ret) {
		cod.add(new Instruction(Function.APILA_DIR, 0));
		cod.add(new Instruction(Function.APILA, 1));
		cod.add(new Instruction(Function.ADD));
		cod.add(new Instruction(Function.APILA, ret));
		cod.add(new Instruction(Function.DESAPILA_IND));
	}

	public void inicioPasoParam() {
		cod.add(new Instruction(Function.APILA_DIR, 0));
		cod.add(new Instruction(Function.APILA, 3));
		cod.add(new Instruction(Function.ADD));
	}

	public void finPasoParam() {
		cod.add(new Instruction(Function.DESAPILA));
	}

	public void pasoParametro(Modo modoReal, Param param) {
		cod.add(new Instruction(Function.APILA, param.props.dir));
		cod.add(new Instruction(Function.ADD));
		cod.add(new Instruction(Function.FLIP));
		if (param.modo == Modo.VALOR && modoReal == Modo.VARIABLE)
			cod.add(new Instruction(Function.MUEVE, param.tipo.tam));
		else
			cod.add(new Instruction(Function.DESAPILA_IND));

	}

	/*
	 * public void concatena(Codigo c) { cod.addAll(c.getCod()); }
	 */
	public void print() {
		for (int i = 0; i < cod.size(); i++) {
			System.out.println(cod.get(i).toString());
			System.out.println();
		}
	}
}