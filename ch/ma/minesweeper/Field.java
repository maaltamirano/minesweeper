package ch.ma.minesweeper;

import javafx.scene.image.ImageView;

public class Field{
    private boolean isMine;
    private boolean isFlagged;
    private boolean isCovered;
    private int amountNeighbours;
    public ImageView image;

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public int getAmountNeighbours() {
        return amountNeighbours;
    }

    public void setAmountNeighbours(int amountNeighbours) {
        this.amountNeighbours = amountNeighbours;
    }

    public boolean isCovered() {
        return isCovered;
    }

    public void setCovered(boolean covered) {
        isCovered = covered;
    }
}
