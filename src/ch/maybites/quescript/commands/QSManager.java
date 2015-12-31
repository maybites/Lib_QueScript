package ch.maybites.quescript.commands;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import ch.maybites.quescript.QueMessage;
import ch.maybites.quescript.OutputConnector;
import ch.maybites.quescript.expression.RunTimeEnvironment;
import ch.maybites.quescript.messages.CMsgTrigger;
import ch.maybites.quescript.messages.ScriptMsgException;
import ch.maybites.tools.Debugger;

/**
 * 
 * @author maybites
 *
 */
public class QSManager implements OutputInterface{
	// info messages //
	
	// QueList -  for filling the que selection menu
	private final String QUELIST = "quelist";
	private final String QUELIST_START = "start";
	private final String QUELIST_NAME = "name";
	private final String QUELIST_DONE = "done";
	private final String QUELIST_STOP = "stop";
	private final String QUELIST_PLAY = "play";
	
	// Script - shows current running ques and their position
	private final String SCRIPT = "script";

	private final String PARSING = "parsing";
	private final String PARSING_OK = "ok";
	private final String PARSING_ERROR = "error";
	
	private static String SCHEMA_FILENAME = "/queListSchema.xsd";
	
	private ArrayList<CMsgTrigger> triggerQueue;
	private ArrayList<CMsgTrigger> triggerQueCopy;
	
	private Validator validator;
	
	private boolean debugMode = false;
	
	private boolean autostart = false;

	private int viewplayingquesFreq = 0;
	
	private long lastviewTime = 0;

	private String fileName;

	private RunTimeEnvironment globalExprEnvironment;
	
	private CmndScript myScript;

	private ArrayList<String[]> selfCommands;
	
	private OutputConnector conn;
	
	public QSManager(){		
		triggerQueue = new ArrayList<CMsgTrigger>();
		triggerQueCopy = new ArrayList<CMsgTrigger>();

		// creating a validator for validating the script files
		try {
			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// load a WXS schema, represented by a Schema instance 
			
			// first locate the schema inside the jar file
			URL locator = this.getClass().getResource(SCHEMA_FILENAME);
			// open an inputstream
			InputStream stream = locator.openStream();
			// and load the schema file
			Source schemaFile = new StreamSource(stream);
	    
	    	Schema schema = factory.newSchema(schemaFile);

	    	// create a Validator instance, which can be used to validate an instance document
	    	validator = schema.newValidator();

	    	// validate the DOM tree
	    	validator.setErrorHandler(new MyErrorHandler());

	    	stream.close();
	    	
	    	globalExprEnvironment = new RunTimeEnvironment();
	    	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		selfCommands = new ArrayList<String[]>();
		
		myScript = new CmndScript();
	}
	
	public void bang(){	
		executeInternalMessages();
		
		long timer = System.currentTimeMillis();
		// and then keep on going

		Calendar md = Calendar.getInstance();
		globalExprEnvironment.setGlobalVariable("$HOUR", md.get(Calendar.HOUR_OF_DAY));
		globalExprEnvironment.setGlobalVariable("$MIN", md.get(Calendar.MINUTE));
		globalExprEnvironment.setGlobalVariable("$SEC", md.get(Calendar.SECOND));
		globalExprEnvironment.setGlobalVariable("$MILLI", md.get(Calendar.MILLISECOND));

		// all the que's receive a bang message, since some of them might still be in shutdown mode
		CmndQue nextElement;
		
		// make sure that no concurrent triggers get lost.
		triggerQueCopy = triggerQueue;
		if(triggerQueue.size() > 0)
			triggerQueue = new ArrayList<CMsgTrigger>();

		for(Cmnd e: myScript.getChildren()){
			nextElement = (CmndQue)e;
//			Debugger.verbose("QueScript", "banging...: " + nextElement.getQueName());	
			nextElement.bang(triggerQueCopy);
//			Debugger.verbose("QueScript", "... banged: " + nextElement.getQueName());	
		}
		triggerQueue.clear();

		if(viewplayingquesFreq > 0 && lastviewTime + (1000 / viewplayingquesFreq) < timer ){
			lastviewTime = timer;
			int outCounter = 0;
			outputInfoMsg(QueMsgFactory.getMsg(SCRIPT).add("playtime").add(System.currentTimeMillis() - timer).done());
			for(Cmnd child: myScript.getChildren()){
				CmndQue q = (CmndQue)child;
				if(q.isPlaying){
					outputInfoMsg(QueMsgFactory.getMsg(SCRIPT).add(outCounter).add(q.scriptLineSize).add(q.waitLineNumber - q.lineNumber).add("que("+q.queName+") "+q.waitLineMsg).add(1).done());
					outCounter++;
				}
			}
			for(int i = outCounter; i < 12; i++){
				outputInfoMsg(QueMsgFactory.getMsg(SCRIPT).add(i).add(20).add(1).add("-").add(0).done());
			}
		}
	}

	public void executeInternalMessages(){
		// first execute self inflicted commands (the script calls itself)
		if(selfCommands.size() > 0){
			ArrayList<String[]> copyCommands = new ArrayList<String[]>();
			for(String[] cmd: selfCommands){
				copyCommands.add(cmd);
			}
			// then clear all the commands
			selfCommands.clear();
			for(String[] cmd: copyCommands){
				if(cmd[0].equals(CmndInternal.NODE_NAME_PLAY)){
					play(cmd[2]);
				}else if(cmd[0].equals(CmndInternal.NODE_NAME_STOP)){
					if(cmd[2] == null){ // no name attribute was set at the stop message
						stopExcept(cmd[1]);
					} else { // there was a name attribute
						stop(cmd[2]);
					}
				}else if(cmd[0].equals(CmndInternal.NODE_NAME_SHUTDOWN)){
					if(cmd[2] == null){ // no name attribute was set at the shutdown message
						shutDownExcept(cmd[1]);
					} else { // there was a name attribute
						shutdown(cmd[2]);
					}
				}else if(cmd[0].equals(CmndInternal.NODE_NAME_PAUSE)){
					if(cmd[2] == null){ // no name attribute was set at the pause message
						pauseExcept(cmd[1]);
					} else { // there was a name attribute
						pause(cmd[2]);
					}
				}else if(cmd[0].equals(CmndInternal.NODE_NAME_RESUME)){
					if(cmd[2] == null){ // no name attribute was set at the play message
						resume();
					} else { // there was a name attribute
						resume(cmd[2]);
					}
				}else if(cmd[0].equals(CmndMessage.NODE_NAME_TRIGGER)){
					if(cmd.length == 3){ // 
						trigger(cmd[2], null);
					} else if(cmd.length > 3){
						String[] args = new String[cmd.length - 3];
						for(int i = 3; i < cmd.length; i++){
							args[i - 3] = cmd[i];
						}
						trigger(cmd[2], args);
					}
				}
			}
		}
	}

	/**
	 * loads a script file and returns the que's names it contains
	 * @param _filepath
	 */
	public void load(String _filepath){
		myScript.setOutput(this);

		// before loading the new ques, que will be removed if
		//  ->  ques are not playing
		//  ->  if the same script is loaded again
		for(Iterator<Cmnd> e = myScript.getChildren().iterator(); e.hasNext();){
			CmndQue que = (CmndQue)e.next();
			if(!que.isPlaying || _filepath.equals(fileName)){
				que.clear();
				e.remove();
			}
		}
				
		String  firstQueName = null;
		
		try {			
		    // Validate Script against the XSD
		    SAXSource source = new SAXSource(new InputSource(new java.io.FileInputStream(_filepath)));
		    validator.validate(source);

		    // Load the Script and make it accessible for building
		    InputStream is = new java.io.FileInputStream(_filepath);
			Document document = PositionalXMLReader.readXML(is);
			
			document.getDocumentElement().normalize();	
						
			myScript.build(document.getFirstChild());
			myScript.setup(globalExprEnvironment);
			
			if(!myScript.getChildren().isEmpty()){
				firstQueName = ((CmndQue)myScript.getChildren().get(0)).getQueName();
				Debugger.info("QueScript", "loaded " +_filepath + " with " + myScript.getChildren().size() + " que's");
			} else {
				Debugger.error("QueScript", "loaded " +_filepath + " with " + myScript.getChildren().size() + " que's");
			}
											
			outputInfoMsg(QueMsgFactory.getMsg(PARSING).add(PARSING_OK).done());

		} catch (SAXParseException e) {
			Debugger.error("QueScript", "Error at line[" + e.getLineNumber() + 
					"] col[" + e.getColumnNumber() + "]: " + 
					e.getMessage().substring(e.getMessage().indexOf(":")+1));
			outputInfoMsg(QueMsgFactory.getMsg(PARSING).add(PARSING_ERROR).add(e.getMessage().substring(e.getMessage().indexOf(":")+1) + " at line(" + e.getLineNumber() + ") col(" + e.getColumnNumber() + ")").done());
			return;

		} catch (ScriptMsgException e) {
			Debugger.error("QueScript", "Error: " + e.getMessage());
			outputInfoMsg(QueMsgFactory.getMsg(PARSING).add(PARSING_ERROR).add(e.getMessage()).done());
			return;

		} catch (Exception e) {
			Debugger.error("QueScript", "DocumentBuilder Exceptions:" + e.getMessage());
			outputInfoMsg(QueMsgFactory.getMsg(PARSING).add(PARSING_ERROR).add(e.getMessage()).done());
			e.printStackTrace();
			return;
		}
		
		// if autostart is selected, play the first que of the new loaded file
		if(autostart){
			play(firstQueName);
		}
				
		outputInfoMsg(QueMsgFactory.getMsg(QUELIST).add(QUELIST_START).done());
		for(Cmnd e: myScript.getChildren()){
			CmndQue _next = (CmndQue)e;
			outputInfoMsg(QueMsgFactory.getMsg(QUELIST).add(QUELIST_NAME).add(_next.getQueName()).done());
		}
		outputInfoMsg(QueMsgFactory.getMsg(QUELIST).add(QUELIST_DONE).done());
		
		fileName = _filepath;
	}

	public void var(String name, double value){
		globalExprEnvironment.setGlobalVariable(name, value);
	}
	
	public void var(String name, String value){
		globalExprEnvironment.setGlobalVariable(name, value);
	}
	
	public void clearGlobalVars(){
		globalExprEnvironment.clearGlobalVariables();
	}
	
	public void reset(){
		clearGlobalVars();
		stop();
		if(fileName != null)
			load(fileName);

	}
	
	public void play(String _queName){
		outputInfoMsg(QueMsgFactory.getMsg(QUELIST).add(QUELIST_PLAY).add(_queName).done());

		if(myScript.hasQue(_queName)){
			if(debugMode)
				Debugger.verbose("QueScript", "play... :" + _queName);	
			myScript.getQue(_queName).play(debugMode);
			if(debugMode)
				Debugger.verbose("QueScript", "... play :" + _queName);	
		}
	}
	
	/**
	 * ShutDown all ques
	 */
	public void shutdown(){
		if(debugMode)
			Debugger.verbose("QueScript", "shuting down... : all");	
		for(Cmnd e: myScript.getChildren()){
			((CmndQue)e).shutDown();
		}
		if(debugMode)
			Debugger.verbose("QueScript", "... shut all down");	
	}

	/**
	 * shutdown only specified que
	 * @param _exception
	 */
	public void shutdown(String _name){
		for(Cmnd e: myScript.getChildren()){
			CmndQue _next = (CmndQue)e;
			if(_next.queName.equals(_name)){
				if(debugMode)
					Debugger.verbose("QueScript", "shutDown... : " + _name);	
				_next.shutDown();
				if(debugMode)
					Debugger.verbose("QueScript", ".... shutDown : " + _name);	
			}
		}
	}

	/**
	 * shutdown all ques except the specified
	 * @param _exceptionName
	 */
	public void shutDownExcept(String _exceptionName){
		for(Cmnd e: myScript.getChildren()){
			CmndQue _next = (CmndQue)e;
			if(!_next.queName.equals(_exceptionName)){
				if(debugMode)
					Debugger.verbose("QueScript", "shutDownExcept.... : " + _exceptionName);	
				_next.shutDown();
				if(debugMode)
					Debugger.verbose("QueScript", ".... shutDownExcept: " + _exceptionName);	
			}
		}
	}

	/**
	 * resume all executed que's
	 */
	public void resume(){
		if(debugMode)
			Debugger.verbose("QueScript", "resume all executed que's");	
		for(Cmnd e: myScript.getChildren()){
			((CmndQue)e).resume();
		}
		if(debugMode)
			Debugger.verbose("QueScript", "... all paused que's are resumed");	
	}

	/**
	 * resume specified executed que
	 * @param _name
	 */
	public void resume(String _name){
		for(Cmnd e: myScript.getChildren()){
			CmndQue _next = (CmndQue)e;
			if(_next.queName.equals(_name)){
				if(debugMode)
					Debugger.verbose("QueScript", "resume paused que: " + _name);	
				_next.resume();
				if(debugMode)
					Debugger.verbose("QueScript", "... paused que is resumed playing: " + _name);	
			}
		}
	}

	/**
	 * pause all executed que's
	 */
	public void pause(){
		if(debugMode)
			Debugger.verbose("QueScript", "pause all executed que's");	
		for(Cmnd e: myScript.getChildren()){
			((CmndQue)e).pause();
		}
		if(debugMode)
			Debugger.verbose("QueScript", "... all executed que's are paused");	
	}

	/**
	 * pause specified executed que
	 * @param _name
	 */
	public void pause(String _name){
		for(Cmnd e: myScript.getChildren()){
			CmndQue _next = (CmndQue)e;
			if(_next.queName.equals(_name)){
				if(debugMode)
					Debugger.verbose("QueScript", "pause executed que: " + _name);	
				_next.pause();
				if(debugMode)
					Debugger.verbose("QueScript", "... executed que is paused: " + _name);	
			}
		}
	}

	/**
	 * Pause all que's except the specified
	 * @param _exceptionName
	 */
	public void pauseExcept(String _exceptionName){
		for(Cmnd e: myScript.getChildren()){
			CmndQue _next = (CmndQue)e;
			if(!_next.queName.equals(_exceptionName)){
				if(debugMode)
					Debugger.verbose("QueScript", "pause all, except.... : " + _exceptionName);	
				_next.pause();
				if(debugMode)
					Debugger.verbose("QueScript", "... paused all, except: " + _exceptionName);	
			}
		}
	}

	/**
	 * Stop all ques
	 */
	public void stop(){
		outputInfoMsg(QueMsgFactory.getMsg(QUELIST).add(QUELIST_STOP).done());

		if(debugMode)
			Debugger.verbose("QueScript", "stoping all...");	
		for(Cmnd e: myScript.getChildren()){
			((CmndQue)e).stop();
		}
		if(debugMode)
			Debugger.verbose("QueScript", "... all stopped");	
	}

	/**
	 * Stops only specified que
	 * @param _exception
	 */
	public void stop(String _name){
		for(Cmnd e: myScript.getChildren()){
			CmndQue _next = (CmndQue)e;
			if(_next.queName.equals(_name)){
				if(debugMode)
					Debugger.verbose("QueScript", "stopping... : " + _name);	
				_next.stop();
				if(debugMode)
					Debugger.verbose("QueScript", "...stopped: " + _name);	
			}
		}
	}

	/**
	 * Stops all ques except the specified
	 * @param _exceptionName
	 */
	public void stopExcept(String _exceptionName){
		for(Cmnd e: myScript.getChildren()){
			CmndQue _next = (CmndQue)e;
			if(!_next.queName.equals(_exceptionName)){
				if(debugMode)
					Debugger.verbose("QueScript", "stopExcept.... : " + _exceptionName);	
				_next.stop();
				if(debugMode)
					Debugger.verbose("QueScript", "... stopped Except: " + _exceptionName);	
			}
		}
	}

	public void progressupdate(int _frequency){
		viewplayingquesFreq = _frequency;
	}


	public void autostart(int _autostart){
		autostart = (_autostart == 1)? true: false;
	}
	
	public void trigger(String _triggerName, String[] args){
		triggerQueue.add(new CMsgTrigger(_triggerName, args));
	}
			
	public void printStructure(){
		myScript.printStructure();
	}

	protected void notifyDeleted(){
	}
	
	private static class MyErrorHandler extends DefaultHandler {
		public void warning(SAXParseException e) throws SAXException {
			printInfo(e);
		}

		public void error(SAXParseException e) throws SAXException {
			printInfo(e);
		}

		public void fatalError(SAXParseException e) throws SAXException {
			printInfo(e);
		}

		private void printInfo(SAXParseException e) {
			Debugger.error("QueScript", "Error at line(" + e.getLineNumber() + ") col(" + e.getColumnNumber() + 
					"): " + e.getMessage().substring(e.getMessage().indexOf(":")+2));
		}
	}

	public void setDebug(int _debug){
		debugMode = (_debug == 1)? true: false;
		myScript.setDebugMode(debugMode);
	}
	
	public void registerConnector(OutputConnector conn){
		this.conn = conn;
	}

	public void outputSendMsg(QueMessage msg) {
		conn.outputSendMsg(msg);
	}

	public void outputInfoMsg(QueMessage msg) {
		conn.outputInfoMsg(msg);
	}

	public void outputDumpMsg(QueMessage msg) {
		conn.outputDumpMsg(msg);
	}

	@Override
	public void selfCommand(String[] _comnd) {
		selfCommands.add(_comnd);
	}
}
