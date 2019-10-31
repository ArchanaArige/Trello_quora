package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class QuestionBusinessService {

    @Autowired
    UserDao userDao;

    @Autowired
    QuestionDao questionDao;

@Transactional(propagation = Propagation.REQUIRED)
   public String deleteQuestion(final String questionUuid, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
       UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(accessToken);

       /** comments by Archana **/
       //If the accessToken of admin or QuestionOwner doesnt exist in the database throw following Exception
       //It means that if the user hasnt signedin, then the basic Authentication is not done and the accessToken is not generated
       if (userAuthToken == null) {
           throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
       }
       //we have logoutAt attribute in the userAuth table, upon successfull signout of the application
       //the user logoutAt attribute will be updated. So if the logoutAt is not null then it means that user has signed out
       ZonedDateTime logoutTime = userAuthToken.getLogoutAt();
       if(logoutTime!= null) {
           throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
       }
       //If the questionUuid doesnt exist in the database throw following exception
       QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);
        if(questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
       //The owneroftheQuestion or an admin will have privelege of deleting the Question
       //The user with the role non-admin and non-owner of the Question is trying to delete the Question
       //then following exception is thrown
        String role = userAuthToken.getUser().getRole();
        String questionOwnnerUuid = questionEntity.getUser().getUuid();
        String signedInUserUuid = userAuthToken.getUser().getUuid();

        if(role.equals("admin") || questionOwnnerUuid.equals(signedInUserUuid)) {
            questionDao.deleteQuestion(questionEntity);
        }else {
            throw new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");
        }

        return questionUuid;
   }
}
