import lt.codeacademy.UserChecker;
import lt.codeacademy.service.LoginService;
import lt.codeacademy.user.UserType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class LoginServiceTest {
    @Test
    void testPasswordWhenCorrectRepeat(){
        UserChecker checker = new LoginService();
        String input = "password";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        checker.isRepeatPasswordCorrect(scanner, "password");
    }

    @Test
    void testIfTeacherTypeSelected(){
        UserChecker checker = new LoginService();
        String input = "1";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        UserType userType = checker.getUserType(scanner);
        assertEquals(1, userType.getRoleCode());
    }

    @Test
    void testIfStudentTypeSelected(){
        UserChecker checker = new LoginService();
        String input = "2";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        UserType userType = checker.getUserType(scanner);
        assertEquals(0, userType.getRoleCode());
    }
    @Test
    void testWhenUserNameNotUsed(){
        UserChecker checker = new LoginService();
        String input = "Vardas Pavarde";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        String fullName = checker.getUniqueUserName(scanner);
        assertEquals("Vardas Pavarde", fullName);
    }
}
