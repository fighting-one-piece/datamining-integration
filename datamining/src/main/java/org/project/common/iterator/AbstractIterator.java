package org.project.common.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.project.common.preconditions.Preconditions;

public abstract class AbstractIterator<T> implements Iterator<T> {

	private State state = State.NOT_READY;

	protected AbstractIterator() {
	}

	private enum State {
		READY, NOT_READY, DONE, FAILED,
	}

	private T next;

	protected abstract T computeNext();

	protected final T endOfData() {
		state = State.DONE;
		return null;

	}

	@Override
	public final boolean hasNext() {
		Preconditions.checkState(state != State.FAILED);
		switch (state) {
		case DONE:
			return false;
		case READY:
			return true;
		default:
		}
		return tryToComputeNext();

	}

	private boolean tryToComputeNext() {
		state = State.FAILED; // temporary pessimism
		next = computeNext();
		if (state != State.DONE) {
			state = State.READY;
			return true;
		}
		return false;
	}

	@Override
	public final T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		state = State.NOT_READY;
		return next;
	}

	@Override
	public final void remove() {
		throw new UnsupportedOperationException();
	}

}
