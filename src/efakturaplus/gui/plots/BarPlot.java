package efakturaplus.gui.plots;

import efakturaplus.models.Invoice;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Comparator;

public class BarPlot extends Plot{
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
}
