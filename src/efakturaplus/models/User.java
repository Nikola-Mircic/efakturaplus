package efakturaplus.models;

import java.util.ArrayList;

public class User {
	private static User instance;

	public String API_KEY = "";
	public ArrayList<Invoice> purchases;
	public ArrayList<Invoice> sales;

	private User(){
		purchases = new ArrayList<>();
		sales = new ArrayList<>();
	}

	public static User getUser(){
		if(instance == null){
			instance = new User();
		}
		return instance;
	}

	public static void useApiKey(String apiKey){
		User user = getUser();
		user.API_KEY = apiKey;
		user.purchases.clear();
		user.sales.clear();
	}

}
