package group22;

public class String {
    public static boolean isValidStringCharacter(char input) {
        return (input == ' ') ||
                (input >= 'a' && input <= 'z') ||
                (input >= 'A' && input <= 'Z') ||
                (input >= '0' && input <= '9');
    }
}
