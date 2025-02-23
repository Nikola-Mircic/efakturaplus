package efakturaplus.gui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import efakturaplus.gui.StretchIcon;
import efakturaplus.gui.Window;
import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceStatus;
import efakturaplus.models.InvoiceType;
import efakturaplus.models.User;
import efakturaplus.util.EFakturaUtil;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Window parent;
	
	// Display invoices
	private CardLayout dataPanelLayout;
	private JPanel dataPanel;
	
	// Navigation
	private JPanel navigator;
	private JButton purchaseBtn;
	private JButton salesBtn;
	private JButton statsBtn;
	
	private InvoicePanel purchaseIl;
	private InvoicePanel salesIl;
	
	private StatisticsPanel statsPanel;

	public MainPanel(Window parent) {
		this.parent = parent;
		this.setLayout(new BorderLayout());
		
		// Display invoices
		dataPanelLayout = new CardLayout();
		this.dataPanel = new JPanel();
		
		dataPanel.setLayout(dataPanelLayout);

		User user = User.getUser();

		purchaseIl = new InvoicePanel(user.purchases);
		dataPanel.add(purchaseIl, "PURCHASE");
		
		salesIl = new InvoicePanel(user.sales);
		dataPanel.add(salesIl, "SALES");
		
		statsPanel = new StatisticsPanel();
		dataPanel.add(statsPanel, "STATS");
		
		dataPanelLayout.show(dataPanel, "PURCHASE");
		
		this.add(dataPanel, BorderLayout.CENTER);
		
		// Navigation
		createNavigator();
	}
	
	private void createNavigator() {
		Image purchaseBtnImg = null;
		Image salesBtnImg = null;
		Image statsButtonImg = null;
		Image logoutImg = null;
		try {
			purchaseBtnImg = ImageIO.read(new File("icons/invoice-purchase.png"));
			salesBtnImg = ImageIO.read(new File("icons/invoice-sales.png"));
			statsButtonImg = ImageIO.read(new File("icons/stats.png"));
			logoutImg = ImageIO.read(new File("icons/logout.png"));
			
			//this.purchaseBtn.add(new JLabel("Sales"));
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.navigator = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 0;
        gbc.gridy = 0;
		
		this.purchaseBtn = makeButton(purchaseBtnImg, "Purchase", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dataPanelLayout.show(dataPanel, "PURCHASE");
			}
		});
		
		navigator.add(purchaseBtn, gbc);
		
		
		gbc.gridy = 1;
		this.salesBtn = makeButton(salesBtnImg, "Sales", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dataPanelLayout.show(dataPanel, "SALES");
			}
		});
		
		navigator.add(salesBtn, gbc);
		
		gbc.gridy = 2;
		this.statsBtn = makeButton(statsButtonImg, "Statistics", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dataPanelLayout.show(dataPanel, "STATS");
			}
		});
		
		navigator.add(statsBtn, gbc);
		
		JButton logout = makeButton(logoutImg, "Log out", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.showKeyPanel();
			}
		});
		
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		gbc.weighty = 1;
		
		navigator.add(logout, gbc);
		
		this.add(navigator, BorderLayout.WEST);
	}
	
	private JButton makeButton(Image ico, String text, ActionListener listener) {
		JButton btn = new JButton();
		
		btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));

		if(ico != null)
			btn.add(centeredLabel(new StretchIcon(ico)));

		btn.add(centeredLabel(text));
		btn.addActionListener(listener);
		
		return btn;
	}
	
	private JLabel centeredLabel(String text) {
		JLabel label = new JLabel(text);
		label.setAlignmentX(CENTER_ALIGNMENT);
		return label;
	}
	
	private JLabel centeredLabel(StretchIcon ico) {
		JLabel label = new JLabel(ico);
		label.setAlignmentX(CENTER_ALIGNMENT);
		return label;
	}

	public void loadUserInvoices() {
		Thread loadingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				EFakturaUtil efu = EFakturaUtil.getInstance();

				User user = User.getUser();

				efu.getInvoices((Invoice inv) -> {
					user.addInvoice(inv);

					purchaseIl.printInvoices();
					salesIl.printInvoices();

					dataPanel.revalidate();
				});
				statsPanel.updateData();
			}
		});

		loadingThread.start();
	}

}
