package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.CompetitionType;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionDto {
    private Long id;
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
