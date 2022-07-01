package com.example.demo.src.classes;

import com.example.demo.config.BaseException;
import com.example.demo.src.classes.model.GetOnlineClasses;
import com.example.demo.src.products.ProductDao;
import com.example.demo.src.products.model.GetProductsRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
@Transactional
public class ClassService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ClassDao classDao;
    private final JwtService jwtService;


    @Autowired
    public ClassService(ClassDao classDao, JwtService jwtService) {
        this.classDao = classDao;
        this.jwtService = jwtService;
    }

    public GetOnlineClasses getOnlineClasses(long userId) throws BaseException {
        try {
            GetOnlineClasses  getOnlineClasses = classDao.getOnlineClasses(userId);
            return getOnlineClasses;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
