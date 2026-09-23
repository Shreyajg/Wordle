package com.example.guessgame.service;

import com.example.guessgame.model.User;
import com.example.guessgame.repository.UserRepository;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.guessgame.exception.GameException;
import com.example.guessgame.model.Role;

@Service 
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder)
    {
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GameException("User not found"));
    }
    public User register(String username,String password,String confirmPassword)
    {
        if(username.length()<5) throw new GameException("username should have a minimum of 5 characters");
        boolean upper=false;
        boolean lower=false;
        for(int i=0;i<username.length();i++)
        {
            char c=username.charAt(i);
            if(Character.isAlphabetic(c))
            {
                if(Character.isUpperCase(c))
                {
                    upper=true;
                }
                else if(Character.isLowerCase(c))
                {
                    lower=true;
                }
            }
            
        }
        if(!upper || !lower) throw new GameException("Username must contain atleast one upper and lower letters");
        
        Optional<User> userRecord=userRepository.findByUsername(username);
        if(userRecord.isPresent()) throw new GameException("Username already exists try logging in");

        if(password.length()<5) throw new GameException("Password should have a minimum length of 5");
        upper=false;
        lower=false;
        boolean special=false;
        boolean num=false;
        for(int i=0;i<password.length();i++)
        {
            char c=password.charAt(i);
            if(Character.isAlphabetic(c))
            {
                if(Character.isUpperCase(c))
                {
                    upper=true;
                }
                else if(Character.isLowerCase(c))
                {
                    lower=true;
                }
            }
            else if(Character.isDigit(c))
            {
                num=true;
            }
            if (c == '$' || c == '%' || c == '*') {
                special = true;
            }
        }

        if(!upper || !lower || !num || !special) throw new GameException("Password should have atleast one lower case letter atleast one upper case letter atleast one number and atleast on of the ($,% or *)");
        if(!password.equals(confirmPassword))
        {
            throw new GameException("Passwords dont match");
        }
        String passwordHash = passwordEncoder.encode(password);
        User user=new User(username,passwordHash,Role.PLAYER);
        return userRepository.save(user);
    }

    public User login(String username,String password)
    {
        Optional<User> user=userRepository.findByUsername(username);
        if(!user.isPresent()) throw new GameException("User not found please register");
        User currUser=user.get();
        if(passwordEncoder.matches(password,currUser.getPasswordHash()))
        {
            return currUser;
        }
        else{
            throw new GameException("Username or password is incorrect");
        }

    }

}
