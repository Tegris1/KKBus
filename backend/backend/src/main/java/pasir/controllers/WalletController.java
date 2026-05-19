package pasir.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pasir.dtos.WalletDto;
import pasir.dtos.WalletMoneyUpdateDto;
import pasir.dtos.WalletPointsUpdateDto;
import pasir.services.WalletService;

@RestController
@RequestMapping("/api/wallet")
@AllArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<WalletDto> getWallet() {
        return ResponseEntity.ok(walletService.getCurrentUserWallet());
    }

    @PatchMapping("/money")
    public ResponseEntity<WalletDto> updateMoney(@Valid @RequestBody WalletMoneyUpdateDto dto) {
        return ResponseEntity.ok(walletService.updateMoney(dto.getMoney()));
    }

    @PatchMapping("/points")
    public ResponseEntity<WalletDto> updatePoints(@Valid @RequestBody WalletPointsUpdateDto dto) {
        return ResponseEntity.ok(walletService.updatePoints(dto.getPoints()));
    }
}
