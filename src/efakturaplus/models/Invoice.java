package efakturaplus.models;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class Invoice {
	private Document document;
	
	public Invoice(String source) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			InputSource is = new InputSource(new StringReader(source));
			
			this.document = builder.parse(is);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: ["+e.getClass()+"]");
		}
	}
	
	public String parseSupplierParty() {
		Node supplierPartyName = document.getElementsByTagName("cbc:Name").item(0);
		
		return supplierPartyName.getTextContent();
	}
}
