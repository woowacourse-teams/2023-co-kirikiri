package co.kirikiri.persistence.goalroom;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.LimitedMemberCount;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

@RepositoryTest
public class GoalRoomRepositoryTest {

    private final MemberRepository memberRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;

    public GoalRoomRepositoryTest(final MemberRepository memberRepository,
                                  final RoadmapCategoryRepository roadmapCategoryRepository,
                                  final RoadmapRepository roadmapRepository,
                                  final GoalRoomRepository goalRoomRepository) {
        this.memberRepository = memberRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
    }

    @Test
    void 골룸에_참가한다() {
        //given
        final Roadmap roadmap = 로드맵을_생성한다();
        final GoalRoom goalRoom = 골룸을_생성한다(roadmap.getContents().getContents().get(0));
        final Member member = 사용자를_생성한다("identifier3", "닉네임3");
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(member);

        //when
        goalRoom.addMember(goalRoomPendingMember);

        //then
        assertThat(goalRoom.getCurrentMemberCount().getValue()).isEqualTo(2);
    }

    private Roadmap 로드맵을_생성한다() {
        final Member creator = 사용자를_생성한다("identifier1", "닉네임1");
        final RoadmapCategory category = 로드맵_카테고리를_생성한다();
        final RoadmapContent content = new RoadmapContent("로드맵 제목");

        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 설명", 100,
                RoadmapDifficulty.NORMAL, RoadmapStatus.CREATED, creator, category);
        roadmap.addContent(content);

        return roadmapRepository.save(roadmap);
    }

    private Member 사용자를_생성한다(final String identifier, final String nickname) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1995, 9, 30),
                new Nickname(nickname), "010-1234-5678");
        final Member member = new Member(new Identifier(identifier),
                new EncryptedPassword(new Password("password1!")), memberProfile);

        return memberRepository.save(member);
    }

    private RoadmapCategory 로드맵_카테고리를_생성한다() {
        final RoadmapCategory category = new RoadmapCategory("운동");
        return roadmapCategoryRepository.save(category);
    }

    private GoalRoom 골룸을_생성한다(final RoadmapContent roadmapContent) {
        final GoalRoom goalRoom = new GoalRoom("골룸 이름",
                new LimitedMemberCount(20),
                roadmapContent,
                new GoalRoomPendingMember(사용자를_생성한다("identifier2", "닉네임2"))
        );

        return goalRoomRepository.save(goalRoom);
    }
}
