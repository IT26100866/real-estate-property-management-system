import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("dilshan: " + encoder.encode("userPass_dilshan123"));
        System.out.println("chaminda: " + encoder.encode("agentPass_chaminda123"));
        System.out.println("admin: " + encoder.encode("adminPass_master123"));
    }
}
