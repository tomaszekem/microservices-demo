package com.tomaszekem.userservice.user;

class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    public static UserDTO fromEntity(UserEntity userEntity) {
        var dto = new UserDTO();
        dto.id = userEntity.getId();
        dto.firstName = userEntity.getFirstName();
        dto.lastName = userEntity.getLastName();
        dto.email = userEntity.getEmail();

        return dto;
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
}
