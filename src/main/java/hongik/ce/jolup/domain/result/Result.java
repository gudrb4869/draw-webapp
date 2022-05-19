package hongik.ce.jolup.domain.result;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Result {
    @Column(nullable = false)
    private Integer plays;

    @Column(nullable = false)
    private Integer win;

    @Column(nullable = false)
    private Integer draw;

    @Column(nullable = false)
    private Integer lose;

    @Column(nullable = false)
    private Integer goalFor;

    @Column(nullable = false)
    private Integer goalAgainst;

    @Column(nullable = false)
    private Integer goalDifference;

    @Column(nullable = false)
    private Integer points;
}