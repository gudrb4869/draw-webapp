package hongik.ce.jolup.module.competition.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompetitionOption {
    SINGLE_ROUND_ROBIN("싱글 라운드 로빈"),
    DOUBLE_ROUND_ROBIN("더블 라운드 로빈"),
    SINGLE_ELIMINATION_TOURNAMENT("싱글 엘리미네이션 토너먼트"),
    DOUBLE_ELIMINATION_TOURNAMENT("더블 엘리미네이션 토너먼트");

    private final String description;
}
