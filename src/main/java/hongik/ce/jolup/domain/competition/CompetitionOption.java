package hongik.ce.jolup.domain.competition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompetitionOption {
    SINGLE("단판"),
    DOUBLE("2판");
//    THREE_OUT_OF_TWO_WINS("3판 2선");
    private final String description;
}
