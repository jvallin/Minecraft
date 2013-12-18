package balckangel.EditSign;

public class Message
{
	int nuLigne;
	String message;
	
	public Message(int i, String s)
	{
		nuLigne = i;
		message = s;
	}
	
	public int getNuLigne()
	{
		return nuLigne;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public String toString()
	{
		return (nuLigne+" "+message);
	}
}
