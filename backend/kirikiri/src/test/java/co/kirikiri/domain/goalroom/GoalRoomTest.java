package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.exception.BadRequestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GoalRoomTest {

    private static Member member;
    private static MemberProfile memberProfile;

    @BeforeAll
    static void setUp() {
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final EncryptedPassword encryptedPassword = new EncryptedPassword(password);
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";
        memberProfile = new MemberProfile(Gender.MALE, LocalDate.now(), nickname, phoneNumber);
        member = new Member(identifier, encryptedPassword, memberProfile);
    }

    @Test
    void 정상적으로_골룸에_참여한다() {
        //given
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, member);
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalRoomName"), new LimitedMemberCount(20), new RoadmapContent("content"));

        //when
        //then
        assertDoesNotThrow(() -> goalRoom.participate(goalRoomPendingMember));
    }

    @Test
    void 골룸_참여_시_정원이_다_찼을때_예외를_던진다() {
        //given
        final Identifier identifier = new Identifier("identifier2");
        final Password password = new Password("password1!");
        final EncryptedPassword encryptedPassword = new EncryptedPassword(password);
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";
        final Member member2 = new Member(identifier, encryptedPassword, new MemberProfile(Gender.MALE, LocalDate.now(), nickname, phoneNumber));
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, member2);

        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalRoomName"), new LimitedMemberCount(1), new RoadmapContent("content"));
        goalRoom.participate(goalRoomPendingMember2);

        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, member);

        //when
        //then
        assertThatThrownBy(() -> goalRoom.participate(goalRoomPendingMember))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("정원 초과입니다.");
    }

    @Test
    void 골룸_참여_시_이미_참여한_사람이_참여할_때_예외를_던진다() {
        //given
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalRoomName"), new LimitedMemberCount(20), new RoadmapContent("content"));
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, member);
        goalRoom.participate(goalRoomPendingMember);

        //when
        //then
        assertThatThrownBy(() -> goalRoom.participate(goalRoomPendingMember))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이미 참여 중인 상태입니다.");
    }
}
