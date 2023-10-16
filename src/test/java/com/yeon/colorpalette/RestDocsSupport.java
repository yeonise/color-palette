package com.yeon.colorpalette;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeon.colorpalette.auth.application.AuthService;
import com.yeon.colorpalette.member.application.MemberService;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
public abstract class RestDocsSupport {

	@Autowired
	private WebApplicationContext context;

	protected MockMvc mockMvc;
	protected ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp(RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(MockMvcRestDocumentation.documentationConfiguration(provider))
			.build();
	}

	@MockBean
	protected MemberService memberService;

	@MockBean
	protected AuthService authService;

}
