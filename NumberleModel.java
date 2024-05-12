
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class  NumberleModel extends Observable implements INumberleModel {
    private String targetNumber;
    private StringBuilder currentGuess;
    private int remainingAttempts;
    private boolean gameWon;
    private final ArrayList<String> colors = new ArrayList<>();
    private final Map<String, Set<Character>> map = new HashMap<>();
    private boolean isRandom=true;

    //Randomly select an equation from equation.txt as the target equation
    private String generateTargetEquation() {
        List<String> equations = new ArrayList<>();
        String fileName = "equations.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                equations.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!equations.isEmpty() && isRandom) {
            Random rand = new Random();
            return equations.get(rand.nextInt(equations.size()));
        } else {
            return "1+1+1=3";
        }
    }
    //Override initialize game function
    @Override
    public void initialize() {
        Random rand = new Random();
        targetNumber = Integer.toString(rand.nextInt(10000000));
        currentGuess = new StringBuilder("");
        remainingAttempts = MAX_ATTEMPTS;
        gameWon = false;
        setChanged();
        notifyObservers();
        targetNumber = generateTargetEquation();
        System.out.println("New game start. Equation to guess:" + targetNumber);
    }

    @Override
    public void processInput(String input) {
        colors.clear();
        if (input == null || input.length() != 7) {
            setChanged();
            notifyObservers("Invalid Input");//check validation of Input
            return;
        }

        if (!evaluateExpression(input)) {
            return;
        }
        remainingAttempts--;//Reduce remaining attempts
        if (input.equals(targetNumber)) {
            gameWon = true;
            for(int i = 0; i < input.length(); i++) {
                colors.add("0");
            }
            System.out.println();
        } else {
            for (int i = 0; i < input.length(); i++) {
                char a = input.charAt(i);
                if (a == targetNumber.charAt(i)) {

                    colors.add("0");
                    map.computeIfAbsent("Green", k -> new HashSet<>()).add(a);
                    System.out.println("Green:" + a );

                } else if (targetNumber.contains(String.valueOf(a))) {

                    colors.add("1");
                    map.computeIfAbsent("Orange", k -> new HashSet<>()).add(a);
                    System.out.println("Orange:" + a);

                } else {
                    colors.add("2");
                    map.computeIfAbsent("Gray", k -> new HashSet<>()).add(a);
                    System.out.println("Gray:" + a);
                }
            }
        }
        System.out.println();
        if (isGameOver()) {
            setChanged();
            notifyObservers(gameWon ? "Game Won" : "Game Over");
            map.clear();
        } else {
            setChanged();
            notifyObservers("Try Again");
        }
    }

    /*Get a list of currently remaining unused characters*/
    public List<Character> getUnused() {
        List<Character> getUnused = new ArrayList<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '/'));
        for (Set<Character> usedChars : map.values()) {
            getUnused.removeAll(usedChars);
        }
        return getUnused;
    }

    public void setRandom(boolean random){
        this.isRandom = random;
    }

    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    @Override
    public boolean isGameWon() {
        return gameWon;
    }

    @Override
    public String getTargetNumber() {
        return targetNumber;
    }

    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess;
    }

    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    @Override
    public void startNewGame() {
        initialize();
    }

    @Override
    public ArrayList<String> getColors(){
        return colors;
    }

    @Override
    public Map<String, Set<Character>> getMap() {
        return map;
    }


    /*Verify mathematical expression logic*/
    @Override
    public boolean evaluateExpression(String expression) {
        String[] parts = expression.split("=");
        if (parts.length != 2) {
            setChanged();
            notifyObservers("No Equal");
            return false;
        }

        if (!isExpressionValid(expression)) {
            setChanged();
            notifyObservers("Missing Symbols");
            return false;
        }

        if (Math.abs( evaluateSide(parts[0]) - evaluateSide(parts[1]) ) < 0.0001) {
            return true;
        } else {
            setChanged();
            notifyObservers("Not Equal");
            return false;
        }

    }

    private static boolean isExpressionValid(String expression) {
        String regex = "^[0-9=]*[+\\-*/][0-9=+\\-*/]*$";
        return Pattern.matches(regex, expression);
    }


    /*Calculate one side of a mathematical expression*/
    private static double evaluateSide(String side) {
        List<Double> numbers = new ArrayList<>();//Store all numbers obtained from parsing
        List<Character> operators = new ArrayList<>();//Store all operators encountered

        String tempNum = "";
        for (char ch : side.toCharArray()) {
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                numbers.add(Double.parseDouble(tempNum));
                tempNum = "";
                operators.add(ch);
            } else {
                tempNum += ch;
            }
        }
        numbers.add(Double.parseDouble(tempNum));
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i) == '*' || operators.get(i) == '/') {
                double result = operators.get(i) == '*' ? numbers.get(i) * numbers.get(i + 1) : numbers.get(i) / numbers.get(i + 1);
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--;
            }
        }

        double result = numbers.get(0);
        for (int i = 0; i < operators.size(); i++) {
            double number = numbers.get(i + 1);
            switch (operators.get(i)) {
                case '+':
                    result += number;
                    break;
                case '-':
                    result -= number;
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected operator: " + operators.get(i));
            }
        }
        return result;
    }
}

