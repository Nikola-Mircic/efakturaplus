package efakturaplus.gui.panels;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import efakturaplus.gui.plots.Plot;
import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceType;
import efakturaplus.models.User;
import efakturaplus.util.Pair;

public class StatisticsPanel extends JPanel {

	private static final int PREF_W = 800;
	private static final int PREF_H = 650;
	private static final int BORDER_GAP = 30;
	private static final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
	private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
	private static final int GRAPH_POINT_WIDTH = 5;
	
	private Plot plot;
	
	public StatisticsPanel() {
		this.plot = new Plot();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double DATA_WIDTH = getWidth() - 2 * BORDER_GAP;
		double DATA_HEIGHT = getHeight() - 2 * BORDER_GAP;

		ArrayList<Point> graphPoints = new ArrayList<Point>();
		for (int i = 0; i < plot.points.size(); i++) {
			
			int x1 = BORDER_GAP + (int)(plot.points.get(i).first * DATA_WIDTH);
			int y1 = BORDER_GAP + (int)(DATA_HEIGHT - plot.points.get(i).second * DATA_HEIGHT);
			
			graphPoints.add(new Point(x1, y1));
		}

		// create x and y axes
		g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
		g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

		// create hatch marks for y axis.
		for (int i = 0; i < graphPoints.size(); i++) {
			int x0 = BORDER_GAP;
			int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
			int y0 = graphPoints.get(i).y;
			int y1 = y0;
			g2.drawLine(x0, y0, x1, y1);
		}

		// and for x axis
		for (int i = 0; i < graphPoints.size(); i++) {
			int x0 = graphPoints.get(i).x;
			int x1 = x0;
			int y0 = getHeight() - BORDER_GAP;
			int y1 = y0 - GRAPH_POINT_WIDTH;
			g2.drawLine(x0, y0, x1, y1);
		}
		
		
		// Pillars
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(GRAPH_STROKE);
		for (int i = 0; i < graphPoints.size() - 1; i++) {
			int x1 = graphPoints.get(i).x - 5;
			int y1 = graphPoints.get(i).y;
			int x2 = graphPoints.get(i).x + 5;
			int y2 = getHeight() - BORDER_GAP;
			
			g2.setColor(plot.colors.get(i));
			g2.fillRect(x1, y1, 10, y2-y1);
		}

		g2.setStroke(oldStroke);
		g2.setColor(GRAPH_POINT_COLOR);
		for (int i = 0; i < graphPoints.size(); i++) {
			int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
			int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;
			;
			int ovalW = GRAPH_POINT_WIDTH;
			int ovalH = GRAPH_POINT_WIDTH;
			g2.fillOval(x, y, ovalW, ovalH);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREF_W, PREF_H);
	}

	public void addInvoice(Invoice invoice) {
	}
	
	public void updatePlot() {
		User user = User.getUser();

		ArrayList<Invoice> toPlot = new ArrayList<>(user.purchases);
		toPlot.addAll(user.sales);

		this.plot.updateData(toPlot);
		this.plot.makePoints();
		repaint();
	}
}