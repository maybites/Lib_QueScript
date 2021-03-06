/*
 * Copyright 2015/2016 Martin Fröhlich
 * Copyright 2012/2015 Udo Klimaschewski
 * 
 * http://maybites.ch
 * http://UdoJava.com/
 * http://about.me/udo.klimaschewski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */

package ch.maybites.quescript.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import ch.maybites.quescript.expression.RunTimeEnvironment.Function;
import ch.maybites.quescript.expression.RunTimeEnvironment.Operator;

/**
 * <h1>EvalEx - Java Expression Evaluator</h1>
 * 
 * <h2>Introduction</h2> EvalEx is a handy expression evaluator for Java, that
 * allows to evaluate simple mathematical and boolean expressions. <br>
 * Key Features:
 * <ul>
 * <li>Uses ExpressionVar for calculation and result</li>
 * <li>Three class implementation, very compact</li>
 * <li>No dependencies to external libraries</li>
 * <li>Supports variables</li>
 * <li>Supports strings</li>
 * <li>Standard boolean and mathematical operators</li>
 * <li>Standard basic mathematical and boolean functions</li>
 * </ul>
 * <br>
 * <h2>Examples</h2>
 * 
 * <pre>
 *  ExpressionVar result = null;
 *  
 *  RunTimeEnvironment rt = new RunTimeEnvironment();
 *  
 *  Expression expression = new Expression("1+1/3");
 *  result = expression.parse(rt):
 *  
 *  System.out.printlnt("result = " + result.eval());
 *  
 *  result = new Expression("(3.4 + -4.1)/2").parse(rt);
 *  
 *  ExpressionVar a = new ExpressionVar("2.4");
 *  ExpressionVar b = new ExpressionVar("9.235");
 *  
 *  rt.addPublicVariable("a", a);
 *  rt.addPublicVariable("b", b);
 *  
 *  result = new Expression("SQRT(a^2 + b^2").parse(rt);
 *  
 *  System.out.printlnt("result = " + result.eval());
 *  
 *  result = new Expression("2.4/PI").parse(rt);
 *  
 *  System.out.printlnt("result = " + result.eval());
 *  
 *  result = new Expression("random() &gt; 0.5").parse(rt);
 *  
 *  System.out.printlnt("result = " + result.eval());
 *  
 *  ExpressionVar x = new ExpressionVar("22.9");
 *  rt.addPublicVariable("x", x);
 *  
 *  result = new Expression("not(x &lt; 7 || sqrt(max(x,9)) &lt;= 3))").parse(rt);
 *  
 *  System.out.printlnt("result = " + result.eval());
 *  
 * </pre>
 * 
 * <br>
 * <h2>Supported Operators</h2>
 * <table>
 * <tr>
 * <th>Mathematical Operators</th>
 * </tr>
 * <tr>
 * <th>Operator</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>+</td>
 * <td>Additive operator</td>
 * </tr>
 * <tr>
 * <td>-</td>
 * <td>Subtraction operator</td>
 * </tr>
 * <tr>
 * <td>*</td>
 * <td>Multiplication operator</td>
 * </tr>
 * <tr>
 * <td>/</td>
 * <td>Division operator</td>
 * </tr>
 * <tr>
 * <td>%</td>
 * <td>Remainder operator (Modulo)</td>
 * </tr>
 * <tr>
 * <td>^</td>
 * <td>Power operator</td>
 * </tr>
 * </table>
 * <br>
 * <table>
 * <tr>
 * <th>Boolean Operators<sup>*</sup></th>
 * </tr>
 * <tr>
 * <th>Operator</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>=</td>
 * <td>Equals</td>
 * </tr>
 * <tr>
 * <td>==</td>
 * <td>Equals</td>
 * </tr>
 * <tr>
 * <td>!=</td>
 * <td>Not equals</td>
 * </tr>
 * <tr>
 * <td>&lt;&gt;</td>
 * <td>Not equals</td>
 * </tr>
 * <tr>
 * <td>&lt;</td>
 * <td>Less than</td>
 * </tr>
 * <tr>
 * <td>&lt;=</td>
 * <td>Less than or equal to</td>
 * </tr>
 * <tr>
 * <td>&gt;</td>
 * <td>Greater than</td>
 * </tr>
 * <tr>
 * <td>&gt;=</td>
 * <td>Greater than or equal to</td>
 * </tr>
 * <tr>
 * <td>&amp;&amp;</td>
 * <td>Boolean and</td>
 * </tr>
 * <tr>
 * <td>||</td>
 * <td>Boolean or</td>
 * </tr>
 * </table>
 * *Boolean operators result always in a ExpressionVar value of 1 or 0 (zero). Any
 * non-zero value is treated as a _true_ value. Boolean _not_ is implemented by
 * a function. <br>
 * <h2>Supported Functions</h2>
 * <table>
 * <tr>
 * <th>Function<sup>*</sup></th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>NOT(<i>expression</i>)</td>
 * <td>Boolean negation, 1 (means true) if the expression is not zero</td>
 * </tr>
 * <tr>
 * <td>IF(<i>condition</i>,<i>value_if_true</i>,<i>value_if_false</i>)</td>
 * <td>Returns one value if the condition evaluates to true or the other if it
 * evaluates to false</td>
 * </tr>
 * <tr>
 * <td>RANDOM()</td>
 * <td>Produces a random number between 0 and 1</td>
 * </tr>
 * <tr>
 * <td>MIN(<i>e1</i>,<i>e2</i>, <i>...</i>)</td>
 * <td>Returns the smallest of the given expressions</td>
 * </tr>
 * <tr>
 * <td>MAX(<i>e1</i>,<i>e2</i>, <i>...</i>)</td>
 * <td>Returns the biggest of the given expressions</td>
 * </tr>
 * <tr>
 * <td>ABS(<i>expression</i>)</td>
 * <td>Returns the absolute (non-negative) value of the expression</td>
 * </tr>
 * <tr>
 * <td>ROUND(<i>expression</i>)</td>
 * <td>Rounds a value</td>
 * </tr>
 * <tr>
 * <td>FLOOR(<i>expression</i>)</td>
 * <td>Rounds the value down to the nearest integer</td>
 * </tr>
 * <tr>
 * <td>CEILING(<i>expression</i>)</td>
 * <td>Rounds the value up to the nearest integer</td>
 * </tr>
 * <tr>
 * <td>LOG(<i>expression</i>)</td>
 * <td>Returns the natural logarithm (base e) of an expression</td>
 * </tr>
 * <tr>
 * <td>LOG10(<i>expression</i>)</td>
 * <td>Returns the common logarithm (base 10) of an expression</td>
 * </tr>
 * <tr>
 * <td>SQRT(<i>expression</i>)</td>
 * <td>Returns the square root of an expression</td>
 * </tr>
 * <tr>
 * <td>SIN(<i>expression</i>)</td>
 * <td>Returns the trigonometric sine of an angle (in radians)</td>
 * </tr>
 * <tr>
 * <td>COS(<i>expression</i>)</td>
 * <td>Returns the trigonometric cosine of an angle (in radians)</td>
 * </tr>
 * <tr>
 * <td>TAN(<i>expression</i>)</td>
 * <td>Returns the trigonometric tangens of an angle (in radians)</td>
 * </tr>
 * <tr>
 * <td>ASIN(<i>expression</i>)</td>
 * <td>Returns the angle of asin (in radians)</td>
 * </tr>
 * <tr>
 * <td>ACOS(<i>expression</i>)</td>
 * <td>Returns the angle of acos (in radians)</td>
 * </tr>
 * <tr>
 * <td>ATAN(<i>expression</i>)</td>
 * <td>Returns the angle of atan (in radians)</td>
 * </tr>
 * <tr>
 * <td>SINH(<i>expression</i>)</td>
 * <td>Returns the hyperbolic sine of a value</td>
 * </tr>
 * <tr>
 * <td>COSH(<i>expression</i>)</td>
 * <td>Returns the hyperbolic cosine of a value</td>
 * </tr>
 * <tr>
 * <td>TANH(<i>expression</i>)</td>
 * <td>Returns the hyperbolic tangens of a value</td>
 * </tr>
 * <tr>
 * <td>RAD(<i>expression</i>)</td>
 * <td>Converts an angle measured in radians to an approximately equivalent
 * angle measured in radians</td>
 * </tr>
 * <tr>
 * <td>DEG(<i>expression</i>)</td>
 * <td>Converts an angle measured in radians to an approximately equivalent
 * angle measured in radians</td>
 * </tr>
 * </table>
 * *Functions names are case insensitive. <br>
 * <h2>Supported Constants</h2>
 * <table>
 * <tr>
 * <th>Constant</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>PI</td>
 * <td>The value of <i>PI</i>, exact to 100 digits</td>
 * </tr>
 * <tr>
 * <td>TRUE</td>
 * <td>The value one</td>
 * </tr>
 * <tr>
 * <td>FALSE</td>
 * <td>The value zero</td>
 * </tr>
 * </table>
 * 

 * 
 * The software is licensed under the MIT Open Source license (see LICENSE
 * file). <br>
 * <ul>
 * <li>The *power of* operator (^) implementation was copied from [Stack
 * Overflow
 * ](http://stackoverflow.com/questions/3579779/how-to-do-a-fractional-power
 * -on-ExpressionVar-in-java) Thanks to Gene Marin</li>
 * <li>The SQRT() function implementation was taken from the book [The Java
 * Programmers Guide To numerical
 * Computing](http://www.amazon.de/Java-Number-Cruncher
 * -Programmers-Numerical/dp/0130460419) (Ronald Mak, 2002)</li>
 * </ul>
 * 
 * @author Udo Klimaschewski (http://about.me/udo.klimaschewski)
 * @author Martin Fröhlich (http://maybites.ch)
 */
public class Expression {

	/**
	 * The original infix expression.
	 */
	private String expression = null;

	/**
	 * 
	 */
	private String infoString = "";
	
	/**
	 * The cached RPN (Reverse Polish Notation) of the expression.
	 */
	private List<String> rpn = null;

	/**
	 * The ExpressionVar representation of the left parenthesis, 
	 * used for parsing varying numbers of function parameters.
	 */
	private static final ExpressionVar PARAMS_START = new ExpressionVar(0);

	/**
	 * What character to use for decimal separators.
	 */
	private static final char decimalSeparator = '.';

	/**
	 * What character to use for minus sign (negative values).
	 */
	private static final char minusSign = '-';

	/**
	 * Creates a new expression instance from an expression string with a given
	 * default match context.
	 * 
	 * @param expression
	 *            The expression. E.g. <code>"2.4*sin(3)/(2-4)"</code> or
	 *            <code>"sin(y)&gt;0 & max(z, 3)&gt;3"</code>
	 */
	public Expression(String expression) throws ExpressionException {
		this.expression = expression;
	}

	/**
	 * Creates a new expression instance from an expression string with a given
	 * default match context.
	 * 
	 * @param expression
	 * @param trimStart the characters at the beginning of the expression to be cut away (if they exist)
	 * @param trimEnd the characters at the end of the expression to be cut away (if they exist)
	 * @throws ExpressionException
	 */
	public Expression(String expression, String trimStart, String trimEnd) throws ExpressionException {
		if(expression.startsWith(trimStart))
			expression = expression.substring(trimStart.length());
		if(expression.endsWith(trimEnd))
			expression = expression.substring(0, expression.length() - trimEnd.length());
		this.expression = expression;
	}
	
	/**
	 * Allows to enter some more infos about the expression. ie. in which line of the script
	 * it is located. Usefull for debugging purposes.
	 * 
	 * @param infoString
	 * @return
	 */
	public Expression setInfo(String infoString){
		this.infoString = infoString;
		return this;
	}

	/**
	 * Is the string a number?
	 * 
	 * @param st
	 *            The string.
	 * @return <code>true</code>, if the input string is a number.
	 */
	private boolean isNumber(String st) {
		if (st.charAt(0) == minusSign && st.length() == 1) return false;
		if (st.charAt(0) == '+' && st.length() == 1) return false;
		if (st.charAt(0) == 'e' ||  st.charAt(0) == 'E') return false;
		for (char ch : st.toCharArray()) {
			if (!Character.isDigit(ch) && ch != minusSign
					&& ch != decimalSeparator
                                        && ch != 'e' && ch != 'E' && ch != '+')
				return false;
		}
		return true;
	}

	/**
	 * Implementation of the <i>Shunting Yard</i> algorithm to transform an
	 * infix expression to a RPN expression.
	 * 
	 * @param expression
	 *            The input expression in infx.
	 * @return A RPN representation of the expression, with each token as a list
	 *         member.
	 * @throws ExpressionException 
	 */
	private List<String> shuntingYard(RunTimeEnvironment rt) throws ExpressionException {
		List<String> outputQueue = new ArrayList<String>();
		Stack<String> stack = new Stack<String>();

		Tokenizer tokenizer = new Tokenizer(expression);

		String lastFunction = null;
		String previousToken = null;
		while (tokenizer.hasNext()) {
			String token = tokenizer.next(rt);
			if (isNumber(token)) {
				outputQueue.add(token);
			} else if (rt.staticVars.containsKey(token)) {
				outputQueue.add(token);
			} else if (rt.functions.containsKey(token.toUpperCase(Locale.ROOT))) {
				stack.push(token);
				lastFunction = token;
			//} else if (Character.isLetter(token.charAt(0))) {
			//	stack.push(token);
			} else if (",".equals(token)) {
				while (!stack.isEmpty() && !"(".equals(stack.peek())) {
					outputQueue.add(stack.pop());
				}
				if (stack.isEmpty()) {
					throw new ExpressionException(createError("Parse error for function '"
							+ lastFunction + "'", tokenizer.getPos()));
				}
			} else if (rt.operators.containsKey(token)) {
				Operator o1 = rt.operators.get(token);
				String token2 = stack.isEmpty() ? null : stack.peek();
				while (rt.operators.containsKey(token2)
						&& ((o1.isLeftAssoc() && o1.getPrecedence() <= rt.operators
								.get(token2).getPrecedence()) || (o1
								.getPrecedence() < rt.operators.get(token2)
								.getPrecedence()))) {
					outputQueue.add(stack.pop());
					token2 = stack.isEmpty() ? null : stack.peek();
				}
				stack.push(token);
			} else if ("(".equals(token)) {
				if (previousToken != null) {
					if (isNumber(previousToken)) {
						throw new ExpressionException(createError("Missing operator", tokenizer.getPos()));
					}
					// if the ( is preceded by a valid function, then it
					// denotes the start of a parameter list
					if (rt.functions.containsKey(previousToken.toUpperCase(Locale.ROOT))) {
						outputQueue.add(token);
					}
				}
				stack.push(token);
			} else if (")".equals(token)) {
				while (!stack.isEmpty() && !"(".equals(stack.peek())) {
					outputQueue.add(stack.pop());
				}
				if (stack.isEmpty()) {
					throw new ExpressionException(createError("Mismatched parentheses - missing opening '('", tokenizer.getPos()));
				}
				stack.pop();
				if (!stack.isEmpty()
						&& rt.functions.containsKey(stack.peek().toUpperCase(
								Locale.ROOT))) {
					outputQueue.add(stack.pop());
				}
			} else if ("[".equals(token)) {
				if (previousToken != null) {
					if (isNumber(previousToken)) {
						throw new ExpressionException(createError("Array perenthesis [ is missing variable name", tokenizer.getPos()));
					}
					// if the [ is preceded by a valid variable, then it
					// denotes the start of an operator list
					if (!rt.containsVar(previousToken)) {
						throw new ExpressionException(createError("Array perenthesis [ is missing variable name", tokenizer.getPos()));						
					}
				}
				stack.push(token);
			} else if ("]".equals(token)) {
				while (!stack.isEmpty() && !"[".equals(stack.peek())) {
					outputQueue.add(stack.pop());
				}
				if (stack.isEmpty()) {
					throw new ExpressionException(createError("Mismatched parentheses - missing opening '['", tokenizer.getPos()));
				}
				stack.pop();
				outputQueue.add("[]");
				// the array parantheses are turned into an operand 
				// with the variable and the index as parameter
			} else if (token.startsWith("'") && token.endsWith("'")) {
				if (previousToken != null && !previousToken.equals("(") && !previousToken.equals(",")) {
					if (!rt.operators.containsKey(previousToken)) {
						throw new ExpressionException(createError("Missing operator", tokenizer.getPos() - token.length()));
					}
				}
				// take the string from the stack and put it into the outputQue
				outputQueue.add(token);
			}	else {
				// its a variable that can be not set
				outputQueue.add(token);
			} 
			previousToken = token;
		}
		while (!stack.isEmpty()) {
			String element = stack.pop();
			if ("(".equals(element) || ")".equals(element)) {
				throw new ExpressionException(
						"Mismatched parentheses - missing closing ) \n" + "{"+expression+"}" + infoString);
			}
			if ("[".equals(element) || "]".equals(element)) {
				throw new ExpressionException(
						"Mismatched parentheses - missing closing ] \n" + "{"+expression+"}" + infoString);
			}
			if ("'".equals(element)) {
				throw new ExpressionException(
						"Mismatched string - Missing text delimiter ' \n" + "{"+expression+"}" + infoString);
			}
			if(element.startsWith("'") && element.endsWith("'")){
				outputQueue.add(element);
			} else if (!rt.operators.containsKey(element)) {
				throw new ExpressionException(
						"Unknown operator or function: '"
								+ element + "' \n" + "{"+expression+"}" + infoString);
			} else {
				outputQueue.add(element);
			}
		}
		return outputQueue;
	}

	/**
	 * Check that the expression have enough numbers and variables to fit the 
	 * requirements of the operators and functions, also check 
	 * for only 1 result stored at the end of the evaluation.  
	 * @throws ExpressionException 
	 *
	 */
	private void validate(List<String> rpn, RunTimeEnvironment rt) throws ExpressionException {
		/*- 
		* Thanks to Norman Ramsey:
		* http://http://stackoverflow.com/questions/789847/postfix-notation-validation
		*/
		int counter = 0;
		Stack<Integer> params = new Stack<Integer>();
		for (String token : rpn) {
			if ("(".equals(token)) {
				// is this a nested function call?
				if (!params.isEmpty()) {
					// increment the current function's param count
					// (the return of the nested function call
					// will be a parameter for the current function)
					params.set(params.size() - 1, params.peek() + 1);
				}
				// start a new parameter count
				params.push(0);
			} else if ("[".equals(token)) {
				// is this a nested function call?
				if (!params.isEmpty()) {
					// increment the current function's param count
					// (the return of the nested function call
					// will be a parameter for the current function)
					params.set(params.size() - 1, params.peek() + 1);
				}
				// start a new parameter count
				params.push(0);
			} else if (!params.isEmpty()) {
				if (rt.functions.containsKey(token.toUpperCase(Locale.ROOT))) {
					// remove the parameters and the ( from the counter
					counter -= params.pop() + 1;
				} else {
					// increment the current function's param count
					params.set(params.size() - 1, params.peek() + 1);
				}
			} else if (rt.operators.containsKey(token)) {
				//we only have binary operators
				counter -= 2;
			}
			if (counter < 0) {
				throw new ExpressionException("Too many operators or functions at: "
					+ token + " | " +"{"+expression+"}" + infoString);
			}
			counter++;
		}
		if (counter > 1) {
			throw new ExpressionException("Too many numbers or variables | " +"{"+expression+"}" + infoString);
		} else if (counter < 1) {
			throw new ExpressionException("Empty expression | " +"{"+expression+"}" + infoString);
		}
	}


	/**
	 * Parses the Expression with the provided RunTimeEnvironment and
	 * returns an ExpressionVar that contains an ExpressionTree.
	 * In order to get the result of the Expression, you need to 
	 * use the eval() method of the returned Object.
	 * @param rt
	 * @return a ExpressionVar that contains an ExpressionTree.
	 * @throws ExpressionException
	 */
	public ExpressionVar parse(RunTimeEnvironment rt) throws ExpressionException {
		rpn = shuntingYard(rt);
		validate(rpn, rt);

		Stack<ExpressionVar> stack = new Stack<ExpressionVar>();

		int scopeToken = 0;
				
		for (String token : rpn) {
			// the scope token are the two semicolon in front of the variable
			scopeToken = hasScopePrefix(token);
			if(scopeToken > 0){
				token = token.substring(scopeToken);
			}
			if (rt.operators.containsKey(token)) {
				ArrayList<ExpressionVar> p = new ArrayList<ExpressionVar>();
				ExpressionVar v1 = stack.pop();
				ExpressionVar v2 = stack.pop();
				p.add(v2);
				p.add(v1);
				stack.push(new ExpressionVar(rt.operators.get(token), p));
			} else if (rt.functions.containsKey(token.toUpperCase(Locale.ROOT))) {
				if(scopeToken == 0){
					Function f = rt.functions.get(token.toUpperCase(Locale.ROOT));
					ArrayList<ExpressionVar> p = new ArrayList<ExpressionVar>(
							!f.numParamsVaries() ? f.getNumParams() : 0);
					// pop parameters off the stack until we hit the start of 
					// this function's parameter list
					while (!stack.isEmpty() && stack.peek() != PARAMS_START) {
						p.add(0, stack.pop());
					}
					if (stack.peek() == PARAMS_START) {
						stack.pop();
					}
					if (!f.numParamsVaries() && p.size() != f.getNumParams()) {
						throw new ExpressionException("Function " + token + " expected " + f.getNumParams() + " parameters, got " + p.size() + " | " +"{"+expression+"}" + infoString);
					}
					stack.push(new ExpressionVar(f, p));
				} else {
					// if the token has a scopeToken in front, it is meant to be a variable, but
					// it matches a function and that is not allowed.
					throw new ExpressionException("Variable name invalid: Matches a function: " + token + "() | {"+expression+"}" + infoString);
				}
			} else if ("(".equals(token)) {
				stack.push(PARAMS_START);
			} else if (token.startsWith("'")) { 
				// its a string
				stack.push(new ExpressionVar(token.substring(1, token.length() - 1)));		
			} else if(isNumber(token)){
				// its a number
				stack.add(new ExpressionVar(token));
			} else if (rt.staticVars.containsKey(token)) {
				stack.push(rt.staticVars.get(token));
			} else if (rt.containsVar(token)) {
				if(scopeToken == 2){
					// the :: forces to create / overwrite a variable in local scope, no
					// matter if there is a variable in a higher scope
					
					// we first set a variable
					rt.setLocalVariable(token, new ExpressionVar());	
					// we need to get the reference of it if, since if it already existed, the old
					// reference is what we want.
					stack.push(rt.getLocalVar(token));					
				} else {
					// we take the variable found in whatever scope
					stack.push(rt.getVar(token));
				}
			} else {
				// its variable cannot be found in any scope yet.
				// if a scope token is set, we create one
				if(scopeToken > 0){
					// the : or the :: forces to create new variable inside the local scope
					ExpressionVar newvar = new ExpressionVar();
					rt.setLocalVariable(token, newvar);					
					stack.add(newvar);
				} else {
					throw new ExpressionException("Variable '" + token + "' not declared | " +"{"+expression+"}" + infoString);
				}
			}
		}
		return stack.pop().setExpression("{"+expression+"}" + infoString);
	}

	private int hasScopePrefix(String var){
		if(var.startsWith("::")){
			return 2;
		} else if (var.startsWith(":")){
			return 1;
		}
		return 0;
	}

	/**
	 * Get a string representation of the RPN (Reverse Polish Notation) for this
	 * expression.
	 * 
	 * @return A string with the RPN representation for this expression.
	 * @throws ExpressionException if the expression has not been parsed
	 *  with a RunTimeEnvironment beforehand
	 */
	public String toRPN() throws ExpressionException {
		StringBuilder result = new StringBuilder();
		if(rpn != null){
			for (String st : rpn) {
				if (result.length() != 0)
					result.append(" ");
				result.append(st);
			}
		} else {
			throw new ExpressionException("Expression not parsed yet: " + "{"+expression+"}"  + infoString);
		}
		return result.toString();
	}

	/**
	 * The expression evaluators exception class.
	 */
	public static class ExpressionException extends Exception {
		private static final long serialVersionUID = 1118142866870779047L;

		public ExpressionException(String message) {
			super(message);
		}
	}

	/**
	 * Expression tokenizer that allows to iterate over a {@link String}
	 * expression token by token. Blank characters will be skipped.
	 */
	private class Tokenizer {

		/**
		 * Actual position in expression string.
		 */
		private int pos = 0;

		/**
		 * The original input expression.
		 */
		private String input;
		/**
		 * The previous token or <code>null</code> if none.
		 */
		private String previousToken;
		
		/**
		 * Creates a new tokenizer for an expression.
		 * 
		 * @param input
		 *            The expression string.
		 */
		public Tokenizer(String input) {
			this.input = input.trim();
		}

		public boolean hasNext(){
			return (pos < input.length());
		}

		/**
		 * Peek at the next character, without advancing the iterator.
		 * 
		 * @return The next character or character 0, if at end of string.
		 */
		private char peekNextChar() {
			if (pos < (input.length() - 1)) {
				return input.charAt(pos + 1);
			} else {
				return 0;
			}
		}

		public String next(RunTimeEnvironment rt) throws ExpressionException{
			StringBuilder token = new StringBuilder();
			if (pos >= input.length()) {
				return previousToken = null;
			}
			char ch = input.charAt(pos);
			// ignore whitespaces (that are not inside a string)
			while (Character.isWhitespace(ch) && pos < input.length()) {
				ch = input.charAt(++pos);
			}
			if (Character.isDigit(ch)) {
				while ((Character.isDigit(ch) 
						|| ch == decimalSeparator
                        || ch == 'e' || ch == 'E'
                        || (ch == minusSign && token.length() > 0 && ('e'==token.charAt(token.length()-1) 
                        || 'E'==token.charAt(token.length()-1)))
                        || (ch == '+' && token.length() > 0  && ('e'==token.charAt(token.length()-1) 
                        || 'E'==token.charAt(token.length()-1)))) 
						&& (pos < input.length())) {
					// --> it is a number
					token.append(input.charAt(pos++));
					ch = pos == input.length() ? 0 : input.charAt(pos);
				}
			} else if (ch == minusSign 
					&& Character.isDigit(peekNextChar())
					&& ("(".equals(previousToken) || ",".equals(previousToken)
							|| previousToken == null || rt.operators
								.containsKey(previousToken))) {
				// --> it is a minus sign
				token.append(minusSign);
				pos++;
				// recursive call
				token.append(next(rt));
			} else if (Character.isLetter(ch) || (ch == '_')  || (ch == '$') || (ch == ':')) {
				// --> it is a variable or a function
				int currentPos = pos;
				while ((Character.isLetter(ch) || Character.isDigit(ch) || (ch == '_') || (ch == ':') || (ch == '.') || (ch == '$'))
						&& (pos < input.length())) {
					if((ch == ':') && (pos - currentPos) > 1){
						throw new ExpressionException(createError("Invalid use of scope-assignment. Only ':' or '::' are permitted at the beginnig of the variable", (pos - token.length() + 1)));
					}
					token.append(input.charAt(pos++));
					ch = pos == input.length() ? 0 : input.charAt(pos);
				}
			} else if (ch == '(' || ch == ')' || ch == ',' || ch == '[' || ch == ']') {
				// it is a structural delimiter 
				token.append(ch);
				pos++;
			} else if (ch == '\'') {
				// --> it is a string
				token.append(ch);
				pos++;
				ch = pos == input.length() ? 0 : input.charAt(pos);
				boolean insideString = true;
				while (pos < input.length() && insideString) {
					if(ch == '\'')
						insideString = false;
					token.append(input.charAt(pos));
					pos++;
					ch = pos == input.length() ? 0 : input.charAt(pos);
				}
				if(insideString == true){
					throw new ExpressionException(createError("Missing String delimiter '", (pos - token.length() + 1)));
				}
			} else {
				// --> it is an operator ( but not a minus sign)
				while (!Character.isLetter(ch) && !Character.isDigit(ch)
						&& ch != '_' && !Character.isWhitespace(ch)
						&& ch != '(' && ch != ')' && ch != ','
						&& ch != '\'' 		//<-------
						&& (pos < input.length())) {
					token.append(input.charAt(pos));
					pos++;
					ch = pos == input.length() ? 0 : input.charAt(pos);
					if (ch == minusSign) {
						break;
					}
				}
				if (!rt.operators.containsKey(token.toString())) {
					throw new ExpressionException(createError("Unknown operator '" + token
							+ "'", (pos - token.length() + 1)));
				}
			}
			return previousToken = token.toString();
		}

		/**
		 * Get the actual character position in the string.
		 * 
		 * @return The actual character position.
		 */
		public int getPos() {
			return pos;
		}

	}

	private String createError(String error, int atpos){
		char[] array = new char[atpos - 1];
	    Arrays.fill(array, ' ');
	    return error + "\n" + "{"+expression+"}" + infoString + "\n" + new String(array) + "^";
	}
}
