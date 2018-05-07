package zad1;

import zad1.ICommand.IResponse;

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
		return new Response(sender, recipient, (String)args[0]);
	}

	public class Response implements IResponse {
		private String recipient;
		private String sender;
		private String result;
		
		public Response(String recipient, String sender, String result) {
			this.recipient = recipient;
			this.sender = sender;
			this.result = result;
		}

		public String getResult() {
			return result;
		}

		public String getRecipient() {
			return recipient;
		}

		public String getSender() {
			return sender;
		}
	}
}
