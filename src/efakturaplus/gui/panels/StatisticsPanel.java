package efakturaplus.gui.panels;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import efakturaplus.gui.plots.BarPlot;
import efakturaplus.gui.plots.Plot;
import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceType;
import efakturaplus.models.User;
import efakturaplus.util.Pair;

public class StatisticsPanel extends JPanel {
	
	private Plot plot;
	
	public StatisticsPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.plot = new BarPlot();
		this.add(plot);
	}
	
	public void updatePlot() {
		User user = User.getUser();

		ArrayList<Invoice> toPlot = new ArrayList<>(user.purchases);
		toPlot.addAll(user.sales);

		this.plot.updateData(toPlot);
		repaint();
	}
}