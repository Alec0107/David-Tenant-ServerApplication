package SERVER.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;


@Getter
@Setter
@AllArgsConstructor
@ToString(exclude = "password")
public class UserDetails {

    private int id;
    private String username;
    private String email;
    private String password;

    public UserDetails(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }


}
