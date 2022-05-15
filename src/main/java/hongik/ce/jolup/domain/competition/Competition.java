package hongik.ce.jolup.domain.competition;

import hongik.ce.jolup.domain.Time;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.dto.CompetitionDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "competition")
@ToString(of = {"id", "title", "competitionType", "headCount"})
public class Competition extends Time {
    /*
    drop table if exists competition CASCADE;
    create table competition(
    competition_id bigint generated by default as identity,
    subject varchar(255) not null,
    competition_type enum('LEAGUE', 'TOURNAMENT') not null,
    mem_num bigint not null,
    primary key(competition_id)
    );
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_id")
    private Long id;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompetitionType competitionType;

    @Column(nullable = false)
    private Long headCount;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Join> joins = new ArrayList<>();

    @Builder
    public Competition(Long id, String title, CompetitionType competitionType, Long headCount/*, List<Join> joins*/) {
        this.id = id;
        this.title = title;
        this.competitionType = competitionType;
        this.headCount = headCount;
//        this.joins = joins;
    }

    public CompetitionDto toDto() {
        return CompetitionDto.builder()
                .id(id)
                .title(title)
                .competitionType(competitionType)
                .headCount(headCount)
//                .joinDtos(joins.stream().map(Join::toDto).collect(Collectors.toList()));
                .build();
    }

    public Competition update(String title, CompetitionType competitionType, Long headCount) {
        this.title = title;
        this.competitionType = competitionType;
        this.headCount = headCount;
        return this;
    }
}