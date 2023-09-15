package efakturaplus.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import efakturaplus.models.Invoice;;

public class InvoiceList extends JPanel {

	private static final long serialVersionUID = 1L;

	ArrayList<Invoice> invoices;
	
	private JPanel invoiceDisplay;

	public InvoiceList(int width, int height) {
		this.setLayout(new BorderLayout());
		
		this.invoices = new ArrayList<>();
		
		this.invoiceDisplay = new JPanel(new GridBagLayout());
		
		JScrollPane sp = new JScrollPane(invoiceDisplay);
		this.add(sp, BorderLayout.CENTER);
	}

	public void addInvoice(Invoice invoice) {
		InvoiceListItem item = new InvoiceListItem(invoice, new Dimension(this.getWidth(), 35), this.invoices.size());
		
		int n = this.invoices.size();
		
		GridBagConstraints constr = new GridBagConstraints();
		constr.gridy = n*45;
        constr.anchor = GridBagConstraints.CENTER;
        constr.weightx  = 0.5;
        constr.weighty = 1.0;
		constr.ipadx = 15;
		constr.ipady = 12;
		constr.fill = GridBagConstraints.BOTH;
		
		constr.gridx = 0;
		constr.gridwidth = 1;
		this.invoiceDisplay.add(item.date, constr);
		
		constr.gridx = 1;
		constr.gridwidth = 1;
		this.invoiceDisplay.add(item.amount, constr);
		
		constr.gridx = 2;
		constr.gridwidth = 2;
		this.invoiceDisplay.add(item.supplier, constr);
		
		this.invoices.add(invoice);
	}

}

class InvoiceListItem extends JComponent implements MouseListener{

	private static final long serialVersionUID = 1L;

	private Invoice invoice;

	private Color borderColor;
	
	private boolean selected = false;
	
	public JLabel date;
	public JLabel amount;
	public JLabel supplier;

	public InvoiceListItem(Invoice invoice, Dimension size, int idx) {
		super();
		this.invoice = invoice;

		this.selectBorderColor();

		this.loadComponents();

		this.addMouseListener(this);
	}

	private void selectBorderColor() {
		switch (this.invoice.status) {
		case ReNotified:
			this.borderColor = Color.red;
			break;
		case New:
			this.borderColor = Color.green;
			break;
		case Seen:
			this.borderColor = Color.blue;
			break;
		case Approved:
			this.borderColor = Color.cyan;
			break;

		default:
			this.borderColor = Color.gray;
			break;
		}
	}

	private void loadComponents() {
		this.removeAll();
		this.setBorder(null);

		this.date = new JLabel(this.invoice.getDateString(), JLabel.CENTER);
		this.amount = new JLabel("" + this.invoice.payableAmount, JLabel.CENTER);
		this.supplier = new JLabel(this.invoice.supplier.name.toString());
		
		Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor);
		this.date.setBorder(border);
		this.amount.setBorder(border);
		this.supplier.setBorder(border);
		
		this.date.addMouseListener(this);
		this.amount.addMouseListener(this);
		this.supplier.addMouseListener(this);
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
			this.date.setBackground(borderColor);
			this.amount.setBackground(borderColor);
			this.supplier.setBackground(borderColor);
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
