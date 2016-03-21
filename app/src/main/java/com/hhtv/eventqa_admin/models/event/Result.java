
package com.hhtv.eventqa_admin.models.event;

import org.parceler.Parcel;

@Parcel
public class Result {
    private String status;
    private boolean success;
    private String message;
    private String id;
    private String name;
    private String description;
    private String imageLink;
    private String qrCodeLink;
    private String creator_id;
    private String creator_name;
    private String create_at;
    private int total_question;
    private int answered_question;

    public Result() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getQrCodeLink() {
        return qrCodeLink;
    }

    public void setQrCodeLink(String qrCodeLink) {
        this.qrCodeLink = qrCodeLink;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public int getTotal_question() {
        return total_question;
    }

    public void setTotal_question(int total_question) {
        this.total_question = total_question;
    }

    public int getAnswered_question() {
        return answered_question;
    }

    public void setAnswered_question(int answered_question) {
        this.answered_question = answered_question;
    }

    public Result(String status, boolean success, String message, String id, String name, String description, String imageLink, String qrCodeLink, String creator_id, String creator_name, String create_at, int total_question, int answered_question) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageLink = imageLink;
        this.qrCodeLink = qrCodeLink;
        this.creator_id = creator_id;
        this.creator_name = creator_name;
        this.create_at = create_at;
        this.total_question = total_question;
        this.answered_question = answered_question;
    }
}
