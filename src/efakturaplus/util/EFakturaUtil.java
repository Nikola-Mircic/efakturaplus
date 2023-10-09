package efakturaplus.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Flow.Subscriber;

import org.json.JSONArray;
import org.json.JSONObject;

import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceStatus;
import efakturaplus.models.InvoiceType;
import efakturaplus.models.User;

public class EFakturaUtil {
	//Singleton instance
	private static EFakturaUtil instance = null;

	private String API_KEY = "";

	private String efakturaURI = "https://efaktura.mfin.gov.rs/api/publicApi/";
	
	private String invoiceXmlURI;
	private String invoiceIdsURI;
	
	private EFakturaUtil(String API_KEY) {
		this.API_KEY = API_KEY;
	}

	public static EFakturaUtil getInstance() {
		if(instance == null) {
			instance = new EFakturaUtil(User.API_KEY);
		}

		return instance;
	}

	private ArrayList<String> getIdsFromResponse(InvoiceType type, HttpResponse<String> response){
		ArrayList<String> ids = new ArrayList<>();

		JSONObject object = new JSONObject(response.body());
		JSONArray invoiceIds;
		
		if(type == InvoiceType.SALES)
			invoiceIds = object.getJSONArray("SalesInvoiceIds");
		else
			invoiceIds = object.getJSONArray("PurchaseInvoiceIds");
		
		invoiceIds.toList().forEach((id) -> ids.add(id.toString()));
		return ids;
	}

	private HttpResponse<String> sendRequest(HttpRequest request) {
		HttpClient client = HttpClient.newHttpClient();
		try {
			// GetInvoiceRequest
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

		System.out.println("[Status] getInvoice("+invoiceId+") : " + PrintColor.MAGENTA +res.statusCode() + PrintColor.RESET);

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
		System.out.println("[Status] getIdsList() : " + PrintColor.MAGENTA + res.statusCode() + PrintColor.RESET);

		return ids;
	}

	public ArrayList<Invoice> getInvoices(InvoiceType type, InvoiceStatus status){
		createURI(type, status);
		
		ArrayList<Invoice> list = new ArrayList<>();

		ArrayList<String> ids = getIdsList(type, status);

		for(String id: ids) {
			Invoice inv = this.getInvoice(id);
			inv.status = status;
			list.add(inv);
		}

		return list;
	}
	
	private void createURI(InvoiceType type, InvoiceStatus status) {
		Date date = new Date();
		
		Calendar c = Calendar.getInstance();
		
		c.setTime(date);
		
		c.add(Calendar.MONTH, -3);
		c.set(Calendar.DAY_OF_MONTH, 1);
		
		Date fromDate = c.getTime();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		
		String fromDateStr = "dateFrom=" + format.format(fromDate);
		String toDateStr = "dateTo=" + format.format(date);
		
		if(type == InvoiceType.PURCHASE) {
			this.invoiceIdsURI = efakturaURI + "purchase-invoice/ids?status=" + status + "&" + fromDateStr + "&" + toDateStr;
			this.invoiceXmlURI = efakturaURI + "purchase-invoice/xml" + "?invoiceId=";
		}else {
			this.invoiceIdsURI = efakturaURI + "sales-invoice/ids?status=" + status + "&" + fromDateStr + "&" + toDateStr;
			this.invoiceXmlURI = efakturaURI + "sales-invoice/xml" + "?invoiceId=";
		}
		
	}
}
