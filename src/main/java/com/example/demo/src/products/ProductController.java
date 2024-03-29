package com.example.demo.src.products;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.products.model.*;
import com.example.demo.src.user.UserService;
import com.example.demo.utils.JwtService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

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

    @ResponseBody
    @GetMapping("/{userId}/categories")
    public BaseResponse<List<Category>> getCategories(@PathVariable("userId") long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<Category> categoryList = productService.getCategories(userId);
            return new BaseResponse<>(categoryList);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/categories/{categoryId}")
    public BaseResponse<List<GetCategoryProduct>> getCategoryProducts(@PathVariable("userId") long userId,
                                                                      @PathVariable("categoryId") long categoryId,
                                                                      @ModelAttribute RequestParams params) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetCategoryProduct> getCategoryProductList = productService.getCategoryProducts(userId, categoryId, params);
            return new BaseResponse<>(getCategoryProductList);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{userId}/{productId}/likes")
    public BaseResponse<String> setProductLike(@PathVariable("userId") long userId, @PathVariable("productId") long productId) {

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            productService.setProductLike(userId, productId);
            String result = "찜 목록에 추가되었습니다";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/{productId}/likes")
    public BaseResponse<String> deleteProductLike(@PathVariable("userId") long userId, @PathVariable("productId") long productId) {

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            productService.deleteProductLike(userId, productId);
            String result = "찜 목록에서 제외되었습니다";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/{productId}/options")
    public BaseResponse<GetProductOption> getProductOptions(@PathVariable("userId") long userId, @PathVariable("productId") long productId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetProductOption getProductOption = productService.getProductOptions(userId, productId);
            return new BaseResponse<>(getProductOption);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ResponseBody
    @PostMapping("/{userId}/{productId}/options")
    public BaseResponse<String> addBasketProducts(@PathVariable("userId") long userId,
                                                  @PathVariable("productId") long productId,
                                                  @RequestBody OrderProduct orderProduct) {


        if(orderProduct.getOrderOptionList() == null) {
            return new BaseResponse<>(POST_ADD_EMPTY_ORDER_PRODUCT);
        }

        for (int i =0; i < orderProduct.getOrderOptionList().size(); i++) {
            if (orderProduct.getOrderOptionList().get(i).getProductOptionId() == null ||
                    orderProduct.getOrderOptionList().get(i).getOptionDetailId() == null) {
                return new BaseResponse<>(POST_ADD_EMPTY_ORDER_PRODUCT_OPTION);
            }
        }

        if(orderProduct.getAmount() == null) {
            return new BaseResponse<>(POST_ADD_EMPTY_ORDER_AMOUNT);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            productService.addBasketProducts(userId, productId, orderProduct);
            String result = "장바구니에 작품이 담겼습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
