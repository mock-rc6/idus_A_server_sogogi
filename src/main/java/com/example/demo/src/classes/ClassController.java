package com.example.demo.src.classes;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.classes.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
public class ClassController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ClassService classService;
    private final JwtService jwtService;


    @Autowired
    public ClassController(ClassService classService, JwtService jwtService){
        this.classService = classService;
        this.jwtService = jwtService;
    }


    @ResponseBody
    @GetMapping("/online-classes/{userId}")
    public BaseResponse<GetOnlineClasses> getOnlineClasses(@PathVariable("userId") long userId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetOnlineClasses getOnlineClasses = classService.getOnlineClasses(userId);
            return new BaseResponse<>(getOnlineClasses);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ResponseBody
    @GetMapping("/online-classes/{userId}/{onClassId}")
    public BaseResponse<GetOnlineClass> getOnlineClass(@PathVariable("userId") long userId,
                                                       @PathVariable("onClassId") long onlineClassId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetOnlineClass getOnlineClass = classService.getOnlineClass(userId, onlineClassId);
            return new BaseResponse<>(getOnlineClass);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/online-classes/{userId}/{onClassId}/reviews")
    public BaseResponse<List<ClassReviews>> getOnlineClassReviews(@PathVariable("userId") long userId,
                                                                 @PathVariable("onClassId") long onlineClassId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<ClassReviews> onlineClassReviewsList = classService.getOnlineClassReviews(userId, onlineClassId);
            return new BaseResponse<>(onlineClassReviewsList);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/offline-classes/{userId}")
    public BaseResponse<GetOfflineClasses> getOfflineClasses(@PathVariable("userId") long userId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetOfflineClasses getOfflineClasses = classService.getOfflineClasses(userId);
            return new BaseResponse<>(getOfflineClasses);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/offline-classes/{userId}/{offClassId}")
    public BaseResponse<GetOfflineClass> getOfflineClass(@PathVariable("userId") long userId,
                                                         @PathVariable("offClassId") long offlineClassId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

           GetOfflineClass getOfflineClass = classService.getOfflineClass(userId, offlineClassId);
            return new BaseResponse<>(getOfflineClass);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/offline-classes/{userId}/{offClassId}/reviews")
    public BaseResponse<List<ClassReviews>> getOfflineClassReviews(@PathVariable("userId") long userId,
                                                                 @PathVariable("offClassId") long offlineClassId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<ClassReviews> offlineClassReviewsList = classService.getOfflineClassReviews(userId, offlineClassId);
            return new BaseResponse<>(offlineClassReviewsList);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/online-classes/{userId}/{onClassId}/{reviewId}")
    public BaseResponse<ClassReview> getOnlineClassReview(@PathVariable("userId") long userId,
                                                          @PathVariable("onClassId") long onlineClassId,
                                                          @PathVariable("reviewId") long reviewId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            ClassReview classReview = classService.getOnlineClassReview(userId, onlineClassId, reviewId);
            return new BaseResponse<>(classReview);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/offline-classes/{userId}/{offClassId}/{reviewId}")
    public BaseResponse<ClassReview> getOfflineClassReview(@PathVariable("userId") long userId,
                                                          @PathVariable("offClassId") long offlineClassId,
                                                          @PathVariable("reviewId") long reviewId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            ClassReview classReview = classService.getOfflineClassReview(userId, offlineClassId, reviewId);
            return new BaseResponse<>(classReview);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/offline-classes/{userId}/categories")
    public BaseResponse<GetCategories> getCategories(@PathVariable("userId") long userId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetCategories getCategories = classService.getCategories(userId);
            return new BaseResponse<>(getCategories);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/offline-classes/{userId}/categories/{categoryId}")
    public BaseResponse<List<NearOfflineClass>> getCategoryClasses(@PathVariable("userId") long userId, @PathVariable("categoryId") long categoryId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<NearOfflineClass> getCategoryClasses = classService.getCategoryClasses(userId, categoryId);
            return new BaseResponse<>(getCategoryClasses);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

    }


    @ResponseBody
    @GetMapping("/offline-classes/{userId}/address/{addressId}")
    public BaseResponse<List<NearOfflineClass>> getAddressClasses(@PathVariable("userId") long userId, @PathVariable("addressId") long addressId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<NearOfflineClass> getAddressClasses = classService.getAddressClasses(userId, addressId);
            return new BaseResponse<>(getAddressClasses);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ResponseBody
    @PostMapping("/online-classes/{userId}/{onClassId}/likes")
    public BaseResponse<String> setOnlineClassLike(@PathVariable("userId") long userId, @PathVariable("onClassId") long onlineClassId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            classService.setOnlineClassLike(userId, onlineClassId);
            String result = "찜 목록에 추가되었습니다";
            return new BaseResponse<>(result);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/offline-classes/{userId}/{offClassId}/likes")
    public BaseResponse<String> setOfflineClassLike(@PathVariable("userId") long userId, @PathVariable("offClassId") long offlineClassId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            classService.setOfflineClassLike(userId, offlineClassId);
            String result = "찜 목록에 추가되었습니다";
            return new BaseResponse<>(result);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/online-classes/{userId}/{onClassId}/likes")
    public BaseResponse<String> deleteOnlineClassLike(@PathVariable("userId") long userId, @PathVariable("onClassId") long onlineClassId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            classService.deleteOnlineClassLike(userId, onlineClassId);
            String result = "찜 목록에서 제외되었습니다";
            return new BaseResponse<>(result);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/offline-classes/{userId}/{offClassId}/likes")
    public BaseResponse<String> deleteOfflineClassLike(@PathVariable("userId") long userId, @PathVariable("offClassId") long offlineClassId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            classService.deleteOfflineClassLike(userId, offlineClassId);
            String result = "찜 목록에서 제외되었습니다";
            return new BaseResponse<>(result);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



}
