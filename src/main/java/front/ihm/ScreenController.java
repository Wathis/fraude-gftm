package front.ihm;

import exception.UninstantiatedException;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.HashMap;

public class ScreenController {

    private static ScreenController instance;

    public static ScreenController newInstance(Scene mainScene) {
        instance = new ScreenController(mainScene);
        return instance;
    }

    public static ScreenController getInstance() {
        if (instance == null) {
            throw new UninstantiatedException();
        }
        return instance;
    }

    private HashMap<String, Pane> screenMap = new HashMap<>();
    private Scene main;

    private ScreenController(Scene main) {
        this.main = main;
    }

    public void addScreen(String name, Pane pane){
        screenMap.put(name, pane);
    }

    public void removeScreen(String name){
        screenMap.remove(name);
    }

    public void activate(String name){
        main.setRoot( screenMap.get(name) );
    }
}