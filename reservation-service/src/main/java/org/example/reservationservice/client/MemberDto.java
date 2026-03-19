package org.example.reservationservice.client;

public class MemberDto {

    private Long id;
    private String fullName;
    private String email;
    private String subscriptionType;
    private Boolean suspended;
    private Integer maxConcurrentBookings;

    public MemberDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }

    public Integer getMaxConcurrentBookings() {
        return maxConcurrentBookings;
    }

    public void setMaxConcurrentBookings(Integer maxConcurrentBookings) {
        this.maxConcurrentBookings = maxConcurrentBookings;
    }
}