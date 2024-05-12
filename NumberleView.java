
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    private final JFrame frame = new JFrame("Numberle");
    private final JTextField inputTextField = new JTextField(3);;
    private final StringBuilder input;
    private final JTextField[][] fields = new JTextField[INumberleModel.MAX_ATTEMPTS][7];
    private final Map<String, JButton> buttonMap = new HashMap<>();
    private int remainingAttempts;
    private int currentPosition = 0;
    private boolean showErrorMess=true;


    /*Initialize model*/
    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        ((NumberleModel)this.model).addObserver(this);
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel)this.model, null);
        input = controller.getCurrentGuess();
    }

    /*Initialize Frame*/
    public void initializeFrame() {
        /*Configure the main window*/
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        /* Configure menu bar */
        JMenuBar menuBar = new JMenuBar();
        JToggleButton showError = new JToggleButton("Error display");
        showError.setSelected(true);
        showError.addActionListener(e -> {
            clearAllContent();
            controller.startNewGame();
            currentPosition = 0;
            remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
            input.setLength(0);

            showError(showError.isSelected());

        });
        menuBar.add(showError);
        /*Restart Button*/
        JButton restartGame = new JButton("Restart");
        restartGame.addActionListener(e -> restartGame());
        menuBar.add(restartGame);
        /*Answer Button*/
        JButton showAnswer = new JButton("Answer");
        showAnswer.addActionListener(e -> showAnswer());
        menuBar.add(showAnswer);
        /*Random Game Button*/
        JToggleButton randomGame = new JToggleButton("Random Game");
        randomGame.setSelected(true);
        randomGame.addActionListener(e -> randomGame(randomGame.isSelected()));
        menuBar.add(randomGame);

        frame.setJMenuBar(menuBar);

        /*Configure the main panel*/
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.add(new JPanel());

        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new GridLayout(6, 7, 5, 5));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        Font font = new Font("Arial", Font.BOLD, 30);
        for (int i = 0; i < INumberleModel.MAX_ATTEMPTS; i++) {
            for (int j = 0; j < 7; j++) {
                fields[i][j] = new JTextField();
                fields[i][j].setEditable(false);
                fields[i][j].setHorizontalAlignment(JTextField.CENTER);
                fields[i][j].setFont(font);fields[i][j].setFont(font);
                fields[i][j].setPreferredSize(new Dimension(60,60));
                displayPanel.add(fields[i][j]);
            }
        }
        center.add(displayPanel);
        center.add(new JPanel());
        frame.add(center, BorderLayout.NORTH);

        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(2, INumberleModel.MAX_ATTEMPTS, 5, 5));
        keyboardPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel numberPanel = new JPanel(new GridLayout(1, 10, 5, 5));
        String[] numberKeys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        Color numberButtonColor = Color.decode("#dce1ed");
        Color numberTextColor = Color.BLACK;

        for (String key : numberKeys) {
            JButton button = new JButton(key);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setBackground(numberButtonColor);
            button.setForeground(numberTextColor);
            button.addActionListener(e -> {
                if (currentPosition < 7) {
                    fields[remainingAttempts][currentPosition].setText(key);
                    currentPosition++;
                }
            });
            buttonMap.put(key, button);
            numberPanel.add(button);
        }

        /*Layout*/
        JPanel operationPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        String[] operationKeys = {"Back", "+", "-", "*", "/", "=", "Enter"};
        for (String key : operationKeys) {
            JButton button = new JButton(key);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setBackground(numberButtonColor);
            button.setForeground(numberTextColor);
            button.addActionListener(e -> {
                if (currentPosition <= 7) {
                    switch (key) {
                        case "Back":
                            if (currentPosition > 0) {
                                fields[remainingAttempts][currentPosition - 1].setText("");
                                currentPosition--;
                            }
                            break;
                        case "Enter":
                            for (int i = 0; i < currentPosition; i++) {
                                input.append(fields[remainingAttempts][i].getText());
                            }
                            controller.processInput(input.toString());
                            remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                            break;
                        case "+":
                        case "-":
                        case "*":
                        case "/":
                        case "=":
                            if (currentPosition < 6) {
                                fields[remainingAttempts][currentPosition].setText(key);
                                currentPosition++;
                            }
                            break;
                    }
                }
            });
            buttonMap.put(key, button);
            operationPanel.add(button);
        }

        keyboardPanel.add(numberPanel, BorderLayout.NORTH);
        keyboardPanel.add(operationPanel, BorderLayout.SOUTH);
        frame.add(keyboardPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

    }
    /*Update the status of game progress*/
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String message = (String) arg;
            switch (message) {
                case "Invalid Input":
                    if(showErrorMess){
                        JOptionPane.showMessageDialog(frame, message, "Game Won", JOptionPane.INFORMATION_MESSAGE);
                    }
                    currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "Game Won":
                    showColor();
                    JOptionPane.showMessageDialog(frame, "Congratulations! You won the game!", "Game Won", JOptionPane.INFORMATION_MESSAGE);
                    clearAllContent();
                    currentPosition = 0;
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    controller.startNewGame();
                    break;
                case "Game Over":
                    showColor();
                    JOptionPane.showMessageDialog(frame, message + "! No Attempts, The correct equation was: " + controller.getTargetWord(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
                    controller.startNewGame();
                    clearAllContent();
                    currentPosition = 0;
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "Try Again":
                    showColor();
                    setButtonColors();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    JOptionPane.showMessageDialog(frame, message + ", Attempts remaining: " + model.getRemainingAttempts(), "Try Again", JOptionPane.INFORMATION_MESSAGE);
                    currentPosition = 0;
                    input.setLength(0);
                    break;
                case "No Equal":
                    if(showErrorMess){
                        JOptionPane.showMessageDialog(frame,  "Missing '=' sign.", message, JOptionPane.INFORMATION_MESSAGE);
                    }
                    currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "Missing Symbols":
                    if(showErrorMess){
                        JOptionPane.showMessageDialog(frame,  "There must be at least one '+-*/'.", message, JOptionPane.INFORMATION_MESSAGE);
                    }
                    currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "Not Equal":
                    if(showErrorMess){
                        JOptionPane.showMessageDialog(frame,  "The left side is not equal to the right.", message, JOptionPane.INFORMATION_MESSAGE);
                    }
                    currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    break;
            }
        }
    }

    private void randomGame(boolean on) {
        controller.setRandom(on);
        restartGame();
    }

    private void showAnswer() {
        JOptionPane.showMessageDialog(frame, controller.getTargetWord());
    }

    private void restartGame() {
        controller.startNewGame();
        clearAllContent();
        currentPosition=0;//Variable reset to 0, input will start from the first position in the new game
        remainingAttempts = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
        input.setLength(0);
    }

    private void showError(boolean on) {
        showErrorMess=on;
    }

    private void showColor() {
        for (int i = 0; i < model.getColors().size(); i++) {
            switch (model.getColors().get(i)) {
                case "0":
                    fields[remainingAttempts][i].setBackground(Color.decode("#2fc1a5"));
                    break;
                case "1":
                    fields[remainingAttempts][i].setBackground(Color.decode("#f79a6f"));
                    break;
                case "2":
                    fields[remainingAttempts][i].setBackground(Color.decode("#5a6376"));
                    break;
            }
        }
    }

    private void clearAllContent() {
        for (int i = 0; i < INumberleModel.MAX_ATTEMPTS; i++) {
            for (int j = 0; j < 7; j++) {
                fields[i][j].setText("");
                fields[i][j].setBackground(null);
            }
        }
        for (JButton button : buttonMap.values()) {
            button.setBackground(null);
        }
    }

    public void setButtonColors() {
        Map<String, Color> colorDefinitions = new HashMap<>();

        colorDefinitions.put("Green", Color.decode("#2fc1a5"));
        colorDefinitions.put("Orange", Color.decode("#f79a6f"));
        colorDefinitions.put("Gray", Color.decode("#5a6376"));


        for (Map.Entry<String, Set<Character>> entry : model.getMap().entrySet()) {
            String colorName = entry.getKey();
            Set<Character> characters = entry.getValue();
            Color color = colorDefinitions.get(colorName);
            if (color == null) continue;
            for (Character character : characters) {
                JButton button = buttonMap.get(character.toString());
                if (button != null) {
                    button.setBackground(color);
                }
            }
        }
    }


}