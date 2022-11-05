package hongik.ce.jolup.module.competition.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompetitionType {
    SINGLE_ROUND_ROBIN("리그"),
    DOUBLE_ROUND_ROBIN("리그(홈 앤드 어웨이)"),
    SINGLE_ELIMINATION_TOURNAMENT("싱글 엘리미네이션 토너먼트"),
    DOUBLE_ELIMINATION_TOURNAMENT("더블 엘리미네이션 토너먼트");

    private final String description;
}
