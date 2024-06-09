package com.example.licencespring.model;

public class APIRequestLog {
    private String id;
    private String userId;
    private String requestTime;
    private String apiKey;
    private String requestDetails;

    public APIRequestLog() {}

    public APIRequestLog(String userId, String requestTime, String apiKey, String requestDetails) {
        this.userId = userId;
        this.requestTime = requestTime;
        this.apiKey = apiKey;
        this.requestDetails = requestDetails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getRequestDetails() {
        return requestDetails;
    }

    public void setRequestDetails(String requestDetails) {
        this.requestDetails = requestDetails;
    }
}
