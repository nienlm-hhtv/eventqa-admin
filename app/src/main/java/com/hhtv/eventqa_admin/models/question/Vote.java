
package com.hhtv.eventqa_admin.models.question;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Vote {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("changed_questions")
    @Expose
    private List<Result> changed_questions = new ArrayList<>();
    @SerializedName("new_questions")
    @Expose
    private List<Result> new_questions = new ArrayList<>();
    @SerializedName("removed_questions")
    @Expose
    private List<Result> removed_questions = new ArrayList<>();

    public List<Result> getRemoved_questions() {
        return removed_questions;
    }

    public void setRemoved_questions(List<Result> removed_questions) {
        this.removed_questions = removed_questions;
    }

    public List<Result> getChanged_questions() {
        return changed_questions;
    }

    public void setChanged_questions(List<Result> changed_questions) {
        this.changed_questions = changed_questions;
    }

    public List<Result> getNew_questions() {
        return new_questions;
    }

    public void setNew_questions(List<Result> new_questions) {
        this.new_questions = new_questions;
    }

    /**
     * 
     * @return
     *     The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * 
     * @param success
     *     The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * 
     * @return
     *     The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * 
     * @param message
     *     The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public Vote(Boolean success, String message, List<Result> changed_questions, List<Result> new_questions, List<Result> removed_questions) {
        this.success = success;
        this.message = message;
        this.changed_questions = changed_questions;
        this.new_questions = new_questions;
        this.removed_questions = removed_questions;
    }
}
