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
        boolean isValid = false;
        int width = 0;
        int height = 0;
        int mines = 0;

        try {
            width = Integer.parseInt(this.width.getText());
            height = Integer.parseInt(this.height.getText());
            mines = Integer.parseInt(this.mines.getText());
            if (mines < width * height) {
                if (mines > 0 && width > 0 && height > 0) {
                    isValid = true;
                } else {
                    error.setText("Invalid number");
                }
            } else {
                error.setText("Too many mines");
            }
        } catch (NumberFormatException n) {
            error.setText("Not a number");
        }

        if (isValid) {
            error.setText("");
            game = new Game(width, height, mines);
            ((Stage) this.width.getScene().getWindow()).close();
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