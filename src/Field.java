import javafx.scene.image.ImageView;

public class Field {
    private boolean isMine;
    private State fieldState;
    private int amountNeighboursThatAreMines;
    private ImageView imageView;

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
