package hongik.ce.jolup.module.competition.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompetitionType {
    LEAGUE("리그(단판)"),
    TOURNAMENT("토너먼트");

    private final String description;
}
