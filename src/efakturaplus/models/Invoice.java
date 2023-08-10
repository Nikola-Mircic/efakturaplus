package efakturaplus.models;

import java.io.StringReader;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Invoice {
	public Party customer;
	public Party supplier;
	
	public Date deliveryDate;
	
	public String paymentMod;
	public String paymentId;
	
	public String payeeFinancialAcc;
	
	public double taxExAmount; // Tax exclusive amount
	public double taxAmount;
	
	public Invoice(String id, String source) {
		supplier = new Party();
		customer = new Party();
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			InputSource is = new InputSource(new StringReader(source));
			
			Document document = builder.parse(is);
			
			parse(document);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: ["+e.getClass()+"]");
		}
	}
	
	private void parse(Document doc) {
		NodeList parties = doc.getElementsByTagName("cac:Party");
		
		Node supplierParty = parties.item(0);
		Node customerParty = parties.item(1);
		
		parseParty(supplier, supplierParty);
		parseParty(customer, customerParty);
	}
	
	private void parseParty(Party p, Node node) {
		switch (node.getNodeName()) {
		case "cbc:Name":
			p.name = node.getTextContent();
			break;
		case "cbc:StreetName":
			p.streetName = node.getTextContent();
			break;
		case "cbc:CityName":
			p.cityName = node.getTextContent();
			break;
		case "cbc:PostalZone":
			p.postalZone = node.getTextContent();
			break;
		case "cbc:IdentificationCode":
			p.countryIdCode = node.getTextContent();
			break;
		default:
			NodeList childs = node.getChildNodes();
			for (int i = 0; i < childs.getLength(); i++) {
				parseParty(p, childs.item(i));
			}
			break;
		}
	}
	
}

class Party{
	public String name;
	public String streetName;
	public String cityName;
	public String postalZone;
	public String countryIdCode;
	
	@Override
	public String toString() {
		return "[Party] "+name+", "+streetName;
	}
}
