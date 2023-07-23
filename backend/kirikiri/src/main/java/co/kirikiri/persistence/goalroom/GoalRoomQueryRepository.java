package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.member.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GoalRoomQueryRepository {

    Optional<GoalRoom> findByIdWithMember(final Long Id, final Member member);

    Page<GoalRoom> findGoalRoomsPageByMember(final Member member, final Pageable pageable);
}
