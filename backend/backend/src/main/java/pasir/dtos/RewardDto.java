package pasir.dtos;

public record RewardDto(
        Long id,
        String name,
        String description,
        int pointsCost,
        boolean affordable
) {
}
