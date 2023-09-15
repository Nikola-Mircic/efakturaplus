package efakturaplus.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import javax.swing.UIManager;
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
		InvoiceListItem item = new InvoiceListItem(invoice);
		
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

class InvoiceListItem implements MouseListener{

	private static final long serialVersionUID = 1L;
	
	private static InvoiceListItem selectedInvoice = null;

	private Invoice invoice;

	private Color borderColor;
	private Color fontColor;
	private Color bckgColor;
	
	public JLabel date;
	public JLabel amount;
	public JLabel supplier;

	public InvoiceListItem(Invoice invoice) {
		super();
		this.invoice = invoice;

		this.selectBorderColor();
		
		this.fontColor = UIManager.getColor ( "Label.foreground" );;
		this.bckgColor = UIManager.getColor ( "Panel.background" );;

		this.date = new JLabel(this.invoice.getDateString(), JLabel.CENTER);
		this.amount = new JLabel("" + this.invoice.payableAmount, JLabel.CENTER);
		this.supplier = new JLabel(this.invoice.supplier.name.toString());
		
		this.date.addMouseListener(this);
		this.amount.addMouseListener(this);
		this.supplier.addMouseListener(this);
		
		loadComponents();
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
		setComponentUI(date);
		setComponentUI(amount);
		setComponentUI(supplier);
	}
	
	private void setComponentUI(JLabel label) {
		label.setOpaque(true);

		Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor);
		label.setBorder(border);

		label.setForeground(fontColor);
		
		label.setBackground(bckgColor);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if( this.equals(selectedInvoice) ) {
			System.out.println(efakturaplus.util.Color.CYAN+"Loading PDF doucments..." + efakturaplus.util.Color.RESET);
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
		}else {
			if(selectedInvoice != null) 
				deselect(selectedInvoice);
			
			selectedInvoice = this;
			select(this);
		}
	}
	
	private void select(InvoiceListItem item) {
		item.bckgColor = borderColor;
		item.fontColor = Color.BLACK;
		item.loadComponents();
		System.out.println("Selected:" + item.invoice.supplier.name);
	}
	
	private void deselect(InvoiceListItem item) {
		item.bckgColor = UIManager.getColor ( "Panel.background" );
		item.fontColor = UIManager.getColor ( "Label.foreground" );
		item.loadComponents();
		System.out.println("Deselected:" + item.invoice.supplier.name);
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
