package com.example.studyolle.account;

import com.example.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }


    @GetMapping("/sign-up")
    public String signUpForm(Model model){

        model.addAttribute("signUpForm",new SignUpForm());

        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){
     if(errors.hasErrors()){
         return "account/sign-up";
     }

     Account account = accountService.processNewAccount(signUpForm);
     accountService.login(account);
        //TODO 회원 가입 처리
     return "redirect:/";
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrrentUser Account account,Model model){
        model.addAttribute("email",account.getEmail());

        return "account/check-email";
    }

    @GetMapping("/check-email-token")
    public String checkEmailTOken(String token, String email, Model model){
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        if(account == null){
            model.addAttribute("error","wrong.email");
            return view;
        }

        if(!account.isValidToken(token)){
            model.addAttribute("error","wrong.token");
            return view;
        }

        account.completeSignUp();
        accountService.login(account);
        model.addAttribute("numberOfUser",accountRepository.count());
        model.addAttribute("nickname",account.getNickname());

        return view;
    }


    @GetMapping("/resend-confirm-email")
    public String resendCOnfirmEmail(@CurrrentUser Account account, Model model){

        if(!account.canSendConfirmEmail()){
            model.addAttribute("error","인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email",account.getEmail());

            return "account/check-email";
        }

        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model,@CurrrentUser Account account){
        Account byNickname = accountRepository.findByNickname(nickname);

        if(nickname == null){
            throw new IllegalArgumentException((nickname + "에 해당하는 사용자가 없습니다."));
        }

        model.addAttribute("account",byNickname);
        model.addAttribute("isOwner",byNickname.equals(account));

        return "account/profile";
    }

}
