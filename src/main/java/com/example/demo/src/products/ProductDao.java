package com.example.demo.src.products;

import com.example.demo.src.products.model.CategoryProduct;
import com.example.demo.src.products.model.GetProductsRes;
import com.example.demo.src.products.model.ProductReview;
import com.example.demo.src.products.model.Products;
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
}
