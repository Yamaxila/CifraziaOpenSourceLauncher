package io.github.yamaxila.cifrazia.cats.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UUIDModel  extends BaseResponse {

    @JsonProperty("uuid")
    private String uuid;

    public UUIDModel (){}

    public String getUuid() {
        return  this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    @Override
    public String toString() {
        return "UUIDModel{" +
                "uuid='" +  this.uuid + '\'' +
                '}';
    }

}
