package efakturaplus.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

import efakturaplus.models.Invoice;;

public class InvoiceList extends JComponent {

	private static final long serialVersionUID = 1L;

	ArrayList<Invoice> invoices;

	public InvoiceList(int width, int height) {
		this.setSize(width, height);
		this.setLayout(null);

		this.invoices = new ArrayList<>();
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

	private Invoice invoice;

	private Color borderColor;
	
	private boolean selected = false;

	public InvoiceListItem(Invoice invoice, Dimension size, int idx) {
		super();
		this.invoice = invoice;

		int w = size.width;
		int h = size.height;
		this.setBounds(0, h*idx, w, h);

		this.selectBorderColor();

		this.display();

		this.addMouseListener(this);

		this.setVisible(true);
	}

	private void selectBorderColor() {
		switch (this.invoice.status) {
		case ReNotified:
			this.borderColor = Color.red;
			break;
		case New:
			this.borderColor = Color.green;
			break;
		case Approved:
			this.borderColor = Color.cyan;
			break;

		default:
			this.borderColor = Color.gray;
			break;
		}
	}

	private void display() {
		Border border = BorderFactory.createLineBorder(this.borderColor, 1);
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
		if(this.selected) {
			JFrame frame1 = new JFrame("PDF Document");
			PDFDisplay pdfDisplay1 = new PDFDisplay(this.invoice.pdfInvoice);		
			frame1.add(pdfDisplay1);
			frame1.setSize(700, 1000);
			frame1.setVisible(true);
			
			if(this.invoice.pdfAttachment != null) {
				JFrame frame2 = new JFrame("PDF Attachment");
				PDFDisplay pdfDisplay2 = new PDFDisplay(this.invoice.pdfAttachment);		
				frame2.add(pdfDisplay2);
				frame2.setSize(700, 1000);
				frame2.setVisible(true);
			}
			
			selected = false;
		}else {
			this.setBackground(borderColor);
			selected = true;
		}
		
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
