package models;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by cj on 1/16/16.
 */
public class Devices {

    public static Devices Builder(){
        return new Devices();
    }

    public String getConsoleIP() {
        return ConsoleIP.get();
    }

    public SimpleStringProperty consoleIPProperty() {
        return ConsoleIP;
    }

    public Devices setConsoleIP(String consoleIP) {
        ConsoleIP = new SimpleStringProperty("<ConsoleIP>");
        this.ConsoleIP.set(consoleIP);
        return this;
    }

    private SimpleStringProperty ConsoleIP;

}
