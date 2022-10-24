package hongik.ce.jolup.module.competition.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompetitionType {
    SINGLE_ROUND_ROBIN("리그(단판)"),
    DOUBLE_ROUND_ROBIN("리그(홈 & 어웨이)"),
    SINGLE_ELIMINATION_TOURNAMENT("토너먼트"),
    DOUBLE_ELIMINATION_TOURNAMENT("토너먼트(패자부활전 O)");

    private final String description;
}
