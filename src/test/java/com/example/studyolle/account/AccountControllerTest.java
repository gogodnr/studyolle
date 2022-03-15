package com.example.studyolle.account;

import com.example.studyolle.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
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

            @DisplayName("회원 가입 처리 - 입력값 정상")
            @Test
            void signUpSubmit_with_correct_input() throws Exception{
                mockMvc.perform(post("/sign-up")
                                .param("nickname","sangwook")
                                .param("email","work.sangwook@gmail.com")
                                .param("password" ,"12345678")
                                .with(csrf()))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(view().name("redirect:/"));

                Account account = accountRepository.findByEmail("work.sangwook@gmail.com");
                assertNotNull(account);
                assertNotEquals(account.getPassword(),"12345678");

                assertTrue(accountRepository.existsByEmail("work.sangwook@gmail.com"));
                then(javaMailSender).should().send(any(SimpleMailMessage.class));

            }

            @DisplayName("회원 가입 처리 - 입력값 오류")
            @Test
            void signUpSubmit_with_wrong_input() throws Exception{
                mockMvc.perform(post("/sign-up")
                        .param("nickname","sangwook")
                        .param("email","work.sangwook@gmail.com")
                        .param("password" ,"12345678")
                        .with(csrf()))
                        .andExpect(status().isOk())
                        .andExpect(view().name("account/sign-up"))
                        .andExpect(authenticated().withUsername("sangwook"));

                Account account = accountRepository.findByEmail("work.sangwook@gmail.com");
                assertNotNull(account);
                assertNotEquals(account.getPassword(),"12345678");

                assertTrue(accountRepository.existsByEmail("work.sangwook@gmail.com"));
                then(javaMailSender).should().send(any(SimpleMailMessage.class));

            }


            @DisplayName("인증 메일 확인 - 입력값 정상")
            @Test
            void checkEmailToken_with_correct_inpuit() throws Exception{
                Account account = Account.builder()
                        .email("test@email.com")
                        .password("123455678")
                        .nickname("sangwook")
                        .build();
                Account newAccount = accountRepository.save(account);
                newAccount.generateEmailCheckToken();
        
                mockMvc.perform(get("/check-email-token")
                                .param("token",newAccount.getEmailCheckToken())
                                .param("email",newAccount.getEmail()))
                        .andExpect(status().isOk())
                        .andExpect(model().attributeDoesNotExist("error"))
                        .andExpect(model().attributeExists("nickname"))
                        .andExpect(model().attributeExists("numberOfUser"))
                        .andExpect(view().name("account/checked-email"))
                        .andExpect(authenticated().withUsername("sangwook"));
            }            
            @DisplayName("인증 메일 확인 - 입력값 오류")
            @Test
            void checkEmailToken_with_wrong_inpuit() throws Exception{
                Account account = Account.builder()
                        .email("test@email.com")
                        .password("123455678")
                        .nickname("sangwook")
                        .build();
                Account newAccount = accountRepository.save(account);
                newAccount.generateEmailCheckToken();

                mockMvc.perform(get("/check-email-token")
                        .param("token",newAccount.getEmailCheckToken())
                        .param("email",newAccount.getEmail()))
                        .andExpect(status().isOk())
                        .andExpect(model().attributeDoesNotExist("error"))
                        .andExpect(model().attributeExists("nickname"))
                        .andExpect(model().attributeExists("numberOfUser"))
                        .andExpect(view().name("account/checked-email"))
                        .andExpect(unauthenticated());
            }

}
