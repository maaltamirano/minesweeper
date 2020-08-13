package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

import models.*;

public class GameController {
    private Game game;
    private boolean mouseDrag;
    private boolean overField;
    private boolean overRestartButton;
    private int[][] neighbours = {
            {-1, -1},
            {-1, 0},
            {-1, 1},
            {0, -1},
            {0, 1},
            {1, -1},
            {1, 0},
            {1, 1}
    };
    private Image restart = new Image(getClass().getResource("/images/2x/restart.png").toExternalForm());
    private Image restartO = new Image(getClass().getResource("/images/2x/restartO.png").toExternalForm());
    private Image restartPressed = new Image(getClass().getResource("/images/2x/pressedRestart.png").toExternalForm());
    private Image won = new Image(getClass().getResource("/images/2x/won.png").toExternalForm());
    private Image lost = new Image(getClass().getResource("/images/2x/lost.png").toExternalForm());
    private Image field = new Image(getClass().getResource("/images/2x/field.png").toExternalForm());
    private Image flag = new Image(getClass().getResource("/images/2x/flag.png").toExternalForm());
    private Image wrongFlag = new Image(getClass().getResource("/images/2x/wrongFlag.png").toExternalForm());
    private Image mine = new Image(getClass().getResource("/images/2x/mine.png").toExternalForm());
    private Image detonatedMine = new Image(getClass().getResource("/images/2x/detonatedMine.png").toExternalForm());
    private Image[] fields = {
            new Image(getClass().getResource("/images/2x/0.png").toExternalForm()),
            new Image(getClass().getResource("/images/2x/1.png").toExternalForm()),
            new Image(getClass().getResource("/images/2x/2.png").toExternalForm()),
            new Image(getClass().getResource("/images/2x/3.png").toExternalForm()),
            new Image(getClass().getResource("/images/2x/4.png").toExternalForm()),
            new Image(getClass().getResource("/images/2x/5.png").toExternalForm()),
            new Image(getClass().getResource("/images/2x/6.png").toExternalForm()),
            new Image(getClass().getResource("/images/2x/7.png").toExternalForm()),
            new Image(getClass().getResource("/images/2x/8.png").toExternalForm())
    };
    private Image[] digits = {
            new Image(getClass().getResource("/images/digits/0.png").toExternalForm()),
            new Image(getClass().getResource("/images/digits/1.png").toExternalForm()),
            new Image(getClass().getResource("/images/digits/2.png").toExternalForm()),
            new Image(getClass().getResource("/images/digits/3.png").toExternalForm()),
            new Image(getClass().getResource("/images/digits/4.png").toExternalForm()),
            new Image(getClass().getResource("/images/digits/5.png").toExternalForm()),
            new Image(getClass().getResource("/images/digits/6.png").toExternalForm()),
            new Image(getClass().getResource("/images/digits/7.png").toExternalForm()),
            new Image(getClass().getResource("/images/digits/8.png").toExternalForm()),
            new Image(getClass().getResource("/images/digits/9.png").toExternalForm())
    };

    @FXML
    VBox gameVBox;

    @FXML
    GridPane gameGridPane;

    @FXML
    ImageView restartButton;

    @FXML
    ImageView flagsDigit1;
    @FXML
    ImageView flagsDigit2;
    @FXML
    ImageView flagsDigit3;

    @FXML
    ImageView timerDigit1;
    @FXML
    ImageView timerDigit2;
    @FXML
    ImageView timerDigit3;

    private Timeline timer;

    public void initialize() {
        gameVBox.addEventFilter(MouseEvent.DRAG_DETECTED , mouseEvent -> gameVBox.startFullDrag());

        setRestartButtonFunctions();

        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimer()));
        timer.setCycleCount(999);

        startBeginner();
    }

    public void startBeginner() {
        startGame(new Game(8, 8, 10));
    }

    public void startIntermediate() {
        startGame(new Game(16, 16, 40));
    }

    public void startExpert() {
        startGame(new Game(30, 16, 99));
    }

    public void startCustom() {
        Game game = showCustomGameDialogue();
        if (game != null) {
            startGame(game);
        }
    }

    private Game showCustomGameDialogue() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/customGameDialogue.fxml"));
            GridPane root = loader.load();

            CustomGameController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("title");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
            return controller.getGame();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initializeGameField() {
        // Remove the already existing rows and columns of the GridPane
        gameGridPane.getChildren().removeAll(gameGridPane.getChildren());

        // Add the columns
        for (int i = 0; i < game.getWidth(); i++) {
            gameGridPane.addColumn(i);
        }

        // Add the rows
        for (int i = 0; i < game.getHeight(); i++) {
            gameGridPane.addRow(i);
        }

        // Initialize the fields
        for (int x = 0; x < game.getWidth(); x++) {
            for (int y = 0; y < game.getHeight(); y++) {
                initializeField(x, y);
                gameGridPane.add(game.getField(x, y).getImageView(), x, y);
            }
        }

        updateFlagsLeft();
        mouseDrag = false;
        overField = false;
        overRestartButton = false;
    }

    private void initializeField(int x, int y) {
        game.setField(new Field(), x, y);
        game.getField(x, y).setImageView(new ImageView(field));
        // Ist benötigt damit immer das Feld auf dem die Maus gerade ist
        game.getField(x, y).getImageView().setOnMouseDragEntered((EventHandler<MouseEvent>) event -> {
            if (game.isRunning() && game.getField(x, y).getFieldState() == State.COVERED && event.getButton() == MouseButton.PRIMARY) {
                restartButton.setImage(restartO);
                game.getField(x, y).getImageView().setImage(fields[0]);
            }
            overField = true;
            mouseDrag = true;
        });
        // Ist benötigt um das Feld welches man gerade verlassen hat wieder zuzudecken
        game.getField(x, y).getImageView().setOnMouseDragExited((EventHandler<MouseEvent>) event -> {
            if (game.isRunning() && game.getField(x, y).getFieldState() == State.COVERED && event.getButton() == MouseButton.PRIMARY) {
                restartButton.setImage(restart);
                game.getField(x, y).getImageView().setImage(field);
            }
            overField = false;
            mouseDrag = false;
        });
        // Ist benötigt weil setOnMouseDrag nicht funktioniert wenn man die Maus nur um einen Pixel bewegt
        game.getField(x, y).getImageView().setOnMouseExited(event -> {
            if (game.isRunning() && game.getField(x, y).getFieldState() == State.COVERED && event.getButton() == MouseButton.PRIMARY) {
                game.getField(x, y).getImageView().setImage(field);
            }
            overField = false;
        });
        game.getField(x, y).getImageView().setOnMouseEntered(event -> overField = true);
        // Ist benötigt weil setOnMouseDragEntered erst aktiviert wird wenn man die Maus bewegt
        game.getField(x, y).getImageView().setOnMousePressed(event -> {
            overField = true;
            if (game.isRunning() && game.getField(x, y).getFieldState() == State.COVERED && event.getButton() == MouseButton.PRIMARY) {
                restartButton.setImage(restartO);
                game.getField(x, y).getImageView().setImage(fields[0]);
            }
        });
        // Ist benötigt falls man nur auf ein Feld klickt ohne die Maus zu bewegen
        game.getField(x, y).getImageView().setOnMouseReleased(event -> {
            if (game.isRunning()) {
                restartButton.setImage(restart);
                if (overField) {
                    if (game.isFirstClick() && event.getButton() == MouseButton.PRIMARY && game.getField(x, y).getFieldState() == State.COVERED) {
                        firstClick(x, y);
                        game.setFirstClick(false);
                    }
                    if (!mouseDrag) {
                        mouseEventHandler(event.getButton(), x, y);
                    }
                }
                mouseDrag = false;
            }
        });
        // Ist benötigt falls man die Maus bewegt und dann loslässt
        game.getField(x, y).getImageView().setOnMouseDragReleased(event -> {
            if (overField) {
                if (game.isFirstClick() && event.getButton() == MouseButton.PRIMARY && game.getField(x, y).getFieldState() == State.COVERED) {
                    firstClick(x, y);
                    game.setFirstClick(false);
                }
                mouseEventHandler(event.getButton(), x, y);
            }
        });
    }

    private void setRestartButtonFunctions() {
        restartButton.setOnMouseExited(event -> {
            if (game.isRunning()) {
                overRestartButton = false;
                restartButton.setImage(restart);
            }
        });
        restartButton.setOnMouseEntered(event -> overRestartButton = true);
        restartButton.setOnMousePressed(event -> {
            if (overRestartButton) {
                restartButton.setImage(restartPressed);
            }
        });
        restartButton.setOnMouseReleased(event -> {
            restartButton.setImage(restart);
            if (overRestartButton) {
                startGame(new Game(game.getWidth(), game.getHeight(), game.getMines()));
            }
        });
        restartButton.setOnMouseDragReleased(event -> {
            restartButton.setImage(restart);
            if (overRestartButton) {
                startGame(new Game(game.getWidth(), game.getHeight(), game.getMines()));
            }
        });
        restartButton.setOnMouseDragEntered(event -> {
            restartButton.setImage(restartPressed);
            overRestartButton = true;
        });
        restartButton.setOnMouseDragExited(event -> {
            restartButton.setImage(restart);
            overRestartButton = false;
        });

        gameVBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.R) {
                restartButton.setImage(restartPressed);
            }
        });
        gameVBox.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.R) {
                restartButton.setImage(restart);
                startGame(new Game(game.getWidth(), game.getHeight(), game.getMines()));
            }
        });
    }

    private void firstClick(int x, int y) {
        setMines(x, y);
        timer.play();
    }

    private void startGame(Game game) {
        this.game = game;
        timer.stop();
        timerDigit1.setImage(digits[0]);
        timerDigit2.setImage(digits[0]);
        timerDigit3.setImage(digits[0]);
        initializeGameField();
    }

    private void setMines(int mouseX, int mouseY) {
        Random random = new Random();
        int x;
        int y;

        for (int i = 0; i < game.getMines(); i++) {
            do {
                x = random.nextInt(game.getWidth());
                y = random.nextInt(game.getHeight());
            } while (!isAValidMineLocation(x, y, mouseX, mouseY));
            game.getField(x, y).setMine(true);
            for (int[] neighbour : neighbours) {
                try {
                    game.getField(x + neighbour[0], y + neighbour[1]).increaseNumberOfNeighboursThatAreMines();
                } catch (ArrayIndexOutOfBoundsException ignore) {}
            }
        }
    }

    private boolean isAValidMineLocation(int x, int y, int mouseX, int mouseY) {
        if (game.getField(x, y).isMine()) {
            return false;
        }

        if (x == mouseX && y == mouseY) {
            return false;
        }

        if (game.getFieldsToUncover() < 9) {
            return true;
        }

        for (int[] neighbour : neighbours) {
            try {
                if (x == mouseX + neighbour[0] && y == mouseY + neighbour[1]) {
                    return false;
                }
            } catch (ArrayIndexOutOfBoundsException ignore) {}
        }
        return true;
    }

    private void mouseEventHandler(MouseButton button, int x, int y) {
        if (game.isRunning()) {
            switch (button) {
                case PRIMARY:
                    uncover(x, y);
                    break;
                case SECONDARY:
                    flag(x, y);
                    break;
                case MIDDLE:
                    uncoverNeighbours(x, y);
                    break;
                default:
            }
        }
    }

    private void uncover(int x, int y) {
        if (game.getField(x, y).getFieldState() == State.COVERED) {
            game.getField(x, y).setFieldState(State.UNCOVERED);
            if (game.getField(x, y).isMine()) {
                lose();
                game.getField(x, y).getImageView().setImage(detonatedMine);
            } else {
                game.getField(x, y).getImageView().setImage(fields[game.getField(x, y).getNumberOfNeighboursThatAreMines()]);
                game.decreaseFieldsToUncover();
                if (game.getField(x, y).getNumberOfNeighboursThatAreMines() == 0) {
                    uncoverNeighbours(x, y);
                }
            }
        }

        if (game.getFieldsToUncover() == 0) {
            win();
        }
    }

    private void flag(int x, int y) {
        if (game.getField(x, y).getFieldState() == State.COVERED) {
            game.getField(x, y).getImageView().setImage(flag);
            game.getField(x, y).setFieldState(State.FLAGGED);
            game.decreaseFlagsLeft();
        } else if (game.getField(x, y).getFieldState() == State.FLAGGED) {
            game.getField(x, y).getImageView().setImage(field);
            game.getField(x, y).setFieldState(State.COVERED);
            game.increaseFlagsLeft();
        }
        updateFlagsLeft();
    }

    private void uncoverNeighbours(int x, int y) {
        if (countFlaggedNeighbours(x, y) == game.getField(x, y).getNumberOfNeighboursThatAreMines() && game.getField(x, y).getFieldState() == State.UNCOVERED) {
            for (int[] neighbour : neighbours) {
                try {
                    uncover(x + neighbour[0], y + neighbour[1]);
                } catch (ArrayIndexOutOfBoundsException ignore) {}
            }
        }
    }

    private int countFlaggedNeighbours(int x, int y) {
        int amount = 0;
        for (int[] neighbour : neighbours) {
            try {
                if (game.getField(x + neighbour[0], y + neighbour[1]).getFieldState() == State.FLAGGED) {
                    amount++;
                }
            } catch (ArrayIndexOutOfBoundsException ignore) {}
        }
        return amount;
    }

    private void win() {
        timer.stop();
        for (int x = 0; x < game.getWidth(); x++) {
            for (int y = 0; y < game.getHeight(); y++) {
                if (game.getField(x, y).isMine() && game.getField(x, y).getFieldState() == State.COVERED) {
                    flag(x, y);
                }
            }
        }

        game.setRunning(false);
        restartButton.setImage(won);
    }

    private void lose() {
        timer.stop();
        for (int x = 0; x < game.getWidth(); x++) {
            for (int y = 0; y < game.getHeight(); y++) {
                if (game.getField(x, y).isMine() && game.getField(x, y).getFieldState() == State.COVERED) {
                    game.getField(x, y).getImageView().setImage(mine);
                } else if (!game.getField(x, y).isMine() && game.getField(x, y).getFieldState() == State.FLAGGED) {
                    game.getField(x, y).getImageView().setImage(wrongFlag);
                }
            }
        }

        game.setRunning(false);
        restartButton.setImage(lost);
    }

    private void updateFlagsLeft() {
        updateDigits(flagsDigit1, flagsDigit2, flagsDigit3, game.getFlagsLeft());
    }

    private void updateTimer() {
        game.increaseTime();
        updateDigits(timerDigit1, timerDigit2, timerDigit3, game.getTime());
    }

    private void updateDigits(ImageView imageView1, ImageView imageView2, ImageView imageView3, int data) {
        try {
            imageView1.setImage(digits[(data / 100) % 10]);
            imageView2.setImage(digits[(data / 10) % 10]);
            imageView3.setImage(digits[data % 10]);
        } catch (ArrayIndexOutOfBoundsException ignore) {}
    }
}