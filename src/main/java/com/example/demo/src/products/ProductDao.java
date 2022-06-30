package com.example.demo.src.products;

import com.example.demo.src.products.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public GetProductsRes getProductsToday(Long userId) {
        String getCategoryQuery = "select categoryId from ProductCategory";
        List<Long> categoryList = this.jdbcTemplate.query(getCategoryQuery, (rs, rowNum) -> new Long(rs.getLong("categoryId")));

        String getProductsQuery = "select P.productId, PI.imgUrl , P.title\n" +
                "from Product P\n" +
                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                "where categoryId = ? limit 10";

        String getCategoryNameQuery = "select categoryName from ProductCategory where categoryId = ?";

        GetProductsRes getProductsRes = new GetProductsRes();

        List<CategoryProduct> categoryProductList = new ArrayList<>();

        for (long categoryId:categoryList) {
            List<Products> productsList = this.jdbcTemplate.query(getProductsQuery,
                    (rs, rowNum) -> new Products(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getString("title")), categoryId);

            if(productsList.isEmpty()) {
                break;
            }

            String categoryName = this.jdbcTemplate.queryForObject(getCategoryNameQuery, String.class, categoryId);

            CategoryProduct categoryProduct = new CategoryProduct(categoryId, categoryName, productsList);

            categoryProductList.add(categoryProduct);
        }

        getProductsRes.setCategoryProductList(categoryProductList);

        String getProductReviewQuery = "select PR.productReviewId, PRI.imgUrl as reviewImg, PR.rating, U.nickName, PR.contents, P.productId, PI.imgUrl as productImg, P.title\n" +
                "from ProductReview PR\n" +
                "inner join (select productReviewId, imgUrl from ProductReviewImg group by (productReviewId)) PRI using(productReviewId)\n" +
                "inner join User U using(userId)\n" +
                "inner join (select productId, title from Product) P using (productId)\n" +
                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                "order by (productReviewId) desc limit 5";

        List<ProductReview> productReviewList = this.jdbcTemplate.query(getProductReviewQuery,(rs, rowNum) -> new ProductReview(
                rs.getLong("productReviewId"),
                rs.getString("reviewImg"),
                rs.getInt("rating"),
                rs.getString("nickName"),
                rs.getString("contents"),
                rs.getLong("productId"),
                rs.getString("productImg"),
                rs.getString("title")));

        getProductsRes.setProductReviewList(productReviewList);

        return getProductsRes;

    }

    public GetProductsRealTime getProductsRealTimeBuy(Long userId) {
        String getNowQuery = "select date_format(now(), '%c월 %d일 %H:%i 기준')";
        GetProductsRealTime getProductsRealTime =
                new GetProductsRealTime(this.jdbcTemplate.queryForObject(getNowQuery, String.class));

        String getProductsQuery = "select distinct(P.productId), if(isnull(PL.status), false, true) as isLike , PI.imgUrl, W.nickName, P.title, P.rating, if(isnull(PR.countReview), 0, PR.countReview) countReview, PR2.contents\n" +
                "from Ordered O\n" +
                "inner join OrderProduct OP using (orderProductId)\n" +
                "inner join Product P using (productId)\n" +
                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                "inner join Writer W using (writerId)\n" +
                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR2 using (productId)\n" +
                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                "order by (orderedId) desc";

        List<RealTimeProducts> realTimeProductsList = this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new RealTimeProducts(rs.getLong("productId"),
                        rs.getBoolean("isLike"),
                        rs.getString("imgUrl"),
                        rs.getString("nickName"),
                        rs.getString("title"),
                        rs.getDouble("rating"),
                        rs.getInt("countReview"),
                        rs.getString("contents")), userId);

        getProductsRealTime.setRealTimeProducts(realTimeProductsList);
        return getProductsRealTime;

    }

    public GetProductsRealTime getProductsRealTimeBuyImg(Long userId) {

        String getNowQuery = "select date_format(now(), '%c월 %d일 %H:%i 기준')";
        GetProductsRealTime getProductsRealTime =
                new GetProductsRealTime(this.jdbcTemplate.queryForObject(getNowQuery, String.class));

        String getProductsQuery = "select distinct(P.productId), if(isnull(PL.status), false, true) as isLike, PI.imgUrl\n" +
                "from Ordered O\n" +
                "inner join OrderProduct OP using (orderProductId)\n" +
                "inner join Product P using (productId)\n" +
                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                "order by (orderedId) desc";

        List<RealTimeProducts> realTimeProductsList = this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new RealTimeProducts(rs.getLong("productId"),
                        rs.getBoolean("isLike"),
                        rs.getString("imgUrl")), userId);

        getProductsRealTime.setRealTimeProducts(realTimeProductsList);
        return getProductsRealTime;
    }

    public GetProductsRealTime getProductsRealTimeReview(Long userId) {

        String getNowQuery = "select date_format(now(), '%c월 %d일 %H:%i 기준')";
        GetProductsRealTime getProductsRealTime =
                new GetProductsRealTime(this.jdbcTemplate.queryForObject(getNowQuery, String.class));

        String getProductsQuery = "select PR.productId, if(isnull(PL.status), false, true) as isLike, PI.imgUrl, W.nickName, P.title, P.rating, PR1.countReview, PR.contents\n" +
                "from (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR group by(productId)) PR\n" +
                "inner join (select productId, writerId, title, rating from Product) P using (productId)\n" +
                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                "inner join Writer W using (writerId)\n" +
                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR1 using (productId)";

        List<RealTimeProducts> realTimeProductsList = this.jdbcTemplate.query(getProductsQuery, ((rs, rowNum) ->
                new RealTimeProducts(
                        rs.getLong("productId"),
                        rs.getBoolean("isLike"),
                        rs.getString("imgUrl"),
                        rs.getString("nickName"),
                        rs.getString("title"),
                        rs.getDouble("rating"),
                        rs.getInt("countReview"),
                        rs.getString("contents"))), userId);
        getProductsRealTime.setRealTimeProducts(realTimeProductsList);
        return getProductsRealTime;
    }

    public GetProductsRealTime getProductsRealTimeReviewImg(Long userId) {

        String getNowQuery = "select date_format(now(), '%c월 %d일 %H:%i 기준')";
        GetProductsRealTime getProductsRealTime =
                new GetProductsRealTime(this.jdbcTemplate.queryForObject(getNowQuery, String.class));

        String getProductsQuery = "select PR.productId, if(isnull(PL.status), false, true) as isLike, PI.imgUrl\n" +
                "from (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR group by(productId)) PR\n" +
                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)";

        List<RealTimeProducts> realTimeProductsList = this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new RealTimeProducts(rs.getLong("productId"),
                        rs.getBoolean("isLike"),
                        rs.getString("imgUrl")), userId);

        getProductsRealTime.setRealTimeProducts(realTimeProductsList);
        return getProductsRealTime;
    }
}
