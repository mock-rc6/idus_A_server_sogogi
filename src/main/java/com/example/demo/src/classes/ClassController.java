package com.example.demo.src.classes;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.classes.model.GetOnlineClasses;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/classes")
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
    @GetMapping("/{userId}/online")
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



}
