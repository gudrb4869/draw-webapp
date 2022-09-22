package hongik.ce.jolup.domain.competition;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_Id")
    private Competition competition;

    @Builder
    public League(Long id, String name, Competition competition) {
        this.id = id;
        this.name = name;
        this.competition = competition;
    }
}
