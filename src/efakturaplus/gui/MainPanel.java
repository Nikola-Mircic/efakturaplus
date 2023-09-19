package efakturaplus.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JPanel;

import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceStatus;
import efakturaplus.models.InvoiceType;
import efakturaplus.util.EFakturaUtil;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Window parent;
	
	private CardLayout dataPanelLayout;
	
	private InvoiceList purchaseIl;
	private InvoiceList salesIl;

	public MainPanel(Window parent) {
		this.parent = parent;
		this.setLayout(new BorderLayout());
		
		dataPanelLayout = new CardLayout();
		JPanel dataPanel = new JPanel();
		dataPanel.setLayout(dataPanelLayout);
		
		purchaseIl = new InvoiceList();
		dataPanel.add(purchaseIl, "PURCHASE");
		
		salesIl = new InvoiceList();
		dataPanel.add(salesIl, "Sales");
		
		dataPanelLayout.show(dataPanel, "PURCHASE");
		
		this.add(dataPanel, BorderLayout.CENTER);
	}

	public void printPurchaseInvoices() {
		displayInvoicesByStatus(InvoiceType.PURCHASE, InvoiceStatus.ReNotified);
		displayInvoicesByStatus(InvoiceType.PURCHASE, InvoiceStatus.New);
		displayInvoicesByStatus(InvoiceType.PURCHASE, InvoiceStatus.Seen);
		displayInvoicesByStatus(InvoiceType.PURCHASE, InvoiceStatus.Approved);
		
		displayInvoicesByStatus(InvoiceType.SALES, InvoiceStatus.ReNotified);
		displayInvoicesByStatus(InvoiceType.SALES, InvoiceStatus.New);
		displayInvoicesByStatus(InvoiceType.SALES, InvoiceStatus.Seen);
		displayInvoicesByStatus(InvoiceType.SALES, InvoiceStatus.Approved);
	}

	private void displayInvoicesByStatus(InvoiceType type, InvoiceStatus status) {
		EFakturaUtil efu = EFakturaUtil.getInstance();

		ArrayList<Invoice> invoices = efu.getInvoices(type, status);
		Collections.reverse(invoices);

		for (Invoice element : invoices) {
			if(type == InvoiceType.PURCHASE)
				purchaseIl.addInvoice(element);
			else
				salesIl.addInvoice(element);
		}
	}

}
