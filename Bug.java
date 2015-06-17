package bugmanager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bug {
	
	private final int Id;
	private long TimeAdded;
	private long TimeLastModified;
	private String Title;
	private String BugShortDescription;
	private String BugLongDescription;
	private short BugPriority;
	private String BugStatus;
	private String UserReportedEmail;
	private String UserAssignedEmail = ""; // don't let this be null
		
	public Bug(int Id, long TimeAdded)
	{
		this.Id = Id;
		this.TimeAdded = TimeAdded;
		this.TimeLastModified = TimeAdded;
	}

	public Bug( int Id, 
				long TimeAdded,
				long TimeLastModified,
				String Title,
				String ShortDescription,
				String LongDescription,
				short Priority,
				String Status,
				String ReportedEmail,
				String AssignedEmail) throws EmailException
	{
		this.Id = Id;
		this.TimeAdded = TimeAdded;
		this.TimeLastModified = TimeLastModified;
		this.Title = Title;
		this.BugShortDescription = ShortDescription;
		this.BugLongDescription = LongDescription;
		this.BugPriority = Priority;
		this.BugStatus = Status;
		this.setUserAssignedEmail(AssignedEmail);
		this.setUserReportedEmail(ReportedEmail);
		
	}
		
	public int getId()
	{
		return this.Id;
	}
		
	public long getTimeAdded()
	{
		return this.TimeAdded;
	}
	
	public long getTimeLastModifed()
	{
		return this.TimeLastModified;
	}
	
	// set time last modified, return true if successful
	public boolean setTimeLastModifed(long NewTime)
	{
		this.TimeLastModified = NewTime;
		return true;
	}
	
	// For use with bug.TimeAdded and bug.TimeLastModified
	public static String getFormattedDate(long timestamp)
	{
		Date date = new Date(timestamp*1000); // *1000 is to convert seconds to milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy"); 
		return sdf.format(date);
	}
	
	public String getTitle()
	{
		return this.Title;
	}
	
	// set Title, return true if successful
	public boolean setTitle(String NewTitle) throws FieldLengthException
	{
		if(!(NewTitle.length()<41))
			throw new FieldLengthException("Field cannot exceed 40 characters.");
		this.Title = NewTitle; 
		return true; // No Exception thrown, therefore valid.
	}
	
	public String getShortDescription()
	{
		return this.BugShortDescription;
	}
	
	// set short description, return true if successful
	public boolean setShortDescription(String NewDescription) throws FieldLengthException
	{
		if(!(NewDescription.length()<256))
			throw new FieldLengthException("Field cannot exceed 255 characters.");
		this.BugShortDescription = NewDescription;
		return true; // No Exception thrown, therefore valid.
	}
	
	public String getLongDescription()
	{
		return this.BugLongDescription;
	}
	
	// set long description, return true if successful
	public boolean setLongDescription(String NewDescription)
	{
		// No limit on how long this can be.
		this.BugLongDescription = NewDescription;
		return true; // This will always be true.
	}
	
	public short getPriority(){
		return this.BugPriority;
	}
	
	// set priority, return true if successful
	public boolean setPriority(short NewPriority){
		this.BugPriority = NewPriority;
		return true;
	}
	
	public String getStatus(){
		return this.BugStatus;
	}
	
	// set status, return true if successful
	public boolean setStatus(String NewStatus){
		this.BugStatus = NewStatus;
		return true;
	}

	public String getUserReportedEmail(){
		return this.UserReportedEmail;
	}
	
	// Set E-mail only if NOT empty string AND valid email
	// return true if successful
	public boolean setUserReportedEmail(String Email) throws EmailException{
		if(isValidEmailLength(Email) && isValidEmail(Email))
			this.UserReportedEmail = Email;
		return true; // No Exception thrown, therefore valid.
	}

	// Return the email of the user assigned to the bug.
	public String getUserAssignedEmail(){
		return this.UserAssignedEmail;
	}
	
	// Set E-mail only if empty string OR a valid email
	// Return true if successful 
	public boolean setUserAssignedEmail(String Email) throws EmailException{
		if(isValidEmail(Email))
			this.UserAssignedEmail = Email;			
		return true; // No Exception thrown, therefore valid.
	}
	
	// Determine if the e-mail is a minimum amount of characters,
	// if not, throw an exception
	public boolean isValidEmailLength(String Email) throws EmailException
	{
		// if not at least 6 chars , ex: a@a.co
		if(Email.length()<6)
			throw new EmailException("E-mail must be at least 6 characters.");
		return true; // No Exception thrown, therefore valid.
	}
	
	// Determine if the e-mail, if any, is a valid e-mail,
	// if not, throw an exception
	// it is OK if the email string is empty
	public boolean isValidEmail(String Email) throws EmailException
	{
		if(Email.length() > 0)
		{
			// Create RegEx pattern for a valid e-mail (no special characters are allowed)
			// double-escape RegEx escape characters
			Pattern p = Pattern.compile("[A-z0-9_\\.\\-]+@[A-z0-9_\\.\\-]+\\.[A-z]{2,3}");
			Matcher m = p.matcher(Email);
			if(!(m.matches()))
				throw new EmailException("E-mail has invalid or missing characters.");			 
		}		
		return true; // No Exception thrown, therefore valid.
	}
	
}

