package models;

public class Game {
    private Field[][] gameField;
    private boolean isRunning;
    private int width;
    private int height;
    private int mines;
    private int fieldsToUncover;
    private int flagsLeft;
    private boolean firstClick;
    private int time;

    public Game(int width, int height, int mines) {
        this.width = width;
        this.height = height;
        this.mines = mines;

        gameField = new Field[width][height];
        fieldsToUncover = width * height - mines;
        flagsLeft = mines;

        time = 0;
        firstClick = true;
        isRunning = true;
    }

    public Field getField(int x, int y) {
        return gameField[x][y];
    }

    public void setField(Field field, int x, int y) {
        this.gameField[x][y] = field;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMines() {
        return mines;
    }

    public void setMines(int mines) {
        this.mines = mines;
    }

    public int getFieldsToUncover() {
        return fieldsToUncover;
    }

    public void increaseFieldsToUncover() {
        fieldsToUncover++;
    }

    public void decreaseFieldsToUncover() {
        fieldsToUncover--;
    }

    public int getFlagsLeft() {
        return flagsLeft;
    }

    public void increaseFlagsLeft() {
        flagsLeft++;
    }

    public void decreaseFlagsLeft() {
        flagsLeft--;
    }

    public boolean isFirstClick() {
        return firstClick;
    }

    public void setFirstClick(boolean firstClick) {
        this.firstClick = firstClick;
    }

    public int getTime() {
        return time;
    }

    public void increaseTime() {
        time++;
    }
}
