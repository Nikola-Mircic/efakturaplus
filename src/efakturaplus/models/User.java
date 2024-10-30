package efakturaplus.models;

import java.util.ArrayList;

public class User {
	private static User instance;

	public String API_KEY = "";
	public ArrayList<Invoice> purchases;
	public ArrayList<Invoice> sales;

	// Income, outcome and tax amounts
	public double totalIncome = 0;
	public double totalOutcome = 0;
	public double totalIncomeTax = 0;
	public double totalOutcomeTax = 0;


	private User(){
		purchases = new ArrayList<>();
		sales = new ArrayList<>();
	}

	public static User getUser(){
		if(instance == null){
			instance = new User();
		}
		return instance;
	}

	public static void useApiKey(String apiKey){
		User user = getUser();
		user.API_KEY = apiKey;
		user.purchases.clear();
		user.sales.clear();
	}

	public void addInvoice(Invoice invoice){
		if(invoice.type == InvoiceType.PURCHASE) {
			this.purchases.add(invoice);
			this.totalOutcome += invoice.payableAmount;
			this.totalOutcomeTax += invoice.payableAmount - invoice.taxExAmount;
		}else {
			this.sales.add(invoice);
			this.totalIncome += invoice.payableAmount;
			this.totalIncomeTax += invoice.payableAmount - invoice.taxExAmount;
		}
	}
}
