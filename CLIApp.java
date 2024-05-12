

import java.util.Scanner;


public class CLIApp {
    public static void main(String[] args) {
        INumberleModel model = new NumberleModel();

        try (Scanner scanner = new Scanner(System.in)) {
            model.startNewGame();
            System.out.println("Numberle CLI Version");
            System.out.println("You have " + model.getRemainingAttempts() + " attempts to guess。");

            while (!model.isGameOver()) {
                System.out.println("Enter your guess: ");
                String input = scanner.nextLine();

                model.processInput(input);

                if (model.isGameOver()) {
                    if (model.isGameWon()) {
                        System.out.println("Congratulations! You won.");
                    } else {
                        System.out.println("Game Over! The correct equation was: " + model.getTargetNumber());
                    }
                } else {
                    System.out.println("Unused number and symbols: "+ model.getUnused());

                    System.out.println("Try again. You have " + model.getRemainingAttempts() + " attempts .");
                }
            }
        }
    }
}
