package efakturaplus;

import java.util.Scanner;

import efakturaplus.util.EFakturaUtil;

public class App {
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Please enter your API key:");
		
		String API_KEY = sc.next();
		
		sc.close();
		
		
		EFakturaUtil efaktura = EFakturaUtil.getInstance(API_KEY);
		
		efaktura.getInvoiceExample();
		efaktura.getIdsList();
	}

}
