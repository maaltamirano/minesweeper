import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class Game {
    private Field[][] game;
    private boolean isRunning;
    private boolean mouseDrag = false;
    private boolean firstClick = true;
    private boolean overField = false;
    private boolean overRestartButton = false;
    private int mines;
    private int width;
    private int height;
    private int fieldsToUncover;
    private int flagsLeft;
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
    private int time;

    public Game(int mines, int width, int height) {
        this.mines = mines;
        this.width = width;
        this.height = height;
    }

    public void start() {
//        VBox root = new VBox();

//        Scene scene = new Scene(root);
//        scene.addEventFilter(MouseEvent.DRAG_DETECTED , mouseEvent -> scene.startFullDrag());

        initialize();

        initializeHeader();

        HBox flagsDigits = new HBox();
        flagsDigits.getChildren().addAll(flagsDigit1, flagsDigit2, flagsDigit3);
        HBox timerDigits = new HBox();
        timerDigits.getChildren().addAll(timerDigit1, timerDigit2, timerDigit3);
        BorderPane header = new BorderPane();
        header.setLeft(flagsDigits);

        updateFlagsLeft();

        header.setCenter(restartButton);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.R) {
                restartButton.setImage(restartPressed);
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.R) {
                restartButton.setImage(restart);
                restart();
            }
        });

        header.setRight(timerDigits);

        GridPane gridpane = new GridPane();
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                gridpane.add(game[x][y].getImageView(), x, y);
            }
        }

        root.getChildren().addAll(header, gridpane);

        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimer()));
        timer.setCycleCount(999);

        Stage gameStage = new Stage();
        gameStage.getIcons().add(new Image(getClass().getResource("/images/4x/restart.png").toExternalForm()));
        gameStage.setTitle("Minesweeper");
        gameStage.resizableProperty().setValue(false);
        gameStage.setScene(scene);
        gameStage.show();
    }

    private void initialize() {
        fieldsToUncover = height * width - mines;
        flagsLeft = mines;
        game = new Field[width][height];
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                initializeField(x, y);
            }
        }
        isRunning = true;
    }

    private void initializeField(int x, int y) {
        game[x][y] = new Field();
        game[x][y].setMine(false);
        game[x][y].setFieldState(State.COVERED);
        game[x][y].setAmountNeighboursThatAreMines(0);
        game[x][y].setImageView(new ImageView(field));
        // Ist benötigt damit immer das Feld auf dem die Maus gerade ist
        game[x][y].getImageView().setOnMouseDragEntered((EventHandler<MouseEvent>) event -> {
            if (isRunning && game[x][y].getFieldState() == State.COVERED && event.getButton() == MouseButton.PRIMARY) {
                restartButton.setImage(restartO);
                game[x][y].getImageView().setImage(fields[0]);
            }
            overField = true;
            mouseDrag = true;
        });
        // Ist benötigt um das Feld welches man gerade verlassen hat wieder zuzudecken
        game[x][y].getImageView().setOnMouseDragExited((EventHandler<MouseEvent>) event -> {
            if (isRunning && game[x][y].getFieldState() == State.COVERED && event.getButton() == MouseButton.PRIMARY) {
                restartButton.setImage(restart);
                game[x][y].getImageView().setImage(field);
            }
            overField = false;
            mouseDrag = false;
        });
        // Ist benötigt weil setOnMouseDrag nicht funktioniert wenn man die Maus nur um einen Pixel bewegt
        game[x][y].getImageView().setOnMouseExited(event -> {
            if (isRunning && game[x][y].getFieldState() == State.COVERED && event.getButton() == MouseButton.PRIMARY) {
                game[x][y].getImageView().setImage(field);
            }
            overField = false;
        });
        game[x][y].getImageView().setOnMouseEntered(event -> overField = true);
        // Ist benötigt weil setOnMouseDragEntered erst aktiviert wird wenn man die Maus bewegt
        game[x][y].getImageView().setOnMousePressed(event -> {
            overField = true;
            if (isRunning && game[x][y].getFieldState() == State.COVERED && event.getButton() == MouseButton.PRIMARY) {
                restartButton.setImage(restartO);
                game[x][y].getImageView().setImage(fields[0]);
            }
        });
        // Ist benötigt falls man nur auf ein Feld klickt ohne die Maus zu bewegen
        game[x][y].getImageView().setOnMouseReleased(event -> {
            if (isRunning) {
                restartButton.setImage(restart);
                if (overField) {
                    if (firstClick && event.getButton() == MouseButton.PRIMARY && game[x][y].getFieldState() == State.COVERED) {
                        firstClick(x, y);
                        firstClick = false;
                    }
                    if (!mouseDrag) {
                        mouseEventHandler(event.getButton(), x, y);
                    }
                }
                mouseDrag = false;
            }
        });
        // Ist benötigt falls man die Maus bewegt und dann loslässt
        game[x][y].getImageView().setOnMouseDragReleased(event -> {
            if (overField) {
                if (firstClick && event.getButton() == MouseButton.PRIMARY && game[x][y].getFieldState() == State.COVERED) {
                    firstClick(x, y);
                    firstClick = false;
                }
                mouseEventHandler(event.getButton(), x, y);
            }
        });

    }

    private void initializeHeader() {
        flagsDigit1 = new ImageView(digits[0]);
        flagsDigit2 = new ImageView(digits[0]);
        flagsDigit3 = new ImageView(digits[0]);

        timerDigit1 = new ImageView(digits[0]);
        timerDigit2 = new ImageView(digits[0]);
        timerDigit3 = new ImageView(digits[0]);

        restartButton = new ImageView(restart);
        restartButton.setOnMouseExited(event -> {
            if (isRunning) {
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
                restart();
            }
        });
        restartButton.setOnMouseDragReleased(event -> {
            restartButton.setImage(restart);
            if (overRestartButton) {
                restart();
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
    }

    private void firstClick(int x, int y) {
        setMines(x, y);
        time = 0;
        timer.play();
    }

    private void restart() {
        timer.stop();
        fieldsToUncover = height * width - mines;
        flagsLeft = mines;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                restartField(x, y);
            }
        }
        updateFlagsLeft();
        timerDigit1.setImage(digits[0]);
        timerDigit2.setImage(digits[0]);
        timerDigit3.setImage(digits[0]);
        firstClick = true;
        isRunning = true;
    }

    private void restartField(int x, int y) {
        game[x][y].setMine(false);
        game[x][y].setFieldState(State.COVERED);
        game[x][y].setAmountNeighboursThatAreMines(0);
        game[x][y].getImageView().setImage(field);
    }

    private void setMines(int mouseX, int mouseY) {
        Random random = new Random();
        int x;
        int y;

        for (int i = 0; i < this.mines; i++) {
            do {
                x = random.nextInt(this.width);
                y = random.nextInt(this.height);
            } while (!isAValidMineLocation(x, y, mouseX, mouseY));
            game[x][y].setMine(true);
            for (int[] neighbour : neighbours) {
                try {
                    game[x + neighbour[0]][y + neighbour[1]].setAmountNeighboursThatAreMines(game[x + neighbour[0]][y + neighbour[1]].getAmountNeighboursThatAreMines() + 1);
                } catch (ArrayIndexOutOfBoundsException ignore) {}
            }
        }
    }

    private boolean isAValidMineLocation(int x, int y, int mouseX, int mouseY) {
        if (game[x][y].isMine()) {
            return false;
        }

        if (x == mouseX && y == mouseY) {
            return false;
        }

        if (fieldsToUncover < 9) {
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
        if (isRunning) {
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
        if (game[x][y].getFieldState() == State.COVERED) {
            game[x][y].setFieldState(State.UNCOVERED);
            if (game[x][y].isMine()) {
                lose();
                game[x][y].getImageView().setImage(detonatedMine);
            } else {
                game[x][y].getImageView().setImage(fields[game[x][y].getAmountNeighboursThatAreMines()]);
                fieldsToUncover--;
                if (game[x][y].getAmountNeighboursThatAreMines() == 0) {
                    uncoverNeighbours(x, y);
                }
            }
        }

        if (fieldsToUncover == 0) {
            win();
        }
    }

    private void flag(int x, int y) {
        if (game[x][y].getFieldState() == State.COVERED) {
            game[x][y].getImageView().setImage(flag);
            game[x][y].setFieldState(State.FLAGGED);
            flagsLeft--;
        } else if (game[x][y].getFieldState() == State.FLAGGED) {
            game[x][y].getImageView().setImage(field);
            game[x][y].setFieldState(State.COVERED);
            flagsLeft++;
        }
        updateFlagsLeft();
    }

    private void uncoverNeighbours(int x, int y) {
        if (countFlaggedNeighbours(x, y) == game[x][y].getAmountNeighboursThatAreMines() && game[x][y].getFieldState() == State.UNCOVERED) {
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
                if (game[x + neighbour[0]][y + neighbour[1]].getFieldState() == State.FLAGGED) {
                    amount++;
                }
            } catch (ArrayIndexOutOfBoundsException ignore) {}
        }
        return amount;
    }

    private void win() {
        timer.stop();
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (game[x][y].isMine() && game[x][y].getFieldState() == State.COVERED) {
                    flag(x, y);
                }
            }
        }

        isRunning = false;
        restartButton.setImage(won);
    }

    private void lose() {
        timer.stop();
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (game[x][y].isMine() && game[x][y].getFieldState() == State.COVERED) {
                    game[x][y].getImageView().setImage(mine);
                } else if (!game[x][y].isMine() && game[x][y].getFieldState() == State.FLAGGED) {
                    game[x][y].getImageView().setImage(wrongFlag);
                }
            }
        }

        isRunning = false;
        restartButton.setImage(lost);
    }

    private void updateFlagsLeft() {
        updateDigits(flagsDigit1, flagsDigit2, flagsDigit3, flagsLeft);
    }

    private void updateTimer() {
        time++;
        updateDigits(timerDigit1, timerDigit2, timerDigit3, time);
    }

    private void updateDigits(ImageView imageView1, ImageView imageView2, ImageView imageView3, int data) {
        try {
            imageView1.setImage(digits[(data / 100) % 10]);
            imageView2.setImage(digits[(data / 10) % 10]);
            imageView3.setImage(digits[data % 10]);
        } catch (ArrayIndexOutOfBoundsException ignore) {}
    }
}