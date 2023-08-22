package efakturaplus.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;

import efakturaplus.models.*;

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
		this.add(new InvoiceListItem(invoice, new Dimension(this.getWidth(), 35), this.invoices.size()));
		this.validate();
		
		this.invoices.add(invoice);
		
	}
	
}

class InvoiceListItem extends JComponent implements MouseListener{

	private static final long serialVersionUID = 1L;
	
	Invoice invoice;
	
	public InvoiceListItem(Invoice invoice, Dimension size, int idx) {
		super();
		this.invoice = invoice;
		
		int w = size.width;
		int h = size.height;
		this.setBounds(0, h*idx, w, h);
		
		this.display();
		
		this.addMouseListener(this);
		
		this.setVisible(true);
	}
	
	private void display() {
		Border border = BorderFactory.createLineBorder(Color.cyan, 1);
		this.setBorder(border);
		
		JLabel date = new JLabel(this.invoice.getDateString());
		JLabel supplier = new JLabel(this.invoice.supplier.toString());
		JLabel amount = new JLabel("" + this.invoice.payableAmount);
		
		date.setBounds(15, 5, this.getWidth()/4, 25);
		supplier.setBounds(this.getWidth()/2, 5, this.getWidth()/2, 25);
		amount.setBounds(this.getWidth()/4, 5, this.getWidth()/4, 25);
		
		this.add(date);
		this.add(supplier);
		this.add(amount);
		
		/*label.setFont(new Font("Arial", Font.PLAIN, 20));
		label.setForeground(Color.black);
		*/
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
