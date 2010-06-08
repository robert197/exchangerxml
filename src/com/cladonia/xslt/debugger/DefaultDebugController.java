
package com.cladonia.xslt.debugger;

public class DefaultDebugController extends DebugController {

public DefaultDebugController( BreakpointList stylesheetBreakpoints, BreakpointList inputBreakpoints) {
	super( stylesheetBreakpoints, inputBreakpoints);
}

public static void main(String[] args) {
	BreakpointList inputBreakpoints = new BreakpointList();
	BreakpointList stylesheetBreakpoints = new BreakpointList();

	DefaultDebugController debugController = new DefaultDebugController( stylesheetBreakpoints, inputBreakpoints);

	debugController.setXSLTProcessor("saxon1");

	debugController.setInputFilename("e:\\top\\dev\\xngrdbg\\test.xml");
	debugController.setStylesheetFilename("e:\\top\\dev\\xngrdbg\\test.xsl");


	//debugController.setInputFilename("e:\\diamond\\content\\loaded\\wileyml\\tie013\\tie013.xml");
	//debugController.setStylesheetFilename("e:\\diamond\\scripts\\render\\mrwml\\driver.xsl");

	debugController.setOutputFilename("e:\\top\\dev\\xngrdbg\\test.out");

	// TODO set stream for <xsl:message> output

	// set output stream
	debugController.setOutputStream(System.out);

	debugController.setParam("name1", "value1");
	debugController.setParam("name2", "value2");

	debugController.initializeDebugger();


	//debugController.setAutomated(true, 2000);

	debugController.startDebugger();

try
{
	Thread.sleep(5000);
}
catch (Exception ex) {}

System.out.println("\n****** before stopImmediately");

try
{


	debugController.stopImmediately();

}
catch (Exception ex) {}

System.out.println("\n****** after stopImmediately");


}

public void onStylesheetChange(int reason, XSLTStatus status)
{
}



public void onStylesheetBreakpoint(String stylesheetFilename, int stylesheetLineNumber)
{
	//System.out.println("\n******onStylesheetBreakpoint " + stylesheetFilename + " " + stylesheetLineNumber);

	String var = this.getVariableValue("var1");

	//System.out.println("\n\n\n+++++++ " + var + "+++++++\n\n\n");
}

public void onStylesheetLineChange(String stylesheetFilename, int stylesheetLineNumber)
{
	//System.out.println("\nonStylesheetLineChange " + stylesheetFilename + " " + stylesheetLineNumber);

}


public void onInputBreakpoint(String inputFilename, int inputLineNumber)
{
	//System.out.println("\n*******onInputBreakpoint " + inputFilename+ " " + inputLineNumber);

}


public void onInputLineChange(String inputFilename, int inputLineNumber)
{
	//System.out.println("\nonInputLineChange " + inputFilename+ " " + inputLineNumber);

}


public void onOpenOutputDocument(String outputFilename)
{
	System.out.println("\nonOpenOutputDocument " + outputFilename);


	String var = this.getVariableValue("outdir");

	System.out.println("\n    outdir " + var);


}

public void onCloseOutputDocument(String outputFilename)
{
	System.out.println("\nCloseOutputDocument " + outputFilename);
	//updateUI();

}


public void onStop(String stylesheetFilename, int stylesheetLineNumber, String inputFilename, int inputLineNumber)
{

	//System.out.println("\nonStop " + stylesheetFilename + " " + stylesheetLineNumber + " " + inputFilename + " " + inputLineNumber);
	updateUI();

}


public void onStart()
{
	//System.out.println("\nonStart ");
	this._debugger.command(XSLTDebugger.COMMAND_STEP);
}

public void onEnd()
{
	flushDebuggerOutput();

	//System.out.println("\nonEnd ");
}
public void onException(Exception ex)	{
  //TODO
}

public void stateChanged( int state) {
	// do whatever needs to be done on a change of state.
}

public String getVariableValue( String value) { 
	return null;
}
}