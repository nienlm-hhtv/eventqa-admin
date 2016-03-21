
package com.hhtv.eventqa_admin.models.event;

import java.util.ArrayList;
import java.util.List;

public class Event {

    private String status;
    private boolean success;
    private String msg;
    private List<Result> results = new ArrayList<Result>();

    public Event() {
    }

    public Event(String status, boolean success, String msg, List<Result> results) {
        this.status = status;
        this.success = success;
        this.msg = msg;
        this.results = results;
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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
