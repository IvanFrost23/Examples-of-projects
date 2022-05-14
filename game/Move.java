package game;

public class Move {
    private final Cell value;
    private final int row;
    private final int col;

    public Move(int row, int col, Cell value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public Cell getValue() {
        return value;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return String.format("Move(%s, %d, %d)", value, row + 1, col + 1);
    }
}
