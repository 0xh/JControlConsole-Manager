package models;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by cj on 1/16/16.
 */
public class ConsoleKV {
    public ConsoleKV setKey(String key) {
        this.Ckey.set(key);
        return this;
    }
    public ConsoleKV setValue(String value) {
        this.Cval.set(value);
        return this;
    }

    public static ConsoleKV Builder(String key, String val){
        ConsoleKV consoleKV = new ConsoleKV();
        consoleKV.Ckey = new SimpleStringProperty("<Ckey>");
        consoleKV.Cval = new SimpleStringProperty("<Cval>");
        consoleKV.setKey(key).setValue(val);
        return consoleKV;
    }

    public String getCkey() {
        return Ckey.get();
    }

    public SimpleStringProperty ckeyProperty() {
        return Ckey;
    }

    public void setCkey(String ckey) {
        this.Ckey.set(ckey);
    }

    public String getCval() {
        return Cval.get();
    }

    public SimpleStringProperty cvalProperty() {
        return Cval;
    }

    public void setCval(String cval) {
        this.Cval.set(cval);
    }

    private SimpleStringProperty Ckey,Cval;
}
