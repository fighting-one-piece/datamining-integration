package org.project.common.vector;

public interface Vector<T> extends Cloneable {

	int size();

	void assign(T value);

	void assign(T[] value);
	
	void assign(Vector<T> value);
	
	Element<T> getElement(int index);
	
	Iterable<Element<T>> all();
	
	void set(int index, T value);
	
	Vector<T> plus(T value);
	
	Vector<T> plus(Vector<T> vector);

	Vector<T> minus(T value);

	Vector<T> minus(Vector<T> vector);

	Vector<T> multiply(T value);

	Vector<T> multiply(Vector<T> vector);
	
	Vector<T> divide(T value);

	Vector<T> divide(Vector<T> vector);
	
	Vector<T> clone();
	
	interface Element<T> {
		
		T get();
		
		int index();
		
		void set(T value);
	}
}
