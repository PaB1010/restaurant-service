package blue.onedu.restaurant.controllers;

import blue.onedu.restaurant.entities.Restaurant;
import blue.onedu.restaurant.exceptions.RestaurantNotFoundException;
import blue.onedu.restaurant.services.RestaurantInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantInfoService infoService;

    @GetMapping("/info/{seq}")
    public Restaurant info(@PathVariable("seq") Long seq) {
        return infoService.get(seq);
    }

    @GetMapping("/list")
    public List<Restaurant> list(RestaurantSearch search) {
        return infoService.getList(search);
    }

    @GetMapping("/category")
    public List<String> categories() {
        return infoService.getCategories();
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<Void> errorHandler() {

        return ResponseEntity.notFound().build();
    }
}