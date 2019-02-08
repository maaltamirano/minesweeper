import javafx.scene.image.ImageView;

public class Field{
    private boolean isMine;
    private boolean isFlagged;
    private boolean isCovered;
    private int amountNeighboursThatAreMines;
    private ImageView imageView;

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        this.isMine = mine;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        this.isFlagged = flagged;
    }

    public boolean isCovered() {
        return isCovered;
    }

    public void setCovered(boolean covered) {
        this.isCovered = covered;
    }

    public int getAmountNeighboursThatAreMines() {
        return amountNeighboursThatAreMines;
    }

    public void setAmountNeighboursThatAreMines(int amountNeighboursThatAreMines) {
        this.amountNeighboursThatAreMines = amountNeighboursThatAreMines;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
