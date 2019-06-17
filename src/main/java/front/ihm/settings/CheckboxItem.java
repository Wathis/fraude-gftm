package front.ihm.settings;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;

public class CheckboxItem {
    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty on = new SimpleBooleanProperty();
    private Object content;

    public CheckboxItem(Object content, String name, boolean on) {
        this.content = content;
        setName(name);
        setOn(on);
    }
    public CheckboxItem(String name, boolean on) {
        setName(name);
        setOn(on);
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public final StringProperty nameProperty() {
        return this.name;
    }

    public final String getName() {
        return this.nameProperty().get();
    }

    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    public final BooleanProperty onProperty() {
        return this.on;
    }

    public final boolean isOn() {
        return this.onProperty().get();
    }

    public final void setOn(final boolean on) {
        this.onProperty().set(on);
    }

    @Override
    public String toString() {
        return getName();
    }


}
