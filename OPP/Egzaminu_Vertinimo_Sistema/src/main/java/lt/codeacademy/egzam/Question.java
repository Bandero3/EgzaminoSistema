package lt.codeacademy.egzam;

import java.util.List;

public record Question (int questionNumb, String question, List<String> answers, int correctAnswer){
}
