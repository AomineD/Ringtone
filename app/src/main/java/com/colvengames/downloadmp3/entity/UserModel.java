package com.colvengames.downloadmp3.entity;

public class UserModel {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActiveit() {
        return activeit;
    }

    public void setActiveit(boolean activeit) {
        this.activeit = activeit;
    }

    private String name;

    private String email;

    private boolean activeit;
}
