//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.yamaxila.cifrazia.cats;


import com.cifrazia.cats.api.primitives.UnsignedShort;
import com.cifrazia.cats.enumiration.CompressionType;
import com.cifrazia.cats.enumiration.DataType;
import com.cifrazia.cats.model.header.BasicHeader;
import com.cifrazia.cats.model.request.BasicRequest;
import com.cifrazia.cats.model.response.BasicResponse;
import com.cifrazia.magiccore.common.api.SerDes;
import com.cifrazia.magiccore.common.tcp.api.ResponseType;
import com.cifrazia.magiccore.common.tcp.exception.ResponseException;
import com.cifrazia.magiccore.common.tcp.model.Error;
import com.cifrazia.magiccore.common.tcp.model.Header;
import com.cifrazia.magiccore.common.tcp.model.Request;
import com.cifrazia.magiccore.common.tcp.model.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class AdequateRequestDispatcher {
    public <T> Mono<T> sendRequest(AdequateTcpClient client, Request request, ResponseType responseType, TypeReference<T> typeReference) {
        return this.sendRequest(client, request, responseType).map((response) -> {
            if (response != null) {
                try {
                    String msg = response.getStringMessage();
                    if(msg.isEmpty()) {
                        msg = "{\"str\": \"Server returned empty string!\"}";
                        return SerDes.OBJECT_MAPPER.readValue(msg, new TypeReference<T>() {});
                    }
                    return SerDes.OBJECT_MAPPER.readValue(msg, typeReference);

                } catch (JsonProcessingException var3) {
                    throw new ResponseException(var3);
                }
            } else {
                return null;
            }
        }).onErrorResume((t) -> {
            t.printStackTrace();
            return Mono.empty();
        }).subscribeOn(Schedulers.elastic());
    }

    public <T> Mono<T> sendRequest(AdequateTcpClient client, Request request, ResponseType responseType, Class<T> tClass) {
        return this.sendRequest(client, request, responseType).map((response) -> {
            if (response != null) {
                try {
                    return SerDes.OBJECT_MAPPER.readValue(response.getStringMessage(), tClass);
                } catch (JsonProcessingException var3) {
                    throw new ResponseException(var3);
                }
            } else {
                return null;
            }
        }).onErrorResume((t) -> {
            t.printStackTrace();
            return Mono.empty();
        }).subscribeOn(Schedulers.elastic());
    }

    public Mono<Response> sendRequest(AdequateTcpClient client, Request request, ResponseType responseType) {
        Header header = request.getHeader();
        DataType dataType = DataType.BYTES;
        if (header.getType() == 1) {
            dataType = DataType.JSON;
        }

        BasicHeader basicHeader = new BasicHeader(UnsignedShort.valueOf(header.getAction()), dataType, CompressionType.NONE);
        BasicRequest basicRequest = new BasicRequest(basicHeader, new HashMap(), request.getByteBufMessage(), new HashMap(), request.getTimeOutAt());
        Mono<Response> responseMono = client.getCatsConnect().sendMessage(basicRequest).map((response) -> {
            return this.parseResponse((BasicResponse)response);
        });
        return responseType == ResponseType.NONE ? null : responseMono;
    }

    private Response parseResponse(BasicResponse basicResponse) throws ResponseException {
        try {
            Error error = null;
            int status = Integer.valueOf(basicResponse.getMessageHeader().get("Status").toString());
            if (status != 200) {
                error = (Error)SerDes.OBJECT_MAPPER.readValue(basicResponse.getData().toString(StandardCharsets.UTF_8), Error.class);
            }
            BasicHeader basicHeader = (BasicHeader)basicResponse.getHeader();
            Header header = new Header(basicHeader.getMessageId().intValue(), basicHeader.getDataLength().intValue(), basicHeader.getDataType().getValue(), status, basicHeader.getHandlerId().intValue());
            Response response = new Response(header, basicResponse.getData());
            if (error == null) {
                return response;
            } else {
                throw new ResponseException(error);
            }
        } catch (IOException var7) {
            return null;
        }
    }

    public AdequateRequestDispatcher() {
    }
}
