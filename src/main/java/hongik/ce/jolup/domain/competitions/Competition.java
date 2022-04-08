package hongik.ce.jolup.domain.competitions;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private CompetitionType type;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private Long count;
}
