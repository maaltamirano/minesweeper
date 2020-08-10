package models;

import javafx.scene.image.ImageView;

public class Field {
    private boolean isMine;
    private State fieldState;
    private int numberOfNeighboursThatAreMines;
    private ImageView imageView;

    public Field() {
        isMine = false;
        fieldState = State.COVERED;
        numberOfNeighboursThatAreMines = 0;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        this.isMine = mine;
    }

    public State getFieldState() {
        return fieldState;
    }

    public void setFieldState(State fieldState) {
        this.fieldState = fieldState;
    }

    public int getNumberOfNeighboursThatAreMines() {
        return numberOfNeighboursThatAreMines;
    }

    public void increaseNumberOfNeighboursThatAreMines() {
        numberOfNeighboursThatAreMines++;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
