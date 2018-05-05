package zad1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	JLabel labelLogin;
	JLabel labelImie;
	JLabel labelNazwisko;
	JLabel labelHaslo;
	JLabel labelHasloRej;
	JLabel labelPowtorzHaslo;
	
	JTextField loginLog;
	JTextField loginRej;
	JTextField imie;
	JTextField nazwisko;
	JTextField haslo;
	JTextField hasloRej;
	JTextField powtorzHaslo;
	
	JButton buttonZalogujSie;
	JButton buttonRejestracja;
	JButton buttonWylogujSie;
	
	JTextArea textAreaDisable;
	JTextArea textAreaEnable;
	
	JScrollPane scrolltextAreaDisable;
	
	JPanel panelFlow;
	JPanel panelGrid;
	JPanel panelChat;
	
	Dimension dimension;

	GUI(Client client) {
		this.client = client;
		
		loginLog = new JTextField("Wprowadz login");
		haslo = new JTextField("Wprowadz haslo");	
		buttonZalogujSie = new JButton("Zaloguj się");
		
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
		
		textAreaDisable = new JTextArea(50,20);
		textAreaEnable = new JTextArea(3,20);
		buttonWylogujSie = new JButton("Wyloguj się");
		
		panelFlow = new JPanel(new FlowLayout());
		panelGrid = new JPanel(new GridLayout(0, 2));
		panelChat = new JPanel(new BorderLayout(10,10));
		showLogSystem();
	}
	
	public void showLogSystem() {
		loginLog.setForeground(Color.GRAY);				
		haslo.setForeground(Color.GRAY);
		
		buttonZalogujSie.addActionListener(this);
		buttonZalogujSie.setActionCommand("zaloguj się");
		
		panelFlow.add(loginLog);
		panelFlow.add(haslo);
		panelFlow.add(buttonZalogujSie);
	
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
		scrolltextAreaDisable = new JScrollPane(textAreaDisable); 
		
		textAreaEnable.setEditable(true);
		
		buttonWylogujSie.addActionListener(this);
		buttonWylogujSie.setActionCommand("wyloguj się");
		
		panelChat.add(textAreaDisable, BorderLayout.NORTH);
		panelChat.add(textAreaEnable, BorderLayout.CENTER);
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
			getContentPane().removeAll();
			getContentPane().repaint();
			getContentPane().revalidate();
			showChat();
			getContentPane().repaint();
			getContentPane().revalidate();
		}
		else if("wyloguj się".equals(e.getActionCommand())) {
			getContentPane().removeAll();
			getContentPane().repaint();
			getContentPane().revalidate();
			showLogSystem();
			getContentPane().repaint();
			getContentPane().revalidate();
		}
	}
}
