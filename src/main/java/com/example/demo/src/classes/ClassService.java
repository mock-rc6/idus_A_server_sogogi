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

    public List<ClassReviews> getOnlineClassReviews(long userId, long onlineClassId) throws BaseException {
        try {
            List<ClassReviews> onlineClassReviewsList = classDao.getOnlineClassReviews(userId, onlineClassId);
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

    public List<ClassReviews> getOfflineClassReviews(long userId, long offlineClassId) throws BaseException {
        try {
            List<ClassReviews> offlineClassReviewsList = classDao.getOfflineClassReviews(userId, offlineClassId);
            return offlineClassReviewsList;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public ClassReview getOnlineClassReview(long userId, long onlineClassId, long reviewId) throws  BaseException {
        if(classDao.checkOnlineClassReviewExists(onlineClassId, reviewId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_ONLINE_CLASS_REVIEW);
        }

        try {
            ClassReview classReview = classDao.getOnlineClassReview(userId, onlineClassId, reviewId);
            return classReview;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public ClassReview getOfflineClassReview(long userId, long offlineClassId, long reviewId) throws  BaseException {
        if(classDao.checkOfflineClassReviewExists(offlineClassId, reviewId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_OFFLINE_CLASS_REVIEW);
        }

        try {
            ClassReview classReview = classDao.getOfflineClassReview(userId, offlineClassId, reviewId);
            return classReview;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetCategories getCategories(long userId) throws BaseException {
        try {
            GetCategories getCategories = classDao.getCategories(userId);
            return getCategories;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<NearOfflineClass> getCategoryClasses(long userId, long categoryId) throws BaseException {
        if(classDao.checkCategory(categoryId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_OFFLINE_CATEGORY);
        }

        try {
            List<NearOfflineClass> getCategoryClasses = classDao.getCategoryClasses(userId, categoryId);
            return getCategoryClasses;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<NearOfflineClass> getAddressClasses(long userId, long addressId) throws BaseException {
        if(classDao.checkAddress(addressId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_OFFLINE_ADDRESS);
        }

        try {
            List<NearOfflineClass> getCategoryClasses = classDao.getAddressClasses(userId, addressId);
            return getCategoryClasses;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void setOnlineClassLike(long userId, long onlineClassId) throws BaseException {
        if(classDao.checkOnlineClass(onlineClassId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_ONLINE_CLASS);
        }
        if(classDao.checkOnlineClassLike(userId, onlineClassId) == 1) {
            throw new BaseException(DUPLICATED_ONLINE_LIKE);
        }

        try {
            classDao.setOnlineClassLike(userId, onlineClassId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void setOfflineClassLike(long userId, long offlineClassId) throws BaseException {
        if(classDao.checkOfflineClass(offlineClassId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_OFFLINE_CLASS);
        }
        if(classDao.checkOfflineClassLike(userId, offlineClassId) == 1) {
            throw new BaseException(DUPLICATED_OFFLINE_LIKE);
        }

        try {
            classDao.setOfflineClassLike(userId, offlineClassId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteOnlineClassLike(long userId, long onlineClassId) throws BaseException {

        if(classDao.checkOnlineClass(onlineClassId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_ONLINE_CLASS);
        }
        if(classDao.checkOnlineClassLike(userId, onlineClassId) == 0) {
            throw new BaseException(EMPTY_ONLINE_LIKE);
        }

        try {
            classDao.deleteOnlineClassLike(userId, onlineClassId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteOfflineClassLike(long userId, long offlineClassId) throws BaseException {

        if(classDao.checkOfflineClass(offlineClassId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_OFFLINE_CLASS);
        }
        if(classDao.checkOfflineClassLike(userId, offlineClassId) == 0) {
            throw new BaseException(EMPTY_OFFLINE_LIKE);
        }

        try {
            classDao.deleteOfflineClassLike(userId, offlineClassId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
