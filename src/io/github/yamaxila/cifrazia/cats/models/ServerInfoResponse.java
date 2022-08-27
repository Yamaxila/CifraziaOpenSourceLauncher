package io.github.yamaxila.cifrazia.cats.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ServerInfoResponse extends BaseResponse {

    private long id;

    private String name;

    private long modpack;

    private String ip;

    private int port;

    private int online;

    private int max;

    private boolean status;

    private List<PlayerModel> players;

    @JsonProperty("picture_url")
    private String pictureUrl;

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public long getModpack() {
        return this.modpack;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getOnline() {
        return this.online;
    }

    public int getMax() {
        return this.max;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return this.status;
    }

    public void setPlayers(List<PlayerModel> players) {
        this.players = players;
    }

    public List<PlayerModel> getPlayers() {
        return this.players;
    }

    public String getPictureUrl() {
        return this.pictureUrl;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", modpack=" + modpack +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", online=" + online +
                ", max=" + max +
                ", status=" + status +
                ", players=" + players +
                ", pictureUrl='" + pictureUrl + '\'' +
                '}';
    }
}
