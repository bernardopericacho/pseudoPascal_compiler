package Scanner;

import java.io.BufferedReader;
import java.io.FileReader;

public class Scanner {

	// ATRIBUTOS
	private BufferedReader input;
	private StringBuffer lex;
	private char sigCar;
	private Estado estado;
	CatLexica catActual;

	// CONSTRUCTORAS
	public Scanner() {
		lex = new StringBuffer();
		catActual = CatLexica.ID;
	}

	// GETTERS Y SETTERS
	public void setReader(FileReader fr) {
		input = new BufferedReader(fr);
	}

	// METODOS PRIVADOS
	private void error() throws Exception {
		inicializa();
		estado = Estado.E0;
		lex.delete(0, lex.length());
	}

	private boolean hay(char car) {
		if (sigCar == car) {
			switch (car) {
			case '\n':
				break;
			case '\r':
				break;
			case '\b':
				break;
			default:
			}
			return true;
		}
		return false;
	}

	private boolean hayAlfanumerico() {
		return hayLetra() || hayDigito();
	}

	private boolean hayDigito() {
		return (hay('0') || hayDigPos());
	}

	private boolean hayDigPos() {
		if (sigCar >= '1' && sigCar <= '9') {
			return true;
		}
		return false;
	}

	private boolean hayEof() {
		return sigCar == (char) -1;
	}

	private boolean hayIgnorable() {
		return (hay(' ') || hay('\t') || hay('\n') || hay('\r') || hay('\b'));
	}

	private boolean hayLetra() {
		if (sigCar >= 'a' && sigCar <= 'z' || sigCar >= 'A' && sigCar <= 'Z') {
			return true;
		}
		return false;
	}

	private CatLexica sigToken() {
		try {
			estado = Estado.E0;
			lex.delete(0, lex.length());
			while (true) {
				switch (estado) {
				case E0:
					if (hay('&'))
						transita(Estado.E1);
					else if (hay(';'))
						transita(Estado.E2);
					else if (hay(':'))
						transita(Estado.E3);
					else if (hay('<'))
						transita(Estado.E5);
					else if (hay('>'))
						transita(Estado.E6);
					else if (hay('='))
						transita(Estado.E9);
					else if (hay('+'))
						transita(Estado.E12);
					else if (hay('-'))
						transita(Estado.E13);
					else if (hay('*'))
						transita(Estado.E14);
					else if (hay('/'))
						transita(Estado.E15);
					else if (hay('%'))
						transita(Estado.E16);
					else if (hay(')'))
						transita(Estado.E19);
					else if (hay('('))
						transita(Estado.E20);
					else if (hay('|'))
						transita(Estado.E21);
					else if (hay('#'))
						transitaIgnorando(Estado.E41);
					else if (hay('\''))
						transita(Estado.E42);
					else if (hay('0'))
						transita(Estado.E45);
					else if (hayDigPos())
						transita(Estado.E46);
					else if (hay('{'))
						transita(Estado.E56);
					else if (hay('}'))
						transita(Estado.E57);
					else if (hay('['))
						transita(Estado.E54);
					else if (hay(']'))
						transita(Estado.E55);
					else if (hayLetra())
						transita(Estado.E58);
					else if (hay(','))
						transita(Estado.E59);
					else if (hay('.'))
						transita(Estado.E60);
					else if (hayIgnorable())
						transitaIgnorando(Estado.E0);
					else if (hayEof())
						transitaIgnorando(Estado.E61);
					break;
				case E1:
					return CatLexica.SEP;
				case E2:
					return CatLexica.PYCOMA;
				case E3:
					if (hay('='))
						transita(Estado.E4);
					else
						return CatLexica.DOSPUNTOS;
					break;
				case E4:
					return CatLexica.ASIG;
				case E5:
					if (hay('='))
						transita(Estado.E7);
					else if (hay('<'))
						transita(Estado.E17);
					else
						return CatLexica.MENOR;
					break;
				case E6:
					if (hay('='))
						transita(Estado.E8);
					else if (hay('>'))
						transita(Estado.E18);
					else
						return CatLexica.MAYOR;
					break;
				case E7:
					return CatLexica.MENOROIGUAL;
				case E8:
					return CatLexica.MAYOROIGUAL;
				case E9:
					if (hay('/'))
						transita(Estado.E10);
					else
						return CatLexica.IGUAL;
					break;
				case E10:
					if (hay('='))
						transita(Estado.E11);
					else
						error();
					break;
				case E11:
					return CatLexica.DISTINTO;
				case E12:
					return CatLexica.MAS;
				case E13:
					if (hay('>'))
						transita(Estado.E53);
					else
						return CatLexica.MENOS;
					break;
				case E14:
					return CatLexica.MULT;
				case E15:
					return CatLexica.DIV;
				case E16:
					return CatLexica.MOD;
				case E17:
					return CatLexica.DESPIZQ;
				case E18:
					return CatLexica.DESPDER;
				case E19:
					return CatLexica.PCIE;
				case E20:
					if (hay('n'))
						transita(Estado.E22);
					else if (hay('i'))
						transita(Estado.E26);
					else if (hay('c'))
						transita(Estado.E30);
					else if (hay('f'))
						transita(Estado.E35);
					else
						return CatLexica.PAP;
					break;
				case E21:
					return CatLexica.BARRAV;
				case E22:
					if (hay('a'))
						transita(Estado.E23);
					else
						error();
					break;
				case E23:
					if (hay('t'))
						transita(Estado.E24);
					else
						error();
					break;
				case E24:
					if (hay(')'))
						transita(Estado.E25);
					else
						error();
					break;
				case E25:
					return CatLexica.CNAT;
				case E26:
					if (hay('n'))
						transita(Estado.E27);
					else
						error();
					break;
				case E27:
					if (hay('t'))
						transita(Estado.E28);
					else
						error();
					break;
				case E28:
					if (hay(')'))
						transita(Estado.E29);
					else
						error();
					break;
				case E29:
					return CatLexica.CINT;
				case E30:
					if (hay('h'))
						transita(Estado.E31);
					else
						error();
					break;
				case E31:
					if (hay('a'))
						transita(Estado.E32);
					else
						error();
					break;
				case E32:
					if (hay('r'))
						transita(Estado.E33);
					else
						error();
					break;
				case E33:
					if (hay(')'))
						transita(Estado.E34);
					else
						error();
					break;
				case E34:
					return CatLexica.CCHAR;
				case E35:
					if (hay('l'))
						transita(Estado.E36);
					else
						error();
					break;
				case E36:
					if (hay('o'))
						transita(Estado.E37);
					else
						error();
					break;
				case E37:
					if (hay('a'))
						transita(Estado.E38);
					else
						error();
					break;
				case E38:
					if (hay('t'))
						transita(Estado.E39);
					else
						error();
					break;
				case E39:
					if (hay(')'))
						transita(Estado.E40);
					else
						error();
					break;
				case E40:
					return CatLexica.CFLOAT;
				case E41:
					if (hay('\n'))
						transitaIgnorando(Estado.E0);
					else
						transitaIgnorando(Estado.E41);
					break;
				case E42:
					if (!hay('\''))
						transita(Estado.E43);
					else
						error();
					break;
				case E43:
					if (hay('\''))
						transita(Estado.E44);
					else
						error();
					break;
				case E44:
					return CatLexica.CHAR;
				case E45:
					if (hay('.'))
						transita(Estado.E47);
					else if (hay('e') || hay('E'))
						transita(Estado.E49);
					else
						return CatLexica.NNAT;
					break;
				case E46:
					if (hayDigito())
						transita(Estado.E46);
					else if (hay('.'))
						transita(Estado.E47);
					else if (hay('e') || hay('E'))
						transita(Estado.E49);
					else
						return CatLexica.NNAT;
					break;
				case E47:
					if (hay('0'))
						transita(Estado.E47);
					else if (hayDigPos())
						transita(Estado.E48);
					else
						error();
					break;
				case E48:
					if (hay('0'))
						transita(Estado.E47);
					else if (hayDigPos())
						transita(Estado.E48);
					else if (hay('e') || hay('E'))
						transita(Estado.E49);
					else
						return CatLexica.NFLOAT;
					break;
				case E49:
					if (hay('+') || hay('-'))
						transita(Estado.E50);
					else if (hay('0'))
						transita(Estado.E51);
					else if (hayDigPos())
						transita(Estado.E52);
					else
						error();
					break;
				case E50:
					if (hay('0'))
						transita(Estado.E51);
					else if (hayDigPos())
						transita(Estado.E52);
					else
						error();
					break;
				case E51:
					return CatLexica.NFLOAT;
				case E52:
					if (hayDigito())
						transita(Estado.E52);
					else
						return CatLexica.NFLOAT;
					break;
				case E53:
					return CatLexica.FLECHA;
				case E54:
					return CatLexica.CORCHETEIZQ;
				case E55:
					return CatLexica.CORCHETEDER;
				case E56:
					return CatLexica.LLAVEIZQ;
				case E57:
					return CatLexica.LLAVEDER;
				case E58:
					if (hayAlfanumerico())
						transita(Estado.E58);
					else
						return hayReservada();
					break;
				case E59:
					return CatLexica.COMA;
				case E60:
					return CatLexica.PUNTO;
				case E61:
					return CatLexica.EOF;
				}
			}
		} catch (Exception e) {
			System.out.println("Error de lectura");
			return CatLexica.EOF;
		}
	}

	private CatLexica hayReservada() {
		Reservadas[] palRes = Reservadas.values();
		String lexema = lex.toString();
		for (int i = 0; i < palRes.length; i++) {
			if (palRes[i].toString().toLowerCase().compareTo(lexema) == 0)
				return CatLexica.valueOf(palRes[i].toString());
		}
		return CatLexica.ID;
	}

	private void transita(Estado sig) throws Exception {
		lex.append((char) sigCar);
		sigCar = (char) input.read();
		estado = sig;
	}

	private void transitaIgnorando(Estado sig) throws Exception {
		sigCar = (char) input.read();
		estado = sig;
	}

	// METODOS PUBLICOS
	public void inicializa() throws Exception {
		sigCar = (char) input.read();
		catActual = sigToken();
	}

	public boolean entrada(CatLexica cat) {
		return (catActual == cat);
	}

	public String leeLexema() {
		return lex.toString();
	}

	public boolean reconoce(CatLexica cat) {
		if (entrada(cat)) {
			catActual = sigToken();
			return true;
		}
		return false;
	}

}