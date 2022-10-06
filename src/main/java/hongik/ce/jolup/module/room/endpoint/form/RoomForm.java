package hongik.ce.jolup.module.room.endpoint.form;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RoomForm {

    @NotBlank(message = "방명을 입력하세요.")
    private String name;

    @NotNull(message = "공개 여부를 선택하세요.")
    private boolean access;
}
