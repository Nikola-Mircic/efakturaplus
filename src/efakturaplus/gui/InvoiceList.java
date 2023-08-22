package efakturaplus.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;

import efakturaplus.models.Invoice;

public class InvoiceList extends JComponent {

	private static final long serialVersionUID = 1L;
	
	ArrayList<Invoice> invoices;

	public InvoiceList(int width, int height) {
		this.setSize(width, height);
		this.setLayout(null);
		
		this.invoices = new ArrayList<Invoice>();
	}
	
	public int getLength() {
		return this.invoices.size();
	}
	
	public void addInvoice(Invoice invoice) {
		this.add(new InvoiceListItem(invoice, this.invoices.size()));
		this.validate();
		
		this.invoices.add(invoice);
		
	}
	
}

class InvoiceListItem extends JComponent implements MouseListener{

	private static final long serialVersionUID = 1L;
	
	Invoice invoice;
	
	public InvoiceListItem(Invoice invoice, int idx) {
		super();
		this.invoice = invoice;
		
		int w = 600;
		int h = 30;
		this.setBounds(0, 30*idx, w, h);
		
		this.display();
		
		this.addMouseListener(this);
		
		this.setVisible(true);
	}
	
	private void display() {
		JLabel label = new JLabel(this.invoice.toString());
		
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		label.setBounds(15, 0, this.getWidth(), 25);
		
		label.setForeground(Color.black);
		
		this.add(label);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(this.invoice);
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}	
