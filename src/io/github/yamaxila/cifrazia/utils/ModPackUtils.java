package io.github.yamaxila.cifrazia.utils;

import io.github.yamaxila.cifrazia.Main;
import io.github.yamaxila.cifrazia.cats.Protocol;
import io.github.yamaxila.cifrazia.cats.models.ModPackModel;
import io.github.yamaxila.cifrazia.cats.models.VersionFilesResponse;
import io.github.yamaxila.cifrazia.ui.windows.MainWindow;
import io.github.yamaxila.core.utils.Logger;
import io.github.yamaxila.core.utils.OsUtils;
import org.apache.commons.compress.archivers.ArchiveException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ModPackUtils {


    public static List<String> downloadAll(ModPackModel modPack, List<String> toDownload) {

        List<String> out = new ArrayList<>();


        DownloadUtils currentDownload;

        for(String currentPart : toDownload) {
            if(!currentPart.equals(modPack.getName())) {
                currentDownload = new DownloadUtils(String.format(Main.BASE_DOWNLOAD_URL, "minecraft/" + modPack.getVersion(), currentPart), currentPart + (!currentPart.startsWith("minecraft.jar") ? ".tar" : ""), Main.GAME_DIR + File.separator + modPack.getVersion() + File.separator);
            } else {
                currentDownload = new DownloadUtils(String.format(Main.BASE_DOWNLOAD_URL, "modpack", modPack.getId()), modPack.getName() + ".tar", Main.GAME_DIR + File.separator + modPack.getVersion() + File.separator);
            }
            Logger.log("Downloading full pack: " + currentPart);
            if(currentPart.startsWith("minecraft.jar"))
                out.add(currentDownload.downloadFile().replace(".tar", ""));
            else
                out.add(currentDownload.downloadFile());

        }



//            Logger.warn("Cant' download natives for current OS!");
//            JOptionPane.showMessageDialog(null, "Cant' download natives for current OS!\nPlease add natives to folder: " + natives.getOutputPath() + natives.getOutputName().split("\\.")[0]);


        return out;
    }

    public static void unpack(boolean mode, String... input) {

        for (String path : input) {
            File f = new File(path);
            MainWindow.getInstance().setInfo("Unpacking " + f.getName());
            if(!f.exists())
                continue;
            String name = f.getName().split("\\.")[0];
            File nDir = new File(f.getParentFile(), name);
            if(!nDir.exists())
                nDir.mkdirs();

            try {
                if(mode)
                    ArchiveUtils.unTar(f, nDir);
                else
                    ArchiveUtils.unGzip(f, nDir);

                if (!f.delete())
                    throw new SecurityException("Can't delete downloaded archive!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ArchiveException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void downloadParts(Protocol protocol, List<String> toCheck, ModPackModel modPack) {

        for(String currentPart : toCheck) {
            MainWindow.getInstance().setInfinityProgress();
            MainWindow.getInstance().setInfo("Checking client resources...");
            List<VersionFilesResponse.FileModel> toDownload = SecurityUtils.checkFiles(protocol.getVersionFilesRequest(modPack.getVersion(), currentPart), new File(Main.GAME_DIR, modPack.getVersion() + File.separator + currentPart));
            MainWindow.getInstance().setInfo(toDownload.size() + " files need to be updated.");
            if(!toDownload.isEmpty()) {
                String outFile = new DownloadUtils(
                        String.format(Main.BASE_DOWNLOAD_URL, "minecraft", "" + modPack.getVersion() + "/" + currentPart) + "?only=" + getUpdateList(toDownload, "/" + modPack.getVersion() + "/" + currentPart)
                        , currentPart + (!currentPart.startsWith("minecraft.jar") ? ".tar" : "")
                        , Main.GAME_DIR + File.separator + modPack.getVersion()
                ).downloadFile();
                MainWindow.getInstance().setInfo("Unpacking " + currentPart + "...");
                unpack(false, outFile);
            }
        }
        MainWindow.getInstance().setInfinityProgress();
        MainWindow.getInstance().setInfo("Checking client files...");

        List<VersionFilesResponse.FileModel> toDownload = SecurityUtils.checkFiles(protocol.getModPackFilesRequest(modPack.getId()), new File(Main.GAME_DIR, modPack.getVersion() + File.separator + modPack.getName() ), ".jar", ".zip", "/config/", "scripts", ".zs");
        if(!toDownload.isEmpty()) {
            MainWindow.getInstance().setInfo("Downloading missing client files...");
            Logger.log("Downloading missing client files...");
            Logger.log(toDownload.toString());
            String outFile = new DownloadUtils(
                    String.format(Main.BASE_DOWNLOAD_URL, "modpack", "" + modPack.getId()) + "?only=" + getUpdateList(toDownload, "/" + modPack.getVersion() + "/" + modPack.getName())
                    , modPack.getName() + ".tar"
                    , Main.GAME_DIR + File.separator + modPack.getVersion()
            ).downloadFile();
            MainWindow.getInstance().setInfo("Unpacking client files...");

            unpack(false, outFile);
        }
    }

    private static String getUpdateList(List<VersionFilesResponse.FileModel> toDownload, String replaceStr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < toDownload.size(); i++) {
            sb.append("\"");
            sb.append(toDownload.get(i).getUpdatePath().replace(replaceStr, ""));
            sb.append("\"");
            if(i < toDownload.size()-1)
                sb.append(",");
        }

        sb.append("]");
        return sb.toString();
    }

    public static List<String> isModPackFilesNotPresents(ModPackModel modPack) {

        List<String> toDownload = new ArrayList<>();
        File base = new File(Main.GAME_DIR, modPack.getVersion());

        if(!new File(base, "libraries").exists())
            toDownload.add("libraries");

        if(!new File(base, "assets").exists())
            toDownload.add("assets");

        if(OsUtils.getOS() == OsUtils.OS.WINDOWS)
            if(!new File(base, "natives").exists())
                toDownload.add("natives");

        if((new File(base, "minecraft.jar").exists() &&
                Objects.equals(SecurityUtils.getMd5(new File(base, "minecraft,jar")), modPack.getMinecraft().getMinecraftJarMd5()))
                || !new File(base, "minecraft.jar").exists())
            toDownload.add("minecraft.jar");

        if(!new File(base, modPack.getName()).exists())
            toDownload.add(modPack.getName());
        return toDownload;
    }
}
