package com.example.demo.src.classes;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.classes.model.GetOfflineClasses;
import com.example.demo.src.classes.model.GetOnlineClass;
import com.example.demo.src.classes.model.GetOnlineClasses;
import com.example.demo.src.classes.model.OnlineClassReviews;
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
    public BaseResponse<List<OnlineClassReviews>> getOnlineClassReview(@PathVariable("userId") long userId,
                                                                       @PathVariable("onClassId") long onlineClassId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<OnlineClassReviews> onlineClassReviewsList = classService.getOnlineClassReview(userId, onlineClassId);
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

}
