package com.example.demo.src.products;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.products.model.GetProductsRealTime;
import com.example.demo.src.products.model.GetProductsRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

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
}
