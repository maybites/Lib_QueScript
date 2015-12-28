package ch.maybites.quescript.commands;

import ch.maybites.quescript.QueMessage;

public interface OutputInterface {

	public void outputSendMsg(QueMessage msg);

	public void outputInfoMsg(QueMessage msg);

	public void outputDumpMsg(QueMessage msg);

	public void selfCommand(String[] _comnd);

}
