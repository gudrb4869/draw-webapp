package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.room.RoomType;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomForm {
    private List<@NotBlank(message = "참가자 ID는 필수 입력 값입니다!") String> emails = new ArrayList<>();

    @NotBlank(message = "제목은 필수 입력 값입니다!")
    private String title;

    @NotNull(message = "대회 방식은 필수 입력 값입니다!")
    private RoomType roomType;

    @NotNull(message = "참여 인원수는 필수 입력 값입니다!")
    private Long memNum;

    public void addEmail(String email) {
        this.emails.add(email);
    }
}
