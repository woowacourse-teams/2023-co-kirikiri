package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.exception.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoalRoomPendingMembersTest {

    @Test
    void 골룸의_리더를_찾는다() {
        // given
        final GoalRoom goalRoom = new GoalRoom("goalroom", 10, GoalRoomStatus.RECRUITING,
                new RoadmapContent("content"));
        final Member member1 = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1")),
                new MemberProfile(Gender.FEMALE, LocalDate.of(2023, 7, 20), new Nickname("nickname"), "010-1111-1111"));
        final Member member2 = new Member(new Identifier("identifier2"),
                new EncryptedPassword(new Password("password2")),
                new MemberProfile(Gender.FEMALE, LocalDate.of(2023, 7, 20), new Nickname("nickname"), "010-1111-1111"));

        // when
        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(List.of(
                new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom, member1),
                new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom, member2)
        ));

        // then
        assertThat(goalRoomPendingMembers.findGoalRoomLeader()).isEqualTo(member2);
    }

    @Test
    void 골룸의_리더가_없으면_예외가_발생한다() {
        // given
        final GoalRoom goalRoom = new GoalRoom("goalroom", 10, GoalRoomStatus.RECRUITING,
                new RoadmapContent("content"));
        final Member member1 = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1")),
                new MemberProfile(Gender.FEMALE, LocalDate.of(2023, 7, 20), new Nickname("nickname"), "010-1111-1111"));
        final Member member2 = new Member(new Identifier("identifier2"),
                new EncryptedPassword(new Password("password2")),
                new MemberProfile(Gender.FEMALE, LocalDate.of(2023, 7, 20), new Nickname("nickname"), "010-1111-1111"));

        // when
        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(List.of(
                new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom, member1),
                new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom, member2)
        ));

        // then
        assertThatThrownBy(() -> assertThat(goalRoomPendingMembers.findGoalRoomLeader()))
                .isInstanceOf(NotFoundException.class);
    }
}
