package front.ihm.settings;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FilterCheckboxItem {
    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty on = new SimpleBooleanProperty();
    private Class filterClass;

    public FilterCheckboxItem(Class filterClass, String name, boolean on) {
        this.filterClass = filterClass;
        setName(name);
        setOn(on);
    }

    public Class getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class filterClass) {
        this.filterClass = filterClass;
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
