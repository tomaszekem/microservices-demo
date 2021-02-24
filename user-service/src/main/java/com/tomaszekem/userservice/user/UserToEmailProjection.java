package com.tomaszekem.userservice.user;

class UserToEmailProjection {
    private final Long userId;
    private final String userEmail;

    public UserToEmailProjection(Long userId, String userEmail) {
        this.userId = userId;
        this.userEmail = userEmail;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
