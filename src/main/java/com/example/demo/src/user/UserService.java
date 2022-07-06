package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.products.model.Category;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public List<GetOrderList> getOrderList(long userId) throws BaseException {
        try {
            List<GetOrderList> getOrderLists = userDao.getOrderList(userId);
            return getOrderLists;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetLikeProduct> getLikeProducts(long userId) throws BaseException {
        try {
            List<GetLikeProduct> getLikeProductList = userDao.getLikeProducts(userId);
            return getLikeProductList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetLikeOnlineClasses> getLikeOnlineClasses(long userId) throws BaseException {
        try {
            List<GetLikeOnlineClasses> getLikeOnlineClasses = userDao.getLikeOnlineClasses(userId);
            return getLikeOnlineClasses;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetLikeOfflineClasses> getLikeOfflineClasses(long userId) throws BaseException {
        try {
            List<GetLikeOfflineClasses> getLikeOfflineClasses = userDao.getLikeOfflineClasses(userId);
            return getLikeOfflineClasses;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public KakaoUser kakaoLogIn(String code) throws BaseException {
        String accessToken = "";
        String refreshToken = "";
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=ce6eb7f79713e251c5d2932d6b45b0e7"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:9000/users/kakao-login"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = connection.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + accessToken);
            System.out.println("refresh_token : " + refreshToken);

            br.close();
            bw.close();


            Long userId = getUserByToken(accessToken);

            if(userDao.checkUserId(userId) == 0) {
                throw new BaseException(INVALID_KAKAO_USER);
            }

            String jwt = jwtService.createJwt(userId);
            return new KakaoUser(userId, jwt);

        } catch (Exception exception) {
            throw new BaseException(RESPONSE_ERROR);
        }

    }

    public Long getUserByToken(String accessToken) {
        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response.getBody());

        System.out.println(element.toString());


        Long id = element.getAsJsonObject().get("id").getAsLong();

        System.out.println("id : " + id);

        return id;

    }
}
