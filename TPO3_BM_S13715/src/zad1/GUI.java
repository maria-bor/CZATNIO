package zad1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class GUI extends JFrame implements ActionListener{

	private Client client = null;
	private UserClient user;
	private Map<String, String> listUser; // <User.toString(), login>
	private Map<String, User> listLoginToUser; // <login, User>
	
	private JLabel labelLogin;
	private JLabel labelImie;
	private JLabel labelNazwisko;
	private JLabel labelHasloRej;
	private JLabel labelPowtorzHaslo;
	
	private JTextField loginLog;
	private JTextField loginRej;
	private JTextField imie;
	private JTextField nazwisko;
	private JTextField haslo;
	private JTextField hasloRej;
	private JTextField powtorzHaslo;
	
	private JComboBox listaKlientow;
	
	private JButton buttonZalogujSie;
	private JButton buttonRejestracja;
	private JButton buttonWylogujSie;
	private JButton buttonWyrejestrujSie;
	
	private JTextArea textAreaDisable;
	private JTextArea textAreaEnable;
	
	private JScrollPane scrolltextAreaDisable;
	private JScrollPane scrolltextAreaEnable;
	
	private JPanel panelFlow;
	private JPanel panelGrid;
	private JPanel panelChat;
	private JPanel panelChatPomocniczy;
	
	private Dimension dimension;

	GUI(Client client) {
		this.client = client;
		this.client.setGUI(this);
		this.listUser = new HashMap<String, String>();
		this.listLoginToUser = new HashMap<String, User>();
		
		loginLog = new JTextField("Wprowadz login");
		loginLog.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if (loginLog.getText().isEmpty()) {
					loginLog.setForeground(Color.GRAY);
					loginLog.setText("Wprowadz login");
				}				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (loginLog.getText().equals("Wprowadz login") ) {
					loginLog.setText("");
					loginLog.setForeground(Color.BLACK);
				}				
			}
		});
		
		haslo = new JTextField("Wprowadz haslo");
		haslo.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if (haslo.getText().isEmpty()) {
					haslo.setForeground(Color.GRAY);
					haslo.setText("Wprowadz haslo");
				}
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (haslo.getText().equals("Wprowadz haslo") ) {
					haslo.setText("");
					haslo.setForeground(Color.BLACK);
				}
				
			}
		});
		
		buttonZalogujSie = new JButton("Zaloguj się");
		buttonZalogujSie.addActionListener(this);
		buttonZalogujSie.setActionCommand("zaloguj się");
		
		buttonWyrejestrujSie = new JButton("Usuń konto");
		buttonWyrejestrujSie.addActionListener(this);
		buttonWyrejestrujSie.setActionCommand("Usuń konto");
		
		labelLogin = new JLabel("Login:");
		loginRej = new JTextField();
		
		labelImie = new JLabel("Imie:");
		imie = new JTextField();
		
		labelNazwisko = new JLabel("Nazwisko:");
		nazwisko = new JTextField();
		
		labelHasloRej = new JLabel("Haslo:");
		hasloRej = new JTextField();
		
		labelPowtorzHaslo = new JLabel("Powtórz haslo:");
		powtorzHaslo = new JTextField();		
		buttonRejestracja = new JButton("Rejestracja");
		buttonRejestracja.addActionListener(this);
		buttonRejestracja.setActionCommand("Rejestracja");
		
		textAreaDisable = new JTextArea(50,20);
		textAreaDisable.setLineWrap(true);
		scrolltextAreaDisable = new JScrollPane(textAreaDisable);
		scrolltextAreaDisable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textAreaEnable = new JTextArea(3,20);
		textAreaEnable.setLineWrap(true);
		scrolltextAreaEnable = new JScrollPane(textAreaEnable);
		scrolltextAreaEnable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textAreaEnable.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub			
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub			
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
					if (listaKlientow.getSelectedIndex() != 0) {
						String s = (String)listaKlientow.getSelectedItem();						
						try {
							client.send("Message", listUser.get(s), user.getLogin(), textAreaEnable.getText());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						textAreaEnable.setEditable(false);
						e.consume();
					}
				}			
			}
		});
		
		listaKlientow = new JComboBox();
		buttonWylogujSie = new JButton("Wyloguj się");
		buttonWylogujSie.addActionListener(this);
		buttonWylogujSie.setActionCommand("wyloguj się");
		
		panelFlow = new JPanel(new FlowLayout());
		panelGrid = new JPanel(new GridLayout(0, 2));
		panelChat = new JPanel(new BorderLayout(10,10));
		panelChatPomocniczy = new JPanel(new BorderLayout(10,10));
		showLogSystem();
	}
	
	public void showLogSystem() {
		loginLog.setForeground(Color.GRAY);	
		loginLog.setText("Wprowadz login");
		haslo.setForeground(Color.GRAY);
		haslo.setText("Wprowadz haslo");
		
		panelFlow.add(loginLog);
		panelFlow.add(haslo);
		panelFlow.add(buttonZalogujSie);
		panelFlow.add(buttonWyrejestrujSie);
	
		panelGrid.add(labelLogin);
		panelGrid.add(loginRej);
		
		panelGrid.add(labelImie);
		panelGrid.add(imie);
		
		panelGrid.add(labelNazwisko);
		panelGrid.add(nazwisko);
		
		panelGrid.add(labelHasloRej);
		panelGrid.add(hasloRej);		

		panelGrid.add(labelPowtorzHaslo);
		panelGrid.add(powtorzHaslo);
		
		panelGrid.add(buttonRejestracja);
		panelGrid.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
		
		this.setMinimumSize(new Dimension(300, 300));
		this.add(panelFlow, BorderLayout.NORTH);
		this.add(new JSeparator(), BorderLayout.CENTER);
		this.add(panelGrid, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		
		setFrameOnCentre();
		setVisible(true);
	}

	public void showChat() {
		textAreaDisable.setEditable(false); 	
		textAreaEnable.setEditable(true);
		
		panelChat.add(scrolltextAreaDisable, BorderLayout.NORTH);
		panelChatPomocniczy.add(listaKlientow, BorderLayout.SOUTH);
		panelChatPomocniczy.add(scrolltextAreaEnable, BorderLayout.NORTH);
		panelChat.add(panelChatPomocniczy, BorderLayout.CENTER);
		panelChat.add(buttonWylogujSie, BorderLayout.SOUTH);
		
		DefaultComboBoxModel model = (DefaultComboBoxModel) listaKlientow.getModel();
		model.addElement("Wybierz rozmówcę:");
		listaKlientow.setModel(model);
		
		this.setMinimumSize(new Dimension(500, 500));
		this.add(panelChat, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();	
		
		setFrameOnCentre();    
		setVisible(true);
	}
	
	public void setFrameOnCentre() {
		dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
	    this.setLocation(x, y);
	} 
	
	public static void runGUI(Client client){
	    SwingUtilities.invokeLater(new Runnable() {
	      public void run() {
	        new GUI(client);        
	      }
	    });
	  }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if("zaloguj się".equals(e.getActionCommand())) {
			if (this.loginLog.getText().isEmpty() || this.loginLog.getText().equals("Wprowadz login") || this.haslo.getText().isEmpty() || this.haslo.getText().equals("Wprowadz haslo")  ) {
				JOptionPane.showMessageDialog(this, "Pola login i hasło nie mogą być puste!", "Błąd logowania", JOptionPane.WARNING_MESSAGE, null);
			}
			else {
				try {
					client.send("Login", this.loginLog.getText(), this.haslo.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				this.user = null;
				this.loginLog.setEditable(false);
			}
		}
		else if("wyloguj się".equals(e.getActionCommand())) {
			try {
				client.send("Logout", this.loginLog.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			getContentPane().removeAll();
			getContentPane().repaint();
			getContentPane().revalidate();
			showLogSystem();
			getContentPane().repaint();
			getContentPane().revalidate();
			this.textAreaDisable.setText("");
			this.textAreaDisable.selectAll();
			this.textAreaDisable.replaceSelection("");
			this.textAreaEnable.setText("");
			this.textAreaEnable.selectAll();
			this.textAreaEnable.replaceSelection("");
			DefaultComboBoxModel model = (DefaultComboBoxModel) listaKlientow.getModel();
			model.removeAllElements();
			listaKlientow.setModel(model);
			setTitle("");
		}
		else if("Rejestracja".equals(e.getActionCommand())) {
			if ( this.loginRej.getText().isEmpty() || this.hasloRej.getText().isEmpty() || this.powtorzHaslo.getText().isEmpty() || this.imie.getText().isEmpty() || this.nazwisko.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Wypełnij wszystkie pola, żeby zarejestrować się!", "Błąd rejestracji", JOptionPane.WARNING_MESSAGE, null);
			}
			else {
				if (this.hasloRej.getText().equals(this.powtorzHaslo.getText())) {
					try {
						client.send("Register", this.loginRej.getText(), this.imie.getText(), this.nazwisko.getText(), this.hasloRej.getText());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else {
					JOptionPane.showMessageDialog(this, "Hasła się nie zgadzają!", "Błąd rejestracji", JOptionPane.WARNING_MESSAGE, null);
				}
			}
		}
		else if("Usuń konto".equals(e.getActionCommand())) {
			if (this.loginLog.getText().isEmpty() || this.loginLog.getText().equals("Wprowadz login") || this.haslo.getText().isEmpty() || this.haslo.getText().equals("Wprowadz haslo")  ) {
				JOptionPane.showMessageDialog(this, "Pola login i hasło nie mogą być puste!", "Błąd usuwania konta", JOptionPane.WARNING_MESSAGE, null);
			}
			else {
				try {
					client.send("Unregister", this.loginLog.getText(), this.haslo.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void updateComboBox(String r, boolean isLogged) {
		String[] str = r.split(" ");
		if (str.length % 3 != 0) {
			System.out.println("GUI: !!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		DefaultComboBoxModel model = (DefaultComboBoxModel) listaKlientow.getModel();
		if (isLogged) {
			for (int i = 0; i < str.length; i+=3) {
				model.addElement(str[i+1] + " " + str[i+2]);
				this.listUser.put(str[i+1] + " " + str[i+2], str[i]);
				this.listLoginToUser.put(str[i], new User(str[i+1], str[i+2]));
			}
		}
		else {
			for (int i = 0; i < str.length; i+=3) {
				model.removeElement(str[i+1] + " " + str[i+2]);
				this.listUser.remove(str[i+1] + " " + str[i+2]);
				this.listLoginToUser.remove(str[i]);
			}
		}
		listaKlientow.setModel(model);
	}

	public void showReceivedText(String s) {
		String[] str = s.split(" ", 2);
		Font newTextAreaFont = new Font(this.textAreaDisable.getFont().getName(),Font.BOLD, this.textAreaDisable.getFont().getSize());
		this.textAreaDisable.setFont(newTextAreaFont);
		if(this.listLoginToUser.containsKey(str[0]))
			textAreaDisable.append(this.listLoginToUser.get(str[0]).toString() + ":\n");
		textAreaDisable.append(str[1] + "\n");
		Font oldTextAreaFont = new Font(this.textAreaDisable.getFont().getName(),Font.PLAIN, this.textAreaDisable.getFont().getSize());
		this.textAreaDisable.setFont(oldTextAreaFont);
		
		try {
			client.send("MessageResponse", str[0], this.user.getLogin(), "SUCCESS");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showSendedText(String s) {
		String[] str = s.split(" ", 2);
		if(str[1].equals("SUCCESS")) {
			this.textAreaDisable.append(this.user.toString() + ":\n");
			this.textAreaDisable.append(this.textAreaEnable.getText() + "\n");

			this.textAreaEnable.setText("");
			this.textAreaEnable.selectAll();
			this.textAreaEnable.replaceSelection("");
		}
		else {
			JOptionPane.showMessageDialog(this, "Upps.. Problem z wysyłaniem wiadomości.", "Błąd sendingu.", JOptionPane.WARNING_MESSAGE, null);
		}
		this.textAreaEnable.setEditable(true);
	}

	public void userRegistered(String r) {
		if (r.equals("SUCCESS")) {
			this.loginRej.setText("");
			this.imie.setText("");
			this.nazwisko.setText("");
			this.hasloRej.setText("");
			this.powtorzHaslo.setText("");
			JOptionPane.showMessageDialog(this, "Rejestracja przebiegła pomyślnie. Możesz zalogować się :)", "Rejestracja", JOptionPane.INFORMATION_MESSAGE, null);
		}
		else {
			JOptionPane.showMessageDialog(this, "Taki użytkownik już istnieje w systemie!", "Błąd rejestracji", JOptionPane.WARNING_MESSAGE, null);
		}	
	}

	public void userUnregistered(String r) {
		if (r.equals("SUCCESS")) {
			this.loginLog.setText("");
			this.haslo.setText("");
			JOptionPane.showMessageDialog(this, "Usunięcie konta przebiegło pomyślnie.", "Usuń konto", JOptionPane.INFORMATION_MESSAGE, null);
		}
		else {
			this.user = null;
			JOptionPane.showMessageDialog(this, "Taki użytkownik nie istnieje w systemie lub jest zalogowany!", "Usuń konto", JOptionPane.WARNING_MESSAGE, null);
		}
	}
	
	public void userLogged(String r) {
		String[] str = r.split(" ");
 		if (str[0].equals("SUCCESS")) {
			this.user = new UserClient(str[1], str[2], str[3]);
			setTitle(this.user.toString());
			getContentPane().removeAll();
			getContentPane().repaint();
			getContentPane().revalidate();
			showChat();	
		}
		else {
			JOptionPane.showMessageDialog(this, "Podany login lub hasło jest niewłaściwe lub brak zarejestrowanego użytkownika lub użytkownik jest już zarejestrowny w systemie!", "Błąd logowania", JOptionPane.WARNING_MESSAGE, null);
		}
		this.loginLog.setEditable(true);
	}
}