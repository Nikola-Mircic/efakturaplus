package efakturaplus.gui;

import java.util.ArrayList;

import javax.swing.JPanel;

import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceStatus;
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
		
		ArrayList<Invoice> invoices = efu.getInvoices(InvoiceStatus.Approved);
		
		InvoiceList il = new InvoiceList(this.getWidth(), this.getHeight());
		
		for (int i = 0; i < invoices.size(); i++) {
			il.addInvoice(invoices.get(i));
		}
		
		this.add(il);
		
		this.setVisible(true);
	}

}
