

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.List;

public interface INumberleModel {
    int MAX_ATTEMPTS = 6;
    void initialize();
    void processInput(String input);
    boolean isGameOver();
    boolean isGameWon();
    String getTargetNumber();
    StringBuilder getCurrentGuess();
    int getRemainingAttempts();
    void startNewGame();
    ArrayList<String> getColors();
    Map<String, Set<Character>> getMap();
    boolean evaluateExpression(String expression);
    void setRandom(boolean random);
    List<Character> getUnused();}