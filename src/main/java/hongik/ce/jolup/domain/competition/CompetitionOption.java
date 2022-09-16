package hongik.ce.jolup.domain.competition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompetitionOption {
    O1("단판"),
    O2("2판"),
    O3("3판 2선");
    private final String description;
}
