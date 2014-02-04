package com.rndapp.task_feed.models;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 7/13/13
 * Time: 3:12 PM
 */
public class NewUserModel {
    private SignInModel user;

    public NewUserModel(SignInModel user) {
        this.user = user;
    }

    public SignInModel getUser() {
        return user;
    }

    public void setUser(SignInModel user) {
        this.user = user;
    }
}
