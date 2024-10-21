package efakturaplus.gui.plots;

import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceType;
import efakturaplus.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class Plot extends JComponent implements MouseListener, MouseMotionListener {

    protected int PREF_W = 800;
    protected int PREF_H = 650;
    protected int BORDER_GAP = 30;
    protected Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
    protected Stroke GRAPH_STROKE = new BasicStroke(3f);
    protected int GRAPH_POINT_WIDTH = 5;

    protected ArrayList<PlotItem> items;

    public Plot() {
        addMouseListener(this);
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

        // Pravougaonici
        for(PlotItem plotItem : items){
            g.setColor(plotItem.color);
            g.fillRect(BORDER_GAP + plotItem.x - plotItem.width/2, BORDER_GAP + plotItem.y, plotItem.width, plotItem.height);
        }

        // Crtice
        for (PlotItem item : items) {
            g.setColor(Color.BLACK);
            int x0 = BORDER_GAP + item.x;
            int x1 = x0;
            int y0 = getHeight() - BORDER_GAP - GRAPH_POINT_WIDTH;
            int y1 = y0 + 2 * GRAPH_POINT_WIDTH;
            g2.drawLine(x0, y0, x1, y1);
        }
    }

    protected abstract PlotItem isItemFocused(int x, int y);

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        PlotItem item = isItemFocused(e.getX() - BORDER_GAP, e.getY() - BORDER_GAP);
        if(item != null){
            System.out.println(item.getLabel());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        PlotItem item = isItemFocused(e.getX() - BORDER_GAP, e.getY() - BORDER_GAP);
        if(item != null){
            System.out.println(item.getLabel());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }
}
