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

	private static String ATTR_SCOPE = "scope";
	private static String ATTR_NAME = "name";

	private static String ATTR_SCOPE_SCRIPT = "script";
	private static String ATTR_SCOPE_QUE = "que";
	private static String ATTR_SCOPE_LOCAL = "local";

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
		showVarDomain = 0;
		
		if(getAttributeValue(ATTR_SCOPE) != null){
			String scope = getAttributeValue(ATTR_SCOPE);
			if(scope.equals(ATTR_SCOPE_SCRIPT)){
				showVarDomain = 0;
			} else if(scope.equals(ATTR_SCOPE_QUE)){
				showVarDomain = 1;
			} else if(scope.equals(ATTR_SCOPE_LOCAL)){
				showVarDomain = 10;
			} else{
				new ScriptMsgException("<que name=\""+parentNode.getQueName()+"\"> <debugger>: attribute '" + ATTR_SCOPE + "' must be either script|que|local, but is " + getAttributeValue(ATTR_SCOPE) + " at line(" + lineNumber +" )");
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
				
			int levels = prt.getScopeLevels();
			for(int i = 0; i < levels; i++){	
				if(showVarDomain >= i){
					HashMap<String, ExpressionVar> global = (HashMap<String, ExpressionVar>) prt.getScope(i);
					getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("-------  ").add("Scope:").add(i).add("  -------").done());
					it = global.keySet().iterator();
					while(it.hasNext()){
						var = it.next();
						exVar = global.get(var);
						getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add(var).add(" = ").add(exVar.getStringValue()).add(" (" + ((exVar.isNumber)?"float":"string") + ")").done());
					}
				}
			}
			getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("------------------").done());
		}		
	}

	@Override
	public void resume(long _timePassed) {
		// TODO Auto-generated method stub
		
	}

}
