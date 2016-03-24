
package com.hhtv.eventqa_admin.models.event;

import org.parceler.Parcel;

@Parcel
public class Result {
    private String id;
    private String name;
    private String created_date;
    private String description;
    private String image_link;
    private String qrcode_link;
    private String status;
    private String user_id;
    private String modified_date;
    private String start_date;
    private String end_date;

    public Result() {
    }

    public Result(String id, String name, String created_date, String description, String image_link, String qrcode_link, String status, String user_id, String modified_date, String start_date, String end_date) {
        this.id = id;
        this.name = name;
        this.created_date = created_date;
        this.description = description;
        this.image_link = image_link;
        this.qrcode_link = qrcode_link;
        this.status = status;
        this.user_id = user_id;
        this.modified_date = modified_date;
        this.start_date = start_date;
        this.end_date = end_date;
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

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getQrcode_link() {
        return qrcode_link;
    }

    public void setQrcode_link(String qrcode_link) {
        this.qrcode_link = qrcode_link;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
