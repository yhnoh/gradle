package org.modulea;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class User {

    private String username;

    public User(String username) {
        this.username = username;
    }

    public String toJsonNotThrow(){
        try {
            return toJson();
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

    public String getUsername() {
        return username;
    }
}
