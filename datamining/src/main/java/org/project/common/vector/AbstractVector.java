package org.project.common.vector;

import java.util.Iterator;

public abstract class AbstractVector<T> implements Vector<T> {

	private int size = 16;
	
	protected T[] values = null;
	
	protected AbstractVector(int size) {
		this.size = size;
	}
	
	@Override
	public void assign(T t) {
		
	}
	
	@Override
	public void assign(T[] value) {
		
	}
	
	@Override
	public void assign(Vector<T> value) {
		
	}
	
	@Override
	public Element<T> getElement(int index) {
		return new AbstractElement(index);
	}
	
	@Override
	public Iterable<Element<T>> all() {
		return new Iterable<Element<T>>() {
			@Override
			public Iterator<Element<T>> iterator() {
				return iteratorImpl();
			}
		};
	}
	
	protected abstract Iterator<Element<T>> iteratorImpl();
	
	@Override
	public void set(int index, T value) {
		getElement(index).set(value);
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public Vector<T> plus(T value) {
		return null;
	}
	
	@Override
	public Vector<T> plus(Vector<T> vector) {
		return null;
	}
	
	@Override
	public Vector<T> minus(T value) {
		return null;
	}
	
	@Override
	public Vector<T> minus(Vector<T> vector) {
		return null;
	}

	@Override
	public Vector<T> multiply(T value) {
		return null;
	}
	
	@Override
	public Vector<T> multiply(Vector<T> vector) {
		return null;
	}

	@Override
	public Vector<T> divide(T value) {
		return null;
	}
	
	@Override
	public Vector<T> divide(Vector<T> vector) {
		return null;
	}
	
	protected Vector<T> createOptimizedCopy() {
		return createOptimizedCopy(this);
	}

	protected Vector<T> createOptimizedCopy(Vector<T> vector) {
		return vector.clone();
	}
	
	@SuppressWarnings("unchecked")
	public Vector<T> clone() {
		AbstractVector<T> vector = null;
		try {
			vector = (AbstractVector<T>) super.clone();
			vector.size = size;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return vector;
	}

	protected final class AbstractElement implements Vector.Element<T> {

		private int index = 0;
		
		public AbstractElement(int index) {
			this.index = index;
		}
		
		@Override
		public T get() {
			return values[index];
		}

		@Override
		public int index() {
			return index;
		}

		@Override
		public void set(T value) {
			values[index] = value;
		}
		
	}

}
