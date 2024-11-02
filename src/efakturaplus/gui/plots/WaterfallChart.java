package efakturaplus.gui.plots;

import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceType;

import javax.xml.crypto.Data;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class WaterfallChart extends Plot{
    public WaterfallChart(){
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

        double DATA_WIDTH = getWidth() - 2 * BORDER_GAP;
        double DATA_HEIGHT = getHeight() - 2 * BORDER_GAP;

        // Pravougaonici
        for(PlotItem plotItem : items){
            g.setColor(plotItem.color);

            int x = (int) (plotItem.x * DATA_WIDTH);
            int y = (int) (plotItem.y * DATA_HEIGHT);
            int width = (int) (plotItem.width * DATA_WIDTH);
            int height = (int) (plotItem.height * DATA_HEIGHT);

            g.fillRect(BORDER_GAP + x, BORDER_GAP + y, width, height);
        }

        // Crtice
        for (PlotItem item : items) {
            g.setColor(Color.BLACK);

            int x = (int) (item.x * DATA_WIDTH);
            int width = (int) (item.width * DATA_WIDTH);

            int x0 = BORDER_GAP + x + width/2;
            int x1 = x0;
            int y0 = getHeight() - BORDER_GAP - GRAPH_POINT_WIDTH;
            int y1 = y0 + 2 * GRAPH_POINT_WIDTH;
            g2.drawLine(x0, y0, x1, y1);
        }
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

        double itemWidth = 1.0 / invoices.size();

        double minValue = 0;
        double maxValue = 0;
        double currentValue = 0;

        for(Invoice invoice : invoices){
            currentValue += invoice.payableAmount * ((invoice.type == InvoiceType.PURCHASE) ? -1 : 1);

            maxValue = Math.max(currentValue, maxValue);
            minValue = Math.min(currentValue, minValue);
        }

        double totalValueDiff = maxValue - minValue;
        double heightOffset = Math.abs(minValue) / totalValueDiff ;

        double currentHeight = 1 - heightOffset;

        for(int i=0; i < invoices.size(); ++i){
            PlotItem item = new PlotItem(invoices.get(i));

            item.width = itemWidth;
            item.height = 0.9 * invoices.get(i).payableAmount / totalValueDiff;
            item.x = i * itemWidth;

            if(invoices.get(i).type == InvoiceType.SALES){
                currentHeight -= item.height;
                item.y = (currentHeight);
            }else{
                item.y = (currentHeight);
                currentHeight += item.height;
            }

            System.out.println("Item: ("+item.x+", "+item.y+", "+item.width+", "+item.height+")");
            this.items.add(item);
        }
    }

    @Override
    protected PlotItem isItemFocused(int x, int y) {
        x /= getWidth();
        y /= getHeight();

        for(PlotItem item : items){
            if(x >= item.x && x <= item.x + item.width
                    && y >= item.y && y <= item.y + item.height){
                return item;
            }
        }
        return null;
    }
}
