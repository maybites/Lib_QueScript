package ch.maybites.quescript.commands;

import ch.maybites.quescript.expression.RunTimeEnvironment;
import ch.maybites.quescript.messages.CMsgShuttle;
import ch.maybites.quescript.messages.ScriptMsgException;

public class CmndScript extends Cmnd{
	public static String NODE_NAME = "script";
	
	OutputInterface output;

	public CmndScript() {
		super(null);
		super.setCmndName(NODE_NAME);
	}
	
	public void setup(RunTimeEnvironment rt)throws ScriptMsgException{
		for(Cmnd child: getChildren()){
			CmndQue que = (CmndQue)child;
			if(que.prt == null)
				child.setup(rt);
		}
	}

	public void setOutput(OutputInterface output){
		this.output = output;
	}

	public OutputInterface getOutput(){
		return output;
	}

	/**
	 * Checks if this Script has a Que of this name
	 * @param queName
	 * @return true if this is the case 
	 */
	public boolean hasQue(String queName){
		for(Cmnd q: getChildren()){
			CmndQue que = (CmndQue) q;
			if(que.getQueName().equals(queName))
				return true;
		}
		return false;
	}

	/**
	 * Returns the first que of this name this script can find inside its children.
	 * @param queName
	 * @return the instance of the que
	 */
	public CmndQue getQue(String queName){
		for(Cmnd q: getChildren()){
			CmndQue que = (CmndQue) q;
			if(que.getQueName().equals(queName))
				return que;
		}
		return null;
	}
	
	@Override
	public void bang(CMsgShuttle _msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lockLessBang(CMsgShuttle _msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume(long _timePassed) {
		// TODO Auto-generated method stub
		
	}

}
