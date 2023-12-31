package efakturaplus.models;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.pdfview.PDFFile;

import efakturaplus.util.PrintColor;

public class Invoice {
	public String id;
	
	public Party customer;
	public Party supplier;

	public Date deliveryDate;

	public String paymentMod;
	public String paymentId;

	public ArrayList<String> payeeFinancialAccs;

	public double taxExAmount; // Tax exclusive amount
	public double payableAmount;

	public InvoiceStatus status;
	public InvoiceType type;
	
	public PDFFile pdfInvoice;
	public PDFFile pdfAttachment;
	
	private String source;

	public Invoice(String id, String source) {
		this.id = id;
		
		this.source = source;
		
		supplier = new Party();
		customer = new Party();

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			InputSource is = new InputSource(new StringReader(source));

			Document document = builder.parse(is);

			parse(document);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(PrintColor.RED + "ERROR: ["+e.getClass()+"]"+"\u001B[0m" + PrintColor.RESET);
			System.out.println(PrintColor.GREEN_BOLD + e.getMessage() + PrintColor.RESET);
		}
	}

	private void parse(Document doc) throws DOMException, ParseException, IOException {
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

		Node dateNode = doc.getElementsByTagName("cbc:IssueDate").item(0);
		this.deliveryDate = format.parse(dateNode.getTextContent());

		/*
		 * PAYMENT ID PARSING
		 */
		Node paymentIdNode = doc.getElementsByTagName("cbc:PaymentID").item(0);
		if(paymentIdNode != null) {
			String idString = paymentIdNode.getTextContent();
			
			if(idString.contains("mod")) {
				this.paymentMod = idString.substring(4, 6);
				this.paymentId = idString.substring(8);
			}else {
				this.paymentMod = "";
				this.paymentId = idString;
			}
		}
		/*
		 * PARSING PAYEE FINANCIAL ACCOUNTS
		 */
		this.payeeFinancialAccs = new ArrayList<>();

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
		
		/*
		 * PARSING PDF INVOICE AND PDF ATTACHMENT
		 * */
		Node DocumentPDF = doc.getElementsByTagName("env:DocumentPdf").item(0);
		byte[] documentBytes = Base64.getDecoder().decode(DocumentPDF.getTextContent().getBytes("UTF-8"));
		/*FileOutputStream fos = new FileOutputStream(this.supplier.name+".pdf");
		fos.write(documentBytes);
		fos.close();
		
		fos = new FileOutputStream(this.supplier.name+".xml");
		fos.write(this.source.getBytes());
		
		fos.close();*/
		
		
		this.pdfInvoice = new PDFFile(ByteBuffer.wrap(documentBytes));
		System.out.println(this.pdfInvoice.getNumPages());
		
		/*
		 * PARSING PDF ATTACHMENT
		 * */
		Node AttachmentPDF = doc.getElementsByTagName("cbc:EmbeddedDocumentBinaryObject").item(0);
		if(AttachmentPDF != null) {
			byte[] attachmentBytes = Base64.getDecoder().decode(AttachmentPDF.getTextContent().getBytes("UTF-8"));
			this.pdfAttachment = new PDFFile(ByteBuffer.wrap(attachmentBytes));
		}
	}

	private void parseParty(Party p, Node node) {
		switch (node.getNodeName()) {
		case "cbc:Name":
			if(p.name == null)
				p.name = toLatin(node.getTextContent().replace("\\", ""));
			break;
		case "cbc:StreetName":
			p.streetName = toLatin(node.getTextContent());
			break;
		case "cbc:CityName":
			p.cityName = toLatin(node.getTextContent());
			break;
		case "cbc:PostalZone":
			p.postalZone = toLatin(node.getTextContent());
			break;
		case "cbc:IdentificationCode":
			p.countryIdCode = toLatin(node.getTextContent());
			break;
		default:
			NodeList childs = node.getChildNodes();
			for (int i = 0; i < childs.getLength(); i++) {
				parseParty(p, childs.item(i));
			}
			break;
		}
	}
	
	
	private String toLatin(String text) {
		StringBuilder res = new StringBuilder(text);
		
		String[] latin = {" ", "a","b","v","g","d","dj","e","ž","z","i","j","k","l","lj","m","n","nj","o","p","r","s","t","ć","u","f","h","c","č","dž","š"};
		char[] cyrillic = {' ','а','б','в','г','д','ђ','е','ж','з','и','ј','к','л','љ','м','н','њ','о','п','р','с','т','ћ','у','ф','х','ц','ч','џ','ш'};
		
		text = text.toLowerCase();
		
		for(int i =0; i<text.length(); ++i) {
			for(int j=0;j<cyrillic.length; ++j) {
				if(text.charAt(i) == cyrillic[j])
					res.replace(i, i+1, latin[j]);
			}
			
		}
		
		text = res.toString();
		
		return text.toUpperCase();
		}

	@Override
	public String toString() {
		String result = "";

		DateFormat format = new SimpleDateFormat("dd.MM.yyyy");

		result += "["+format.format(this.deliveryDate)+"] ";


		result += this.supplier;
		result += " --> " + this.payableAmount+" RSD";

		return result;
	}

	public String getDateString() {
		DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		
		if(this.deliveryDate != null)
			return format.format(this.deliveryDate);
		return format.format(new Date());
	}


}
