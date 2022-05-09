package lt.codeacademy.service;


import lt.codeacademy.Main;
import lt.codeacademy.UserChecker;
import lt.codeacademy.user.User;
import lt.codeacademy.user.UserType;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoginService implements UserChecker{
    private static final int MAX_RETRY = 3;
    public static List<User> users = new ArrayList<>();

    public void userRegistration(Scanner scanner){
        String userName = getUniqueUserName(scanner);
        if (userName == null) {
            return;
        }

        System.out.println("Iveskite slaptažodi");
        String password = scanner.nextLine();

        if (!isRepeatPasswordCorrect(scanner, password)) {
            System.out.println("Vartotojo sukurti nepavyko");
            return;
        }
        String[] name = userName.split(" ");

        UserType userType = getUserType(scanner);

        int userId = users.stream().map(User::ID).max(Integer::compareTo).orElse(0)+1;

        users.add(new User(userId,name[0], name[1], userType, DigestUtils.sha512Hex(password)));
        System.out.println("Registracija sekminga");

    }

    public void login(Scanner scanner) throws IOException {
        Main main = new Main();


        User existingUser = getUser(scanner);

        if (existingUser == null) {
            System.out.println("Tokio vartotojo nera");
            return;
        }
        System.out.println("Iveskite slaptažodi");
        String password = scanner.nextLine();

        if (!existingUser.password().equals(DigestUtils.sha512Hex(password))) {
            System.out.println("Neteisingas slaptažodis");
            return;
        }
        System.out.printf("Sveikiname prisijungus %s %s\n", existingUser.name(), existingUser.surname());

        if (existingUser.Type().getRoleCode() == 1) {
            main.teacherAction();
            main.loggedIn = true;
        } else {
            main.studentAction(existingUser);
            main.loggedIn = true;
        }

    }
    private User getUser(Scanner scanner) {
        while (true) {
            System.out.println("Iveskita savo Varda ir Pavarde");
            String userName = scanner.nextLine();

            String[] name = userName.split(" ");

            if (name.length != 2) {
                System.out.println("Blogai ivestas vardas ir pavarde");
                continue;
            }

            return users.stream()
                    .filter(n -> n.name().equals(name[0]) && n.surname().equals(name[1]))
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    public boolean isRepeatPasswordCorrect(Scanner scanner, String password) {
        for (int i = 0; i < MAX_RETRY; i++) {
            System.out.println("Pakartotinai iveskite savo slaptažodi");
            String repeatPassword = scanner.nextLine();

            if (!repeatPassword.equals(password)) {
                System.out.println("Slaptazodziai nesutampa");
                continue;
            }
            return true;
        }
        return false;
    }


    @Override
    public String getUniqueUserName(Scanner scanner) {
        while (true) {
            System.out.println("Iveskite savo Varda ir Pavarde:");
            String newUserName = scanner.nextLine();
            String[] name = newUserName.split(" ");
            if (name.length != 2) {
                System.out.println("Blogai ivestas vardas ir pavarde");
                continue;
            }

            User existingUser = users.stream()
                    .filter(n -> n.name().equals(name[0]) && n.surname().equals(name[1]))
                    .findFirst()
                    .orElse(null);

            if (existingUser != null) {
                System.out.printf("Vartotojas %s jau existuoja\n", newUserName);
                return null;
            }
            return newUserName;
        }
    }

    @Override
    public UserType getUserType(Scanner scanner) {
        String userType;
        do {
            userTypeMenu();
            userType = scanner.nextLine();
            switch (userType) {
                case "1" -> {
                    return UserType.TEACHER;
                }
                case "2" -> {
                    return UserType.STUDENT;
                }
                default -> System.out.println("Tokios roles nera");
            }
        } while (true);
    }

    private void userTypeMenu() {
        System.out.println("""
                Pasirinkite savo role:
                [1]. Destytojas
                [2]. Studentas""");
    }
}
