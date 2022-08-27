package io.github.yamaxila.cifrazia.ui.windows;

import io.github.yamaxila.cifrazia.Starter;
import io.github.yamaxila.cifrazia.utils.LauncherUtils;
import io.github.yamaxila.cifrazia.utils.SimpleConfigUtils;
import io.github.yamaxila.core.messages.BaseMessage;
import io.github.yamaxila.core.messages.MessageDisplayer;
import io.github.yamaxila.core.messages.MessageType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class PreloaderWindow implements Initializable {

    @FXML
    TextField libs_path, server1host, server1port, server1secret, server2host, server2port, server2secret;
    @FXML
    Slider ram;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        libs_path.setText(SimpleConfigUtils.getValue("libs_path", "C:/Cifrazia/games/Minecraft/1.12.2/libraries"));
        server1host.setText(SimpleConfigUtils.getValue("secret1host", ""));
        server1port.setText(SimpleConfigUtils.getValue("secret1port", ""));
        server1secret.setText(SimpleConfigUtils.getValue("secret1secret", ""));

        server2host.setText(SimpleConfigUtils.getValue("secret2host", ""));
        server2port.setText(SimpleConfigUtils.getValue("secret2port", ""));
        server2secret.setText(SimpleConfigUtils.getValue("secret2secret", ""));

        ram.setValue(Double.parseDouble(SimpleConfigUtils.getValue("ram", "2048")));

    }

    @FXML
    public void selectPath() {

    }

    @FXML
    public void save() {
        if(libs_path.getText().isEmpty() || !new File(libs_path.getText()).exists()) {
            MessageDisplayer.displayMessage(new BaseMessage("Libs path not exists!", MessageType.ERROR));
            return;
        }

        if(this.server1host.getText().isEmpty() || this.server1port.getText().isEmpty() || this.server1secret.getText().isEmpty()) {
            MessageDisplayer.displayMessage(new BaseMessage("One of Launch Server params empty!", MessageType.ERROR));
            return;
        }

        if(this.server2host.getText().isEmpty() || this.server2port.getText().isEmpty() || this.server2secret.getText().isEmpty()) {
            MessageDisplayer.displayMessage(new BaseMessage("One of Game Server params empty!", MessageType.ERROR));
            return;
        }

        SimpleConfigUtils.setValue("libs_path", libs_path.getText());
        SimpleConfigUtils.setValue("server1host", server1host.getText());
        SimpleConfigUtils.setValue("server1port", server1port.getText());
        SimpleConfigUtils.setValue("server1secret", server1secret.getText());
        SimpleConfigUtils.setValue("server2host", server2host.getText());
        SimpleConfigUtils.setValue("server2port", server2port.getText());
        SimpleConfigUtils.setValue("server2secret", server2secret.getText());
        SimpleConfigUtils.setValue("ram", String.valueOf((int)ram.getValue()));
        try {
            SimpleConfigUtils.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Starter.preloaderWindow.hide();
            LauncherUtils.restartLauncher(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
