package Parser;

import java.util.ArrayList;
import java.util.Iterator;

import Parser.Type.Array;
import Parser.Type.Proc;
import Parser.Type.Punt;
import Parser.Type.Ref;
import Parser.Type.Reg;

public class SymbolTable {

	// ATRIBUTOS
	private ArrayList<Table> tablas;
	private ArrayList<String> tipos;
	private ArrayList<String> procedimientos;

	// CONSTRUCTORA
	public SymbolTable() {
		tablas = new ArrayList<Table>();
		tipos = new ArrayList<String>();
		procedimientos = new ArrayList<String>();
		creaTSHija();
	}

	// METODOS PRIVADOS

	// METODOS PUBLICOS
	public Data getData(String id) {
		Data tmpData;
		for (int i = tablas.size() - 1; i >= 0; i--) {
			tmpData = tablas.get(i).tabla.get(id);
			if (tmpData != null)
				return tmpData;
		}
		return null;
	}

	public boolean existeId(String id) {
		Table tmpTable;
		for (int i = tablas.size() - 1; i >= 0; i--) {
			tmpTable = tablas.get(i);
			if (tmpTable.tabla.containsKey(id))
				return true;
		}
		return false;
	}

	public void ponId(String id, Props props) {
		Table tabla = tablas.get(props.nivel);
		tabla.tabla.put(id, new Data(props.tipo, props.nivel, props.clase,
				tablas.size() - 1, props.init));
	}

	public void addProcPend(String id) {
		procedimientos.add(id);
	}

	public void remProcPend(String id) {
		Iterator<String> itStr = procedimientos.iterator();
		String tmpStr;
		while (itStr.hasNext()) {
			tmpStr = itStr.next();
			if (tmpStr.compareTo(id) == 0) {
				procedimientos.remove(tmpStr);
				return;
			}
		}
	}

	public void addTipoPend(String id) {
		tipos.add(id);
	}

	public void remTipoPend(String id) {
		Iterator<String> itStr = tipos.iterator();
		String tmpStr;
		while (itStr.hasNext()) {
			tmpStr = itStr.next();
			if (tmpStr.compareTo(id) == 0) {
				tipos.remove(tmpStr);
				return;
			}
		}
	}

	public boolean esPend(String id) {
		return tipos.contains(id);
	}

	public boolean hayPend() {
		return !(tipos.isEmpty() && procedimientos.isEmpty());
	}

	public boolean refErronea(String id) {

		Table tmpTable;
		for (int i = tablas.size() - 1; i >= 0; i--) {
			tmpTable = tablas.get(i);
			if (tmpTable.tabla.containsKey(id))
				if (tmpTable.tabla.get(id).tipo.tipo == Types.REF)
					return true;
		}
		return false;
		/*
		 * 
		 * devuelve true si existe el tipo en la tabla de simbolos y es un tipo
		 * puntero
		 */
	}

	public boolean esDuplicado(ArrayList<Campo> campos, String id) {
		Iterator<Campo> itCmp = campos.iterator();
		Campo c;
		while (itCmp.hasNext()) {
			c = itCmp.next();
			if (c.id.compareTo(id) == 0) {
				return true;
			}
		}
		return false;
	}

	public void creaTSHija() {
		Table c = new Table();
		tablas.add(c);
	}
	public void quitaTSHija() {
		
		tablas.remove(tablas.size()-1);
	}

	public boolean compatibles(Type tipo1, Type tipo2) {
		ArrayList<Nodo> visitados= new ArrayList<Nodo>();
		
		return compatibles2(tipo1,tipo2,visitados);
		
	}
	
	private boolean compatibles2(Type tipo1,Type tipo2,ArrayList<Nodo> visitados){
		Iterator<Nodo> it = visitados.iterator();
		boolean encontrado=false;
		while (it.hasNext()&& ! encontrado){
			Nodo n=it.next();
			if((tipo1.tipo.equals(n.getTipo1().tipo))&&(tipo2.tipo.equals(n.getTipo2().tipo))){
				encontrado=true;
			}
		}
		if (encontrado) return true;
		else {
			visitados.add(new Nodo(tipo1,tipo2));
		}
		
		if ((tipo1.tipo==Types.NATURAL && tipo2.tipo==Types.NATURAL) || (tipo1.tipo==Types.INTEGER && tipo2.tipo==Types.INTEGER) || 
		(tipo1.tipo==Types.FLOAT && tipo2.tipo==Types.FLOAT)|| (tipo1.tipo==Types.BOOLEAN && tipo2.tipo==Types.BOOLEAN)|| 
		(tipo1.tipo==Types.CHARACTER && tipo2.tipo==Types.CHARACTER) || (tipo1.tipo==Types.INTEGER && tipo2.tipo==Types.NATURAL)||
		(tipo1.tipo==Types.FLOAT && tipo2.tipo==Types.NATURAL)|| (tipo1.tipo==Types.FLOAT && tipo2.tipo==Types.INTEGER)||
		(tipo1.tipo==Types.POINTER && tipo2.tipo==Types.POINTER && ((Punt)tipo2.elems).tBase.tipo==Types.ERR)){
			
			return true;}
		
		else if (tipo1.tipo==Types.REF){
			Type ty = this.getData(((Ref)tipo1.elems).id).tipo;
			return compatibles2(this.getData(((Ref)tipo1.elems).id).tipo,tipo2,visitados);
		}
		else if (tipo2.tipo==Types.REF){
			return compatibles2(tipo1,this.getData(((Ref)tipo2.elems).id).tipo,visitados);
		}
		else if (tipo1.tipo==Types.ARRAY && tipo2.tipo==Types.ARRAY && ((Array)tipo1.elems).nElems==((Array)tipo2.elems).nElems){
			return compatibles2(((Array)tipo1.elems).tBase,((Array)tipo2.elems).tBase,visitados);
		}
		else if (tipo1.tipo==Types.RECORD && tipo2.tipo==Types.RECORD && ((Reg)tipo1.elems).campos.size()==((Reg)tipo2.elems).campos.size()){
			int n=((Reg)tipo1.elems).campos.size();
			for(int i=0;i<n;i++){
				if (!compatibles2(((Reg)tipo1.elems).campos.get(i).tipo,((Reg)tipo1.elems).campos.get(i).tipo,visitados)){
					return false;
				}
			}
			return true;
		}
		else if (tipo1.tipo==Types.POINTER && tipo2.tipo==Types.POINTER ){
			return compatibles2(((Punt)tipo1.elems).tBase,((Punt)tipo2.elems).tBase,visitados);
		}
		else if (tipo1.tipo==Types.PROC && tipo2.tipo==Types.PROC && ((Proc)tipo1.elems).params.size()== ((Proc)tipo2.elems).params.size()){
			int n=((Proc)tipo1.elems).params.size();
			for(int i=0;i<n;i++){
				if (!compatibles2(((Proc)tipo1.elems).params.get(i).tipo,((Proc)tipo1.elems).params.get(i).tipo,visitados) || 
						(((Proc)tipo1.elems).params.get(i).modo==Modo.VARIABLE && ((Proc)tipo2.elems).params.get(i).modo!=Modo.VARIABLE	)){
					return false;
				}
			}
			return true;
		}
		
		return false;
	
	}

	public boolean compatibleTipoBasico(Type type) {
		return compatibles(type, new Type(Types.BOOLEAN, 1)) || compatibles(type, new Type(Types.CHARACTER, 1))
			|| compatibles(type, new Type(Types.INTEGER, 1)) || compatibles(type, new Type(Types.NATURAL, 1))
			|| compatibles(type, new Type(Types.FLOAT, 1));
	}

	public Type sigueRef(Type tipo) throws Exception {
		if (tipo.tipo==Types.REF){
			String id=((Ref)tipo.elems).id;
			if(existeId(id)){
				return sigueRef(getData(id).tipo);
			}
			else
				throw new Exception("Referencia Erronea");
		}
		return tipo;
		
	}
	
}
