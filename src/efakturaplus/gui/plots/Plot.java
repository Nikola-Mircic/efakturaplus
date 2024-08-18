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

public class Plot extends JComponent {
    private final Color PURCHASE_COLOR = new Color(0.0f, 1.0f, 0.1f, 0.2f);
    private final Color SALE_COLOR = new Color(1.0f, 0.0f, 0.1f, 0.2f);

    public ArrayList<Date> dates;
    public ArrayList<Double> values;
    public ArrayList<Color> colors;

    public ArrayList<Pair<Double, Double>> points;

    public Plot() {
        this.dates = new ArrayList<Date>();
        this.values = new ArrayList<Double>();

        this.colors = new ArrayList<Color>();

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
        this.colors = new ArrayList<Color>();

        for(Invoice inv : invoices) {
            this.dates.add(inv.deliveryDate);
            this.values.add(inv.payableAmount);
            this.colors.add((inv.type.equals(InvoiceType.PURCHASE)) ? PURCHASE_COLOR : SALE_COLOR);
        }
    }

    public void makePoints() {
        this.points = new ArrayList<Pair<Double, Double>>();

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
        }

    }
}
