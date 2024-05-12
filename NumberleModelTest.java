import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class NumberleModelTest {
    private NumberleModel model;
/*Pre
Set up testing environment
Created an instance of NumberleModel and disabled its randomness to ensure predictability and consistency of test results.*/
    @Before
    public void setUp() {
        model = new NumberleModel();
        model.setRandom(false); // Disable randomness for consistent testing
    }
/*POST
Execute specific test cases to check if different functional points are working as expected*/
    @Test//First Test for initialization
    public void testInitialization() {
        model.startNewGame();
        assertNotNull("Target number should not be null after initialization", model.getTargetNumber());
        assertEquals("Should have maximum attempts after initialization", NumberleModel.MAX_ATTEMPTS, model.getRemainingAttempts());
        assertFalse("Game should not be won at start", model.isGameWon());
    }

    @Test//Second Test for testProcessValidaAndInvalidInput
    public void testProcessValidAndInvalidInput() {
        model.startNewGame();
        String invalidInput = "123"; // Incorrect length
        model.processInput(invalidInput);
        assertEquals("Remaining attempts should decrease after invalid input", NumberleModel.MAX_ATTEMPTS - 1, model.getRemainingAttempts());

        String validInput = "1234567"; // Assuming correct length but random content
        model.processInput(validInput);
        assertEquals("Remaining attempts should decrease after valid input", NumberleModel.MAX_ATTEMPTS - 2, model.getRemainingAttempts());

    }

    @Test//Third test for testEndGameConditions
    public void testEndGameConditions() {
        model.startNewGame();
        // Simulate the maximum number of wrong guesses minus one
        for (int i = 0; i < NumberleModel.MAX_ATTEMPTS - 1; i++) {
            model.processInput("1234567"); // Assume this is always wrong
        }
        assertTrue("Game should be over after maximum attempts", model.isGameOver());
        assertFalse("Game should not be won with incorrect guesses", model.isGameWon());

        // Test winning condition
        model.startNewGame();
        model.processInput(model.getTargetNumber());
        assertTrue("Game should be won if target number is guessed", model.isGameWon());
    }


}
