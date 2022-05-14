package game;

import java.util.Scanner;

public class HumanPlayer implements Player {
    private final Scanner in;

    public HumanPlayer(Scanner in) {
        this.in = in;
    }

    @Override
    public Move makeMove(Position position, int n, int m) {
        Move nowMove = null;

        System.out.println();
        System.out.println("Current position");
        System.out.println(position);
        System.out.println("Enter your move for " + position.getTurn());

        while (true) {
            String x = in.next();
            String y = in.next();

            try {
                nowMove = new Move(Integer.parseInt(x) - 1, Integer.parseInt(y) - 1, position.getTurn());
                if (position.isValid(nowMove)) {
                    return nowMove;
                }
                throw new Exception();
            } catch (Exception e) {
                System.out.println("Your move is incorrect!");
                System.out.println(position);
                System.out.println("Please enter a legal move for " + position.getTurn());
            }
        }
    }
}
