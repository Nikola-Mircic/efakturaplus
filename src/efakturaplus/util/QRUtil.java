package efakturaplus.util;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.pdfbox.util.StringUtil;
import org.json.JSONObject;

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

			int status = object.getJSONObject("s").getInt("code");

			if(status != 0){
				String title = object.getJSONObject("s").getString("desc");
				List<String> messages = object.getJSONArray("e").toList().stream().map(Object::toString).collect(Collectors.toList());

				String msg = join(messages, "\n");
				
				JOptionPane.showMessageDialog(null,
										"<html><body><p style='width: 300px;'>"+msg+"</p></body></html>",
												title,
												JOptionPane.ERROR_MESSAGE);

				return new byte[0];
			}

			String encodedImg = object.getString("i");
			
			return Base64.getDecoder().decode(encodedImg.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private String join(List<String> list, String delimiter) {
		StringBuilder sb = new StringBuilder();

		for(String s : list) {
			sb.append(s);
		}

		return sb.toString();
	}

	private String removeUselessChars(String str) {
		str = str.trim();
		char[] chars = str.toCharArray();

		for (int i = 0; i < str.length(); i++) {
			if(!Character.isLetterOrDigit( chars[i] ) && chars[i] != '-') {
				chars[i] = '-';
			}
		}

		str = String.valueOf(chars);
		
		return str;
	}

	private String invoiceToString(Invoice inv) {
		ArrayList<String> s = new ArrayList<String>();
		
		s.add("K:PR");
		s.add("V:01");
		s.add("C:1");
		
		inv.payeeFinancialAccs.set(0, inv.payeeFinancialAccs.get(0).replace("-", ""));
		
		StringBuffer buff = new StringBuffer(inv.payeeFinancialAccs.get(0).trim());
		
		while(buff.length() < 18) {
			buff.insert(3, "0");
		}
		
		s.add("R:"+buff.toString());
		
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
			
			if(inv.paymentMod == null || inv.paymentMod.isEmpty()) {
				ro+="00";
			}else {
				ro+=inv.paymentMod;
				inv.paymentId = removeUselessChars(inv.paymentId);
			}

			ro+=inv.paymentId;

			if("97".equals(inv.paymentMod))
				ro = ro.replace("-", "");
			
			s.add(ro);
		}
		
		s.add("S:Transakcije po nalogu građana");
		
		
		return String.join("|", s);
	}
}
