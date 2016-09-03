import controls.MessageBox;
import models.ConsoleKV;
import models.Devices;
import com.mrsmyx.CCAPI;
import com.mrsmyx.core.CCAPIException;
import com.mrsmyx.models.ConsoleInfo;
import utils.Network;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

public class JControlConsole extends Application implements EventHandler<ActionEvent>, Network.NetworkListener {

    private ComboBox<CCAPI.BuzzerType> buzzerTypeComboBox;
    private ComboBox<CCAPI.ShutdownMode> shutdownModeComboBox;
    private ComboBox<CCAPI.LedColor> ledColorComboBox;
    private ComboBox<CCAPI.LedStatus> ledStatusComboBox;
    private ComboBox<CCAPI.NotifyIcon> notifyIconComboBox;
    private TextField idps_edit_text, psid_edit_text, notify_edit_text;
    private TableView<Devices> console_list;
    private TableView<ConsoleKV> console_info_list;
    private Button reset_idps_btn, reset_psid_btn, set_idps_btn, set_psid_btn,
            set_boot_idps_btn, set_boot_psid_btn, find_console_btn,
            notify_btn, shut_btn, led_btn, buzz_btn;
    private CheckBox gta_rtm_godmode, gta_rtm_neverwanted, gta_rtm_unlammo, gta_rtm_cardon, gta_rtm_superjump;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("CCAPI Manager Linux + RTM (Created By Mr Smithy x)");
        primaryStage.setScene(new Scene(root));
        init(primaryStage.getScene());
        attach();
        fill();
        primaryStage.show();
    }

    private void fill() {
        shutdownModeComboBox.getItems().addAll(CCAPI.ShutdownMode.values());
        ledColorComboBox.getItems().addAll(CCAPI.LedColor.values());
        ledStatusComboBox.getItems().addAll(CCAPI.LedStatus.values());
        buzzerTypeComboBox.getItems().addAll(CCAPI.BuzzerType.values());
        notifyIconComboBox.getItems().addAll(CCAPI.NotifyIcon.values());
        notifyIconComboBox.getSelectionModel().select(0);
        buzzerTypeComboBox.getSelectionModel().select(0);
        ledStatusComboBox.getSelectionModel().select(0);
        shutdownModeComboBox.getSelectionModel().select(0);
        ledColorComboBox.getSelectionModel().select(0);
    }


    private void attach() {
        notify_btn.setOnAction(this);
        buzz_btn.setOnAction(this);
        find_console_btn.setOnAction(this);
        led_btn.setOnAction(this);
        shut_btn.setOnAction(this);
        set_psid_btn.setOnAction(this);
        set_idps_btn.setOnAction(this);
        set_boot_psid_btn.setOnAction(this);
        set_boot_idps_btn.setOnAction(this);
        reset_idps_btn.setOnAction(this);
        reset_psid_btn.setOnAction(this);
        gta_rtm_cardon.setOnAction(gta_checkbox);
        gta_rtm_godmode.setOnAction(gta_checkbox);
        gta_rtm_unlammo.setOnAction(gta_checkbox);
        gta_rtm_neverwanted.setOnAction(gta_checkbox);
        gta_rtm_superjump.setOnAction(gta_checkbox);
    }

    EventHandler<ActionEvent> gta_checkbox = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (ccapi == null) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // Update/Query the FX classes here
                        MessageBox.Builder(Alert.AlertType.ERROR, "Not Connected", "Connect to the ps3 first").show();
                    }
                });
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!ccapi.isProcessAttached()) {
                        try {
                            if (!ccapi.attach()) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Update/Query the FX classes here
                                        MessageBox.Builder(Alert.AlertType.ERROR, "Error", "Could not find process").show();
                                    }
                                });
                                return;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        if (event.getSource() == gta_rtm_godmode) {
                            if (gta_rtm_godmode.isSelected()) {
                                ccapi.setMemory(0x118A548, new char[]{0x38, 0x60, 0x7F, 0xFF, 0xB0, 0x7F, 0x00, 0xB4});
                                ccapi.notify(CCAPI.NotifyIcon.INFO, "Mr Smithy x (JCCAPILib): Godmode Enabled");
                            } else {
                                ccapi.setMemory(0x118A548, new char[]{0xFC, 0x01, 0x10, 0x00, 0x41, 0x80, 0x01, 0x14});
                                ccapi.notify(CCAPI.NotifyIcon.INFO, "Mr Smithy x (JCCAPILib): Godmode Disabled");
                            }
                        } else if (event.getSource() == gta_rtm_neverwanted) {
                            if (gta_rtm_neverwanted.isSelected()) {
                                ccapi.setMemory(0x150877C, new char[]{0x39, 0x60, 0x00, 0x00});
                                ccapi.notify(CCAPI.NotifyIcon.INFO, "Mr Smithy x (JCCAPILib): Never Wanted Enabled");
                            } else {
                                ccapi.setMemory(0x150877C, new char[]{0x81, 0x7d, 0x00, 0x04});
                                ccapi.notify(CCAPI.NotifyIcon.INFO, "Mr Smithy x (JCCAPILib): Never Wanted Disabled");
                            }
                        } else if (event.getSource() == gta_rtm_cardon) {
                            if (gta_rtm_cardon.isSelected()) {
                                ccapi.setMemory(0x18A65C4, new char[]{0x40, 0x00, 0x00, 0x00});
                                ccapi.notify(CCAPI.NotifyIcon.INFO, "Mr Smithy x (JCCAPILib): Carmageddon Enabled");
                            } else {
                                ccapi.setMemory(0x18A65C4, new char[]{0x3F, 0x80, 0x00, 0x00});
                                ccapi.notify(CCAPI.NotifyIcon.INFO, "Mr Smithy x (JCCAPILib): Carmageddon Disabled");
                            }
                        } else if (event.getSource() == gta_rtm_superjump) {
                            if (gta_rtm_superjump.isSelected()) {
                                ccapi.setMemory(0x5EB040, new char[]{0x60, 0x00, 0x00, 0x00});
                                ccapi.notify(CCAPI.NotifyIcon.INFO, "Mr Smithy x (JCCAPILib): Super Jump Enabled");
                            } else {
                                ccapi.setMemory(0x5EB040, new char[]{0x41, 0x82, 0x00, 0x10});
                                ccapi.notify(CCAPI.NotifyIcon.INFO, "Mr Smithy x (JCCAPILib): Super Jump Disabled");
                            }
                        } else if (event.getSource() == gta_rtm_unlammo) {
                            if (gta_rtm_unlammo.isSelected()) {
                                ccapi.setMemory(0xFD3904, new char[]{0x3b, 0xa0, 0x03, 0xe7});
                                ccapi.setMemory(0xFDEC80, new char[]{0x38, 0xe0, 0x00, 0x63});
                                ccapi.notify(CCAPI.NotifyIcon.INFO, "Mr Smithy x (JCCAPILib): Unlimited Ammo Enabled");
                            } else {
                                ccapi.setMemory(0xFD3904, new char[]{0x63, 0xFD, 0x00, 0x00});
                                ccapi.setMemory(0xFDEC80, new char[]{0x60, 0x87, 0x00, 0x00});
                                ccapi.notify(CCAPI.NotifyIcon.INFO, "Mr Smithy x (JCCAPILib): Unlimited Ammo Disabled");
                            }
                        }
                    } catch (Exception | CCAPIException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        }
    };

    private void init(Scene scene) {
        reset_idps_btn = (Button) scene.lookup("#reset_idps_btn");
        reset_psid_btn = (Button) scene.lookup("#reset_psid_btn");
        set_idps_btn = (Button) scene.lookup("#set_idps_btn");
        set_psid_btn = (Button) scene.lookup("#set_psid_btn");
        set_boot_idps_btn = (Button) scene.lookup("#set_boot_idps_btn");
        set_boot_psid_btn = (Button) scene.lookup("#set_boot_psid_btn");
        find_console_btn = (Button) scene.lookup("#find_console_btn");
        notify_btn = (Button) scene.lookup("#notify_btn");
        shut_btn = (Button) scene.lookup("#shut_btn");
        led_btn = (Button) scene.lookup("#led_btn");
        buzz_btn = (Button) scene.lookup("#buzzer_btn");
        console_list = (TableView<Devices>) scene.lookup("#console_list");
        notifyIconComboBox = (ComboBox<CCAPI.NotifyIcon>) scene.lookup("#icon_combo");
        buzzerTypeComboBox = (ComboBox<CCAPI.BuzzerType>) scene.lookup("#buzzer_combo");
        ledColorComboBox = (ComboBox<CCAPI.LedColor>) scene.lookup("#led_color_combo");
        ledStatusComboBox = (ComboBox<CCAPI.LedStatus>) scene.lookup("#led_status_combo");
        shutdownModeComboBox = (ComboBox<CCAPI.ShutdownMode>) scene.lookup("#shut_combo");
        idps_edit_text = (TextField) scene.lookup("#idps_edit_text");
        psid_edit_text = (TextField) scene.lookup("#psid_edit_text");
        notify_edit_text = (TextField) scene.lookup("#notify_edit_text");
        console_info_list = (TableView<ConsoleKV>) scene.lookup("#console_info_list");
        gta_rtm_godmode = (CheckBox) scene.lookup("#gta_rtm_godmode");
        gta_rtm_cardon = (CheckBox) scene.lookup("#gta_rtm_cardon");
        gta_rtm_neverwanted = (CheckBox) scene.lookup("#gta_rtm_neverwanted");
        gta_rtm_superjump = (CheckBox) scene.lookup("#gta_rtm_superjump");
        gta_rtm_unlammo = (CheckBox) scene.lookup("#gta_rtm_unlammo");
        Hyperlink hyperlink = (Hyperlink)scene.lookup("#hyperlink_http");
        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getHostServices().showDocument("http://www.nextgenupdate.com/forums/members/1138878-mr-smithy-x.html");
            }
        });
        MenuItem connect = new MenuItem("Connect");
        connect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Devices selectedItem = console_list.getSelectionModel().getSelectedItem();
                        if (selectedItem != null) {
                            ccapi = new CCAPI(selectedItem.getConsoleIP());
                            try {
                                ccapi.notify(CCAPI.NotifyIcon.ARROW, "Mr Smithy x: Connected!");
                                ccapi.ringBuzzer(CCAPI.BuzzerType.SINGLE);
                                grabConsoleInfo();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
        console_list.getContextMenu().getItems().add(connect);
        MenuItem menuItem = new MenuItem("Refresh");
        menuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        grabConsoleInfo();
                    }
                }).start();
            }
        });
        console_info_list.getContextMenu().getItems().add(menuItem);
    }

    private void grabConsoleInfo() {
        if (ccapi != null) {
            try {
                ConsoleInfo consoleInfo = ccapi.getConsoleInfo();
                System.out.println(consoleInfo.getFirmware());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (console_info_list.getItems().size() > 0) console_info_list.getItems().clear();
                        console_info_list.getItems().addAll(ConsoleKV.Builder("Console", consoleInfo.getFirmware()),
                                ConsoleKV.Builder("Type", consoleInfo.getType().name()),
                                ConsoleKV.Builder("Celsius", consoleInfo.getRsxC()),
                                ConsoleKV.Builder("Fahren.", consoleInfo.getRsxF()),
                                ConsoleKV.Builder("Celsius", consoleInfo.getCellC()),
                                ConsoleKV.Builder("Fahren.", consoleInfo.getCellF())
                        );

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private CCAPI ccapi;

    @Override
    public void handle(ActionEvent event) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (event.getSource() == find_console_btn) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                if (console_list.getItems() != null) console_list.getItems().clear();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Network.printReachableHosts(Network.getWLANipAddress("IPv4"), JControlConsole.this);
                                        } catch (SocketException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                    } else if (ccapi == null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                MessageBox.Builder(Alert.AlertType.ERROR,"Error", "Connect to the ps3 first").show();
                            }
                        });return;
                    }
                    else if (event.getSource() == notify_btn) {
                        ccapi.notify(notifyIconComboBox.getSelectionModel().getSelectedItem(), notify_edit_text.getText());
                    } else if (event.getSource() == buzz_btn) {
                        ccapi.ringBuzzer(buzzerTypeComboBox.getSelectionModel().getSelectedItem());
                    } else if (event.getSource() == led_btn) {
                        ccapi.setConsoleLed(ledColorComboBox.getSelectionModel().getSelectedItem(), ledStatusComboBox.getSelectionModel().getSelectedItem());
                    } else if (event.getSource() == shut_btn) {
                        ccapi.shutDown(shutdownModeComboBox.getSelectionModel().getSelectedItem());
                    } else if (event.getSource() == reset_idps_btn) {
                        ccapi.setBootConsoleIds(CCAPI.ConsoleIdType.IDPS, false, "00000000000000000000000000000000");
                        ccapi.notify(CCAPI.NotifyIcon.INFO, "IDPS reset");
                    } else if (event.getSource() == reset_psid_btn) {
                        ccapi.setBootConsoleIds(CCAPI.ConsoleIdType.PSID, false, "00000000000000000000000000000000");
                        ccapi.notify(CCAPI.NotifyIcon.INFO, "PSID reset");
                    } else if (event.getSource() == set_boot_idps_btn) {
                        if (idps_edit_text.getText().length() == 32) {
                            ccapi.setBootConsoleIds(CCAPI.ConsoleIdType.IDPS, true, idps_edit_text.getText());
                            ccapi.notify(CCAPI.NotifyIcon.INFO, "Boot IDPS set");
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    MessageBox.Builder(Alert.AlertType.ERROR, "Error", "IDPS is suppose to be 32 characters long").show();
                                }
                            });
                        }
                    } else if (event.getSource() == set_boot_psid_btn) {
                        if (psid_edit_text.getText().length() == 32) {
                            ccapi.setBootConsoleIds(CCAPI.ConsoleIdType.PSID, false, psid_edit_text.getText());
                            ccapi.notify(CCAPI.NotifyIcon.INFO, "Boot PSID set");
                        } else
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    MessageBox.Builder(Alert.AlertType.ERROR, "Error", "PSID is suppose to be 32 characters long").show();
                                }
                            });
                    } else if (event.getSource() == set_idps_btn) {
                        if (idps_edit_text.getText().length() == 32) {
                            ccapi.setConsoleIds(CCAPI.ConsoleIdType.IDPS, idps_edit_text.getText());
                            ccapi.notify(CCAPI.NotifyIcon.INFO, "IDPS set");
                        } else
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    MessageBox.Builder(Alert.AlertType.ERROR, "Error", "IDPS is suppose to be 32 characters long").show();
                                }
                            });
                    } else if (event.getSource() == set_psid_btn) {
                        if (psid_edit_text.getText().length() == 32) {
                            ccapi.setConsoleIds(CCAPI.ConsoleIdType.PSID, psid_edit_text.getText());
                            ccapi.notify(CCAPI.NotifyIcon.INFO, "PSID set");
                        } else
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    MessageBox.Builder(Alert.AlertType.ERROR, "Error", "PSID is suppose to be 32 characters long").show();
                                }
                            });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void OnNetworkFound(InetAddress byName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (!console_list.getItems().contains(Devices.Builder().setConsoleIP(byName.getHostAddress()))) {
                    console_list.getItems().add(Devices.Builder().setConsoleIP(byName.getHostAddress()));
                }
            }
        });
    }

    @Override
    public void OnNetworkFail(String response) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MessageBox.Builder(Alert.AlertType.INFORMATION, "Response", response).show();
            }});
    }
}
