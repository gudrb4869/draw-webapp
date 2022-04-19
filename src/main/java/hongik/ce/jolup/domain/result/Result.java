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
    @Column(name = "p", nullable = false)
    private Integer plays;

    @Column(name = "w", nullable = false)
    private Integer win;

    @Column(name = "d", nullable = false)
    private Integer draw;

    @Column(name = "l", nullable = false)
    private Integer lose;

    @Column(name = "gf", nullable = false)
    private Integer goalFor;

    @Column(name = "ga", nullable = false)
    private Integer goalAgainst;

    @Column(name = "gd", nullable = false)
    private Integer goalDifference;

    @Column(name = "pts", nullable = false)
    private Integer points;
}