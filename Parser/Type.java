package Parser;

import java.util.ArrayList;
import java.util.Iterator;

public class Type {

	// ATRIBUTOS
	public Types tipo;
	public Elems elems;
	public int tam;

	// CONSTRUCTORAS
	public Type(Types t, int ta) {
		tipo = t;
		switch (t) {
		case ARRAY:
			elems = new Array();
			break;
		case PROC:
			elems = new Proc();
			break;
		case POINTER:
			elems = new Punt();
			break;
		case RECORD:
			elems = new Reg();
			break;
		case REF:
			elems = new Ref();
			break;
		default:
			elems = null;
		}
		tam = ta;
	}

	private void commonConstructor(Types t, int ta) {
		tipo = t;
		tam = ta;
	}

	public Type(Types t, int ta, String i) {
		this.commonConstructor(t, ta);
		elems = new Ref();
		((Ref) elems).id = i;
	}

	public Type(Types t, int ta, int n, Type tb) {
		this.commonConstructor(t, ta);
		elems = new Array();
		((Array) elems).nElems = n;
		((Array) elems).tBase = tb;
	}

	public Type(Types t, int ta, Type tb) {
		this.commonConstructor(t, ta);
		elems = new Punt();
		((Punt) elems).tBase = tb;
	}

	public Type(Types t, int ta, ArrayList<Campo> c) {
		this.commonConstructor(t, ta);
		elems = new Reg();
		((Reg) elems).campos = c;
	}

	public Type(Types t, ArrayList<Param> p) {
		this.commonConstructor(t, 0);
		elems = new Proc();
		((Proc) elems).params = p;
	}

	// CLASES PRIVADAS
	public class Ref extends Elems {
		public String id;
	}

	public class Array extends Elems {
		public int nElems;
		public Type tBase;
	}

	public class Punt extends Elems {
		public Type tBase;
	}

	public class Reg extends Elems {
		public ArrayList<Campo> campos;
	
	
		public Campo getCampo(String id){
			Campo c = null;
			boolean encontrado=false;
			Iterator<Campo> it = campos.iterator();
			while (it.hasNext()&& !encontrado){
				c = it.next();
				if (c.id.compareTo(id)==0) encontrado=true;
			}
			return c;
		}
	}

	public class Proc extends Elems {
		public ArrayList<Param> params;
	}

}
