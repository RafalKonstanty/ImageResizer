import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.scene.paint.Color.TRANSPARENT;

public class MainView extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MainView.fxml"));
        Scene scene = new Scene(root);
        scene.setFill(TRANSPARENT);
        primaryStage.setTitle("ImageResizer - R.K");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }
}
