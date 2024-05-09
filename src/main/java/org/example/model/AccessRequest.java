package org.example.model;

import java.io.Serializable;

/**
 * description:
 *
 * @author liuhuayang
 * date: 2024/5/9 16:26
 */
public class AccessRequest implements Serializable {
    private int userId;
    private String[] endpoints;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String[] getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(String[] endpoints) {
        this.endpoints = endpoints;
    }
}