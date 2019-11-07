package com.upgrad.quora.service.business;

//import com.upgrad.quora.service.entity.

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerBusinessService {
    @Autowired
    private AnswerDao answerDao;

    @Autowired
    UserDao userDao;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    private QuestionBusinessService quesBusinessService;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity ansEntity,final String accessToken , final String questionId ) throws AuthorizationFailedException ,InvalidQuestionException{
        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }
        QuestionEntity questionEntity = quesBusinessService.validateQuestion(questionId);
        if (questionEntity == null){
            throw new InvalidQuestionException("QUES-001" , "The question entered is invalid");
        }
        ansEntity.setUuid(UUID.randomUUID().toString());
        ansEntity.setDate(ZonedDateTime.now());
        ansEntity.setQuestion(questionEntity);
        ansEntity.setUser(userAuthEntity.getUser());
        return answerDao.createAnswer(ansEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity getAnswerById(String uuid) throws AnswerNotFoundException {
        AnswerEntity answerEntity = answerDao.getAnsById(uuid);
        if (answerEntity == null)
        {
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }
        return answerEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity checkAnswerByUser(UserAuthTokenEntity userAuthEntity, AnswerEntity answerEntity) throws AuthorizationFailedException {

        UserEntity userEntity = userAuthEntity.getUser();

        if(userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002" ,"User is signed out.Sign in first to edit an answer");
        }
        String ansuid = answerEntity.getUuid();
        String uuid = userEntity.getUuid();
        AnswerEntity checkedAnswer = answerDao.checkAnswerByUser(ansuid,uuid);
        if(checkedAnswer==null)
        {
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit or delete the answer");
        }
        return checkedAnswer;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteAnswer(final String answerUuid,final String accessToken)throws AuthorizationFailedException,AnswerNotFoundException
    {

        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(accessToken);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime logoutTime = userAuthToken.getLogoutAt();
        if(logoutTime!= null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }
        AnswerEntity answerEntity = answerDao.getAnsById(answerUuid);
        if(answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }



        String role = userAuthToken.getUser().getRole();
        String ansOwnnerUuid = answerEntity.getUser().getUuid();
        String signedInUserUuid = userAuthToken.getUser().getUuid();

        if(role.equals("admin") || ansOwnnerUuid.equals(signedInUserUuid)) {
            answerDao.deleteAnswer(answerEntity);
        }
        else {
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
        }
        return answerUuid;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer(final AnswerEntity answerEntity ,final String ansId , final String accessToken)throws AuthorizationFailedException
    {
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(accessToken);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime logoutTime = userAuthToken.getLogoutAt();
        if(logoutTime!= null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        }
        String ansOwnnerUuid = answerEntity.getUser().getUuid();
        String signedInUserUuid = userAuthToken.getUser().getUuid();
        if(ansOwnnerUuid.equals(signedInUserUuid)) {
            answerDao.deleteAnswer(answerEntity);
        }
        else {
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");
        }
        AnswerEntity updatedAnswer = answerDao.updateAnswer(answerEntity);
        return updatedAnswer;
    }

    public List<AnswerEntity> getAllAnswers(String questionId , final String accessToken) throws AuthorizationFailedException,InvalidQuestionException
    {
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(accessToken);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime logoutTime = userAuthToken.getLogoutAt();
        if(logoutTime!= null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        }
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        if(questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        return answerDao.getAllAnswers(questionId);
    }


}
