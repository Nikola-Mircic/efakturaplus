package efakturaplus.gui;

import java.awt.*;
import java.text.spi.DateFormatSymbolsProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.swing.JPanel;

import ch.randelshofer.util.ArrayUtil;
import efakturaplus.models.Invoice;
import efakturaplus.util.Pair;

public class StatisticsPanel extends JPanel {

	private static final int PREF_W = 800;
	private static final int PREF_H = 650;
	private static final int BORDER_GAP = 30;
	private static final Color GRAPH_COLOR = new Color(0.0f, 1.0f, 0.1f, 0.2f);
	private static final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
	private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
	private static final int GRAPH_POINT_WIDTH = 5;

	private ArrayList<Invoice> invoices;
	
	private Plot plot;
	
	public StatisticsPanel() {
		this.invoices = new ArrayList<Invoice>();
		
		this.plot = new Plot(new ArrayList<Date>(), new ArrayList<Double>());
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

		Stroke oldStroke = g2.getStroke();
		g2.setColor(GRAPH_COLOR);
		g2.setStroke(GRAPH_STROKE);
		for (int i = 0; i < graphPoints.size() - 1; i++) {
			int x1 = graphPoints.get(i).x;
			int y1 = graphPoints.get(i).y;
			int x2 = graphPoints.get(i + 1).x;
			int y2 = graphPoints.get(i + 1).y;
			g2.drawLine(x1, y1, x2, y2);
			
			/*g2.fillPolygon(new int[] {x1, x1, x2, x2},
					   	   new int[] {y1, BORDER_GAP+(int)DATA_HEIGHT, BORDER_GAP+(int)DATA_HEIGHT, y2},
					   	   4);*/
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
		this.invoices.add(invoice);
	}
	
	public void updatePlot() {
		this.plot.updateData(invoices);
		this.plot.makePoints();
		repaint();
	}
}

class Plot {
	public ArrayList<Date> dates;
	public ArrayList<Double> values;

	public ArrayList<Pair<Double, Double>> points;

	public Plot(ArrayList<Date> dates, ArrayList<Double> values) {
		this.dates = dates;
		this.values = values;

		makePoints();
	}
	
	public void updateData(ArrayList<Invoice> invoices) {
		invoices.sort(new Comparator<Invoice>() {

			@Override
			public int compare(Invoice o1, Invoice o2) {
				return o1.deliveryDate.compareTo(o2.deliveryDate);
			}
		});
		
		this.dates = new ArrayList<Date>();
		this.values = new ArrayList<Double>();
		
		for(Invoice inv : invoices) {
			this.dates.add(inv.deliveryDate);
			this.values.add(inv.payableAmount);
		}
	}

	public void makePoints() {
		this.points = new ArrayList<Pair<Double, Double>>();
		
		if(dates.size() == 0)
			return;
		
		Date refDate = Collections.min(dates);
		
		System.out.println(values.toString());
		
		int n = dates.size();
		
		for(int i=1; i<n; ++i) {
			values.set(i, values.get(i-1) + values.get(i));
		}
		
		System.out.println(values.toString());
		
		double maxValue = Collections.max(values);
		double minValue = Collections.min(values);
		
		System.out.println(minValue + " < " + maxValue);

		long dateDiff = (new Date()).getTime() - refDate.getTime();
		dateDiff = TimeUnit.DAYS.convert(dateDiff, TimeUnit.MILLISECONDS);

		for (int i = 0; i < dates.size(); ++i) {
			long diff = dates.get(i).getTime() - refDate.getTime();
			diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

			points.add(new Pair<Double, Double>(1.0 * diff / dateDiff, values.get(i) / maxValue));
		}
		
		points.sort(new Comparator<Pair<Double, Double>>() {
			@Override
			public int compare(Pair<Double, Double> o1, Pair<Double, Double> o2) {
				return (int) (o1.first*1000 - o2.first*1000);
			}
		});
	}
}
