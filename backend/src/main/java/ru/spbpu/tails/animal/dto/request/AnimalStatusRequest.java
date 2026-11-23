package ru.spbpu.tails.animal.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.spbpu.tails.common.entity.AnimalStatus;

@Getter
@Setter
@NoArgsConstructor
public class AnimalStatusRequest {

    @NotNull
    private AnimalStatus status;
}
