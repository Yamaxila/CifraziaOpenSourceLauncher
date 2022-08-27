package io.github.yamaxila.cifrazia.cats.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerModel extends BaseResponse {
    @JsonProperty("name")
    private String name;
    @JsonProperty("id")
    private String id;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return  this.name;
    }

    @Override
    public String toString() {
        return "PlayerModel{" +
                "name='" +  this.name + '\'' +
                ", id='" +  this.id + '\'' +
                '}';
    }
}
