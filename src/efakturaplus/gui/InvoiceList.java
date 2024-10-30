package efakturaplus.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.Border;

import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceStatus;
import efakturaplus.models.InvoiceType;
import efakturaplus.util.EFakturaUtil;
import efakturaplus.util.PrintColor;
import efakturaplus.util.QRUtil;;

public class InvoiceList extends JPanel {

	private static final long serialVersionUID = 1L;

	ArrayList<Invoice> invoices;
	
	private JPanel invoiceDisplay;
	private GridBagLayout layout;

	public InvoiceList(ArrayList<Invoice> invoices) {
		this.setLayout(new BorderLayout());
		
		this.invoices = invoices;
		
		this.layout = new GridBagLayout();
		this.invoiceDisplay = new JPanel(this.layout);
		
		JScrollPane sp = new JScrollPane(invoiceDisplay);
		this.add(sp, BorderLayout.CENTER);
		
		printInvoices();
	}
	
	public void printInvoices() {
		this.invoiceDisplay.removeAll();

		this.invoices.sort(new Comparator<Invoice>() {
			@Override
			public int compare(Invoice item1, Invoice item2) {
				return item2.deliveryDate.compareTo(item1.deliveryDate);
			}
		});

		int n = this.invoices.size();
		GridBagConstraints constr = new GridBagConstraints();

		ArrayList<InvoiceListItem> items = new ArrayList<InvoiceListItem>();

		if (n == 0) {
			ImageIcon loading_gif = new ImageIcon("./icons/loading.gif");
			JLabel loading = new JLabel("Loading ...", loading_gif, JLabel.CENTER);

			constr.gridx = 0;
			constr.gridy = 0;
			constr.weightx = 1;
			constr.anchor = GridBagConstraints.CENTER;

			this.invoiceDisplay.add(loading, constr);
		}

		InvoiceListItem item = null;
		
		for(int i=0; i<n; ++i) {
			item = new InvoiceListItem(this.invoices.get(i));

			constr.gridx = 0;
			constr.gridy = i*45;
	        constr.anchor = GridBagConstraints.FIRST_LINE_START;
	        constr.weightx = 1.0;
	        constr.weighty = 0.0;
			constr.fill = GridBagConstraints.HORIZONTAL;
			
			this.invoiceDisplay.add(item, constr);
		}
		
		if(n != 0) {
			constr.weighty = 1.0;
			this.layout.setConstraints(item, constr);
		}
	}
}

class InvoiceListItem extends JPanel implements MouseListener{

	private static final long serialVersionUID = 1L;
	
	private static InvoiceListItem selectedInvoice = null;

	public Invoice invoice;

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
		
		DecimalFormat formater = new DecimalFormat("###,###,##0.00");
		
		this.amount = new JLabel("" + formater.format(this.invoice.payableAmount) + " "+this.invoice.currency, JLabel.RIGHT);
		this.supplier = new JLabel(this.invoice.supplier.name.toString());
		
		this.addMouseListener(this);
		
		setComponentsLayout();
		
		setColors();
		
		Border border = BorderFactory.createMatteBorder(0, 4, 0, 0, borderColor);
		this.setBorder(border);
		
		createOptions(invoice);
	}
	
	private void setComponentsLayout() {
		this.date.setPreferredSize(new Dimension(120, 20));
		this.amount.setPreferredSize(new Dimension(120, 20));
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
		gbc.insets.right = 50;
		this.add(amount, gbc);
		
		gbc.gridx = 2;
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
		System.out.println(this.invoice.status.name() + " " + this.invoice.payableAmount + " " + this.invoice.supplier.name);
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
		
		details.setPreferredSize(new Dimension(150, 30));
		
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
		
		options.add(details);
		
		if(invoice.type == InvoiceType.PURCHASE) {
			JButton pay = new JButton("Pay");
			JButton approve = new JButton("Approve");
			
			pay.setPreferredSize(new Dimension(150, 30));
			approve.setPreferredSize(new Dimension(150, 30));
			
			pay.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					System.out.println(invoice);
					QRUtil util = new QRUtil();
					BufferedImage img = util.getQRCode(invoice);
					
					JFrame frame = new JFrame("Your QR code: ");
					
					frame.setSize(300, 350);
					frame.setLocationRelativeTo(InvoiceListItem.this);
					
					ImageIcon icon = new ImageIcon(img);
					
					JButton ok = new JButton("OK");
					ok.setSize(100, 50);
					
					ok.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							approveOrReject(true);
							frame.dispose();
						}
					});
					
					JButton cancel = new JButton("Cancel");
					cancel.setSize(100, 50);
					
					cancel.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							frame.dispose();
						}
					});
					
					frame.getContentPane().setLayout(new FlowLayout());
					
					frame.add(new JLabel(icon));
					
					frame.add(ok);
					frame.add(cancel);
					
					frame.setVisible(true);
				}
			});
			
			approve.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					approveOrReject(true);
				}
			});
			
			options.add(pay);
			options.add(approve);
		}
		
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
	
	private void approveOrReject(boolean approve) {
		if(approve)
			this.invoice.status = InvoiceStatus.Approved;
		
		EFakturaUtil util = EFakturaUtil.getInstance();
		
		util.approveOrReject(this.invoice, approve);
		
		selectBorderColor();
		
		Border border = BorderFactory.createMatteBorder(0, 4, 0, 0, borderColor);
		InvoiceListItem.this.setBorder(border);
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
