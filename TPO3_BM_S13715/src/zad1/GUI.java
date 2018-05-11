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

import zad1.MessageCommand.Response;

public class GUI extends JFrame implements ActionListener{

	private Client client = null;
	private String login;
	private User user;
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
		scrolltextAreaDisable = new JScrollPane(textAreaDisable);
		scrolltextAreaDisable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textAreaEnable = new JTextArea(3,20);
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
						MessageCommand msg = new MessageCommand(textAreaEnable.getText(), listUser.get(s), login);
						client.sendOnly(msg);
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
				LogInOutCommand logCmd = new LogInOutCommand(this.loginLog.getText(), this.haslo.getText(), true);
				client.sendOnly(logCmd);
				this.user = null;
				this.login = this.loginLog.getText();
				this.loginLog.setEditable(false);
			}
		}
		else if("wyloguj się".equals(e.getActionCommand())) {
			LogInOutCommand logCmd = new LogInOutCommand(this.loginLog.getText(), this.haslo.getText(), false);
			client.sendOnly(logCmd);
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
					this.user = new User(this.imie.getText(), this.nazwisko.getText());
					ReUngisterCommand regCmd = new ReUngisterCommand(this.loginRej.getText(), this.hasloRej.getText(), user);
					client.sendOnly(regCmd);
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
				ReUngisterCommand unregCmd = new ReUngisterCommand(this.loginLog.getText(), this.haslo.getText());
				client.sendOnly(unregCmd);
			}
		}
	}
	
	public void updateComboBox(NewLoggedUsersMapCommand cmd) {
		DefaultComboBoxModel model = (DefaultComboBoxModel) listaKlientow.getModel();
		Map<String, User> newlistUser  = null;
		try {
			newlistUser = (Map<String, User>)cmd.handle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cmd.isUserLogged()) {	
			for(Map.Entry<String, User> entry : newlistUser.entrySet()) {
				model.addElement(entry.getValue().toString());
				this.listUser.put(entry.getValue().toString(), entry.getKey());
				this.listLoginToUser.put(entry.getKey(), entry.getValue());
			}
		}
		else {
			for(Map.Entry<String, User> entry : newlistUser.entrySet()) {
				model.removeElement(entry.getValue().toString());
				this.listUser.remove(entry.getValue().toString());
				this.listLoginToUser.remove(entry.getKey());
			}
		}
		listaKlientow.setModel(model);
	}

	public void showReceivedText(MessageCommand mc) {
		Font newTextAreaFont = new Font(this.textAreaDisable.getFont().getName(),Font.BOLD, this.textAreaDisable.getFont().getSize());
		this.textAreaDisable.setFont(newTextAreaFont);
		textAreaDisable.append(this.listLoginToUser.get(mc.getSender()).toString() + ":\n");
		textAreaDisable.append(mc.getText() + "\n");
		Font oldTextAreaFont = new Font(this.textAreaDisable.getFont().getName(),Font.PLAIN, this.textAreaDisable.getFont().getSize());
		this.textAreaDisable.setFont(oldTextAreaFont);
	}

	public void showSendedText(Response response) {
		if(response.getResult().equals("SUCCESS")) {
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

	public void userRegistered(ReUngisterCommand.Response r) {
		ReUngisterCommand.Response response = (ReUngisterCommand.Response) r;
		if (response.getResult().equals("SUCCESS")) {
			this.loginRej.setText("");
			this.imie.setText("");
			this.nazwisko.setText("");
			this.hasloRej.setText("");
			this.powtorzHaslo.setText("");
			JOptionPane.showMessageDialog(this, "Rejestracja przebiegła pomyślnie. Możesz zalogować się :)", "Rejestracja", JOptionPane.INFORMATION_MESSAGE, null);
		}
		else {
			this.user = null;
			JOptionPane.showMessageDialog(this, "Taki użytkownik już istnieje w systemie!", "Błąd rejestracji", JOptionPane.WARNING_MESSAGE, null);
		}	
	}

	public void userUnregistered(ReUngisterCommand.Response r) {
		ReUngisterCommand.Response response = (ReUngisterCommand.Response) r;
		if (response.getResult().equals("SUCCESS")) {
			this.loginLog.setText("");
			this.haslo.setText("");
			JOptionPane.showMessageDialog(this, "Usunięcie konta przebiegło pomyślnie.", "Usuń konto", JOptionPane.INFORMATION_MESSAGE, null);
		}
		else {
			this.user = null;
			JOptionPane.showMessageDialog(this, "Taki użytkownik nie istnieje w systemie lub jest zalogowany!", "Usuń konto", JOptionPane.WARNING_MESSAGE, null);
		}
	}
	
	public void userLogged(LogInOutCommand.Response r) {
		if (r.getResult().equals("SUCCESS")) {
			this.user = r.getUser();
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