package ch.maybites.quescript.commands;

import com.cycling74.max.Atom;

import ch.maybites.quescript.expression.Expression.ExpressionException;
import ch.maybites.quescript.expression.ExpressionVar;

public class QueMessageAtom extends QueMessageRAW{
	
	private Atom[] messageList;
	
	private int iterationIndex;
	
	protected QueMessageAtom(String messageName){
		super(messageName);
	}

	public boolean hasAtoms() {
		return (messageList == null)?false: true;
	}

	public Object[] getAtoms() {
		return messageList;
	}

	public void iterate() {
		iterationIndex = 0;
	}

	public boolean hasNext() {
		return (iterationIndex <= index);
	}

	public boolean isNextString() {
		return messageList[iterationIndex].isString();
	}

	public boolean isNextFloat() {
		return messageList[iterationIndex].isFloat();
	}

	public String nextString() {
		return messageList[iterationIndex].toString();
	}

	public float nextFloat() {
		return messageList[iterationIndex].toFloat();
	}

	protected QueMessageRAW done() {
		messageList = new Atom[index + 1];
		for(int i = 0; i <= index; i++){
			if(tempList.get(i) instanceof String){
				messageList[i] = Atom.newAtom((String)tempList.get(i));
			} else if(tempList.get(i) instanceof Double){
				messageList[i] = Atom.newAtom((Double)tempList.get(i));
			} else if(tempList.get(i) instanceof Long){
				messageList[i] = Atom.newAtom((Long)tempList.get(i));
			} else if(tempList.get(i) instanceof ExpressionVar){
				messageList[i] = Atom.newAtom(0);
			} else {
				messageList[i] = Atom.newAtom(0);
			} 
		}
		return this;
	}

	protected QueMessageRAW eval() throws ExpressionException {
		if(evalList != null && evalList.size() > 0){
			ExpressionVar ev;
//			System.out.println(" evallist size = " + evalList.size() + " | messageList size = " + messageList.length);
			for(Integer i: evalList.keySet()){
//				System.out.println(" -> evallist int = " + i);
				ev = evalList.get(i);
				if(ev.isNumber)
					messageList[i] = Atom.newAtom(ev.eval().getNumberValue());
				else
					messageList[i] = Atom.newAtom(ev.eval().getStringValue());
			}
		}
		return this;
	}
	
	protected String[] getStringArray(String cmdName, String queName){
		String[] cmnd = new String[messageList.length + 2];
		cmnd[0] = cmdName;
		cmnd[1] = queName;
		for(int i = 0; i < messageList.length; i++){
			cmnd[i + 2] = messageList[i].getString();
		}
		return cmnd;
	}

}
