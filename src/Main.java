import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: When increasing window size menu bar doesn't change
        VBox root = FXMLLoader.load(getClass().getResource("views/gameView.fxml"));
        Scene scene = new Scene(root);
        primaryStage.getIcons().add(new Image(getClass().getResource("/images/4x/restart.png").toExternalForm()));

        ((GridPane) ((VBox) root.getChildren().get(1)).getChildren().get(1)).heightProperty().addListener(observable -> {
            Window window = scene.getWindow();

            if (window != null) {
                window.sizeToScene();
            }
        });

        primaryStage.setTitle("Minesweeper");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}