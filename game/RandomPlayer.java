package game;

import java.util.Random;

public class RandomPlayer implements Player {
    private final Random random = new Random();

    @Override
    public Move makeMove(Position position, int n, int m) {
        while (true) {
            final Move move = new Move(
                    random.nextInt(n) % n,
                    random.nextInt(m) % m,
                    position.getTurn());
            if (position.isValid(move)) {
                return move;
            }
        }
    }
}
