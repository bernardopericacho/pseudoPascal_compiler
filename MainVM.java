import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import vm.VirtualMachine;

import common.Function;
import common.Instruction;

public class MainVM {

	// ATRIBUTOS
	static VirtualMachine vm;

	// METODOS PRIVADOS
	private static Instruction processLine(String line) throws Exception {
		String[] parts = line.split(" ");
		Function function = Function.NOP;
		if (parts[0].compareTo(Function.ABS.toString()) == 0)
			function = Function.ABS;
		else if (parts[0].compareTo(Function.ADDF.toString()) == 0)
			function = Function.ADDF;
		else if (parts[0].compareTo(Function.ADDI.toString()) == 0)
			function = Function.ADDI;
		else if (parts[0].compareTo(Function.AND.toString()) == 0)
			function = Function.AND;
		else if (parts[0].compareTo(Function.APILA.toString()) == 0)
			function = Function.APILA;
		else if (parts[0].compareTo(Function.APILA_DIR.toString()) == 0)
			function = Function.APILA_DIR;
		else if (parts[0].compareTo(Function.CCHAR.toString()) == 0)
			function = Function.CCHAR;
		else if (parts[0].compareTo(Function.CFLOAT.toString()) == 0)
			function = Function.CFLOAT;
		else if (parts[0].compareTo(Function.CINT.toString()) == 0)
			function = Function.CINT;
		else if (parts[0].compareTo(Function.CNAT.toString()) == 0)
			function = Function.CNAT;
		else if (parts[0].compareTo(Function.DESAPILA.toString()) == 0)
			function = Function.DESAPILA;
		else if (parts[0].compareTo(Function.DESAPILA_DIR.toString()) == 0)
			function = Function.DESAPILA_DIR;
		else if (parts[0].compareTo(Function.DIVF.toString()) == 0)
			function = Function.DIVF;
		else if (parts[0].compareTo(Function.DIVI.toString()) == 0)
			function = Function.DIVI;
		else if (parts[0].compareTo(Function.EQUAL.toString()) == 0)
			function = Function.EQUAL;
		else if (parts[0].compareTo(Function.GREATER.toString()) == 0)
			function = Function.GREATER;
		else if (parts[0].compareTo(Function.GREATEREQ.toString()) == 0)
			function = Function.GREATEREQ;
		else if (parts[0].compareTo(Function.IN.toString()) == 0)
			function = Function.IN;
		else if (parts[0].compareTo(Function.LESS.toString()) == 0)
			function = Function.LESS;
		else if (parts[0].compareTo(Function.LESSEQ.toString()) == 0)
			function = Function.LESSEQ;
		else if (parts[0].compareTo(Function.MOD.toString()) == 0)
			function = Function.MOD;
		else if (parts[0].compareTo(Function.MULTF.toString()) == 0)
			function = Function.MULTF;
		else if (parts[0].compareTo(Function.MULTI.toString()) == 0)
			function = Function.MULTI;
		else if (parts[0].compareTo(Function.NEQUAL.toString()) == 0)
			function = Function.NEQUAL;
		else if (parts[0].compareTo(Function.NOT.toString()) == 0)
			function = Function.NOT;
		else if (parts[0].compareTo(Function.OR.toString()) == 0)
			function = Function.OR;
		else if (parts[0].compareTo(Function.OUT.toString()) == 0)
			function = Function.OUT;
		else if (parts[0].compareTo(Function.SHIFTL.toString()) == 0)
			function = Function.SHIFTL;
		else if (parts[0].compareTo(Function.SHIFTR.toString()) == 0)
			function = Function.SHIFTR;
		else if (parts[0].compareTo(Function.SUBF.toString()) == 0)
			function = Function.SUBF;
		else if (parts[0].compareTo(Function.SUBI.toString()) == 0)
			function = Function.SUBI;
		else if (parts[0].compareTo(Function.COPIA.toString()) == 0)
			function = Function.COPIA;
		else if (parts[0].compareTo(Function.IR_A.toString()) == 0)
			function = Function.IR_A;
		else if (parts[0].compareTo(Function.IR_F.toString()) == 0)
			function = Function.IR_F;
		else if (parts[0].compareTo(Function.IR_V.toString()) == 0)
			function = Function.IR_V;
		else if (parts[0].compareTo(Function.IR_IND.toString()) == 0)
			function = Function.IR_IND;
		else if (parts[0].compareTo(Function.APILA_IND.toString()) == 0)
			function = Function.APILA_IND;
		else if (parts[0].compareTo(Function.DESAPILA_IND.toString()) == 0)
			function = Function.DESAPILA_IND;
		else if (parts[0].compareTo(Function.ADD.toString()) == 0)
			function = Function.ADD;
		else if (parts[0].compareTo(Function.SUB.toString()) == 0)
			function = Function.SUB;
		else if (parts[0].compareTo(Function.MULT.toString()) == 0)
			function = Function.MULT;
		else if (parts[0].compareTo(Function.DIV.toString()) == 0)
			function = Function.DIV;
		else if (parts[0].compareTo(Function.FLIP.toString()) == 0)
			function = Function.FLIP;
		else if (parts[0].compareTo(Function.MUEVE.toString()) == 0)
			function = Function.MUEVE;
		else if (parts[0].compareTo(Function.INV.toString()) == 0)
			function = Function.INV;
		if (function.compareTo(Function.NOP) == 0)
			throw new Exception();
		Instruction tmpInst;
		if (parts.length > 1)
			tmpInst = new Instruction(function, Integer.parseInt(parts[1]));
		else
			tmpInst = new Instruction(function, 0);
		return tmpInst;
	}

	private static void loadProgram(String path) throws Exception {
		/*
		 * InputStream file = new FileInputStream(path); InputStream buffer =
		 * new BufferedInputStream(file); ObjectInput inputD = new
		 * ObjectInputStream(buffer);
		 * vm.setInstructionMemory((ArrayList<Instruction>)
		 * inputD.readObject()); inputD.close();
		 */
		/*
		File file = new File(path);
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			i.add(processLine(scanner.nextLine()));
		}
		scanner.close();
		*/
		
		InputStream file = new FileInputStream(path);
		InputStream buffer = new BufferedInputStream(file);
		ObjectInput inputD = new ObjectInputStream(buffer);
		vm.setInstructionMemory((ArrayList<Instruction>)inputD.readObject());
		inputD.close();
	}

	private static void printHelp() {
		System.out.println("VirtualMachine PATH [-t/-h]");
		System.out.println();
		System.out.println("Argumentos:");
		System.out.println();
		System.out.println("PATH : indica la ruta del fichero fuente");
		System.out.println("-t : Ejecuta el programa en modo traza");
		System.out.println("-h : Muestra la ayuda");
		System.out.println();
	}

	private static void printOptions() {
		System.out.println("Opciones:");
		System.out.println("- Pulse ENTER para ejecutar una instruccion");
		System.out.println("- c : Continuar (abandonar modo traza)");
		System.out.println("- q : Salir del programa");
		System.out.println();
	}

	// MAIN
	public static void main(String[] args) {

		vm = VirtualMachine.getInstance();

		System.out.println();
		System.out.println("MAQUINA PILA VIRTUAL");
		System.out.println("====================");
		System.out.println();

		switch (args.length) {
		case 0:
			System.out.println("ERROR: No se ha especificado un fichero");
			System.exit(1);
		case 1:
			if (args[0].compareToIgnoreCase("-h") == 0) {
				printHelp();
				System.exit(0);
			}
			vm.setTrace(false);
			break;
		case 2:
			if (args[1].compareToIgnoreCase("-t") == 0)
				vm.setTrace(true);
			else {
				System.out
						.println("ERROR: Argumento 1 no válido, escriba -h para obtener ayuda");
				System.exit(1);
			}
			break;
		default:
			System.out.println("ERROR: Demasiados argumentos");
			System.exit(1);
		}

		try {
			loadProgram(args[0]);			
			
		} catch (Exception e) {
			System.out
					.println("ERROR: El fichero no existe o no se pudo cargar");
			System.exit(1);
		}

		if (vm.isTrace())
			printOptions();

		vm.exec();
	}

}
