package model;

import java.util.NoSuchElementException;

//a simple pointer-based stack data structure
public class Stack<E> implements Iterable<E> {
	private int size;
	private Node<E> top; //most recently added
	
	public Stack() {
		size = 0;
		top = null;
	}
	
	public void push(E element) {
		if(isEmpty()) {
			top = new Node<E>(element);
		} else {
			Node<E> temp = new Node<E>(element);
			temp.setNext(top);
			top = temp;
		}
		size++;
	}
	
	public E pop() {
		E temp = top.getData();
		top = top.getNext();
		size--;
		return temp;
	}
	
	public E peek() {
		return top.getData();
	}
	
	public boolean isEmpty() {
		return top == null;
	}
	
	public int size() {
		return size;
	}
	
	private Node<E> getTop() {
		return top;
	}

	@Override
	public java.util.Iterator<E> iterator() {
		return new Iterator<E>(this);
	}
	
	private static class Iterator<E> implements java.util.Iterator<E> {
		private Node<E> current;
		
		public Iterator(Stack<E> s) {
			current = s.getTop();
		}
		
		public Node<E> getNext() throws NoSuchElementException {
			if(!hasNext()) throw new NoSuchElementException();
			Node<E> temp = current;
			current = current.getNext();
			return temp;
		}

		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public E next() {
			return getNext().getData();
		}
	}
}
