package Parser;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import common.Function;
import common.Instruction;

import Parser.Type.Array;
import Parser.Type.Proc;
import Parser.Type.Punt;
import Parser.Type.Ref;
import Parser.Type.Reg;
import Scanner.CatLexica;
import Scanner.Estado;
import Scanner.Scanner;

public class Parser {

	// ATRIBUTOS
	private Scanner al;
	private SymbolTable ts;
	public Codigo cod;
	private int etq;

	// CONSTRUCTORAS
	public Parser(Scanner al) {
		this.al = al;
		ts = new SymbolTable();
		cod = new Codigo();
		etq = 0;
	}

	// METODOS PRIVADOS
	private void Prog() throws Exception {
		boolean decs = false;
		if (!al.entrada(CatLexica.SEP)) {
			AtomicReference<Integer> n = new AtomicReference<Integer>();
			AtomicReference<Integer> dir = new AtomicReference<Integer>();
			cod.inicio(0, 0);
			etq = Codigo.longInicio - 1;
			cod.emite(new Instruction(Function.IR_A));
			etq++;
			Decs(0, 0, n, dir);
			int tmpetq = etq;
			cod.parchea(0, n.get() + 1);
			cod.parchea(2, n.get() + 1 + dir.get());
			cod.parchea(4, tmpetq);
			decs = true;
		}
		if (!decs) {
			cod.inicio(1, 0);
			etq = Codigo.longInicio - 1;
		}
		if (!al.reconoce(CatLexica.SEP))
			throw new Exception("Error sintactico");
		Is();
		if (!al.entrada(CatLexica.EOF))
			throw new Exception("Error sintactico");
		if (ts.hayPend())
			throw new Exception("Hay procedimientos o tipos sin declarar");
	}

	private void Decs(int dirh, int nh, AtomicReference<Integer> n,
			AtomicReference<Integer> dir) throws Exception {
		AtomicReference<Integer> n1 = new AtomicReference<Integer>();
		AtomicReference<Integer> n2 = new AtomicReference<Integer>();
		AtomicReference<Props> props = new AtomicReference<Props>();
		AtomicReference<String> id = new AtomicReference<String>();
		AtomicReference<Integer> tam = new AtomicReference<Integer>();
		AtomicReference<Boolean> fwd = new AtomicReference<Boolean>();
		Dec(dirh, nh, n1, props, id, tam, fwd);
		if (ts.existeId(id.get()) && (ts.getData(id.get()).nivel == nh)
				&& (props.get().clase != Clases.DECPROC))
			throw new Exception("Error declaracion");
		ts.ponId(id.get(), props.get());
		if (props.get().clase == Clases.TIPO)
			ts.remTipoPend(id.get());
		else if ((props.get().clase == Clases.DECPROC) && (!fwd.get()))
			ts.remProcPend(id.get());
		RDecs(dirh + tam.get(), nh, n2, dir);
		if (n1.get() >= n2.get())
			n.set(n1.get());
		else
			n.set(n2.get());
	}

	private void RDecs(int dirh, int nh, AtomicReference<Integer> n,
			AtomicReference<Integer> dir) throws Exception {
		if (al.entrada(CatLexica.PYCOMA)) {
			al.reconoce(CatLexica.PYCOMA);
			AtomicReference<Integer> n1 = new AtomicReference<Integer>();
			AtomicReference<Integer> n2 = new AtomicReference<Integer>();
			AtomicReference<Props> props = new AtomicReference<Props>();
			AtomicReference<String> id = new AtomicReference<String>();
			AtomicReference<Integer> tam = new AtomicReference<Integer>();
			AtomicReference<Boolean> fwd = new AtomicReference<Boolean>();
			Dec(dirh, nh, n1, props, id, tam, fwd);
			if (ts.existeId(id.get())
					&& (ts.getData(id.get()).nivel == nh)
					&& ((props.get().clase != Clases.DECPROC) || (ts.esPend(id
							.get()) || fwd.get())))
				throw new Exception("Error declaracion");
			ts.ponId(id.get(), props.get());
			if (props.get().clase == Clases.TIPO)
				ts.remTipoPend(id.get());
			else if ((props.get().clase == Clases.DECPROC) && (!fwd.get()))
				ts.remProcPend(id.get());
			RDecs(dirh + tam.get(), nh, n2, dir);
			if (n1.get() >= n2.get())
				n.set(n1.get());
			else
				n.set(n2.get());
		} else { // lambda
			n.set(nh);
			dir.set(dirh);
		}
	}

	private void Dec(int dirh, int nh, AtomicReference<Integer> n,
			AtomicReference<Props> props, AtomicReference<String> id,
			AtomicReference<Integer> tam, AtomicReference<Boolean> fwd)
			throws Exception {
		AtomicReference<Type> tipo = new AtomicReference<Type>();
		AtomicReference<Props> decprops = new AtomicReference<Props>();
		AtomicReference<Integer> ini = new AtomicReference<Integer>();
		if (DecVar(id, tipo)) {
			props.set(new Props(Clases.VAR, tipo.get(), nh, dirh, 0));
			tam.set(tipo.get().tam);
			n.set(nh);
			fwd.set(false);
		} else if (DecTipo(id, tipo)) {
			props.set(new Props(Clases.TIPO, tipo.get(), nh, 0, 0));
			tam.set(0);
			n.set(nh);
			fwd.set(false);
		} else if (DecProc(dirh, nh, id, n, decprops, ini, fwd)) {
			props.set(new Props(decprops.get().clase, decprops.get().tipo, nh,
					0, ini.get()));
			tam.set(0);
		} else
			throw new Exception("Error sintactico");
	}

	private boolean DecVar(AtomicReference<String> id,
			AtomicReference<Type> tipo) throws Exception {
		String lex = al.leeLexema();
		if (!al.reconoce(CatLexica.ID))
			return false;
		id.set(lex);
		if (!al.reconoce(CatLexica.DOSPUNTOS))
			throw new Exception("Error sintactico");
		Tipo(tipo);
		if (ts.existeId(lex))
			throw new Exception("ID repetido");
		if (ts.refErronea(id.get()))
			throw new Exception("Referencia erronea");
		return true;
	}

	private boolean DecTipo(AtomicReference<String> id,
			AtomicReference<Type> tipo) throws Exception {
		if (!al.entrada(CatLexica.TIPO))
			return false;
		al.reconoce(CatLexica.TIPO);
		String lex = al.leeLexema();
		if (!al.reconoce(CatLexica.ID))
			throw new Exception("Error sintactico");
		id.set(lex);
		if (!al.reconoce(CatLexica.IGUAL))
			throw new Exception("Error sintactico");
		Tipo(tipo);
		if (ts.existeId(lex))
			throw new Exception("ID repetido");
		if (ts.refErronea(id.get()))
			throw new Exception("Referencia erronea");
		return true;
	}

	private boolean Tipo(AtomicReference<Type> tipo) throws Exception {
		if (al.entrada(CatLexica.CHARACTER)) {
			al.reconoce(CatLexica.CHARACTER);
			tipo.set(new Type(Types.CHARACTER, 1));
			return true;
		} else if (al.entrada(CatLexica.NATURAL)) {
			al.reconoce(CatLexica.NATURAL);
			tipo.set(new Type(Types.NATURAL, 1));
			return true;
		} else if (al.entrada(CatLexica.INTEGER)) {
			al.reconoce(CatLexica.INTEGER);
			tipo.set(new Type(Types.INTEGER, 1));
			return true;
		} else if (al.entrada(CatLexica.FLOAT)) {
			al.reconoce(CatLexica.FLOAT);
			tipo.set(new Type(Types.FLOAT, 1));
			return true;
		} else if (al.entrada(CatLexica.BOOLEAN)) {
			al.reconoce(CatLexica.BOOLEAN);
			tipo.set(new Type(Types.BOOLEAN, 1));
			return true;
		} else if (al.entrada(CatLexica.ID)) {
			String lex = al.leeLexema();
			al.reconoce(CatLexica.ID);
			if (ts.existeId(lex)) {
				if (ts.getData(lex).clase != Clases.TIPO)
					throw new Exception("No es un tipo");
			}
			if (!ts.existeId(lex))
				ts.addTipoPend(lex);
			Type t;
			if (ts.existeId(lex))
				t = new Type(Types.REF, ts.getData(lex).tipo.tam, lex);
			else
				t = new Type(Types.REF, 0, lex);
			tipo.set(t);
			return true;
		} else if (al.entrada(CatLexica.ARRAY)) {
			al.reconoce(CatLexica.ARRAY);
			if (!al.reconoce(CatLexica.CORCHETEIZQ))
				throw new Exception("Error sintactico");
			String lex = al.leeLexema();
			if (!al.reconoce(CatLexica.NNAT))
				throw new Exception("Error sintactico");
			if (!al.reconoce(CatLexica.CORCHETEDER))
				throw new Exception("Error sintactico");
			if (!al.reconoce(CatLexica.OF))
				throw new Exception("Error sintactico");
			AtomicReference<Type> tipo1 = new AtomicReference<Type>();
			Tipo(tipo1);
			if ((tipo1.get().tipo == Types.REF)
					&& (ts.refErronea(((Ref) tipo1.get().elems).id)))
				throw new Exception("Referencia erronea");
			Type t = new Type(Types.ARRAY, Integer.parseInt(lex)
					* tipo1.get().tam, Integer.parseInt(lex), tipo1.get());
			tipo.set(t);
			return true;
		} else if (al.entrada(CatLexica.POINTER)) {
			al.reconoce(CatLexica.POINTER);
			AtomicReference<Type> tipo1 = new AtomicReference<Type>();
			Tipo(tipo1);
			Type t = new Type(Types.POINTER, 1, tipo1.get());
			tipo.set(t);
			return true;
		} else if (al.entrada(CatLexica.RECORD)) {
			AtomicReference<ArrayList<Campo>> campos = new AtomicReference<ArrayList<Campo>>();
			campos.set(new ArrayList<Campo>());
			AtomicReference<Integer> tam = new AtomicReference<Integer>();
			al.reconoce(CatLexica.RECORD);
			if (!al.reconoce(CatLexica.LLAVEIZQ))
				throw new Exception("Error sintactico");
			Campos(campos, tam);
			if (!al.reconoce(CatLexica.LLAVEDER))
				throw new Exception("Error sintactico");
			tipo.set(new Type(Types.RECORD, tam.get(), campos.get()));
			return true;
		} else {
			return false;
		}
	}

	private void Campos(AtomicReference<ArrayList<Campo>> campos,
			AtomicReference<Integer> tam) throws Exception {
		AtomicReference<Campo> campo = new AtomicReference<Campo>();
		AtomicReference<Integer> tam1 = new AtomicReference<Integer>();
		AtomicReference<String> id = new AtomicReference<String>();
		Campo(0, tam1, campo, id);
		ArrayList<Campo> campos1 = new ArrayList<Campo>();
		campos1.add(campo.get());
		RCampos(tam1.get(), campos1, tam, campos);
	}

	private void RCampos(int desph, ArrayList<Campo> camposh,
			AtomicReference<Integer> tam,
			AtomicReference<ArrayList<Campo>> campos) throws Exception {
		if (al.entrada(CatLexica.PYCOMA)) {
			AtomicReference<Integer> tam1 = new AtomicReference<Integer>();
			AtomicReference<Campo> campo = new AtomicReference<Campo>();
			AtomicReference<String> id = new AtomicReference<String>();
			ArrayList<Campo> camposh1 = new ArrayList<Campo>();
			al.reconoce(CatLexica.PYCOMA);
			Campo(desph, tam1, campo, id);
			if (ts.esDuplicado(camposh, id.get()))
				throw new Exception("Campo duplicado");
			camposh1 = camposh;
			camposh1.add(campo.get());
			RCampos(desph + tam1.get(), camposh1, tam, campos);
		} else { // lambda
			tam.set(desph);
			campos.set(camposh);
		}
	}

	private void Campo(int desph, AtomicReference<Integer> tam,
			AtomicReference<Campo> campo, AtomicReference<String> id)
			throws Exception {
		AtomicReference<Type> tipo = new AtomicReference<Type>();
		Tipo(tipo);
		id.set(al.leeLexema());
		tam.set(tipo.get().tam);
		if (!al.reconoce(CatLexica.ID))
			throw new Exception("Error sintactico");
		Campo c = new Campo(id.get(), tipo.get(), desph);
		campo.set(c);
	}

	private boolean DecProc(int dirh, int nh, AtomicReference<String> id,
			AtomicReference<Integer> n, AtomicReference<Props> decprops,
			AtomicReference<Integer> ini, AtomicReference<Boolean> fwd)
			throws Exception {
		AtomicReference<ArrayList<Param>> params = new AtomicReference<ArrayList<Param>>();
		AtomicReference<Integer> dir = new AtomicReference<Integer>();
		AtomicReference<Integer> init = new AtomicReference<Integer>();
		if (!al.entrada(CatLexica.PROCEDURE))
			return false;
		al.reconoce(CatLexica.PROCEDURE);
		String lex = al.leeLexema();
		if (!al.entrada(CatLexica.ID))
			throw new Exception("Error sintactico");
		id.set(lex);
		al.reconoce(CatLexica.ID);
		if (!al.reconoce(CatLexica.PAP))
			throw new Exception("Error sintactico");
		ts.creaTSHija();
		Params(dirh, nh + 1, params, dir);
		if (!al.reconoce(CatLexica.PCIE))
			throw new Exception("Error sintactico");
		decprops.set(new Props(Clases.DECPROC, new Type(Types.PROC, params
				.get()), nh + 1, 0, 0));
		if (ts.existeId(id.get()) && (ts.getData(id.get()).nivel == nh + 1))
			throw new Exception("ID duplicado");
		ts.ponId(id.get(), decprops.get());
		Bloque(dir.get(), nh + 1, n, init, fwd);
		if (fwd.get())
			ts.addProcPend(id.get());
		ini.set(init.get());
		ts.getData(id.get()).init = init.get();
		ts.quitaTSHija();
		return true;
	}

	private void Params(int dirh, int nh,
			AtomicReference<ArrayList<Param>> params,
			AtomicReference<Integer> dir) throws Exception {
		if (LParams(nh, params, dir)) {
		} else { // lambda
			params.set(new ArrayList<Param>());
			dir.set(0);
		}
	}

	private boolean LParams(int nh, AtomicReference<ArrayList<Param>> params,
			AtomicReference<Integer> dir) throws Exception {
		AtomicReference<Param> param = new AtomicReference<Param>();
		AtomicReference<Integer> tam = new AtomicReference<Integer>();
		AtomicReference<String> id = new AtomicReference<String>();
		AtomicReference<Props> props = new AtomicReference<Props>();
		if (!Param(0, nh, param, tam, id, props))
			return false;
		ArrayList<Param> paramsh = new ArrayList<Param>();
		paramsh.add(param.get());
		ts.ponId(id.get(), props.get());
		RLParams(tam.get(), nh, paramsh, dir, params);
		return true;
	}

	private void RLParams(int dirh, int nh, ArrayList<Param> paramsh,
			AtomicReference<Integer> dir,
			AtomicReference<ArrayList<Param>> params) throws Exception {
		if (al.entrada(CatLexica.COMA)) {
			AtomicReference<Param> param = new AtomicReference<Param>();
			AtomicReference<Integer> tam = new AtomicReference<Integer>();
			AtomicReference<String> id = new AtomicReference<String>();
			AtomicReference<Props> props = new AtomicReference<Props>();
			al.reconoce(CatLexica.COMA);
			Param(dirh, nh, param, tam, id, props);
			paramsh.add(param.get());
			if (ts.existeId(id.get()) && (ts.getData(id.get()).nivel == nh))
				throw new Exception("ID duplicado");
			ts.ponId(id.get(), props.get());
			RLParams(dirh + tam.get(), nh, paramsh, dir, params);
		} else { // lambda
			dir.set(dirh);
			params.set(paramsh);
		}
	}

	private boolean Param(int dirh, int nh, AtomicReference<Param> param,
			AtomicReference<Integer> tam, AtomicReference<String> id,
			AtomicReference<Props> props) throws Exception {
		AtomicReference<Clases> clase = new AtomicReference<Clases>();
		AtomicReference<Modo> modo = new AtomicReference<Modo>();
		AtomicReference<Type> tipo = new AtomicReference<Type>();
		Modo(clase, modo);
		if (!Tipo(tipo))
			return false;
		String lex = al.leeLexema();
		if (!al.entrada(CatLexica.ID))
			throw new Exception("Error sintactico");
		al.reconoce(CatLexica.ID);
		id.set(lex);
		props.set(new Props(clase.get(), tipo.get(), nh, dirh, 0));
		param.set(new Param(modo.get(), tipo.get(), props.get()));
		if (modo.get() == Modo.VARIABLE)
			tam.set(1);
		else
			tam.set(tipo.get().tam);
		return true;
	}

	private void Modo(AtomicReference<Clases> clase, AtomicReference<Modo> modo) {
		if (al.entrada(CatLexica.VAR)) {
			al.reconoce(CatLexica.VAR);
			clase.set(Clases.PVAR);
			modo.set(Modo.VARIABLE);
		} else { // lambda
			clase.set(Clases.VAR);
			modo.set(Modo.VALOR);
		}
	}

	private void Bloque(int dirh, int nh, AtomicReference<Integer> n,
			AtomicReference<Integer> init, AtomicReference<Boolean> fwd)
			throws Exception {
		if (al.entrada(CatLexica.FORWARD)) {
			al.reconoce(CatLexica.FORWARD);
			fwd.set(true);
			n.set(nh);
			init.set(0);
		} else if (al.entrada(CatLexica.LLAVEIZQ)) {
			al.reconoce(CatLexica.LLAVEIZQ);
			RBloque(dirh, nh, n, init);
			fwd.set(false);
		} else {
			throw new Exception("Error sintactico");
		}
	}

	private void RBloque(int dirh, int nh, AtomicReference<Integer> n,
			AtomicReference<Integer> inicio) throws Exception {
		AtomicReference<Integer> dir = new AtomicReference<Integer>();
		if (!al.entrada(CatLexica.SEP)) {
			Decs(dirh, nh, dir, n);
		}
		if (!al.reconoce(CatLexica.SEP))
			throw new Exception("Error sintactico");
		inicio.set(etq);
		etq += Codigo.longPrologo;
		cod.prologo(nh, dirh);
		Is();
		if (!al.reconoce(CatLexica.LLAVEDER))
			throw new Exception("Error sintactico");
		etq += Codigo.longEpilogo + 1;
		cod.epilogo(nh);
		cod.emite(new Instruction(Function.IR_IND));
		n.set(nh);
	}

	private void Is() throws Exception {
		I();
		RIs();
	}

	private void RIs() throws Exception {
		if (al.entrada(CatLexica.PYCOMA)) {
			al.reconoce(CatLexica.PYCOMA);
			I();
			RIs();
		}
		// else lambda
	}

	private void I() throws Exception {
		String lex = al.leeLexema();
		if (al.entrada(CatLexica.ID)) {
			if (ts.getData(lex).clase == Clases.DECPROC) {
				ICall();
			} else
				IAsig();
		}

		else if (ILec()) {
		} else if (IEsc()) {
		} else if (IIf()) {
		} else if (IWhile()) {
		} else if (IFor()) {
		} else if (INew()) {
		} else if (IDelete()) {
		} else {
			throw new Exception("Error sintactico");
		}
	}

	private boolean IAsig() throws Exception {
		AtomicReference<Type> tipo = new AtomicReference<Type>();
		AtomicReference<Type> tipo1 = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo = new AtomicReference<Modo>();
		if (!Desig(tipo))
			return false;
		if (al.entrada(CatLexica.ASIG)) {
			al.reconoce(CatLexica.ASIG);
			Exp0(false, tipo1, listav, listaf, modo);
			if (!ts.compatibles(tipo.get(), tipo1.get()))
				throw new Exception("Tipos incompatibles");
			return true;
		} else
			throw new Exception("Error sintactico");
	}

	private boolean Exp0(boolean parh, AtomicReference<Type> tipo,
			AtomicReference<ArrayList<Integer>> listav,
			AtomicReference<ArrayList<Integer>> listaf,
			AtomicReference<Modo> modo) throws Exception {
		AtomicReference<Type> tipo1 = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo1 = new AtomicReference<Modo>();
		if (!Exp1(false, tipo1, listav1, listaf1, modo1))
			return false;
		cod.parchea(listav1.get(), listaf1.get(), etq, etq);
		RExp0(modo1.get(), tipo1.get(), listav1.get(), listaf1.get(), listav,
				listaf, modo, tipo);
		return true;
	}

	private void RExp0(Modo modoh, Type tipoh, ArrayList<Integer> listavh,
			ArrayList<Integer> listafh,
			AtomicReference<ArrayList<Integer>> listav,
			AtomicReference<ArrayList<Integer>> listaf,
			AtomicReference<Modo> modo, AtomicReference<Type> tipo)
			throws Exception {
		AtomicReference<Oper> op = new AtomicReference<Oper>();
		AtomicReference<Type> tipo1 = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo1 = new AtomicReference<Modo>();
		if (OP0(op)) {
			Exp1(false, tipo1, listav1, listaf1, modo1);
			cod.parchea(listav1.get(), listaf1.get(), etq, etq);
			cod.emite(op.get());
			etq++;
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			tipo.set(new Type(TypeTables.tipoOp0(op.get(), tipoh.tipo, tipo1
					.get().tipo), 0));
			modo.set(Modo.VALOR);
		} else { // lambda
			listav.set(listavh);
			listaf.set(listafh);
			tipo.set(tipoh);
			modo.set(modoh);
		}
	}

	private boolean Exp1(boolean parh, AtomicReference<Type> tipo,
			AtomicReference<ArrayList<Integer>> listav,
			AtomicReference<ArrayList<Integer>> listaf,
			AtomicReference<Modo> modo) throws Exception {
		AtomicReference<Type> tipo1 = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo1 = new AtomicReference<Modo>();
		if (!Exp2(false, tipo1, listav1, listaf1, modo1))
			return false;
		RExp1(modo1.get(), tipo1.get(), listav1.get(), listaf1.get(), listav,
				listaf, modo, tipo);
		return true;
	}

	private void RExp1(Modo modoh, Type tipoh, ArrayList<Integer> listavh,
			ArrayList<Integer> listafh,
			AtomicReference<ArrayList<Integer>> listav,
			AtomicReference<ArrayList<Integer>> listaf,
			AtomicReference<Modo> modo, AtomicReference<Type> tipo)
			throws Exception {
		AtomicReference<Oper> op = new AtomicReference<Oper>();
		AtomicReference<Type> tipo1 = new AtomicReference<Type>();
		Type tipoh1;
		AtomicReference<ArrayList<Integer>> listav1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> lv = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> lf = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo1 = new AtomicReference<Modo>();
		AtomicReference<Modo> mod = new AtomicReference<Modo>();
		if (OP1(op)) {
			cod.parchea(listavh, listafh, etq, etq);
			Exp2(false, tipo1, listav1, listaf1, modo1);
			cod.parchea(listav1.get(), listaf1.get(), etq, etq);
			cod.emite(op.get());
			etq++;
			tipoh1 = new Type(TypeTables.tipoOp1(tipoh.tipo, tipo1.get().tipo),
					0);
			RExp1(Modo.VALOR, tipoh1, listav1.get(), listaf1.get(), lv, lf,
					mod, tipo);
		} else if (al.entrada(CatLexica.OR)) {
			al.reconoce(CatLexica.OR);
			int tmpetq = etq;
			cod.parchea(null, listafh, 0, etq + 2);
			cod.emite(new Instruction(Function.COPIA));
			cod.emite(new Instruction(Function.IR_V));
			cod.emite(new Instruction(Function.DESAPILA));
			etq += 3;
			Exp2(false, tipo1, listav1, listaf1, modo1);
			cod.parchea(null, listaf1.get(), 0, etq);
			tipoh1 = new Type(TypeTables.tipoOr(tipoh.tipo, tipo1.get().tipo),
					0);
			RExp1(Modo.VALOR, tipoh1, listav1.get(), listaf1.get(), lv, lv,
					mod, tipo);
			listaf.set(new ArrayList<Integer>());
			listavh.addAll(listav1.get());
			listavh.add(tmpetq + 1);
			listav.set(listavh);
			modo.set(Modo.VALOR);
		} else { // lambda
			listaf.set(listafh);
			listav.set(listavh);
			modo.set(modoh);
			tipo.set(tipoh);
		}
	}

	private boolean Exp2(boolean parh, AtomicReference<Type> tipo,
			AtomicReference<ArrayList<Integer>> listav,
			AtomicReference<ArrayList<Integer>> listaf,
			AtomicReference<Modo> modo) throws Exception {
		AtomicReference<Type> tipo1 = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo1 = new AtomicReference<Modo>();
		if (!Exp3(false, tipo1, listav1, listaf1, modo1))
			return false;
		RExp2(modo1.get(), tipo1.get(), listav1.get(), listaf1.get(), listav,
				listaf, modo, tipo);
		return true;
	}

	private void RExp2(Modo modoh, Type tipoh, ArrayList<Integer> listavh,
			ArrayList<Integer> listafh,
			AtomicReference<ArrayList<Integer>> listav,
			AtomicReference<ArrayList<Integer>> listaf,
			AtomicReference<Modo> modo, AtomicReference<Type> tipo)
			throws Exception {
		AtomicReference<Type> tipo1 = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> lv = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> lf = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Oper> op = new AtomicReference<Oper>();
		AtomicReference<Modo> modo1 = new AtomicReference<Modo>();
		AtomicReference<Modo> mod = new AtomicReference<Modo>();
		if (OP2(op)) {
			cod.parchea(listavh, listafh, etq, etq);
			Exp3(false, tipo1, listav1, listaf1, modo1);
			cod.parchea(listav1.get(), listaf1.get(), etq, etq);
			cod.emite(op.get());
			etq++;
			Type tipoh1 = new Type(TypeTables.tipoOp2(op.get(), tipoh.tipo,
					tipo1.get().tipo), 0);
			RExp2(Modo.VALOR, tipoh1, listav1.get(), listaf1.get(), lv, lf,
					mod, tipo);
			listaf.set(new ArrayList<Integer>());
			listav.set(new ArrayList<Integer>());
		} else if (al.entrada(CatLexica.AND)) {
			al.reconoce(CatLexica.AND);
			int tmpetq = etq;
			cod.parchea(listavh, null, etq + 2, 0);
			cod.emite(new Instruction(Function.COPIA));
			cod.emite(new Instruction(Function.IR_F));
			cod.emite(new Instruction(Function.DESAPILA));
			etq += 3;
			Exp3(false, tipo1, listav1, listaf1, modo1);
			cod.parchea(listav1.get(), null, etq, 0);
			Type tipoh1 = new Type(TypeTables.tipoAnd(tipoh.tipo,
					tipo1.get().tipo), 0);
			RExp2(Modo.VALOR, tipoh1, listav1.get(), listaf1.get(), lv, lf,
					mod, tipo);
			listav.set(new ArrayList<Integer>());
			listafh.addAll(listaf1.get());
			listafh.add(tmpetq + 1);
			listaf.set(listafh);
			modo.set(Modo.VALOR);
		} else { // lambda
			listav.set(listavh);
			listaf.set(listafh);
			modo.set(modoh);
			tipo.set(tipoh);
		}
	}

	private boolean Exp3(boolean parh, AtomicReference<Type> tipo,
			AtomicReference<ArrayList<Integer>> listav,
			AtomicReference<ArrayList<Integer>> listaf,
			AtomicReference<Modo> modo) throws Exception {
		AtomicReference<Type> tipo1 = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo1 = new AtomicReference<Modo>();
		if (!Exp4(false, tipo1, listav1, listaf1, modo1))
			return false;
		cod.parchea(listav1.get(), listaf1.get(), etq, etq);
		RExp3(modo1.get(), tipo1.get(), listav1.get(), listaf1.get(), listav,
				listaf, modo, tipo);
		return true;
	}

	private void RExp3(Modo modoh, Type tipoh, ArrayList<Integer> listavh,
			ArrayList<Integer> listafh,
			AtomicReference<ArrayList<Integer>> listav,
			AtomicReference<ArrayList<Integer>> listaf,
			AtomicReference<Modo> modo, AtomicReference<Type> tipo)
			throws Exception {
		AtomicReference<Oper> op = new AtomicReference<Oper>();
		AtomicReference<Type> tipo1 = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo1 = new AtomicReference<Modo>();
		if (OP3(op)) {
			Exp4(false, tipo1, listav1, listaf1, modo1);
			cod.parchea(listav1.get(), listaf1.get(), etq, etq);
			cod.emite(op.get());
			etq++;
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			modo.set(Modo.VALOR);
			tipo.set(new Type(TypeTables.tipoOp3(tipoh.tipo, tipo1.get().tipo),
					0));
		} else { // else
			listav.set(listavh);
			listaf.set(listafh);
			modo.set(modoh);
			tipo.set(tipoh);
		}
	}

	private boolean Exp4(boolean parh, AtomicReference<Type> tipo,
			AtomicReference<ArrayList<Integer>> listav,
			AtomicReference<ArrayList<Integer>> listaf,
			AtomicReference<Modo> modo) throws Exception {
		AtomicReference<Oper> op = new AtomicReference<Oper>();
		AtomicReference<Type> tipo1 = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo1 = new AtomicReference<Modo>();
		if (OP4A(op)) {
			Exp4(false, tipo1, listav1, listaf1, modo1);
			tipo.set(new Type(TypeTables.tipoOp4A(op.get(), tipo1.get().tipo),
					0));
			modo.set(Modo.VALOR);
			cod.parchea(listav1.get(), listaf1.get(), etq, etq);
			cod.emite(op.get());
			etq++;
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			return true;
		} else if (OP4NA(op)) {
			Exp5(false, tipo1, listav1, listaf1, modo1);
			tipo.set(new Type(TypeTables.tipoOp4NA(op.get(), tipo1.get().tipo),
					0));
			modo.set(Modo.VALOR);
			cod.parchea(listav1.get(), listaf1.get(), etq, etq);
			cod.emite(op.get());
			etq++;
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			return true;
		} else {
			if (Exp5(parh, tipo, listav, listaf, modo))
				return true;
			else
				return false;
		}
	}

	private boolean Exp5(boolean parh, AtomicReference<Type> tipo,
			AtomicReference<ArrayList<Integer>> listav,
			AtomicReference<ArrayList<Integer>> listaf,
			AtomicReference<Modo> modo) throws Exception {
		if (al.entrada(CatLexica.PAP)) {
			al.reconoce(CatLexica.PAP);
			Exp0(parh, tipo, listav, listaf, modo);
			if (!al.reconoce(CatLexica.PCIE))
				throw new Exception("Error sintactico");
			return true;
		} else if (al.entrada(CatLexica.NNAT)) {
			String lex = al.leeLexema();
			al.reconoce(CatLexica.NNAT);
			tipo.set(new Type(Types.NATURAL, 0));
			modo.set(Modo.VALOR);
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			cod.emite(new Instruction(Function.APILA, lex));
			etq++;
			return true;
		} else if (al.entrada(CatLexica.NFLOAT)) {
			String lex = al.leeLexema();
			al.reconoce(CatLexica.NFLOAT);
			tipo.set(new Type(Types.FLOAT, 0));
			modo.set(Modo.VALOR);
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			cod.emite(new Instruction(Function.APILA, lex));
			etq++;
			return true;
		} else if (al.entrada(CatLexica.MENOS)) {
			al.reconoce(CatLexica.MENOS);
			String lex = al.leeLexema();
			if (!al.reconoce(CatLexica.NNAT))
				throw new Exception("Error sintactico");
			tipo.set(new Type(Types.INTEGER, 0));
			modo.set(Modo.VALOR);
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			cod.emite(new Instruction(Function.APILA, lex));
			cod.emite(new Instruction(Function.INV));
			etq += 2;
			return true;
		} else if (al.entrada(CatLexica.MAS)) {
			al.reconoce(CatLexica.MAS);
			String lex = al.leeLexema();
			if (!al.reconoce(CatLexica.NNAT))
				throw new Exception("Error sintactico");
			tipo.set(new Type(Types.INTEGER, 0));
			modo.set(Modo.VALOR);
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			cod.emite(new Instruction(Function.APILA, lex));
			etq++;
			return true;
		} else if (al.entrada(CatLexica.TRUE)) {
			al.reconoce(CatLexica.TRUE);
			tipo.set(new Type(Types.BOOLEAN, 0));
			modo.set(Modo.VALOR);
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			cod.emite(new Instruction(Function.APILA, true));
			etq++;
			return true;
		} else if (al.entrada(CatLexica.FALSE)) {
			al.reconoce(CatLexica.FALSE);
			tipo.set(new Type(Types.BOOLEAN, 0));
			modo.set(Modo.VALOR);
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			cod.emite(new Instruction(Function.APILA, false));
			etq++;
			return true;
		} else if (al.entrada(CatLexica.NULL)) {
			al.reconoce(CatLexica.NULL);
			tipo.set(new Type(Types.POINTER, 0, new Type(Types.ERR, 0)));
			modo.set(Modo.VALOR);
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			cod.emite(new Instruction(Function.APILA, null));
			etq++;
			return true;
		} else if (al.entrada(CatLexica.CHAR)) {
			String lex = al.leeLexema();
			al.reconoce(CatLexica.CHAR);
			tipo.set(new Type(Types.CHARACTER, 0));
			modo.set(Modo.VALOR);
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			cod.emite(new Instruction(Function.APILA, lex));
			etq++;
			return true;
		} else if (Desig(tipo)) {
			modo.set(Modo.VARIABLE);
			if (ts.compatibleTipoBasico(tipo.get())) {
				cod.emite(new Instruction(Function.APILA_IND));
				etq++;
			}
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			return true;
		} else if (al.entrada(CatLexica.BARRAV)) {
			al.reconoce(CatLexica.BARRAV);
			AtomicReference<Type> tipo1 = new AtomicReference<Type>();
			Exp0(false, tipo1, listav, listaf, modo);
			if (!al.reconoce(CatLexica.BARRAV))
				throw new Exception("Error sintactico");
			tipo.set(new Type(TypeTables.Abs(tipo1.get().tipo), 0));
			modo.set(Modo.VALOR);
			cod.emite(new Instruction(Function.ABS));
			etq++;
			listav.set(new ArrayList<Integer>());
			listaf.set(new ArrayList<Integer>());
			return true;
		} else {
			throw new Exception("Error sintactico");
		}
	}

	private boolean Desig(AtomicReference<Type> tipo) throws Exception {
		if (al.entrada(CatLexica.ID)) {
			Type tipoh = new Type(Types.ERR, 0);
			String lex = al.leeLexema();
			al.reconoce(CatLexica.ID);

			if (ts.existeId(lex))
				if (ts.getData(lex).clase == Clases.PVAR)
					tipoh = ts.sigueRef(ts.getData(lex).tipo);
			if (ts.getData(lex).clase == Clases.VAR)
				tipoh = ts.sigueRef(ts.getData(lex).tipo);
			cod.accesoVar(ts.getData(lex));
			etq += cod.longAccesoVar(ts.getData(lex));
			RDesig(tipoh, tipo);
			return true;
		}
		return false;
	}

	private void RDesig(Type tipoh, AtomicReference<Type> tipo)
			throws Exception {
		Type tipoh1 = new Type(Types.ERR, 0);
		if (al.entrada(CatLexica.FLECHA)) {
			al.reconoce(CatLexica.FLECHA);
			if (tipoh.tipo == Types.POINTER)
				tipoh1 = ts.sigueRef(((Punt) tipoh.elems).tBase);
			cod.emite(new Instruction(Function.APILA_IND));
			etq++;
			RDesig(tipoh1, tipo);
		} else if (al.entrada(CatLexica.CORCHETEIZQ)) {
			al.reconoce(CatLexica.CORCHETEIZQ);
			AtomicReference<Type> tipo1 = new AtomicReference<Type>();
			AtomicReference<ArrayList<Integer>> listav = new AtomicReference<ArrayList<Integer>>();
			AtomicReference<ArrayList<Integer>> listaf = new AtomicReference<ArrayList<Integer>>();
			AtomicReference<Modo> modo = new AtomicReference<Modo>();
			Exp0(false, tipo1, listav, listaf, modo);
			if (al.entrada(CatLexica.CORCHETEDER)) {
				al.reconoce(CatLexica.CORCHETEDER);
				if ((tipoh.tipo == Types.ARRAY)
						&& ((tipo1.get().tipo == Types.NATURAL) || (tipo1.get().tipo == Types.INTEGER)))
					tipoh1 = ts.sigueRef(((Array) tipoh.elems).tBase);
				cod.emite(new Instruction(Function.APILA,
						((Array) tipoh.elems).tBase.tam));
				cod.emite(Oper.MULT);
				cod.emite(Oper.SUMA);
				etq += 3;
				RDesig(tipoh1, tipo);
			}
		} else if (al.entrada(CatLexica.PUNTO)) {
			al.reconoce(CatLexica.PUNTO);

			tipoh1 = ts.sigueRef(tipoh);
			if (al.entrada(CatLexica.ID)) {
				String lex = al.leeLexema();
				al.reconoce(CatLexica.ID);
				if (tipoh.tipo == Types.RECORD)
					if (ts.esDuplicado(((Reg) tipoh.elems).campos, lex)) {
						tipoh1 = ts
								.sigueRef(((Reg) tipoh.elems).getCampo(lex).tipo);
					}
				cod.emite(new Instruction(Function.APILA, ((Reg) tipoh.elems)
						.getCampo(lex).desp));
				cod.emite(new Instruction(Function.ADD));
				etq += 2;
				RDesig(tipoh1, tipo);
			}
		} else { // lambda
			tipo.set(tipoh);
		}
	}

	private boolean ILec() throws Exception {
		if (!al.entrada(CatLexica.IN))
			return false;
		al.reconoce(CatLexica.IN);
		if (!al.reconoce(CatLexica.PAP))
			throw new Exception("Error sintactico");
		String lex = al.leeLexema();
		if (!al.reconoce(CatLexica.ID))
			throw new Exception("Error sintactico");
		if (!al.reconoce(CatLexica.PCIE))
			throw new Exception("Error sintactico");
		if (!ts.existeId(lex))
			throw new Exception("ID desconocido");
		cod.emite(new Instruction(Function.IN));
		cod.emite(new Instruction(Function.APILA_DIR, ts.getData(lex).dir));
		etq += 2;
		return true;
	}

	private boolean IEsc() throws Exception {
		AtomicReference<Type> tipo = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo = new AtomicReference<Modo>();
		if (!al.entrada(CatLexica.OUT))
			return false;
		al.reconoce(CatLexica.OUT);
		if (!al.reconoce(CatLexica.PAP))
			throw new Exception("Error sintactico");
		Exp0(false, tipo, listav, listaf, modo);
		if (!al.reconoce(CatLexica.PCIE))
			throw new Exception("Error sintactico");
		cod.emite(new Instruction(Function.OUT));
		etq++;
		return true;
	}

	private boolean IIf() throws Exception {
		AtomicReference<Type> tipo = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo = new AtomicReference<Modo>();
		AtomicReference<Integer> etqi = new AtomicReference<Integer>();
		int tmpetq = 0;
		if (!al.entrada(CatLexica.IF))
			return false;
		al.reconoce(CatLexica.IF);
		Exp0(false, tipo, listav, listaf, modo);
		if (!al.reconoce(CatLexica.THEN))
			throw new Exception("Error sintactico");
		cod.parchea(listav.get(), listaf.get(), etq, etq);
		cod.emite(new Instruction(Function.IR_F, etqi));
		etq++;
		tmpetq = etq;
		BloqueI();
		PElse(etqi);
		cod.parchea(tmpetq, etqi.get());
		if (tipo.get().tipo != Types.BOOLEAN)
			throw new Exception("Error de tipos");
		return true;
	}

	private void PElse(AtomicReference<Integer> etqi) throws Exception {
		if (al.entrada(CatLexica.ELSE)) {
			int tmpetq = 0;
			al.reconoce(CatLexica.ELSE);
			cod.emite(new Instruction(Function.IR_A, tmpetq));
			etq++;
			etqi.set(etq);
			BloqueI();
			tmpetq = etq;
			cod.parchea(etqi.get(), tmpetq);
		} else { // lambda
			etqi.set(etq);
		}
	}

	private boolean IWhile() throws Exception {
		AtomicReference<Type> tipo = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo = new AtomicReference<Modo>();
		int tmpetq = 0;
		int principio = 0;
		int parchetq = 0;
		if (!al.entrada(CatLexica.WHILE))
			return false;
		al.reconoce(CatLexica.WHILE);
		principio = etq;
		Exp0(false, tipo, listav, listaf, modo);
		cod.parchea(listav.get(), listaf.get(), etq, etq);
		if (!al.reconoce(CatLexica.DO))
			throw new Exception("Error sintactico");
		cod.emite(new Instruction(Function.IR_F, tmpetq));
		etq++;
		parchetq = etq;
		BloqueI();
		cod.emite(new Instruction(Function.IR_A, principio));
		etq++;
		tmpetq = etq;
		cod.parchea(parchetq, tmpetq);
		if (tipo.get().tipo != Types.BOOLEAN)
			throw new Exception("Error de tipos");
		return true;
	}

	private boolean IFor() throws Exception {
		if (!al.entrada(CatLexica.FOR))
			return false;
		al.reconoce(CatLexica.FOR);
		String lex = al.leeLexema();
		if (!al.reconoce(CatLexica.ID))
			throw new Exception("Error sintactico");
		if (!al.reconoce(CatLexica.IGUAL))
			throw new Exception("Error sintactico");
		AtomicReference<Type> tipo1 = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf1 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo1 = new AtomicReference<Modo>();
		Exp0(false, tipo1, listav1, listaf1, modo1);
		int principio = etq;
		cod.parchea(listav1.get(), listaf1.get(), etq, etq);
		cod.emite(new Instruction(Function.COPIA));
		cod.emite(new Instruction(Function.DESAPILA_DIR, ts.getData(lex).dir));
		etq += 2;
		if (!al.reconoce(CatLexica.TO))
			throw new Exception("Error sintactico");
		AtomicReference<Type> tipo2 = new AtomicReference<Type>();
		AtomicReference<ArrayList<Integer>> listav2 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf2 = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo2 = new AtomicReference<Modo>();
		Exp0(false, tipo2, listav2, listaf2, modo2);
		cod.parchea(listav2.get(), listaf2.get(), etq, etq);
		cod.emite(new Instruction(Function.LESSEQ));
		cod.emite(new Instruction(Function.IR_F));
		etq += 2;
		int parchetq = etq;
		if (!al.reconoce(CatLexica.DO))
			throw new Exception("Error sintactico");
		BloqueI();
		if (!ts.existeId(lex)
				|| (!((tipo1.get().tipo == Types.NATURAL) && (tipo2.get().tipo == Types.NATURAL))
						&& !((tipo1.get().tipo == Types.INTEGER) && (tipo2
								.get().tipo == Types.INTEGER)) && !ts
						.compatibles(ts.getData(lex).tipo, tipo1.get())))
			throw new Exception("Tipos incompatibles");
		cod.emite(new Instruction(Function.APILA_DIR, ts.getData(lex).dir));
		cod.emite(new Instruction(Function.APILA, 1));
		cod.emite(Oper.SUMA);
		cod.emite(new Instruction(Function.IR_A, principio));
		etq += 4;
		int tmpetq = etq;
		cod.parchea(parchetq, tmpetq);
		return true;
	}

	private void BloqueI() throws Exception {
		if (al.entrada(CatLexica.LLAVEIZQ)) {
			al.reconoce(CatLexica.LLAVEIZQ);
			Is();
			if (!al.reconoce(CatLexica.LLAVEDER))
				throw new Exception("Error sintactico");
		} else {
			I();
		}
	}

	private boolean ICall() throws Exception {
		if (!al.entrada(CatLexica.ID))
			return false;
		String lex = al.leeLexema();
		al.reconoce(CatLexica.ID);
		if (!al.reconoce(CatLexica.PAP))
			throw new Exception("Error sintactico");
		ArrayList<Param> paramsh = ((Proc) ts.getData(lex).tipo.elems).params;
		int parchetq = etq;
		etq += Codigo.longApilaDirRetorno;
		int tmpetq = 0;
		cod.apilarDirRetorno(tmpetq);
		Args(paramsh);
		if (!al.reconoce(CatLexica.PCIE))
			throw new Exception("Error sintactico");
		if (!ts.existeId(lex) || (ts.getData(lex).tipo.tipo != Types.PROC))
			throw new Exception("ID desconocido");
		etq++;
		cod.emite(new Instruction(Function.IR_A, ts.getData(lex).init));
		tmpetq = etq;
		cod.parchea(parchetq + 4, tmpetq);
		return true;
	}

	private void Args(ArrayList<Param> paramsh) throws Exception {
		if (!al.entrada(CatLexica.PCIE)) {
			cod.inicioPasoParam();
			etq += Codigo.longInicioPasoParam;
			AtomicReference<Integer> nparams = new AtomicReference<Integer>();
			LArgs(paramsh, nparams);
			if (paramsh.size() != nparams.get())
				throw new Exception("Error parametros");
			cod.finPasoParam();
			etq += Codigo.longFinPasoParam;
		} else { // lambda
			if (paramsh.size() > 0)
				throw new Exception("Error argumentos");
		}
	}

	private boolean LArgs(ArrayList<Param> paramsh,
			AtomicReference<Integer> nparams) throws Exception {
		etq++;
		cod.emite(new Instruction(Function.COPIA));
		AtomicReference<ArrayList<Integer>> listav = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<ArrayList<Integer>> listaf = new AtomicReference<ArrayList<Integer>>();
		AtomicReference<Modo> modo = new AtomicReference<Modo>();
		AtomicReference<Type> tipo = new AtomicReference<Type>();
		Exp0(paramsh.get(0).modo == Modo.VARIABLE, tipo, listav, listaf, modo);
		etq += 1 + Codigo.longPasoParametro;
		cod.emite(new Instruction(Function.FLIP));
		cod.pasoParametro(modo.get(), paramsh.get(0));
		RLArgs(1, paramsh, nparams);
		if ((paramsh.size() == 0)
				|| ((paramsh.get(0).modo == Modo.VARIABLE) && (modo.get() == Modo.VALOR))
				|| (!ts.compatibles(paramsh.get(0).tipo, tipo.get())))
			throw new Exception("Error parámetros");
		return true;
	}

	private void RLArgs(int nparamsh, ArrayList<Param> paramsh,
			AtomicReference<Integer> nparams) throws Exception {
		if (al.entrada(CatLexica.COMA)) {
			al.reconoce(CatLexica.COMA);
			etq++;
			cod.emite(new Instruction(Function.COPIA));
			AtomicReference<ArrayList<Integer>> listav = new AtomicReference<ArrayList<Integer>>();
			AtomicReference<ArrayList<Integer>> listaf = new AtomicReference<ArrayList<Integer>>();
			AtomicReference<Modo> modo = new AtomicReference<Modo>();
			AtomicReference<Type> tipo = new AtomicReference<Type>();
			Exp0(paramsh.get(nparamsh).modo == Modo.VARIABLE, tipo, listav,
					listaf, modo);
			etq += 1 + Codigo.longPasoParametro;
			cod.emite(new Instruction(Function.FLIP));
			cod.pasoParametro(modo.get(), paramsh.get(nparamsh));
			RLArgs(1 + nparamsh, paramsh, nparams);
		} else { // lambda
			nparams.set(nparamsh);
		}
	}

	private boolean INew() throws Exception {
		AtomicReference<Type> tipo = new AtomicReference<Type>();
		if (!al.entrada(CatLexica.NEW))
			return false;
		al.reconoce(CatLexica.NEW);
		Desig(tipo);
		int tam = 0;
		if (tipo.get().tipo == Types.REF) {
			tam = ts.getData(((Ref) tipo.get().elems).id).tipo.tam;
		} else
			tam = ((Punt) tipo.get().elems).tBase.tam;
		cod.emite(new Instruction(Function.NEW, tam));
		cod.emite(new Instruction(Function.DESAPILA_IND));
		etq += 2;
		if (tipo.get().tipo != Types.POINTER)
			throw new Exception("Error de tipos");
		return true;
	}

	private boolean IDelete() throws Exception {
		AtomicReference<Type> tipo = new AtomicReference<Type>();
		if (!al.entrada(CatLexica.DISPOSE))
			return false;
		al.reconoce(CatLexica.DISPOSE);
		Desig(tipo);
		if (tipo.get().tipo != Types.POINTER)
			throw new Exception("No es un puntero");
		int tam = 0;
		if (tipo.get().tipo == Types.REF)
			tam = ts.getData(((Ref) tipo.get().elems).id).tipo.tam;
		else
			tam = ((Punt) tipo.get().elems).tBase.tam;
		cod.emite(new Instruction(Function.DISPOSE, tam));
		etq++;
		return true;
	}

	private boolean OP0(AtomicReference<Oper> op) throws Exception {
		if (al.entrada(CatLexica.MENOR)) {
			al.reconoce(CatLexica.MENOR);
			op.set(Oper.MENOR);
			return true;
		} else if (al.entrada(CatLexica.MAYOR)) {
			al.reconoce(CatLexica.MAYOR);
			op.set(Oper.MAYOR);
			return true;
		} else if (al.entrada(CatLexica.MENOROIGUAL)) {
			al.reconoce(CatLexica.MENOROIGUAL);
			op.set(Oper.MENORIG);
			return true;
		} else if (al.entrada(CatLexica.MAYOROIGUAL)) {
			al.reconoce(CatLexica.MAYOROIGUAL);
			op.set(Oper.MAYORIG);
			return true;
		} else if (al.entrada(CatLexica.DISTINTO)) {
			al.reconoce(CatLexica.DISTINTO);
			op.set(Oper.DISTINTO);
			return true;
		} else if (al.entrada(CatLexica.IGUAL)) {
			al.reconoce(CatLexica.IGUAL);
			op.set(Oper.IGUAL);
			return true;
		} else
			return false;
	}

	private boolean OP1(AtomicReference<Oper> op) throws Exception {
		if (al.entrada(CatLexica.MAS)) {
			al.reconoce(CatLexica.MAS);
			op.set(Oper.SUMA);
			return true;
		} else if (al.entrada(CatLexica.MENOS)) {
			al.reconoce(CatLexica.MENOS);
			op.set(Oper.RESTA);
			return true;
		} else
			return false;
	}

	private boolean OP2(AtomicReference<Oper> op) throws Exception {
		if (al.entrada(CatLexica.DIV)) {
			al.reconoce(CatLexica.DIV);
			op.set(Oper.DIV);
			return true;
		} else if (al.entrada(CatLexica.MULT)) {
			al.reconoce(CatLexica.MULT);
			op.set(Oper.MULT);
			return true;
		} else if (al.entrada(CatLexica.MOD)) {
			al.reconoce(CatLexica.MOD);
			op.set(Oper.MOD);
			return true;
		} else
			return false;
	}

	private boolean OP3(AtomicReference<Oper> op) throws Exception {
		if (al.entrada(CatLexica.DESPDER)) {
			al.reconoce(CatLexica.DESPDER);
			op.set(Oper.DESPDER);
			return true;
		} else if (al.entrada(CatLexica.DESPIZQ)) {
			al.reconoce(CatLexica.DESPIZQ);
			op.set(Oper.DESPIZQ);
			return true;
		} else
			return false;
	}

	private boolean OP4A(AtomicReference<Oper> op) throws Exception {
		if (al.entrada(CatLexica.NOT)) {
			al.reconoce(CatLexica.NOT);
			op.set(Oper.NOT);
			return true;
		} else if (al.entrada(CatLexica.MENOS)) {
			al.reconoce(CatLexica.MENOS);
			op.set(Oper.INV);
			return true;
		} else
			return false;
	}

	private boolean OP4NA(AtomicReference<Oper> op) throws Exception {
		if (al.entrada(CatLexica.CFLOAT)) {
			al.reconoce(CatLexica.CFLOAT);
			op.set(Oper.CFLOAT);
			return true;
		} else if (al.entrada(CatLexica.CNAT)) {
			al.reconoce(CatLexica.CNAT);
			op.set(Oper.CNAT);
			return true;
		} else if (al.entrada(CatLexica.CINT)) {
			al.reconoce(CatLexica.CINT);
			op.set(Oper.CINT);
			return true;
		} else if (al.entrada(CatLexica.CCHAR)) {
			al.reconoce(CatLexica.CCHAR);
			op.set(Oper.CCHAR);
			return true;
		} else
			return false;
	}

	// METODOS PUBLICOS
	public void process() throws Exception {
		Prog();
	}

}
