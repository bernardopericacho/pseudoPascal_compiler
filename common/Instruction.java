package common;

import java.io.Serializable;

public class Instruction implements Serializable {

	// ATRIBUTOS
	private static final long serialVersionUID = 1L;
	Function func;
	Object arg;

	// CONSTRUCTORAS
	public Instruction(Function func) {
		this.func = func;
		this.arg = null;
	}

	public Instruction(Function func, Object arg) {
		this.func = func;
		this.arg = arg;
	}

	// METODOS PUBLICOS
	public void exec() throws Exception {
		func.exec(arg);
	}

	public String toString() {
		if (arg != null)
			return func.toString() + " " + arg + "\n";
		else
			return func.toString() + "\n";
	}

	public void setArg(Object o) {
		this.arg = o;
	}

}