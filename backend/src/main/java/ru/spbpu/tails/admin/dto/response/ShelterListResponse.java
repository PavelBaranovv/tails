package ru.spbpu.tails.admin.dto.response;

import java.util.List;

public record ShelterListResponse(
        List<ShelterResponse> content
) {
}
