package lt.codeacademy.user;

public enum UserType {
    STUDENT(0),
    TEACHER(1);

    private final int roleCode;

    UserType(int roleCode) {
        this.roleCode = roleCode;
    }

    public int getRoleCode() {
        return roleCode;
    }
}
