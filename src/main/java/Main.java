import front.FraudingerApplication;
import ihm.controller.ScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception{
		Parent root = FXMLLoader.load(getClass().getResource("ihm/fxml/root.fxml"));
		Scene rootScene = new Scene(root, 400, 300);
		primaryStage.setTitle("Fraudinger");
		primaryStage.setResizable(false);
		primaryStage.setScene(rootScene);
		setupScenes(rootScene);
		primaryStage.show();
	}

	private void setupScenes(Scene rootScene) throws IOException {
		ScreenController.newInstance(rootScene);
		ScreenController.getInstance().addScreen("fileChooser", FXMLLoader.load(getClass().getResource("ihm/fxml/fileChooser.fxml")));
		ScreenController.getInstance().addScreen("settings", FXMLLoader.load(getClass().getResource("ihm/fxml/settings.fxml")));
		ScreenController.getInstance().activate("fileChooser");
	}

	public static void main(String[] args) {
		if (args.length > 0 && "-ui".equals(args[0])) {
			launch(args);
		} else {
			FraudingerApplication fraudingerApplication = new FraudingerApplication();
			fraudingerApplication.launch();
		}
	}


}
