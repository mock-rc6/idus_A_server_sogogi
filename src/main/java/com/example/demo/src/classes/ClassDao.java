package com.example.demo.src.classes;

import com.example.demo.src.classes.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
