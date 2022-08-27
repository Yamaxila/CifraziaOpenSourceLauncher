package io.github.yamaxila.cifrazia.cats;

import com.cifrazia.cats.model.CatsBroadcast;
import com.cifrazia.magiccore.common.tcp.api.IActivateHandler;
import io.github.yamaxila.cifrazia.cats.models.RequestAuthenticate;
import com.cifrazia.cats.CatsConnect;
import com.cifrazia.magiccore.common.tcp.api.ActionType;
import com.cifrazia.magiccore.common.tcp.api.IConnectData;
import com.cifrazia.magiccore.common.tcp.api.ResponseType;
import com.cifrazia.magiccore.common.tcp.model.Request;
import com.cifrazia.magiccore.common.tcp.model.Response;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.yamaxila.cifrazia.cats.models.*;
import io.github.yamaxila.cifrazia.models.UserModel;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public class Protocol {

    private AdequateTcpClient client;
    private CatsConnect connection;
    private AdequateRequestDispatcher dispatcher;

    public Protocol(String host, int port, String secretKey, CatsBroadcast catsBroadcast, IActivateHandler activateHandler) {
        this.client = new AdequateTcpClient(new IConnectData() {
            @Override
            public String getSecretKey() {
                return secretKey;
            }

            @Override
            public String getIp() {
                return host;
            }

            @Override
            public Integer getPort() {
                return port;
            }
        },
                catsBroadcast,
                activateHandler);
    }

    public Protocol(CatsData data, CatsBroadcast catsBroadcast, IActivateHandler activateHandler) {
        this.client = new AdequateTcpClient(data, catsBroadcast, activateHandler);
    }

    public Protocol connect() {
        this.client.connect();
        this.connection = this.client.getCatsConnect();
        this.dispatcher = new AdequateRequestDispatcher();
        return this;
    }

    public void sendApiData() {
        this.dispatcher.sendRequest( this.client,new Request(1,0x00,"{\"api\":0,\"client_time\":" + System.currentTimeMillis() + ",\"compressors\":[\"zlib\"],\"default_compression\":\"zlib\",\"scheme_format\":\"JSON\"}"), ResponseType.NONE);
    }
    public UUIDModel sendGetUuidRequest() {
        return this.dispatcher.sendRequest(this.client, new Request(1, 512, ""), ResponseType.RESPONSE, new TypeReference<UUIDModel>(){}).block();
    }

    public AuthResponse sendRefreshRequest00B0(String version) {
        return this.dispatcher.sendRequest(this.client, new Request(1, 0x00B0, "{}"), ResponseType.RESPONSE, new TypeReference<AuthResponse>(){}).block();
    }

    public AuthResponse sendRefreshRequest(String refreshToken) {
        return this.dispatcher.sendRequest(this.client, new Request(1, 0x0106, String.format("{\"platform\":\"windows\",\"platform_bit\":\"64\",\"refresh_token\":\"%s\"}", refreshToken)), ResponseType.RESPONSE, new TypeReference<AuthResponse>(){}).block(Duration.ofSeconds(10));
    }

    public Mono<AuthResponse> sendRawRefreshRequest(String refreshToken, TypeReference<AuthResponse> typeReference) {
        return this.dispatcher.sendRequest(this.client, new Request(1, 0x0106, String.format("{\"platform\":\"windows\",\"platform_bit\":\"64\",\"refresh_token\":\"%s\"}", refreshToken)), ResponseType.RESPONSE, typeReference);
    }

    public AuthResponse sendAuthRequest(AuthRequest user) {
        return this.dispatcher.sendRequest(this.client, new Request(1, 0x0100, user), ResponseType.RESPONSE, new TypeReference<AuthResponse>(){}).block();
    }

    public RequestAuthenticate sendClientAuthRequest(UserModel user, int modPackId) {
        return this.dispatcher.sendRequest(this.client,
                new Request(
                        1,
                        1,
                        new RequestAuthenticate.RequestAuthenticateBuilder()
                                .accessToken(user.getUserCredentials().getAccess_token())
                                .refreshToken(user.getUserCredentials().getRefresh_token())
                                .modPackId(modPackId).build()
                ),
                ResponseType.RESPONSE,
                new TypeReference<RequestAuthenticate>(){}).block();
    }

    public void sendAuthRequest(String token) {
        this.dispatcher.sendRequest(this.client, new Request(1, 1, String.format("{\"access_token\":\"%s\"}", token)), ResponseType.NONE);
    }

    public VersionFilesResponse getVersionFilesRequest(String version, String type) {
        return this.dispatcher.sendRequest(this.client, new Request(1, 0x0b, "{\"bit\":\"64\",\"part\":\"" + type + "\",\"platform\":\"win\",\"version\":\"" + version + "\"}"), ResponseType.RESPONSE, new TypeReference<VersionFilesResponse>() {
        }).block();
    }

    public VersionFilesResponse getModPackFilesRequest(int id) {
        return this.dispatcher.sendRequest(this.client, new Request(1, 0x0c, "{\"id\":" + id + "}"), ResponseType.RESPONSE, new TypeReference<VersionFilesResponse>() {
        }).block();
    }

    public List<ModPackModel> getModPacksList() {

        return this.dispatcher.sendRequest(this.client, new Request(1, 52, "{}"), ResponseType.RESPONSE, new TypeReference<List<ModPackModel>>() {
        }).block();

    }

    public List<ServerInfoResponse> getServersInfo(int modPackId) {
        return this.dispatcher.sendRequest(this.client, new Request(1, 54, "{\"modpack\":" + modPackId + "}"), ResponseType.RESPONSE, new TypeReference<List<ServerInfoResponse>>() {
        }).block();
    }

    public void sendTest() {
        String response =  ((Response)this.dispatcher.sendRequest(this.client, new Request(1, ActionType.GAME_PROFILE, "{\"uuid\":\"0005e2e61e63f720ff1f2ecafdbd8bec\"}"), ResponseType.RESPONSE).block()).getStringMessage();
        System.out.println(response);
//        Gson gson = (new GsonBuilder()).registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
//        ProfileResponse propertiesResponse = gson.fromJson(response, ProfileResponse.class);
//        System.out.println(propertiesResponse.getUuid());
//        System.out.println(propertiesResponse.getUsername());
//        GameProfile newGameProfile = new GameProfile(UUIDTypeAdapter.fromString(propertiesResponse.getUuid()), propertiesResponse.getUsername());
//        newGameProfile.getProperties().putAll((Multimap)propertiesResponse.getProperties());
//        System.out.println(newGameProfile);
    }



    public void sendRaw(int type, String s) {
        System.out.println(s);
        this.dispatcher.sendRequest(this.client, new Request(1, type, String.format("{\"access_token\":\"%s\"}", s)), ResponseType.RESPONSE).subscribe(response -> {
            System.out.println(type + " : " + response.getStringMessage());
        });
//
    }
}
