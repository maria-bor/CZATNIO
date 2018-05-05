package zad1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Map;

public class BaseCommunication {
	protected ICommand cmd;
	protected Map<SocketChannel, Integer> dataSizeToRead;
	protected SocketChannel sc;
	protected Selector selector;
	
	public BaseCommunication() {
		this.dataSizeToRead = new HashMap<SocketChannel, Integer>();
		try {
			this.selector = SelectorProvider.provider().openSelector();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void read(SelectionKey key) {
		System.out.println("READ()");
		this.sc = (SocketChannel) key.channel();		
		Integer s = dataSizeToRead.get(sc);
		int numbetOfBytes = 0;
		int len = 0;
		
		if (s == null) {
			ByteBuffer bb = ByteBuffer.allocate(4);
			try {				
				numbetOfBytes = sc.read(bb);
				if (numbetOfBytes != 4) {
					System.out.println("#########");
				}
			} 
			catch (IOException e) {
			e.printStackTrace();
			key.cancel();
			}
			
			if (numbetOfBytes == -1) {
				try {
					key.channel().close();
					System.out.println("@@@@@@@@@@");
				} catch (IOException e) {
					e.printStackTrace();
				}
				key.cancel();
				return;
			}
			
			if (numbetOfBytes == 4) {
				// byte[] => int
				for (int i = 0; i < 4; ++i) {
					len |= (bb.array()[3-i] & 0xff) << (i << 3);
				}
				dataSizeToRead.put(sc, len);
			}
			else {
				dataSizeToRead.put(sc, null);
				len = s.intValue();
			}
		}
		else {
			len = s.intValue();
		}
		ByteBuffer bb2 = ByteBuffer.allocate(len);
		try {
			numbetOfBytes = sc.read(bb2);
		} catch (IOException e) {
			e.printStackTrace();
			key.cancel();
			return;
		}
		
		if (numbetOfBytes == -1) {
			try {
				key.channel().close();
				System.out.println("$$$$$$$$$$");
			} catch (IOException e) {
				e.printStackTrace();
			}
			key.cancel();
			return;
		}
		
		if (numbetOfBytes == len) {
			dataSizeToRead.put(sc, null);
			ByteArrayInputStream bais = new ByteArrayInputStream(bb2.array());
			try(ObjectInputStream ois = new ObjectInputStream(bais)) {
				this.cmd = (ICommand) ois.readObject();			
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}		
	}
	
	protected void send(SocketChannel sc, Object result) {
		byte[] size = new byte[4];
		byte[] data;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try(ObjectOutputStream oos = new ObjectOutputStream(baos)){
			oos.writeObject(result);
			oos.flush();
			data = baos.toByteArray();
			
			int number = data.length;;
			// int -> byte[]
			for (int i = 0; i < 4; ++i) {
				int shift = i << 3; // i * 8
				size[3-i] = (byte)((number & (0xff << shift)) >>> shift);
			}
			
			ByteBuffer sizeBB = ByteBuffer.wrap(size);
			ByteBuffer dataBB = ByteBuffer.wrap(data);
			sc.write(sizeBB);
			sc.write(dataBB);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
 	}
}
