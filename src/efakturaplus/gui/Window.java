package efakturaplus.gui;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;

import efakturaplus.models.User;

public class Window extends JFrame {
	private static final long serialVersionUID = 1L;

	private static String TITLE = "EFakturaPlus";
	private int WIDTH = 800;
	private int HEIGHT = 600;

	private KeyPanel keyPanel;
	private MainPanel mainPanel;
	
	private CardLayout panels;
	
	public Window() {
		this(TITLE);
	}

	public Window(String title) {
		this.setTitle(title);
		this.setSize(new Dimension(WIDTH, HEIGHT));
		this.setLocationRelativeTo(null);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.panels = new CardLayout();
		this.setLayout(panels);
		
		this.keyPanel = new KeyPanel(this, WIDTH, HEIGHT);
		this.mainPanel = new MainPanel(this, WIDTH, HEIGHT);
		
		this.add(keyPanel, "KEY_PANEL");
		this.add(mainPanel, "MAIN_PANEL");
		
		if(User.API_KEY == "") {
			panels.show(this.getContentPane(), "KEY_PANEL");
		}else {
			panels.show(this.getContentPane(), "MAIN_PANEL");
		}

		this.setVisible(true);
	}

	public void switchPanels() {
		this.mainPanel.printInvoices();
		
		this.panels.show(this.getContentPane(), "MAIN_PANEL");
	}

}
