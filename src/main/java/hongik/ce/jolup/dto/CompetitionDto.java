package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.CompetitionType;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionDto {
    private Long id;
    @NotBlank(message = "대회 이름을 입력해주세요!")
    @Size(min = 3, max = 30, message = "최소 3글자 최대 30글자로 입력해주세요!")
    private String title;
    private CompetitionType competitionType;
    private RoomDto roomDto;

    public Competition toEntity() {
        return Competition.builder()
                .id(id)
                .title(title)
                .competitionType(competitionType)
                .room(roomDto.toEntity())
                .build();
    }
}
