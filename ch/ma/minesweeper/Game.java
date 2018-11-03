package ch.ma.minesweeper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
    private ImageView restartButton;
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
    private ImageView digit1;
    private ImageView digit2;
    private ImageView digit3;

    private ImageView digit4;
    private ImageView digit5;
    private ImageView digit6;

    private Timeline timer;
    private int time;

    public Game(int mines, int width, int height) {
        this.mines = mines;
        this.width = width;
        this.height = height;
    }

    public void start() {
        Stage gameStage = new Stage();
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        scene.addEventFilter(MouseEvent.DRAG_DETECTED , mouseEvent -> scene.startFullDrag());

        initialize();

        GridPane gridpane = new GridPane();
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                gridpane.add(game[i][j].getImageView(), j, i);
            }
        }

        initializeHeader();

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

        GridPane header = new GridPane();
        header.add(digit1, 0, 0);
        header.add(digit2, 1, 0);
        header.add(digit3, 2, 0);
        header.add(restartButton, 3, 0);
        header.add(digit4, 4, 0);
        header.add(digit5, 5, 0);
        header.add(digit6, 6, 0);
        update();

        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> timer()));
        timer.setCycleCount(999);

        root.setTop(header);
        root.setCenter(gridpane);
        gameStage.getIcons().add(new Image(getClass().getResource("/images/4x/restart.png").toExternalForm()));
        gameStage.setTitle("Minesweeper");
        gameStage.resizableProperty().setValue(false);
        gameStage.setScene(scene);
        gameStage.show();
    }

    private void initialize() {
        fieldsToUncover = height * width - mines;
        flagsLeft = mines;
        game = new Field[height][width];
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                initializeField(i, j);
            }
        }
        isRunning = true;
    }

    private void initializeField(int i, int j) {
        game[i][j] = new Field();
        game[i][j].setMine(false);
        game[i][j].setFlagged(false);
        game[i][j].setCovered(true);
        game[i][j].setAmountNeighboursThatAreMines(0);
        game[i][j].setImageView(new ImageView(field));
        // Ist benötigt damit immer das Feld auf dem die Maus gerade ist
        game[i][j].getImageView().setOnMouseDragEntered((EventHandler<MouseEvent>) event -> {
            if (isRunning && game[i][j].isCovered() && !game[i][j].isFlagged() && event.getButton() == MouseButton.PRIMARY) {
                restartButton.setImage(restartO);
                game[i][j].getImageView().setImage(fields[0]);
            }
            overField = true;
            mouseDrag = true;
        });
        // Ist benötigt um das Feld welches man gerade verlassen hat wieder zuzudecken
        game[i][j].getImageView().setOnMouseDragExited((EventHandler<MouseEvent>) event -> {
            if (isRunning && game[i][j].isCovered() && !game[i][j].isFlagged() && event.getButton() == MouseButton.PRIMARY) {
                restartButton.setImage(restart);
                game[i][j].getImageView().setImage(field);
            }
            overField = false;
            mouseDrag = false;
        });
        // Ist benötigt weil setOnMouseDrag nicht funktioniert wenn man die Maus nur um einen Pixel bewegt
        game[i][j].getImageView().setOnMouseExited(event -> {
            if (isRunning && game[i][j].isCovered() && !game[i][j].isFlagged() && event.getButton() == MouseButton.PRIMARY) {
                game[i][j].getImageView().setImage(field);
            }
            overField = false;
        });
        game[i][j].getImageView().setOnMouseEntered(event -> overField = true);
        // Ist benötigt weil setOnMouseDragEntered erst aktiviert wird wenn man die Maus bewegt
        game[i][j].getImageView().setOnMousePressed(event -> {
            overField = true;
            if (isRunning && game[i][j].isCovered() && !game[i][j].isFlagged() && event.getButton() == MouseButton.PRIMARY) {
                restartButton.setImage(restartO);
                game[i][j].getImageView().setImage(fields[0]);
            }
        });
        // Ist benötigt falls man nur auf ein Feld klickt ohne die Maus zu bewegen
        game[i][j].getImageView().setOnMouseReleased(event -> {
            if (isRunning) {
                restartButton.setImage(restart);
                if (overField) {
                    if (firstClick && event.getButton() == MouseButton.PRIMARY && !game[i][j].isFlagged()) {
                        firstClick(i, j);
                        firstClick = false;
                    }
                    if (!mouseDrag) {
                        mouseEventHandler(event.getButton(), i, j);
                    }
                }
                mouseDrag = false;
            }
        });
        // Ist benötigt falls man die Maus bewegt und dann loslässt
        game[i][j].getImageView().setOnMouseDragReleased(event -> {
            if (overField) {
                if (firstClick && event.getButton() == MouseButton.PRIMARY && !game[i][j].isFlagged()) {
                    firstClick(i, j);
                    firstClick = false;
                }
                mouseEventHandler(event.getButton(), i, j);
            }
        });

    }

    private void initializeHeader() {
        digit1 = new ImageView(digits[0]);
        digit2 = new ImageView(digits[0]);
        digit3 = new ImageView(digits[0]);

        digit4 = new ImageView(digits[0]);
        digit5 = new ImageView(digits[0]);
        digit6 = new ImageView(digits[0]);

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

    private void firstClick(int i, int j) {
            setMines(i, j);
            time = 0;
            timer.play();
    }

    private void restart() {
        timer.stop();
        fieldsToUncover = height * width - mines;
        flagsLeft = mines;
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                restartField(i, j);
            }
        }
        update();
        digit4.setImage(digits[0]);
        digit5.setImage(digits[0]);
        digit6.setImage(digits[0]);
        firstClick = true;
        isRunning = true;
    }

    private void restartField(int i, int j) {
        game[i][j].setMine(false);
        game[i][j].setFlagged(false);
        game[i][j].setCovered(true);
        game[i][j].setAmountNeighboursThatAreMines(0);
        game[i][j].getImageView().setImage(field);
    }

    private void setMines(int mouseX, int mouseY) {
        Random random = new Random();
        int x;
        int y;

        for (int i = 0; i < this.mines; i++) {
            do {
                x = random.nextInt(this.height);
                y = random.nextInt(this.width);
            } while (validateMineLocation(x, y, mouseX, mouseY));
            game[x][y].setMine(true);
            for (int[] neighbour : neighbours) {
                try {
                    game[x + neighbour[0]][y + neighbour[1]].setAmountNeighboursThatAreMines(game[x + neighbour[0]][y + neighbour[1]].getAmountNeighboursThatAreMines() + 1);
                } catch (IndexOutOfBoundsException ignored) {}
            }
        }
    }

    private boolean validateMineLocation(int x, int y, int mouseX, int mouseY) {

        if (game[x][y].isMine()) {
            return true;
        }

        if (x == mouseX && y == mouseY) {
            return true;
        }

        if (fieldsToUncover < 9) {
            return false;
        }

        for (int[] neighbour : neighbours) {
            try {
                if (x == mouseX + neighbour[0] && y == mouseY + neighbour[1]) {
                    return true;
                }
            } catch (IndexOutOfBoundsException ignored) {}
        }
        return false;
    }

    private void mouseEventHandler(MouseButton button, int i, int j) {
        if (isRunning) {
            switch (button) {
                case PRIMARY:
                    uncover(i, j);
                    break;
                case SECONDARY:
                    flag(i, j);
                    break;
                case MIDDLE:
                    if (countFlaggedNeighbours(i, j) == game[i][j].getAmountNeighboursThatAreMines() && !game[i][j].isCovered()) {
                        uncoverNeighbours(i, j);
                    }
                    break;
                default:
            }
        }
    }

    private int countFlaggedNeighbours(int i, int j) {
        int amount = 0;
        for (int[] neighbour : neighbours) {
            try {
                if (game[i + neighbour[0]][j + neighbour[1]].isFlagged()) {
                    amount++;
                }
            } catch (IndexOutOfBoundsException ignored) {}
        }
        return amount;
    }

    private void lose() {
        timer.stop();
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                if (game[i][j].isMine() && !game[i][j].isFlagged()) {
                    game[i][j].getImageView().setImage(mine);
                } else if (!game[i][j].isMine() && game[i][j].isFlagged()) {
                    game[i][j].getImageView().setImage(wrongFlag);
                }
            }
        }

        isRunning = false;
        restartButton.setImage(lost);
    }

    private void win() {
        timer.stop();
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                if (game[i][j].isMine() && !game[i][j].isFlagged()) {
                    flag(i, j);
                }
            }
        }

        isRunning = false;
        restartButton.setImage(won);
    }

    private void uncover(int i, int j) {
        if (game[i][j].isCovered() && !game[i][j].isFlagged()) {
            if (game[i][j].isMine()) {
                lose();
                game[i][j].getImageView().setImage(detonatedMine);
                game[i][j].setCovered(false);
            } else if (game[i][j].getAmountNeighboursThatAreMines() == 0) {
                game[i][j].getImageView().setImage(fields[0]);
                game[i][j].setCovered(false);
                fieldsToUncover--;
                uncoverNeighbours(i, j);
            } else {
                game[i][j].getImageView().setImage(fields[game[i][j].getAmountNeighboursThatAreMines()]);
                game[i][j].setCovered(false);
                fieldsToUncover--;
            }
        }

        if (fieldsToUncover == 0) {
            win();
        }
    }

    private void uncoverNeighbours(int i, int j) {
        for (int[] neighbour : neighbours) {
            try {
                uncover(i + neighbour[0], j + neighbour[1]);
            } catch (IndexOutOfBoundsException ignored) {}
        }
    }

    private void flag(int i, int j) {

        if (!game[i][j].isFlagged() && game[i][j].isCovered()) {
            game[i][j].getImageView().setImage(flag);
            game[i][j].setFlagged(true);
            flagsLeft--;
        } else if (game[i][j].isFlagged()) {
            game[i][j].getImageView().setImage(field);
            game[i][j].setFlagged(false);
            flagsLeft++;
        }
        update();
    }

    private void update() {
        try {
            digit1.setImage(digits[(flagsLeft / 100) % 10]);
            digit2.setImage(digits[(flagsLeft / 10) % 10]);
            digit3.setImage(digits[flagsLeft % 10]);
        } catch (ArrayIndexOutOfBoundsException ignore) {}
    }

    private void timer() {
        time++;
        try {
            digit4.setImage(digits[(time / 100) % 10]);
            digit5.setImage(digits[(time / 10) % 10]);
            digit6.setImage(digits[time % 10]);
        } catch (ArrayIndexOutOfBoundsException ignore) {}
    }
}