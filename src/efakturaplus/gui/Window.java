package efakturaplus.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;

import efakturaplus.models.User;

public class Window extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private String TITLE = "EFakturaPlus";
	private int WIDTH = 800;
	private int HEIGHT = 600;
	
	
	public Window() {
		this.setTitle(TITLE);
		this.setSize(new Dimension(WIDTH, HEIGHT));
		this.setLocationRelativeTo(null);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		if(User.API_KEY == "") {
			KeyPanel panel = new KeyPanel(WIDTH, HEIGHT);
			this.add(panel);

		}
		
		this.setVisible(true);
	}

}
