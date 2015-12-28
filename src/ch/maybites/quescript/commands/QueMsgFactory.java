package ch.maybites.quescript.commands;

public class QueMsgFactory {
	
	private static int msgType = 1;

	public static void setMsgTypeToMax(){
		msgType = 1;
	}

	public static void setMsgTypeToProcessing(){
		msgType = 2;
	}

	public static QueMessageRAW getMsg(String msgName){
		return new QueMessageAtom(msgName);
	}
	
}
