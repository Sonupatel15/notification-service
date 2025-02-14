package com.example.user_service.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String name;
    // Add other fields as needed, but avoid including sensitive data like IDs or passwords directly.
    //  For example, you might include a simplified address, or a list of roles.

    // Example:
    // private String city;
    // private List<String> roles;


    // It's generally a good practice to create separate DTOs for different operations
    // (e.g., UserCreateDTO, UserUpdateDTO) if the fields required for those operations
    // are different.

    // Example for UserCreateDTO:
    // @Data
    // public class UserCreateDTO {
    //     @NotBlank(message = "Email is required") // Example validation
    //     @Email(message = "Invalid email format")
    //     private String email;
    //     @NotBlank(message = "Name is required")
    //     private String name;
    //     // ... other fields needed for user creation
    // }

    // Example for UserResponseDTO (if you want a different response structure):
    // @Data
    // public class UserResponseDTO {
    //     private Long id;  // Include ID in the response
    //     private String email;
    //     private String name;
    //     // ... other fields
    // }
}