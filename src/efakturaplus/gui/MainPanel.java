package efakturaplus.gui;

import java.util.ArrayList;

import javax.swing.JPanel;

import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceStatus;
import efakturaplus.util.EFakturaUtil;
import test.myrenderer.PDFDisplay;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Window parent;

	public MainPanel(Window parent, int width, int height) {
		this.parent = parent;
		this.setSize(width, height);
		this.setLayout(null);
		
		String filename = "/home/nikola/Desktop/plati.euprava.gov.rs_api_Payment_PaymentSlips.pdf";
		
	}

	public void printInvoices() {
		displayInvoicesByStatus(InvoiceStatus.ReNotified);
		displayInvoicesByStatus(InvoiceStatus.New);
		displayInvoicesByStatus(InvoiceStatus.Seen);
		displayInvoicesByStatus(InvoiceStatus.Approved);

	}

	private void displayInvoicesByStatus(InvoiceStatus status) {
		EFakturaUtil efu = EFakturaUtil.getInstance();

		ArrayList<Invoice> invoices = efu.getInvoices(status);

		InvoiceList il = new InvoiceList(this.getWidth(), this.getHeight());

		for (Invoice element : invoices) {
			il.addInvoice(element);
		}

		this.add(il);
		
		this.setVisible(true);
	}

}
