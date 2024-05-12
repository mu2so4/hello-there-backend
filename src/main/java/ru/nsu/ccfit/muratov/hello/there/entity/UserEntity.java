package ru.nsu.ccfit.muratov.hello.there.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@Entity(name = "Users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(unique = true)
    private String username;
    private String password;

    private String firstName;
    private String lastName;
    private Date registrationTime;
    private Date birthday;
}
