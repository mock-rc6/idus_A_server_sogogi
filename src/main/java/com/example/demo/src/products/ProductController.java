package com.example.demo.src.products;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.products.model.*;
import com.example.demo.src.user.UserService;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/products")
public class ProductController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductService productService;
    private final JwtService jwtService;


    @Autowired
    public ProductController(ProductService productService, JwtService jwtService) {
        this.productService = productService;
        this.jwtService = jwtService;
    }

    //홈화면 중 투데이 탭일때
    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<GetProductsRes> getProductsToday(@PathVariable Long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetProductsRes getProductsRes = productService.getProductsToday(userId);
            return new BaseResponse<>(getProductsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    @ResponseBody
    @GetMapping("/{userId}/real-time")
    public BaseResponse<GetProductsRealTime> getProductsRealTime(@RequestParam(value = "br") int br,
                                                                 @RequestParam(value = "img") int img,
                                                                 @PathVariable Long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetProductsRealTime getProductsRealTime;

            //실시간 구매
            if (br == 1) {
                //이미지만 볼래요 X
                if (img == 0) {
                    getProductsRealTime = productService.getProductsRealTimeBuy(userId);
                }
                //이미지만 볼래요
                else {
                    getProductsRealTime = productService.getProductsRealTimeBuyImg(userId);
                }
            }
            //실시간 후기
            else{
                //이미지만 볼래요 X
                if (img == 0) {
                    getProductsRealTime = productService.getProductsRealTimeReview(userId);
                }
                //이미지만 볼래요
                else{
                    getProductsRealTime = productService.getProductsRealTimeReviewImg(userId);
                }
            }

            return new BaseResponse<>(getProductsRealTime);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/new")
    public BaseResponse<GetProductsNew> getProductsNew(@RequestParam("img") int img, @PathVariable Long userId) {

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetProductsNew getProductsNew;
            //이미지만 볼래요 X
            if(img == 0) {
                getProductsNew = productService.getProductsNew(userId);
            }
            //이미지만 볼래요
            else {
                getProductsNew = productService.getProductsNewImg(userId);
            }
            return new BaseResponse<>(getProductsNew);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/{productId}")
    public BaseResponse<GetProductDetail> getProductDetail(@PathVariable("userId") long userId, @PathVariable("productId") long productId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetProductDetail getProductDetail = productService.getProductDetail(userId, productId);
            return new BaseResponse<>(getProductDetail);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/{productId}/reviews")
    public BaseResponse<List<Review>> getProductReviews(@PathVariable("userId") long userId, @PathVariable("productId") long productId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<Review> reviewList = productService.getProductReviews(userId, productId);
            return new BaseResponse<>(reviewList);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/{productId}/comments")
    public BaseResponse<List<Comment>> getProductComments(@PathVariable("userId") long userId, @PathVariable("productId") long productId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<Comment> commentList = productService.getProductComments(userId, productId);
            return new BaseResponse<>(commentList);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


}
