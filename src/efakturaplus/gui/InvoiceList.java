package efakturaplus.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.Border;

import efakturaplus.models.Invoice;
import efakturaplus.util.PrintColor;
import efakturaplus.util.QRUtil;;

public class InvoiceList extends JPanel {

	private static final long serialVersionUID = 1L;

	ArrayList<InvoiceListItem> invoices;
	
	private JPanel invoiceDisplay;
	private GridBagLayout layout;

	public InvoiceList() {
		this.setLayout(new BorderLayout());
		
		this.invoices = new ArrayList<>();
		
		this.layout = new GridBagLayout();
		this.invoiceDisplay = new JPanel(this.layout);
		
		JScrollPane sp = new JScrollPane(invoiceDisplay);
		this.add(sp, BorderLayout.CENTER);
	}

	public void addInvoice(Invoice invoice) {
		InvoiceListItem item = new InvoiceListItem(invoice);
		
		int n = this.invoices.size();
	
		GridBagConstraints constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = n*45;
        constr.anchor = GridBagConstraints.FIRST_LINE_START;
        constr.weightx = 1.0;
        constr.weighty = 0.0;
		constr.fill = GridBagConstraints.HORIZONTAL;
		
		if(n != 0)
			this.layout.setConstraints(this.invoices.get(n-1), constr);
		
		constr.weighty = 1.0;
		this.invoiceDisplay.add(item, constr);
		
		this.invoices.add(item);
	}

}

class InvoiceListItem extends JPanel implements MouseListener{

	private static final long serialVersionUID = 1L;
	
	private static InvoiceListItem selectedInvoice = null;

	private Invoice invoice;

	private Color borderColor;
	private Color fontColor;
	private Color bckgColor;
	
	private JLabel date;
	private JLabel amount;
	private JLabel supplier;
	
	private JPanel options;

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
		
		setComponentsLayout();
		
		setColors();
		
		Border border = BorderFactory.createMatteBorder(0, 4, 0, 0, borderColor);
		this.setBorder(border);
		
		createOptions(invoice);
	}
	
	private void setComponentsLayout() {
		this.date.setPreferredSize(new Dimension(150, 20));
		this.amount.setPreferredSize(new Dimension(150, 20));
		this.supplier.setPreferredSize(new Dimension(150, 20));

		this.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipadx = 10;
		gbc.ipady = 10;
		gbc.insets.top = 5;
		gbc.insets.bottom = 5;
		
		this.add(date, gbc);
		
		gbc.gridx = 1;
		this.add(amount, gbc);
		
		gbc.gridx = 2;
		gbc.weightx = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		this.add(supplier, gbc);
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

	private void setColors() {
		setCompColors(date);
		setCompColors(amount);
		setCompColors(supplier);
		
		this.setBackground(bckgColor);
	}
	
	private void setCompColors(JLabel label) {
		label.setForeground(fontColor);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if( this.equals(selectedInvoice) ) {
			
			deselect(selectedInvoice);
		}else {
			if(selectedInvoice != null) 
				deselect(selectedInvoice);
			
			select(this);
		}
	}
	
	private void createOptions(Invoice invoice) {
		this.options = new JPanel();
		
		JButton details = new JButton("Details");
		JButton pay = new JButton("Pay");
		
		details.setPreferredSize(new Dimension(150, 30));
		pay.setPreferredSize(new Dimension(150, 30));
		
		details.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(PrintColor.CYAN+"Loading PDF doucments..." + PrintColor.RESET);
				JFrame frame1 = new JFrame("PDF Document");
				PDFDisplay pdfDisplay1 = new PDFDisplay(invoice.pdfInvoice);		
				frame1.add(pdfDisplay1);
				frame1.setSize(700, 1000);
				frame1.setVisible(true);
				
				if(invoice.pdfAttachment != null) {
					JFrame frame2 = new JFrame("PDF Attachment");
					PDFDisplay pdfDisplay2 = new PDFDisplay(invoice.pdfAttachment);		
					frame2.add(pdfDisplay2);
					frame2.setSize(700, 1000);
					frame2.setVisible(true);
				}
			}
		});
		
		pay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println(invoice);
				QRUtil util = new QRUtil();
				BufferedImage img = util.getQRCode(invoice);
				
				JFrame frame = new JFrame("Test");
				
				frame.setSize(300, 300);
				frame.setLocationRelativeTo(InvoiceListItem.this);
				
				ImageIcon icon = new ImageIcon(img);
				
				frame.add(new JLabel(icon));
				
				frame.setVisible(true);
			}
		});
		
		options.add(details);
		options.add(pay);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(5, 5, 5, 5); 
		
		this.add(options, gbc);
		
		options.setVisible(false);
	}
	
	private void select(InvoiceListItem item) {
		selectedInvoice = this;
		
		item.options.setVisible(true);
		
		Border border = BorderFactory.createMatteBorder(2, 5, 2, 2, borderColor);
		this.setBorder(border);
		
		item.setColors();
	}
	
	private void deselect(InvoiceListItem item) {
		selectedInvoice = null;
		
		item.options.setVisible(false);
		
		Border border = BorderFactory.createMatteBorder(0, 4, 0, 0, item.borderColor);
		item.setBorder(border);
		
		item.setColors();
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
