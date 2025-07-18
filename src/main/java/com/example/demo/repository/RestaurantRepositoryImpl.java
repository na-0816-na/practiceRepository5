package com.example.demo.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Restaurant;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {

	private final JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Restaurant> selectByNameWildcard(String restaurantName) {

		String sql =
			" SELECT                                                 " + 
			"   mr.restaurant_id,                                    " +
			"   mr.restaurant_name,                                  " +
			"   mr.catch_phrase,                                     " +
			"   COALESCE(COUNT(tr.rating),0) AS review_cnt         " +
			" FROM                                                   " +
			"  	m_restaurant mr                                      " +
			"  	LEFT OUTER JOIN t_review tr                          " +
			"  	             ON mr.restaurant_id = tr.restaurant_id  " +
			" WHERE                                                  " +
			"  	mr.restaurant_name LIKE ?                            " +
			" GROUP BY                                               " +
			"  	mr.restaurant_id,                                    " +
			"  	mr.restaurant_name,                                  " +
			"  	mr.catch_phrase                                      " +
			" ORDER BY                                               " +
			"   mr.restaurant_id                                     ";
		
		String p = "%" + restaurantName + "%";	// プレースホルダの値
		
		// SQLで検索（プレースホルダ：p）
		List<Map<String, Object>> list 
				= jdbcTemplate.queryForList(sql, p);
		
		// 値の取得⇒結果の格納
		List<Restaurant> result = new ArrayList<Restaurant>(); // 結果の初期化
		for (Map<String, Object> one : list) {
			Restaurant restaurant = new Restaurant();
			restaurant.setRestaurantId((int)one.get("restaurant_id"));
			restaurant.setRestaurantName((String)one.get("restaurant_name"));
			restaurant.setCatchPhrase((String)one.get("catch_phrase"));
			int i = ((Long)one.get("review_cnt")).intValue();
			restaurant.setReviewCnt(i);
			result.add(restaurant);
		}
		
		return result;
	}

}
