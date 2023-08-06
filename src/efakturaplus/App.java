package efakturaplus;

import efakturaplus.util.EFakturaUtil;

public class App {
	
	public static void main(String[] args) {
		EFakturaUtil efaktura = new EFakturaUtil();
		
		efaktura.getInvoiceExample();
		efaktura.getIdsList();
	}

}
