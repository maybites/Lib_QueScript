package ch.maybites.quescript;

/**
 * Interface to define the output object
 * 
 * @author martin froehlich http://maybites.ch
 *
 */
public interface OutputConnector {

	public void outputSendMsg(QueMessage msg);

	public void outputInfoMsg(QueMessage msg);

	public void outputDumpMsg(QueMessage msg);

}
