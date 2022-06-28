package com.example.demo.src.user;

import com.example.demo.src.user.model.PostUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (emailAddr, phoneNumber, nickName, password) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getEmail(), postUserReq.getPhoneNumber()
                , postUserReq.getName(), postUserReq.getPassword()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String getUserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(getUserIdQuery,int.class);
    }


    public int checkEmail(String email) {
        String checkEmailQuery = "select exists (select userId from User where emailAddr = ?)";
        String checkParam = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, checkParam);
    }

    public int checkPhoneNumber(String phoneNumber) {
        String checkPhoneNumberQuery = "select exists (select userId from User where phoneNumber = ?)";
        String checkParam = phoneNumber;
        return this.jdbcTemplate.queryForObject(checkPhoneNumberQuery, int.class, checkParam);
    }
}
