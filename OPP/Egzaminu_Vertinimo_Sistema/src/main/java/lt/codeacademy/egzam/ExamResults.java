package lt.codeacademy.egzam;


import java.time.LocalDateTime;

public record ExamResults(int examId, String examName, int studentId, String studentName, String studentSurname, int grade, LocalDateTime dateTime) {

}
