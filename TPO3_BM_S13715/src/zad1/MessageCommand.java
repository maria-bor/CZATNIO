package zad1;

@SuppressWarnings("serial")
public class MessageCommand implements ICommand{
	private String text;
	private String recipient;
	private String sender;
	
	public MessageCommand(String text, String recipient, String sender) {
		this.text = text;
		this.recipient = recipient;
		this.sender = sender;
	}

	public String getSender() {
		return sender;
	}

	public String getText() {
		return text;
	}

	public String getRecipient() {
		return recipient;
	}

	@Override
	public Object handle(Object... args) throws Exception {
		return null;
	}

}
