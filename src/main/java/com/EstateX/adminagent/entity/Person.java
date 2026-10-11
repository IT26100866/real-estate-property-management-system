package com.EstateX.adminagent.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Parent class for Admin and Agent (INHERITANCE + ABSTRACTION).
 *
 * It is mapped to the team's shared "users" table.
 * SINGLE_TABLE + the "role" column decides the child class:
 *      role = 'ADMIN'  -> Admin
 *      role = 'AGENT'  -> Agent
 * (CUSTOMER rows belong to the User Management member.)
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class Person {

    // ENCAPSULATION: every field is private, access only through getters/setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // username and email are already UNIQUE in the users table
    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;            // always stored as a BCrypt hash

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String address;

    // new column (Admin/Agent management): ACTIVE / INACTIVE / SUSPENDED
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;    // filled by the database

    protected Person() { }              // required by JPA

    protected Person(String username, String fullName, String email, String phone,
                     String address, String password, AccountStatus status) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.status = (status == null) ? AccountStatus.ACTIVE : status;
    }

    // POLYMORPHISM: Admin and Agent each give their own answer
    public abstract String getType();

    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
