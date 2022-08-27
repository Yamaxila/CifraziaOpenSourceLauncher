package io.github.yamaxila.cifrazia;

import io.github.yamaxila.cifrazia.utils.LauncherUtils;
import io.github.yamaxila.cifrazia.utils.SimpleConfigUtils;
import io.github.yamaxila.core.gui.Window;
import io.github.yamaxila.core.gui.theme.models.ThemeDark;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Starter extends Application {

    public static Window preloaderWindow;

    public static void main(String[] args) throws Exception {
        try {
            SimpleConfigUtils.loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(SimpleConfigUtils.getValue("libs_path", null) == null)
            launch(args);
        else
            LauncherUtils.restartLauncher(true);

    }

    @Override
    public void start(Stage stage) throws Exception {
        preloaderWindow = new Window(stage, new ThemeDark());
        preloaderWindow.createNewFrame(Window.getResourceURL("io/github/yamaxila/cifrazia/ui/preloader.fxml"), "Simple Settings");
    }
}
