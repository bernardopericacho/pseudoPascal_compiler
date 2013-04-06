package Parser;

public class Nodo {

	private Type tipo1;
	private Type tipo2;
	public Nodo(Type tipo11, Type tipo22) {
		tipo1=tipo11;
		tipo2=tipo22;
	}
	public void setTipo1(Type tipo1) {
		this.tipo1 = tipo1;
	}
	public Type getTipo1() {
		return tipo1;
	}
	public void setTipo2(Type tipo2) {
		this.tipo2 = tipo2;
	}
	public Type getTipo2() {
		return tipo2;
	}
	
}
