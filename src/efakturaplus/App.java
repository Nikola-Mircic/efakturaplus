package efakturaplus;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

import efakturaplus.gui.Window;
import efakturaplus.models.User;

public class App {

	public static void main(String[] args) {
		try {	
			File f = new File("user.enc");
			
			if(f.exists()) {
				FileInputStream fis = new FileInputStream(f);
				BufferedReader br = new BufferedReader(new FileReader(f));
				StringBuilder sb = new StringBuilder();
				String line;
				while((line = br.readLine()) == null) {
					sb.append(line);
				}
				
				User.API_KEY = sb.toString();
			}
			
		}catch(Exception e){
			System.out.println("Error reading a file!");
		};
		
		@SuppressWarnings("unused")
		Window w = new Window();
	}

}
