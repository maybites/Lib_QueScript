package ch.maybites.quescript.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import ch.maybites.quescript.expression.Expression;
import ch.maybites.quescript.expression.RunTimeEnvironment;
import ch.maybites.quescript.expression.Expression.ExpressionException;
import ch.maybites.quescript.messages.CMsgShuttle;
import ch.maybites.quescript.messages.ScriptMsgException;
import ch.maybites.utils.Debug;

public class CmndMessage extends Cmnd {
	public static String NODE_NAME_SEND 	= "send";
	public static String NODE_NAME_PRINT 	= "print";
	public static String NODE_NAME_OUT 		= "out";
	public static String NODE_NAME_TRIGGER 	= "trigger";
	public static String NODE_NAME_OSC 		= "osc";

	private static String ATTR_SENDTO = "sendto";
	
	String sendto = "default";
	
	QueMessageRAW myMessage;
	
	public CmndMessage(Cmnd _parentNode, String _cmdName){
		super(_parentNode);
		super.setCmndName(_cmdName);
	}

	public void build(Node _xmlNode) throws ScriptMsgException{
		super.build(_xmlNode);

		myMessage = QueMsgFactory.getMsg(this.cmdName);

		if(super.cmdName.equals(NODE_NAME_OSC)){
			// only the OSC message has an sendto attribute
			if(this.hasAttributeValue(ATTR_SENDTO)){
				sendto = getAttributeValue(ATTR_SENDTO);
			}
			myMessage.addSendTo(sendto);
		}
	}
	
	private void parseContentString(String _content, RunTimeEnvironment rt) throws ExpressionException{
		List<String> segmts = new ArrayList<String>();
		Matcher m = Pattern.compile("([^{]\\S*|.+?[{}])\\s*").matcher(_content);
		while (m.find())
			segmts.add(m.group(1).trim()); // Add .replace("\"", "") to remove surrounding quotes.

		boolean isNotSet = false;
		for(int i = 0; i < segmts.size(); i++){
			isNotSet = true;
			// first try int
			if(isNotSet){
				try{
					myMessage.add(Integer.parseInt(segmts.get(i)));
					isNotSet = !isNotSet;
				} catch (NumberFormatException e){;}
			} 
			if(isNotSet){
				try{
					myMessage.add(Double.parseDouble(segmts.get(i)));
					isNotSet = !isNotSet;
				} catch (NumberFormatException e){;}
			}
			if(isNotSet){
				if(segmts.get(i).startsWith("{"))
					myMessage.add(new Expression(segmts.get(i), "{", "}").setInfo(" at line(" + lineNumber + ")").parse(rt));
				else 
					myMessage.add(segmts.get(i));
			}
		}
		myMessage.done();
	}
	
	/**
	 * Parse the Expressions with the RuntimeEnvironement
	 */
	public void setup(RunTimeEnvironment rt)throws ScriptMsgException{
		try {
			parseContentString(super.content, rt);
		} catch (ExpressionException e) {
			throw new ScriptMsgException("Script - Command <" + cmdName +"> " + e.getMessage() + " at line("+lineNumber+")");
		}
		
		if(debugMode)
			Debug.verbose("QueScript - NodeFactory", "que("+parentNode.getQueName()+") "+new String(new char[getLevel()]).replace('\0', '_')+" created "+cmdName+"-Comnd = '"+super.content+"'");			

		// and then do it for all the children
		for(Cmnd child: this.getChildren()){
			child.setup(rt);
		}
	}
		
	/**
	 * Sends the message after it takes all the interpolated values.
	 */
	public void lockLessBang(CMsgShuttle _msg){
		try {
			myMessage.eval();
			getOutput().outputSendMsg(myMessage);
		} catch (ExpressionException e) {
			Debug.error("Script - Command <" + cmdName +">", "expression evaluation error: " + e.getMessage() + " at line("+lineNumber+")");			
		}
	}
	
	public void clear(){
		myMessage.clear();
		for(Cmnd child: getChildren()){
			child.clear();
		}
	}
	
	public void bang(CMsgShuttle _msg) {
		if(!_msg.isWaitLocked()){
			lockLessBang(_msg);
			if(cmdName.equals(NODE_NAME_TRIGGER)){
				sendInternalMessage();
			}
		}
	}
	
	private void sendInternalMessage(){
		getOutput().selfCommand(myMessage.getStringArray(cmdName, parentNode.getQueName()));
	}

	public void resume(long _timePassed) {
	}

}
