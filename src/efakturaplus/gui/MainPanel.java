package efakturaplus.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JPanel;

import ch.randelshofer.util.ArrayUtil;
import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceStatus;
import efakturaplus.models.InvoiceType;
import efakturaplus.util.EFakturaUtil;
import test.myrenderer.PDFDisplay;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Window parent;
	
	private CardLayout dataPanelLayout;
	
	private InvoiceList purchaseIl;
	private InvoiceList salsesIl;

	public MainPanel(Window parent, int width, int height) {
		this.parent = parent;
		this.setSize(width, height);
		this.setLayout(new BorderLayout());
		
		dataPanelLayout = new CardLayout();
		JPanel dataPanel = new JPanel();
		dataPanel.setLayout(dataPanelLayout);
		
		purchaseIl = new InvoiceList();
		dataPanel.add(purchaseIl, "PURCHASE");
		
		salsesIl = new InvoiceList();
		dataPanel.add(salsesIl, "Sales");
		
		dataPanelLayout.show(dataPanel, "PURCHASE");
		
		this.add(dataPanel, BorderLayout.CENTER);
	}

	public void printPurchaseInvoices() {
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
			purchaseIl.addInvoice(element);
		}
	}

}
