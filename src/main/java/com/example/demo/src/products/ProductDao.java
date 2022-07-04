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

    public GetProductsNew getProductsNew(Long userId) {
        String getNowQuery = "select date_format(now(), '%c월 %d일 %H:%i 기준')";
        GetProductsNew getProductsNew =
                new GetProductsNew(this.jdbcTemplate.queryForObject(getNowQuery, String.class));

        String getProductQuery = "select P.productId, if(isnull(PL.status), false, true) as isLike, PI.imgUrl, W.nickName, P.title, P.price,\n" +
                "       P.discountRate, round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, if(P.deliveryFee=0, true, false) as freeDelivery\n" +
                "from Product P\n" +
                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                "inner join Writer W using (writerId)\n" +
                "order by (productId) desc limit 20";
        List<NewProducts> newProductsList = this.jdbcTemplate.query(getProductQuery,
                ((rs, rowNum) -> new NewProducts(
                        rs.getLong("productId"),
                        rs.getBoolean("isLike"),
                        rs.getString("imgUrl"),
                        rs.getString("nickName"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getInt("discountRate"),
                        rs.getInt("finalPrice"),
                        rs.getBoolean("freeDelivery"))), userId);

        getProductsNew.setNewProductsList(newProductsList);
        return getProductsNew;
    }

    public GetProductsNew getProductsNewImg(Long userId) {
        String getNowQuery = "select date_format(now(), '%c월 %d일 %H:%i 기준')";
        GetProductsNew getProductsNew =
                new GetProductsNew(this.jdbcTemplate.queryForObject(getNowQuery, String.class));

        String getProductsQuery = "select P.productId, if(isnull(PL.status), false, true) as isLike, PI.imgUrl\n" +
                "from Product P\n" +
                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                "order by (productId) desc limit 15";

        List<NewProducts> newProductsList = this.jdbcTemplate.query(getProductsQuery,
                ((rs, rowNum) -> new NewProducts(
                        rs.getLong("productId"),
                        rs.getBoolean("isLike"),
                        rs.getString("imgUrl"))), userId);

        getProductsNew.setNewProductsList(newProductsList);
        return getProductsNew;
    }

    public GetProductDetail getProductDetail(long userId, long productId) {

        String getImgUrlQuery = "select imgUrl from ProductImg where productId=?";
        List<String> imgUrlList = this.jdbcTemplate.query(getImgUrlQuery,
                ((rs, rowNum) -> rs.getString("imgUrl")), productId);

        String getQuery1 = "select W.writerId, W.nickName, W.profileImg, P.rating, PR.countReview, P.title, P.price, P.discountRate,\n" +
                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, OP.countBuyer, P.deliveryFee, P.freeAmount,\n" +
                "       P.leftAmount, P.contents, W.rating as ratingAverage, WPR.countAllReview, PL.countProductLike\n" +
                "from Product P\n" +
                "inner join Writer W using (writerId)\n" +
                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                "left outer join (select productId, count(userId) as countBuyer from Ordered inner join OrderProduct using (orderProductId) group by (productId)) OP using (productId)\n" +
                "left outer join (select writerId, count(productReviewId) as countAllReview from ProductReview inner join Product using(productId) inner join Writer using(writerId) group by (writerId)) WPR using(writerId)\n" +
                "left outer join (select productId, count(productLikeId) as countProductLike from ProductLike group by (productId)) PL using (productId)\n" +
                "where productId = ?";

        GetProductDetail getProductDetail = this.jdbcTemplate.queryForObject(getQuery1,
                ((rs, rowNum) -> new GetProductDetail(
                        rs.getLong("writerId"),
                        rs.getString("nickName"),
                        rs.getString("profileImg"),
                        rs.getDouble("rating"),
                        rs.getInt("countReview"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getInt("discountRate"),
                        rs.getInt("finalPrice"),
                        rs.getInt("countBuyer"),
                        rs.getInt("deliveryFee"),
                        rs.getInt("freeAmount"),
                        rs.getInt("leftAmount"),
                        rs.getString("contents"),
                        rs.getDouble("ratingAverage"),
                        rs.getInt("countAllReview"),
                        rs.getInt("countProductLike"))), productId);

        getProductDetail.setImgUrlList(imgUrlList);

        String getQuery2 = "select exists(select productLikeId from ProductLike where userId=? and productId=?)";
        Object[] params = new Object[]{userId, productId};

        int isLike = this.jdbcTemplate.queryForObject(getQuery2, int.class, params);
        if(isLike == 1)
            getProductDetail.setLike(true);
        else
            getProductDetail.setLike(false);

        String getWriterId = "select writerId from Product where productId=?";
        int writerId = this.jdbcTemplate.queryForObject(getWriterId, int.class, productId);

        String getQuery3 = "select sum(PL.countLike) as countProductLike from (select productId, count(productLikeId) as countLike from ProductLike group by (productId)) PL\n" +
                "inner join Product using (productId) group by (writerId) having writerId = ?";

        getProductDetail.setCountAllLike(this.jdbcTemplate.queryForObject(getQuery3, int.class, writerId));

        String getQuery4 = "select count(userId) from Follow group by (writerId) having writerId=?";

        getProductDetail.setCountFollow(this.jdbcTemplate.queryForObject(getQuery4, int.class, writerId));

        String getQuery5 = "select count(userId) from Support group by (writerId) having writerId=?";

        getProductDetail.setCountSupport(this.jdbcTemplate.queryForObject(getQuery5, int.class, writerId));

        String getQuery6 = "select if(repurchase='Y', true, false) as repurchase, PRI.imgUrl, contents\n" +
                "from ProductReview\n" +
                "left outer join (select productReviewId, imgUrl from ProductReviewImg group by (productReviewId)) PRI using (productReviewId)\n" +
                "where productId = ? and rating = 5\n" +
                "order by (productReviewId) desc limit 2";
        List<ShortReview> shortReviewList = this.jdbcTemplate.query(getQuery6,
                ((rs, rowNum) -> new ShortReview(
                        rs.getBoolean("repurchase"),
                        rs.getString("imgUrl"),
                        rs.getString("contents"))), productId);

        getProductDetail.setShortReviewList(shortReviewList);

        String getQuery7 = "select PR.productReviewId, U.nickName, U.profileImg, PR.rating, date_format(PR.createAt, '%Y년 %c월 %e일') as createAt,\n" +
                "       if(PR.repurchase='Y', true, false) as repurchase, PRI.imgUrl, PR.contents\n" +
                "from ProductReview PR\n" +
                "inner join User U using(userId)\n" +
                "left outer join (select productReviewId, imgUrl from ProductReviewImg group by (productReviewId)) PRI using (productReviewId)\n" +
                "where productId = ?";

        List<Review> reviewList = this.jdbcTemplate.query(getQuery7,
                ((rs, rowNum) -> new Review(
                        rs.getLong("productReviewId"),
                        rs.getString("nickName"),
                        rs.getString("profileImg"),
                        rs.getInt("rating"),
                        rs.getString("createAt"),
                        rs.getBoolean("repurchase"),
                        rs.getString("imgUrl"),
                        rs.getString("contents"))), productId);

        getProductDetail.setReviewList(reviewList);

        String getQuery8 = "select U.nickName as userName, U.profileimg as userProfileImg, PC.contents as userComment, W.nickName as writerName,\n" +
                "       W.profileImg as writerProfileImg, PWC.contents as writerComment\n" +
                "from ProductComment PC\n" +
                "inner join User U using(userId)\n" +
                "left outer join ProductWriterComment PWC using (commentId)\n" +
                "left outer join Writer W using (writerId)\n" +
                "where productId = ? limit 5";

        List<Comment> commentList = this.jdbcTemplate.query(getQuery8,
                ((rs, rowNum) -> new Comment(
                        rs.getString("userName"),
                        rs.getString("userProfileImg"),
                        rs.getString("userComment"),
                        rs.getString("writerName"),
                        rs.getString("writerProfileImg"),
                        rs.getString("writerComment"))), productId);

        getProductDetail.setCommentList(commentList);

        return getProductDetail;
    }

    public int checkProduct(long productId) {
        String checkProductQuery = "select exists(select productId from Product where productId=?)";

        return this.jdbcTemplate.queryForObject(checkProductQuery, int.class, productId );
    }

    public List<Review> getProductReviews(long userId, long productId) {
        String getReviewsQuery = "select PR.productReviewId, U.nickName, U.profileImg, PR.rating, date_format(PR.createAt, '%Y년 %c월 %e일') as createAt,\n" +
                "       if(PR.repurchase='Y', true, false) as repurchase, PRI.imgUrl, PR.contents\n" +
                "from ProductReview PR\n" +
                "inner join User U using(userId)\n" +
                "left outer join (select productReviewId, imgUrl from ProductReviewImg group by (productReviewId)) PRI using (productReviewId)\n" +
                "where productId = ?";
        List<Review> reviewList = this.jdbcTemplate.query(getReviewsQuery,
                (rs, row) -> new Review(
                        rs.getLong("productReviewId"),
                        rs.getString("nickName"),
                        rs.getString("profileImg"),
                        rs.getInt("rating"),
                        rs.getString("createAt"),
                        rs.getBoolean("repurchase"),
                        rs.getString("imgUrl"),
                        rs.getString("contents")), productId);

        return reviewList;
    }

    public List<Comment> getProductComments(long userId, long productId) {
        String getCommentsQuery = "select U.nickName as userName, U.profileimg as userProfileImg, PC.contents as userComment, W.nickName as writerName,\n" +
                "       W.profileImg as writerProfileImg, PWC.contents as writerComment\n" +
                "from ProductComment PC\n" +
                "inner join User U using(userId)\n" +
                "left outer join ProductWriterComment PWC using (commentId)\n" +
                "left outer join Writer W using (writerId)\n" +
                "where productId = ?";

        List<Comment> commentList = this.jdbcTemplate.query(getCommentsQuery,
                ((rs, rowNum) -> new Comment(
                        rs.getString("userName"),
                        rs.getString("userProfileImg"),
                        rs.getString("userComment"),
                        rs.getString("writerName"),
                        rs.getString("writerProfileImg"),
                        rs.getString("writerComment"))), productId);

        return commentList;
    }

    public List<Category> getCategories(long userId) {

        String getCategoriesQuery = "select categoryId, categoryName from ProductCategory";
        return this.jdbcTemplate.query(getCategoriesQuery, (rs, rowNum) -> new Category(
                rs.getLong("categoryId"),
                rs.getString("categoryName")));
    }

    public List<GetCategoryProduct> getCategoryProducts(long userId, long categoryId, RequestParams params) {

        List<GetCategoryProduct> getCategoryProductList;
        Object[] queryParams1 = new Object[] {userId, categoryId, params.getDis()};
        Object[] queryParams2 = new Object[] {userId, categoryId, params.getMin(), params.getDis()};
        Object[] queryParams3 = new Object[] {userId, categoryId, params.getMin(), params.getMax(), params.getDis()};

        //무료배송 필터 OFF
        if (params.getFree() == 0) {
            //인기순 정렬
            if(params.getSort() == 0) {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                            "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                            "from Product P\n" +
                            "inner join Writer W using (writerId)\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "left outer join (select productId, count(orderedId) as countOrder from Ordered inner join OrderProduct using(orderProductId) group by (productId)) OP using(productId)\n" +
                            "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                            "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                            "where P.categoryId = ? and P.discountRate >= ? order by (OP.countOrder) desc";

                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike"),
                            rs.getString("nickName"),
                            rs.getString("title"),
                            rs.getInt("price"),
                            rs.getInt("discountRate"),
                            rs.getInt("finalPrice"),
                            rs.getLong("rating"),
                            rs.getInt("countReview"),
                            rs.getString("lastReview")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(orderedId) as countOrder from Ordered inner join OrderProduct using(orderProductId) group by (productId)) OP using(productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.discountRate >= ? order by (OP.countOrder) desc";

                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(orderedId) as countOrder from Ordered inner join OrderProduct using(orderProductId) group by (productId)) OP using(productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.discountRate >= ? order by (OP.countOrder) desc";

                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams3);
                    }
                }
            }
            //최신순 정렬
            else if(params.getSort() == 1) {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                            "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                            "from Product P\n" +
                            "inner join Writer W using (writerId)\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                            "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                            "where P.categoryId = ? and P.discountRate >= ? order by (productId) desc";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike"),
                            rs.getString("nickName"),
                            rs.getString("title"),
                            rs.getInt("price"),
                            rs.getInt("discountRate"),
                            rs.getInt("finalPrice"),
                            rs.getLong("rating"),
                            rs.getInt("countReview"),
                            rs.getString("lastReview")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.discountRate >= ? order by (productId) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.discountRate >= ? order by (productId) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams3);
                    }
                }
            }
            //낮은 가격순 정렬
            else if(params.getSort() == 2) {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                            "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                            "from Product P\n" +
                            "inner join Writer W using (writerId)\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                            "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                            "where P.categoryId = ? and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2))";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike"),
                            rs.getString("nickName"),
                            rs.getString("title"),
                            rs.getInt("price"),
                            rs.getInt("discountRate"),
                            rs.getInt("finalPrice"),
                            rs.getLong("rating"),
                            rs.getInt("countReview"),
                            rs.getString("lastReview")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2))";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2))";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams3);
                    }
                }
            }
            //높은 가격순 정렬
            else {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                            "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                            "from Product P\n" +
                            "inner join Writer W using (writerId)\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                            "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                            "where P.categoryId = ? and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2)) desc";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike"),
                            rs.getString("nickName"),
                            rs.getString("title"),
                            rs.getInt("price"),
                            rs.getInt("discountRate"),
                            rs.getInt("finalPrice"),
                            rs.getLong("rating"),
                            rs.getInt("countReview"),
                            rs.getString("lastReview")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2)) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2)) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams3);
                    }
                }
            }
        }
        //무료배송 필터 ON
        else {
            //인기순 정렬
            if(params.getSort() == 0) {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                            "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                            "from Product P\n" +
                            "inner join Writer W using (writerId)\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, count(orderedId) as countOrder from Ordered inner join OrderProduct using(orderProductId) group by (productId)) OP using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                            "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                            "where P.categoryId = ? and P.deliveryFee = 0 and P.discountRate >= ? order by (OP.countOrder) desc";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike"),
                            rs.getString("nickName"),
                            rs.getString("title"),
                            rs.getInt("price"),
                            rs.getInt("discountRate"),
                            rs.getInt("finalPrice"),
                            rs.getLong("rating"),
                            rs.getInt("countReview"),
                            rs.getString("lastReview")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, count(orderedId) as countOrder from Ordered inner join OrderProduct using(orderProductId) group by (productId)) OP using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.deliveryFee = 0 and P.discountRate >= ? order by (OP.countOrder) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, count(orderedId) as countOrder from Ordered inner join OrderProduct using(orderProductId) group by (productId)) OP using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.deliveryFee = 0 and P.discountRate >= ? order by (OP.countOrder) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams3);
                    }
                }
            }
            //최신순 정렬
            else if(params.getSort() == 1) {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                            "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                            "from Product P\n" +
                            "inner join Writer W using (writerId)\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                            "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                            "where P.categoryId = ? and P.deliveryFee = 0 and P.discountRate >= ? order by (productId) desc";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike"),
                            rs.getString("nickName"),
                            rs.getString("title"),
                            rs.getInt("price"),
                            rs.getInt("discountRate"),
                            rs.getInt("finalPrice"),
                            rs.getLong("rating"),
                            rs.getInt("countReview"),
                            rs.getString("lastReview")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.deliveryFee = 0 and P.discountRate >= ? order by (productId) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.deliveryFee = 0 and P.discountRate >= ? order by (productId) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams3);
                    }
                }
            }
            //낮은 가격순 정렬
            else if(params.getSort() == 2) {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                            "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                            "from Product P\n" +
                            "inner join Writer W using (writerId)\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                            "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                            "where P.categoryId = ? and P.deliveryFee = 0 and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2))";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike"),
                            rs.getString("nickName"),
                            rs.getString("title"),
                            rs.getInt("price"),
                            rs.getInt("discountRate"),
                            rs.getInt("finalPrice"),
                            rs.getLong("rating"),
                            rs.getInt("countReview"),
                            rs.getString("lastReview")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.deliveryFee = 0 and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2))";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.deliveryFee = 0 and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2))";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams3);
                    }
                }
            }
            //높은 가격순 정렬
            else {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                            "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                            "from Product P\n" +
                            "inner join Writer W using (writerId)\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                            "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                            "where P.categoryId = ? and P.deliveryFee = 0 and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2)) desc";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike"),
                            rs.getString("nickName"),
                            rs.getString("title"),
                            rs.getInt("price"),
                            rs.getInt("discountRate"),
                            rs.getInt("finalPrice"),
                            rs.getLong("rating"),
                            rs.getInt("countReview"),
                            rs.getString("lastReview")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.deliveryFee = 0 and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2)) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike, W.nickName, P.title, P.price, P.discountRate,\n" +
                                "       round(P.price-(P.price*P.discountRate/100), -2) as finalPrice, P.rating, PR.countReview, PR1.contents as lastReview\n" +
                                "from Product P\n" +
                                "inner join Writer W using (writerId)\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR using (productId)\n" +
                                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR1 group by(productId)) PR1 using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.deliveryFee = 0 and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2)) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike"),
                                rs.getString("nickName"),
                                rs.getString("title"),
                                rs.getInt("price"),
                                rs.getInt("discountRate"),
                                rs.getInt("finalPrice"),
                                rs.getLong("rating"),
                                rs.getInt("countReview"),
                                rs.getString("lastReview")),queryParams3);
                    }
                }
            }
        }

        return getCategoryProductList;
    }

    public List<GetCategoryProduct> getCategoryProductsImg(long userId, long categoryId, RequestParams params) {
        List<GetCategoryProduct> getCategoryProductList;
        Object[] queryParams1 = new Object[] {userId, categoryId, params.getDis()};
        Object[] queryParams2 = new Object[] {userId, categoryId, params.getMin(), params.getDis()};
        Object[] queryParams3 = new Object[] {userId, categoryId, params.getMin(), params.getMax(), params.getDis()};

        //무료배송 필터 OFF
        if (params.getFree() == 0) {
            //인기순 정렬
            if(params.getSort() == 0) {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                            "from Product P\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "left outer join (select productId, count(orderedId) as countOrder from Ordered inner join OrderProduct using(orderProductId) group by (productId)) OP using(productId)\n" +
                            "where P.categoryId = ? and P.discountRate >= ? order by (OP.countOrder) desc";

                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(orderedId) as countOrder from Ordered inner join OrderProduct using(orderProductId) group by (productId)) OP using(productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.discountRate >= ? order by (OP.countOrder) desc";

                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "left outer join (select productId, count(orderedId) as countOrder from Ordered inner join OrderProduct using(orderProductId) group by (productId)) OP using(productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.discountRate >= ? order by (OP.countOrder) desc";

                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams3);
                    }
                }
            }
            //최신순 정렬
            else if(params.getSort() == 1) {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                            "from Product P\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "where P.categoryId = ? and P.discountRate >= ? order by (productId) desc";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.discountRate >= ? order by (productId) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.discountRate >= ? order by (productId) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams3);
                    }
                }
            }
            //낮은 가격순 정렬
            else if(params.getSort() == 2) {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                            "from Product P\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "where P.categoryId = ? and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2))";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2))";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2))";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams3);
                    }
                }
            }
            //높은 가격순 정렬
            else {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                            "from Product P\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "where P.categoryId = ? and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2)) desc";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2)) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2)) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams3);
                    }
                }
            }
        }
        //무료배송 필터 ON
        else {
            //인기순 정렬
            if(params.getSort() == 0) {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                            "from Product P\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, count(orderedId) as countOrder from Ordered inner join OrderProduct using(orderProductId) group by (productId)) OP using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "where P.categoryId = ? and P.deliveryFee = 0 and P.discountRate >= ? order by (OP.countOrder) desc";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, count(orderedId) as countOrder from Ordered inner join OrderProduct using(orderProductId) group by (productId)) OP using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.deliveryFee = 0 and P.discountRate >= ? order by (OP.countOrder) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, count(orderedId) as countOrder from Ordered inner join OrderProduct using(orderProductId) group by (productId)) OP using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.deliveryFee = 0 and P.discountRate >= ? order by (OP.countOrder) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams3);
                    }
                }
            }
            //최신순 정렬
            else if(params.getSort() == 1) {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                            "from Product P\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "where P.categoryId = ? and P.deliveryFee = 0 and P.discountRate >= ? order by (productId) desc";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.deliveryFee = 0 and P.discountRate >= ? order by (productId) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.deliveryFee = 0 and P.discountRate >= ? order by (productId) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams3);
                    }
                }
            }
            //낮은 가격순 정렬
            else if(params.getSort() == 2) {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                            "from Product P\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "where P.categoryId = ? and P.deliveryFee = 0 and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2))";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.deliveryFee = 0 and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2))";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.deliveryFee = 0 and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2))";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams3);
                    }
                }
            }
            //높은 가격순 정렬
            else {
                //가격대 필터 OFF
                if(params.getMin() == 0 && params.getMax() == 0) {
                    String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                            "from Product P\n" +
                            "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                            "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                            "where P.categoryId = ? and P.deliveryFee = 0 and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2)) desc";
                    getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                            rs.getLong("productId"),
                            rs.getString("imgUrl"),
                            rs.getBoolean("userLike")),queryParams1);
                }
                else {
                    //2만원 이상
                    if(params.getMax() == 0) {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) > ? and P.deliveryFee = 0 and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2)) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams2);
                    }
                    else {
                        String getProductsQuery = "select P.productId, PI.imgUrl, if(isnull(PL.status), false, true) as userLike\n" +
                                "from Product P\n" +
                                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
                                "left outer join (select productId, status from ProductLike where userId = ?) PL using (productId)\n" +
                                "where P.categoryId = ? and round(P.price-(P.price*P.discountRate/100), -2) between ? and ? and P.deliveryFee = 0 and P.discountRate >= ? order by (round(P.price-(P.price*P.discountRate/100), -2)) desc";
                        getCategoryProductList = this.jdbcTemplate.query(getProductsQuery, (rs, rowNum) -> new GetCategoryProduct(
                                rs.getLong("productId"),
                                rs.getString("imgUrl"),
                                rs.getBoolean("userLike")),queryParams3);
                    }
                }
            }
        }

        return getCategoryProductList;

    }

    public int checkCategory(long categoryId) {
        String checkQuery = "select exists (select categoryId from ProductCategory where categoryId = ?)";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, categoryId);
    }

    public void setProductLike(long userId, long productId) {
        String setLikeQuery = "INSERT INTO ProductLike (userId, productId) values (?, ?)";
        Object[] params = new Object[] {userId, productId};

        this.jdbcTemplate.update(setLikeQuery, params);
    }

    public int checkLike(long userId, long productId) {
        String checkQuery = "select exists (select productLikeId from ProductLike where userId = ? and productId = ?)";
        Object[] params = new Object[] {userId, productId};
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, params);
    }
}
