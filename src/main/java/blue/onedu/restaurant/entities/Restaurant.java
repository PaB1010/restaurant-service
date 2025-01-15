package blue.onedu.restaurant.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    private Long seq;

    @Column(length = 40)
    private String category;

    // 식당 분류
    private String address;

    // 식당명
    private String name;

    // 위도  // 34.8264515
    private double latitude;

    // 경도  // 128.400468
    private double longitude;
}
