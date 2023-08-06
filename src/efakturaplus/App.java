package efakturaplus;

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
			
			System.out.println("["+id+"] - "+invoice.parseSupplierParty());
		}
	}

}
