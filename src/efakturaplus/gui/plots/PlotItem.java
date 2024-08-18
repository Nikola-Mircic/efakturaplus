package efakturaplus.gui.plots;

import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceType;

public class PlotItem {
    private Invoice invoiceRef;

    public int x,y;
    public int height;

    public PlotItem(Invoice invoiceRef){
        this(invoiceRef,0,0, 0);
    }

    public PlotItem(Invoice invoiceRef, int x, int y){
        this(invoiceRef,x,y,0);
    }

    public PlotItem(Invoice invoiceRef, int x, int y, int height){
        this.invoiceRef = invoiceRef;
        this.x = x;
        this.y = y;
        this.height = height;
    }

    public double getValue(){
        return invoiceRef.payableAmount;
    }

    public String getLabel(){
        return (invoiceRef.type == InvoiceType.PURCHASE) ? invoiceRef.supplier.name : invoiceRef.customer.name;
    }
}
