package ch.maybites.quescript.commands;

import java.util.Locale;

import org.w3c.dom.Node;

import ch.maybites.quescript.expression.Expression;
import ch.maybites.quescript.expression.ExpressionVar;
import ch.maybites.quescript.expression.RunTimeEnvironment;
import ch.maybites.quescript.expression.Expression.ExpressionException;
import ch.maybites.quescript.messages.CMsgShuttle;
import ch.maybites.quescript.messages.ScriptMsgException;
import ch.maybites.tools.Debugger;

public class CmndVar extends Cmnd {
	protected static String NODE_NAME = "var";

	private static String ATTR_NAME = "name";

	ExpressionVar myExpression;
	ExpressionVar varValue;
	String name;
	
	RunTimeEnvironment prt;

	public CmndVar(Cmnd _parentNode){
		super(_parentNode);
		super.setCmndName(NODE_NAME);
	}

	public void build(Node _xmlNode) throws ScriptMsgException{
		super.build(_xmlNode);
	}

	/**
	 * Parse the Expressions with the RuntimeEnvironement
	 */
	public void setup(RunTimeEnvironment rt)throws ScriptMsgException{
		prt = rt;
		if(super.content != null){
			try {
				myExpression = new Expression(super.content, "{", "}").setInfo(" at line(" + lineNumber + ")").parse(rt);
			} catch (ExpressionException e) {
				throw new ScriptMsgException("QueScript - Command <var>: Value Expression: " + e.getMessage());
			}
			
			name = getAttributeValue(ATTR_NAME);
			
			if(prt.functions.containsKey(name.toUpperCase(Locale.ROOT))){
				throw new ScriptMsgException("QueScript - Command <var>: Attribute name: Variable name invalid: It matches a function: " + name + "() at line(" + lineNumber + ")");
			}
			
			// if no local variable of this name exists, create one with value NULL
			try {
				myExpression.eval();
				varValue = new ExpressionVar();
				if(myExpression.isArray){
					varValue.copyFrom(myExpression);
				} else {
					varValue.set(myExpression);
				}
				varValue.setUsedAsVariable();
				// we create two different expressionVars because we dont want to 
				//   have the original expression altered by any other process.
				//     the varValue variable is passed on to other expressions, might be part of them and
				//     altered by them, so we can't afford to break these references. 
				prt.setLocalVariable(name, varValue);
			} catch (ExpressionException e) {
				Debugger.error("QueScript que("+parentNode.getQueName()+") - Command <var>: Value Expression", e.getMessage());
			}

			if(debugMode)
				Debugger.verbose("QueScript - NodeFactory", "que("+parentNode.getQueName()+") "+new String(new char[getLevel()]).replace('\0', '_')+"created var-Comnd = "+ super.content);	
			
		} else {
			throw new ScriptMsgException("QueScript - Command <var>: Expression missing at line(" + lineNumber + ")");			
		}
	}

	public void bang(CMsgShuttle _msg) {
		if(!_msg.isWaitLocked()){
			lockLessBang(_msg);
		}
	}
	
	public void lockLessBang(CMsgShuttle _msg){
		try {
			// we already know that this variable must exist, since it was created on load-time
			// so we can simply pass on the evaluation of the initial expression
			prt.setLocalVariable(name, myExpression.eval());
		} catch (ExpressionException e) {
			Debugger.error("QueScript que("+parentNode.getQueName()+") - Command <var>: Value Expression", e.getMessage());
		}
	}

	public void resume(long _timePassed) {;}

}
