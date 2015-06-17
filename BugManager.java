/*
 * Filename: BugManager.java
 * 
 * Purpose: Provide front-end GUI for end-users, 
 * and uses the XmlManager class to indirectly manipulate Bug objects.
 * 
 * References
 * 
 * SWT - A Developer's Notebook. Tim Hatton, O'Reilly Media 2005
 * 
 * Change Log
 * 
 * 01/31/15	-	file created
 * 02/01/15	-	added swt components
 * 02/07/15	-	integrate with BugShell class
 * 02/16/15	-	integrate with BugList class
 * 02/17/15	-	completed new bug function
 * 02/18/15	-	completed view/edit bug function
 * 
 */
package bugmanager;

import java.util.List;

import org.eclipse.swt.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class BugManager {
	
	private XmlManager xml;
	private List<Bug> bugs;
	private int numBugs;
	
	private static final String PROGRAM_NAME = "Bug Manager";
	private static final String PROGRAM_ICON = "bug.ico";
	
	private BugList s;
	private Display d;
	
	public static void main(String[] args) {
		
		try {
			BugManager manager = new BugManager();
		} catch (Exception e) {
			e.printStackTrace();
			DisplayErrorMessage("There was a problem reading the data file.");
		} 
		
	}
	
	public BugManager() throws Exception
	{		
		this.xml = new XmlManager("src/bugmanager/data.xml");	
		this.bugs = xml.getBugList();
		this.numBugs = xml.getNumBugs();
		// set up the GUI.
		this.init();
	}
	
	public void init()
	{
		this.d = new Display();
		// set the main screen to be the list of bugs
		this.s = new BugList(this.d, this.xml, this.numBugs, this.bugs);
		
		s.setText(PROGRAM_NAME+" - Home");
		s.setImage(new Image(d,PROGRAM_ICON));
		
		Menu m = new Menu(s,SWT.BAR);
		s.setMenuBar(m);
		
		final MenuItem file = new MenuItem(m, SWT.CASCADE);
		file.setText("&File");
		
		final Menu filemenu = new Menu(s, SWT.DROP_DOWN);
		file.setMenu(filemenu);
		
		final MenuItem openItem = new MenuItem(filemenu, SWT.PUSH);
		openItem.setText("&New Bug\tCTRL+N");
		openItem.setAccelerator(SWT.CTRL+'N');
		
		openItem.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {		
				MakeNewBugShell();
			}
		});
		
		final MenuItem seperator = new MenuItem(filemenu, SWT.SEPARATOR);
		
		final MenuItem exitItem = new MenuItem(filemenu, SWT.PUSH);
		exitItem.setText("Exit");
		
		exitItem.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {		
				ConfirmExit();
			}
		});
			
		s.open();
				
		while(!s.isDisposed()){
			if(!d.readAndDispatch())
				d.sleep();
		}
		
		try {
			xml.SaveChanges();
		} catch (Exception e1) {
			System.out.println("The changes could not be saved to the XML file.");
			e1.printStackTrace();
		}
	}
	
	// Display the form to create a new bug.
	public void MakeNewBugShell()
	{
		
		try {
			Display display = Display.getDefault();
			// generate a new Bug ID
			int NewID = xml.GetNextID();
			// Create Bug Shell 
			BugShell shell = new BugShell(display,xml,NewID);
			shell.setImage(new Image(d,PROGRAM_ICON));
			shell.open();
			shell.layout();			
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			// Update Bug List, if the shell was closed as a result of selecting Create
			if(shell.SelectedCreate)
			{
				this.bugs = xml.getBugList();
				this.numBugs = xml.getNumBugs();			
				this.s.AddTableRow(this.bugs.get(this.numBugs-1),this.numBugs-1);
				this.s.layout();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
				
	}
	

	public void ConfirmExit()
	{
		MessageBox messageBox = new MessageBox(s, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setMessage("Do you really want to exit?");
		messageBox.setText("Exiting Application");
		int response = messageBox.open();
		if(response == SWT.YES)
			System.exit(0);
	}
	
	public static void DisplayErrorMessage(String message)
	{
		System.out.println(message);
	}
	


}