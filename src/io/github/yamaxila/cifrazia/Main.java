package io.github.yamaxila.cifrazia;

import io.github.yamaxila.cifrazia.utils.LauncherUtils;
import io.github.yamaxila.cifrazia.utils.SecurityUtils;
import io.github.yamaxila.cifrazia.utils.SimpleConfigUtils;
import io.github.yamaxila.core.gui.Window;
import io.github.yamaxila.core.gui.theme.models.ThemeDark;
import io.github.yamaxila.core.utils.TranslationUtils;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Main extends Application {

    public static String GAME_DIR = getCurrentPath() + "/game";
    public static String BASE_DOWNLOAD_URL = "https://minecraft.cifrazia.com/files/download/%s/%s";


    public static Window mainWindow;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        SimpleConfigUtils.loadConfig();

        System.out.println("Launcher is loading libraries...");
        LauncherUtils.loadLibraries(SecurityUtils.collectURLs(new File(SimpleConfigUtils.getValue("libs_path")), new LinkedList<>())
                .stream().filter(p -> (p.getFile().contains("netty")
                        || p.getFile().contains("byte-buddy-1.10.5")
                        || p.getFile().contains("byte-buddy-agent-1.10.5")
                        || p.getFile().contains("cache2k-api-1.2.4.Final")
                        || p.getFile().contains("cache2k-core-1.2.4.Final")
                        || p.getFile().contains("jackson")
                        || p.getFile().contains("guava")
                        || p.getFile().contains("reactive")
                        || p.getFile().contains("reactor")
                        || p.getFile().contains("cats")
                        || p.getFile().contains("magic")
                        || p.getFile().contains("hamcrest")
                        || p.getFile().contains("snakeyaml")
                        || p.getFile().contains("apache")
                        || p.getFile().contains("commons"))).collect(Collectors.toList()));
        System.out.println("Launcher loading libraries done!");


        mainWindow = new Window(stage, new ThemeDark());
        mainWindow.createNewFrame(Window.getResourceURL("io/github/yamaxila/cifrazia/ui/main.fxml"), "Test");

        stage.setOnCloseRequest((event) -> {
           LauncherUtils.crashLauncher();
        });
    }
    public static String getCurrentPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString();
    }
}