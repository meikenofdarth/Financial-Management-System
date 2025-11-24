package com.fincore.controller;

import com.fincore.dto.*;
import com.fincore.model.Account;
import com.fincore.model.Customer;
import com.fincore.model.Loan;
import com.fincore.repository.DataStore;
import com.fincore.service.AccountService;
import com.fincore.service.LoanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class BankController {

    private final AccountService accountService = new AccountService();
    private final LoanService loanService = new LoanService();
    private final DataStore dataStore = DataStore.getInstance();

    // --- HOME & LOGIN ---
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    // FIX 1: Added ("customerId")
    public String processLogin(@RequestParam("customerId") String customerId, Model model) {
        Customer c = dataStore.getCustomer(customerId);
        if (c == null) {
            model.addAttribute("error", "Customer ID not found.");
            return "login";
        }
        return "redirect:/dashboard/" + customerId;
    }

    // --- DASHBOARD ---
    @GetMapping("/dashboard/{customerId}")
    // FIX 2: Added ("customerId")
    public String dashboard(@PathVariable("customerId") String customerId, Model model) {
        Customer c = dataStore.getCustomer(customerId);
        if (c == null) return "redirect:/";

        model.addAttribute("customer", c);
        model.addAttribute("accounts", dataStore.getAccountsForCustomer(customerId));
        model.addAttribute("loans", dataStore.getLoansForCustomer(customerId));
        return "dashboard";
    }

    // --- REGISTRATION ---
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("dto", new CustomerRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("dto") CustomerRegistrationDto dto, Model model) {
        try {
            String id = "CUST-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            accountService.registerCustomer(id, dto.getFirstName(), dto.getLastName(), 
                                          dto.getEmail(), dto.getPassword(), 
                                          dto.getCreditScore(), dto.getYearlyIncome());
            model.addAttribute("success", "Success! Your ID is: " + id + " (Save this!)");
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    // --- OPEN ACCOUNT ---
    @GetMapping("/create-account")
    public String showCreateAccount(Model model) {
        model.addAttribute("dto", new OpenAccountDto());
        return "create-account";
    }

    @PostMapping("/create-account")
    public String processCreateAccount(@ModelAttribute("dto") OpenAccountDto dto, Model model) {
        try {
            String accId = "ACC-" + UUID.randomUUID().toString().substring(0, 8);
            Account acc = accountService.createAccount(dto.getType(), accId, dto.getCustomerId(), dto.getInitialDeposit());
            return "redirect:/dashboard/" + dto.getCustomerId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "create-account";
        }
    }

    // --- TRANSACTIONS ---
    @GetMapping("/transaction")
    public String showTransaction(Model model) {
        model.addAttribute("dto", new TransactionDto());
        return "transaction";
    }

    @PostMapping("/transaction")
    public String processTransaction(@ModelAttribute("dto") TransactionDto dto, Model model) {
        try {
            if ("DEPOSIT".equalsIgnoreCase(dto.getType())) {
                accountService.deposit(dto.getAccountId(), dto.getAmount());
            } else {
                accountService.withdraw(dto.getAccountId(), dto.getAmount());
            }
            Account acc = dataStore.getAccount(dto.getAccountId());
            if (acc != null) return "redirect:/dashboard/" + acc.getCustomerId();
            
            model.addAttribute("success", "Transaction Successful");
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "transaction";
        }
    }

    // --- TRANSFER ---
    @GetMapping("/transfer")
    public String showTransfer(Model model) {
        model.addAttribute("dto", new TransferDto());
        return "transfer";
    }

    @PostMapping("/transfer")
    public String processTransfer(@ModelAttribute("dto") TransferDto dto, Model model) {
        try {
            accountService.transfer(dto.getFromAccountId(), dto.getToAccountId(), dto.getAmount());
            model.addAttribute("success", "Transfer Complete!");
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "transfer";
        }
    }

    // --- LOAN ---
    @GetMapping("/loan")
    public String showLoan(Model model) {
        model.addAttribute("dto", new LoanApplicationDto());
        return "loan";
    }

    @PostMapping("/loan")
    public String processLoan(@ModelAttribute("dto") LoanApplicationDto dto, Model model) {
        try {
            String loanId = "LN-" + UUID.randomUUID().toString().substring(0, 8);
            loanService.applyForLoan(loanId, dto.getCustomerId(), dto.getAmount(), dto.getTenureMonths());
            return "redirect:/dashboard/" + dto.getCustomerId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "loan";
        }
    }
}