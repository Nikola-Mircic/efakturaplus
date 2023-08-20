package efakturaplus.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;

import efakturaplus.models.Invoice;
import efakturaplus.models.User;
import efakturaplus.util.EFakturaUtil;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Window parent;
	
	public MainPanel(Window parent, int width, int height) {
		this.parent = parent;
		this.setSize(width, height);
		this.setLayout(null);
	}

	public void printInvoices() {
		EFakturaUtil efu = EFakturaUtil.getInstance();
		
		ArrayList<Invoice> invoices = efu.getInvoices("Approved");
		
		for (int i = 0; i < invoices.size(); i++) {
			JLabel label = new JLabel(invoices.get(i).toString());
			
			label.setFont(new Font("Arial", Font.PLAIN, 20));
			label.setBounds(15, i*25, this.getWidth(), 25);
			
			label.setForeground(Color.black);
			
			this.add(label);
		}
		
		this.setVisible(true);
	}

}
