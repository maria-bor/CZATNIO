package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;

public class BaseCommunication {
	protected SocketChannel scMain;
	protected Selector selector;
	// Strona kodowa do kodowania/dekodowania buforów
	private static Charset charset  = Charset.forName("ISO-8859-2");
	private static final int BSIZE = 1024;
	// Tu będzie zlecenie do pezetworzenia
	protected StringBuffer reqString = new StringBuffer();

	// Bufor bajtowy - do niego są wczytywane dane z kanału
	private ByteBuffer bbuf = ByteBuffer.allocate(BSIZE);
	
	private static String commands[] = {"Register", "Unregister", "Login", "Logout", "Message", "MessageResponse", "BroadcastLogin", "BroadcastLogout"};

	public BaseCommunication() {
		try {
			this.selector = SelectorProvider.provider().openSelector();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void read(SelectionKey key) throws Exception {
		SocketChannel scLocal = (SocketChannel) key.channel();

		if (!scLocal.isOpen()) return;
		// Odczytanie zlecenia
		reqString.setLength(0);
		bbuf.clear();
		try {
			readLoop:                    // Czytanie jest nieblokujące
				while (true) {               // kontynujemy je dopóki
					int n = scLocal.read(bbuf);     // nie natrafimy na koniec wiersza
					if (n > 0) {
						bbuf.flip();
						CharBuffer cbuf = charset.decode(bbuf);
						while(cbuf.hasRemaining()) {
							char c = cbuf.get();
							if (c == '\r' || c == '\n') break readLoop;
							reqString.append(c);
						}					
					}
				}
		} catch (Exception exc) {                  // przerwane polączenie?
			exc.printStackTrace();
			try { 
				scLocal.close();
				scLocal.socket().close();
			} catch (Exception e) {}
		}
	}

	protected void send(SocketChannel scLocal, String... args) throws IOException {
		String str = String.join(" ", args);
		str += "\n";
		ByteBuffer buf = charset.encode(CharBuffer.wrap(str));
		scLocal.write(buf);
	}
	
	protected void send(String... args) throws IOException {
		send(this.scMain, args);
	}
}
