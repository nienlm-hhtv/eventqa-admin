package com.hhtv.eventqa_admin.models.question;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nienb on 20/3/16.
 */
public class MarkQuestionResponse {
    private String status;
    private boolean success;
    private String msg;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public MarkQuestionResponse() {
    }

    /**
     *
     * @param status
     * @param msg
     * @param success
     */
    public MarkQuestionResponse(String status, boolean success, String msg) {
        this.status = status;
        this.success = success;
        this.msg = msg;
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
     * The msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     *
     * @param msg
     * The msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
