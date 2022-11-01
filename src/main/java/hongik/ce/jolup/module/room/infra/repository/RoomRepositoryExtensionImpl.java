package hongik.ce.jolup.module.room.infra.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import hongik.ce.jolup.module.room.domain.entity.QRoom;
import hongik.ce.jolup.module.room.domain.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class RoomRepositoryExtensionImpl extends QuerydslRepositorySupport implements RoomRepositoryExtension {

    public RoomRepositoryExtensionImpl() {
        super(Room.class);
    }

    @Override
    public Page<Room> findByKeyword(String keyword, Pageable pageable) {
        QRoom room = QRoom.room;
        JPQLQuery<Room> query = from(room)
                .where(room.title.containsIgnoreCase(keyword)
                        .and(room.revealed.isTrue()))
                .distinct();
        JPQLQuery<Room> jpqlQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Room> fetchResults = jpqlQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }
}
