package ch.maybites.quescript.commands;

import org.w3c.dom.Node;

import ch.maybites.quescript.expression.RunTimeEnvironment;
import ch.maybites.quescript.messages.CMsgShuttle;
import ch.maybites.quescript.messages.ScriptMsgException;
import ch.maybites.tools.Debugger;

public class CmndInternal extends Cmnd {
	public static String NODE_NAME_STOP = "stop";
	public static String NODE_NAME_RESUME = "resume";
	public static String NODE_NAME_PAUSE = "pause";
	public static String NODE_NAME_PLAY = "play";
	public static String NODE_NAME_SHUTDOWN = "shutdown";
	
	private static String ATTR_NAME = "name";

	String name;
	
	public CmndInternal(Cmnd _parentNode, String _cmdName){
		super(_parentNode);
		super.setCmndName(_cmdName);
	}
	
	public void build(Node _xmlNode) throws ScriptMsgException{
		super.build(_xmlNode);
		
		// use the attribute or the first value of the key
		if(this.hasAttributeValue(ATTR_NAME))
			name = getAttributeValue(ATTR_NAME);
		
	}

	/**
	 * Parse the Expressions with the RuntimeEnvironement
	 */
	public void setup(RunTimeEnvironment rt)throws ScriptMsgException{
		if(debugMode)
			Debugger.verbose("QueScript - NodeFactory", "que("+parentNode.getQueName()+") "+new String(new char[getLevel()]).replace('\0', '_')+" created internal "+ cmdName +"-Comnd for: " + name);	
	}
	
	/**
	 * checks if the attribute name is set
	 * @return
	 */
	public boolean hasName(){
		return (name == null)?false:true;
	}
	
	/**
	 * get the value of attribute name
	 * @return
	 */
	public String getName(){
		return name;
	}

	@Override
	public void store(Node _parentElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bang(CMsgShuttle _msg) {
		if(!_msg.isWaitLocked()){
			lockLessBang(_msg);
		}
	}

	public void lockLessBang(CMsgShuttle _msg){
		if(!_msg.isInStopMode()){
			this.getOutput().selfCommand(new String[]{cmdName, parentNode.getQueName(), name});
			if(debugMode)
				Debugger.verbose("QueScript "+cmdName+"-Command que:(" + parentNode.getQueName() + ")", "sent "+cmdName+"-message to "+cmdName+": " + name);
		}
	}

	@Override
	public void resume(long _timePassed) {
		// TODO Auto-generated method stub
		
	}

}
