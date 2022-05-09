package lt.codeacademy.user;


public record User(int ID, String name, String surname, UserType Type, String password) {
}

