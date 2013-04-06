package Parser;

public class Props {

	// ATRIBUTOS
	public Clases clase;
	public Type tipo;
	public int nivel;
	public int dir;
	public int init;

	// CONSTRUCTORA
	public Props(Clases c, Type t, int n, int d, int i) {
		clase = c;
		tipo = t;
		nivel = n;
		dir = d;
		init = i;
	}
}
