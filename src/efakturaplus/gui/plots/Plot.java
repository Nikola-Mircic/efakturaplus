package efakturaplus.gui.plots;

import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceType;
import efakturaplus.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class Plot extends JComponent {

    protected int PREF_W = 800;
    protected int PREF_H = 650;
    protected int BORDER_GAP = 30;
    protected Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
    protected Stroke GRAPH_STROKE = new BasicStroke(3f);
    protected int GRAPH_POINT_WIDTH = 5;

    protected ArrayList<PlotItem> items;

    public Plot() {

    }

    public void updateData(ArrayList<Invoice> invoices) {
        makeItems(invoices);

        repaint();
    }

    abstract void makeItems(ArrayList<Invoice> invoices);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // create x and y axes
        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

        System.out.println("Rendering " + items.size() + "items ....");

        for (PlotItem item : items) {
            int x0 = item.x + item.width/2;
            int x1 = x0;
            int y0 = getHeight() - BORDER_GAP;
            int y1 = y0 - GRAPH_POINT_WIDTH;
            g2.drawLine(x0, y0, x1, y1);
        }

        for(PlotItem plotItem : items){
            g.setColor(plotItem.color);
            g.fillRect(BORDER_GAP + plotItem.x, BORDER_GAP + plotItem.y, plotItem.width, plotItem.height);
        }
    }
       /* this.points = new ArrayList<Pair<Double, Double>>();

        if(dates.size() == 0)
            return;

        Date refDate = Collections.min(dates);

        System.out.println(values.toString());

        int n = dates.size();

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
        }*/

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }
}
