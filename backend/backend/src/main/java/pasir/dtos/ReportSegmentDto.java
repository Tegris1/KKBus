package pasir.dtos;

public record ReportSegmentDto(
        String origin,
        String destination,
        Integer passengerCount
) {
}
