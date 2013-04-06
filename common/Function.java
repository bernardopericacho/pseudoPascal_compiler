package common;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.lang.Math;

import vm.VirtualMachine;

public enum Function implements Serializable {

	// FUNCIONES MAQUINA PILA
	APILA_DIR {
		public void exec(Object arg) throws Exception {
			VirtualMachine.getInstance().load((Integer) arg);
		}

		public String toString() {
			return "APILA_DIR";
		}
	},
	IN {
		public void exec(Object arg) {
			String s = "";
			try {
				Reader input = new InputStreamReader(System.in);
				// VirtualMachine.getInstance().push(input.read());

				char[] cbuf = new char[100];
				int tam = input.read(cbuf);
				char[] cbuf2 = new char[tam];
				for (int i = 0; i < tam - 2; i++) {
					s += cbuf[i];
					cbuf2[i] = cbuf[i];
					System.out.print(cbuf[i]);
				}
			} catch (Exception e) {
			}
			boolean parseado = false;
			try {
				System.out.println(s.length() + "   " + s);
				int i = Integer.parseInt(s);
				// Integer i = Integer.parseInt(s);
				parseado = true;
				VirtualMachine.getInstance().push(i);
			} catch (NumberFormatException e) {
			}
			if (!parseado)
				try {
					Float f = Float.parseFloat(s);
					parseado = true;
					VirtualMachine.getInstance().push(f);
				} catch (NumberFormatException e) {
				}
			if (!parseado)
				try {
					Boolean b = Boolean.parseBoolean(s);
					VirtualMachine.getInstance().push(b);
					/*
					 * if(s.equalsIgnoreCase("true") ||
					 * s.equalsIgnoreCase("false")) { parseado = true;
					 * VirtualMachine.getInstance().push(new Boolean(s)); }
					 */
					/*
					 * if(s.startsWith("true")) {
					 * //if(s.equalsIgnoreCase("true")) {
					 * VirtualMachine.getInstance().push(true); }
					 * if(s.startsWith("false")) {
					 * //if(s.equalsIgnoreCase("false")) {
					 * VirtualMachine.getInstance().push(false); }
					 */
				} catch (NumberFormatException e) {
				}

			// VirtualMachine.getInstance().store((Integer) arg,
			// cbuf2.toString());

		}

		public String toString() {
			return "IN";
		}
	},
	OUT {
		public void exec(Object arg) {
			System.out.println(VirtualMachine.getInstance().pop().toString());
		}

		public String toString() {
			return "OUT";
		}
	},
	DESAPILA {
		public void exec(Object arg) {
			VirtualMachine.getInstance().pop();
		}

		public String toString() {
			return "DESAPILA";
		}
	},
	APILA {
		public void exec(Object arg) {
			VirtualMachine.getInstance().push(arg);
		}

		public String toString() {
			return "APILA";
		}
	},
	DESAPILA_DIR {
		public void exec(Object arg) throws Exception {
			Object o = VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().store((Integer) arg, o);
		}

		public String toString() {
			return "DESAPILA_DIR";
		}
	},
	// OPERADORES
	/*
	 * 0 -> BOOL 1 -> CHAR 2 -> NAT 3 -> INT 4 -> FLOAT
	 */
	GREATER { // A > B
		public void exec(Object arg) {
			Object b = VirtualMachine.getInstance().pop();
			Object a = VirtualMachine.getInstance().pop();
			boolean r = false;
			if (a instanceof Integer && b instanceof Integer)
				r = (Integer) a > (Integer) b;
			else if (a instanceof Float && b instanceof Float)
				r = (Float) a > (Float) b;
			else if (a instanceof Float && b instanceof Integer)
				r = (Float) a > new Float((Integer) b);
			else if (a instanceof Integer && b instanceof Float)
				r = new Float((Integer) a) > (Float) b;
			else if (a instanceof Boolean && b instanceof Boolean)
				r = (Boolean) a && !(Boolean) b;
			else if (a instanceof Character && b instanceof Character)
				r = (Character) a > (Character) b;
			VirtualMachine.getInstance().push(r);
			/*
			 * Integer tipo = (Integer) arg; switch (tipo) { case 0: Boolean b0
			 * = (Boolean) VirtualMachine.getInstance().pop(); Boolean a0 =
			 * (Boolean) VirtualMachine.getInstance().pop(); Boolean res = a0 &&
			 * !b0; VirtualMachine.getInstance().push(res); break; case 1:
			 * Character b1 = (Character) VirtualMachine.getInstance().pop();
			 * Character a1 = (Character) VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a1 > b1)); break;
			 * case 2: Integer b2 = (Integer)
			 * VirtualMachine.getInstance().pop(); Integer a2 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a2 > b2)); break;
			 * case 3: Integer b3 = (Integer)
			 * VirtualMachine.getInstance().pop(); Integer a3 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a3 > b3)); break;
			 * case 4: Float b4 = (Float) VirtualMachine.getInstance().pop();
			 * Float a4 = (Float) VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a4 > b4)); break; }
			 */
		}

		public String toString() {
			return "GREATER";
		}
	},
	GREATEREQ { // A >= B
		public void exec(Object arg) {
			Object b = VirtualMachine.getInstance().pop();
			Object a = VirtualMachine.getInstance().pop();
			boolean r = false;
			if (a instanceof Integer && b instanceof Integer)
				r = (Integer) a >= (Integer) b;
			else if (a instanceof Float && b instanceof Float)
				r = (Float) a >= (Float) b;
			else if (a instanceof Float && b instanceof Integer)
				r = (Float) a >= new Float((Integer) b);
			else if (a instanceof Integer && b instanceof Float)
				r = new Float((Integer) a) >= (Float) b;
			else if (a instanceof Boolean && b instanceof Boolean)
				r = (Boolean) a || !(Boolean) b;
			else if (a instanceof Character && b instanceof Character)
				r = (Character) a >= (Character) b;
			VirtualMachine.getInstance().push(r);

			/*
			 * Integer tipo = (Integer) arg; switch (tipo) { case 0:
			 * VirtualMachine.getInstance().pop(); // Quitar b, no se usa
			 * Boolean a0 = (Boolean) VirtualMachine.getInstance().pop();
			 * Boolean res = a0; VirtualMachine.getInstance().push(res); break;
			 * case 1: Character b1 = (Character)
			 * VirtualMachine.getInstance().pop(); Character a1 = (Character)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a1 >= b1)); break;
			 * case 2: Integer b2 = (Integer)
			 * VirtualMachine.getInstance().pop(); Integer a2 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a2 >= b2)); break;
			 * case 3: Integer b3 = (Integer)
			 * VirtualMachine.getInstance().pop(); Integer a3 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a3 >= b3)); break;
			 * case 4: Float b4 = (Float) VirtualMachine.getInstance().pop();
			 * Float a4 = (Float) VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a4 >= b4)); break; }
			 */
		}

		public String toString() {
			return "GREATEREQ";
		}
	},
	LESS { // A < B
		public void exec(Object arg) {
			Object b = VirtualMachine.getInstance().pop();
			Object a = VirtualMachine.getInstance().pop();
			boolean r = false;
			if (a instanceof Integer && b instanceof Integer)
				r = (Integer) a < (Integer) b;
			else if (a instanceof Float && b instanceof Float)
				r = (Float) a < (Float) b;
			else if (a instanceof Float && b instanceof Integer)
				r = (Float) a < new Float((Integer) b);
			else if (a instanceof Integer && b instanceof Float)
				r = new Float((Integer) a) < (Float) b;
			else if (a instanceof Boolean && b instanceof Boolean)
				r = !(Boolean) a && (Boolean) b;
			else if (a instanceof Character && b instanceof Character)
				r = (Character) a < (Character) b;
			VirtualMachine.getInstance().push(r);

			/*
			 * Integer tipo = (Integer) arg; switch (tipo) { case 0: Boolean b0
			 * = (Boolean) VirtualMachine.getInstance().pop(); Boolean a0 =
			 * (Boolean) VirtualMachine.getInstance().pop(); Boolean res = !a0
			 * && b0; VirtualMachine.getInstance().push(res); break; case 1:
			 * Character b1 = (Character) VirtualMachine.getInstance().pop();
			 * Character a1 = (Character) VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a1 < b1)); break;
			 * case 2: Integer b2 = (Integer)
			 * VirtualMachine.getInstance().pop(); Integer a2 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a2 < b2)); break;
			 * case 3: Integer b3 = (Integer)
			 * VirtualMachine.getInstance().pop(); Integer a3 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a3 < b3)); break;
			 * case 4: Float b4 = (Float) VirtualMachine.getInstance().pop();
			 * Float a4 = (Float) VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a4 < b4)); break; }
			 */
		}

		public String toString() {
			return "LESS";
		}
	},
	LESSEQ { // A <= B
		public void exec(Object arg) {
			Object b = VirtualMachine.getInstance().pop();
			Object a = VirtualMachine.getInstance().pop();
			boolean r = false;
			if (a instanceof Integer && b instanceof Integer)
				r = (Integer) a <= (Integer) b;
			else if (a instanceof Float && b instanceof Float)
				r = (Float) a <= (Float) b;
			else if (a instanceof Float && b instanceof Integer)
				r = (Float) a <= new Float((Integer) b);
			else if (a instanceof Integer && b instanceof Float)
				r = new Float((Integer) a) <= (Float) b;
			else if (a instanceof Boolean && b instanceof Boolean)
				r = !(Boolean) a || (Boolean) b;
			else if (a instanceof Character && b instanceof Character)
				r = (Character) a <= (Character) b;
			VirtualMachine.getInstance().push(r);
			/*
			 * Integer tipo = (Integer) arg; switch (tipo) { case 0: Boolean b0
			 * = (Boolean) VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().pop(); // Quitar a, no se usa
			 * Boolean res = b0; VirtualMachine.getInstance().push(res); break;
			 * case 1: Character b1 = (Character)
			 * VirtualMachine.getInstance().pop(); Character a1 = (Character)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a1 <= b1)); break;
			 * case 2: Integer b2 = (Integer)
			 * VirtualMachine.getInstance().pop(); Integer a2 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a2 <= b2)); break;
			 * case 3: Integer b3 = (Integer)
			 * VirtualMachine.getInstance().pop(); Integer a3 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a3 <= b3)); break;
			 * case 4: Float b4 = (Float) VirtualMachine.getInstance().pop();
			 * Float a4 = (Float) VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a4 <= b4)); break; }
			 */
		}

		public String toString() {
			return "LESSEQ";
		}
	},
	EQUAL { // A == B
		public void exec(Object arg) {
			Object b = VirtualMachine.getInstance().pop();
			Object a = VirtualMachine.getInstance().pop();
			boolean r = false;
			if (a.getClass().equals(b.getClass()))
				r = a.equals(b);
			// if((a instanceof Integer && b instanceof Integer) || (a
			// instanceof Float && b instanceof Integer))
			// r = a.equals(b);
			else if (a instanceof Float && b instanceof Integer)
				r = ((Float) a).compareTo(new Float((Integer) b)) == 0;
			else if (a instanceof Integer && b instanceof Float)
				r = ((Float) b).compareTo(new Float((Integer) a)) == 0;
			VirtualMachine.getInstance().push(r);
			/*
			 * Object b = VirtualMachine.getInstance().pop(); Object a =
			 * VirtualMachine.getInstance().pop(); if(a instanceof )
			 * VirtualMachine.getInstance().push(a.equals(b));
			 */
			/*
			 * Integer tipo = (Integer) arg; switch (tipo) { case 0: Boolean b0
			 * = (Boolean) VirtualMachine.getInstance().pop(); Boolean a0 =
			 * (Boolean) VirtualMachine.getInstance().pop(); Boolean res = !(a0
			 * ^ b0); VirtualMachine.getInstance().push(res); break; case 1:
			 * Character b1 = (Character) VirtualMachine.getInstance().pop();
			 * Character a1 = (Character) VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a1 == b1)); break;
			 * case 2: Integer b2 = (Integer)
			 * VirtualMachine.getInstance().pop(); Integer a2 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a2 == b2)); break;
			 * case 3: Integer b3 = (Integer)
			 * VirtualMachine.getInstance().pop(); Integer a3 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a3 == b3)); break;
			 * case 4: Float b4 = (Float) VirtualMachine.getInstance().pop();
			 * Float a4 = (Float) VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a4 == b4)); break; }
			 */
		}

		public String toString() {
			return "EQUAL";
		}
	},
	NEQUAL { // A != B
		public void exec(Object arg) {
			Object b = VirtualMachine.getInstance().pop();
			Object a = VirtualMachine.getInstance().pop();
			boolean r = false;
			if (a.getClass().equals(b.getClass()))
				r = a.equals(b);
			// if((a instanceof Integer && b instanceof Integer) || (a
			// instanceof Float && b instanceof Integer))
			// r = a.equals(b);
			else if (a instanceof Float && b instanceof Integer)
				r = ((Float) a).compareTo(new Float((Integer) b)) != 0;
			else if (a instanceof Integer && b instanceof Float)
				r = ((Float) b).compareTo(new Float((Integer) a)) != 0;
			VirtualMachine.getInstance().push(r);
			/*
			 * Object b = VirtualMachine.getInstance().pop(); Object a =
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push(!a.equals(b));
			 */
			/*
			 * Integer tipo = (Integer) arg; switch (tipo) { case 0: Boolean b0
			 * = (Boolean) VirtualMachine.getInstance().pop(); Boolean a0 =
			 * (Boolean) VirtualMachine.getInstance().pop(); Boolean res = a0 ^
			 * b0; VirtualMachine.getInstance().push(res); break; case 1:
			 * Character b1 = (Character) VirtualMachine.getInstance().pop();
			 * Character a1 = (Character) VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a1 != b1)); break;
			 * case 2: Integer b2 = (Integer)
			 * VirtualMachine.getInstance().pop(); Integer a2 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a2 != b2)); break;
			 * case 3: Integer b3 = (Integer)
			 * VirtualMachine.getInstance().pop(); Integer a3 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a3 != b3)); break;
			 * case 4: Float b4 = (Float) VirtualMachine.getInstance().pop();
			 * Float a4 = (Float) VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Boolean) (a4 != b4)); break; }
			 */
		}

		public String toString() {
			return "NEQUAL";
		}
	},
	ADD {
		public void exec(Object arg) {
			Object b = VirtualMachine.getInstance().pop();
			Object a = VirtualMachine.getInstance().pop();
			if (a instanceof Integer && b instanceof Integer)
				VirtualMachine.getInstance().push((Integer) a + (Integer) b);
			else if (a instanceof Float && b instanceof Float)
				VirtualMachine.getInstance().push((Float) a + (Float) b);
			else if (a instanceof Float && b instanceof Integer)
				VirtualMachine.getInstance().push(
						(Float) a + new Float((Integer) (b)));
			else if (a instanceof Integer && b instanceof Float)
				VirtualMachine.getInstance().push(
						new Float((Integer) (a)) + (Float) b);
		}

		public String toString() {
			return "ADD";
		}
	},
	ADDI {
		public void exec(Object arg) {
			Integer b = (Integer) VirtualMachine.getInstance().pop();
			Integer a = (Integer) VirtualMachine.getInstance().pop();
			Integer result1;
			result1 = a + b;
			VirtualMachine.getInstance().push((Integer) result1);
		}

		public String toString() {
			return "ADDI";
		}
	},
	ADDF {
		public void exec(Object arg) {
			Float b = (Float) VirtualMachine.getInstance().pop();
			Float a = (Float) VirtualMachine.getInstance().pop();
			Float result2;
			result2 = a + b;
			VirtualMachine.getInstance().push((Float) result2);
		}

		public String toString() {
			return "ADDF";
		}
	},
	SUB {
		public void exec(Object arg) {
			Object b = VirtualMachine.getInstance().pop();
			Object a = VirtualMachine.getInstance().pop();
			if (a instanceof Integer && b instanceof Integer)
				VirtualMachine.getInstance().push((Integer) a - (Integer) b);
			else if (a instanceof Float && b instanceof Float)
				VirtualMachine.getInstance().push((Float) a - (Float) b);
			else if (a instanceof Float && b instanceof Integer)
				VirtualMachine.getInstance().push(
						(Float) a - new Float((Integer) (b)));
			else if (a instanceof Integer && b instanceof Float)
				VirtualMachine.getInstance().push(
						new Float((Integer) (a)) - (Float) b);
		}

		public String toString() {
			return "SUB";
		}
	},
	SUBI {
		public void exec(Object arg) {
			Integer b = (Integer) VirtualMachine.getInstance().pop();
			Integer a = (Integer) VirtualMachine.getInstance().pop();
			Integer result1;
			result1 = a - b;
			VirtualMachine.getInstance().push((Integer) result1);
		}

		public String toString() {
			return "SUBI";
		}
	},
	SUBF {
		public void exec(Object arg) {
			Float b = (Float) VirtualMachine.getInstance().pop();
			Float a = (Float) VirtualMachine.getInstance().pop();
			Float result2;
			result2 = a - b;
			VirtualMachine.getInstance().push((Float) result2);
		}

		public String toString() {
			return "SUBF";
		}
	},
	OR {
		public void exec(Object arg) {
			Boolean B = (Boolean) VirtualMachine.getInstance().pop();
			Boolean A = (Boolean) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().push(A || B);
		}

		public String toString() {
			return "OR";
		}
	},
	MULT {
		public void exec(Object arg) {
			Object b = VirtualMachine.getInstance().pop();
			Object a = VirtualMachine.getInstance().pop();
			if (a instanceof Integer && b instanceof Integer)
				VirtualMachine.getInstance().push((Integer) a * (Integer) b);
			else if (a instanceof Float && b instanceof Float)
				VirtualMachine.getInstance().push((Float) a * (Float) b);
			else if (a instanceof Float && b instanceof Integer)
				VirtualMachine.getInstance().push(
						(Float) a * new Float((Integer) (b)));
			else if (a instanceof Integer && b instanceof Float)
				VirtualMachine.getInstance().push(
						new Float((Integer) (a)) * (Float) b);
		}

		public String toString() {
			return "MULT";
		}
	},
	MULTI { // A * B
		public void exec(Object arg) {
			Integer b = (Integer) VirtualMachine.getInstance().pop();
			Integer a = (Integer) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().push((Integer) (a * b));
		}

		public String toString() {
			return "MULTI";
		}
	},
	MULTF { // A * B
		public void exec(Object arg) {
			Float b = (Float) VirtualMachine.getInstance().pop();
			Float a = (Float) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().push((Float) (a * b));
		}

		public String toString() {
			return "MULTF";
		}
	},
	DIV {
		public void exec(Object arg) {
			Object b = VirtualMachine.getInstance().pop();
			Object a = VirtualMachine.getInstance().pop();
			if (a instanceof Integer && b instanceof Integer)
				VirtualMachine.getInstance().push((Integer) a / (Integer) b);
			else if (a instanceof Float && b instanceof Float)
				VirtualMachine.getInstance().push((Float) a / (Float) b);
			else if (a instanceof Float && b instanceof Integer)
				VirtualMachine.getInstance().push(
						(Float) a / new Float((Integer) (b)));
			else if (a instanceof Integer && b instanceof Float)
				VirtualMachine.getInstance().push(
						new Float((Integer) (a)) / (Float) b);
		}

		public String toString() {
			return "DIV";
		}
	},
	DIVI { // A / B
		public void exec(Object arg) {
			Integer b = (Integer) VirtualMachine.getInstance().pop();
			Integer a = (Integer) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().push((Integer) (a / b));
		}

		public String toString() {
			return "DIVI";
		}
	},
	DIVF { // A / B
		public void exec(Object arg) {
			Float b = (Float) VirtualMachine.getInstance().pop();
			Float a = (Float) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().push((Float) (a / b));
		}

		public String toString() {
			return "DIVF";
		}
	},
	MOD {
		public void exec(Object arg) {
			Integer b = (Integer) VirtualMachine.getInstance().pop();
			Integer a = (Integer) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().push((Integer) (a % b));
		}

		public String toString() {
			return "MOD";
		}
	},
	AND {
		public void exec(Object arg) {
			Boolean B = (Boolean) VirtualMachine.getInstance().pop();
			Boolean A = (Boolean) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().push(A && B);
		}

		public String toString() {
			return "AND";
		}
	},
	SHIFTR {
		public void exec(Object arg) {
			Integer b = (Integer) VirtualMachine.getInstance().pop();
			Integer a = (Integer) VirtualMachine.getInstance().pop();
			for (int i = 0; i < b; i++) {
				a = a / 2;
			}
			VirtualMachine.getInstance().push(a);
		}

		public String toString() {
			return "SHIFTR";
		}
	},
	SHIFTL {
		public void exec(Object arg) {
			Integer b = (Integer) VirtualMachine.getInstance().pop();
			Integer a = (Integer) VirtualMachine.getInstance().pop();
			for (int i = 0; i < b; i++) {
				a = a * 2;
			}
			VirtualMachine.getInstance().push(a);
		}

		public String toString() {
			return "SHIFTL";
		}
	},
	NOT {
		public void exec(Object arg) {
			Boolean a = (Boolean) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().push(!a);
		}

		public String toString() {
			return "NOT";
		}
	},
	CFLOAT {
		public void exec(Object arg) {
			Object o = VirtualMachine.getInstance().pop();
			if (o instanceof Integer)
				VirtualMachine.getInstance().push(new Float((Integer) o));
			else if (o instanceof Float)
				VirtualMachine.getInstance().push((Float) o);
			// else if(o instanceof Character)
			// VirtualMachine.getInstance().push(((Character) o).);
			else if (o instanceof Character)
				// VirtualMachine.getInstance().push(Character.getNumericValue(((Character)o).charValue()));
				VirtualMachine.getInstance().push(
						new Float(String.valueOf(((Character) o).charValue())
								.codePointAt(0)));
		}

		public String toString() {
			return "CFLOAT";
		}
	},
	CINT {
		public void exec(Object arg) {
			Object o = VirtualMachine.getInstance().pop();
			if (o instanceof Integer)
				VirtualMachine.getInstance().push((Integer) o);
			else if (o instanceof Float)
				VirtualMachine.getInstance().push(((Float) o).intValue());
			else if (o instanceof Character)
				// VirtualMachine.getInstance().push(Character.getNumericValue(((Character)o).charValue()));
				VirtualMachine.getInstance().push(
						String.valueOf(((Character) o).charValue())
								.codePointAt(0));
		}

		public String toString() {
			return "CINT";
		}
	},
	CNAT {
		public void exec(Object arg) {
			// Character a = (Character) VirtualMachine.getInstance().pop();
			// VirtualMachine.getInstance().push((Integer) (int) a);
			Object o = VirtualMachine.getInstance().pop();
			if (o instanceof Integer)
				VirtualMachine.getInstance().push(Math.abs((Integer) o));
			else if (o instanceof Character)
				// VirtualMachine.getInstance().push(Character.getNumericValue(((Character)o).charValue()));
				VirtualMachine.getInstance().push(
						String.valueOf(((Character) o).charValue())
								.codePointAt(0));
		}

		public String toString() {
			return "CNAT";
		}
	},
	CCHAR {
		public void exec(Object arg) {
			Object o = VirtualMachine.getInstance().pop();
			if (o instanceof Character)
				VirtualMachine.getInstance().push((Character) o);
			else if (o instanceof Integer)
				VirtualMachine.getInstance().push(
						(char) ((Integer) o).intValue());
		}

		public String toString() {
			return "CCHAR";
		}
	},
	ABS {
		/*
		 * public void exec(Object arg) { // -A Integer tipo = (Integer) arg; /*
		 * 0 -> BOOL 1 -> CHAR 2 -> NAT 3 -> INT 4 -> FLOAT
		 * 
		 * switch (tipo) { case 3: Integer B4 = (Integer)
		 * VirtualMachine.getInstance().pop();
		 * VirtualMachine.getInstance().push((Integer) Math.abs(B4)); break;
		 * case 4: Float B5 = (Float) VirtualMachine.getInstance().pop();
		 * VirtualMachine.getInstance().push((Float) (Math.abs(B5))); break; }
		 */
		public void exec(Object arg) {
			Object o = VirtualMachine.getInstance().pop();
			if (o instanceof Integer)
				VirtualMachine.getInstance().push(Math.abs((Integer) o));
			else if (o instanceof Float)
				VirtualMachine.getInstance().push((Float) Math.abs((Float) o));
		}

		public String toString() {
			return "ABS";
		}
	},
	COPIA {
		public void exec(Object arg) {
			Object o = VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().push(o);
			VirtualMachine.getInstance().push(o);
		}

		public String toString() {
			return "COPIA";
		}
	},
	IR_A {
		public void exec(Object arg) {
			VirtualMachine.getInstance().setPC(((Integer) arg).intValue() - 1);
		}

		public String toString() {
			return "IR_A";
		}
	},
	IR_F {
		public void exec(Object arg) {
			Boolean b = (Boolean) VirtualMachine.getInstance().pop();
			if (!b)
				VirtualMachine.getInstance().setPC(
						((Integer) arg).intValue() - 1);
		}

		public String toString() {
			return "IR_F";
		}
	},
	IR_V {
		public void exec(Object arg) {
			Boolean b = (Boolean) VirtualMachine.getInstance().pop();
			if (b)
				VirtualMachine.getInstance().setPC(
						((Integer) arg).intValue() - 1);
		}

		public String toString() {
			return "IR_V";
		}
	},
	IR_IND {
		public void exec(Object arg) {
			Integer pc = (Integer) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().setPC(pc - 1);
		}

		public String toString() {
			return "IR_IND";
		}
	},
	APILA_IND {
		public void exec(Object arg) throws Exception {
			Integer dir = (Integer) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().load(dir);
		}

		public String toString() {
			return "APILA_IND";
		}
	},
	DESAPILA_IND {
		public void exec(Object arg) throws Exception {
			Object o = VirtualMachine.getInstance().pop();
			Integer dir = (Integer) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().store(dir, o);
		}

		public String toString() {
			return "DESAPILA_IND";
		}
	},
	NOP {
		public void exec(Object arg) {
		}

		public String toString() {
			return "NOP";
		}
	},
	FLIP {
		public void exec(Object arg) {
			Object cima = VirtualMachine.getInstance().pop();
			Object subcima = VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().push(cima);
			VirtualMachine.getInstance().push(subcima);
		}

		public String toString() {
			return "FLIP";
		}
	},
	MUEVE {
		public void exec(Object arg) throws Exception {
			/*
			 * La asignación de objetos complejos implica el movimiento de
			 * bloques completos de memoria. • Para facilitar dicho movimiento
			 * se introduce la instrucción mueve(s). Dicha instrucción encuentra
			 * en la cima la dirección origen o y en la subcima la dirección
			 * destino d, y realiza el movimiento de s celdas desde o a s.
			 * 
			 * para i<-0 hasta s-1 hacer Mem[Pila[ST-1]+i] <- Mem[Pila[ST]+i]
			 * ST<-ST-2 PC <- PC+1
			 */
			Integer origen = (Integer) VirtualMachine.getInstance().pop();
			Integer destino = (Integer) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().copy(origen.intValue(),
					destino.intValue(), ((Integer) arg).intValue());

		}

		public String toString() {
			return "MUEVE";
		}
	},
	NEW {
		public void exec(Object arg) throws Exception {
			Integer dir = VirtualMachine.getInstance().malloc((Integer) arg);
			VirtualMachine.getInstance().push(dir);
		}

		public String toString() {
			return "NEW";
		}
	},
	DISPOSE {
		public void exec(Object arg) {
			Integer dir = (Integer) VirtualMachine.getInstance().pop();
			VirtualMachine.getInstance().free(dir, (Integer) arg);
		}

		public String toString() {
			return "DISPOSE";
		}
	},
	INV {
		public void exec(Object arg) {
			// Integer tipo = (Integer) arg;
			/*
			 * 0 -> BOOL 1 -> CHAR 2 -> NAT 3 -> INT 4 -> FLOAT
			 */
			/*
			 * switch (tipo) { case 3: Integer B4 = (Integer)
			 * VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Integer)(-B4)); break; case 4:
			 * Float B5 = (Float) VirtualMachine.getInstance().pop();
			 * VirtualMachine.getInstance().push((Float)(-B5)); break; }
			 */
			Object o = VirtualMachine.getInstance().pop();
			if (o instanceof Integer)
				VirtualMachine.getInstance().push(-(Integer) o);
			else if (o instanceof Float)
				VirtualMachine.getInstance().push(-(Float) o);
		}

		public String toString() {
			return "INV";
		}
	};

	public abstract void exec(Object arg) throws Exception;

	public abstract String toString();

}