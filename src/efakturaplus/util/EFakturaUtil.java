package efakturaplus.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Flow.Subscriber;

import org.json.JSONArray;
import org.json.JSONObject;

public class EFakturaUtil {	
	// TEST KEY
	// 
	private String API_KEY = "";
	
	// TESTING URI for an invoice [ xml ]
	private final String getInvoiceURI = "https://efaktura.mfin.gov.rs/api/publicApi/purchase-invoice/xml?invoiceId=19150969";
	
	// TESTING URI for a list of ids [ json ]
	private final String getInvoiceIDsURI = "https://efaktura.mfin.gov.rs/api/publicApi/purchase-invoice/ids?status=Approved";
	
	private HttpRequest GetIDRequest;
	private HttpRequest GetInvoiceRequest;
	
	public EFakturaUtil() {
		if(this.API_KEY == "") {
			Scanner sc = new Scanner(System.in);
			
			System.out.println("Please enter your API key:");
			
			this.API_KEY = sc.next();
			
			sc.close();
		}
		
		GetIDRequest = HttpRequest.newBuilder()
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
				.uri(URI.create(getInvoiceIDsURI))
				.build();
		
		GetInvoiceRequest = HttpRequest.newBuilder()
				.GET()
				.header("ApiKey", this.API_KEY)
				.header("accept", "*/*")
				.uri(URI.create(getInvoiceURI))
				.build();
	}
	
	private ArrayList<Integer> getIdsFromResponse(HttpResponse<String> response){
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		JSONObject object = new JSONObject(response.body());
		JSONArray purchaseInvoiceIds = object.getJSONArray("PurchaseInvoiceIds");
		
		purchaseInvoiceIds.toList().forEach((id) -> ids.add(Integer.parseInt(id.toString())));;
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
	
	public void getInvoiceExample() {
		HttpResponse<String> res = sendRequest(GetInvoiceRequest);
		
		System.out.println(res.body());
		System.out.println(res.statusCode());
	}
	
	public void getIdsList() {
		HttpResponse<String> res = sendRequest(GetIDRequest);
		
		System.out.println(getIdsFromResponse(res));
		System.out.println(res.statusCode());
	}
}
