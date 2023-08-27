package efakturaplus.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;

import efakturaplus.models.User;

public class Window extends JFrame {
	private static final long serialVersionUID = 1L;

	private static String TITLE = "EFakturaPlus";
	private int WIDTH = 800;
	private int HEIGHT = 600;

	KeyPanel keyPanel;
	MainPanel mainPanel;

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

		this.keyPanel = new KeyPanel(this, WIDTH, HEIGHT);
		this.mainPanel = new MainPanel(this, WIDTH, HEIGHT);

		if(User.API_KEY == "") {
			this.add(this.keyPanel);
		}else {
			this.add(this.keyPanel);
		}

		this.setVisible(true);
	}

	public void switchPanels() {
		this.keyPanel.setVisible(false);
		this.remove(keyPanel);

		this.mainPanel.printInvoices();
		this.mainPanel.setVisible(true);
		this.add(mainPanel);

	}

}
