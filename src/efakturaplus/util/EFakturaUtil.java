package efakturaplus.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Flow.Subscriber;

import org.json.JSONArray;
import org.json.JSONObject;

import efakturaplus.models.Invoice;
import efakturaplus.models.InvoiceStatus;
import efakturaplus.models.User;

public class EFakturaUtil {	
	//Singleton instance
	private static EFakturaUtil instance = null;
	
	private String API_KEY = "";
	
	// URI for an invoice [ xml ]
	// QUERY: ?invoiceId=SOME_ID (19150969)
	private final String getInvoiceURI = "https://efaktura.mfin.gov.rs/api/publicApi/purchase-invoice/xml";
	
	// URI for a list of ids [ json ]
	private final String getInvoiceIDsURI = "https://efaktura.mfin.gov.rs/api/publicApi/purchase-invoice/ids?status=";

	private EFakturaUtil(String API_KEY) {
		this.API_KEY = API_KEY;
	}
	
	public static EFakturaUtil getInstance() {
		if(instance == null) {
			instance = new EFakturaUtil(User.API_KEY);
		}
		
		return instance;
	}
	
	private ArrayList<String> getIdsFromResponse(HttpResponse<String> response){
		ArrayList<String> ids = new ArrayList<String>();
		
		JSONObject object = new JSONObject(response.body());
		JSONArray purchaseInvoiceIds = object.getJSONArray("PurchaseInvoiceIds");
		
		purchaseInvoiceIds.toList().forEach((id) -> ids.add(id.toString()));;
		return ids;
	}
	
	private HttpResponse<String> sendRequest(HttpRequest request) {
		HttpClient client = HttpClient.newHttpClient();
		try {
			// GetInvoiceRequest
			HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			return res;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Invoice getInvoice(String invoiceId) {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.header("ApiKey", this.API_KEY)
				.header("accept", "*/*")
				.uri(URI.create(getInvoiceURI+"?invoiceId="+invoiceId))
				.build();
		
		HttpResponse<String> res = sendRequest(request);
		
		Invoice invoice = new Invoice(invoiceId, res.body());
		
		System.out.println("[Status] getInvoice("+invoiceId+") : " + Color.MAGENTA +res.statusCode() + Color.RESET);
		
		return invoice;
	}
	
	public ArrayList<String> getIdsList(InvoiceStatus status) {
		HttpRequest getIDRequest = HttpRequest.newBuilder()
				.POST(new BodyPublisher() {
					public void subscribe(Subscriber<? super ByteBuffer> subscriber) {
						// TODO Auto-generated method stub
					}
					public long contentLength() {
						// TODO Auto-generated method stub
						return 0;
					}
				})
				.header("ApiKey", this.API_KEY)
				.header("accept", "text/plain")
				.uri(URI.create(getInvoiceIDsURI+status))
				.build();
		
		HttpResponse<String> res = sendRequest(getIDRequest);
		
		ArrayList<String> ids = getIdsFromResponse(res);
		
		System.out.println(ids);
		System.out.println("[Status] getIdsList() : " + Color.MAGENTA + res.statusCode() + Color.RESET);
		
		return ids;
	}
	
	public ArrayList<Invoice> getInvoices(InvoiceStatus status){
		ArrayList<Invoice> list = new ArrayList<Invoice>();
		
		ArrayList<String> ids = getIdsList(status);
		
		for(String id: ids) {
			list.add(this.getInvoice(id));
		}
		
		return list;
	}
}
