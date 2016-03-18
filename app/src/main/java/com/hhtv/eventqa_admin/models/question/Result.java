
package com.hhtv.eventqa_admin.models.question;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Result implements Serializable{

    private int id;
    private String name;
    private String body;
    private String create_at;
    private int creator_id;
    private String creator_name;
    private int vote_up_count;
    private int vote_down_count;
    private Boolean isVoted;
    private int status;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Result() {
    }

    /**
     * 
     * @param id
     * @param body
     * @param vote_down_count
     * @param create_at
     * @param status
     * @param name
     * @param vote_up_count
     * @param creator_id
     * @param isVoted
     */
    public Result(int id, String name, String body, String create_at, int creator_id, String creator_name, int vote_up_count, int vote_down_count, Boolean isVoted, int status) {
        this.id = id;
        this.name = name;
        this.body = body;
        this.create_at = create_at;
        this.creator_id = creator_id;
        this.creator_name = creator_name;
        this.vote_up_count = vote_up_count;
        this.vote_down_count = vote_down_count;
        this.isVoted = isVoted;
        this.status = status;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }

    /**
     * 
     * @return
     *     The id
     */
    public int getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The body
     */
    public String getBody() {
        return body;
    }

    /**
     * 
     * @param body
     *     The body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * 
     * @return
     *     The create_at
     */
    public String getcreate_at() {
        return create_at;
    }

    /**
     * 
     * @param create_at
     *     The create_at
     */
    public void setcreate_at(String create_at) {
        this.create_at = create_at;
    }

    /**
     * 
     * @return
     *     The creator_id
     */
    public int getcreator_id() {
        return creator_id;
    }

    /**
     * 
     * @param creator_id
     *     The creator_id
     */
    public void setcreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    /**
     * 
     * @return
     *     The vote_up_count
     */
    public int getvote_up_count() {
        return vote_up_count;
    }

    /**
     * 
     * @param vote_up_count
     *     The vote_up_count
     */
    public void setvote_up_count(int vote_up_count) {
        this.vote_up_count = vote_up_count;
    }

    /**
     * 
     * @return
     *     The vote_down_count
     */
    public int getvote_down_count() {
        return vote_down_count;
    }

    /**
     * 
     * @param vote_down_count
     *     The vote_down_count
     */
    public void setvote_down_count(int vote_down_count) {
        this.vote_down_count = vote_down_count;
    }

    /**
     * 
     * @return
     *     The isVoted
     */
    public Boolean getIsVoted() {
        return isVoted;
    }

    /**
     * 
     * @param isVoted
     *     The isVoted
     */
    public void setIsVoted(Boolean isVoted) {
        this.isVoted = isVoted;
    }

    /**
     * 
     * @return
     *     The status
     */
    public int getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    /*@Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o == null || o.getClass() != getClass()){
            return false;
        }
        else{
            Result r = (Result) o;
            if (r.getBody().equals(this.getBody()) && r.getcreator_id() == this.getcreator_id()
                    && r.getcreate_at().equals(this.getcreate_at())){
                result = true;
            }
        }
        return result;
    }*/
}
