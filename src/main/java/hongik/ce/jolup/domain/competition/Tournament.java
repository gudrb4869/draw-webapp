package hongik.ce.jolup.domain.competition;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "competitionId")
    private Competition competition;

    @Builder
    public Tournament(Long id, String name, Competition competition) {
        this.id = id;
        this.name = name;
        this.competition = competition;
    }
}
