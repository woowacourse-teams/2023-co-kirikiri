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
import co.kirikiri.exception.BadRequestException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class GoalRoomTest {

    @Test
    void 골룸에_사용자를_추가한다() {
        //given
        final GoalRoom goalRoom = new GoalRoom("골룸 이름", new LimitedMemberCount(10), new RoadmapContent("로드맵 내용"),
                사용자를_생성한다("identifier1", "시진이"));
        final Member member = 사용자를_생성한다("identifier2", "팔로워");

        //when
        goalRoom.join(member);

        //then
        final Integer currentMemberCount = goalRoom.getCurrentMemberCount();
        assertThat(currentMemberCount)
                .isEqualTo(2);
    }

    @Test
    void 모집중이_아닌_골룸에_사용자를_추가하면_예외가_발생한다() {
        //given
        final GoalRoom goalRoom = new GoalRoom("골룸 이름", new LimitedMemberCount(10), new RoadmapContent("로드맵 내용"),
                사용자를_생성한다("identifier1", "시진이"));
        goalRoom.updateStatus(GoalRoomStatus.RUNNING);
        final Member member = 사용자를_생성한다("identifier2", "팔로워");

        //when, then
        assertThatThrownBy(() -> goalRoom.join(member))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("모집 중이지 않은 골룸에는 참여할 수 없습니다.");
    }

    @Test
    void 제한_인원이_가득_찬_골룸에_사용자를_추가하면_예외가_발생한다() {
        //given
        final GoalRoom goalRoom = new GoalRoom("골룸 이름", new LimitedMemberCount(1), new RoadmapContent("로드맵 내용"),
                사용자를_생성한다("identifier1", "시진이"));
        final Member member = 사용자를_생성한다("identifier2", "팔로워");

        //when,then
        assertThatThrownBy(() -> goalRoom.join(member))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("제한 인원이 꽉 찬 골룸에는 참여할 수 없습니다.");
    }

    @Test
    void 이미_참여_중인_사용자를_골룸에_추가하면_예외가_발생한다() {
        //given
        final Member member = 사용자를_생성한다("identifier1", "시진이");
        final GoalRoom goalRoom = new GoalRoom("골룸 이름", new LimitedMemberCount(2),
                new RoadmapContent("로드맵 내용"), member);

        //when,then
        assertThatThrownBy(() -> goalRoom.join(member))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이미 참여한 골룸에는 참여할 수 없습니다.");
    }

    private Member 사용자를_생성한다(final String identifier, final String nickname) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1995, 9, 30),
                new Nickname(nickname), "010-1234-5678");

        return new Member(1L, new Identifier(identifier), new EncryptedPassword(new Password("password1!")),
                memberProfile);
    }
}
