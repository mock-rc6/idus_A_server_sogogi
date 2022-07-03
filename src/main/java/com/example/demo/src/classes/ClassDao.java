package com.example.demo.src.classes;

import com.example.demo.src.classes.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.List;

@Repository
public class ClassDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public GetOnlineClasses getOnlineClasses(long userId) {

        String getQuery1 = "select OCR.classReviewId, OCRI.imgUrl, OCR.rating, U.nickName, OCR.contents, OCR.onlineClassId, OC.profileImg, OC.title\n" +
                "from OnlineClassReview OCR\n" +
                "left outer join (select classReviewId, imgUrl from OnlineClassReviewImg group by (classReviewId)) OCRI using (classReviewId)\n" +
                "inner join User U using(userId)\n" +
                "inner join OnlineClass OC using (onlineClassId)\n" +
                "where OCR.rating=5 and OCRI.imgUrl is not null\n" +
                "order by (classReviewId) desc limit 6";

        List<BestReview> bestReviewList = this.jdbcTemplate.query(getQuery1, ((rs, rowNum) -> new BestReview(
                rs.getLong("classReviewId"),
                rs.getString("imgUrl"),
                rs.getInt("rating"),
                rs.getString("nickName"),
                rs.getString("contents"),
                rs.getLong("onlineClassId"),
                rs.getString("profileImg"),
                rs.getString("title"))));

        String getQuery2 = "select OC.onlineClassId, OC.profileImg, if(OC.streaming='Y', true, false) as isStreaming, if(isnull(OCL.status), false, true) as isLike,\n" +
                "       CC.categoryName, W.nickName as writerName, OC.title\n" +
                "from OnlineClass OC\n" +
                "inner join ClassCategory CC using (categoryId)\n" +
                "inner join Writer W using(writerId)\n" +
                "left outer join (select onlineClassId, status from OnlineClassLike where userId = ?) OCL using (onlineClassId)\n" +
                "order by (TIMESTAMPDIFF(MINUTE, OC.startingDay, now())) limit 6";

        List<NewOpenClasses> newOpenClassesList = this.jdbcTemplate.query(getQuery2,
                ((rs, rowNum) -> new NewOpenClasses(
                        rs.getLong("onlineClassId"),
                        rs.getString("profileImg"),
                        rs.getBoolean("isStreaming"),
                        rs.getBoolean("isLike"),
                        rs.getString("categoryName"),
                        rs.getString("writerName"),
                        rs.getString("title"))), userId);

        String getQuery3 = "select OCL.onlineClassId, OC.profileImg, CC.categoryName, W.nickName, OC.title\n" +
                "from OnlineClassLike OCL\n" +
                "inner join OnlineClass OC using(onlineClassId)\n" +
                "inner join ClassCategory CC using (categoryId)\n" +
                "inner join Writer W using(writerId)\n" +
                "where userId = 1 order by (classLikeId) desc";

        List<UserLikeClasses> userLikeClassesList = this.jdbcTemplate.query(getQuery3, ((rs, rowNum) ->
                new UserLikeClasses(
                        rs.getLong("onlineClassId"),
                        rs.getString("profileImg"),
                        rs.getString("categoryName"),
                        rs.getString("nickName"),
                        rs.getString("title"))));

        String getQuery4 = "select OC.onlineClassId, OC.profileImg, if(OC.streaming='Y', true, false) as isStreaming, if(isnull(OCL.status), false, true) as isLike,\n" +
                "       CC.categoryName, W.nickName as writerName, OC.title, CR.rating, CR.nickName, CR.contents\n" +
                "from OnlineClass OC\n" +
                "inner join ClassCategory CC using (categoryId)\n" +
                "inner join Writer W using(writerId)\n" +
                "left outer join (select * from (select classReviewId, nickName, onlineClassId, rating, contents from OnlineClassReview inner join User using(userId) order by (classReviewId) desc LIMIT 18446744073709551615) CR1 group by(onlineclassId)) CR using (onlineClassId)\n" +
                "left outer join (select onlineClassId, status from OnlineClassLike where userId = ?) OCL using (onlineClassId)";
        List<AllOnlineClasses> allOnlineClassesList = this.jdbcTemplate.query(getQuery4, ((rs, rowNum) ->
                new AllOnlineClasses(
                        rs.getLong("onlineClassId"),
                        rs.getString("profileImg"),
                        rs.getBoolean("isStreaming"),
                        rs.getBoolean("isLike"),
                        rs.getString("categoryName"),
                        rs.getString("writerName"),
                        rs.getString("title"),
                        rs.getInt("rating"),
                        rs.getString("nickName"),
                        rs.getString("contents"))), userId);

        GetOnlineClasses getOnlineClasses = new GetOnlineClasses(bestReviewList, newOpenClassesList,
                userLikeClassesList, allOnlineClassesList);
        return  getOnlineClasses;
    }

    public int checkOnlineClass(long onlineClassId) {
        String checkQuery = "select exists (select onlineClassId from OnlineClass where onlineClassId=?)";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, onlineClassId);
    }

    public GetOnlineClass getOnlineClass(long userId, long onlineClassId) {
        String getOnlineClassQuery = "select OC.introVideo, W.writerId, W.profileImg as writerImg, W.nickName as writerName, CC.categoryName, OC.title, if(OC.streaming='Y', true, false) as streaming,\n" +
                "       IF(timestampdiff(SECOND, now(), OC.startingDay) < 0, '신청 즉시', date_format(OC.startingDay, '%c월 %d일')) as startingDay,\n" +
                "       case when OC.level=1 then '쉬움' when OC.level=2 then '보통' else '어려움' end as level, if(isnull(OCL.status), false, true) as isLike,\n" +
                "       OCL1.countLike, OC.rating, OCR.countReview, OC.contents\n" +
                "from OnlineClass OC\n" +
                "inner join Writer W using (writerId)\n" +
                "inner join ClassCategory CC using(categoryId)\n" +
                "left outer join (select onlineClassId, status from OnlineClassLike where userId = ?) OCL using (onlineClassId)\n" +
                "left outer join (select onlineClassId, count(classLikeId) as countLike from OnlineClassLike group by (onlineClassId)) OCL1 using(onlineClassId)\n" +
                "left outer join (select onlineClassId, count(classReviewId) as countReview from OnlineClassReview group by (onlineClassId)) OCR using(onlineClassId)\n" +
                "where onlineClassId = ?";

        Object[] params = new Object[] {userId, onlineClassId};

        GetOnlineClass getOnlineClass = this.jdbcTemplate.queryForObject(getOnlineClassQuery, ((rs, rowNum) -> new GetOnlineClass(
                rs.getString("introVideo"),
                rs.getLong("writerId"),
                rs.getString("writerImg"),
                rs.getString("writerName"),
                rs.getString("categoryName"),
                rs.getString("title"),
                rs.getBoolean("streaming"),
                rs.getString("startingDay"),
                rs.getString("level"),
                rs.getBoolean("isLike"),
                rs.getInt("countLike"),
                rs.getDouble("rating"),
                rs.getInt("countReview"),
                rs.getString("contents"))), params);

        String getQuery1 = "select OCR.classReviewId, OCRI.imgUrl, U.nickName, U.profileImg, date_format(OCR.createAt, '%Y년 %c월 %e일') as createAt, OCR.contents\n" +
                "from OnlineClassReview OCR\n" +
                "inner join User U using(userId)\n" +
                "left outer join (select classReviewId, imgUrl from OnlineClassReviewImg group by (classReviewId)) OCRI using(classReviewId)\n" +
                "where OCR.onlineClassId = ? and OCRI.imgUrl is not null\n" +
                "order by (classReviewId) desc";

        List<OnlineClassReview> onlineClassReviewList = this.jdbcTemplate.query(getQuery1, ((rs, rowNum) -> new OnlineClassReview(
                rs.getLong("classReviewId"),
                rs.getString("imgUrl"),
                rs.getString("nickName"),
                rs.getString("profileImg"),
                rs.getString("createAt"),
                rs.getString("contents"))), onlineClassId);

        getOnlineClass.setOnlineClassReviewList(onlineClassReviewList);


        String getQuery2 = "select U.nickName as userName, U.profileImg as userImg, OC.contents as userComment, W.nickName as writerName, W.profileImg as writerImg, OWC.contents as writerComment\n" +
                "from OnlineComment OC\n" +
                "inner join User U using(userId)\n" +
                "left outer join OnlineWriterComment OWC using (commentId)\n" +
                "inner join Writer W using(writerId)\n" +
                "where onlineClassId = ?";

        List<ClassComment> onlineClassCommentList = this.jdbcTemplate.query(getQuery2, ((rs, rowNum) ->
                new ClassComment(
                        rs.getString("userName"),
                        rs.getString("userImg"),
                        rs.getString("userComment"),
                        rs.getString("writerName"),
                        rs.getString("writerImg"),
                        rs.getString("writerComment"))), onlineClassId);

        getOnlineClass.setOnlineClassCommentList(onlineClassCommentList);

        return getOnlineClass;
    }

    public List<ClassReviews> getOnlineClassReviews(long userId, long onlineClassId) {

        String getReviewsQuery = "select OCR.classReviewId, OCRI.imgUrl, U.nickName, U.profileImg, date_format(OCR.createAt, '%Y년 %c월 %e일') as createAt, OCR.rating, OCR.contents\n" +
                "from OnlineClassReview OCR\n" +
                "inner join User U using(userId)\n" +
                "left outer join (select classReviewId, imgUrl from OnlineClassReviewImg group by (classReviewId)) OCRI using(classReviewId)\n" +
                "where OCR.onlineClassId = ? order by (classReviewId) desc";

        List<ClassReviews> onlineClassReviewList = this.jdbcTemplate.query(getReviewsQuery, ((rs, rowNum) ->
                new ClassReviews(
                        rs.getLong("classReviewId"),
                        rs.getString("imgUrl"),
                        rs.getString("nickName"),
                        rs.getString("profileImg"),
                        rs.getString("createAt"),
                        rs.getInt("rating"),
                        rs.getString("contents"))), onlineClassId );
        return onlineClassReviewList;
    }

    public GetOfflineClasses getOfflineClasses(long userId) {

        String getClassCategory = "select categoryId, categoryName from ClassCategory";
        List<ClassCategory> classCategoryList = this.jdbcTemplate.query(getClassCategory, ((rs, rowNum) ->
                new ClassCategory(rs.getLong("categoryId"), rs.getString("categoryName"))));

        String getUserAddressQuery = "select address from User where userId=?";
        String userAddressName = this.jdbcTemplate.queryForObject(getUserAddressQuery, String.class, userId);

        String getNearOfflineClasses = "select OC.offlineClassId, OCI.imgUrl, OC.address, CC.categoryName, OC.title, OC.rating, OCR.countReview\n" +
                "from OfflineClass OC\n" +
                "inner join ClassCategory CC using (categoryId)\n" +
                "inner join (select offlineClassId, imgUrl from OfflineClassImg group by (offlineClassId)) OCI using (offlineClassId)\n" +
                "left outer join (select offlineClassId, count(classReviewId) as countReview from OfflineClassReview group by (offlineClassId)) OCR using(offlineClassId)\n" +
                "where OC.address=?";
        List<NearOfflineClass> nearOfflineClassList = this.jdbcTemplate.query(getNearOfflineClasses,
                ((rs, rowNum) -> new NearOfflineClass(
                        rs.getLong("offlineClassId"),
                        rs.getString("imgUrl"),
                        rs.getString("address"),
                        rs.getString("categoryName"),
                        rs.getString("title"),
                        rs.getDouble("rating"),
                        rs.getInt("countReview"))), userAddressName);

        String getNewOfflineClasses = "select OC.offlineClassId, OCI.imgUrl, OC.address, CC.categoryName, OC.title\n" +
                "from OfflineClass OC\n" +
                "inner join ClassCategory CC using (categoryId)\n" +
                "inner join (select offlineClassId, imgUrl from OfflineClassImg group by (offlineClassId)) OCI using (offlineClassId)\n" +
                "order by (offlineClassId) desc";
        List<NewOfflineClass> newOfflineClassList = this.jdbcTemplate.query(getNewOfflineClasses, ((rs, rowNum) ->
                new NewOfflineClass(
                        rs.getLong("offlineClassId"),
                        rs.getString("imgUrl"),
                        rs.getString("address"),
                        rs.getString("categoryName"),
                        rs.getString("title"))));

        GetOfflineClasses getOfflineClasses = new GetOfflineClasses(classCategoryList, userAddressName,
                nearOfflineClassList, newOfflineClassList);
        return getOfflineClasses;
    }

    public int checkOfflineClass(long offlineClassId) {
        String checkQuery = "select exists (select offlineClassId from OfflineClass where offlineClassId=?)";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, offlineClassId);
    }

    public GetOfflineClass getOfflineClass(long userId, long offlineClassId) {
        String getOfflineClassQuery = "select CC.categoryName, OC.title, OC.price, OC.discountRate, round(OC.price-(OC.price*OC.discountRate/100), -2) as finalPrice,\n" +
                "       case when OC.level=1 then '쉬움' when OC.level=2 then '보통' else '어려움' end as level, concat(OC.timeSpend, '시간') as timeSpend,\n" +
                "       concat(OC.maxCapacity, '명') as maxCapacity, if(OCL.status=1, true, false) as userLike, OCL1.countLike, W.writerId,\n" +
                "       W.profileImg, W.nickName, OC.contents, OC.address\n" +
                "\n" +
                "from OfflineClass OC\n" +
                "inner join ClassCategory CC using(categoryId)\n" +
                "left outer join (select offlineClassId, status from OfflineClassLike where userId = ?) OCL using (offlineClassId)\n" +
                "left outer join (select offlineClassId, count(classLikeId) as countLike from OfflineClassLike group by (offlineClassId)) OCL1 using(offlineClassId)\n" +
                "inner join Writer W using(writerId)\n" +
                "where offlineClassId = ?";
        Object[] params = new Object[] {userId, offlineClassId};

        GetOfflineClass getOfflineClass = this.jdbcTemplate.queryForObject(getOfflineClassQuery, (rs, rowNum) -> new GetOfflineClass(
                rs.getString("categoryName"),
                rs.getString("title"),
                rs.getInt("price"),
                rs.getInt("discountRate"),
                rs.getInt("finalPrice"),
                rs.getString("level"),
                rs.getString("timeSpend"),
                rs.getString("maxCapacity"),
                rs.getBoolean("userLike"),
                rs.getInt("countLike"),
                rs.getLong("writerId"),
                rs.getString("profileImg"),
                rs.getString("nickName"),
                rs.getString("contents"),
                rs.getString("address")), params);

        String getClassImgQuery = "select imgUrl from OfflineClassImg where offlineClassId=?";
        List<String> classImgList = this.jdbcTemplate.query(getClassImgQuery, (rs, rowNum) -> rs.getString("imgUrl"), offlineClassId);
        getOfflineClass.setClassImgList(classImgList);

        String getReviewQuery = "select OCR.classReviewId, OCRI.imgUrl, U.nickName, U.profileImg, date_format(OCR.createAt, '%Y년 %c월 %e일') as createAt, OCR.rating, OCR.contents\n" +
                "from OfflineClassReview OCR\n" +
                "inner join User U using(userId)\n" +
                "left outer join (select classReviewId, imgUrl from OfflineClassReviewImg group by (classReviewId)) OCRI using(classReviewId)\n" +
                "where OCR.offlineClassId = ? order by (classReviewId) desc limit 5";

        List<ClassReviews> classReviewsList = this.jdbcTemplate.query(getReviewQuery, (rs, rowNum) -> new ClassReviews(
                rs.getLong("classReviewId"),
                rs.getString("imgUrl"),
                rs.getString("nickName"),
                rs.getString("profileImg"),
                rs.getString("createAt"),
                rs.getInt("rating"),
                rs.getString("contents")), offlineClassId);
        getOfflineClass.setClassReviewsList(classReviewsList);

        String getCommentQuery = "select U.nickName as userName, U.profileImg as userImg, OC.contents as userComment,\n" +
                "       W.nickName as writerName, W.profileImg as writerImg, OWC.contents as writerComment\n" +
                "from OfflineComment OC\n" +
                "inner join User U using(userId)\n" +
                "left outer join OfflineWriterComment OWC using (commentId)\n" +
                "inner join Writer W using(writerId)\n" +
                "where offlineClassId = ?";
        List<ClassComment> classCommentList = this.jdbcTemplate.query(getCommentQuery, (rs, rowNum) -> new ClassComment(
                rs.getString("userName"),
                rs.getString("userImg"),
                rs.getString("userComment"),
                rs.getString("writerName"),
                rs.getString("writerImg"),
                rs.getString("writerComment")), offlineClassId);
        getOfflineClass.setClassCommentList(classCommentList);

        return getOfflineClass;
    }

    public List<ClassReviews> getOfflineClassReviews(long userId, long offlineClassId) {

        String getReviewsQuery = "select OCR.classReviewId, OCRI.imgUrl, U.nickName, U.profileImg, date_format(OCR.createAt, '%Y년 %c월 %e일') as createAt, OCR.rating, OCR.contents\n" +
                "from OfflineClassReview OCR\n" +
                "inner join User U using(userId)\n" +
                "left outer join (select classReviewId, imgUrl from OfflineClassReviewImg group by (classReviewId)) OCRI using(classReviewId)\n" +
                "where OCR.offlineClassId = ? order by (classReviewId) desc";

        List<ClassReviews> offlineClassReviewList = this.jdbcTemplate.query(getReviewsQuery, ((rs, rowNum) ->
                new ClassReviews(
                        rs.getLong("classReviewId"),
                        rs.getString("imgUrl"),
                        rs.getString("nickName"),
                        rs.getString("profileImg"),
                        rs.getString("createAt"),
                        rs.getInt("rating"),
                        rs.getString("contents"))), offlineClassId );
        return offlineClassReviewList;
    }

    public int checkOnlineClassReviewExists(long onlineClassId, long reviewId) {
        String checkQuery = "select exists (select classReviewId from OnlineClassReview where onlineClassId = ? and classReviewId = ?)";
        Object[] params = new Object[] {onlineClassId, reviewId};

        return this.jdbcTemplate.queryForObject(checkQuery, int.class, params);
    }

    public ClassReview getOnlineClassReview(long userId, long onlineClassId, long reviewId) {
        String getReviewImgQuery = "select imgUrl from OnlineClassReviewImg where classReviewId=?";
        List<String> reviewImgList = this.jdbcTemplate.query(getReviewImgQuery, (rs, rowNum) -> rs.getString("imgUrl"), reviewId);

        String getReviewQuery = "select U.nickName, U.profileImg, date_format(OCR.createAt, '%Y년 %c월 %e일') as createAt, OCR.rating, OCR.contents\n" +
                "from OnlineClassReview OCR\n" +
                "inner join User U using (userId)\n" +
                "where classReviewId = ?";

        ClassReview classReview = this.jdbcTemplate.queryForObject(getReviewQuery, (rs, rowNum) -> new ClassReview(
                rs.getString("nickName"),
                rs.getString("profileImg"),
                rs.getString("createAt"),
                rs.getInt("rating"),
                rs.getString("contents"),
                reviewImgList), reviewId);

        return classReview;
    }

    public int checkOfflineClassReviewExists(long offlineClassId, long reviewId) {
        String checkQuery = "select exists (select classReviewId from OfflineClassReview where offlineClassId = ? and classReviewId = ?)";
        Object[] params = new Object[] {offlineClassId, reviewId};

        return this.jdbcTemplate.queryForObject(checkQuery, int.class, params);
    }

    public ClassReview getOfflineClassReview(long userId, long offlineClassId, long reviewId) {
        String getReviewImgQuery = "select imgUrl from OfflineClassReviewImg where classReviewId=?";
        List<String> reviewImgList = this.jdbcTemplate.query(getReviewImgQuery, (rs, rowNum) -> rs.getString("imgUrl"), reviewId);

        String getReviewQuery = "select U.nickName, U.profileImg, date_format(OCR.createAt, '%Y년 %c월 %e일') as createAt, OCR.rating, OCR.contents\n" +
                "from OfflineClassReview OCR\n" +
                "inner join User U using (userId)\n" +
                "where classReviewId = ?";

        ClassReview classReview = this.jdbcTemplate.queryForObject(getReviewQuery, (rs, rowNum) -> new ClassReview(
                rs.getString("nickName"),
                rs.getString("profileImg"),
                rs.getString("createAt"),
                rs.getInt("rating"),
                rs.getString("contents"),
                reviewImgList), reviewId);

        return classReview;
    }


    public GetCategories getCategories(long userId) {

        String getCategoriesQuery = "select categoryId, categoryName from ClassCategory";
        List<Category> categoryList = this.jdbcTemplate.query(getCategoriesQuery, (rs, rowNum) -> new Category(
                rs.getLong("categoryId"),
                rs.getString("categoryName")));

        String getAddressQuery = "select addressId, addressName from Address";
        List<Address> addressList = this.jdbcTemplate.query(getAddressQuery, (rs, rowNum) -> new Address(
                rs.getLong("addressId"),
                rs.getString("addressName")));

        return new GetCategories(categoryList, addressList);
    }

    public int checkCategory(long categoryId) {
        String checkQuery = "select exists (select categoryId from ClassCategory where categoryId = ?)";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, categoryId);
    }

    public List<NearOfflineClass> getCategoryClasses(long userId, long categoryId) {
        String getQuery = "select OC.offlineClassId, OCI.imgUrl, OC.address, CC.categoryName, OC.title, OC.rating, OCR.countReview\n" +
                "from OfflineClass OC\n" +
                "inner join ClassCategory CC using (categoryId)\n" +
                "inner join (select offlineClassId, imgUrl from OfflineClassImg group by (offlineClassId)) OCI using (offlineClassId)\n" +
                "left outer join (select offlineClassId, count(classReviewId) as countReview from OfflineClassReview group by (offlineClassId)) OCR using(offlineClassId)\n" +
                "where OC.categoryId =?";
        List<NearOfflineClass> getCategoryClasses = this.jdbcTemplate.query(getQuery, (rs, rowNum) -> new NearOfflineClass(
                rs.getLong("offlineClassId"),
                rs.getString("imgUrl"),
                rs.getString("address"),
                rs.getString("categoryName"),
                rs.getString("title"),
                rs.getDouble("rating"),
                rs.getInt("countReview")), categoryId);
        return getCategoryClasses;
    }

    public int checkAddress(long addressId) {
        String checkQuery = "select exists (select addressId from Address where addressId = ?)";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, addressId);
    }

    public List<NearOfflineClass> getAddressClasses(long userId, long addressId) {
        String getQuery = "select OC.offlineClassId, OCI.imgUrl, OC.address, CC.categoryName, OC.title, OC.rating, OCR.countReview\n" +
                "from OfflineClass OC\n" +
                "inner join ClassCategory CC using (categoryId)\n" +
                "inner join (select offlineClassId, imgUrl from OfflineClassImg group by (offlineClassId)) OCI using (offlineClassId)\n" +
                "left outer join (select offlineClassId, count(classReviewId) as countReview from OfflineClassReview group by (offlineClassId)) OCR using(offlineClassId)\n" +
                "where OC.addressId =?";
        List<NearOfflineClass> getCategoryClasses = this.jdbcTemplate.query(getQuery, (rs, rowNum) -> new NearOfflineClass(
                rs.getLong("offlineClassId"),
                rs.getString("imgUrl"),
                rs.getString("address"),
                rs.getString("categoryName"),
                rs.getString("title"),
                rs.getDouble("rating"),
                rs.getInt("countReview")), addressId);
        return getCategoryClasses;
    }
}
