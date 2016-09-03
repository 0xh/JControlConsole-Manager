package controls;

import javafx.scene.control.Alert;

/**
 * Created by cj on 1/16/16.
 */
public class MessageBox {
    public static Alert Builder(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);

        alert.setContentText(content);
        return alert;
    }
}