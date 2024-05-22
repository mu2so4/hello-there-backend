package ru.nsu.ccfit.muratov.hello.there.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String name;
}
