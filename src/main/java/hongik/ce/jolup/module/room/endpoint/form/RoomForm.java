package hongik.ce.jolup.module.room.endpoint.form;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RoomForm {

    @NotBlank(message = "방명을 입력하세요.")
    private String title;

    @NotNull(message = "공개 여부를 선택하세요.")
    private Boolean access;

    @NotBlank
    @Length(max = 50)
    private String shortDescription;
}
