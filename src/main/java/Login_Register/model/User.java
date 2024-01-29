package Login_Register.model;

import UserPage.model.ProjDb;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.jwt.build.Jwt;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;




@Entity
@Table(name = "user_db")
@UserDefinition
public class User extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "UserID")
    public long userid;

    @Username
    public String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Password
    public String password;

    @Roles
    public String role;

    @Column(unique = true)
    @Email
    public String email;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<ProjDb> projects;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
            this.username = username;

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
            this.password = BcryptUtil.bcryptHash(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
            this.email = email;
        }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(username, user.username)) return false;
        if (!Objects.equals(password, user.password)) return false;
        if (!Objects.equals(email, user.email)) return false;
        return Objects.equals(role, user.role);
    }
    public static User findByUsername(String username) {
        return User.find("username", username).firstResult();
    }



    public long getUserid() {
        return userid;
    }

    // Check if the user is an admin
    private boolean isAdmin() {
        if(role.equals("Admin")){
            return true;
        } else {
            return false;
        }
    }
    private boolean isUser() {
        if(role.equals("User")){
            return true;
        } else {
            return false;
        }
    }
    public String generate(User user) {
        List<String> groups;
        if (user.isAdmin()) {
            groups = Arrays.asList("User", "Admin");
        } else if(user.isUser()) {
            groups = Arrays.asList("User");
        } else {
            return "False";
        }

        long durationSeconds = 3600;
        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        long expirationTime = currentTimeInSeconds + durationSeconds;

        String token = Jwt.upn(user.getUsername())
                .groups(new HashSet<>(groups))
                .claim("username", user.getUsername())
                .claim("userId", user.getUserid())
                .expiresAt(expirationTime)
                .sign();
        System.out.println(token);
        return token;
    }
    @JsonIgnore
    public List<ProjDb> getProjects() {
        return projects;
    }
    @JsonIgnore
    public void setProjects(List<ProjDb> projects) {
        this.projects = projects;
    }
}