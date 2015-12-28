package ch.maybites.quescript.commands;

import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Node;

import ch.maybites.quescript.expression.ExpressionVar;
import ch.maybites.quescript.expression.RunTimeEnvironment;
import ch.maybites.quescript.messages.CMsgShuttle;
import ch.maybites.quescript.messages.ScriptMsgException;
import ch.maybites.tools.Debugger;

public class CmndDebugger extends Cmnd {
	protected static String NODE_NAME = "debugger";

	private static String ATTR_SHOWVARDOMAIN = "vardomain";
	private static String ATTR_NAME = "name";

	RunTimeEnvironment prt;
	
	private int showVarDomain = 0;
	private String name = "";

	public CmndDebugger(Cmnd _parentNode){
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

		if(getAttributeValue(ATTR_SHOWVARDOMAIN) != null){
			try{
				showVarDomain = Integer.parseInt(getAttributeValue(ATTR_SHOWVARDOMAIN));
			} catch (NumberFormatException e){
				new ScriptMsgException("<que name=\""+parentNode.getQueName()+"\"> <debugger>: attribute '" + ATTR_SHOWVARDOMAIN + "' must be an integer, but instead it is " + getAttributeValue(ATTR_SHOWVARDOMAIN) + " at line(" + lineNumber +" )");
			}
		}
		if(getAttributeValue(ATTR_NAME) != null){
			name = getAttributeValue(ATTR_NAME);
		} else {
			name = "que(" + this.getQueName()+ ") inside " + parentNode.cmdName + " at line(" + lineNumber +")";
		}

		if(debugMode)
			Debugger.verbose("QueScript - NodeFactory", "que("+parentNode.getQueName()+") "+new String(new char[getLevel()]).replace('\0', '_')+" created "+cmdName+"-Comnd");			
	}

	@Override
	public void store(Node _parentElement) {
		// TODO Auto-generated method stub

	}

	public void bang(CMsgShuttle _msg) {
		if(!_msg.isWaitLocked()){
			lockLessBang(_msg);
		}
	}

	public void lockLessBang(CMsgShuttle _msg){
		if(debugMode){
			String var;
			ExpressionVar exVar;
			Iterator<String> it;
			getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("DEBUGGER " + name).done());
			getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("------------------").done());
				
			int levels = prt.getDomainLevels();
			int currnt;
			for(int i = (levels - 1); i >= 0; i--){	
				currnt = levels  - 1 - i;
				if(showVarDomain >= currnt){
					HashMap<String, ExpressionVar> global = (HashMap<String, ExpressionVar>) prt.getDomain(i);
					getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("Variables inside Domain:").add(currnt).done());
					it = global.keySet().iterator();
					while(it.hasNext()){
						var = it.next();
						exVar = global.get(var);
						getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add(var).add(" = ").add(exVar.getStringValue()).add(" (" + ((exVar.isNumber)?"float":"string") + ")").done());
					}
					getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("------------------").done());
				}
			}
		}		
	}

	@Override
	public void resume(long _timePassed) {
		// TODO Auto-generated method stub
		
	}

}
