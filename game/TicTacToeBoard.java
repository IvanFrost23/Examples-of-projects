package game;

import java.awt.desktop.SystemEventListener;
import java.util.Arrays;
import java.util.*;
import java.lang.*;

public class TicTacToeBoard implements Board, Position {
    private static final Map<Cell, String> CELL_TO_STRING = Map.of(
            Cell.E, ".",
            Cell.X, "X",
            Cell.O, "0",
            Cell.T, "-",
            Cell.P, "|"
    );

    private Cell[][] field;
    private Cell turn;
    private int n;
    private int m;
    private int k;
    private int numberPlayers;

    public void setGame(int numberPlayers, int n, int m, int k) {
        this.numberPlayers = numberPlayers;
        this.n = n;
        this.m = m;
        this.k = k;
        field = new Cell[n][m];
        for (Cell[] row : field) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
    }

    @Override
    public Position getPosition() {
        return this;
    }

    @Override
    public GameResult makeMove(Move move) {
        // Check that move is correct. If computer player made a mistake he loses.
        if (!isValid(move)) {
            return GameResult.LOSE;
        }

        field[move.getRow()][move.getCol()] = move.getValue();

        if (checkWin(move)) {
            return GameResult.WIN;
        }

        if (checkDraw()) {
            return GameResult.DRAW;
        }

        // Change turn
        if (turn == Cell.X) {
            turn = Cell.O;
        } else if (turn == Cell.O && numberPlayers >= 3) {
            turn = Cell.T;
        } else if (turn == Cell.T && numberPlayers == 4) {
            turn = Cell.P;
        } else {
            turn = Cell.X;
        }

        return GameResult.UNKNOWN;
    }

    public boolean isValid(Move move) {
        return 0 <= move.getRow() && move.getRow() < n
                && 0 <= move.getCol() && move.getCol() < m
                && turn == move.getValue()
                && field[move.getRow()][move.getCol()] == Cell.E;
    }

    @Override
    public Cell getCell(int row, int column) {
        return field[row][column];
    }

    public Cell getTurn() {
        return turn;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder(" ");

        for (int i = 1; i <= m; i++) {
            sb.append(Integer.toString(i));
        }

        sb.append(System.lineSeparator());

        for (int r = 0; r < n; r++) {
            sb.append(r + 1);
            for (Cell cell : field[r]) {
                sb.append(CELL_TO_STRING.get(cell));
            }
            sb.append(System.lineSeparator());
        }
        sb.setLength(sb.length() - System.lineSeparator().length());
        return sb.toString();
    }

    private boolean checkDraw() {
        int count = 0;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                if (field[r][c] == Cell.E) {
                    count++;
                }
            }
        }
        return count == 0;
    }

    private boolean checkWin(Move move) {
        int[] dx = {1, 1, 1, 0, -1, -1, -1, 0};
        int[] dy = {-1, 0, 1, 1, 1, 0, -1, -1};
        int[] cnt = {0, 0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < 8; i++) {
            int x = move.getRow();
            int y = move.getCol();

            while (x >= 0 && x < n && y >= 0 && y < m && field[x][y] == move.getValue()) {
                x += dx[i];
                y += dy[i];
                cnt[i]++;
            }
        }

        for (int i = 0; i < 4; i++) {
            if (cnt[i] + cnt[i + 4] - 1 >= k) {
                return true;
            }
        }
        return false;
    }
}
