package ch.maybites.quescript.commands;

import java.util.ArrayList;
import java.util.List;

import ch.maybites.quescript.expression.RunTimeEnvironment;
import ch.maybites.quescript.messages.CMsgShuttle;
import ch.maybites.quescript.messages.ScriptMsgException;

public class CmndScript extends Cmnd{
	public static String NODE_NAME = "script";
	
	/** list with only que nodes */
	private ArrayList<Cmnd> queChildren;

	OutputInterface output;

	public CmndScript() {
		super(null);
		queChildren = new ArrayList<Cmnd>();
		super.setCmndName(NODE_NAME);
	}
	
	public void setup(RunTimeEnvironment rt)throws ScriptMsgException{
		queChildren.clear();
		for(Cmnd child: this.getChildren()){
			child.setup(rt);
			if(child.cmdName.equals(CmndQue.NODE_NAME)){
				queChildren.add(child);
			}
		}
	}

	public void setOutput(OutputInterface output){
		this.output = output;
	}

	public OutputInterface getOutput(){
		return output;
	}

	/**
	 * gets all this objects ques
	 * @return
	 */
	public List<Cmnd> getQues(){
		return queChildren;
	}

	/**
	 * Checks if this Script has a Que of this name
	 * @param queName
	 * @return true if this is the case 
	 */
	public boolean hasQue(String queName){
		for(Cmnd q: getQues()){
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
		for(Cmnd q: getQues()){
			CmndQue que = (CmndQue) q;
			if(que.getQueName().equals(queName)){
				return que;
			}
		}
		return null;
	}
	
	/**
	 * Checks if this Script has a playing que
	 * @return true if one of its ques is playing
	 */
	public boolean hasQuePlaying(){
		for(Cmnd q: getQues()){
			CmndQue que = (CmndQue) q;
			if(que.isPlaying){
				return true;
			}
		}
		return false;
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
