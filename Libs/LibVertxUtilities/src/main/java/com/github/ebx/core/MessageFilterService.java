package com.github.ebx.core;

/**
 * @author Gregory Boissinot
 */
public class MessageFilterService {

    private String address;

    private String action;

    public MessageFilterService(String address, String action) {
        this.address = address;
        this.action = action;
    }

    public String getAddress() {
        return address;
    }

    public String getAction() {
        return action;
    }
}
