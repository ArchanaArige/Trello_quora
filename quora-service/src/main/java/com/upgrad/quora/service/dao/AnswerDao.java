package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager em;

    public AnswerEntity createAnswer(AnswerEntity ansEntity){
        em.persist(ansEntity);
        return ansEntity;
    }

    public AnswerEntity getAnsById(String uuid){
        try{
          return em.createNamedQuery("getAnswerById",AnswerEntity.class).setParameter("uuid",uuid).getSingleResult();
        }
        catch (NoResultException msg){
            return null;
        }
    }

    public AnswerEntity checkAnswerByUser(String ansuuid, String uuuid)
    {

        try {
            return em.createNamedQuery("checkAnswerByUser", AnswerEntity.class).setParameter("ansuuid", ansuuid).setParameter("uuuid",uuuid).getSingleResult();
        }catch (NoResultException msg)
        {
            return null;
        }
    }

    public List<AnswerEntity> getAllAnswers(String quesId)
    {
        return em.createNamedQuery("getAllAnswers",AnswerEntity.class)
                .setParameter("uuid",quesId).getResultList();
    }

    public AnswerEntity updateAnswer(AnswerEntity ansEntity)
    {
        return em.merge(ansEntity);
    }

    public AnswerEntity deleteAnswer(AnswerEntity ansEntity)
    {
        em.remove(ansEntity);
        return ansEntity;
    }
}
