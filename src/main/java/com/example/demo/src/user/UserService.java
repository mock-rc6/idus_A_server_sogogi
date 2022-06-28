package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;

    }

    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //이메일 중복 체크
        if(userDao.checkEmail(postUserReq.getEmail()) == 1) {
            throw new BaseException(DUPLICATED_EMAIL);
        }
        //휴대폰 번호 중복 체크
        if(userDao.checkPhoneNumber(postUserReq.getPhoneNumber()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_PHONENUMBER);
        }

        //비밀 번호 암호화
        try {
            String pwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);

        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        try {
            int userId = userDao.createUser(postUserReq);
            String jwt = jwtService.createJwt(userId);

            return new PostUserRes(jwt, userId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
