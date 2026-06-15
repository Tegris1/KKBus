package pasir.dtos;

import java.util.List;

public record ReportOptionsDto(
        List<DriverOptionDto> drivers,
        List<Short> busIds
) {
}
