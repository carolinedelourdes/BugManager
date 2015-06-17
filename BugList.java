package bugmanager;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class BugList extends Shell {
	
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final String PROGRAM_ICON = "C:\\Users\\Mousey\\adv_prog_workspace\\BugManager\\bug.ico";
	
	private XmlManager xml;
	private List<Bug> bugs;
	private int numBugs;
	
	private Table table;


	/**
	 * Create the shell.
	 * @param display
	 */
	public BugList(Display display, XmlManager xml, int numBugs, List<Bug> bugs) {
		super(display, SWT.SHELL_TRIM);
		setSize(WIDTH,HEIGHT);
		setLayout(new FormLayout());
		
		// set up class members
		this.numBugs = numBugs;
		this.bugs = bugs;
		this.xml = xml;
		
		// set up table attributes
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		FormData fd_table = new FormData();
		fd_table.height = 0;
		fd_table.width = 0;
		fd_table.top = new FormAttachment(0, 5);
		fd_table.right = new FormAttachment(99);
		fd_table.bottom = new FormAttachment(0, 552);
		fd_table.left = new FormAttachment(0, 5);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		// Set up the table columns
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setResizable(false);
		tblclmnNewColumn.setWidth(51);
		tblclmnNewColumn.setText("ID");
		
		TableColumn tblclmnPriority = new TableColumn(table, SWT.NONE);
		tblclmnPriority.setWidth(60);
		tblclmnPriority.setText("Priority");
		
		TableColumn tblclmnStatus = new TableColumn(table, SWT.NONE);
		tblclmnStatus.setWidth(80);
		tblclmnStatus.setText("Status");
		
		TableColumn tblclmnTitle = new TableColumn(table, SWT.NONE);
		tblclmnTitle.setWidth(218);
		tblclmnTitle.setText("Title");
		
		TableColumn tblclmnAssignedTo = new TableColumn(table, SWT.NONE);
		tblclmnAssignedTo.setWidth(172);
		tblclmnAssignedTo.setText("Assigned To");
		
		TableColumn tblclmnCreated = new TableColumn(table, SWT.NONE);
		tblclmnCreated.setWidth(90);
		tblclmnCreated.setText("Created");
		
		TableColumn tblclmnView = new TableColumn(table, SWT.NONE);
		tblclmnView.setWidth(49);
		
		TableColumn tblclmnEdit = new TableColumn(table, SWT.NONE);
		tblclmnEdit.setWidth(50);
		
		// Set up each row (loop through bug list)
		
		for (int i = 0; i < this.numBugs; i++)
		{
			Bug b = this.bugs.get(i);
			this.AddTableRow(b,i);			
		}

	}

	public void AddTableRow(Bug b, final int rowPosition)
	{
		TableItem tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(GetRowText(b));

		Button View = new Button(table, SWT.NONE);
		View.setText("View");
		View.pack();
		View.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				OpenViewEidtBugShell(rowPosition,false);				
			}
		});
		
		TableEditor editor = new TableEditor (table);		
		editor.minimumWidth = View.getSize().x;
		editor.horizontalAlignment = SWT.LEFT;
		editor.setEditor(View, tableItem, 6);
		
		Button Edit = new Button(table, SWT.NONE);
		Edit.setText("Edit");
		Edit.pack();
		Edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				OpenViewEidtBugShell(rowPosition,true);					
			}
		});
		
		editor = new TableEditor (table);		
		editor.minimumWidth = Edit.getSize().x;
		editor.horizontalAlignment = SWT.LEFT;
		editor.setEditor(Edit, tableItem, 7);
		
	}
	
	private String[] GetRowText(Bug b)
	{
		return new String[]{ Integer.toString(b.getId()), Short.toString(b.getPriority()), b.getStatus(), b.getTitle(), b.getUserAssignedEmail(), Bug.getFormattedDate(b.getTimeAdded()) };
	}
	

	public void OpenViewEidtBugShell(int rowPosition, boolean editable)
	{
		try {
			Display display = Display.getDefault();
			// get the bug object based on row position 
			
			// Create Bug Shell 
			BugShell shell = new BugShell(display,xml,bugs.get(rowPosition),editable);
			shell.setImage(new Image(display,PROGRAM_ICON));
			shell.open();
			shell.layout();			
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			if (shell.SelectedUpdate)
			{
				table.getItem(rowPosition).setText(GetRowText(this.bugs.get(rowPosition)));		
				this.layout();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}					
	}

	public void OpenNewBugShell()
	{
		
		try {
			Display display = Display.getDefault();
			// generate a new Bug ID
			int NewID = xml.GetNextID();
			// Create Bug Shell 
			BugShell shell = new BugShell(display,xml,NewID);
			shell.setImage(new Image(display,PROGRAM_ICON));
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
				this.layout();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	
}
