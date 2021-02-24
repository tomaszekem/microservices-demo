package com.tomaszekem.userservice.user;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "user")
@Where(clause = "deleted <> 1")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean deleted;


    public UserEntity(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UserEntity() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void markAsDeleted() {
        deleted = true;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void update(UpdateUserCommand updateUserCommand) {
        email = updateUserCommand.getEmail();
        firstName = updateUserCommand.getFirstName();
        lastName = updateUserCommand.getLastName();
    }

    public boolean isDeleted() {
        return deleted;
    }
}
