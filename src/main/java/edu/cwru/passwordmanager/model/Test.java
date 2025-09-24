package edu.cwru.passwordmanager.model;

import java.util.Base64;

public class Test {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        try {
            PasswordModel.initializePasswordFile("hii");
        } catch (Exception e) {
            System.out.println("Failed to initialize password file");
         //   e.printStackTrace();
          
        }
        String s = Base64.getEncoder().encodeToString(PasswordModel.passwordFileSalt);
        System.out.println(s);

        System.out.println(PasswordModel.verifyPassword("brother"));

    }
}
