package lt.codeacademy.egzam;

import java.util.List;

public record Exam(int id, String title, List<Question> questions){
}
