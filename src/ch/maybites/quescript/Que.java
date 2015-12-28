package ch.maybites.quescript;

import com.cycling74.max.Atom;
import com.cycling74.max.DataTypes;
import com.cycling74.max.MaxObject;

import ch.maybites.quescript.commands.QSManager;
import ch.maybites.quescript.commands.QueMsgFactory;
import ch.maybites.tools.Debugger;

/**
 * QueScript External for MaxMSP
 * 
 * @author Martin Froehlich http://maybites.ch
 *
 */
public class Que extends MaxObject implements OutputConnector{
	
	final int OUTLET_SEND = 0;
	final int OUTLET_TRIGGER = 1;
	final int OUTLET_INFO = 2;
	final int OUTLET_DUMP = 3;

	QSManager queManager;
			
	public Que(Atom[] _args){
		
		declareInlets(new int[]{DataTypes.ALL, DataTypes.ALL});
		declareOutlets(new int[]{DataTypes.ALL, DataTypes.ALL, DataTypes.ALL});

		queManager = new QSManager();
		
		QueMsgFactory.setMsgTypeToMax();
	}
	
	/**
	 * read script file
	 * @param _fileName
	 */
	public void read(String _fileName){
		queManager.registerConnector(this);
		queManager.load(_fileName);
	}
	
	/**
	 * create next frame
	 */
	public void bang(){		
		queManager.bang();
	}
	
	/**
	 * set global variable
	 * @param name
	 * @param val
	 */
	public void var(String name, float val){
		queManager.var(name, val);
	}
	
	/**
	 * set global variable
	 * @param name
	 * @param val
	 */
	public void var(String name, String val){
		queManager.var(name, val);
	}
	
	/**
	 * autostart = 1 will play the first que of the script upon loading the script
	 * @param _autostart
	 */
	public void autostart(int _autostart){
		queManager.autostart(_autostart);
	}

	/**
	 * clears all global Variables, stops all que's and reloads the script
	 */
	public void reset(){
		queManager.reset();
	}

	/**
	 * Start the output of information about the currently playing que's, including the time each
	 * frame takes in milliseconds.
	 * @param _frequency the number of updates per second.
	 */
	public void viewplayingques(int _frequency){
		queManager.viewplayingques(_frequency);
	}
	
	/**
	 * Start playing specified que name
	 * @param queName
	 */
	public void play(String queName){
		queManager.play(queName);
	}

	/**
	 * trigger message
	 * @param _args list
	 */
	public void trigger(Atom[] _args){
		String[] args = new String[_args.length - 1];
		for(int i = 1; i < _args.length; i++){
			args[i - 1] = _args[i].getString();
		}		
		queManager.trigger(_args[0].toString(), args);
	}

	/**
	 * trigger message
	 * @param _triggerName string
	 */
	public void trigger(String _triggerName){
		queManager.trigger(_triggerName, null);
	}
		
	/**
	 * stops all running que's
	 */
	public void stop(){
		queManager.stop();
	}
	
	/**
	 * stops specified que
	 * @param queName
	 */
	public void stop(String queName){
		queManager.stop(queName);
	}
	
	/**
	 * resumes playing all paused que's
	 */
	public void resume(){
		queManager.resume();
	}
	
	/**
	 * resumes playing specified que
	 * @param queName
	 */
	public void resume(String queName){
		queManager.resume(queName);
	}

	/**
	 * pause all running que's
	 */
	public void pause(){
		queManager.pause();
	}
	
	/**
	 * pause specified que
	 * @param queName
	 */
	public void pause(String queName){
		queManager.pause(queName);
	}

	/**
	 * shutdown all que's
	 */
	public void shutdown(){
		queManager.shutdown();
	}
	
	/**
	 * Shutdown specified que
	 * @param queName
	 */
	public void shutdown(String queName){
		queManager.shutdown(queName);
	}

	/**
	 * switch debug mode
	 * @param _debug 0 = off, 1 = on
	 */
	public void debug(int _debug){
		queManager.setDebug(_debug);
	}
	
	/**
	 * Debugger level 
	 * @param _level (verbose, debug, info, warning, error, fatal)
	 */
	public void java_debug(String _level){
		if(_level.equals("verbose"))
			Debugger.setLevelToVerbose();
		else if(_level.equals("debug"))
			Debugger.setLevelToDebug();
		else if(_level.equals("info"))
			Debugger.setLevelToInfo();
		else if(_level.equals("warning"))
			Debugger.setLevelToWarning();
		else if(_level.equals("error"))
			Debugger.setLevelToError();
		else if(_level.equals("fatal"))
			Debugger.setLevelToFatal();
	}

	public void outputSendMsg(QueMessage msg) {
		if(msg.hasAtoms())
			outlet(OUTLET_SEND, (Atom[])msg.getAtoms());
	}

	public void outputInfoMsg(QueMessage msg) {
		if(msg.hasAtoms())
			outlet(OUTLET_INFO, (Atom[])msg.getAtoms());
	}
	
	public void outputDumpMsg(QueMessage msg) {
		if(msg.hasAtoms())
			outlet(OUTLET_DUMP, (Atom[])msg.getAtoms());
	}
		
}
