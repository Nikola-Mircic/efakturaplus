package efakturaplus.gui.plots;

import efakturaplus.models.Invoice;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Comparator;

public class BarPlot extends Plot{

    public BarPlot(){
        super();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        // create x and y axes
        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

        System.out.println("Rendering " + items.size() + " items ....");

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

        int a = 5;
    }

    @Override
    void makeItems(ArrayList<Invoice> invoices) {
        this.items = new ArrayList<>();

        invoices.sort(new Comparator<Invoice>() {
            @Override
            public int compare(Invoice o1, Invoice o2) {
                return o1.deliveryDate.compareTo(o2.deliveryDate);
            }
        });

        double DATA_WIDTH = getWidth() - 2 * BORDER_GAP;
        double DATA_HEIGHT = getHeight() - 2 * BORDER_GAP;

        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime from = LocalDate.now().minusMonths(3).atStartOfDay();

        double maxAmount = -1;

        for(Invoice inv : invoices)
            maxAmount = Math.max(maxAmount, inv.payableAmount);

        for(Invoice inv: invoices){
            PlotItem item = new PlotItem(inv);

            long daysToItem = Duration.between(from, inv.deliveryDate).toDays();
            long totalDays = Duration.between(from, today).toDays();

            item.x = (int) (DATA_WIDTH * daysToItem / totalDays);
            item.y = (int) ((1 - 0.9 * inv.payableAmount / maxAmount) * DATA_HEIGHT);
            item.width = GRAPH_POINT_WIDTH;
            item.height = (int) DATA_HEIGHT - item.y;

            this.items.add(item);
        }
    }

    @Override
    protected PlotItem isItemFocused(int x, int y) {
        for(PlotItem item : items){
            if(x >= item.x - item.width && x <= item.x + item.width
                    && y >= item.y && y <= item.y + item.height){
                return item;
            }
        }
        return null;
    }
}