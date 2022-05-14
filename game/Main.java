package game;

import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    static final int MAX_SIZE = 10000; // max size for one side in board

    public static void main(String[] args) {
        //Data input

        Scanner scanner = new Scanner(System.in);
        int n, m, k; // Parameters for field and game
        int qPlayers; // Quantity players in game

        System.out.println("Enter size of the field size n, m and parameter k:");

        while (true) {
            String nString = scanner.next(); // Number n like a string
            String mString = scanner.next(); // Number m like a string
            String kString = scanner.next(); // Number k like a string

            try {
                n = Integer.valueOf(nString); // Convert n from string to int
                m = Integer.valueOf(mString); // Convert m from string to int
                k = Integer.valueOf(kString); // Convert k from string to int

                if (correct(n, m, k)) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Your data is incorrect!");
                System.out.println("Please enter a legal n, m, k");
            }
        }

        // Read quantity of players
        while (true) {
            System.out.println("Enter quantity of players: from 2 to 4");
            String qPlayersStr = scanner.next();
            try {
                qPlayers = Integer.valueOf(qPlayersStr);
                if (qPlayers >= 2 && qPlayers <= 4) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("You entered incorrect information!");
            }
        }

        System.out.println("Okay! Now you should to choose players. " +
                "Please for each player enter type:" +
                "Human Player, Random Player or Sequential Player");

        ArrayList<Player> players = new ArrayList<>(); // List that contains different players

        scanner.nextLine();

        /*
            Read describes of players. User can choose one of three types players for each position
            "Human Player" - User plays on his own. User enters moves from the keyboard.
            User can repeat move if he made a mistake.

            "Random Player" - Computer player. It's making random correct moves.
            But if it makes error move, this player died and another players continue game.

            "Sequential Player" - Computer player. It's making valid moves sequential.
             If this player makes error move, this player died and another players continue game.
        */

        for (int i = 0; i < qPlayers; i++) {
            System.out.println("Player " + Integer.toString(i + 1) + ": ");

            String player = scanner.nextLine();

            switch (player) {
                case "Human Player":
                    players.add(new HumanPlayer(new Scanner(System.in)));
                    break;
                case "Random Player":
                    players.add(new RandomPlayer());
                    break;
                case "Sequential Player":
                    players.add(new SequentialPlayer());
                    break;
                default:
                    System.out.println("You entered not a type of player. Please repeat!");
                    i--;
            }
        }

        //Game action

        final int result = new PlayerGame(
                new TicTacToeBoard(),
                players
        ).play(false, qPlayers, n, m, k);

        //Output result of game

        switch (result) {
            case 1:
                System.out.println("First player won");
                break;
            case 2:
                System.out.println("Second player won");
                break;
            case 3:
                System.out.println("Third player won");
                break;
            case 4:
                System.out.println("Forth player won");
                break;
            case 0:
                System.out.println("Draw");
                break;
            default:
                throw new AssertionError("Unknown result " + result);
        }
    }

    // Method, that determine is n, m, k field possible
    private static boolean correct(int n, int m, int k) {
        if (Math.max(n, m) > MAX_SIZE || Math.min(n, m) <= 0 || k > Math.max(n, m) || k < 1) {
            throw new NumberFormatException();
        } else {
            return true;
        }
    }
}

