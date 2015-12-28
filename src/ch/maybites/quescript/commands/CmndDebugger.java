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

	RunTimeEnvironment prt;
	
	boolean showLocal = false;
	boolean showQue = false;
	boolean showGlobal = false;

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
			String domains = getAttributeValue(ATTR_SHOWVARDOMAIN);
			if(domains.contains("local"))
				showLocal = true;
			if(domains.contains("que"))
				showQue = true;
			if(domains.contains("global"))
				showGlobal = true;
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
			HashMap<String, ExpressionVar> locals = (HashMap<String, ExpressionVar>) prt.getPrivateVars();
			HashMap<String, ExpressionVar> que = (HashMap<String, ExpressionVar>) prt.getProtectedVars();
			HashMap<String, ExpressionVar> global = (HashMap<String, ExpressionVar>) prt.getPublicVars();
			String var;
			ExpressionVar exVar;
			Iterator<String> it;
			if(showLocal || showQue || showGlobal){
				getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("DEBUGGER").done());
				getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("------------------").done());

			} else {
				getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("QueScript: <debugger> usage:").done());
				getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("<debugger " + ATTR_SHOWVARDOMAIN + "=\"local, que, global\" />").done());
			}
				
			if(showLocal){
				getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("Local Variables:").done());
				it = locals.keySet().iterator();
				while(it.hasNext()){
					var = it.next();
					exVar = locals.get(var);
					getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add(var).add(" = ").add(exVar.getStringValue()).add(" (" + ((exVar.isNumber)?"float":"string") + ")").done());
				}
				getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("------------------").done());
			}
						
			if(showQue){
				getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("Que Variables:").done());
				it = que.keySet().iterator();
				while(it.hasNext()){
					var = it.next();
					exVar = que.get(var);
					getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add(var).add(" = ").add(exVar.getStringValue()).add(" (" + ((exVar.isNumber)?"float":"string") + ")").done());
				}
				getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("------------------").done());
			}

			if(showGlobal){
				getOutput().outputSendMsg(QueMsgFactory.getMsg("print").add("Global Variables:").done());
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

	@Override
	public void resume(long _timePassed) {
		// TODO Auto-generated method stub
		
	}

}
