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
	protected Object dataPackage;
	protected Map<SocketChannel, Integer> dataSizeToRead;
	protected SocketChannel scMain;
	protected Selector selector;
	
	public BaseCommunication() {
		System.out.println("BaseCommunication()");
		this.dataSizeToRead = new HashMap<SocketChannel, Integer>();
		try {
			this.selector = SelectorProvider.provider().openSelector();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void read(SelectionKey key) {
		System.out.println("BC.read()");
		SocketChannel scLocal = (SocketChannel) key.channel();		
		Integer s = dataSizeToRead.get(scLocal);
		int numbetOfBytes = 0;
		int len = 0;
		
		if (s == null) {
			ByteBuffer bb = ByteBuffer.allocate(4);
			try {				
				numbetOfBytes = scLocal.read(bb);
				if (numbetOfBytes != 4) {
					System.out.println("BC.numbetOfBytes != 4:"+numbetOfBytes);
				}
			} 
			catch (IOException e) {
			e.printStackTrace();
			key.cancel();
			}
			
			if (numbetOfBytes == -1) {
				try {
					key.channel().close();
					System.out.println("BC.numbetOfBytes == -1");
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
				dataSizeToRead.put(scLocal, len);
			}
			else {
				dataSizeToRead.put(scLocal, null);
				len = 0;
			}
		}
		else {
			len = s.intValue();
		}
		
		if(len > 0) {
			ByteBuffer bb2 = ByteBuffer.allocate(len);
			try {
				numbetOfBytes = scLocal.read(bb2);
			} catch (IOException e) {
				e.printStackTrace();
				key.cancel();
				return;
			}

			if (numbetOfBytes == -1) {
				try {
					key.channel().close();
					System.out.println("BC.numbetOfBytes == -1");
				} catch (IOException e) {
					e.printStackTrace();
				}
				key.cancel();
				return;
			}

			if (numbetOfBytes == len) {
				dataSizeToRead.put(scLocal, null);
				ByteArrayInputStream bais = new ByteArrayInputStream(bb2.array());
				try(ObjectInputStream ois = new ObjectInputStream(bais)) {
					this.dataPackage = ois.readObject();			
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void send(SocketChannel scLocal, Object result) {
		System.out.println("BC.send()");
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
			scLocal.write(sizeBB);
			scLocal.write(dataBB);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
 	}
}
