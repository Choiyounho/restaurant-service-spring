package com.soten.eatgo.user.controller;

import com.soten.eatgo.global.exception.EmailNotExistedException;
import com.soten.eatgo.global.exception.PasswordWrongException;
import com.soten.eatgo.user.controller.SessionController;
import com.soten.eatgo.user.domain.User;
import com.soten.eatgo.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("/session : 인증 성공")
    public void createWithValidAttributes() throws Exception {
        String email = "maxosa@naver.com";
        String password = "test";

        User mockUser = User.builder()
                .password("ACCESSTOKEN")
                .build();

        given(userService.authenticate(email, password)).willReturn(mockUser);

        mvc.perform(post("/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"maxosa@naver.com\",\"password\":\"test\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "/session"))
                .andExpect(content().string("{\"accessToken\":\"ACCESSTOKE\"}"));

        verify(userService).authenticate(eq("maxosa@naver.com"), eq("test"));
    }

    @Test
    @DisplayName("/session : 인증 실패")
    public void createWithInValidAttributes() throws Exception {
        given(userService.authenticate("maxosa@naver.com", "x"))
                .willThrow(PasswordWrongException.class);

        mvc.perform(post("/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"maxosa@naver.com\",\"password\":\"x\"}"))
                .andExpect(status().isBadRequest());

        verify(userService).authenticate(eq("maxosa@naver.com"), eq("x"));
    }

    @Test
    @DisplayName("/session : 이메일이 존재 하지 않음")
    public void createWithNotExistedEmail() throws Exception {
        given(userService.authenticate("x@naver.com", "test"))
                .willThrow(EmailNotExistedException.class);

        mvc.perform(post("/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"x@naver.com\",\"password\":\"test\"}"))
                .andExpect(status().isBadRequest());

        verify(userService).authenticate(eq("x@naver.com"), eq("test"));
    }

    @Test
    @DisplayName("/session : 비밀번호 틀림")
    public void createWithWrongPassword() throws Exception {
        given(userService.authenticate("maxosa@naver.com", "x"))
                .willThrow(PasswordWrongException.class);

        mvc.perform(post("/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"maxosa@naver.com\",\"password\":\"x\"}"))
                .andExpect(status().isBadRequest());

        verify(userService).authenticate(eq("maxosa@naver.com"), eq("x"));
    }

}
