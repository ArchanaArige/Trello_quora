package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    //This annotation PersistenceContext handles a set of entities that hold data to be persisted in the database
    @PersistenceContext
    private EntityManager entityManager;
    /** comments by Archana **/
    //This method persists the newly created question entity in the database
    public QuestionEntity persistQuestion(final QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /** comments by Archana **/
   //This method retrieves all the list of questions from the database
    public List<QuestionEntity> getAllQuestions(){
        try{
            return entityManager.createNamedQuery("ListofAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /** comments by Archana **/
    //This method retrieves the question based on question uuid, if found returns question else null
    public QuestionEntity getQuestionByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("QuestionByUuid", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /** comments by Archana **/
    //This method deletes the question record from database
    public void deleteQuestion(final QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }

    /** comments by Avia **/
    //This method updates the question in the database
    public QuestionEntity updateQuestion(final QuestionEntity questionEntity) {
        QuestionEntity updatedQ = entityManager.merge(questionEntity);
        return updatedQ;
    }

    /** comments by Avia **/
    //This method retrieves all the questions posted by a user and returns null if the list is empty.
    public List<QuestionEntity> getAllQuestionsByUser(final String userUuid){
        try{
            return entityManager.createNamedQuery("AllQuestionsByUser", QuestionEntity.class).setParameter("uuid",userUuid).getResultList();
        }
        catch (NoResultException nre){
            return null;
        }
    }
}

