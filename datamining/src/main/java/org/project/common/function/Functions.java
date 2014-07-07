package org.project.common.function;

public class Functions {

	/** Function that returns <tt>Math.abs(a)</tt>. */
	public static final DoubleFunction ABS = new DoubleFunction() {
		@Override
		public double apply(double a) {
			return Math.abs(a);
		}
	};

	/** Function that returns <tt>Math.acos(a)</tt>. */
	public static final DoubleFunction ACOS = new DoubleFunction() {
		@Override
		public double apply(double a) {
			return Math.acos(a);
		}
	};

	/** Function that returns <tt>Math.asin(a)</tt>. */
	public static final DoubleFunction ASIN = new DoubleFunction() {
		@Override
		public double apply(double a) {
			return Math.asin(a);
		}
	};

	/** Function that returns <tt>Math.atan(a)</tt>. */
	public static final DoubleFunction ATAN = new DoubleFunction() {
		@Override
		public double apply(double a) {
			return Math.atan(a);
		}
	};

	/** Function that returns <tt>Math.ceil(a)</tt>. */
	public static final DoubleFunction CEIL = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return Math.ceil(a);
		}
	};

	/** Function that returns <tt>Math.cos(a)</tt>. */
	public static final DoubleFunction COS = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return Math.cos(a);
		}
	};

	/** Function that returns <tt>Math.exp(a)</tt>. */
	public static final DoubleFunction EXP = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return Math.exp(a);
		}
	};

	/** Function that returns <tt>Math.floor(a)</tt>. */
	public static final DoubleFunction FLOOR = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return Math.floor(a);
		}
	};

	/** Function that returns its argument. */
	public static final DoubleFunction IDENTITY = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return a;
		}
	};

	/** Function that returns <tt>1.0 / a</tt>. */
	public static final DoubleFunction INV = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return 1.0 / a;
		}
	};

	/** Function that returns <tt>Math.log(a)</tt>. */
	public static final DoubleFunction LOGARITHM = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return Math.log(a);
		}
	};

	/** Function that returns <tt>Math.log(a) / Math.log(2)</tt>. */
	public static final DoubleFunction LOG2 = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return Math.log(a) * 1.4426950408889634;
		}
	};

	/** Function that returns <tt>-a</tt>. */
	public static final DoubleFunction NEGATE = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return -a;
		}
	};

	/** Function that returns <tt>Math.rint(a)</tt>. */
	public static final DoubleFunction RINT = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return Math.rint(a);
		}
	};

	/** Function that returns <tt>a < 0 ? -1 : a > 0 ? 1 : 0</tt>. */
	public static final DoubleFunction SIGN = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return a < 0 ? -1 : a > 0 ? 1 : 0;
		}
	};

	/** Function that returns <tt>Math.sin(a)</tt>. */
	public static final DoubleFunction SIN = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return Math.sin(a);
		}
	};

	/** Function that returns <tt>Math.sqrt(a)</tt>. */
	public static final DoubleFunction SQRT = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return Math.sqrt(a);
		}
	};

	/** Function that returns <tt>a * a</tt>. */
	public static final DoubleFunction SQUARE = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return a * a;
		}
	};

	/** Function that returns <tt> 1 / (1 + exp(-a) </tt> */
	public static final DoubleFunction SIGMOID = new DoubleFunction() {
		@Override
		public double apply(double a) {
			return 1.0 / (1.0 + Math.exp(-a));
		}
	};

	/** Function that returns <tt> a * (1-a) </tt> */
	public static final DoubleFunction SIGMOIDGRADIENT = new DoubleFunction() {
		@Override
		public double apply(double a) {
			return a * (1.0 - a);
		}
	};

	/** Function that returns <tt>Math.tan(a)</tt>. */
	public static final DoubleFunction TAN = new DoubleFunction() {

		@Override
		public double apply(double a) {
			return Math.tan(a);
		}
	};

	/** Function that returns <tt>Math.atan2(a,b)</tt>. */
	public static final DoubleDoubleFunction ATAN2 = new DoubleDoubleFunction() {

		@Override
		public double apply(double a, double b) {
			return Math.atan2(a, b);
		}
	};

	/** Function that returns <tt>a < b ? -1 : a > b ? 1 : 0</tt>. */
	public static final DoubleDoubleFunction COMPARE = new DoubleDoubleFunction() {

		@Override
		public double apply(double a, double b) {
			return a < b ? -1 : a > b ? 1 : 0;
		}
	};

	/** Function that returns <tt>a / b</tt>. */
	public static final DoubleDoubleFunction DIV = new DoubleDoubleFunction() {

		@Override
		public double apply(double a, double b) {
			return a / b;
		}

		/**
		 * x / 0 = infinity or undefined depending on x
		 * 
		 * @return true iff f(x, 0) = x for any x
		 */
		@Override
		public boolean isLikeRightPlus() {
			return false;
		}

		/**
		 * 0 / y = 0 unless y = 0
		 * 
		 * @return true iff f(0, y) = 0 for any y
		 */
		@Override
		public boolean isLikeLeftMult() {
			return false;
		}

		/**
		 * x / 0 = infinity or undefined depending on x
		 * 
		 * @return true iff f(x, 0) = 0 for any x
		 */
		@Override
		public boolean isLikeRightMult() {
			return false;
		}

		/**
		 * x / y != y / x
		 * 
		 * @return true iff f(x, y) = f(y, x) for any x, y
		 */
		@Override
		public boolean isCommutative() {
			return false;
		}

		/**
		 * x / (y / z) = x * z / y (x / y) / z = x / (y * z)
		 * 
		 * @return true iff f(x, f(y, z)) = f(f(x, y), z) for any x, y, z
		 */
		@Override
		public boolean isAssociative() {
			return false;
		}

	};

	/** Function that returns <tt>a == b ? 1 : 0</tt>. */
	public static final DoubleDoubleFunction EQUALS = new DoubleDoubleFunction() {

		@Override
		public double apply(double a, double b) {
			return a == b ? 1 : 0;
		}

		/**
		 * x = y iff y = x
		 * 
		 * @return true iff f(x, y) = f(y, x) for any x, y
		 */
		@Override
		public boolean isCommutative() {
			return true;
		}
	};

	/** Function that returns <tt>a > b ? 1 : 0</tt>. */
	public static final DoubleDoubleFunction GREATER = new DoubleDoubleFunction() {

		@Override
		public double apply(double a, double b) {
			return a > b ? 1 : 0;
		}
	};

	/** Function that returns <tt>Math.IEEEremainder(a,b)</tt>. */
	public static final DoubleDoubleFunction IEEE_REMAINDER = new DoubleDoubleFunction() {

		@Override
		public double apply(double a, double b) {
			return Math.IEEEremainder(a, b);
		}
	};

	/**
	 * Constructs a function that returns <tt>a < b ? 1 : 0</tt>. <tt>a</tt> is
	 * a variable, <tt>b</tt> is fixed.
	 */
	public static DoubleFunction less(final double b) {
		return new DoubleFunction() {

			@Override
			public double apply(double a) {
				return a < b ? 1 : 0;
			}
		};
	}

	/**
	 * Constructs a function that returns <tt><tt>Math.log(a) / Math.log(b)</tt>
	 * </tt>. <tt>a</tt> is a variable, <tt>b</tt> is fixed.
	 */
	public static DoubleFunction lg(final double b) {
		return new DoubleFunction() {
			private final double logInv = 1 / Math.log(b); // cached for speed

			@Override
			public double apply(double a) {
				return Math.log(a) * logInv;
			}
		};
	}

	/**
	 * Constructs a function that returns <tt>Math.max(a,b)</tt>. <tt>a</tt> is
	 * a variable, <tt>b</tt> is fixed.
	 */
	public static DoubleFunction max(final double b) {
		return new DoubleFunction() {

			@Override
			public double apply(double a) {
				return Math.max(a, b);
			}
		};
	}

	/**
	 * Constructs a function that returns <tt>Math.min(a,b)</tt>. <tt>a</tt> is
	 * a variable, <tt>b</tt> is fixed.
	 */
	public static DoubleFunction min(final double b) {
		return new DoubleFunction() {

			@Override
			public double apply(double a) {
				return Math.min(a, b);
			}
		};
	}

	/**
	 * Constructs a function that returns <tt>a % b</tt>. <tt>a</tt> is a
	 * variable, <tt>b</tt> is fixed.
	 */
	public static DoubleFunction mod(final double b) {
		return new DoubleFunction() {

			@Override
			public double apply(double a) {
				return a % b;
			}
		};
	}

	/**
	 * Constructs a function that returns <tt>a + b</tt>. <tt>a</tt> is a
	 * variable, <tt>b</tt> is fixed.
	 */
	public static DoubleFunction plus(final double b) {
		return new DoubleFunction() {

			@Override
			public double apply(double a) {
				return a + b;
			}
		};
	}

	/**
	 * Constructs a function that returns <tt>a - b</tt>. <tt>a</tt> is a
	 * variable, <tt>b</tt> is fixed.
	 */
	public static DoubleFunction minus(double b) {
		return plus(-b);
	}

	public static DoubleFunction multiply(final double b) {
		return new DoubleFunction() {

			@Override
			public double apply(double a) {
				return a * b;
			}
		};
	}
	
	public static DoubleFunction divide(final double b) {
		return new DoubleFunction() {

			@Override
			public double apply(double a) {
				return a / b;
			}
		};
	}

	public static DoubleDoubleFunction plus() {
		return new DoubleDoubleFunction() {

			@Override
			public double apply(double a, double b) {
				return a + b;
			}
		};
	}

	/**
	 * Constructs a function that returns <tt>Math.pow(a,b)</tt>. <tt>a</tt> is
	 * a variable, <tt>b</tt> is fixed.
	 */
	public static DoubleFunction pow(final double b) {
		return new DoubleFunction() {

			@Override
			public double apply(double a) {
				if (b == 2) {
					return a * a;
				} else {
					return Math.pow(a, b);
				}
			}
		};
	}

	/**
	 * Constructs a function that returns the number rounded to the given
	 * precision; <tt>Math.rint(a/precision)*precision</tt>. Examples:
	 * 
	 * <pre>
	 * precision = 0.01 rounds 0.012 --> 0.01, 0.018 --> 0.02
	 * precision = 10   rounds 123   --> 120 , 127   --> 130
	 * </pre>
	 */
	public static DoubleFunction round(final double precision) {
		return new DoubleFunction() {
			@Override
			public double apply(double a) {
				return Math.rint(a / precision) * precision;
			}
		};
	}

	/**
	 * Constructs a function that returns <tt>function.apply(b,a)</tt>, i.e.
	 * applies the function with the first operand as second operand and the
	 * second operand as first operand.
	 * 
	 * @param function
	 *            a function taking operands in the form
	 *            <tt>function.apply(a,b)</tt>.
	 * @return the binary function <tt>function(b,a)</tt>.
	 */
	public static DoubleDoubleFunction swapArgs(
			final DoubleDoubleFunction function) {
		return new DoubleDoubleFunction() {
			@Override
			public double apply(double a, double b) {
				return function.apply(b, a);
			}
		};
	}

	public static DoubleDoubleFunction minusAbsPow(final double exponent) {
		return new DoubleDoubleFunction() {
			@Override
			public double apply(double x, double y) {
				return Math.pow(Math.abs(x - y), exponent);
			}

			/**
			 * |x - 0|^p = |x|^p != x unless x > 0 and p = 1
			 * 
			 * @return true iff f(x, 0) = x for any x
			 */
			@Override
			public boolean isLikeRightPlus() {
				return false;
			}

			/**
			 * |0 - y|^p = |y|^p
			 * 
			 * @return true iff f(0, y) = 0 for any y
			 */
			@Override
			public boolean isLikeLeftMult() {
				return false;
			}

			/**
			 * |x - 0|^p = |x|^p
			 * 
			 * @return true iff f(x, 0) = 0 for any x
			 */
			@Override
			public boolean isLikeRightMult() {
				return false;
			}

			/**
			 * |x - y|^p = |y - x|^p
			 * 
			 * @return true iff f(x, y) = f(y, x) for any x, y
			 */
			@Override
			public boolean isCommutative() {
				return true;
			}

			/**
			 * |x - |y - z|^p|^p != ||x - y|^p - z|^p
			 * 
			 * @return true iff f(x, f(y, z)) = f(f(x, y), z) for any x, y, z
			 */
			@Override
			public boolean isAssociative() {
				return false;
			}
		};
	}
}
