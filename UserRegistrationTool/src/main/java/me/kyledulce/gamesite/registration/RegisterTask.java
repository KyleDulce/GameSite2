package me.kyledulce.gamesite.registration;

import me.kyledulce.gamesite.registration.file.InputTask;
import me.kyledulce.gamesite.registration.file.OutputTask;
import me.kyledulce.gamesite.registration.security.UserSecurityDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegisterTask implements ApplicationRunner {
    private final InputTask inputTask;
    private final OutputTask outputTask;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterTask(InputTask inputTask, OutputTask outputTask, PasswordEncoder passwordEncoder) {
        this.inputTask = inputTask;
        this.outputTask = outputTask;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<UserInput> input = inputTask.getData();
        for(UserInput userInput : input) {
            outputTask.addUser(regiserUser(userInput));
        }
        outputTask.flush();
    }

    private UserSecurityDetails regiserUser(UserInput userInput) {
        List<GrantedAuthority> authorities = userInput.getRoles()
                .stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                .toList();

        return new UserSecurityDetails(
                authorities,
                authorities.stream().map(GrantedAuthority::getAuthority).toList(),
                userInput.getUsername(),
                passwordEncoder.encode(userInput.getPassword())
        );
    }
}
