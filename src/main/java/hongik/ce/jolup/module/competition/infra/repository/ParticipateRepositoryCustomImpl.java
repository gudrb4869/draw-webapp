package hongik.ce.jolup.module.competition.infra.repository;

import com.querydsl.jpa.JPQLQuery;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.competition.domain.entity.QParticipate;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ParticipateRepositoryCustomImpl extends QuerydslRepositorySupport implements ParticipateRepositoryCustom {
    public ParticipateRepositoryCustomImpl() {
        super(Participate.class);
    }

    @Override
    public List<Participate> findLeagueRankingByCompetition(Competition competition) {
        QParticipate participate = QParticipate.participate;
        List<Participate> participates = from(participate)
                .where(participate.competition.eq(competition))
                .leftJoin(participate.member).fetchJoin()
                .leftJoin(participate.competition).fetchJoin()
                .orderBy(participate.win.multiply(3).add(participate.draw).desc(), participate.goalFor.subtract(participate.goalAgainst).desc(),
                        participate.goalFor.desc())
                .fetch();
        return participates;
    }
}
