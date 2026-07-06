package SRT.test.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @Column(name = "user_name")
    private String userName;

    @Column(name = "account_name", unique = true)
    private String accountName;

    @Column(name = "fullName")
    private String fullName;

    @Column(name = "passwordHash")
    private String passwordHash;

    @Column(name = "email")
    private String email;

    @Column(name = "createdDate")
    private Date createdDate;

    @Column(name = "updatedDate")
    private Date updatedDate;

    @PrePersist
    public void generateIdIfAbsent() {
        if (this.userName == null) {
            this.userName = UUID.randomUUID().toString();
        }
    }
}
