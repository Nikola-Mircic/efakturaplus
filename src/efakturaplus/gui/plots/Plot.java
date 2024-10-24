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

public abstract class Plot extends JComponent implements MouseListener{

    protected int PREF_W = 800;
    protected int PREF_H = 650;
    protected int BORDER_GAP = 30;
    protected Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
    protected Stroke GRAPH_STROKE = new BasicStroke(3f);
    protected int GRAPH_POINT_WIDTH = 5;

    protected ArrayList<PlotItem> items;

    public Plot() {
        this.items = new ArrayList<>();
        addMouseListener(this);
    }

    public void updateData(ArrayList<Invoice> invoices) {
        makeItems(invoices);

        repaint();
    }

    abstract void makeItems(ArrayList<Invoice> invoices);

    protected abstract PlotItem isItemFocused(int x, int y);

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
