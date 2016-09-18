package model;

import java.util.NoSuchElementException;

//simple dynamic queue which can add to the back and leave from the front
public class Queue<E> implements Iterable<E> {
	private Node<E> head, tail;
	private int size;
	
	public Queue() {
		head = null;
		tail = null;
		size = 0;
	}
	
	public boolean isEmpty() {
		return head == null;
	}
	
	//add to back of queue
	public void enqueue(E element) {
		if(isEmpty()) {
			head = new Node<E>(element);
			tail = head;
		} else {
			tail.setNext(new Node<E>(element));
			tail = tail.getNext();
		}
		size++;
	}
	
	//sugar for enqueue
	public void add(E element) {
		enqueue(element);
	}
	
	//add to front of queue
	public void prepend(E element) {
		Node<E> temp = new Node<E>(element);
		temp.setNext(head);
		head = temp;
	}
	
	//take first element on queue
	public E dequeue() throws NoSuchElementException {
		if(isEmpty()) throw new NoSuchElementException("There was nothing on the queue!");
		Node<E> temp = head;
		if(size == 1) {
			clear();
		} else {
			head = head.getNext();
			size--;
		}
		return temp.getData();
	}
	
	//remove an element from the list
	public E remove(E element) throws NoSuchElementException {
		if(tail.getData() == element) return removeTail(); //if this is the last element
		Node<E> prev = findNodeBefore(element);
		Node<E> current = prev.getNext();
		
		prev.setNext(current.getNext());
		size--;
		return current.getData();
	}
	
	//remove the tail from the list
	public E removeTail() throws NoSuchElementException {
		if(isEmpty()) throw new NoSuchElementException();
		
		E data = tail.getData();
		
		if(size == 1) { //the tail is the only thing in the list
			clear();
		} else {
			Node<E> prev = findNodeBefore(data);
			prev.setNext(null);
			tail = prev;
			size--;
		}
		return data;
	}
	
	//helper method for finding the node before an element
	//will throw NotFound even if "element" is in the first node
	private Node<E> findNodeBefore(E element) throws NoSuchElementException {
		Iterator<E> iter = new Iterator<E>(this);
		while(iter.hasNext()) {
			Node<E> next = iter.getNext();
			if(next.getNext() == null) throw new NoSuchElementException();
			if(next.getNext().getData() == element) {
				return next;
			}
		}
		
		throw new NoSuchElementException();
	}
	
	public void clear() {
		head = null;
		tail = null;
		size = 0;
	}
	
	public int size() {
		return size;
	}
	
	public Node<E> getHead() {
		return head;
	}
	
	@Override
	public java.util.Iterator<E> iterator() {
		return new Iterator<E>(this);
	}

	private static class Iterator<E> implements java.util.Iterator<E> {
	private Node<E> current;
	
	public Iterator(Queue<E> list) {
		current = list.getHead();
	}
	
	//move to the next Node in the list and return it
	public Node<E> getNext() throws NoSuchElementException {
		if(!hasNext()) throw new NoSuchElementException();
		Node<E> temp = current;
		current = current.getNext();
		return temp;
	}
	
	//returns false if the current Node is the last one
	public boolean hasNext() {
		return current != null;
	}

	@Override
	public E next() throws NoSuchElementException {
		return getNext().getData();
	}
	
}

}
