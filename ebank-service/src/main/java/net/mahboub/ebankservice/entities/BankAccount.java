package net.mahboub.ebankservice.entities;

import jakarta.persistence.*;
import lombok.*;
import net.mahboub.ebankservice.model.Customer;

import java.util.Date;

@Entity @Setter @Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private Date createdAt;
    private double balance;
    private String type;
    private long customerId;
    @Transient
    private Customer customer;
}
