package front.ihm;

import calculator.CalculatorCommandFactory;
import calculator.IFraudCalculatorCommand;
import filter.FilterFactory;
import front.ihm.settings.CheckboxItem;
import javafx.beans.value.ObservableValue;
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
    private ListView commands;

    @FXML
    public void initialize() {
        setupFiltersListView();
        setupCommandsListView();
    }

    public void setupFiltersListView() {
        FilterFactory filterFactory = FilterFactory.getInstance();
        List<Class> activatedFilters = filterFactory.getActivatedFilters();
        HashMap<Class,String> availableFilters = filterFactory.getAvailableFilters();
        for (Class filterClass : availableFilters.keySet()) {
            CheckboxItem item = new CheckboxItem(filterClass,availableFilters.get(filterClass) + "(" + filterClass.getName() + ")", activatedFilters.contains(filterClass));
            item.onProperty().addListener((obs, wasOn, isNowOn) -> {
                if (isNowOn) {
                    activatedFilters.add((Class) item.getContent());
                } else {
                    activatedFilters.remove(item.getContent());
                }
            });
            filters.getItems().add(item);
        }

        filters.setCellFactory(CheckBoxListCell.forListView(new Callback<CheckboxItem, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(CheckboxItem item) {
                return item.onProperty();
            }
        }));
    }

    public void setupCommandsListView() {
        CalculatorCommandFactory commandFactory = CalculatorCommandFactory.init();
        HashMap<String, IFraudCalculatorCommand> activatedCommands = commandFactory.getCommands();
        HashMap<String,IFraudCalculatorCommand> availableCommands = commandFactory.getAvailableCommands();
        for (String commandName : availableCommands.keySet()) {
            CheckboxItem item = new CheckboxItem(commandName, activatedCommands.keySet().contains(commandName));
            item.onProperty().addListener((obs, wasOn, isNowOn) -> {
                if (isNowOn) {
                    commandFactory.addCommand(item.getName(),availableCommands.get(item.getName()));
                } else {
                    commandFactory.removeCommand(item.getName());
                }
            });
            commands.getItems().add(item);
        }

        commands.setCellFactory(CheckBoxListCell.forListView(new Callback<CheckboxItem, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(CheckboxItem item) {
                return item.onProperty();
            }
        }));
    }


    @FXML
    public void back(ActionEvent actionEvent) {
        ScreenController.getInstance().activate("fileChooser");
    }
}
