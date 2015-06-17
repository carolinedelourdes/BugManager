package bugmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class BugShell extends Shell {
	
	private final String type;
	
	// Display Properties
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final String PROGRAM_NAME = "Bug Manager";
	private String ShellTitle; 
	private String RequiredMark = ""; // if New, this is an asterisk
	
	// Bug Properties - business logic	
	private Label BugIdLabel =  new Label(this, SWT.NONE);
	private Text BugIdText;
	private int BugIDValue;
	
	private Label TitleLabel;
	private boolean TitleEditable;
	private Text TitleText;	
	private String TitleValue;
	
	private Label SummaryLabel;
	private boolean SummaryEditable;
	private Text SummaryText;
	private String SummaryValue;
	
	private final Label AssignedLabel;
	private boolean AssignedEditable;
	private Text AssignedText;
	private String AssignedValue;
	
	private final Label ReporterLabel;
	private boolean ReporterEditable;
	private Text ReporterText;
	private String ReporterValue;
	
	private Label DescriptionLabel;
	private boolean DescriptionEditable;
	private Text DescriptionText;
	private String DescriptionValue;
	
	private Label CreatedDateLabel;
	private Text CreatedDateText;
	private String CreatedValue;
	
	private Label LastModifiedLabel;
	private Text LastModifiedText;
	private String ModifiedValue;
	
	private Label PriorityLabel;
	private boolean PriorityEditable;
	private short SelectedPriority; // 1, 2, or 3
	
	private Label  StatusLabel;
	private boolean StatusEditable;
	private final String[] StatusOptions = { "New", "Assigned", "In Progress", "On Hold", "Closed" };
	private int SelectedStatus; // array position
	
	private String SubmitButtonTitle;

	// misc
	
	private final XmlManager xml;	
	final private Shell thisShell; 
	
	private Bug bug; // current bug being viewed/edited, if applicable
	
	public boolean SelectedCreate = false;
	public boolean SelectedUpdate = false;
	public boolean SelectedClose = false;
	
	/**
	 * Create the shell.
	 * @param display
	 */
	
	// Create New Bug Shell for an Existing Bug
	
	public BugShell(Display display, XmlManager xml, Bug b, boolean editable) {
		super(display, SWT.SHELL_TRIM);

		this.xml = xml;
		this.bug = b;
		this.AssignedLabel =  new Label(this, SWT.NONE);
		this.ReporterLabel =  new Label(this, SWT.NONE);
		
		// set Edit/View settings
		if(editable)
		{
			this.type = "edit";
			ShellTitle = "Edit Bug (#"+b.getId()+")";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
			Date date = new Date();
			ModifiedValue =  dateFormat.format(date);
			SubmitButtonTitle = "Update";
		}
		else
		{
			this.type = "view";
			ShellTitle = "View Bug (#"+b.getId()+")";
			ModifiedValue = Bug.getFormattedDate(b.getTimeLastModifed());
			SubmitButtonTitle = "OK";
		}
			
		// helper object because this cannot be referenced directly in widgetSelected
		thisShell = this;

		// set bug attributes, use "editable" flag to set fields as non-editible when applicable
		BugIDValue = b.getId(); 
		TitleEditable = editable;
		TitleValue = b.getTitle();
		SummaryEditable = editable;
		SummaryValue = b.getShortDescription();
		AssignedEditable = editable;
		AssignedValue = b.getUserAssignedEmail();
		ReporterEditable =  false; // User reported email will never be editable.
		ReporterValue = b.getUserReportedEmail();
		DescriptionEditable = editable;
		DescriptionValue = b.getLongDescription();
		CreatedValue = Bug.getFormattedDate(b.getTimeAdded());
		SelectedPriority = b.getPriority();
		PriorityEditable = editable;
		SelectedStatus = setStatusSelection(b.getStatus());
		StatusEditable = editable;
			
		createContents();
		
	}
	
	// Create Bug Shell for a New Bug
	/**
	 * @wbp.parser.constructor
	 */
	public BugShell(Display display, XmlManager xml, int NewID) {
		super(display, SWT.SHELL_TRIM);

		this.AssignedLabel =  new Label(this, SWT.NONE);
		this.ReporterLabel =  new Label(this, SWT.NONE);
		this.xml = xml;
		this.ShellTitle = "New Bug (#"+NewID+")";
		
		this.type = "new";
		
		// helper object because this cannot be referenced directly in widgetSelected
		thisShell = this;
		
		// bug properties
		BugIDValue = NewID; 
		TitleEditable = true;
		TitleValue = "";
		SummaryEditable = true;
		SummaryValue = "";
		AssignedEditable = true;
		AssignedValue = "";
		ReporterEditable =  true;
		ReporterValue = "";
		DescriptionEditable = true;
		DescriptionValue = "";
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		Date date = new Date();
		CreatedValue = dateFormat.format(date);
		ModifiedValue = "Never";		
		SelectedPriority = 3; // Set Priority to Low
		PriorityEditable = true;
		SelectedStatus = 0; // Set Status to 1st Option
		StatusEditable = false; // Status cannot be changed from New
		SubmitButtonTitle = "Create";
			
		createContents();
		
	}
		

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText(PROGRAM_NAME+" - "+ShellTitle);
		setSize(WIDTH, HEIGHT);
		setLayout(new FormLayout());
		
		// Bug Id Label
		BugIdLabel = new Label(this, SWT.NONE);
		FormData fd_BugIdLabel = new FormData();
		fd_BugIdLabel.top = new FormAttachment(0, 8);
		fd_BugIdLabel.left = new FormAttachment(0, 51);
		BugIdLabel.setLayoutData(fd_BugIdLabel);
		BugIdLabel.setText("Bug ID");
		
		// Bug Id Text Input
		BugIdText = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_BugIdText = new FormData();
		fd_BugIdText.top = new FormAttachment(0, 5);
		fd_BugIdText.left = new FormAttachment(0, 101);
		BugIdText.setLayoutData(fd_BugIdText);
		// Bug ID is not Editable
		BugIdText.setEnabled(false);
		// Set Bug ID - this is generated if type="new'
		BugIdText.setText(Integer.toString(BugIDValue));
		
		// Title Label
		TitleLabel = new Label(this, SWT.NONE);
		FormData fd_TitleLabel = new FormData();
		fd_TitleLabel.top = new FormAttachment(0, 39);
		fd_TitleLabel.left = new FormAttachment(0, 67);
		TitleLabel.setLayoutData(fd_TitleLabel);
		TitleLabel.setText("Title");
		
		// Title Text Input
		TitleText = new Text(this, SWT.BORDER);
		FormData fd_TitleText = new FormData();
		fd_TitleText.right = new FormAttachment(BugIdText, 0, SWT.RIGHT);
		fd_TitleText.top = new FormAttachment(0, 36);
		fd_TitleText.left = new FormAttachment(0, 101);
		TitleText.setLayoutData(fd_TitleText);
		TitleText.setText(TitleValue);
		TitleText.setEnabled(TitleEditable);
		
		// Priority Label
		PriorityLabel = new Label(this, SWT.NONE);
		FormData fd_PriorityLabel = new FormData();
		fd_PriorityLabel.top = new FormAttachment(0, 67);
		fd_PriorityLabel.left = new FormAttachment(0, 49);
		PriorityLabel.setLayoutData(fd_PriorityLabel);
		PriorityLabel.setText("Priority");
			
		// Priority Radio Buttons
		final Button PriorityHighRadio = new Button(this, SWT.RADIO);
		FormData fd_PriorityHighRadio = new FormData();
		fd_PriorityHighRadio.left = new FormAttachment(PriorityLabel, 5);
		PriorityHighRadio.setLayoutData(fd_PriorityHighRadio);
		PriorityHighRadio.setText("1 High");
		PriorityHighRadio.setEnabled(PriorityEditable);

		final Button PriorityMediumRadio = new Button(this, SWT.RADIO);
		fd_PriorityHighRadio.top = new FormAttachment(PriorityMediumRadio, 0, SWT.TOP);
		FormData fd_PriorityMediumRadio = new FormData();
		fd_PriorityMediumRadio.left = new FormAttachment(PriorityHighRadio, 14);
		PriorityMediumRadio.setLayoutData(fd_PriorityMediumRadio);
		PriorityMediumRadio.setText("2 Medium");
		PriorityMediumRadio.setEnabled(PriorityEditable);
		
		final Button PriorityLowRadio = new Button(this, SWT.RADIO);
		fd_PriorityMediumRadio.top = new FormAttachment(PriorityLowRadio, 0, SWT.TOP);
		FormData fd_PriorityLowRadio = new FormData();
		fd_PriorityLowRadio.left = new FormAttachment(PriorityMediumRadio, 17);
		PriorityLowRadio.setLayoutData(fd_PriorityLowRadio);
		PriorityLowRadio.setText("3 Low");
		PriorityLowRadio.setEnabled(PriorityEditable);

		// Set Selected if Applicable)
		if(SelectedPriority == 1)
			PriorityHighRadio.setSelection(true);
		else if(SelectedPriority == 2)
			PriorityMediumRadio.setSelection(true);
		else if(SelectedPriority == 3)
			PriorityLowRadio.setSelection(true);
		
		// Status Label
		StatusLabel = new Label(this, SWT.NONE);
		FormData fd_StatusLabel = new FormData();
		fd_StatusLabel.top = new FormAttachment(TitleText, 6);
		StatusLabel.setLayoutData(fd_StatusLabel);
		StatusLabel.setText("Status");
		
		// Status Dropdown
		final Combo StatusCombo = new Combo(this, SWT.READ_ONLY);
		fd_BugIdText.right = new FormAttachment(StatusCombo, 0, SWT.RIGHT);
		fd_PriorityLowRadio.top = new FormAttachment(StatusCombo, 3, SWT.TOP);
		fd_StatusLabel.right = new FormAttachment(StatusCombo, -6);
		FormData fd_StatusCombo = new FormData();
		fd_StatusCombo.top = new FormAttachment(TitleText, 6);
		fd_StatusCombo.left = new FormAttachment(0, 505);
		fd_StatusCombo.right = new FormAttachment(100, -5);
		StatusCombo.setLayoutData(fd_StatusCombo);
		StatusCombo.setItems(StatusOptions);
		
		// Set Combo Selection
		StatusCombo.select(SelectedStatus); 
		
		// If Status is not Editable
		if(!StatusEditable)
			StatusCombo.setEnabled(StatusEditable);

		// Summary Label
		SummaryLabel = new Label(this, SWT.NONE);
		FormData fd_SummaryLabel = new FormData();
		fd_SummaryLabel.top = new FormAttachment(PriorityLabel, 18);
		fd_SummaryLabel.right = new FormAttachment(BugIdLabel, 0, SWT.RIGHT);
		SummaryLabel.setLayoutData(fd_SummaryLabel);
		SummaryLabel.setText("Summary");
		
		// Summary input
		SummaryText = new Text(this, SWT.BORDER);
		FormData fd_SummaryText = new FormData();
		fd_SummaryText.right = new FormAttachment(BugIdText, 0, SWT.RIGHT);
		fd_SummaryText.left = new FormAttachment(SummaryLabel, 5);
		fd_SummaryText.top = new FormAttachment(StatusCombo, 6);
		SummaryText.setLayoutData(fd_SummaryText);
		SummaryText.setText(SummaryValue);
		SummaryText.setEnabled(SummaryEditable);
		
		// Reporter Label
		FormData fd_ReporterLabel = new FormData();
		fd_ReporterLabel.top = new FormAttachment(SummaryLabel, 90);
		fd_ReporterLabel.right = new FormAttachment(BugIdLabel, 0, SWT.RIGHT);
		ReporterLabel.setLayoutData(fd_ReporterLabel);
		ReporterLabel.setText(RequiredMark + "Reporter");
		
		// Reporter input
		ReporterText = new Text(this, SWT.BORDER);
		fd_SummaryText.bottom = new FormAttachment(100, -348);
		FormData fd_ReporterText = new FormData();
		fd_ReporterText.right = new FormAttachment(BugIdText, 0, SWT.RIGHT);
		fd_ReporterText.top = new FormAttachment(SummaryText, 6);
		fd_ReporterText.left = new FormAttachment(ReporterLabel, 5);
		ReporterText.setLayoutData(fd_ReporterText);
		ReporterText.setText(ReporterValue);
		ReporterText.setEnabled(ReporterEditable);
		
		//AssignedLabel = new Label(this, SWT.NONE);
		FormData fd_AssignedLabel = new FormData();
		fd_AssignedLabel.top = new FormAttachment(ReporterLabel, 15);
		fd_AssignedLabel.right = new FormAttachment(BugIdLabel, 0, SWT.RIGHT);
		AssignedLabel.setLayoutData(fd_AssignedLabel);
		AssignedLabel.setText("Assigned");
		
		AssignedText = new Text(this, SWT.BORDER);
		FormData fd_AssignedText = new FormData();
		fd_AssignedText.right = new FormAttachment(BugIdText, 0, SWT.RIGHT);
		fd_AssignedText.top = new FormAttachment(ReporterText, 6);
		fd_AssignedText.left = new FormAttachment(AssignedLabel, 5);
		AssignedText.setLayoutData(fd_AssignedText);
		AssignedText.setText(AssignedValue);
		AssignedText.setEnabled(AssignedEditable);
		
		DescriptionLabel = new Label(this, SWT.NONE);
		FormData fd_DescriptionLabel = new FormData();
		fd_DescriptionLabel.top = new FormAttachment(AssignedLabel, 6);
		fd_DescriptionLabel.right = new FormAttachment(BugIdLabel, 0, SWT.RIGHT);
		DescriptionLabel.setLayoutData(fd_DescriptionLabel);
		DescriptionLabel.setText("Description");
		
		DescriptionText = new Text(this, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_DescriptionText = new FormData();
		fd_DescriptionText.right = new FormAttachment(BugIdText, 0, SWT.RIGHT);
		fd_DescriptionText.top = new FormAttachment(AssignedText, 6);
		fd_DescriptionText.left = new FormAttachment(DescriptionLabel, 5);
		DescriptionText.setLayoutData(fd_DescriptionText);
		DescriptionText.setText(DescriptionValue);
		DescriptionText.setEnabled(DescriptionEditable);
		
		CreatedDateLabel = new Label(this, SWT.NONE);
		fd_DescriptionText.bottom = new FormAttachment(CreatedDateLabel, -31);
		FormData fd_CreatedDateLabel = new FormData();
		fd_CreatedDateLabel.left = new FormAttachment(0, 101);
		fd_CreatedDateLabel.top = new FormAttachment(0, 415);
		CreatedDateLabel.setLayoutData(fd_CreatedDateLabel);
		CreatedDateLabel.setText("Created Date");
		
		CreatedDateText = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_CreatedDateText = new FormData();
		fd_CreatedDateText.top = new FormAttachment(DescriptionText, 31);
		fd_CreatedDateText.left = new FormAttachment(CreatedDateLabel, 6);
		CreatedDateText.setLayoutData(fd_CreatedDateText);
		CreatedDateText.setText(CreatedValue);
		CreatedDateText.setEnabled(false);
		
		LastModifiedLabel = new Label(this, SWT.NONE);
		fd_CreatedDateText.right = new FormAttachment(100, -371);
		FormData fd_LastModifiedLabel = new FormData();
		fd_LastModifiedLabel.top = new FormAttachment(CreatedDateLabel, 0, SWT.TOP);
		LastModifiedLabel.setLayoutData(fd_LastModifiedLabel);
		LastModifiedLabel.setText("Last Modified");
		
		LastModifiedText = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		fd_LastModifiedLabel.right = new FormAttachment(LastModifiedText, -6);
		FormData fd_LastModifiedText = new FormData();
		fd_LastModifiedText.left = new FormAttachment(0, 561);
		fd_LastModifiedText.right = new FormAttachment(100, -5);
		fd_LastModifiedText.top = new FormAttachment(DescriptionText, 31);
		LastModifiedText.setLayoutData(fd_LastModifiedText);
		LastModifiedText.setText(ModifiedValue);
		LastModifiedText.setEnabled(false);

		Button CancelButton = new Button(this, SWT.NONE);
		FormData fd_CancelButton = new FormData();
		fd_CancelButton.left = new FormAttachment(0, 454);
		fd_CancelButton.bottom = new FormAttachment(100, -10);		
	
		Button SubmitButton = new Button(this, SWT.NONE);
		FormData fd_SubmitButton = new FormData();
		fd_SubmitButton.right = new FormAttachment(BugIdText, 0, SWT.RIGHT);
		fd_SubmitButton.left = new FormAttachment(0, 561);
		fd_SubmitButton.bottom = new FormAttachment(100, -10);
		SubmitButton.setLayoutData(fd_SubmitButton);
		SubmitButton.setText(SubmitButtonTitle);
		
		// define behavior for when create button is selected
		final String type = this.type;
		SubmitButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
				// Get all the bug data.
				int Id = Integer.parseInt(BugIdText.getText());
				String Title = TitleText.getText();
				String BugShortDescription = SummaryText.getText();
				String UserAssignedEmail = AssignedText.getText();
				String UserReportedEmail = ReporterText.getText();
				String BugLongDescription = DescriptionText.getText();	
				String BugStatus = StatusCombo.getText();
				short Priorty = getSelectedPriority(PriorityHighRadio, PriorityMediumRadio, PriorityLowRadio);
				
				// Current Time - helper for assigning DateAdded or DateLastModified
				long unixTime = System.currentTimeMillis() / 1000L;
				
				// Flag for errors in the form.				
				boolean AnyErrors = false;	
				
				// Keep track of errors, reset each time
				String Errors = new String("");
				
				// Type indicates if View, New, or Edit screen, assigned at Shell creation.
				switch(type)
				{
				case "view":
					// Close the shell without doing anything.
					close();
					break;
				case "edit":	
					// set flag for parent classes
					SelectedUpdate = true;
					
					// Set fields common to New and Edit
					Errors =  (SetCommonBugFields(bug, Title, Priorty, BugStatus, BugShortDescription, BugLongDescription, UserAssignedEmail)); 
					// If any error messages were produced.
					if(Errors.length() > 0)
						AnyErrors = true;
					
					// Set fields unique to Edit				
					bug.setTimeLastModifed(unixTime);
					
					// If no errors, continue to the next steps.
					// Otherwise, user must modify fields and resubmit.
					if(!AnyErrors)
					{
						// Pass off to XmlManager
						xml.UpdateBug(bug);		
						close();
					}
					else
					{
						DisplayErrorDialogue(Errors);
					}
					
					break; // exit switch
					
				case "new":
					// set flag for parent classes
					SelectedCreate = true;
					
					// Get Current Time for New Bug Creation date.
					long TimeAdded = unixTime;							
					
					// Create A new Bug
					Bug bug = new Bug(Id, TimeAdded);
					
					// Set fields common to New and Edit
					Errors = (SetCommonBugFields(bug, Title, Priorty, BugStatus, BugShortDescription, BugLongDescription, UserAssignedEmail)); 
					// If any error messages were produced.
					if(Errors.length() > 0)
						AnyErrors = true;
					
					// Set fields unique to New										
					bug.setTimeLastModifed(unixTime);	
					
					// Reset colors.
					ReporterLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
					
					//Validate Reported Email
					try {
						bug.setUserReportedEmail(UserReportedEmail);
					} catch (EmailException e1) {
						ReporterLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
						Errors += "\n Invalid Reporter Email.";
						AnyErrors = true;
					}		
					
					// If no errors, continue to the next steps.
					// Otherwise, user must modify fields and resubmit.
					if(!AnyErrors)
					{
						try {							
							xml.AddNewBug(bug);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("The bug could not be added.");
							e.printStackTrace();
						}						
						// Close the shell
						close();
						break; // exit switch			
					}
					else
					{
						DisplayErrorDialogue(Errors);
					}
				}				
			}
		});
		
	
		CancelButton.setLayoutData(fd_CancelButton);
		// set behavior for cancel button
		CancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// confirm cancellation 
				MessageBox messageBox = new MessageBox(thisShell,  SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setMessage("Are you sure you wish to cancel?");
				messageBox.setText("Cancel New Bug");
				int response = messageBox.open();
				if(response == SWT.YES)
					close();
			}
		});
		CancelButton.setText("Cancel");		
		fd_CancelButton.right = new FormAttachment(SubmitButton, -22);			
	}
	
	// Set Bug Fields common to New and Edit shells.
	private String SetCommonBugFields(
			Bug bug, 
			String Title, 
			short Priorty, 
			String BugStatus, 
			String BugShortDescription, 
			String BugLongDescription,
			String UserAssignedEmail)
	{
		String Errors = "";
		
		//  Reset colors.
		AssignedLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		TitleLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		
		// Validate Assigned Email
		try {
			bug.setUserAssignedEmail(UserAssignedEmail);
		} catch (EmailException e) {
			AssignedLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
			Errors += "Invalid Assigned Email.\n";
			e.printStackTrace();
		}
		
		// Validate Title (max 40 char)
		try {
			bug.setTitle(Title);
		} catch (FieldLengthException e) {
			TitleLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
			Errors += "Title is too loo long (Must be 40 characters or less).\n";
			e.printStackTrace();
		}
		
		bug.setPriority(Priorty);
		bug.setStatus(BugStatus);
		
		// Validate Summary (max 255 char)
		try {
			bug.setShortDescription(BugShortDescription);
			// set color to black in case previous submit made this red, and it's valid now
			SummaryLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		} catch (FieldLengthException e) {
			SummaryLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
			Errors += "Summary is too long (must be 255 characters or less).\n";
			e.printStackTrace();
		}
		
		bug.setLongDescription(BugLongDescription);
		
		return Errors;
		
	}
	
	// Display a dialogue box if there are errors.
	private void DisplayErrorDialogue(String Errors)
	{
		MessageBox messageBox = new MessageBox(this, SWT.ICON_ERROR | SWT.OK);
		
		messageBox.setMessage(Errors);
		messageBox.setText("One or more errors occurred.");
		messageBox.open();
	}
	
	// loop through status options array and return matching index
	private int setStatusSelection(String status)
	{
		int numSelections = StatusOptions.length;
		
		for(int i=0; i < numSelections; i++)
		{
			if(StatusOptions[i].equals(status))
				return i;
		}
		
		return -1;
	}
	
	// determine which priority radio button is selected and return appropriate value
	private short getSelectedPriority(Button High, Button Medium, Button Low)
	{
		if (High.getSelection())
			return 1;
		else if (Medium.getSelection())
			return 2;		
		return 3;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
