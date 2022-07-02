package com.example.demo.src.classes;

import com.example.demo.config.BaseException;
import com.example.demo.src.classes.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional
public class ClassService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ClassDao classDao;
    private final JwtService jwtService;


    @Autowired
    public ClassService(ClassDao classDao, JwtService jwtService) {
        this.classDao = classDao;
        this.jwtService = jwtService;
    }

    public GetOnlineClasses getOnlineClasses(long userId) throws BaseException {
        try {
            GetOnlineClasses  getOnlineClasses = classDao.getOnlineClasses(userId);
            return getOnlineClasses;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public GetOnlineClass getOnlineClass(long userId, long onlineClassId) throws BaseException {
        if(classDao.checkOnlineClass(onlineClassId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_ONLINE_CLASS);
        }

        try {
            GetOnlineClass getOnlineClass = classDao.getOnlineClass(userId, onlineClassId);
            return getOnlineClass;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<ClassReviews> getOnlineClassReview(long userId, long onlineClassId) throws BaseException {
        try {
            List<ClassReviews> onlineClassReviewsList = classDao.getOnlineClassReview(userId, onlineClassId);
            return onlineClassReviewsList;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetOfflineClasses getOfflineClasses(long userId) throws BaseException {
        try {
            GetOfflineClasses getOfflineClasses = classDao.getOfflineClasses(userId);
            return getOfflineClasses;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetOfflineClass getOfflineClass(long userId, long offlineClassId) throws BaseException {
        if(classDao.checkOfflineClass(offlineClassId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_OFFLINE_CLASS);
        }

        try {
            GetOfflineClass getOfflineClass = classDao.getOfflineClass(userId, offlineClassId);
            return getOfflineClass;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}