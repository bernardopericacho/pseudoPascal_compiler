package vm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

public class Memory {

	private static int MEMSIZE = 1000;
	private Vector<Object> mem;
	private int heap;
	private LinkedList<Block> list;

	public Memory() {
		mem = new Vector<Object>(MEMSIZE);
		heap = MEMSIZE;
		list = new LinkedList<Block>();
		mem.setSize(MEMSIZE);
		mem.set(0, 0);
	}

	private int morecore(int tam) throws Exception {
		int staticMem = (Integer) mem.get(0);
		if (heap - tam <= staticMem)
			throw new Exception("OUT OF MEMORY");
		heap -= tam;
		return heap;
	}

	public int malloc(int tam) throws Exception {
		Iterator<Block> blockIt = list.iterator();
		Block tmpB = null;
		while (blockIt.hasNext()) {
			tmpB = blockIt.next();
			if (tmpB.tam == tam) {
				list.remove(tmpB);
				return tmpB.dir;
			} else if (tmpB.tam > tam) {
				int dir = tmpB.dir;
				tmpB.dir = dir + tam;
				return dir;
			}
		}
		if (tmpB == null)
			return morecore(tam);
		return 0;
	}

	public void free(int dir, int tam) {
		if (list.size() == 0) {
			list.add(new Block(dir, tam));
			return;
		}
		// Encontramos huecos anterior y posterior
		Iterator<Block> blockIt = list.iterator();
		Block tmpB1 = null;
		Block tmpB2 = null;
		int b = 0;
		tmpB1 = blockIt.next();
		while (blockIt.hasNext()) {
			tmpB2 = blockIt.next();
			if (tmpB2.dir < dir) {
				break;
			}
			tmpB1 = tmpB2;
			b++;
		}
		if (tmpB2 == null) {
			if (tmpB1.dir < dir) {
				if (tmpB1.dir + tmpB1.tam == dir) {
					tmpB1.tam += tam;
					return;
				} else {
					list.add(b, new Block(dir, tam));
					return;
				}
			} else if ((dir + tam) == tmpB1.dir) {
				tmpB1.dir = dir;
				tmpB1.tam += tam;
				return;
			} else {
				list.add(new Block(dir, tam));
				return;
			}
		}
		// Compactacion hueco posterior
		if ((tmpB2.dir + tmpB2.tam) == dir)
			tmpB2.tam += tam;
		// Compactacion hueco anterior
		if ((dir + tam) == tmpB1.dir) {
			tmpB1.dir -= tam;
			tmpB1.tam += tam;
		}
		if (tmpB2.dir + tmpB2.tam == tmpB1.dir) {
			tmpB2.tam += tmpB1.tam - tam;
			list.remove(tmpB1);
		}

	}

	public Object load(int dir) throws Exception {
		return mem.get(dir);
	}

	public void store(int dir, Object o) throws Exception {
		mem.set(dir, o);
	}
	
	public void print() {
		System.out.println("Memoria");
		System.out.println("-------");
		int i = 0;
		Object o;
		while(i < Memory.MEMSIZE) {
			o = mem.get(i);
			if(o != null) System.out.println("M[" + i + "]\t--> " + o.toString());
			i++;
		}
		System.out.println();
	}
	
	public void printAll() {
		System.out.println("Memoria");
		System.out.println("-------");
		int i = 0;
		int j;
		Object o;
		while(i < Memory.MEMSIZE) {
			j = 0;
			while(j < 10) {
				o = mem.get(i);
				if(o != null) System.out.print(o.toString());
				else System.out.print("_");
				System.out.print("\t");
				i++;
				j++;
			}
			System.out.println();
		}
		System.out.println();
	}

	private class Block {

		public int dir;
		public int tam;

		public Block(int d, int t) {
			dir = d;
			tam = t;
		}

	}

}
