package efakturaplus.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
	private static EFakturaUtil instance;

	private String API_KEY = "";

	private String efakturaURI = "https://efaktura.mfin.gov.rs/api/publicApi/";

	InvoiceStatus[] pStatusArr = {InvoiceStatus.ReNotified, InvoiceStatus.New, InvoiceStatus.Approved, InvoiceStatus.Reminded, InvoiceStatus.Seen, InvoiceStatus.Rejected};
	InvoiceStatus[] sStatusArr = {InvoiceStatus.ReNotified, InvoiceStatus.New, InvoiceStatus.Seen, InvoiceStatus.Approved};
	
	private EFakturaUtil(String API_KEY) {
		this.API_KEY = API_KEY;
	}

	public static EFakturaUtil getInstance() {
		if(instance == null)
			instance = new EFakturaUtil(User.getUser().API_KEY);
		return instance;
	}

	public void getInvoices(OnInvoiceListener onInvoiceListener) {
		// Purchase invoices
		ArrayList<String> purchaseIDs = new ArrayList<>();
		for(InvoiceStatus status : pStatusArr) {
			ArrayList<String> statusIds = getIds(InvoiceType.PURCHASE, status, 3);
			System.out.println("[Purchase - "+status.name()+"]:\n\t"+statusIds);
			for(String invoiceID : statusIds) {
				if(purchaseIDs.contains(invoiceID))
					continue;

				purchaseIDs.add(invoiceID);
				Invoice invoice = getInvoice(InvoiceType.PURCHASE, status, invoiceID);
				onInvoiceListener.onInvoice(invoice);
			}
		}

		// Sales invoices
		ArrayList<String> salesIds = new ArrayList<>();
		for(InvoiceStatus status : sStatusArr) {
			ArrayList<String> statusIds = getIds(InvoiceType.SALES, status, 3);
			System.out.println("[Sales - "+status.name()+"]:\n\t"+statusIds);
			for(String invoiceID : statusIds) {
				if(salesIds.contains(invoiceID))
					continue;

				salesIds.add(invoiceID);
				Invoice invoice = getInvoice(InvoiceType.SALES, status, invoiceID);
				onInvoiceListener.onInvoice(invoice);
			}
		}
	}

	private ArrayList<String> getIds(InvoiceType type, InvoiceStatus status, int n_months) {
		ArrayList<String> ids = new ArrayList<>();
		// For each month in last n months
		for(int m=n_months; m>0; m--){
			LocalDate dateStart = LocalDate.now().minusMonths(m);
			LocalDate dateEnd = LocalDate.now().minusMonths(m-1);

			// Get list of ids for each status
			ArrayList<String> tmp = getIdsByDate(type, status, dateStart, dateEnd);
			ids.addAll(tmp);
		}

		return ids;
	}

	private ArrayList<String> getIdsByDate(InvoiceType type, InvoiceStatus status, LocalDate dateFrom, LocalDate dateTo){
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String fromDateStr = "dateFrom=" + format.format(dateFrom);
		String toDateStr = "dateTo=" + format.format(dateTo);

		String invoiceIdsURI;
		if(type == InvoiceType.PURCHASE) {
			invoiceIdsURI = efakturaURI + "purchase-invoice/ids?status=" + status + "&" + fromDateStr + "&" + toDateStr;
		}else {
			invoiceIdsURI = efakturaURI + "sales-invoice/ids?status=" + status + "&" + fromDateStr + "&" + toDateStr;
		}

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

		HttpResponse<String> response = sendRequest(getIDRequest);

		return parseIdsFromResponse(type, response);
	}

	private ArrayList<String> parseIdsFromResponse(InvoiceType type, HttpResponse<String> response){
		ArrayList<String> ids = new ArrayList<>();

		JSONObject object = new JSONObject(response.body());
		JSONArray invoiceIds;

		if(type == InvoiceType.SALES)
			invoiceIds = object.getJSONArray("SalesInvoiceIds");
		else
			invoiceIds = object.getJSONArray("PurchaseInvoiceIds");

		invoiceIds.toList().forEach((id) -> {
			ids.add(id.toString());
		});

		return ids;
	}

	private Invoice getInvoice(InvoiceType type, InvoiceStatus status, String invoiceId) {
		String invoiceXmlURI;

		if(type == InvoiceType.PURCHASE) {
			invoiceXmlURI = efakturaURI + "purchase-invoice/xml" + "?invoiceId=" + invoiceId;
		}else {
			invoiceXmlURI = efakturaURI + "sales-invoice/xml" + "?invoiceId=" + invoiceId;
		}

		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.header("ApiKey", this.API_KEY)
				.header("accept", "*/*")
				.uri(URI.create(invoiceXmlURI))
				.build();

		HttpResponse<String> res = sendRequest(request);

		Invoice invoice = new Invoice(invoiceId, type, status, res.body());
		
		System.out.println("[Status: "+invoice.status+"] getInvoice("+invoiceId+") : " + PrintColor.MAGENTA +res.statusCode() + PrintColor.RESET);

		return invoice;
	}

	private synchronized HttpResponse<String> sendRequest(HttpRequest request) {
		try {
			HttpClient client = HttpClient.newHttpClient();
			// GetInvoiceRequest
			Thread.sleep(1000);

            return client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
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

	public interface OnInvoiceListener{
		 void onInvoice(Invoice invoice);
	}
}
