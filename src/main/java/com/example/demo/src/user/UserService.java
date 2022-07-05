package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.products.model.Category;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            long userId = userDao.createUser(postUserReq);
            String jwt = jwtService.createJwt(userId);

            return new PostUserRes(jwt, userId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException {
        //이메일 존재하는지 체크
        if(userDao.checkEmail(postLoginReq.getEmail()) == 0) {
            throw new BaseException(FAILED_TO_LOGIN);
        }

        User user = userDao.getPwd(postLoginReq);

        //비밀 번호 암호화
        String encryptedPassword;
        try {
            encryptedPassword = new SHA256().encrypt(postLoginReq.getPassword());
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        if(user.getPassword().equals(encryptedPassword)) {
            long userId = user.getUserId();
            String jwt = jwtService.createJwt(userId);
            return new PostLoginRes(jwt, userId);
        }
        else {
            throw new BaseException(FAILED_TO_LOGIN);
        }



    }

    public GetUserInfo getUser(long userId) throws BaseException {
        try {
            GetUserInfo getUserInfo = userDao.getUser(userId);
            return getUserInfo;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteUser(long userId) throws BaseException {
        try {
             userDao.deleteUser(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetUserDetail getUserDetail(long userId) throws BaseException {
        try {
            GetUserDetail getUserDetail = userDao.getUserDetail(userId);
            return getUserDetail;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserProfile(long userId, String imgUrl) throws BaseException {
        try {
            userDao.modifyUserProfile(userId, imgUrl);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void modifyUserName(long userId, String userName) throws BaseException {
        try {
            userDao.modifyUserName(userId, userName);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserEmail(long userId, String email) throws BaseException {
        try {
            userDao.modifyUserEmail(userId, email);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserBirthDay(long userId, String birthDay) throws BaseException {
        try {
            userDao.modifyUserBirthDay(userId, birthDay);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserGender(long userId, Character gender) throws BaseException {
        try {
            userDao.modifyUserGender(userId, gender);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserPhoneNumber(long userId, String phoneNumber) throws BaseException {
        try {
            userDao.modifyUserPhoneNumber(userId, phoneNumber);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetBasketProduct getBasketProducts(long userId) throws BaseException {
        try {
             GetBasketProduct getBasketProduct = userDao.getBasketProducts(userId);
             return getBasketProduct;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
