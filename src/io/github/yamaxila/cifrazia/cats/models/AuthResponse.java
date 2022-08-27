package io.github.yamaxila.cifrazia.cats.models;

import com.cifrazia.cats.api.jackson.serializer.UnsignedIntegerSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;

public class AuthResponse extends BaseResponse{
    @JsonProperty("access_token")
    private String access_token;
    @JsonProperty("refresh_token")
    private String refresh_token;

    public AuthResponse(String access_token, String refresh_token) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }

    public AuthResponse() { }

    public String getAccess_token() {
        return  this.access_token;
    }

    public String getRefresh_token() {
        return  this.refresh_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    @Override
    public String toString() {
        return String.format("{\"access_token\":\"%s\", \"refresh_token\":\"%s\"}", this.access_token, this.refresh_token);
    }
}
