package com.ecommerce.springboot.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sellerId;

    @NotNull(message="Please enter the first name")
	@Pattern(regexp="[A-Za-z\\s]+", message="First Name should contains alphabets only")
    private String firstName;


    @NotNull(message="Please enter the first name")
	@Pattern(regexp="[A-Za-z\\s]+", message="First Name should contains alphabets only")
    private String lastName;

    @Pattern(regexp="[A-Za-z0-9!@#$%^&*_]{8,15}", message="Please Enter a valid Password")
    private String password;

    @Pattern(regexp = "[6789][0-9]{10}",message = "Please enter valid mobile number")
    @Column(unique = true)
    private String mobile;

    @Email
    @Column(unique = true)
    private String emailId;

    @OneToMany
    @JsonIgnore
    private List<Product> product;

}
