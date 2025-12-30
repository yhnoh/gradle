import com.fasterxml.jackson.core.JsonProcessingException;
import org.modulea.User;

public class Main {

    public static void main(String[] args) throws JsonProcessingException {
        User user = new User("username");
        System.out.println(user.toJson());
    }
}
