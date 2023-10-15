package com.yeon.colorpalette.auth.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yeon.colorpalette.api.ApiResponse;
import com.yeon.colorpalette.api.ResponseMessage;
import com.yeon.colorpalette.auth.application.AuthService;
import com.yeon.colorpalette.auth.application.response.AccessTokenResponse;
import com.yeon.colorpalette.auth.application.response.LoginResponse;
import com.yeon.colorpalette.auth.domain.Account;
import com.yeon.colorpalette.auth.presentation.request.LoginRequest;
import com.yeon.colorpalette.config.resolver.Login;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
		return ApiResponse.ok(ResponseMessage.LOGIN_SUCCESS.getMessage(), authService.login(request));
	}

	@DeleteMapping("/logout")
	public ApiResponse<Void> logout(@Login Account account) {
		authService.logout(account);
		return ApiResponse.noData(HttpStatus.OK, ResponseMessage.LOGOUT_SUCCESS.getMessage());
	}

	@PostMapping("/auth/reissue")
	public ApiResponse<AccessTokenResponse> reissueAccessToken(@Login Account account) {
		return ApiResponse.ok(ResponseMessage.REISSUE_ACCESS_TOKEN_SUCCESS.getMessage(), authService.reissueAccessToken(account));
	}

	@GetMapping("/oauth")
	public ApiResponse<LoginResponse> loginByOAuth(@RequestParam String code) {
		return ApiResponse.ok(ResponseMessage.LOGIN_SUCCESS.getMessage(), authService.loginByOAuth(code));
	}

}
