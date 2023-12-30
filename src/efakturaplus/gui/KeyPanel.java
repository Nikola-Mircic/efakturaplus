package efakturaplus.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.*;
import javax.crypto.spec.*;

import sun.misc.*;

import java.util.Arrays;
import java.util.Base64;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ch.randelshofer.util.ArrayUtil;
import efakturaplus.models.User;

public class KeyPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private String password;
	private byte[] iv;
	
	JLabel keyLabel, passLabel, passLabel2;
	JTextField keyInput;
	JPasswordField passInput, passInput2;

	private Window parent;
	
	private boolean registeredUser;

	public KeyPanel(Window parent, int width, int height) {
		this.parent = parent;
		this.setSize(width, height);
		this.setLayout(null);
		
		addComponents(width, height);
	}

	private void addComponents(int width, int height) {
		
		Font font = new Font("Arial", Font.PLAIN, 20);
		
		passLabel = new JLabel("Please enter your password here:");
		passLabel.setFont(font);
		passLabel.setForeground(Color.black);
		
		passInput = new JPasswordField();
		addBorder(passInput, Color.black);
		passInput.setFont(font);
		
		passInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				submitData();
			}
		});
		
		File userData = new File("user.enc");
		
		if(!userData.exists()) {
			keyLabel = new JLabel("Please enter your API key here:");
			keyLabel.setFont(font);
			keyLabel.setForeground(Color.black);
			keyLabel.setBounds(width/2-150, height/4-75, 350, 50);
			
			keyInput = new JTextField();
			keyInput.setBounds(width/2-150, height/4-25, 300, 60);
			addBorder(keyInput, Color.black);
			keyInput.setFont(font);
			
			keyInput.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					submitData();
				}
			});
			
			passLabel.setText("Please enter new password:");
			passLabel.setBounds(width/2-150, height/2-75, 350, 50);
			passInput.setBounds(width/2-150, height/2-25, 300, 60);
			
			passLabel2 = new JLabel("Please enter your password again:");
			passLabel2.setFont(font);
			passLabel2.setForeground(Color.black);
			passLabel2.setBounds(width/2-150, height*3/4-75, 350, 50);
			
			
			passInput2 = new JPasswordField();
			addBorder(passInput2, Color.black);
			passInput2.setFont(font);
			passInput2.setBounds(width/2-150, height*3/4-25, 300, 60);
			
			passInput2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					submitData();
				}
			});
			
			this.add(keyLabel);
			this.add(keyInput);
			this.add(passLabel);
			this.add(passInput);
			this.add(passLabel2);
			this.add(passInput2);
		}else {
			passLabel.setBounds(width/2-150, height/2-75, 350, 50);
			passInput.setBounds(width/2-150, height/2-25, 300, 60);
			this.add(passLabel);
			this.add(passInput);
		}
	}
	
	private void addBorder(JComponent comp, Color c) {
		comp.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(c, 3, true),
		        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
	}
	
	private void submitData() {
		File userData = new File("user.enc");
		
		if(!userData.exists()) {
			User.API_KEY = keyInput.getText();
			
			try {
				encryptData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			addComponents(getWidth(), getHeight());
		}
		else {
			try {
				String data = decryptData();
				System.out.println("Decrypted: "+data);
				
				User.API_KEY = data;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		parent.showMainPanel();
	}
	
	private void encryptData() throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(passInput.getPassword(), salt, 65536, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			byte[] ciphertext = cipher.doFinal(keyInput.getText().getBytes());
			
			System.out.println("IV length: "+iv.length);
			System.out.println("Cipher text length: "+ciphertext.length);
			
			FileOutputStream fos = new FileOutputStream("user.enc");
			
			fos.write(iv);
			fos.write(salt);
			fos.write(ciphertext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String decryptData() {
		String content = "";
		
	    try (FileInputStream fileIn = new FileInputStream("user.enc")) {
	        byte[] fileIv = new byte[16];
	        byte[] salt = new byte[8];
	        
	        fileIn.read(fileIv);
	        fileIn.read(salt);
	        
	        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        KeySpec spec = new PBEKeySpec(passInput.getPassword(), salt, 65536, 256);
	        SecretKey tmp = factory.generateSecret(spec);
	        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
	        
	        System.out.println(fileIv.toString());
	        
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(fileIv));
	        
	        CipherInputStream cis = new CipherInputStream(fileIn, cipher);
	        
	        String plaintext = new String(cis.readAllBytes());
	        
	        content = plaintext;

	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return content;
	}
}
