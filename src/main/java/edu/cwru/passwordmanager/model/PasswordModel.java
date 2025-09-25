package edu.cwru.passwordmanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//yavajava
import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;


public class PasswordModel {
    private ObservableList<Password> passwords = FXCollections.observableArrayList();

    // !!! DO NOT CHANGE - VERY IMPORTANT FOR GRADING !!!
    static private File passwordFile = new File("passwords.txt");

    static private String separator = "\t";

    static public String passwordFilePassword = "";
    static public byte [] passwordFileKey;
    static public byte [] passwordFileSalt;
    

    // TODO: You can set this to whatever you like to verify that the password the user entered is correct
    private static String verifyString = "abracadabra";

    public void loadPasswords() {
        // TODO: Replace with loading passwords from file, you will want to add them to the passwords list defined above
        // TODO: Tips: Use buffered reader, make sure you split on separator, make sure you decrypt password

        String filePath = "passwords.txt"; 

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            line = br.readLine(); // skip first line
            System.out.println(line);
            String[] pp = line.split(separator);


            while ((line = br.readLine()) != null) {
                System.out.println(line);
                // Trim spaces and handle both space and comma separators
                line = line.trim();
                
                // Split by whitespace or comma
                String[] parts = line.split(separator);
          

                if (parts.length >= 2) {
                    String first = parts[0];
                    String second = parts[1];
              

        
                } else {
                    System.out.println("Skipping malformed line: " + line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PasswordModel() {
        loadPasswords();
    }

    static public boolean passwordFileExists() {
        return passwordFile.exists();
    }

    static public void initializePasswordFile(String password) throws IOException {
        passwordFile.createNewFile();

        // TODO: Use password to create token and save in file with salt (TIP: Save these just like you would save password)
        //set password
        passwordFilePassword = password; 

        //generate salt for application
        String hold = generateSalt();

        //generate key for application
        SecretKeySpec key = generateKey(passwordFilePassword, passwordFileSalt);
        
        try{  //encrypt verify string

            String encodedToken = encryptPassword(verifyString);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(passwordFile))) {
                //add salt and token to password file
                writer.write(hold + separator + encodedToken);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public boolean verifyPassword(String password) {
        passwordFilePassword = password; // DO NOT CHANGE

        try (BufferedReader br = new BufferedReader(new FileReader("passwords.txt"))) {
            //wehn password file exist, read first line to get salt and encrypted token
            String line;
            line = br.readLine(); 
            String[] pp = line.split(separator);
            passwordFileSalt = Base64.getDecoder().decode(pp[0]);
            String tokenString = pp[1];

            //create key using user input password and salt stored in file
            SecretKeySpec key = generateKey(passwordFilePassword, passwordFileSalt);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedToken = Base64.getDecoder().decode(tokenString.getBytes());
            byte[] decryptedToken = cipher.doFinal(decodedToken);
            if(new String(decryptedToken).equals(verifyString)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
        // TODO: Check first line and use salt to verify that you can decrypt the token using the password from the user
        // TODO: TIP !!! If you get an exception trying to decrypt, that also means they have the wrong passcode, return false!

    
    }

    public ObservableList<Password> getPasswords() {
        return passwords;
    }

    public void deletePassword(int index) {
        passwords.remove(index);

        // TODO: Remove it from file
    }

    public void updatePassword(Password password, int index) {
        passwords.set(index, password);

        // TODO: Update the file with the new password information
    }

    public void addPassword(Password password) {
        passwords.add(password);
        String hold = password.getLabel();
        String pass = password.getPassword();
        SecretKeySpec key = new SecretKeySpec(passwordFileKey, "AES");
        try{  
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedToken = cipher.doFinal(pass.getBytes());
            String encodedToken = Base64.getEncoder().encodeToString(encryptedToken);
            BufferedWriter writer = new BufferedWriter(new FileWriter(passwordFile,true));
            writer.write(hold + separator + encodedToken);
            writer.newLine();
            writer.close();
        } catch (Exception e) {
                e.printStackTrace();
        }

        // TODO: Add the new password to the file
    }
    
    // TODO: Tip: Break down each piece into individual methods, for example: generateSalt(), encryptPassword, generateKey(), saveFile, etc ...
    // TODO: Use these functions above, and it will make it easier! Once you know encryption, decryption, etc works, you just need to tie them in
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        passwordFileSalt = new byte[32];
        random.nextBytes(passwordFileSalt);
        return Base64.getEncoder().encodeToString(passwordFileSalt);
    }

    public static SecretKeySpec generateKey(String password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            passwordFileKey = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(passwordFileKey, "AES");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String encryptPassword(String s){
        try (BufferedReader br = new BufferedReader(new FileReader("passwords.txt"))) {
            SecretKeySpec key = new SecretKeySpec(passwordFileKey, "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedPassword = cipher.doFinal(s.getBytes());

            System.out.println(Base64.getEncoder().encodeToString(encryptedPassword));

            return Base64.getEncoder().encodeToString(encryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
