package ch.maybites.quescript.commands;

import org.w3c.dom.Node;

import ch.maybites.quescript.expression.Expression;
import ch.maybites.quescript.expression.ExpressionVar;
import ch.maybites.quescript.expression.RunTimeEnvironment;
import ch.maybites.quescript.expression.Expression.ExpressionException;
import ch.maybites.quescript.messages.CMsgAnim;
import ch.maybites.quescript.messages.CMsgShuttle;
import ch.maybites.quescript.messages.ScriptMsgException;
import ch.maybites.tools.Debugger;

public class CmndWhile extends Cmnd {
	protected static String NODE_NAME = "while";
		
	private static String ATTR_REPEAT = "repeat";
	private static String ATTR_START = "start";
	private static String ATTR_STEP = "step";
	private static String ATTR_NAME = "name";
		
	private ExpressionVar ifCondition = null;
	private ExpressionVar startCondition = null;
	private ExpressionVar stepCondition = null;
	
	private boolean running = false;
	
	private String name = null;

	public CmndWhile(Cmnd _parentNode){
		super(_parentNode);
		super.setCmndName(NODE_NAME);
	}

	public void build(Node _xmlNode) throws ScriptMsgException{
		super.build(_xmlNode);
		
		//if there is a nested <anim> node inside this if, it checks if there is another <anim> node
		// further down the tree towards the root
		for(Cmnd child: this.getChildren()){
			if(child instanceof CmndAnim ||child instanceof CmndWhile){
				Cmnd parnt = this.parentNode;
				while(!(parnt instanceof CmndQue)){
					if(parnt instanceof CmndAnim ||parnt instanceof CmndWhile)
						throw new ScriptMsgException("Command <while>: Nesting of <anim> and <while> nodes are prohibited");
					parnt = parnt.parentNode;
				}
			}
		}
	}

	/**
	 * Parse the Expressions with the RuntimeEnvironement
	 */
	public void setup(RunTimeEnvironment rt)throws ScriptMsgException{
		RunTimeEnvironment prt = new RunTimeEnvironment(rt);

		try {
			if(getAttributeValue(ATTR_START) != null){
				startCondition = new Expression(getAttributeValue(ATTR_START), "{", "}").setInfo(" at line(" + lineNumber + ")").parse(prt);
			}
		} catch (ExpressionException e) {
			throw new ScriptMsgException("Command <while>: Attribute <start>: " + e.getMessage());
		}
		
		try {
			ifCondition = new Expression(getAttributeValue(ATTR_REPEAT), "{", "}").setInfo(" at line(" + lineNumber + ")").parse(prt);
		} catch (ExpressionException e) {
			throw new ScriptMsgException("Command <while>: Attribute <repeat>: " + e.getMessage());
		}
		
		try {
			if(getAttributeValue(ATTR_STEP) != null){
				stepCondition = new Expression(getAttributeValue(ATTR_STEP), "{", "}").setInfo(" at line(" + lineNumber + ")").parse(prt);
			}
		} catch (ExpressionException e) {
			throw new ScriptMsgException("Command <while>: Attribute <step>: " + e.getMessage());
		}
		
		if(getAttributeValue(ATTR_NAME) != null){
			name = getAttributeValue(ATTR_NAME);
		}

		if(debugMode)
			Debugger.verbose("QueScript - NodeFactory", "que("+parentNode.getQueName()+") "+new String(new char[getLevel()]).replace('\0', '_')+" created while Cmnd: " + getAttributeValue(ATTR_REPEAT));

		// Make sure the que- and local- variables are created before the children are parsed
		for(Cmnd child: this.getChildren()){
			child.setup(prt);
		}
	}
	
	@Override
	public void store(Node _parentElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bang(CMsgShuttle _msg) {
		if(_msg.isInStopMode())
			running = false;
		if(_msg.hasFadeMessage(name))
			running = false;
		if(!_msg.isWaitLocked() || running){
			try {
				if(!running && startCondition != null){
					// the <while> loop starts here. should be only called once
					startCondition.eval();
				}
				if(ifCondition.eval().getNumberValue() >= 1){
					for(Cmnd child : getChildren()){
						child.lockLessBang(_msg);
					}
					// if there is a step expression, 
					// it will be executed after all the <while> children
					if(stepCondition != null)
						stepCondition.eval();
					running = true;
				} else {
					running = false;
					// if a name is set, <while> will send an anim message once its looping has finished
					if(name != null)
						_msg.addMessage(new CMsgAnim(name));
				}
			} catch (ExpressionException e) {
				Debugger.error("Script - Command <while>", "while expression: " + e.getMessage());			
			}
		}
	}

	public void lockLessBang(CMsgShuttle _msg){;}

	@Override
	public void resume(long _timePassed) {
	}

}
