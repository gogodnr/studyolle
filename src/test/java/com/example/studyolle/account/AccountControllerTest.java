package com.example.studyolle.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//csrf함수사용을 위해 import 필요
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

            @Autowired private MockMvc mockMvc;

            @Autowired private AccountRepository accountRepository;

            @MockBean JavaMailSender javaMailSender;

            @DisplayName("회원 가입 화면 보이는지 테스트")
            @Test
            void signUpForm() throws Exception{
                mockMvc.perform(get("/sign-up"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(view().name("account/sign-up"))
                        .andExpect(model().attributeExists("signUpForm"));
            }

            @DisplayName("회원 가입 처리 - 입력값 오류")
            @Test
            void signUpSubmit_with_wrong_input() throws Exception{
                mockMvc.perform(post("/sign-up")
                        .param("nickname","sangwook")
                        .param("email","work.sangwook@gmail.com")
                        .param("password" ,"12345678")
                        .with(csrf()))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(view().name("redirect:/"));

                assertTrue(accountRepository.existsByEmail("work.sangwook@gmail.com"));
                then(javaMailSender).should().send(any(SimpleMailMessage.class));

            }
}
