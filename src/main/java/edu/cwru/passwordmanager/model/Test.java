package edu.cwru.passwordmanager.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

public class Test {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("passwords.txt",true))) {
                //add salt and token to password file
                writer.newLine();
                writer.write("no");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

        try {
            //PasswordModel.initializePasswordFile("hii");
        } catch (Exception e) {
            System.out.println("Failed to initialize password file");
         //   e.printStackTrace();
          
        }
        



    }
}
