package com.paybot.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName; 

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits")
    private String phone;

    // No-Args Constructor
    public RegisterRequest() {}

    // All-Args Constructor
public RegisterRequest(String fullName, String email, String password, String phone) {
        // 🟢 Terminal par aane wali values ko print karne ke liye:
        System.out.println("========== FRONTEND DATA RECEIVED ==========");
        System.out.println("Full Name : " + fullName);
        System.out.println("Email     : " + email);
        System.out.println("Password  : " + password);
        System.out.println("Phone     : " + phone);
        System.out.println("=============================================");

        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    // 🟢 KEY FIX: Explicitly binding Jackson property to getter and setter
    @JsonProperty("fullname")
    public String getFullName() { return fullName; }
    
    @JsonProperty("fullname")
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}