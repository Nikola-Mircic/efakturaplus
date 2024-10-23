package efakturaplus.gui.panels;

import java.awt.*;
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


	public StatisticsPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.plotsPanel = new MultiPlotPanel();

		this.plotsPanel.addPlot(new BarPlot(), "BAR-Plot");
		this.plotsPanel.addPlot(new WaterfallChart(), "WATERFALL-PLOT");

		this.add(plotToggleButton());
		this.add(plotsPanel);
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

	public void updatePlot() {
		User user = User.getUser();

		ArrayList<Invoice> data = new ArrayList<>();
		data.addAll(user.purchases);
		data.addAll(user.sales);

		this.plotsPanel.updatePlots(data);
		repaint();
	}
}