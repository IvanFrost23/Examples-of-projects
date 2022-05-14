package game;

public class PlayerGame {
    private final TicTacToeBoard board;
    private final java.util.List<Player> players;
    private final static int UNKNOWN_FLAG = -2000;

    public PlayerGame(TicTacToeBoard board, java.util.List<Player> players) {
        this.board = board;
        this.players = players;
    }

    public int play(boolean log, int qPlayers, int n, int m, int k) {
        board.setGame(qPlayers, n, m, k);
        while (true) {
            for (int i = 0; i < players.size(); i++) {
                final int result = makeMove(players.get(i), i + 1, log, n, m);

                if (result == UNKNOWN_FLAG) {
                    continue;
                }

                if (result < 0) {
                    players.remove(-result - 1);
                    continue;
                }

                if (result == 0) {
                    return 0;
                }
                return i + 1;
            }
        }
    }

    private int makeMove(Player player, int no, boolean log, int n, int m) {
        while (true) {
            final Move move = player.makeMove(board.getPosition(), n, m);

            final GameResult result = board.makeMove(move);

            if (log) {
                System.out.println("Player " + no);
                System.out.println(move);
                System.out.println(board);
                System.out.println("Result: " + result);
            }

            System.out.println(board);

            switch (result) {
                case WIN:
                    return no;
                case LOSE:
                    return -no;
                case DRAW:
                    return 0;
                case EMOVE:
                    break;
                case UNKNOWN:
                    return UNKNOWN_FLAG;
                default:
                    throw new AssertionError("Unknown makeMove result " + result);
            }
        }
    }
}
