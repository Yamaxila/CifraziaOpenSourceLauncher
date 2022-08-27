package io.github.yamaxila.cifrazia.ui.windows;

import com.cifrazia.cats.model.CatsBroadcast;
import com.cifrazia.cats.model.response.Response;
import com.cifrazia.magiccore.common.api.SerDes;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.yamaxila.cifrazia.cats.Protocol;
import io.github.yamaxila.cifrazia.cats.models.*;
import io.github.yamaxila.cifrazia.game.GameLauncher;
import io.github.yamaxila.cifrazia.models.UserModel;
import io.github.yamaxila.cifrazia.utils.LauncherUtils;
import io.github.yamaxila.cifrazia.utils.ModPackUtils;
import io.github.yamaxila.cifrazia.utils.SimpleConfigUtils;
import io.github.yamaxila.core.gui.theme.ThemeManager;
import io.github.yamaxila.core.messages.BaseMessage;
import io.github.yamaxila.core.messages.MessageDisplayer;
import io.github.yamaxila.core.messages.MessageType;
import io.github.yamaxila.core.utils.Logger;
import io.github.yamaxila.core.utils.OsUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainWindow implements Initializable {

    private static MainWindow instance;

    public static MainWindow getInstance() {
        return instance;
    }

    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private ChoiceBox<ModPackModel> serverList;
    @FXML
    private Label status;
    @FXML
    private ListView<String> players;
    @FXML
    private ProgressBar progress;

    private Protocol launcherProtocol;
    private Protocol launcherProtocol2;

    private UserModel currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainWindow.instance = this;
        ThemeManager.getInstance().updateControls(login, password);

        if(SimpleConfigUtils.getValue("login", null) != null)
            this.login.setText(SimpleConfigUtils.getValue("login"));

        new Thread(() -> {
            Thread.currentThread().setName("InitThread");


            this.launcherProtocol = new Protocol(
                    new CatsData(SimpleConfigUtils.getValue("server1host", null)
                            , Integer.parseInt(SimpleConfigUtils.getValue("server1port", "0"))
                            , SimpleConfigUtils.getValue("server1secret", null))
                    , response -> {
                if(this.currentUser != null) {
                    Logger.log("Updating token...");
                    //mono.block(); сломан и даже при наличии ответа от LaunchServer'а не возвращает данные
                    //Единственный вариант - использовать mono.subscribe(resp), чтобы обойти данный баг.
                    this.launcherProtocol.sendRawRefreshRequest(this.currentUser.getUserCredentials().getRefresh_token(), new TypeReference<AuthResponse>() {
                    }).subscribe((authResponse) -> {
                        this.currentUser.update(authResponse);
                        Logger.log("Sending token to second server...");
                        this.launcherProtocol2.sendAuthRequest(this.currentUser.getUserCredentials().getAccess_token());
                        Logger.log("Token update done!");

                    });

                }
            }, ()-> { });

            this.launcherProtocol2 = new Protocol(
                    new CatsData(SimpleConfigUtils.getValue("server2host", null)
                            , Integer.parseInt(SimpleConfigUtils.getValue("server2port", "0"))
                            , SimpleConfigUtils.getValue("server2secret", null))
                    , response -> { },
                    ()-> { });

            this.launcherProtocol.connect();
            this.launcherProtocol2.connect();


//        this.launcherProtocol.sendRaw(770, "{}");
//        this.launcherProtocol.sendRaw(769, "{}");
//        this.launcherProtocol.sendRaw(768, "{}");
//        770 : {"following":[]}
//        769 : {"followers":[]}
//        768 : {"friends":[{"rel_id":722,"relation":"friend_of","uuid":"0005e2eb-d193-6245-a128-37efc518d156","username":"Devillirium","is_verified":false,"bans":[],"avatar":null,"banners":[],"last_seen":1661430915.890582,"full_name":"","sex":null,"gender":null,"status_quote":null,"birth_date":null,"city":null,"bio":null,"web_site":null,"interests":[],"followers":0,"friends":2,"following":0,"posts":0,"work_places":[],"military_places":[],"education_places":[]},{"rel_id":738,"relation":"friend_of","uuid":"0005e2eb-dd39-f92f-4c79-e0abb56107e5","username":"Vitalikgam","is_verified":false,"bans":[],"avatar":null,"banners":[],"last_seen":1661431540.698445,"full_name":"","sex":null,"gender":null,"status_quote":null,"birth_date":null,"city":null,"bio":null,"web_site":null,"interests":[],"followers":0,"friends":2,"following":0,"posts":0,"work_places":[],"military_places":[],"education_places":[]}]}
            //18-й(0x12) пакет - информация о языке + валюте
            //769-й(0x0301) - подписчики(авторизация)
            //52-й- Список серверов(без авторизации) 17200
            //this.launcherProtocol.sendRaw(1, "{\"access_token\": \""+ SimpleConfigUtils.getValue("access_token") + "\"}");

            reAuth();

            Platform.runLater(()-> {

                players.setCellFactory(param -> new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item == null) {
                            setText(null);
                            return;
                        }
                        setAlignment(Pos.CENTER);
                        if(item.endsWith("*")) {
                            item = item.substring(0, item.length() - 1);
                            setFont(Font.font(14));
                        }
                        setText(item);

                    }
                });

                ObservableList<ModPackModel> models = FXCollections.observableList(this.launcherProtocol2.getModPacksList());

                serverList.setItems(models);
                serverList.getSelectionModel().select(models.stream().filter(p -> p.getId() == Integer.parseInt(SimpleConfigUtils.getValue("last_modpack", "1"))).findFirst().get());

                serverList.setOnAction(actionEvent -> {
                    try {
                        SimpleConfigUtils.setValue("last_modpack", String.valueOf(serverList.getValue().getId()));
                        SimpleConfigUtils.save();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    setModPackData(serverList.getValue());
                });
                setModPackData(serverList.getValue());

            });
        }).start();
    }

    @FXML
    public void login() {
        if(GameLauncher.isGameRunning())
            return;

        if (this.currentUser == null) {
            this.setInfo("Sending auth request...");
            AuthResponse authResponse = this.launcherProtocol.sendAuthRequest(new AuthRequest(false, this.login.getText(), this.password.getText()));
            if (authResponse == null) {
                this.setInfo("Error!");
                return;
            }
            this.setInfo("Successful");

            SimpleConfigUtils.setValue("access_token", authResponse.getAccess_token());
            SimpleConfigUtils.setValue("refresh_token", authResponse.getRefresh_token());
            SimpleConfigUtils.setValue("login", this.login.getText());
            try {
                SimpleConfigUtils.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            this.launcherProtocol2.sendAuthRequest(authResponse.getAccess_token());

            this.currentUser = new UserModel(authResponse, this.login.getText(), this.launcherProtocol.sendGetUuidRequest().getUuid());
        }

        this.serverList.setDisable(true);
        new Thread(() -> {
            Thread.currentThread().setName("UpdateAndPlayThread");
            this.setInfinityProgress();
            this.setInfo("Collecting download parts...");
            List<String> toDownload = ModPackUtils.isModPackFilesNotPresents(this.serverList.getValue());
            this.setInfo(toDownload.size() + " parts need to be downloaded.");
            List<String> files = ModPackUtils.downloadAll(serverList.getSelectionModel().getSelectedItem(), toDownload);
            this.setInfo("Unpacking parts...");
            ModPackUtils.unpack(true, files.stream().filter(p -> p.endsWith(".tar")).toArray(String[]::new));
            List<String> toCheck = new ArrayList<>();
            toCheck.add("libraries");
            toCheck.add("assets");
            if(OsUtils.getOS() == OsUtils.OS.WINDOWS)
                toCheck.add("natives");
            else if(SimpleConfigUtils.getValue("natives_warn", "false").equals("false")){
                MessageDisplayer.displayMessage(new BaseMessage("Linux is not supported by cifrazia. Download and install natives to game directory!", MessageType.WARN));
                SimpleConfigUtils.setValue("natives_warn", "true");
                try {
                    SimpleConfigUtils.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            ModPackUtils.downloadParts(this.launcherProtocol2, toCheck, this.serverList.getValue());
            AuthResponse response = this.launcherProtocol2.sendRefreshRequest00B0(this.serverList.getValue().getVersion());

            new GameLauncher(new UserModel(response, this.currentUser.getUsername(), this.currentUser.getUuid()), this.serverList.getValue()).run();
        }).start();
    }

    public void setInfo(String msg) {
        Platform.runLater(() -> this.status.setText(msg));
    }

    public void setProgress(double progress) {
        Platform.runLater(() -> this.progress.setProgress(progress));
    }

    public void setInfinityProgress() {
        setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    }

    private void setModPackData(ModPackModel modPackModel) {
        this.players.setItems(FXCollections.singletonObservableList("Loading...*"));
        new Thread(() -> {
            Thread.currentThread().setName("setModPackDataThread");

            List<String> toSet = new LinkedList<>();
            List<ServerInfoResponse> servers = this.launcherProtocol2.getServersInfo(modPackModel.getId());
            for (ServerInfoResponse server : servers) {
                toSet.add(server.getName()+"*");
                toSet.add(modPackModel.getVersion());
                toSet.add(String.format("%s/%s", server.getOnline(), server.getMax()));
                toSet.add("");
                toSet.add("Players*");
                for (PlayerModel player : server.getPlayers()) {
                    toSet.add(player.getName());
                }
                toSet.add("");
            }

            Platform.runLater(() -> this.players.setItems(FXCollections.observableList(toSet)));

        }).start();

    }


    private boolean reAuth() {
        if(SimpleConfigUtils.getValue("access_token", null) != null) {
            Platform.runLater(()-> {this.setInfo("Logins with access token...");});

            this.launcherProtocol.sendAuthRequest(SimpleConfigUtils.getValue("access_token"));
            UUIDModel uuidModel = this.launcherProtocol.sendGetUuidRequest();
            if(uuidModel == null) {
                if(SimpleConfigUtils.getValue("refresh_token") != null) {
                    AuthResponse authResponse = this.launcherProtocol.sendRefreshRequest(SimpleConfigUtils.getValue("refresh_token"));
                    if(authResponse != null) {
                        SimpleConfigUtils.setValue("access_token", authResponse.getAccess_token());
                        SimpleConfigUtils.setValue("refresh_token", authResponse.getRefresh_token());
                        SimpleConfigUtils.setValue("login", login.getText());
                        uuidModel = this.launcherProtocol.sendGetUuidRequest();
                    }
                }
            }
            if(uuidModel == null) {
                Platform.runLater(()-> {
                    this.setInfo("Error! Input your password again.");
                    this.password.setPromptText("*error*");
                });
                this.currentUser = null;
            } else {
                UUIDModel finalUuidModel = uuidModel;
                Platform.runLater(()-> {

                    this.currentUser = new UserModel(new AuthResponse(SimpleConfigUtils.getValue("access_token"), SimpleConfigUtils.getValue("refresh_token")), this.login.getText(), finalUuidModel.getUuid());
                    this.launcherProtocol2.sendAuthRequest(this.currentUser.getUserCredentials().getAccess_token());
                    setInfo("Successful!");
                    this.password.setPromptText("*saved*");
                    //this.launcherProtocol2.sendRaw(1, currentUser.getUserCredentials().getAccess_token());
                });
                return true;
            }
        }

        return false;
    }
}
