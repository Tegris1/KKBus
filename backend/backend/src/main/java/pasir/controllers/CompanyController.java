package pasir.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pasir.dtos.CompanyInfoDto;

import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {
    @GetMapping
    public ResponseEntity<CompanyInfoDto> getCompanyInfo() {
        return ResponseEntity.ok(new CompanyInfoDto(
                "KKBus sp. z o.o.",
                "Jan Kowalski",
                "ul. Jana Pawla II 37, 31-864 Krakow",
                "(070) 012-34-56",
                "(070) 011-22-33",
                "Firma zajmuje sie transportem osob miedzy Krakowem a Katowicami.",
                List.of("Tomasz Rajdowiec", "Kazimierz Rajdowiec", "Miroslaw Szybki",
                        "Jan Doswiadczony", "Marek Poprawny", "Zuzanna Konkretna"),
                List.of("Piotr Uprzejmy", "Agnieszka Mila")
        ));
    }
}
