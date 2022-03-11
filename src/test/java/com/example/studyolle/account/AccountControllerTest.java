package com.example.studyolle.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

            @Autowired private MockMvc mockMvc;

            @DisplayName("회원 가입 화면 보이는지 테스트")
            @Test
            void signUpForm() throws Exception{
                mockMvc.perform(get("/sign-up"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(view().name("account/sign-up"))
                        .andExpect(model().attributeExists("signUpForm"));
            }
}