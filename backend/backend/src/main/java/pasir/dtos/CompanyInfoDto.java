package pasir.dtos;

import java.util.List;

public record CompanyInfoDto(
        String name,
        String owner,
        String address,
        String phone,
        String fax,
        String description,
        List<String> drivers,
        List<String> secretariat
) {
}
