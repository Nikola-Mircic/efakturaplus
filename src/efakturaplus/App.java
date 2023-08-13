package efakturaplus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

import efakturaplus.models.Invoice;
import efakturaplus.util.EFakturaUtil;

public class App {
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Please enter your API key:");
		
		String API_KEY = sc.next();
		
		sc.close();
		
		EFakturaUtil efaktura = EFakturaUtil.getInstance(API_KEY);
		
		ArrayList<String> ids = efaktura.getIdsList();
		
		for(String id : ids) {
			Invoice invoice = efaktura.getInvoice(id);
			
			DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
			
			System.out.println("<"+format.format(invoice.deliveryDate)+">  ("+invoice.paymentMod+")"+invoice.paymentId);
			
			if(invoice.payeeFinancialAccs != null) {
				for(String financialAcc : invoice.payeeFinancialAccs) {
					System.out.println("<"+financialAcc+">");
				}
			}
			
			System.out.println(invoice.supplier);
			System.out.println(invoice.customer);
			System.out.println("--> " + invoice.payableAmount+" RSD");
		}
	}

}
