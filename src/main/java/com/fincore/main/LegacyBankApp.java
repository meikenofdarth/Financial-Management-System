package com.fincore.main;

import com.fincore.model.Account;
import com.fincore.model.Customer;
import com.fincore.model.Loan;
import com.fincore.model.Transaction;
import com.fincore.repository.DataStore;
import com.fincore.service.AccountService;
import com.fincore.service.LoanService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * The Entry Point of the FinCore Banking System.
 * Provides a Command Line Interface (CLI) for users to interact with the system.
 */
public class LegacyBankApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AccountService accountService = new AccountService();
    private static final LoanService loanService = new LoanService();
    private static final DataStore dataStore = DataStore.getInstance();

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   WELCOME TO FINCORE BANKING SYSTEM     ");
        System.out.println("=========================================");

        boolean running = true;
        while (running) {
            try {
                printMenu();
                System.out.print("Enter your choice: ");
                String input = scanner.nextLine();
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1:
                        handleRegisterCustomer();
                        break;
                    case 2:
                        handleOpenAccount();
                        break;
                    case 3:
                        handleDeposit();
                        break;
                    case 4:
                        handleWithdraw();
                        break;
                    case 5:
                        handleTransfer();
                        break;
                    case 6:
                        handleLoanApplication();
                        break;
                    case 7:
                        handleShowMyDetails();
                        break;
                    case 8:
                        handleShowAllCustomers(); // Admin feature
                        break;
                    case 9:
                        System.out.println("Exiting System. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println("-----------------------------------------");
        }
    }

    private static void printMenu() {
        System.out.println("\nMAIN MENU:");
        System.out.println("1. Register New Customer");
        System.out.println("2. Open New Account");
        System.out.println("3. Deposit Money");
        System.out.println("4. Withdraw Money");
        System.out.println("5. Transfer Funds");
        System.out.println("6. Apply for Loan");
        System.out.println("7. View Customer Dashboard");
        System.out.println("8. List All Customers (Admin)");
        System.out.println("9. Exit");
    }

    private static void handleRegisterCustomer() {
        System.out.println("\n--- Register Customer ---");
        System.out.print("First Name: ");
        String first = scanner.nextLine();
        System.out.print("Last Name: ");
        String last = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        
        System.out.print("Credit Score (300-850): ");
        int score = Integer.parseInt(scanner.nextLine());
        System.out.print("Yearly Income: ");
        double income = Double.parseDouble(scanner.nextLine());

        String id = "CUST-" + UUID.randomUUID().toString().substring(0, 8);
        try {
            Customer c = accountService.registerCustomer(id, first, last, email, pass, score, income);
            System.out.println("Success! Customer ID: " + c.getCustomerId());
        } catch (IllegalArgumentException e) {
            System.out.println("Registration Failed: " + e.getMessage());
        }
    }

    private static void handleOpenAccount() {
        System.out.println("\n--- Open Account ---");
        System.out.print("Enter Customer ID: ");
        String custId = scanner.nextLine();
        System.out.print("Account Type (SAVINGS/CURRENT): ");
        String type = scanner.nextLine();
        System.out.print("Initial Deposit: ");
        double amount = Double.parseDouble(scanner.nextLine());

        String accId = "ACC-" + UUID.randomUUID().toString().substring(0, 8);
        try {
            Account acc = accountService.createAccount(type, accId, custId, amount);
            System.out.println("Account Created! Account ID: " + acc.getAccountId());
        } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    private static void handleDeposit() {
        System.out.print("Enter Account ID: ");
        String accId = scanner.nextLine();
        System.out.print("Amount to Deposit: ");
        double amount = Double.parseDouble(scanner.nextLine());
        
        accountService.deposit(accId, amount);
        System.out.println("Deposit Successful.");
    }

    private static void handleWithdraw() {
        System.out.print("Enter Account ID: ");
        String accId = scanner.nextLine();
        System.out.print("Amount to Withdraw: ");
        double amount = Double.parseDouble(scanner.nextLine());
        
        accountService.withdraw(accId, amount);
        System.out.println("Withdrawal Successful.");
    }

    private static void handleTransfer() {
        System.out.println("\n--- Fund Transfer ---");
        System.out.print("From Account ID: ");
        String from = scanner.nextLine();
        System.out.print("To Account ID: ");
        String to = scanner.nextLine();
        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        accountService.transfer(from, to, amount);
        System.out.println("Transfer Complete.");
    }

    private static void handleLoanApplication() {
        System.out.println("\n--- Loan Application ---");
        System.out.print("Customer ID: ");
        String custId = scanner.nextLine();
        System.out.print("Loan Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Tenure (Months): ");
        int months = Integer.parseInt(scanner.nextLine());

        String loanId = "LOAN-" + UUID.randomUUID().toString().substring(0, 8);
        try {
            Loan loan = loanService.applyForLoan(loanId, custId, amount, months);
            System.out.println("Loan Application Approved!");
            System.out.println("Loan ID: " + loan.getLoanId());
            System.out.println("Interest Rate: " + loan.getInterestRate() + "%");
        } catch (Exception e) {
            System.out.println("Loan Rejected: " + e.getMessage());
        }
    }

    private static void handleShowMyDetails() {
        System.out.print("Enter Customer ID: ");
        String custId = scanner.nextLine();
        Customer c = dataStore.getCustomer(custId);
        
        if (c != null) {
            System.out.println("\n*** Customer Profile ***");
            System.out.println(c);
            
            List<Account> accounts = dataStore.getAccountsForCustomer(custId);
            System.out.println("\n[Accounts]: " + accounts.size());
            for (Account acc : accounts) {
                System.out.println(" - " + acc.toString());
                for (Transaction t : acc.getTransactionHistory()) {
                    System.out.println("    -> " + t.toString());
                }
            }

            List<Loan> loans = dataStore.getLoansForCustomer(custId);
            System.out.println("\n[Loans]: " + loans.size());
            for (Loan l : loans) {
                System.out.println(" - " + l.toString());
            }
        } else {
            System.out.println("Customer not found.");
        }
    }
    
    // Admin helper to see data during manual testing
    private static void handleShowAllCustomers() {
        System.out.println("\n--- All Registered Customers ---");
        List<Customer> list = dataStore.getAllCustomers();
        for(Customer c : list) 
            System.out.println(c.getCustomerId() + " : " + c.getFullName());
    }
}