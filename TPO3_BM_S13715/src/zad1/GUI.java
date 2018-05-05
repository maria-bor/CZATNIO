package zad1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener{

	private Client client = null;
	private String login;
	private User user;
	private JLabel labelLogin;
	private JLabel labelImie;
	private JLabel labelNazwisko;
	private JLabel labelHaslo;
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
		buttonWyrejestrujSie = new JButton("Usuń konto");
		
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
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (listaKlientow.getSelectedIndex() != 0) {
						MessageCommand msg = new MessageCommand(textAreaEnable.getText(), (String)listaKlientow.getSelectedItem(), login);
						client.sendAndReceive(msg);
					}
					
				}			
			}
		});
		
		listaKlientow = new JComboBox();
		DefaultComboBoxModel model = (DefaultComboBoxModel) listaKlientow.getModel();
		model.addElement("Wybierz rozmówcę:");
		listaKlientow.setModel(model);
		
		buttonWylogujSie = new JButton("Wyloguj się");	
		
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
		
		buttonZalogujSie.addActionListener(this);
		buttonZalogujSie.setActionCommand("zaloguj się");
		
		buttonWyrejestrujSie.addActionListener(this);
		buttonWyrejestrujSie.setActionCommand("usun konto");
		
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
		
		buttonWylogujSie.addActionListener(this);
		buttonWylogujSie.setActionCommand("wyloguj się");
		
		panelChat.add(scrolltextAreaDisable, BorderLayout.NORTH);
		panelChatPomocniczy.add(listaKlientow, BorderLayout.SOUTH);
		panelChatPomocniczy.add(scrolltextAreaEnable, BorderLayout.NORTH);
		panelChat.add(panelChatPomocniczy, BorderLayout.CENTER);
		panelChat.add(buttonWylogujSie, BorderLayout.SOUTH);
		
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
				LoginCommand logCmd = new LoginCommand(this.loginLog.getText(), this.haslo.getText());
				String s = (String) client.sendAndReceive(logCmd);
				if (s.equals("SUCCESS")) {
					login = logCmd.getLogin();
					getContentPane().removeAll();
					getContentPane().repaint();
					getContentPane().revalidate();
					showChat();			
				}
				else {
					JOptionPane.showMessageDialog(this, "Podany login lub hasło jest niewłaściwe lub brak zarejestrowanego uytkownika!", "Błąd logowania", JOptionPane.WARNING_MESSAGE, null);
				}
			}
		}
		else if("wyloguj się".equals(e.getActionCommand())) {
			getContentPane().removeAll();
			getContentPane().repaint();
			getContentPane().revalidate();
			showLogSystem();
			getContentPane().repaint();
			getContentPane().revalidate();
		}
		else if("Rejestracja".equals(e.getActionCommand())) {
			if ( this.loginRej.getText().isEmpty() || this.hasloRej.getText().isEmpty() || this.powtorzHaslo.getText().isEmpty() || this.imie.getText().isEmpty() || this.nazwisko.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Wypełnij wszystkie pola, żeby zarejestrować się!", "Błąd rejestracji", JOptionPane.WARNING_MESSAGE, null);
			}
			else {
				if (this.hasloRej.getText().equals(this.powtorzHaslo.getText())) {
					this.user = new User(this.imie.getText(), this.nazwisko.getText());
					RegisterCommand regCmd = new RegisterCommand(this.loginRej.getText(), this.hasloRej.getText(), user);
					String s = (String) client.sendAndReceive(regCmd);
					if (s.equals("SUCCESS")) {
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
				UnregisterCommand unregCmd = new UnregisterCommand(this.loginLog.getText(), this.haslo.getText());
				
			}
		}
	}
	
}