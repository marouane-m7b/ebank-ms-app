package net.mahboub.ebankservice.services;

import net.mahboub.ebankservice.entities.BankAccount;
import net.mahboub.ebankservice.feign.CustomerRestClient;
import net.mahboub.ebankservice.repository.BankAccountRepository;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EbankService {
    private BankAccountRepository accountRepository;
    private CustomerRestClient customerRestClient;

    public EbankService(BankAccountRepository accountRepository, CustomerRestClient customerRestClient) {
        this.accountRepository = accountRepository;
        this.customerRestClient = customerRestClient;
    }
    @McpTool(description = "Get All Bank accounts")
    public List<BankAccount> getAllBankAccounts() {
        return accountRepository.findAll();
    }

    @McpTool(description = "Get A Bank account by id")
    public BankAccount getBankAccountById(@McpToolParam(description = "The bank account id") String id) {
        BankAccount bankAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        bankAccount.setCustomer(customerRestClient.getCustomerById(bankAccount.getCustomerId()));
        return bankAccount;
    }

    @McpTool(description = "Save a new Bank account")
    public BankAccount saveBankAccount(@McpToolParam(description = "The bank account to save (balance, type, customerId)") BankAccount bankAccount) {
        try {
            customerRestClient.getCustomerById(bankAccount.getCustomerId());
            bankAccount.setId(UUID.randomUUID().toString());
            bankAccount.setCreatedAt(new Date());
            return accountRepository.save(bankAccount);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
