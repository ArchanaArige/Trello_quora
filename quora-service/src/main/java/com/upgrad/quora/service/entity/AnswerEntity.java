package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "ANSWER" , schema = "public")
@NamedQueries(
        {@NamedQuery(name = "getAnswerById" , query = "select a from AnswerEntity a where a.uuid = :uuid"),
                @NamedQuery(name = "checkAnswerofUser" , query = "select a from AnswerEntity a INNER JOIN UserEntity u on a.user_id = u.id where a.uuid =:ansuuid and u.uuid = :uuuid"),
                @NamedQuery(name = "getAllAnswers" , query = "select a from AnswerEntity a INNER JOIN QuestionEntity q on a.question_id = q.id where q.uuid = :uuid")
        })

public class AnswerEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @NotNull
    private String uuid;

    @Column(name = "ANS")
    @NotNull
    private String ans;

    @Column(name = "DATE")
    @NotNull
    private ZonedDateTime date;

    //Relation between the users and Answers table is one to many, a user can have many Answers and similarly from
    //Answers to Users i,e but many Answers will be posted by one user
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user_id;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    private QuestionEntity question_id;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAnswer() {
        return ans;
    }

    public void setAnswer(String ans) {
        this.ans = ans;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUser() {
        return user_id;
    }

    public void setUser(UserEntity user_id) {
        this.user_id = user_id;
    }

    public QuestionEntity getQuestion() {
        return question_id;
    }

    public void setQuestion(QuestionEntity question_id) {
        this.question_id = question_id;
    }

    public ZonedDateTime getDate() {
        return date;
    }


    @Override
    public boolean equals (Object obj){
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode () {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString () {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
