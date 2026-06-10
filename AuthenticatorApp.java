package com.mycompany.authenticatorapp;

import java.util.Scanner;

public class AuthenticatorApp {
    public static void main(String[] args) {    
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter your username to register :");
        String username = scanner.next();
        while (!valUserName(username)) {
            System.out.println("Invalid username. Please try again.");
            username = scanner.next();
        }//code for registering

        System.out.println("Please enter your password :");
        String password = scanner.next();
        while (!valPassword(password)) {
            System.out.println("Invalid password. Please try again.");
            password = scanner.next();
        }//code for creating a password

        System.out.println("Please enter your first name :");
        String firstName = scanner.next();

        System.out.println(firstName + " is now successfully registered");

        System.out.println("In order to continue please enter your credentials");
        System.out.println("----------------------------------------");

        System.out.println("Please enter your username :");
        String userNameLogin = scanner.next();
        System.out.println("Please enter your password :");
        String passwordLogin = scanner.next();

         System.out.println("Please enter your phone number :");
        String phoneNumber = scanner.next();
        while (!valPhoneNumber(phoneNumber)) {
            System.out.println("Invalid South African phone number. Please try again.");
            phoneNumber = scanner.next();
        }
        
        if (username.equals(userNameLogin) && password.equals(passwordLogin)) {
            System.out.println("Login successful!");

            // ========= PART 2 INTEGRATION =========
            // Consume the leftover newline from scanner.next() above so that
            // QuickChat's scanner.nextLine() calls work correctly.
            scanner.nextLine();

            // Hand off control to the QuickChat menu
            Quickchat.run(scanner);
            // ======================================

        } else {
            System.out.println("Invalid username or password");
        }
    }//code for logging in

    public static boolean valPassword(String password) {
        if (password.length() >= 8) {
            return checkPassword(password);
        } else {
            System.out.println("Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
            return false;
        }
    }//output for not meeting password requirements

    public static boolean checkPassword(String password) {
        boolean hasNum = false;
        boolean hasCap = false;
        boolean hasLow = false;
        boolean hasSpecial = false;
        char c;
        for (int i = 0; i < password.length(); i++) {
            c = password.charAt(i);
            if (Character.isDigit(c)) {
                hasNum = true;
            } else if (Character.isUpperCase(c)) {
                hasCap = true;
            } else if (Character.isLowerCase(c)) {
                hasLow = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }
        return hasNum && hasCap && hasLow && hasSpecial;
    }//code for password complexity

    public static boolean valUserName(String username) {
        if (username.length() >= 8 && username.contains("_")) {
            return true;
        } else {
            System.out.println("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than 8 characters");
            return false;
        }
    }//password for username complexity

    public static boolean valPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 10) {
            return checkPhoneNumber(phoneNumber);
        } else {
            System.out.println("The phone number is invalid");
            return false;
        }
    }//code for entering phone number

  public static boolean checkPhoneNumber(String phoneNumber) {
    return phoneNumber.matches("\\d+");
}

    public static boolean checkUserName(String username) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }//code checking for a valid phone number

}