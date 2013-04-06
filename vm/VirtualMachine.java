package vm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;

import common.Instruction;

public class VirtualMachine {

	// ATRIBUTOS
	private static VirtualMachine vm = null;
	private static boolean trace;
	private static ArrayList<Instruction> instructionMemory;
	private static Stack<Object> stack;
	private int pc;
	private Memory mem;

	// CONSTRUCTORAS
	private VirtualMachine() {
		trace = false;
		stack = new Stack<Object>();
		pc = 0;
		mem = new Memory();
	}

	// GETTERS Y SETTERS
	public static VirtualMachine getInstance() {
		if (vm == null)
			vm = new VirtualMachine();
		return vm;
	}

	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		VirtualMachine.trace = trace;
	}

	public void setInstructionMemory(ArrayList<Instruction> im) {
		VirtualMachine.instructionMemory = im;
	}

	public void setPC(int pc) {
		this.pc = pc;
	}

	// METODOS PRIVADOS
	private void printCurrentInstruction() {
		if (pc >= instructionMemory.size())
			System.out.println("EJECUCION TERMINADA");
		else {
			System.out.print("Siguiente instruccion " + pc + ": ");
			System.out.print(instructionMemory.get(pc).toString());
		}
	}

	private void printDivision() {
		System.out.println();
		System.out.println("-------------------------------------------------");
		System.out.println();
	}

	private void printMemory() {
		mem.print();
	}
	
	public void printMemoryAll() {
		mem.printAll();
	}

	private void printStack() {
		System.out.println("Pila");
		System.out.println("----");
		Iterator<Object> it = stack.iterator();
		while (it.hasNext()) {
			System.out.print(it.next().toString() + " ");
		}
		System.out.println();
		printDivision();
	}

	// METODOS PUBLICOS
	public void exec() {

		getInstance();
		Scanner in = new Scanner(System.in);
		boolean end = (instructionMemory.size() == 0);

		if (trace) {
			printDivision();
			printCurrentInstruction();
		}

		try {
			while (!end) {
				if (trace) {
					String c = in.nextLine();
					if (c.compareToIgnoreCase("q") == 0) {
						System.out.println("EJECUCION ABORTADA");
						break;
					} else if (c.compareToIgnoreCase("c") == 0)
						trace = false;
				}
				instructionMemory.get(pc).exec();
				pc++;
				if (trace) {
					printDivision();
					printMemory();
					printStack();
					printCurrentInstruction();
				}
				if (pc > (instructionMemory.size() - 1))
					end = true;
			}
		} catch (Exception e) {
			printDivision();
			System.out.println("ERROR DURANTE LA EJECUCION");
			printDivision();
		}

		// SALIDA POR PANTALLA DEL ESTADO DE LA MÁQUINA
		this.printStack();
		this.printMemory();
	}

	public void load(int dir) throws Exception {
		stack.push(mem.load(dir));
	}

	public Object pop() {
		return stack.pop();
	}

	public void push(Object o) {
		stack.push(o);
	}

	public void store(int dir, Object o) throws Exception {
		mem.store(dir, o);
	}

	public void copy(int source, int target, int size) throws Exception {
		for (int i = 0; i < size; i++) {
			mem.store(target + i, mem.load(source + i));
		}
	}
	
	public int malloc(int tam) throws Exception {
		return mem.malloc(tam);
	}
	
	public void free(int dir, int tam) {
		mem.free(dir, tam);
	}

}