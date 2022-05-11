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

    @NotBlank(message = "대회 이름은 필수 입력 값입니다!")
    private String title;

    @NotNull(message = "대회 방식은 필수 입력 값입니다!")
    private RoomType roomType;

    @NotNull(message = "대회 참여 인원 수는 필수 입력 값입니다!")
    private Long memNum;

    /*public RoomForm() {
        this.roomType = RoomType.LEAGUE;
        this.memNum = 2L;
        for (int i = 0; i < memNum; i++) {
            addEmail(new String());
        }
    }

    public RoomForm(List<String> emails, String title, RoomType roomType, Long memNum) {
        this.emails = emails;
        this.title = title;
        this.roomType = roomType;
        this.memNum = memNum;
        for (int i = 0; i < memNum; i++) {
            if (i >= emails.size()) {
                addEmail(new String());
                continue;
            }
            addEmail(emails.get(i));
        }
    }*/

    public void addEmail(String email) {
        this.emails.add(email);
    }
}
