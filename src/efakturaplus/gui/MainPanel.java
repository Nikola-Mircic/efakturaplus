package efakturaplus.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JPanel;

import ch.randelshofer.util.ArrayUtil;
import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceStatus;
import efakturaplus.util.EFakturaUtil;
import test.myrenderer.PDFDisplay;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Window parent;
	
	private InvoiceList il;

	public MainPanel(Window parent, int width, int height) {
		this.parent = parent;
		this.setSize(width, height);
		this.setLayout(null);
		
		String filename = "/home/nikola/Desktop/plati.euprava.gov.rs_api_Payment_PaymentSlips.pdf";
		
		il = new InvoiceList(this.getWidth(), this.getHeight());
		this.add(il);
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
		Collections.reverse(invoices);

		for (Invoice element : invoices) {
			il.addInvoice(element);
		}
	}

}
