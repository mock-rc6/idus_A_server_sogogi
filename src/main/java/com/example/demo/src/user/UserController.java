package com.example.demo.src.user;

import com.example.demo.src.products.model.GetCategoryProduct;
import com.example.demo.src.user.model.*;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;


import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserService userService;
    private final JwtService jwtService;


    @Autowired
    public UserController(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }


    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        if(postUserReq.getEmail().length() > 320) {
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_EMAIL);
        }

        String emailPattern = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-z]+$";
        if(!Pattern.matches(emailPattern, postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        if(postUserReq.getName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }

        if(postUserReq.getName().length() > 20) {
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_NAME);
        }

        if(postUserReq.getPhoneNumber() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUMBER);
        }

        String phoneNumberPattern = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";
        if(!Pattern.matches(phoneNumberPattern, postUserReq.getPhoneNumber())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUMBER);
        }

        if(postUserReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }

        String passwordPattern = "^(?=.*[a-z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}";
        if(!Pattern.matches(passwordPattern, postUserReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }

        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("login")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq) {
        if(postLoginReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        if(postLoginReq.getEmail().length() > 320) {
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_EMAIL);
        }

        String emailPattern = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-z]+$";
        if(!Pattern.matches(emailPattern, postLoginReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        if(postLoginReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }

        String passwordPattern = "^(?=.*[a-z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}";
        if(!Pattern.matches(passwordPattern, postLoginReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }

        try {
            PostLoginRes postLoginRes = userService.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<GetUserInfo> getUser(@PathVariable("userId") long userId)   {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetUserInfo getUserInfo = userService.getUser(userId);
            return new BaseResponse<>(getUserInfo);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/status")
    public BaseResponse<String> deleteUser(@PathVariable("userId") long userId)  {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.deleteUser(userId);

            String result = "아이디어스 회원 탈퇴 성공!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/details")
    public BaseResponse<GetUserDetail> getUserDetail(@PathVariable("userId") long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetUserDetail getUserDetail = userService.getUserDetail(userId);
            return new BaseResponse<>(getUserDetail);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/profiles")
    public BaseResponse<String> modifyUserProfile(@PathVariable("userId") long userId, @RequestBody PatchUserReq patchUserReq) {
        if(patchUserReq.getProfileImg().length() > 2083) {
            return new BaseResponse<>(PATCH_USERS_OVERFLOW_URL);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.modifyUserProfile(userId, patchUserReq.getProfileImg());
            String result = "사용자 프로필 변경에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    @ResponseBody
    @PatchMapping("/{userId}/names")
    public BaseResponse<String> modifyUserName(@PathVariable("userId") long userId, @RequestBody PatchUserReq patchUserReq) {
        if(patchUserReq.getUserName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }

        if(patchUserReq.getUserName().length() > 20) {
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_NAME);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.modifyUserName(userId, patchUserReq.getUserName());
            String result = "사용자 이름 변경에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/emails")
    public BaseResponse<String> modifyUserEmail(@PathVariable("userId") long userId, @RequestBody PatchUserReq patchUserReq) {

        if(patchUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        if(patchUserReq.getEmail().length() > 320) {
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_EMAIL);
        }

        String emailPattern = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-z]+$";
        if(!Pattern.matches(emailPattern, patchUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.modifyUserEmail(userId, patchUserReq.getEmail());
            String result = "사용자 이메일 주소 변경에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/birthDays")
    public BaseResponse<String> modifyUserBirthDay(@PathVariable("userId") long userId, @RequestBody PatchUserReq patchUserReq) {

        String birthDayPattern = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$";
        if(!Pattern.matches(birthDayPattern, patchUserReq.getBirthDay())) {
            return new BaseResponse<>(PATCH_USERS_INVALID_BIRTHDAY);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.modifyUserBirthDay(userId, patchUserReq.getBirthDay());
            String result = "사용자 생일 변경에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/genders")
    public BaseResponse<String> modifyUserGender(@PathVariable("userId") long userId, @RequestBody PatchUserReq patchUserReq) {
        System.out.println("gender : " + patchUserReq.getGender());

        if(patchUserReq.getGender() != 'F' && patchUserReq.getGender() != 'M') {
            return new BaseResponse<>(PATCH_USERS_INVALID_GENDER);
        }
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.modifyUserGender(userId, patchUserReq.getGender());
            String result = "사용자 성별 변경에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/phoneNumbers")
    public BaseResponse<String> modifyUserPhoneNumber(@PathVariable("userId") long userId, @RequestBody PatchUserReq patchUserReq) {

        if(patchUserReq.getPhoneNumber() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUMBER);
        }

        String phoneNumberPattern = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";
        if(!Pattern.matches(phoneNumberPattern, patchUserReq.getPhoneNumber())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUMBER);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.modifyUserPhoneNumber(userId, patchUserReq.getPhoneNumber());
            String result = "사용자 휴대폰 번호 변경에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/baskets")
    public BaseResponse<GetBasketProduct> getBasketProducts(@PathVariable("userId") long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetBasketProduct getBasketProduct = userService.getBasketProducts(userId);
            return new BaseResponse<>(getBasketProduct);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/order-lists")
    public BaseResponse<List<GetOrderList>> getOrderList(@PathVariable("userId") long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetOrderList> getOrderLists = userService.getOrderList(userId);
            return new BaseResponse<>(getOrderLists);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/product-likes")
    public BaseResponse<List<GetLikeProduct>> getLikeProducts(@PathVariable("userId") long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetLikeProduct> getLikeProductList = userService.getLikeProducts(userId);
            return new BaseResponse<>(getLikeProductList);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/online-class-likes")
    public BaseResponse<List<GetLikeOnlineClasses>> getLikeOnlineClasses(@PathVariable("userId") long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetLikeOnlineClasses> getLikeOnlineClasses = userService.getLikeOnlineClasses(userId);
            return new BaseResponse<>(getLikeOnlineClasses);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}

