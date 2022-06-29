package com.example.demo.src.products;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.products.model.GetProductsRes;
import com.example.demo.src.user.UserService;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/products")
public class ProductController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductService productService;
    private final JwtService jwtService;


    @Autowired
    public ProductController(ProductService productService, JwtService jwtService){
        this.productService = productService;
        this.jwtService = jwtService;
    }

    //홈화면 중 투데이 탭일때
    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<GetProductsRes> getProductsToday(@PathVariable Long userId) {
        try{
            long userIdByJwt = jwtService.getUserIdx();
            if(userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetProductsRes getProductsRes = productService.getProductsToday(userId);
            return new BaseResponse<>(getProductsRes);
        } catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


}
