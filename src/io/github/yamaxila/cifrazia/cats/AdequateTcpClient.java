package io.github.yamaxila.cifrazia.cats;

import com.cifrazia.cats.CatsConnect;
import com.cifrazia.cats.model.CatsBroadcast;
import com.cifrazia.cats.model.response.BasicResponse;
import com.cifrazia.magiccore.common.tcp.api.IActivateHandler;
import com.cifrazia.magiccore.common.tcp.api.IConnectData;
import com.google.common.primitives.UnsignedInteger;
import io.github.yamaxila.cifrazia.cats.models.CatsData;

import java.nio.charset.StandardCharsets;

public class AdequateTcpClient {

    private CatsConnect catsConnect;
    private final CatsBroadcast catsBroadcast;
    private final IConnectData connectData;
    private final IActivateHandler activateHandler;


    public void connect() {
        catsConnect = CatsConnect.builder().apiVersion(CatsConnect.CATS_VERSION).secretKey(this.connectData.getSecretKey()).ip(this.connectData.getIp()).port(this.connectData.getPort()).connectEvent(() -> {

        }).reconnectEvent(this.activateHandler::subscribe).catsBroadcast(this.catsBroadcast).build();
    }

    public CatsConnect getCatsConnect() {
        return catsConnect;
    }

    public void reconnect() {
        connect();
    }

    public AdequateTcpClient(IConnectData connectData, CatsBroadcast catsBroadcast, IActivateHandler activateHandler) {
        this.connectData = connectData;
        this.catsBroadcast = catsBroadcast;
        this.activateHandler = activateHandler;
    }

    public AdequateTcpClient(CatsData data, CatsBroadcast catsBroadcast, IActivateHandler activateHandler) {
        this.connectData = new IConnectData() {
            @Override
            public String getSecretKey() {
                return data.getSecretKey();
            }

            @Override
            public String getIp() {
                return data.getIp();
            }

            @Override
            public Integer getPort() {
                return data.getPort();
            }
        };
        this.catsBroadcast = catsBroadcast;
        this.activateHandler = activateHandler;
    }

}
