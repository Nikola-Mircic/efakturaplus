package efakturaplus.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import efakturaplus.models.User;

public class KeyPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public KeyPanel(int width, int height) {
		this.setSize(width, height);
		this.setLayout(null);
		
		addLabel(width, height);
		addTextField(width, height);
	}
	
	private void addLabel(int width, int height) {
		JLabel label = new JLabel("Please enter your API key here:");
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		label.setBounds(width/2-150, height/2-75, 350, 50);
		
		label.setForeground(Color.black);
		
		this.add(label);
	}
	
	private void addTextField(int width, int height) {
		JTextField keyInput = new JTextField();
		keyInput.setBounds(width/2-150, height/2-25, 300, 60);

		keyInput.setBorder(BorderFactory.createLineBorder(Color.black, 3, true));
		
		keyInput.setBorder(BorderFactory.createCompoundBorder(
				keyInput.getBorder(), 
		        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
		
		keyInput.setFont(new Font("Arial", Font.PLAIN, 20));
		
		keyInput.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				User.API_KEY = keyInput.getText();
				System.out.println(User.API_KEY);
			}
		});
		
		this.add(keyInput);
	}
	
}
