package hongik.ce.jolup.module.match.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchClosureTable {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ancestor_match_id")
    private Match ancestor;

    @ManyToOne
    @JoinColumn(name = "descendant_match_id")
    private Match descendant;
}
