package com.hhtv.eventqa_admin.models.user;

import org.parceler.Parcel;

@Parcel
public class GetUserResponse {


    private String status;
    private boolean success;
    private String code;
    private String username;
    private String email;

    /**
     * No args constructor for use in serialization
     *
     */
    public GetUserResponse() {
    }

    /**
     *
     * @param username
     * @param email
     * @param status
     * @param code
     * @param success
     */
    public GetUserResponse(String status, boolean success, String code, String username, String email) {
        this.status = status;
        this.success = success;
        this.code = code;
        this.username = username;
        this.email = email;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     *
     * @return
     * The code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }
}