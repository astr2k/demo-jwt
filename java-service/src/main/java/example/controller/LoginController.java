package example.controller;

import example.dto.LoginRequest;
import example.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for the authentication and token.
 */
@RestController
@Validated
@Tag(name = "Login")
public class LoginController {

	private final AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/token")
	@Operation(description = "Performs authentication and provides JWT token.")
	public String token(@RequestBody @Valid LoginRequest request) {
		return authenticationService.authenticate(request.getLogin(), request.getPassword());
	}

}
