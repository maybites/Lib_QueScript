package ch.maybites.quescript.commands;

import java.util.ArrayList;
import java.util.HashMap;

import ch.maybites.quescript.QueMessage;
import ch.maybites.quescript.expression.ExpressionVar;
import ch.maybites.quescript.expression.Expression.ExpressionException;

public abstract class QueMessageRAW implements QueMessage{

	protected String messageName;
	protected String sendTo;
	
	protected ArrayList<Object> tempList;
	
	protected HashMap<Integer, ExpressionVar> evalList;
	
	protected int index;
	
	protected QueMessageRAW(String messageName){
		index = 0;
		tempList = new ArrayList<Object>();
		tempList.add(messageName);
		this.messageName = messageName;
	}

	protected QueMessageRAW addSendTo(String sendTo){
		index++;
		tempList.add(sendTo);
		this.sendTo = sendTo;
		return this;
	}

	protected QueMessageRAW add(String token){
		index++;
		tempList.add(token);
		return this;
	}
	
	protected QueMessageRAW add(ExpressionVar token){
		index++;
		if (evalList == null)
			evalList = new HashMap<Integer, ExpressionVar>();
		evalList.put(new Integer(index), token);
		tempList.add(token);
		return this;
	}
	
	protected QueMessageRAW add(double number){
		index++;
		tempList.add(new Double(number));	
		return this;
	}
	
	protected QueMessageRAW add(long number){
		index++;
		tempList.add(new Long(number));	
		return this;
	}
	
	/**
	 * Calling this method requires to call eval() beforehand
	 * @param cmdName
	 * @param queName
	 * @return
	 */
	abstract protected String[] getStringArray(String cmdName, String queName);
	
	/**
	 * Calling This method indicates the creation of this method is done
	 * and makes the message ready for shipment
	 * @return
	 */
	abstract protected QueMessageRAW done();
	
	/**
	 * Calling the method will evaluate all stored expressions 
	 * and makes the message ready for shipment
	 * @return
	 * @throws ExpressionException
	 */
	abstract protected QueMessageRAW eval() throws ExpressionException;
	
}
