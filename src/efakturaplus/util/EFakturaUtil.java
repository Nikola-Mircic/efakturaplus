package efakturaplus.util;

import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Flow.Subscriber;

import org.json.JSONArray;
import org.json.JSONObject;

import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceStatus;
import efakturaplus.models.InvoiceType;
import efakturaplus.models.User;

public class EFakturaUtil {
	//Singleton instance

	private String API_KEY = "";

	private String efakturaURI = "https://efaktura.mfin.gov.rs/api/publicApi/";
	
	private String invoiceXmlURI;
	private String invoiceIdsURI;
	
	private Set<String> loadedIds;
	
	private EFakturaUtil(String API_KEY) {
		this.API_KEY = API_KEY;
		this.loadedIds = new HashSet<String>();
	}

	public static EFakturaUtil getInstance() {
		return new EFakturaUtil(User.getUser().API_KEY);
	}

	private ArrayList<String> getIdsFromResponse(InvoiceType type, HttpResponse<String> response){
		ArrayList<String> ids = new ArrayList<>();
		
		System.out.println("[ " + type + " ] response for IDs : ");
		System.out.println(response.body());
		
		JSONObject object = new JSONObject(response.body());
		JSONArray invoiceIds;
		
		if(type == InvoiceType.SALES)
			invoiceIds = object.getJSONArray("SalesInvoiceIds");
		else
			invoiceIds = object.getJSONArray("PurchaseInvoiceIds");
		
		invoiceIds.toList().forEach((id) -> {
			if(loadedIds.contains(id.toString()))
				return;
			
			loadedIds.add(id.toString());
			ids.add(id.toString());
		});
		
		return ids;
	}
	
	private synchronized HttpResponse<String> sendRequest(HttpRequest request) {
		HttpClient client = HttpClient.newHttpClient();
		try {
			// GetInvoiceRequest
			Thread.sleep(1000);
			HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());

			return res;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public Invoice getInvoice(String invoiceId) {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.header("ApiKey", this.API_KEY)
				.header("accept", "*/*")
				.uri(URI.create(invoiceXmlURI+invoiceId))
				.build();

		HttpResponse<String> res = sendRequest(request);

		Invoice invoice = new Invoice(invoiceId, res.body());
		
		System.out.println("[Status: "+invoice.status+"] getInvoice("+invoiceId+") : " + PrintColor.MAGENTA +res.statusCode() + PrintColor.RESET);

		return invoice;
	}

	public ArrayList<String> getIdsList(InvoiceType type, InvoiceStatus status) {
		
		HttpRequest getIDRequest = HttpRequest.newBuilder()
				.POST(new BodyPublisher() {
					@Override
					public void subscribe(Subscriber<? super ByteBuffer> subscriber) {
						// TODO Auto-generated method stub
					}
					@Override
					public long contentLength() {
						// TODO Auto-generated method stub
						return 0;
					}
				})
				.header("ApiKey", this.API_KEY)
				.header("accept", "text/plain")
				.uri(URI.create(invoiceIdsURI))
				.build();
				

		HttpResponse<String> res = sendRequest(getIDRequest);

		ArrayList<String> ids = getIdsFromResponse(type, res);

		System.out.println(ids);

		return ids;
	}

	public ArrayList<Invoice> getInvoices(InvoiceType type, InvoiceStatus status, LocalDate from, LocalDate to){
		createInvoicURI(type, status, from, to);
		
		ArrayList<Invoice> list = new ArrayList<>();

		ArrayList<String> ids = getIdsList(type, status);

		for(String id: ids) {
			Invoice inv = this.getInvoice(id);
			inv.status = status;
			inv.type = type;
			list.add(inv);
		}

		return list;
	}
	
	private void createInvoicURI(InvoiceType type, InvoiceStatus status, LocalDate from, LocalDate to) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		String fromDateStr = "dateFrom=" + format.format(from);
		String toDateStr = "dateTo=" + format.format(to);
		
		if(type == InvoiceType.PURCHASE) {
			this.invoiceIdsURI = efakturaURI + "purchase-invoice/ids?status=" + status + "&" + fromDateStr + "&" + toDateStr;
			this.invoiceXmlURI = efakturaURI + "purchase-invoice/xml" + "?invoiceId=";
		}else {
			this.invoiceIdsURI = efakturaURI + "sales-invoice/ids?status=" + status + "&" + fromDateStr + "&" + toDateStr;
			this.invoiceXmlURI = efakturaURI + "sales-invoice/xml" + "?invoiceId=";
		}
	}
	
	public void approveOrReject(Invoice invoice, boolean approve) {
		JSONObject object = new JSONObject();
		
		System.out.println("ID: "+ invoice.id);
		
		object.put("invoiceId", invoice.id);
		object.put("accepted", approve);
		object.put("comment", "");
		
		String reqestBody = object.toString();
		System.out.println(reqestBody);
		
		HttpRequest approveOrReject = HttpRequest.newBuilder()
				.POST(BodyPublishers.ofString(reqestBody))
				.header("accept", "text/plain")
				.header("ApiKey", this.API_KEY)
				//"Content-Type: application/json"
				.header("Content-Type", "application/json")
				.uri(URI.create("https://efaktura.mfin.gov.rs/api/publicApi/purchase-invoice/acceptRejectPurchaseInvoice"))
				.build();
		
		HttpResponse<String> res = sendRequest(approveOrReject);
		
		
		System.out.println("["+res.statusCode()+"] "+res.body());
	}
}
