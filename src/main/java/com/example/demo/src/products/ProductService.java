package com.example.demo.src.products;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.products.model.*;
import com.example.demo.src.user.UserDao;
import com.example.demo.utils.JwtService;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional
public class ProductService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductDao productDao;
    private final JwtService jwtService;


    @Autowired
    public ProductService(ProductDao productDao, JwtService jwtService) {
        this.productDao = productDao;
        this.jwtService = jwtService;
    }

    public GetProductsRes getProductsToday(Long userId) throws BaseException {
        try {
            GetProductsRes getProductsRes = productDao.getProductsToday(userId);
            return getProductsRes;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProductsRealTime getProductsRealTimeBuy(Long userId) throws BaseException {
        try {
            GetProductsRealTime getProductsRealTime = productDao.getProductsRealTimeBuy(userId);
            return getProductsRealTime;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProductsRealTime getProductsRealTimeBuyImg(Long userId) throws BaseException {
        try {
            GetProductsRealTime getProductsRealTime = productDao.getProductsRealTimeBuyImg(userId);
            return getProductsRealTime;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProductsRealTime getProductsRealTimeReview(Long userId) throws BaseException {
        try {
            GetProductsRealTime getProductsRealTime = productDao.getProductsRealTimeReview(userId);
            return getProductsRealTime;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProductsRealTime getProductsRealTimeReviewImg(Long userId) throws BaseException {
        try {
            GetProductsRealTime getProductsRealTime = productDao.getProductsRealTimeReviewImg(userId);
            return getProductsRealTime;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProductsNew getProductsNew(Long userId) throws BaseException {
        try {
            GetProductsNew getProductsNew = productDao.getProductsNew(userId);
            return getProductsNew;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProductsNew getProductsNewImg(Long userId) throws BaseException {
        try {
            GetProductsNew getProductsNew = productDao.getProductsNewImg(userId);
            return getProductsNew;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProductDetail getProductDetail(long userId, long productId) throws BaseException {

        if(productDao.checkProduct(productId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_PRODUCT);
        }

        try {
            GetProductDetail getProductDetail = productDao.getProductDetail(userId, productId);
            return getProductDetail;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Review> getProductReviews(long userId, long productId) throws BaseException {
        try {
            List<Review> reviewList = productDao.getProductReviews(userId, productId);
            return reviewList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Comment> getProductComments(long userId, long productId) throws BaseException {
        try {
            List<Comment> commentList = productDao.getProductComments(userId, productId);
            return commentList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Category> getCategories(long userId) throws BaseException {
        try {
            List<Category> categoryList = productDao.getCategories(userId);
            return categoryList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetCategoryProduct> getCategoryProducts(long userId, long categoryId, RequestParams params) throws BaseException {

        if(productDao.checkCategory(categoryId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_PRODUCT_CATEGORY);
        }

        try {
            List<GetCategoryProduct> categoryList;

            //이미지만 볼래요 X
            if(params.getImg() == 0) {
                categoryList = productDao.getCategoryProducts(userId, categoryId, params);
            }
            //이미지만 볼래요
            else {
                categoryList = productDao.getCategoryProductsImg(userId, categoryId, params);
            }
            return categoryList;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void setProductLike(long userId, long productId) throws BaseException {
        if(productDao.checkProduct(productId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_PRODUCT);
        }
        if(productDao.checkLike(userId, productId) == 1) {
            throw new BaseException(DUPLICATED_PRODUCT_LIKE);
        }

        try {
            productDao.setProductLike(userId, productId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteProductLike(long userId, long productId) throws BaseException {
        if(productDao.checkProduct(productId) == 0) {
            throw new BaseException(FAILED_TO_SEARCH_PRODUCT);
        }
        if(productDao.checkLike(userId, productId) == 0) {
            throw new BaseException(EMPTY_PRODUCT_LIKE);
        }

        try {
            productDao.deleteProductLike(userId, productId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public GetProductOption getProductOptions(long userId, long productId) throws BaseException {
        try {
            GetProductOption getProductOption = productDao.getProductOptions(userId, productId);
            return getProductOption;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void addBasketProducts(long userId, long productId, OrderProduct orderProduct) throws BaseException {

        if(productDao.countProductOption(productId) != orderProduct.getOrderOptionList().size()) {
            throw new BaseException(INVALID_PRODUCT_OPTION_COUNT);
        }

        for(int i =0; i < orderProduct.getOrderOptionList().size(); i++) {
            if(productDao.checkProductOption(orderProduct.getOrderOptionList().get(i).getProductOptionId()) != productId) {
                throw new BaseException(INVALID_PRODUCT_OPTION);
            }

            if(productDao.checkProductOptionDetail(orderProduct.getOrderOptionList().get(i).getOptionDetailId()) !=
                    orderProduct.getOrderOptionList().get(i).getProductOptionId()) {
                throw new BaseException(INVALID_PRODUCT_OPTION_DETAIL);
            }
        }

        try {
            productDao.addBasketProducts(userId, productId, orderProduct);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
