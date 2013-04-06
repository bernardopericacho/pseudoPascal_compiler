import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import common.Instruction;

import Parser.Parser;
import Scanner.Scanner;

public class MainCompiler {

	private static void compila(String origen, String destino) {
		Scanner scanner = new Scanner();
		Parser parser = new Parser(scanner);
		try {
			File f = new File(origen);
			FileReader fr = new FileReader(f);
			scanner.setReader(fr);
			scanner.inicializa();
			parser.process();
			fr.close();
			Iterator<Instruction> it = parser.cod.cod.iterator();
			OutputStream fileD = new FileOutputStream(destino + ".b");
			OutputStream bufferD = new BufferedOutputStream(fileD);
			ObjectOutput outputD = new ObjectOutputStream(bufferD);
			outputD.writeObject(parser.cod.cod);
			outputD.close();

			FileWriter fichero = new FileWriter(new File(destino + ".txt"));
			while (it.hasNext()) {

				fichero.write(it.next().toString());
			}
			fichero.close();
			System.out.println("COMPILACION REALIZADA CON EXITO");
		} catch (Exception e) {
			System.out.println("ZASCA!");
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			switch (args.length) {
			case 0:
				System.out
						.println("ERROR: No se ha especificado un fichero de entrada ni uno destino");
				System.exit(1);
			case 1:
				System.out
						.println("ERROR: No se ha especificado un fichero de destino");
				System.exit(1);
				break;
			case 2:
				compila(args[0], args[1]);
				break;

			default:
				System.out.println("ERROR: Demasiados argumentos");
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println("Error de lectura");
		}
	}
}