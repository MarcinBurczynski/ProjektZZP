import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class tak {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("admin123");
        System.out.println(encodedPassword); // Wydrukuj zaszyfrowane hasło
    }
}
