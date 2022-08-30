package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Game;

public class CustomGameController {

    private Game game;

    @FXML
    TextField width;

    @FXML
    TextField height;

    @FXML
    TextField mines;

    @FXML
    Text error;

    public void start() {
        try {
            int width = Integer.parseInt(this.width.getText());
            int height = Integer.parseInt(this.height.getText());
            int mines = Integer.parseInt(this.mines.getText());
            if (mines < width * height) {
                if (mines > 0 && width > 0 && height > 0) {
                    error.setText("");
                    game = new Game(width, height, mines);
                    ((Stage) this.width.getScene().getWindow()).close();
                } else {
                    error.setText("Invalid number");
                }
            } else {
                error.setText("Too many mines");
            }
        } catch (NumberFormatException n) {
            error.setText("Not a number");
        }
    }

    public void cancel() {
        game = null;
        ((Stage) width.getScene().getWindow()).close();
    }

    public Game getGame() {
        return game;
    }
}