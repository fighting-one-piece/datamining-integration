package org.project.common.preconditions;

public class Preconditions {

	private Preconditions() {
	}

	/**
	 * * Ensures that {@code expression} is {@code true}. * * @param expression
	 * any boolean expression involving an argument to the * current method * @throws
	 * IllegalArgumentException if {@code expression} is {@code false}
	 */
	public static void checkArgument(boolean expression) {
		if (!expression) {
			failArgument(null);
		}
	}

	/**
	 * * Ensures that {@code expression} is {@code true}. * * @param expression
	 * any boolean expression involving the state of the * current instance (and
	 * not involving arguments) * @throws IllegalStateException if
	 * {@code expression} is {@code false}
	 */
	public static void checkState(boolean expression) {
		if (!expression) {
			failState(null);
		}
	}

	/**
	 * * Ensures that {@code reference} is not {@code null}. * * @param
	 * reference an object reference that was passed as a parameter to the *
	 * current method * @throws NullPointerException if {@code reference} is
	 * {@code null}
	 */
	public static void checkNotNull(Object reference) {
		if (reference == null) {
			failNotNull(null);
		}
	}

	/**
	 * * Ensures that {@code expression} is {@code true}. * * @param expression
	 * any boolean expression involving an argument to the * current method * @param
	 * message a message object which will be converted using *
	 * {@link Object#toString} and included in the exception message if the *
	 * check fails * @throws IllegalArgumentException if {@code expression} is
	 * {@code false}
	 */
	public static void checkArgument(boolean expression, Object message) {
		if (!expression) {
			failArgument(message);
		}
	}

	/**
	 * * Ensures that {@code expression} is {@code true}. * * @param expression
	 * any boolean expression involving the state of the * current instance (and
	 * not involving arguments) * @param message a message object which will be
	 * converted using * {@link Object#toString} and included in the exception
	 * message if the * check fails * @throws IllegalStateException if
	 * {@code expression} is {@code false}
	 */
	public static void checkState(boolean expression, Object message) {
		if (!expression) {
			failState(message);
		}
	}

	/**
	 * * Ensures that {@code reference} is not {@code null}. * * @param
	 * reference an object reference that was passed as a parameter to the *
	 * current method * @param message a message object which will be converted
	 * using * {@link Object#toString} and included in the exception message if
	 * the * check fails * @throws NullPointerException if {@code reference} is
	 * {@code null}
	 */
	public static void checkNotNull(Object reference, Object message) {
		if (reference == null) {
			failNotNull(message);
		}
	}

	private static void failArgument(final Object description) {
		throw new IllegalArgumentException() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getMessage() {
				return buildMessage(this, description);
			}

			@Override
			public String toString() {
				return buildString(this);
			}
		};
	}

	private static void failState(final Object description) {
		throw new IllegalStateException() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getMessage() {
				return buildMessage(this, description);
			}

			@Override
			public String toString() {
				return buildString(this);
			}
		};
	}

	private static void failNotNull(final Object description) {
		throw new NullPointerException() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getMessage() {
				return buildMessage(this, description);
			}

			@Override
			public String toString() {
				return buildString(this);
			}
		};
	}

	private static final int STACK_INDEX = 2;
	private static final String NL = System.getProperty("line.separator");

	private static String buildMessage(RuntimeException e, Object description) {
		StringBuilder sb = new StringBuilder(300).append("precondition failed");
		StackTraceElement[] trace = e.getStackTrace();
		StackTraceElement failedAt = trace[STACK_INDEX];
		if (description != null) {
			sb.append(": ").append(description);
		}
		sb.append(NL).append("    failed check:   at ").append(failedAt);
		for (int i = STACK_INDEX + 1; i < trace.length; i++) {
			if (!trace[i].getClassName().equals(failedAt.getClassName())) {
				sb.append(NL).append("    offending call: at ")
						.append(trace[i]);
				break;
			}
		}
		return sb.toString();
	}

	private static String buildString(Exception e) {
		Class<?> superclass = e.getClass().getSuperclass();
		return superclass.getName() + ": " + e.getMessage();
	}
}
