import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;

public class MainController {

    @FXML
    RadioButton beginner;

    @FXML
    RadioButton intermediate;

    @FXML
    RadioButton expert;

    @FXML
    RadioButton custom;

    @FXML
    TextField width;

    @FXML
    TextField height;

    @FXML
    TextField mines;
    
    @FXML
    Text error;

    @FXML
    ToggleGroup difficulty;

    public void start() {

        Game game;
        boolean isValid = false;
        int mines = 0;
        int width = 0;
        int height = 0;

        if (beginner.isSelected()) {
            error.setText("");
            mines = 10;
            width = 8;
            height = 8;
            isValid = true;
        } else if (intermediate.isSelected()) {
            error.setText("");
            mines = 40;
            width = 16;
            height = 16;
            isValid = true;
        } else if (expert.isSelected()) {
            error.setText("");
            mines = 99;
            width = 24;
            height = 24;
            isValid = true;
        } else if (custom.isSelected()) {
            try {
                mines = Integer.parseInt(this.mines.getText());
                width = Integer.parseInt(this.width.getText());
                height = Integer.parseInt(this.height.getText());
                if (mines < width * height) {
                    if (mines > 0 && width > 0 && height > 0) {
                        isValid = true;
                    } else {
                        error.setText("Invalid number");
                    }
                } else {
                    error.setText("Too much mines");
                }
            } catch (NumberFormatException n) {
                error.setText("Not a number");
            }
        }

        if (isValid) {
            error.setText("");
            game = new Game(mines, width, height);
            game.start();
        }
    }
}