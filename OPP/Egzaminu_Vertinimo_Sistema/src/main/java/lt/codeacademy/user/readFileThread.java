package lt.codeacademy.user;

import lt.codeacademy.Main;

import java.io.IOException;

public class readFileThread extends Thread{
    @Override
    public void run() {
        Main main = new Main();
        try {
            main.readUserFile(main.USER_FILE);
            main.readUserFile(main.EXAM_FILE);
            main.readUserFile(main.RESULT_FILE);
        } catch (IOException e) {
            System.out.println("Failu nuskaityti nepavyko");
        }
    }
}
