package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GoalRoomQueryRepository {

    Page<GoalRoom> findGoalRoomsPageByMember(final Member member, final Pageable pageable);
}
