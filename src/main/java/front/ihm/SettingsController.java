package front.ihm;

import filter.FilterFactory;
import front.ihm.settings.FilterCheckboxItem;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.List;


public class SettingsController {

    @FXML
    private ListView filters;

    @FXML
    public void initialize() {
        setupFiltersListView();
    }

    public void setupFiltersListView() {
        FilterFactory filterFactory = FilterFactory.getInstance();
        List<Class> activatedFilters = filterFactory.getActivatedFilters();
        HashMap<Class,String> availableFilters = filterFactory.getAvailableFilters();
        for (Class filterClass : availableFilters.keySet()) {
            FilterCheckboxItem item = new FilterCheckboxItem(availableFilters.get(filterClass) + "(" + filterClass.getName() + ")", activatedFilters.contains(filterClass));
            item.onProperty().addListener((obs, wasOn, isNowOn) -> {
                // Change
            });
            filters.getItems().add(item);
        }

        filters.setCellFactory(CheckBoxListCell.forListView(new Callback<FilterCheckboxItem, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(FilterCheckboxItem item) {
                return item.onProperty();
            }
        }));
    }


    @FXML
    public void back(ActionEvent actionEvent) {
        ScreenController.getInstance().activate("fileChooser");
    }
}
