package io.github.yamaxila.cifrazia.cats.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseResponse {
    @JsonProperty("str")
    private String str;

    public BaseResponse() {}

    public String getStr() {
        return this.str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
