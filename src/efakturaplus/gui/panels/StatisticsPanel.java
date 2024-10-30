package efakturaplus.gui.panels;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import efakturaplus.gui.plots.BarPlot;
import efakturaplus.gui.plots.MultiPlotPanel;
import efakturaplus.gui.plots.Plot;
import efakturaplus.gui.plots.WaterfallChart;
import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceType;
import efakturaplus.models.User;
import efakturaplus.util.Pair;

public class StatisticsPanel extends JPanel {
	
	private Plot plot;

	private MultiPlotPanel plotsPanel;

	private User user;

	private final JLabel totalIncome;
	private final JLabel totalOutcome;
	private final JLabel totalIncomeTax;
	private final JLabel totalOutcomeTax;

	public StatisticsPanel() {
		this.user = User.getUser();

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.plotsPanel = new MultiPlotPanel();

		this.plotsPanel.addPlot(new BarPlot(), "BAR-Plot");
		this.plotsPanel.addPlot(new WaterfallChart(), "WATERFALL-PLOT");

		this.add(plotToggleButton());
		this.add(plotsPanel);

		this.totalOutcome = new JLabel("", JLabel.RIGHT);
		this.totalIncome = new JLabel("", JLabel.RIGHT);
		this.totalOutcomeTax = new JLabel("", JLabel.RIGHT);
		this.totalIncomeTax = new JLabel("", JLabel.RIGHT);

		this.add(inOutTaxAmountPanel());
	}

	private JPanel inOutTaxAmountPanel(){
		JPanel inOutTaxAmountPanel = new JPanel();

		inOutTaxAmountPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		updateAmountLabels();

		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.ipadx = 60;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx = 0;

		gbc.gridy = 0;
		inOutTaxAmountPanel.add(new JLabel("Total income: "), gbc);
		gbc.gridy = 1;
		inOutTaxAmountPanel.add(new JLabel("Total outcome: "), gbc);
		gbc.gridy = 2;
		inOutTaxAmountPanel.add(new JLabel("Total income tax: "), gbc);
		gbc.gridy = 3;
		inOutTaxAmountPanel.add(new JLabel("Total outcome tax: "), gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		inOutTaxAmountPanel.add(totalIncome, gbc);
		gbc.gridy = 1;
		inOutTaxAmountPanel.add(totalOutcome, gbc);
		gbc.gridy = 2;
		inOutTaxAmountPanel.add(totalIncomeTax, gbc);
		gbc.gridy = 3;
		inOutTaxAmountPanel.add(totalOutcomeTax, gbc);

		return inOutTaxAmountPanel;
	}

	private JButton plotToggleButton(){
		JButton btn = new JButton();
		btn.setAlignmentX(RIGHT_ALIGNMENT);

		btn.addActionListener(e -> {
			plotsPanel.switchPlot();
		});

		JLabel lbl = new JLabel("Switch plot type");
		lbl.setAlignmentX(CENTER_ALIGNMENT);
		lbl.setAlignmentY(CENTER_ALIGNMENT);
		lbl.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

		btn.add(lbl);

		return btn;
	}

	public void updateData() {
		updatePlots();
		updateAmountLabels();

		repaint();
	}

	private void updatePlots(){
		ArrayList<Invoice> data = new ArrayList<>();
		data.addAll(user.purchases);
		data.addAll(user.sales);

		this.plotsPanel.updatePlots(data);
	}

	private void updateAmountLabels(){
		this.totalIncome.setText(formatAmount(user.totalIncome) + " RSD");
		this.totalOutcome.setText(formatAmount(user.totalOutcome) + " RSD");
		this.totalIncomeTax.setText(formatAmount(user.totalIncomeTax) + " RSD");
		this.totalOutcomeTax.setText(formatAmount(user.totalOutcomeTax) + " RSD");
	}

	private String formatAmount(double amount){
		DecimalFormat formater = new DecimalFormat("###,###,##0.00");

		return formater.format(amount);
	}
}