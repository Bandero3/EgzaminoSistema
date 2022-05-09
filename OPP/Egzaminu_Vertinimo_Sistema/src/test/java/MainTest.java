import lt.codeacademy.Main;
import lt.codeacademy.MethodChecker;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class MainTest {
    @Test
    void testWhenWholeNumberProvided(){
        MethodChecker checker = new Main();
        String input = "1";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        int number = checker.getWholeNumber(scanner);
        assertEquals(1,number);
    }

    @Test
    void testWhenLastAnswerOptionProvided(){
        MethodChecker checker = new Main();
        String input = "5";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        int number = checker.getCorrectAnswer(scanner, 5);
        assertEquals(5,number);
    }
}
