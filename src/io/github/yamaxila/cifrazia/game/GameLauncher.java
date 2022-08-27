package io.github.yamaxila.cifrazia.game;

import io.github.yamaxila.cifrazia.Main;
import io.github.yamaxila.cifrazia.cats.models.CatsData;
import io.github.yamaxila.cifrazia.cats.models.ModPackModel;
import io.github.yamaxila.cifrazia.models.UserModel;
import io.github.yamaxila.cifrazia.utils.LauncherUtils;
import io.github.yamaxila.cifrazia.utils.SecurityUtils;
import io.github.yamaxila.cifrazia.utils.SimpleConfigUtils;
import io.github.yamaxila.core.utils.Logger;

import javax.swing.*;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameLauncher {


    private UserModel userModel;
    private ModPackModel modPackModel;

    private static boolean isGameRunning = false;


    public GameLauncher(UserModel userModel, ModPackModel modPackModel) {
        this.userModel = userModel;
        this.modPackModel = modPackModel;
    }

    public GameLauncher run() {
        File assetsDir = new File(String.format(Main.GAME_DIR + "/" + modPackModel.getVersion() + "/assets/"));
        File libsDir = new File(String.format(Main.GAME_DIR + "/" + modPackModel.getVersion() + "/libraries/"));
        File modPackDir = new File(String.format(Main.GAME_DIR + "/" + modPackModel.getVersion() + "/" + modPackModel.getName() + "/"));
        File nativesDir = new File(String.format(Main.GAME_DIR + "/" + modPackModel.getVersion() + "/natives"));
        File minecraft = new File(String.format(Main.GAME_DIR + "/" + modPackModel.getVersion() + "/minecraft.jar"));

        Logger.log(assetsDir.getAbsolutePath());
        Logger.log(libsDir.getAbsolutePath());
        Logger.log(modPackDir.getAbsolutePath());
        Logger.log(nativesDir.getAbsolutePath());
        Logger.log(minecraft.getAbsolutePath());


        List<String> params = new ArrayList<>();


        try {
//            List<URL> libs2Load = SecurityUtils.collectURLs(libsDir, new ArrayList<>());
//            libs2Load.add(minecraft.toURI().toURL());

//            URLClassLoader loader = new URLClassLoader(libs2Load.toArray(libs2Load.toArray(new URL[0])));

            //LauncherUtils.loadLibraries(libs2Load);

            params.add(System.getProperty("java.home") + ("/bin/java"));

            params.add("-Xmx" + SimpleConfigUtils.getValue("ram", "2048") + "M");
            params.add("-Duser.dir=" + modPackDir.getAbsolutePath());
            params.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
            params.add("-Dfml.ignorePatchDiscrepancies=true");
            params.add("-Dforge.lib_folder=" + libsDir.getAbsolutePath());
            params.add("-Dorg.lwjgl.librarypath=" + nativesDir.getAbsolutePath());
            params.add("-Dnet.java.games.input.librarypath=" + nativesDir.getAbsolutePath());
            params.add("-Dlog4j.configurationFile=" + "./log4j.xml");
            params.add("-Djava.library.path=" + nativesDir.getAbsolutePath());

            String cp = convertClassPath(SecurityUtils.collectFiles(libsDir, new ArrayList<>(Collections.singleton(minecraft))));

            params.add("-classpath");
            params.add(cp.substring(0, cp.length()-1));
            params.add(modPackModel.getMinecraft().getMainClientClass());

            params.add("--tweakClass");
            params.add(modPackModel.getMinecraft().getTweakClass());

            params.add("--username");
            params.add(userModel.getUsername());
            params.add("--uuid");
            params.add(userModel.getUuid());
            params.add("--accessToken");
            params.add(userModel.getUserCredentials().toString());
                        params.add("--userType");
            params.add("legacy");

            params.add("--version");
            params.add(this.modPackModel.getVersion());
            params.add("--assetsIndex");
            params.add(this.modPackModel.getVersion());

            params.add("--assetsDir");
            params.add(assetsDir.getAbsolutePath());
            params.add("--gameDir");
            params.add(modPackDir.getAbsolutePath());

            params.add("--tcp");
            params.add(CatsData.usedCats.get(17200).getTcp());
            params.add("--userProperties");
            params.add("{}");
            params.add("--modpack");
            params.add(this.modPackModel.toJson());


            Main.mainWindow.hide();
            ProcessBuilder pb = new ProcessBuilder(params);
            pb.inheritIO();
            pb.directory(modPackDir);

            Process p = pb.start();
            System.setOut(new PrintStream(p.getOutputStream()));
            GameLauncher.isGameRunning = true;
            new Thread(() -> {
                Thread.currentThread().setName("GameChecker");
                while (p.isAlive());
                GameLauncher.isGameRunning = false;
                LauncherUtils.crashLauncher();
            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private static String convertClassPath(List<File> files) {
        return files.stream().map(file -> file.getAbsolutePath() + ":").collect(Collectors.joining());
    }

    public static boolean isGameRunning() {
        return GameLauncher.isGameRunning;
    }
}
