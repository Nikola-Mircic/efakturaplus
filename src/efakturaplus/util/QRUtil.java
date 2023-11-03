package efakturaplus.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.Flow.Subscriber;

import javax.imageio.ImageIO;

import org.json.JSONObject;

import ch.randelshofer.util.ArrayUtil;
import efakturaplus.models.Invoice;

public class QRUtil {
	
	public QRUtil() {
		
	}

	public BufferedImage getQRCode(Invoice inv) {
		//byte[] attachmentBytes = 
		String invString = invoiceToString(inv);
		
		byte[] img = getQRCodeBytes(invString);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(img);
		
		try {
			return ImageIO.read(bis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private byte[] getQRCodeBytes(String reqBody) {
		HttpRequest request = HttpRequest.newBuilder()
				.POST(BodyPublishers.ofString(reqBody))
				.uri(URI.create("https://nbs.rs/QRcode/api/qr/v1/generate"))
				.build();
		
		HttpClient client = HttpClient.newHttpClient();
		try {
			// GetInvoiceRequest
			HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			JSONObject object = new JSONObject(res.body());
			
			System.out.println(res.body());
		
			String encodedImg = object.getString("i");
			
			return Base64.getDecoder().decode(encodedImg.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String invoiceToString(Invoice inv) {
		ArrayList<String> s = new ArrayList<String>();
		
		s.add("K:PR");
		s.add("V:01");
		s.add("C:1");
		StringBuffer buff = new StringBuffer(inv.payeeFinancialAccs.get(0));
		while(buff.length() < 20) {
			buff.insert(4, "0");
		}
		
		s.add("R:"+buff.toString().replace("-", ""));
		
		String supplierData = inv.supplier.getQRCodeData();
		if(supplierData.length() > 70) {
			supplierData = supplierData.substring(0, 70);
		}
		
		s.add("N:"+supplierData);
		
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		
		DecimalFormat df = new DecimalFormat("#######.00", symbols);
		
		s.add("I:RSD"+df.format(inv.payableAmount));
		s.add("SF:289");
		
		if(inv.paymentId != null) {
			String ro = "RO:";
			
			if(inv.paymentMod == null || inv.paymentMod.equals("")) {
				ro+="00";
			}else {
				ro+=inv.paymentMod;
			}
			
			ro+=inv.paymentId;
		}
		
		return String.join("|", s);
	}
}
