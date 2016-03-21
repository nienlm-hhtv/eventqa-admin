
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

    public Result() {
    }

    public Result(String id, String name, String created_date, String description, String image_link, String qrcode_link, String status, String user_id) {
        this.id = id;
        this.name = name;
        this.created_date = created_date;
        this.description = description;
        this.image_link = image_link;
        this.qrcode_link = qrcode_link;
        this.status = status;
        this.user_id = user_id;
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
}
