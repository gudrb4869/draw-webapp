package hongik.ce.jolup.domain.competition;

import hongik.ce.jolup.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class H2HGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private Member home;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_id")
    private Member away;
}
