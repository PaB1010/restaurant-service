package blue.onedu.restaurant.services;

import blue.onedu.restaurant.controllers.RestaurantSearch;
import blue.onedu.restaurant.entities.QRestaurant;
import blue.onedu.restaurant.entities.Restaurant;
import blue.onedu.restaurant.exceptions.RestaurantNotFoundException;
import blue.onedu.restaurant.repositories.RestaurantRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;

@Lazy
@Service
@RequiredArgsConstructor
public class RestaurantInfoService {

    private final RestaurantRepository restaurantRepository;

    private final JPAQueryFactory queryFactory;

    public Restaurant get(Long seq) {
        return restaurantRepository.findById(seq).orElseThrow(RestaurantNotFoundException::new);
    }

    /**
     * 식당목록 조회
     *
     * @param search
     * @return
     */
    public List<Restaurant> getList(RestaurantSearch search)  {
        QRestaurant restaurant = QRestaurant.restaurant;

        String sido = search.getSido();
        String sigugun = search.getSigugun();

        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(sido)) {
            builder.and(restaurant.address.startsWith(sido));

            if (StringUtils.hasText(sigugun)) { // sido가 있을때만 시구군 검색
                builder.and(restaurant.address.contains(sigugun));
            }
        }

        List<String> category = search.getCategory();
        if (category != null && !category.isEmpty()) {
            builder.and(restaurant.category.in(category));
        }

        String sort = search.getSort();
        Sort s = Sort.by(asc("name"));
        if (StringUtils.hasText(sort)) {
            String[] _sort = sort.split("_");
            String field = _sort[0];
            String direction = _sort[1];

            if (field.equals("name")) s = direction.equals("DESC") ? Sort.by(desc("name")) : Sort.by(asc("name"));
            else if (field.equals("address")) s = direction.equals("DESC") ? Sort.by(desc("address")) : Sort.by(asc("address"));
        }


        return (List<Restaurant>)restaurantRepository.findAll(builder, s);
    }

    public List<String> getCategories() {
        QRestaurant restaurant = QRestaurant.restaurant;
        List<String> items = queryFactory.select(restaurant.category).distinct()
                .from(restaurant)
                .where(restaurant.category.isNotNull())
                .orderBy(restaurant.category.asc())
                .fetch();

        return items;
    }
}