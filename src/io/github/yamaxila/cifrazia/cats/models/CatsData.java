package io.github.yamaxila.cifrazia.cats.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatsData {

    private String ip, secretKey;
    private int port;

    public static Map<Integer, CatsData> usedCats = new HashMap<>();

    public CatsData(String ip, int port, String secretKey) {
        this.ip = ip;
        this.secretKey = secretKey;
        this.port = port;
        usedCats.put(this.port, this);
    }

    public String getSecretKey() {
        return  this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getIp() {
        return  this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return  this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTcp() {
        return String.format("{\"ip\":\"%s\",\"port\":%s,\"secretKey\":\"%s\"}", this.ip, this.port, this.secretKey);
    }
}
