package org.sentrysoftware.printf4j;

/*-
 * ╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲
 * Printf4J
 * ჻჻჻჻჻჻
 * Copyright 2023 Sentry Software
 * ჻჻჻჻჻჻
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱
 */

import java.io.PrintStream;
import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p><b>Printf4J</b></p>
 *
 * A utility class that provides the equivalent to C's *printf() functions.
 * <p>
 * Printf4J differs from {@link java.lang.String#format(String, Object...)}
 * in several ways:
 * <ul>
 * <li>Doesn't throw exceptions (fails silently)
 * <li>Support for 'askterisk' width and precision
 * <li>Support for precision in integers
 * <li>Support for zero padding in alternate formats
 * <li>and more!
 * </ul>
 */
public class Printf4J {

	/**
	 * No constructor
	 */
	protected Printf4J() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Regular expression matching with sprintf fields
	 * <ul>
	 * <li>group("percent"): %%
	 * <li>group("eol"): %n
	 * <li>group("specifier"): %-3.2g
	 * <li>group("options"): - (options)
	 * <li>group("width"): 3 (width)
	 * <li>group("precision"): 2 (precision)
	 * <li>group("other"): s (conversion type)
	 * <li>group("float"): g (conversion type)
	 * <li>group("int"): ld (conversion type)
	 * </ul>
	 */
	private static final Pattern PERCENT_PATTERN = Pattern.compile(
			"(?<percent>%%)|(?<eol>%n)|"
			+ "(?<specifier>%(?<options>[ \\-+(#0]*)"
			+ "(?<width>\\*|[1-9]+|(?:[1-9][0-9]+))?"
			+ "(?:\\.(?<precision>[0-9]+))?"
			+ "(?<other>[bBhHsScCtT]|"
			+ "(?:L?(?<float>[eEfFgGaA]))|"
			+ "(?:(?:hh|h|l|ll|j|z|t)?(?<int>[diuoxX]))))");


	/**
	 * Convert a String, Integer, or Double to Double.
	 *
	 * @param o Object to convert.
	 *
	 * @return the "double" value of o, or 0 if invalid
	 */
	public static double toDouble(final Object o) {
		if (o instanceof Number) {
			return ((Number) o).doubleValue();
		} else {
			// Try to convert the string to a number.
			// If failed, try with one character less.
			// 25fix will convert to 25 (any numeric prefix
			// will work)
			String s = o.toString();
			while (s.length() > 0) {
				try {
					return Double.parseDouble(s);
				} catch (NumberFormatException nfe) {
					s = s.substring(0, s.length() - 1);
				}

			}
			// Failed (not even with one char)
			return 0;
		}
	}

	/**
	 * Convert a String, Long, or Double to Long.
	 *
	 * @param o Object to convert.
	 *
	 * @return the "long" value of o, or 0 if invalid
	 */
	public static long toLong(final Object o) {
		if (o instanceof Number) {
			return ((Number)o).longValue();
		} else {
			try {
				return Long.parseLong(o.toString());
			} catch (NumberFormatException nfe) {
				return 0;
			}
		}
	}


	/**
	 * Applies a format string to a set of parameters and
	 * returns the formatted result.
	 * String.format() is adapted to behave like C's sprintf()
	 *
	 * @param locale a {@link java.util.Locale} object
	 * @param format The format string to apply.
	 * @param arr Arguments to format.
	 * @return The formatted string
	 */
	public static String sprintf(final Locale locale, final String format, final Object... arr) {

		// We're processing each format specifier (%d, %-12s, etc.) and will slightly
		// adapt the behavior of Java's Formatter to be like C's sprintf()
		Matcher percentMatcher = PERCENT_PATTERN.matcher(format);

		// Result will be hold in a StringBuilder
		StringBuilder formatResultBuilder = new StringBuilder();

		// We will use a single Formatter instance (closeable)
		try (Formatter formatter = new Formatter(formatResultBuilder, locale)) {

			// Each argument is indexed and will match with the format specifiers we will find
			int argumentIndex = 0;

			// Loop through each matching format specifier
			while (percentMatcher.find()) {

				// Easy: %% -> %
				if (percentMatcher.group("percent") != null) {
					percentMatcher.appendReplacement(formatResultBuilder, "%");
					continue;
				}

				// Easy: %n -> \n (always \n, and not system's EOL
				if (percentMatcher.group("eol") != null) {
					percentMatcher.appendReplacement(formatResultBuilder, "\n");
					continue;
				}

				// Extract the properties of the format specifier from the regex pattern
				String formatSpecifier = percentMatcher.group("specifier");
				String options = percentMatcher.group("options");
				String width = percentMatcher.group("width");
				String precision = percentMatcher.group("precision");
				String conversion = percentMatcher.group("int");
				String prefix = "";
				if (conversion == null) {
					conversion = percentMatcher.group("float");
					if (conversion == null) {
						conversion = percentMatcher.group("other");
					}
				}

				// Dummy case: we don't have enough arguments to fill the format specifiers!
				if (argumentIndex >= arr.length) {
					// Simply append the format specifier "as is"
					// We don't skip directly to the end, because we need to process
					// remaining %% and %n
					percentMatcher.appendReplacement(formatResultBuilder, formatSpecifier);
					continue;
				}

				// Unlikely case: a match, but no specifier
				if (formatSpecifier == null) {
					continue;
				}

				// Some necessary adjustments to behave like C's sprintf()
				if (options != null) {
					// '+' is not supported for %x %o %c %s and %u
					if (options.indexOf('+') > -1 && "xXocsu".contains(conversion)) {
						options = options.replace("+", "");
					}
					// ' ' is not supported for %x %o %c %s and %u
					if (options.indexOf(' ') > -1 && "xXocsu".contains(conversion)) {
						options = options.replace(" ", "");
					}
					// '0' is not supported without a width
					if (width == null) {
						if (options.indexOf('0') > -1) {
							options = options.replace("0", "");
						}
						// '-' is not supported without a width
						if (options.indexOf('-') > -1) {
							options = options.replace("-", "");
						}
					}
					// '0' is not supported with '-'
					if (options.indexOf('0') > -1 && options.indexOf('-') > -1) {
						options = options.replace("0", "");
					}
					// '0' is not supported with '#'
					if (options.indexOf('#') > -1) {
						options = options.replace("#", "");
						if ("x".equals(conversion)) {
							prefix = "0x";
						} else if ("X".equals(conversion)) {
							prefix = "0X";
						} else if ("o".equals(conversion)) {
							prefix = "0";
						}
					}
				}
				if ("i".equals(conversion)) {
					// %i -> %d
					conversion = "d";
				} else if ("u".equals(conversion)) {
					// %u -> %d, and remove the '+' flag, which is useless for unsigned ints
					conversion = "d";
					if (options != null) {
						options = options.replace("+", "");
					}
				} else if ("xXocs".contains(conversion)) {
					// Remove the offending '+' option for %x %X %o %s and %c
					options = options.replace("+", "");
				}
				// Zero width is to be discarded
				if ("0".equals(width)) {
					width = null;
				}
				// '*' width uses an argument
				if ("*".equals(width)) {
					width = Integer.valueOf((int)arr[argumentIndex]).toString();
					argumentIndex++;
				}
				// If width is specified, reduce it by the length of the prefix
				if (width != null && prefix != null) {
					int newWidth = Integer.valueOf(width) - prefix.length();
					if (newWidth < 0) {
						newWidth = 0;
					}
					width = String.valueOf(newWidth);
				}

				// Recheck if we're running out of arguments
				if (argumentIndex >= arr.length) {
					// Simply append the format specifier "as is"
					// We don't skip directly to the end, because we need to process
					// remaining %% and %n
					percentMatcher.appendReplacement(formatResultBuilder, formatSpecifier);
					continue;
				}

				// Reconstruct the formatSpecifier
				StringBuilder newFormatSpecifierBuilder = new StringBuilder(prefix);
				newFormatSpecifierBuilder.append('%');
				if (options != null) { newFormatSpecifierBuilder.append(options); }
				if (width != null) { newFormatSpecifierBuilder.append(width); }
				if (precision != null) { newFormatSpecifierBuilder.append('.').append(precision); }
				newFormatSpecifierBuilder.append(conversion);
				formatSpecifier = newFormatSpecifierBuilder.toString();

				// The logic will differ for each conversion type
				switch (conversion) {

				// %s: simple
				case "s":
					percentMatcher.appendReplacement(formatResultBuilder, "");
					formatter.format(formatSpecifier, arr[argumentIndex]);
					break;

				// Integers: a bit more complicated!
				case "d":
				case "x":
				case "X":
				case "o":
					if (precision == null) {
						// If precision is not specified, Java's formatter works pretty much
						// just like C's sprintf().
						// We only need to make sure an integer is passed
						// Example: %+03d
						percentMatcher.appendReplacement(formatResultBuilder, "");
						formatter.format(formatSpecifier, toLong(arr[argumentIndex]));
						break;

					} else {
						// If precision is specified, we need to process this case manually
						// because Java's formatter doesn't support specifying precision for
						// an integer (it will simply add zeroes to the left!)
						if (width == null) {
							// 2 sub-cases: if width is not specified, we will simply makes sure
							// zeroes are padded to the left
							// Example: %(.3d
							// But first: stupid case with precision zero: return empty string
							if ("0".equals(precision)) {
								percentMatcher.appendReplacement(formatResultBuilder, "");
								break;
							}
							StringBuilder integerFormatStringBuilder = new StringBuilder(prefix);
							integerFormatStringBuilder.append('%');
							if (options != null) { integerFormatStringBuilder.append(options); }
							if (options == null || !options.contains("0") && !precision.startsWith("0")) {
								integerFormatStringBuilder.append("0");
							}
							// For negative number, increase precision by 1, because it's going to be used
							// as width
							if ("d".equals(conversion)
								&& (toLong(arr[argumentIndex]) < 0
									|| options != null && options.contains("+")))
							{
								precision = Integer.toString(Integer.valueOf(precision) + 1);
							}
							integerFormatStringBuilder.append(precision);
							integerFormatStringBuilder.append(conversion);
							percentMatcher.appendReplacement(formatResultBuilder, "");
							formatter.format(integerFormatStringBuilder.toString(), toLong(arr[argumentIndex]));
							break;
						}

						// If width is specified, we will go through 2 formatter calls
						// 1. %0<n>d with the precision n
						// 2. %<n>s with the width
						StringBuilder integerFormatStringBuilder = new StringBuilder(prefix);
						integerFormatStringBuilder.append('%');
						if (options != null) { integerFormatStringBuilder.append(options); }
						if (options == null || !options.contains("0") && !precision.startsWith("0")) {
							integerFormatStringBuilder.append("0");
						}
						integerFormatStringBuilder.append(precision);
						integerFormatStringBuilder.append(conversion);
						percentMatcher.appendReplacement(formatResultBuilder, "");
						String renderedPrecision = String.format(integerFormatStringBuilder.toString(), toLong(arr[argumentIndex]));
						formatter.format("%" + width + "s", renderedPrecision);
					}
					break;

				// Float and double: simple, we just need to make sure to pass a double
				case "e":
				case "E":
				case "f":
				case "a":
				case "A":
					percentMatcher.appendReplacement(formatResultBuilder, "");
					formatter.format(formatSpecifier, toDouble(arr[argumentIndex]));
					break;

				// %g is a bit tricky: if the result of the formatting is a simple decimal notation
				// we need to remove the trailing zeroes that Java adds for nothing (like 2.00000)
				case "g":
				case "G":
					String tempFormatResult = String.format(locale, formatSpecifier, toDouble(arr[argumentIndex]));
					if ((tempFormatResult.indexOf('.') > -1 || tempFormatResult.indexOf(',') > -1) && tempFormatResult.indexOf('e') == -1 && tempFormatResult.indexOf('E') == -1) {
						while (tempFormatResult.endsWith("0")) {
							tempFormatResult = tempFormatResult.substring(0, tempFormatResult.length() - 1);
						}
						if (tempFormatResult.endsWith(".") || tempFormatResult.endsWith(",")) {
							tempFormatResult = tempFormatResult.substring(0, tempFormatResult.length() - 1);
						}
					}
					percentMatcher.appendReplacement(formatResultBuilder, tempFormatResult);
					break;

				default:
					percentMatcher.appendReplacement(formatResultBuilder, "");
					formatter.format(formatSpecifier, arr[argumentIndex]);
				}

				// Pass to the next argument (for the next format specifier we'll find)
				argumentIndex++;
			}
		} catch (IllegalFormatException e) { /* do nothing */ }

		// Now append the rest that didn't match our pattern
		percentMatcher.appendTail(formatResultBuilder);

		// The result!
		return formatResultBuilder.toString();

	}

	/**
	 * Applies a format string to a set of parameters and
	 * returns the formatted result.
	 * String.format() is adapted to behave like C's sprintf()
	 *
	 * @param format The format string to apply.
	 * @param arr Arguments to format.
	 * @return The formatted string
	 */
	public static String sprintf(final String format, final Object... arr) {
		return sprintf(Locale.ENGLISH, format, arr);
	}

	/**
	 * Applies a format string to a set of parameters and
	 * prints the formatted result to System.out.
	 * String.format() is adapted to behave like C's sprintf()
	 *
	 * @param locale a {@link java.util.Locale} object
	 * @param format The format string to apply.
	 * @param arr Arguments to format.
	 */
	public static void printf(final Locale locale, final String format, final Object... arr) {
		System.out.print(sprintf(locale, format, arr));
	}

	/**
	 * Applies a format string to a set of parameters and
	 * prints the formatted result to the specified PrintStream.
	 * String.format() is adapted to behave like C's sprintf()
	 *
	 * @param ps PrintStream to print to
	 * @param locale a {@link java.util.Locale} object
	 * @param format The format string to apply.
	 * @param arr Arguments to format.
	 */
	public static void printf(final PrintStream ps, final Locale locale, final String format, final Object... arr) {
		ps.print(sprintf(locale, format, arr));
	}


}
