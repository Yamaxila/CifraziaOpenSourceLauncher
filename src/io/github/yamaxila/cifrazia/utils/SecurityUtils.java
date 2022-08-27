package io.github.yamaxila.cifrazia.utils;

import io.github.yamaxila.cifrazia.cats.models.VersionFilesResponse;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityUtils {

    public static String encode64(String s) {
        return Base64.getEncoder().withoutPadding().encodeToString(s.getBytes());
    }
    public static String decode64(String s) {
        return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
    }

    public static String getMd5(File file) {
        try (InputStream is = Files.newInputStream(Paths.get(file.getAbsolutePath()))) {
            return DigestUtils.md5Hex(is);
        } catch (IOException e) {
            return null;
        }
    }

    private static boolean filter(String name, String... filter) {
        if(filter.length == 0)
            return true;
        for (String s : filter)
            if(s.startsWith("!")) {
                if(name.contains(s.replace("!", "")))
                    return false;
            } else if(name.contains(s))
                return true;

        return false;
    }


    public static void deleteFiles(List<VersionFilesResponse.FileModel> files) {
        files.parallelStream().forEach((file) -> {
            if(file.getPath() == null)
                throw new NullPointerException("Path can't be null!");

            if(!new File(file.getPath()).delete())
                throw new SecurityException("Can't remove file! " + file.getPath());
        });

    }

    public static List<VersionFilesResponse.FileModel> toNormalList(VersionFilesResponse serverFiles, File baseDir, List<VersionFilesResponse.FileModel> outList) {

        for (VersionFilesResponse.FileModel fileModel : serverFiles.getFiles()) {
            outList.add(new VersionFilesResponse.FileModel(fileModel.getName()
                    , fileModel.getHash(),
                    new File(baseDir, fileModel.getName()).getAbsolutePath()
                    , fileModel.getSize()
                    , fileModel.isUpdate(), fileModel.isDelete()));
        }

        for(VersionFilesResponse folder : serverFiles.getFolders()) {
            toNormalList(folder, new File(baseDir, folder.getName()), outList);
        }

        return outList;
    }

    public static List<VersionFilesResponse.FileModel> clientFiles(File dir, List<VersionFilesResponse.FileModel> out, String... filter) {

        for(File s : dir.listFiles()) {
            if(s.isDirectory())
                clientFiles(s, out, filter);
            else if(filter(s.getAbsolutePath(), filter)) {
                out.add(new VersionFilesResponse.FileModel(s.getName(), getMd5(s), s.getAbsolutePath(), s.length(), false, false));
            }
        }
        return out;
    }

    public static List<VersionFilesResponse.FileModel> checkFiles(VersionFilesResponse serverFiles, File clientDir, String... filter) {

        List<VersionFilesResponse.FileModel> onlyClient = new LinkedList<>();
        List<VersionFilesResponse.FileModel> clientServer = new LinkedList<>();

        List<VersionFilesResponse.FileModel> normalServerFiles = toNormalList(serverFiles, clientDir, new LinkedList<>());
        List<VersionFilesResponse.FileModel> normalClientFiles = clientFiles(clientDir, new LinkedList<>(), filter);

        onlyClient.addAll(normalClientFiles.stream().filter(p -> normalServerFiles.stream().noneMatch(n -> n.getName().equals(p.getName()) && n.getPath().equals(p.getPath()) && n.getHash().equals(p.getHash()))).collect(Collectors.toList()));
        clientServer.addAll(normalServerFiles.stream().filter(p -> normalClientFiles.stream().noneMatch(n -> n.getName().equals(p.getName()) && n.getHash().equals(p.getHash()))).collect(Collectors.toList()));

        deleteFiles(onlyClient.stream().filter(p -> normalServerFiles.stream().anyMatch(n -> n.getHash().equals(p.getHash()) && n.isDelete() ) || normalServerFiles.stream().noneMatch(n -> p.getName().equals(n.getName()) && p.getHash().equals(n.getHash()) && n.getPath().equals(p.getPath()))).collect(Collectors.toList()));

        return clientServer.stream().filter(VersionFilesResponse.FileModel::isUpdate).collect(Collectors.toList());
    }

    public static List<URL> collectURLs(File dir, List<URL> out) throws MalformedURLException {
        for(File s : dir.listFiles()) {
            if(s.isDirectory())
                collectURLs(s, out);
            else if(s.getName().endsWith(".jar"))
                out.add(s.toURI().toURL());
        }
        return out;
    }
    public static List<File> collectFiles(File dir, List<File> out) throws MalformedURLException {
        for(File s : dir.listFiles()) {
            if(s.isDirectory())
                collectFiles(s, out);
            else if(s.getName().endsWith(".jar"))
                out.add(s);
        }
        return out;
    }


}
