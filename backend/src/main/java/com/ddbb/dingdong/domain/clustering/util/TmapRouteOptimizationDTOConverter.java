package com.ddbb.dingdong.domain.clustering.util;

import com.ddbb.dingdong.domain.clustering.entity.Location;
import com.ddbb.dingdong.domain.clustering.model.dto.RequestRouteOptimizationDTO;
import com.ddbb.dingdong.domain.clustering.model.dto.RequestRouteOptimizationDTO.ViaPoint;
import com.ddbb.dingdong.domain.clustering.model.dto.ResponseRouteOptimizationDTO;
import com.ddbb.dingdong.domain.transportation.entity.BusStop;
import com.ddbb.dingdong.domain.transportation.entity.Path;
import com.ddbb.dingdong.domain.transportation.entity.Point;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ddbb.dingdong.domain.clustering.model.dto.ResponseRouteOptimizationDTO.Feature;
import static com.ddbb.dingdong.domain.clustering.model.dto.ResponseRouteOptimizationDTO.Feature.Geometry.GeometryType.LINE_STRING;
import static com.ddbb.dingdong.domain.clustering.model.dto.ResponseRouteOptimizationDTO.Feature.Geometry.GeometryType.POINT;

@Component
@Primary
public class TmapRouteOptimizationDTOConverter implements RouteOptimizationDTOConverter<RequestRouteOptimizationDTO, ResponseRouteOptimizationDTO> {

    @Override
    public RequestRouteOptimizationDTO fromLocations(List<Location> locations, Double endLatitude, Double endLongitude) {
        List<ViaPoint> viaPoints = new ArrayList<>();
        int id = 0;
        for (int i = 0; i < locations.size(); i++) {
            Location currentLocation = locations.get(i);
            viaPoints.add(
                    new ViaPoint(
                            Integer.toString(++id),
                            Double.toString(currentLocation.getLongitude()),
                            Double.toString(currentLocation.getLatitude())
                    )
            );
        }

        HaversineDistanceFunction haversine = HaversineDistanceFunction.getInstance();

        Optional<ViaPoint> farthestPoint = viaPoints.stream().max((o1, o2) -> {
            double[] point1 = {Double.parseDouble(o1.getViaY()), Double.parseDouble(o1.getViaX())};
            double[] point2 = {Double.parseDouble(o2.getViaY()), Double.parseDouble(o2.getViaX())};
            double[] endPoint = {endLatitude, endLongitude};

            double point1Distance = haversine.distance(point1, endPoint);
            double point2Distance = haversine.distance(point2, endPoint);

            return Double.compare(point1Distance, point2Distance);
        });

        viaPoints.remove(farthestPoint.get());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        RequestRouteOptimizationDTO request = new RequestRouteOptimizationDTO(
                farthestPoint.get().getViaX(),
                farthestPoint.get().getViaY(),
                formatter.format(LocalDateTime.now()),
                Double.toString(endLongitude),
                Double.toString(endLatitude),
                viaPoints.subList(1, Math.min(viaPoints.size(), 20))
        );
        return request;
    }

    @Override
    public List<Path> toPaths(List<ResponseRouteOptimizationDTO> responses) {
        List<Path> paths = new ArrayList<>();
        for (ResponseRouteOptimizationDTO response : responses) {
            List<Point> points = new ArrayList<>();
            List<BusStop> busStops = new ArrayList<>();
            int pointSequence = 0;
            int busStopSequence = 0;
            for (Feature feature : response.getFeatures()) {
                Feature.Geometry geometry = feature.getGeometry();
                List<Double> coordinate = geometry.getCoordinates().get(0).doubleArrayValue;
                if (geometry.getType().equals(POINT)) {
                    points.add(new Point(
                            null,
                            pointSequence++,
                            BigDecimal.valueOf(coordinate.get(1)),
                            BigDecimal.valueOf(coordinate.get(0)),
                            null)
                    );
                } else if (geometry.getType().equals(LINE_STRING)) {
                    busStops.add(new BusStop(
                            null,
                            "I_DONT_KNOW...",
                            busStopSequence++,
                            BigDecimal.valueOf(coordinate.get(1)),
                            BigDecimal.valueOf(coordinate.get(0)),
                            LocalDateTime.now(), // TODO : 예상 도착 시간 로직 구현해야 함
                            null
                    ));
                }

            }

            Path path = new Path(
                    null,
                    BigDecimal.valueOf(Long.parseLong(response.getProperties().getTotalDistance())),
                    Long.parseLong(response.getProperties().getTotalTime()),
                    null,
                    points,
                    busStops
            );
        }

        return List.of();
    }
}
