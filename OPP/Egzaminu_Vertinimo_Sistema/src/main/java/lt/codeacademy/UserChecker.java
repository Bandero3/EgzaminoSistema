package lt.codeacademy;

import lt.codeacademy.user.User;
import lt.codeacademy.user.UserType;

import java.util.Scanner;

public interface UserChecker {
    boolean isRepeatPasswordCorrect(Scanner scanner, String password);
    UserType getUserType(Scanner scanner);
    String getUniqueUserName(Scanner scanner);
}
