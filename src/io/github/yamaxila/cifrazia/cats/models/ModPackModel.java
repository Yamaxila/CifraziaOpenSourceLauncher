package io.github.yamaxila.cifrazia.cats.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModPackModel extends BaseResponse {

    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("cover")
    private String coverUrl;
    @JsonProperty("minecraft")
    private Minecraft minecraft;

    public ModPackModel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return minecraft.version;
    }


    public String toJson() {
        return String.format("{\"id\":%s,\"name\":\"%s\"}", this.id, this.name);
    }
    @Override
    public String toString() {
        return this.getName();
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }

    public void setMinecraft(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public static class Minecraft {

        @JsonProperty("id")
        private int id;
        @JsonProperty("version")
        private String version;

        @JsonProperty("client_tweak_class")
        private String tweakClass;
        @JsonProperty("main_client_class")
        private String mainClientClass;
        @JsonProperty("main_client_jar_md5")
        private String minecraftJarMd5;

        public Minecraft() {}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getTweakClass() {
            return tweakClass;
        }

        public void setTweakClass(String tweakClass) {
            this.tweakClass = tweakClass;
        }

        public String getMainClientClass() {
            return mainClientClass;
        }

        public void setMainClientClass(String mainClientClass) {
            this.mainClientClass = mainClientClass;
        }

        public String getMinecraftJarMd5() {
            return minecraftJarMd5;
        }

        public void setMinecraftJarMd5(String minecraftJarMd5) {
            this.minecraftJarMd5 = minecraftJarMd5;
        }
    }
}
