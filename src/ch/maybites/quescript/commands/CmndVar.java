package ch.maybites.quescript.commands;

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
		try {
			varValue = new Expression(super.content, "{", "}").setInfo(" at line(" + lineNumber + ")").parse(rt);
		} catch (ExpressionException e) {
			throw new ScriptMsgException("QueScript - Command <expr>: Value Expression: " + e.getMessage());
		}
		
		name = getAttributeValue(ATTR_NAME);

		// if no global variable of this name exists, create one with value NULL
		if(!rt.containsGlobalVar(name))
			rt.setGlobalVariable(name, new ExpressionVar());

		if(debugMode)
			Debugger.verbose("QueScript - NodeFactory", "que("+parentNode.getQueName()+") "+new String(new char[getLevel()]).replace('\0', '_')+"created expr-Comnd = "+ super.content);	

	}

	public void bang(CMsgShuttle _msg) {
		if(!_msg.isWaitLocked()){
			lockLessBang(_msg);
		}
	}
	
	public void lockLessBang(CMsgShuttle _msg){
		try {
			prt.setGlobalVariable(name, varValue.eval());
		} catch (ExpressionException e) {
			Debugger.error("QueScript que("+parentNode.getQueName()+") - Command <expr>: Value Expression", e.getMessage());
		}
	}

	public void resume(long _timePassed) {;}

}
