package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.exception.BadRequestException;
import org.junit.jupiter.api.Test;

class GoalRoomTest {

    @Test
    void 골룸에_사용자를_추가한다() {
        //given
        final GoalRoom goalRoom = new GoalRoom("골룸 이름",
                new LimitedMemberCount(10),
                new RoadmapContent("로드맵 내용"),
                new GoalRoomPendingMembers()
        );
        final GoalRoomPendingMember member = new GoalRoomPendingMember();

        //when
        goalRoom.addMember(member);

        //then
        final Integer currentMemberCount = goalRoom.getCurrentMemberCount().getValue();
        assertThat(currentMemberCount).isEqualTo(1);
    }

    @Test
    void 모집중이_아닌_골룸에_사용자를_추가하면_예외가_발생한다() {
        //given
        final GoalRoom goalRoom = new GoalRoom("골룸 이름",
                new LimitedMemberCount(10),
                new RoadmapContent("로드맵 내용"),
                new GoalRoomPendingMembers()
        );
        goalRoom.updateStatus(GoalRoomStatus.RUNNING);
        final GoalRoomPendingMember member = new GoalRoomPendingMember();

        //when, then
        assertThatThrownBy(() -> goalRoom.addMember(member))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("모집 중이지 않은 골룸에는 참여할 수 없습니다.");
    }

    @Test
    void 제한_인원이_가득_찬_골룸에_사용자를_추가하면_예외가_발생한다() {
        //given
        final GoalRoom goalRoom = new GoalRoom("골룸 이름",
                new LimitedMemberCount(1),
                new RoadmapContent("로드맵 내용"),
                new GoalRoomPendingMembers()
        );
        final GoalRoomPendingMember member1 = new GoalRoomPendingMember();
        final GoalRoomPendingMember member2 = new GoalRoomPendingMember();
        goalRoom.addMember(member1);

        //when,then
        assertThatThrownBy(() -> goalRoom.addMember(member2))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("제한 인원이 꽉 찬 골룸에는 참여할 수 없습니다.");
    }
}
