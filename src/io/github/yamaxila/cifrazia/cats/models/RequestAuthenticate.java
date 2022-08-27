package io.github.yamaxila.cifrazia.cats.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestAuthenticate {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("modpack_id")
    private int modPackId;

    public static RequestAuthenticateBuilder builder() {
        return new RequestAuthenticateBuilder();
    }

    public static class RequestAuthenticateBuilder {
        private String accessToken;

        private String refreshToken;

        private int modPackId;

        @JsonProperty("access_token")
        public RequestAuthenticateBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        @JsonProperty("refresh_token")
        public RequestAuthenticateBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        @JsonProperty("modpack_id")
        public RequestAuthenticateBuilder modPackId(int modPackId) {
            this.modPackId = modPackId;
            return this;
        }

        public RequestAuthenticate build() {
            return new RequestAuthenticate(this.accessToken, this.refreshToken, this.modPackId);
        }

        public String toString() {
            return "RequestAuthenticate.RequestAuthenticateBuilder(accessToken=" + this.accessToken + ", refreshToken=" + this.refreshToken + ", modPackId=" + this.modPackId + ")";
        }
    }

    public RequestAuthenticate() {}

    public RequestAuthenticate(String accessToken, String refreshToken, int modPackId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.modPackId = modPackId;
    }

    @JsonProperty("modpack_id")
    public void setModPackId(int modPackId) {
        this.modPackId = modPackId;
    }
}
