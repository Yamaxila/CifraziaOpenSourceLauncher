package io.github.yamaxila.cifrazia.cats.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.yamaxila.cifrazia.Main;

import java.util.List;

public class VersionFilesResponse extends BaseResponse{

    @JsonProperty("name")
    private String name;
    @JsonProperty("files")
    private List<FileModel> files;
    @JsonProperty("folders")
    private List<VersionFilesResponse> folders;
    @JsonProperty("delete")
    private boolean delete;
    @JsonProperty("update")
    private boolean update;
    @JsonProperty("size")
    private long size;

    public VersionFilesResponse() {

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFiles(List<FileModel> files) {
        this.files = files;
    }

    public List<FileModel> getFiles() {
        return this.files;
    }

    public boolean isDelete() {
        return this.delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isUpdate() {
        return this.update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public long getSize() {
        return this.size;
    }

    public long getCalculatedSize() {
        return this.files.stream().mapToLong(f -> f.size).sum() + this.folders.stream().mapToLong(VersionFilesResponse::getCalculatedSize).sum();
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "VersionFilesResponse{" +
                "name='" + this.name + '\'' +
                ", files=" + this.files +
                ", folders=" + this.folders +
                ", delete=" + this.delete +
                ", update=" + this.update +
                ", size=" + this.size +
                ", calc=" + this.getCalculatedSize() +
                '}';
    }

    public List<VersionFilesResponse> getFolders() {
        return this.folders;
    }

    public void setFolders(List<VersionFilesResponse> folders) {
        this.folders = folders;
    }


    public static class FileModel {
        @JsonProperty("hash")
        private String hash;
        @JsonProperty("name")
        private String name;
        @JsonProperty("delete")
        private boolean delete;
        private String path;
        @JsonProperty("size")
        private long size;
        @JsonProperty("update")
        private boolean update;

        public FileModel () {

        }

        public FileModel(String name, String hash, long size) {
            this.setName(name);
            this.setHash(hash);
            this.setSize(size);
        }

        public FileModel(String name, String hash, String absPath, long size, boolean update, boolean delete) {
            this.setName(name);
            this.setHash(hash);
            this.setPath(absPath);
            this.setSize(size);
            this.setUpdate(update);
            this.setDelete(delete);
        }

        public String getHash() {
            return this.hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getName() {
            return this.name;
        }

        public String getUpdatePath() {
            return this.path.replace(Main.GAME_DIR, "");
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getSize() {
            return this.size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public boolean isUpdate() {
            return this.update;
        }

        public void setUpdate(boolean update) {
            this.update = update;
        }

        @Override
        public String toString() {
            return "FileModel{" +
                    "hash='" + hash + '\'' +
                    ", name='" + name + '\'' +
                    ", path='" + path + '\'' +
                    ", updatePath='" + getUpdatePath() + '\'' +
                    ", size=" + size +
                    ", update=" + update +
                    '}';
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isDelete() {
            return delete;
        }

        public void setDelete(boolean delete) {
            this.delete = delete;
        }
    }
}
