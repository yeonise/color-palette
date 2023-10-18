package com.yeon.colorpalette.palette.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yeon.colorpalette.api.ApiResponse;
import com.yeon.colorpalette.api.ResponseMessage;
import com.yeon.colorpalette.auth.domain.Account;
import com.yeon.colorpalette.config.resolver.Login;
import com.yeon.colorpalette.palette.application.PaletteService;
import com.yeon.colorpalette.palette.application.response.PaletteCreateResponse;
import com.yeon.colorpalette.palette.presentation.request.PaletteCreateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/palettes")
@RestController
public class PaletteController {

	private final PaletteService paletteService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public ApiResponse<PaletteCreateResponse> createPalette(@Valid @RequestBody PaletteCreateRequest request, @Login Account account) {
		return ApiResponse.of(HttpStatus.CREATED, ResponseMessage.PALETTE_CREATE_SUCCESS.getMessage(),
			paletteService.create(request.toService(account.getId())));
	}

}
