package hongik.ce.jolup.module.competition.infra.repository;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.Participate;

import java.util.List;

public interface ParticipateRepositoryCustom {
    List<Participate> findLeagueRankingByCompetition(Competition competition);
}
