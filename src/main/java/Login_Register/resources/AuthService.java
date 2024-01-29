package Login_Register.resources;

import Login_Register.model.LoginRequest;
import Login_Register.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import java.util.List;
import java.util.regex.Pattern;

@ApplicationScoped
public class AuthService {

    private static final Logger LOG = Logger.getLogger(AuthService.class);

    public boolean verifyPassword(String bCryptPasswordHash, String passwordToVerify) {
        try {
            WildFlyElytronPasswordProvider provider = new WildFlyElytronPasswordProvider();
            PasswordFactory passwordFactory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT, provider);
            Password userPasswordDecoded = ModularCrypt.decode(bCryptPasswordHash);
            Password userPasswordRestored = passwordFactory.translate(userPasswordDecoded);
            return passwordFactory.verify(userPasswordRestored, passwordToVerify.toCharArray());
        } catch (Exception e) {
            LOG.errorf("Password verification failed: %s", e.getMessage());
            return false;
        }
    }

    public boolean isValidLogin(LoginRequest loginRequest) {
        List<User> userList = User.list("username", loginRequest.getUsername());
        if (!userList.isEmpty()) {
            User user = userList.get(0);
            return verifyPassword(user.getPassword(), loginRequest.getPassword());
        } else {
            LOG.warnf("No user found with username: %s", loginRequest.getUsername());
            return false;
        }
    }

    public User getUser(LoginRequest loginRequest) {
        List<User> userList = User.list("username", loginRequest.getUsername());
        return userList.isEmpty() ? null : userList.get(0);
    }

    public boolean isValidEmail(String email) {
        if (email == null) return false;
        String repair = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        return Pattern.matches(repair, email);
    }
    public boolean isValidPassword(String password) {
        // Minimum length of 5 characters, at least one uppercase letter, and at least one special character
        String regex = "^(?=.*[A-Z])(?=.*[!@#$%^&*()-+]).{5,}$";
        return Pattern.matches(regex, password);
    }
    public boolean userExists(String username) {
        return (User.count("username", username) > 0);
    }

    public boolean emailExists(String email) {
        return (User.count("email", email) > 0);
    }
}
