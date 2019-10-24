package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/** comments by Archana **/
 //The class UserBusinessService will define all the business logic required to implement user related functionalities
@Service
public class UserBusinessService {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordCryptographyProvider cryptographyProvider;

    //The signUp method for registering a new user checks for two conditions
    //userName already exists and email already exists. If the attributes found to be existed in the database, then the user is intimated that
    //with the corresponding error message and results in nonRegistration of the user
    //This method also encrypts the password upon the successfull registration of the user

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signUp(final UserEntity userEntity) throws SignUpRestrictedException, NullPointerException {

        String userName = userEntity.getUserName();
        UserEntity userNameExists = userDao.getUserByUserName(userName);
        String email = userEntity.getEmail();
        UserEntity emailExits = userDao.getUserByEmail(email);
        String password = userEntity.getPassword();

       if (userNameExists!=null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        } else if (emailExits!=null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
            String[] encryptedText = cryptographyProvider.encrypt(password);
            userEntity.setSalt(encryptedText[0]);
            userEntity.setPassword(encryptedText[1]);
            return userDao.createUser(userEntity);
        }
    }





