package com.example.demo.src.user;

import com.example.demo.src.user.model.*;
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


    public long createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (emailAddr, phoneNumber, nickName, password) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getEmail(), postUserReq.getPhoneNumber()
                , postUserReq.getName(), postUserReq.getPassword()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String getUserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(getUserIdQuery,long.class);
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

    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select userId, password from User where emailAddr = ?";
        String getPwdParam = postLoginReq.getEmail();
        return this.jdbcTemplate.queryForObject(getPwdQuery, (rs, rowNum) ->
                new User(rs.getLong("userId"), rs.getString("password")), getPwdParam);
    }

    public GetUserInfo getUser(long userId) {
        String getUserQuery = "select U.profileImg, case when U.grade = 1 then '아기손' when U.grade = 2 then '곰손' when U.grade = 3 then '은손' else '금손' end as grade, U.nickName, U.rewardPoint, UC.countCoupon\n" +
                "from User U\n" +
                "left outer join (select userId, count(couponId) as countCoupon from UserCoupon group by (userId)) UC using (userId)\n" +
                "where userId = ?";
        return this.jdbcTemplate.queryForObject(getUserQuery, (rs, rowNum) -> new GetUserInfo(
                rs.getString("profileImg"),
                rs.getString("grade"),
                rs.getString("nickName"),
                rs.getInt("rewardPoint"),
                rs.getInt("countCoupon")), userId);
    }

    public void deleteUser(long userId) {
        String deleteUserQuery = "update User set idus.User.status = 0 where userId = ?";
        this.jdbcTemplate.update(deleteUserQuery, userId);
    }

    public GetUserDetail getUserDetail(long userId) {
        String getUserQuery = "select profileImg, nickName, emailAddr, date_format(birthDay, '%Y년 %c월 %e일') as birthDay, if(isnull(gender), null, if(gender='F', '여성', '남성')) as gender, phoneNumber, address\n" +
                "from User\n" +
                "where userId = ?";
        return this.jdbcTemplate.queryForObject(getUserQuery, (rs, rowNum) -> new GetUserDetail(
                rs.getString("profileImg"),
                rs.getString("nickName"),
                rs.getString("emailAddr"),
                rs.getString("birthDay"),
                rs.getString("gender"),
                rs.getString("phoneNumber"),
                rs.getString("address")), userId);
    }
}
