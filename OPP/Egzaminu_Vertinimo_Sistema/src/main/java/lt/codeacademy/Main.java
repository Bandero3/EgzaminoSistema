package lt.codeacademy;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lt.codeacademy.egzam.Exam;
import lt.codeacademy.egzam.ExamResults;
import lt.codeacademy.egzam.Question;
import lt.codeacademy.service.LoginService;
import lt.codeacademy.user.User;
import lt.codeacademy.user.readFileThread;
import lt.codeacademy.user.writeFileThread;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;


public class Main implements MethodChecker {
    public static final String USER_FILE = "users.json";
    public static final String EXAM_FILE = "exams.json";
    public static final String RESULT_FILE = "results.json";

    public static boolean loggedIn = false;

    LoginService service = new LoginService();

    public static List<Exam> exams = new ArrayList<>();
    public static List<ExamResults> results = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        Thread thread = new readFileThread();
        thread.start();
        main.selectAction();
    }

    private void menu() {
        System.out.println("""
                [1]. Registracija
                [2]. Prisijungimas
                [3]. Uždaryti Programa
                """);
    }

    private void selectAction() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String action;

        do {
            menu();
            action = scanner.nextLine();
            switch (action) {
                case "1" -> service.userRegistration(scanner);
                case "2" -> service.login(scanner);
                case "3" -> {
                    Thread thread = new writeFileThread();
                    thread.start();
                    System.out.println("Programa baige darba");
                }
                default -> System.out.println("Tokio veiksmo nera");
            }
        } while (!action.equals("3") && !loggedIn);
    }

    public void teacherMenu() {
        System.out.println("""
                [1]. Sukurti Egzamina
                [2]. Koreguoti Egzamina
                [3]. Ištrinti Egzamina
                [4]. Peržiureti studentu rezultatus
                [5]. Atsijungti
                """);
    }

    public void teacherAction() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String action;

        do {
            teacherMenu();
            action = scanner.nextLine();
            switch (action) {
                case "1" -> createExam(scanner);
                case "2" -> editExam(scanner);
                case "3" -> deleteExam(scanner);
                case "4" -> teacherCheckExams();
                case "5" -> {
                    System.out.println("Atsijungete");
                    loggedIn = false;
                    selectAction();
                }
                default -> System.out.println("Tokio veiksmo nera");
            }
        } while (!action.equals("5"));
    }

    private void editExam(Scanner scanner) {
        if (exams.isEmpty()) {
            System.out.println("Nera jokiu egzaminu");
            return;
        }
        exams.stream().map(Exam::title).forEach(System.out::println);
        Exam exam = getExam(scanner);
        String action;
        do {
            editExamMenu();
            action = scanner.nextLine();
            switch (action) {
                case "1" -> addToExam(scanner, exam);
                case "2" -> editExamQuestion(scanner, exam);
                case "3" -> System.out.println("");
                default -> System.out.println("Tokio veiksmo nera");
            }
        } while (!action.equals("3"));
    }

    private void editExamMenu() {
        System.out.println("""
                [1]. Prideti papildoma klausyma
                [2]. Pakeisti klausyma
                [3]. Grizti atgal""");
    }

    private void editExamQuestion(Scanner scanner, Exam exam) {
        Question examQuestion = getExamQuestion(scanner, exam);

        int answerAmount = examQuestion.answers().size();

        List<String> answers = new ArrayList<>();
        System.out.printf("Iveskite %s-a klausyma\n", examQuestion.questionNumb());
        String newQuestion = scanner.nextLine();

        for (int i = 1; i < answerAmount + 1; i++) {
            System.out.printf("Iveskite %d-jo klausymo %d-a atsakyma\n", examQuestion.questionNumb(), i);
            String answer = scanner.nextLine();
            answers.add(i + ". " + answer);
        }
        answers.forEach(System.out::println);
        System.out.printf("Iveskite %d-jo klausymo teisinga atsakymo\n", examQuestion.questionNumb());
        int correctAnswer = getCorrectAnswer(scanner, answerAmount);
        exam.questions().remove(examQuestion);
        exam.questions().add(new Question(examQuestion.questionNumb(), newQuestion, answers, correctAnswer));
        List<Question> newQuestions = exam.questions().stream().sorted(Comparator.comparingInt(Question::questionNumb)).toList();
        exam.questions().clear();
        exam.questions().addAll(newQuestions);
        System.out.printf("Pakeitete %s egzamino %s-a klausymae\n",exam.title(), examQuestion.questionNumb());
    }

    private Question getExamQuestion(Scanner scanner, Exam exam) {
        exam.questions().forEach(System.out::println);
        System.out.printf("Iveskite kuri egzamino %s klausima norite pakeisti\n", exam.title());
        int questionNumb;
        while (true) {
            try {
                questionNumb = Integer.parseInt(scanner.nextLine());
                Question question = exam.questions().get(questionNumb - 1);
                if (question == null) {
                    System.out.println("Tokio klausymo nera");
                    continue;
                }
                return question;
            } catch (Exception e) {
                System.out.println("Ivedete bloga skaiciu");
            }

        }
    }


    private void addToExam(Scanner scanner, Exam exam) {
        int questionAmount = exam.questions().size() + 1;
        int answerAmount = exam.questions().get(0).answers().size();

        List<String> answers = new ArrayList<>();
        System.out.printf("Iveskite %s-a klausyma\n", questionAmount);
        String newQuestion = scanner.nextLine();

        for (int i = 1; i < answerAmount + 1; i++) {
            System.out.printf("Iveskite %d-jo klausymo %d-a atsakyma\n", questionAmount, i);
            String answer = scanner.nextLine();
            answers.add(i + ". " + answer);
        }
        answers.forEach(System.out::println);
        System.out.printf("Iveskite %d-jo klausymo teisinga atsakymo\n", questionAmount);
        int correctAnswer = getCorrectAnswer(scanner, answerAmount);
        exam.questions().add(new Question(questionAmount, newQuestion, answers, correctAnswer));
        System.out.printf("Pridejote papildoma %s-a klausyma prie %s egzamino\n",questionAmount,exam.title());
    }

    private Exam getExam(Scanner scanner) {
        while (true) {
            System.out.println("Iveskite pavadinima egzamino kuri norite koreguoti");
            String examName = scanner.nextLine();
            Exam exam = exams.stream().filter(n -> n.title().equals(examName)).findFirst().orElse(null);
            if (exam == null) {
                System.out.printf("Egzamino pavadinimu %s nera\n", examName);
                continue;
            }
            System.out.printf("Modifikuojate egzamina %s\n", examName);
            return exam;
        }
    }

    public void teacherCheckExams() {
        results.forEach(System.out::println);
    }

    private void deleteExam(Scanner scanner) {
        if (exams.isEmpty()) {
            System.out.println("Nera jokiu egzaminu");
            return;
        }
        exams.forEach(System.out::println);
            System.out.println("Irasykite pavadinima egzamino kuri norite istrinti");
            String examName = scanner.nextLine();
            Exam exam = exams.stream().filter(n -> n.title().equals(examName)).findFirst().orElse(null);
            if (exam == null) {
                System.out.printf("Egzamino pavadinimu %s nera\n", examName);
                return;
            }
            System.out.printf("Egzaminas pavadinimu %s buvo istrintas\n", examName);
            exams.remove(exam);
    }

    public void studentMenu() {
        System.out.println("""
                [1]. Laikyti Egzamina
                [2]. Žiureti vidurki
                [3]. Atsijungti
                """);
    }

    public void studentAction(User user) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String action;

        do {
            studentMenu();
            action = scanner.nextLine();
            switch (action) {
                case "1" -> takeExam(scanner, user);
                case "2" -> checkAverage(user);
                case "3" -> {
                    System.out.println("Atsijungete");
                    loggedIn = false;
                    selectAction();
                }
                default -> System.out.println("Tokio veiksmo nera");
            }
        } while (!action.equals("3"));
    }

    public void checkAverage(User user) {
        int studentId = user.ID();
        results.stream().filter(u -> u.studentId() == studentId).forEach(System.out::println);
        double grade = results.stream().filter(u -> u.studentId() == studentId).mapToDouble(ExamResults::grade).average().orElse(0.0);
        System.out.printf("Jusu visu egzaminu vidurkis yra: %.0f\n", grade);
    }

    public void takeExam(Scanner scanner, User user) {
        if (exams.isEmpty()) {
            System.out.println("Nera jokiu egzaminu");
            return;
        }
        double correctAnswers = 0;
        exams.stream().map(Exam::title).forEach(System.out::println);
        Exam exam = getExamName(scanner, user);
        if (exam == null) {
            return;
        }
        double numberOfAnswers = exam.questions().size();
        for (Question question : exam.questions()) {
            System.out.println(question.questionNumb() + ". " + question.question());
            System.out.println(question.answers());
            int answer = getCorrectAnswer(scanner, (int) numberOfAnswers);
            if (question.correctAnswer() == answer) {
                correctAnswers++;
            }
        }
        int grade = (int) Math.round((correctAnswers / numberOfAnswers) * 10);
        System.out.println("Egzaminas baigtas");
        System.out.printf("Jusu pazymis yra: %s\n", grade);

        LocalDateTime localDateTime = LocalDateTime.now();


        results.add(new ExamResults(exam.id(), exam.title(), user.ID(), user.name(), user.surname(), grade, localDateTime));

    }


    private void createExam(Scanner scanner) {
        List<Question> questions = new ArrayList<>();

        String examTitle = getUniqueExamName(scanner);
        if (examTitle == null) {
            return;
        }

        System.out.println("Kiek bus klausymu?");
        int questionAmount = getWholeNumber(scanner);

        System.out.println("Kiek kiekvienas klausimas tures atsakymu?");
        int answerAmount = getWholeNumber(scanner);

        for (int i = 1; i < questionAmount + 1; i++) {
            List<String> answers = new ArrayList<>();
            System.out.printf("Iveskite %d-a klausyma\n", i);
            String question = scanner.nextLine();
            for (int j = 1; j < answerAmount + 1; j++) {
                System.out.printf("Iveskite %d-jo klausymo %d-a atsakyma\n", i, j);
                String answer = scanner.nextLine();
                answers.add(j + ". " + answer);
            }
            answers.forEach(System.out::println);
            System.out.printf("Iveskite %d-jo klausymo teisingo atsakymo numeri\n", i);
            int correctAnswer = getCorrectAnswer(scanner, answerAmount);
            questions.add(new Question(i, question, answers, correctAnswer));
        }
        int examId = exams.stream().map(Exam::id).max(Integer::compareTo).orElse(0) + 1;
        exams.add(new Exam(examId, examTitle, questions));

        exams.forEach(System.out::println);
    }

    @Override
    public int getCorrectAnswer(Scanner scanner, int maxQuestions) {
        while (true) {
            try {
                int number = Integer.parseInt(scanner.nextLine());
                if (number <= 0) {
                    System.out.println("Skaicius negali buti mažesnis už 0");
                    continue;
                } else if (number > maxQuestions) {
                    System.out.println("Tokio variantu nera");
                    continue;
                }
                return number;

            } catch (NumberFormatException e) {
                System.out.println("Blogai ivestas skaičius, bandykite vėl");
            }
        }
    }

    @Override
    public int getWholeNumber(Scanner scanner) {
        while (true) {
            try {
                int number = Integer.parseInt(scanner.nextLine());

                if (number <= 0) {
                    System.out.println("Atsakymas negali buti mažesnis už 0");
                    continue;
                }
                return number;
            } catch (NumberFormatException e) {
                System.out.println("Blogai ivestas skaičius, bandykite vėl");
            }
        }
    }

    private Exam getExamName(Scanner scanner, User user) {
            System.out.println("Parašykite pavadinima egzamino kuri norite laikyti");
            String examName = scanner.nextLine();
            Exam existingExam = exams.stream()
                    .filter(n -> n.title().equals(examName))
                    .findFirst()
                    .orElse(null);
            if (existingExam == null) {
                System.out.printf("Egzamino pavadinimu %s nera\n", examName);
                return null;
            }
            ExamResults examResults = results.stream().filter(e -> e.examName().equals(examName)).filter(u -> u.studentId() == user.ID()).findFirst().orElse(null);
            if (examResults == null) {
                return existingExam;
            }
            if (!LocalDateTime.now().isAfter(examResults.dateTime().plusHours(48))) {
                System.out.println("Perlaikyti si egzamina galesite tik pradejus 48 valandom po pirmo laikymo");
                return null;
            } else {
                results.remove(examResults);
                System.out.printf("Dabar perlaikote %s egzamina\n", examName);
            }
            return existingExam;
    }

    private String getUniqueExamName(Scanner scanner) {
            System.out.println("Irasykite egzamino pavadinima");
            String newExamName = scanner.nextLine();

            Exam existingExam = exams.stream()
                    .filter(n -> n.title().equals(newExamName))
                    .findFirst()
                    .orElse(null);

            if (existingExam != null) {
                System.out.printf("Egzaminas %s jau existuoja\n", newExamName);
                return null;
            }
            return newExamName;
    }

    public synchronized void readUserFile(String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        File file = createFile(fileName);

        if (fileName.equals(USER_FILE)) {
            if (file.length() != 0) {
                service.users = mapper.readValue(file, new TypeReference<>() {
                });
            }
        } else if (fileName.equals(EXAM_FILE)) {
            if (file.length() != 0) {
                exams = mapper.readValue(file, new TypeReference<>() {
                });
            }
        } else if (fileName.equals(RESULT_FILE)) {
            if (file.length() != 0) {
                results = mapper.readValue(file, new TypeReference<>() {
                });
            }
        }
    }

    public synchronized void writeUserFile(String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());


        File file = createFile(fileName);
        if (fileName.equals(USER_FILE)) {
            mapper.writeValue(file, service.users);
        } else if (fileName.equals(EXAM_FILE)) {
            mapper.writeValue(file, exams);
        } else if (fileName.equals(RESULT_FILE)) {
            mapper.writeValue(file, results);
        }
    }

    public synchronized File createFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.printf("Failo %s sukurti nepavyko", fileName);
            }
        }
        return file;
    }

}
