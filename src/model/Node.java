package model;

//node with one pointer - for queues, stacks
public class Node<E> {
	private Node<E> next;
	private E data;
	
	public Node(E data) {
		next = null;
		this.data = data;
	}
	
	public Node() {
		data = null;
		next = null;
	}
	
	public E getData() {
		return data;
	}
	
	public Node<E> getNext() {
		return next;
	}
	
	
	public void setNext(Node<E> n) {
		next = n;
	}
	
	
	public void setData(E d) {
		data = d;
	}
}
