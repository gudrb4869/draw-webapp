package hongik.ce.jolup.domain.matches;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long cid;
    @Column(nullable = false)
    private Long mid1;
    @Column(nullable = false)
    private Long mid2;
    @Column(nullable = false)
    private Long score1;
    @Column(nullable = false)
    private Long score2;
}
