package com.example.studyolle.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

            //TODO email, nickname
            SignUpForm signUpForm = (SignUpForm) errors;
                // 중복 검사
            if(accountRepository.existsByEmail(signUpForm.getEmail())){
                //에러에 대한 처리
                errors.rejectValue("email","invalid.email",new Object[]{signUpForm.getEmail()},"이미 사용중인 이메일입니다.");
            }
               // 중복 검사
            if(accountRepository.existsByNickanem(signUpForm.getNickname())){
                //에러에 대한 처리
                errors.rejectValue("nickname","invalid.nickname",new Object[]{signUpForm.getEmail()},"이미 사용중인 닉네임입니다.");
            }

    }
}
