package efakturaplus.gui.panels;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.*;

import javax.swing.*;

import efakturaplus.gui.Window;
import efakturaplus.models.User;

public class KeyPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	JLabel keyLabel, passLabel, passLabel2;
	JTextField keyInput;
	JPasswordField passInput, passInput2;

	private efakturaplus.gui.Window parent;
	
	public KeyPanel(Window parent, int width, int height) {
		this.parent = parent;
		this.setSize(width, height);
		this.setLayout(new GridBagLayout());
		
		loadAPIKey();
		addComponents(width, height);
	}
	
	private void loadAPIKey() {
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
				
				User.useApiKey(sb.toString());
				
				br.close();
				fis.close();
			}
			
		}catch(Exception e){
			System.out.println("Error reading a file!");
		};
	}

	private void addComponents(int width, int height) {
		
		Font font = new Font("Arial", Font.PLAIN, 20);
		
		passLabel = new JLabel("Please enter your password here:");
		passLabel.setFont(font);
		
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

		JButton signInBtn = makeButton("Sign in", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				submitData();
			}
		});

		if(!userData.exists()) {
			keyLabel = new JLabel("Please enter your API key here:");
			keyLabel.setFont(font);
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
			
			this.add(keyLabel, gbc(0, 0));
			this.add(keyInput, gbc(0, 1));
			this.add(passLabel, gbc(0, 2));
			this.add(passInput, gbc(0, 3));
			this.add(passLabel2, gbc(0, 4));
			this.add(passInput2, gbc(0, 5));
			this.add(signInBtn, gbc(0, 6));
		}else {
			passLabel.setBounds(width/2-150, height/2-75, 350, 50);
			passInput.setBounds(width/2-150, height/2-25, 300, 60);
			this.add(passLabel, gbc(0, 0));
			this.add(passInput, gbc(0, 1));
			this.add(signInBtn, gbc(0, 2));
		}
	}
	
	private GridBagConstraints gbc(int x, int y) {
		GridBagConstraints constr = new GridBagConstraints();
		
		constr.gridx = x;
		constr.gridy = y;
		
		constr.insets = new Insets(10, 5, 10, 5);
		
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.gridwidth = GridBagConstraints.CENTER;
		return constr;
	}

	private JButton makeButton(String text, ActionListener listener) {
		JButton btn = new JButton();

		btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));

		btn.add(centeredLabel(text));
		btn.addActionListener(listener);

		return btn;
	}

	private JLabel centeredLabel(String text) {
		JLabel label = new JLabel(text);
		label.setAlignmentX(CENTER_ALIGNMENT);
		return label;
	}

	private void addBorder(JComponent comp, Color c) {
		comp.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(c, 3, true),
		        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
	}
	
	private void submitData() {
		File userData = new File("user.enc");
		
		if(!userData.exists()) {
			User.useApiKey(keyInput.getText());

			if(!Arrays.equals(passInput.getPassword(), passInput2.getPassword())){
				addBorder(passInput, Color.RED);
				addBorder(passInput2, Color.RED);

				JOptionPane.showMessageDialog(null, "Passwords do not match!");

				return;
			}

			try {
				encryptData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			addComponents(getWidth(), getHeight());
		}
		else {
			if(!validateInput()){
				addBorder(passInput, Color.RED);
				passInput.setText("");

				JOptionPane.showMessageDialog(null, "Incorrect password!");

				return;
			}

			try {
				String data = decryptData();
				System.out.println("Decrypted: "+data);
				
				User.useApiKey(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		parent.showMainPanel();
	}
	
	private boolean validateInput() {
		try {
			FileInputStream fis = new FileInputStream("pass.enc");

			String password = new String(passInput.getPassword());

			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] passEnc = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));

			byte[] loadedPass = fis.readAllBytes();

			if(Arrays.equals(passEnc, loadedPass)){
				return true;
			}

			fis.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void encryptData() throws NoSuchAlgorithmException, InvalidKeySpecException {
		/* SAVING PASSWORD
		* The password is saved as SHA-256 encoded string
		* */

		String password = passInput.getText();

		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] passEnc = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));

        try {
            FileOutputStream fos = new FileOutputStream("pass.enc");

			fos.write(passEnc);

			fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* SAVING EFAKTURA API KEY
		* The API key is encrypted with AES256 algorithm, and saved alongside with IV
		* */
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
			
			fos.close();
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
	        
	        cis.close();

	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return content;
	}
}
