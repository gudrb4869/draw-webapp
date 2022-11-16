package hongik.ce.jolup.module.competition.infra.repository;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ParticipateRepositoryCustom {
    @Transactional(readOnly = true)
    List<Participate> findLeagueRankingByCompetition(Competition competition);
}
