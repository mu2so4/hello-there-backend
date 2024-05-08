package ru.nsu.ccfit.muratov.hello.there.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
@Entity(name = "Users")
public class User {
    @Id
    @GeneratedValue
    private int userId;

    @Column(unique = true)
    private String username;

    private String firstName;
    private String lastName;
    private Date registrationTime;
    private Date birthday;
}
