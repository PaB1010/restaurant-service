package blue.onedu.datatransform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.proj4j.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CSV Mapper")
public class Transfer {

    @Test
    @DisplayName("CSV Mapper Test")
    void process() {

        CsvMapper mapper = new CsvMapper();

        try (BufferedReader br = new BufferedReader(new FileReader("C:/data/data.csv"))) {

            String line;

            while ((line = br.readLine()) != null) {

                try {

                    // 메모리 때문에 한줄 한줄 읽어옴
                    List<String> item = mapper.readValue(line, new TypeReference<>() {
                    });

                    // System.out.println(item);
                    
                    // 폐업 건너뛰기
                    if (item.get(8).equals("폐업")) continue;
                    
                    // 좌표 변환 (위도/경도로)
                    // double lat = Double.parseDouble(item.get(26));
                    // double lon = Double.parseDouble(item.get(27));
                    // System.out.printf("lat = %s, lon = %s%n", lat, lon);

                    double[] location = transformTMToWGS84(Double.parseDouble(item.get(26)), Double.parseDouble(item.get(27)));

                    System.out.println(item);
                    System.out.println(Arrays.toString(location));
                    System.out.println("--------------------------");

                } catch (Exception e) {}
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private double[] transformTMToWGS84(double lon, double lat) {
        CRSFactory crsFactory = new CRSFactory();

        // WGS84 좌표계 (EPSG:4326)
        CoordinateReferenceSystem crsWGS84 = crsFactory.createFromName("EPSG:4326");

        // TM 중부원점 좌표계 (EPSG:2097)
        CoordinateReferenceSystem crsTM = crsFactory.createFromParameters("EPSG:2097", "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +units=m +no_defs");

        // CoordinateTransformFactory 생성
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

        // 좌표 변환 객체 생성
        //CoordinateTransform transform = ctFactory.createTransform(crsWGS84, crsTM);
        CoordinateTransform transform = ctFactory.createTransform(crsTM, crsWGS84);
        // 변환할 좌표 설정
        ProjCoordinate sourceCoordinate = new ProjCoordinate(lon, lat);
        ProjCoordinate targetCoordinate = new ProjCoordinate();

        // 좌표 변환 수행
        transform.transform(sourceCoordinate, targetCoordinate);

        return new double[]{targetCoordinate.x, targetCoordinate.y};
    }
}