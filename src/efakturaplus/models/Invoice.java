package efakturaplus.models;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
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
	
	public ArrayList<String> payeeFinancialAccs;
	
	public double taxExAmount; // Tax exclusive amount
	public double payableAmount;
	
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
			System.out.println("\u001B[31m" + "ERROR: ["+e.getClass()+"]"+"\u001B[0m");
		}
	}
	
	private void parse(Document doc) throws DOMException, ParseException {
		/*
		 * PARTY PARSING
		 */
		NodeList parties = doc.getElementsByTagName("cac:Party");
		
		Node supplierParty = parties.item(0);
		Node customerParty = parties.item(1);
		
		parseParty(supplier, supplierParty);
		parseParty(customer, customerParty);
		
		/*
		 * DATE PARSING
		 */
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		Node dateNode = doc.getElementsByTagName("cbc:ActualDeliveryDate").item(0);
		this.deliveryDate = format.parse(dateNode.getTextContent());
		
		/*
		 * PAYMENT ID PARSING
		 */
		Node paymentIdNode = doc.getElementsByTagName("cbc:PaymentID").item(0);
		String idString = paymentIdNode.getTextContent();
		
		this.paymentMod = idString.substring(4, 6);
		this.paymentId = idString.substring(8);
		
		/*
		 * PARSING PAYEE FINANCIAL ACCOUNTS
		 */
		this.payeeFinancialAccs = new ArrayList<String>();
		
		NodeList accountNode = doc.getElementsByTagName("cac:PayeeFinancialAccount");

		for(int i=0; i<accountNode.getLength(); ++i) {
			this.payeeFinancialAccs.add(accountNode.item(i).getChildNodes().item(1).getTextContent());
		}
		
		/*
		 * PARSING PAYABLE AMOUNT
		 */
		Node TaxExAmount = doc.getElementsByTagName("cbc:TaxExclusiveAmount").item(0);
		Node PayableAmount = doc.getElementsByTagName("cbc:PayableAmount").item(0);
		
		this.taxExAmount = Double.parseDouble(TaxExAmount.getTextContent());
		this.payableAmount = Double.parseDouble(PayableAmount.getTextContent());
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
