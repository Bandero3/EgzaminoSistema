package lt.codeacademy.user;

import lt.codeacademy.Main;

import java.io.IOException;

public class writeFileThread extends Thread{
    @Override
    public void run() {
        Main main = new Main();
        try {
            main.writeUserFile(main.USER_FILE);
            main.writeUserFile(main.EXAM_FILE);
            main.writeUserFile(main.RESULT_FILE);
        } catch (IOException e) {
            System.out.println("Failu irasyti nepavyko");
        }
    }
}
