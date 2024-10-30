package efakturaplus.gui.plots;

import efakturaplus.models.Invoice;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MultiPlotPanel extends JPanel {
    private CardLayout layout;
    private ArrayList<Plot> plots;

    public MultiPlotPanel() {
        this(800, 700);
    }

    public MultiPlotPanel(int prefWidth, int prefHeight) {
        super();
        this.setPreferredSize(new Dimension(prefWidth, prefHeight));

        this.layout = new CardLayout();
        this.setLayout(this.layout);
        this.plots = new ArrayList<>();
    }

    public void addPlot(Plot plot, Object constraint){
        this.add(plot, constraint);

        if( !this.plots.isEmpty() )
            this.layout.show(this, constraint.toString());

        this.plots.add(plot);
    }

    public void switchPlot(){
        this.layout.next(this);
    }

    public void switchPlot(String name){
        this.layout.show(this, name);
    }

    public void updatePlots(ArrayList<Invoice> invoices){
        for(Plot plot : this.plots){
            plot.updateData(invoices);
        }
    }
}
