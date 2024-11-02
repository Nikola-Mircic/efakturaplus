package efakturaplus.gui.plots;

import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceType;

import java.awt.*;

public class PlotItem {
    protected static final Color PURCHASE_COLOR = new Color(1.0f, 0.0f, 0.1f);
    protected static final Color SALE_COLOR = new Color(0.0f, 1.0f, 0.1f);

    public Invoice invoiceRef;

    public double x,y;
    public double width,height;
    public Color color;

    public PlotItem(Invoice invoiceRef){
        this(invoiceRef,0,0,0, 0);
    }

    public PlotItem(Invoice invoiceRef, double x, double y){
        this(invoiceRef,x,y,0,0);
    }

    public PlotItem(Invoice invoiceRef, double x, double y, double width, double height){
        this.invoiceRef = invoiceRef;
        this.color = invoiceRef.type == InvoiceType.PURCHASE ? PURCHASE_COLOR : SALE_COLOR;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getValue(){
        return invoiceRef.payableAmount;
    }

    public String getLabel(){
        return (invoiceRef.type == InvoiceType.PURCHASE) ? invoiceRef.supplier.name : invoiceRef.customer.name;
    }
}
