package example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Defines structure of login request body.")
public class LoginRequest {
    @NotBlank
    @Schema(example = "johndoe")
    private String login;

    @NotBlank
    @Schema(example = "secret")
    private String password;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
