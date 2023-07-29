package co.kirikiri.persistence.goalroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.goalroom.vo.Period;
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
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.persistence.goalroom.dto.GoalRoomFilterType;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@RepositoryTest
class GoalRoomRepositoryTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LAYER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final GoalRoomRepository goalRoomRepository;

    public GoalRoomRepositoryTest(final MemberRepository memberRepository, final RoadmapRepository roadmapRepository,
                                  final RoadmapCategoryRepository roadmapCategoryRepository,
                                  final GoalRoomRepository goalRoomRepository) {
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.goalRoomRepository = goalRoomRepository;
    }

    @Test
    void 골룸_아이디로_골룸_정보를_조회한다() {
        // given
        //given
        final Member creator = 사용자를_생성한다("name1", "01011111111", "identifier1", "password!1");
        final RoadmapCategory category = 카테고리를_저장한다("여가");
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = 로드맵_노드를_생성한다("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1, roadmapNode2));
        로드맵을_생성한다(creator, category, new RoadmapNodes(List.of(roadmapNode1, roadmapNode2)), roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNode2);
        final Member goalRoomPendingMember1 = 사용자를_생성한다("name2", "01011112222", "identifier2", "password!2");
        final GoalRoom goalRoom = 골룸을_생성한다("goalroom1", 6, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)), goalRoomPendingMember1);

        // when
        final GoalRoom findGoalRoom = goalRoomRepository.findByIdWithRoadmapContent(goalRoom.getId())
                .get();

        // then
        assertThat(findGoalRoom)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(goalRoom);
    }

    @Test
    void 골룸을_최신순으로_조회한다() {
        //given
        final Member creator = 사용자를_생성한다("name1", "01011111111", "identifier1", "password!1");
        final RoadmapCategory category = 카테고리를_저장한다("여가");
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = 로드맵_노드를_생성한다("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1, roadmapNode2));
        로드맵을_생성한다(creator, category, new RoadmapNodes(List.of(roadmapNode1, roadmapNode2)), roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNode2);
        final Member goalRoomPendingMember1 = 사용자를_생성한다("name2", "01011112222", "identifier2", "password!2");
        final GoalRoom goalRoom1 = 골룸을_생성한다("goalroom1", 6, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)), goalRoomPendingMember1);

        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNode2);
        final Member goalRoomPendingMember2 = 사용자를_생성한다("name3", "01011113333", "identifier3", "password!3");
        final GoalRoom goalRoom2 = 골룸을_생성한다("goalroom2", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3, goalRoomRoadmapNode4)), goalRoomPendingMember2);

        // when
        final Page<GoalRoom> goalRoomsPage = goalRoomRepository.findGoalRoomsWithPendingMembersPageByCond(
                GoalRoomFilterType.LATEST, PageRequest.of(0, 2));

        // then
        assertAll(
                () -> assertThat(goalRoomsPage.getTotalPages()).isEqualTo(1),
                () -> assertThat(goalRoomsPage.getContent()).hasSize(2),
                () -> assertThat(goalRoomsPage.getContent()).isEqualTo(List.of(goalRoom2, goalRoom1))
        );
    }

    @Test
    void 골룸을_참여율_순으로_조회한다() {
        //given
        final Member creator = 사용자를_생성한다("name1", "01011111111", "identifier1", "password!1");
        final RoadmapCategory category = 카테고리를_저장한다("여가");
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = 로드맵_노드를_생성한다("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1, roadmapNode2));
        로드맵을_생성한다(creator, category, new RoadmapNodes(List.of(roadmapNode1, roadmapNode2)),
                roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNode2);
        final Member goalRoomPendingMember1 = 사용자를_생성한다("name2", "01011112222", "identifier2", "password!2");
        final GoalRoom goalRoom1 = 골룸을_생성한다("goalroom1", 6, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)), goalRoomPendingMember1);

        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNode2);
        final Member goalRoomPendingMember2 = 사용자를_생성한다("name3", "01011113333", "identifier3", "password!3");
        final GoalRoom goalRoom2 = 골룸을_생성한다("goalroom2", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3, goalRoomRoadmapNode4)), goalRoomPendingMember2);

        // when
        final Page<GoalRoom> goalRoomsPage = goalRoomRepository.findGoalRoomsWithPendingMembersPageByCond(
                GoalRoomFilterType.PARTICIPATION_RATE, PageRequest.of(0, 2));

        // then
        assertAll(
                () -> assertThat(goalRoomsPage.getTotalPages()).isEqualTo(1),
                () -> assertThat(goalRoomsPage.getContent()).hasSize(2),
                () -> assertThat(goalRoomsPage.getContent()).isEqualTo(List.of(goalRoom1, goalRoom2))
        );
    }

    private Member 크리에이터를_저장한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
                "010-1234-5678");
        final Member creator = new Member(new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), new Nickname("코끼리"), memberProfile);
        return memberRepository.save(creator);
    }

    private RoadmapCategory 카테고리를_저장한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private Member 사용자를_생성한다(final String nickname, final String phoneNumber, final String identifier,
                             final String password) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
                phoneNumber);
        final Member creator = new Member(new Identifier(identifier),
                new EncryptedPassword(new Password(password)), new Nickname(nickname), memberProfile);
        return memberRepository.save(creator);
    }

    private RoadmapNode 로드맵_노드를_생성한다(final String title, final String content) {
        return new RoadmapNode(title, content);
    }

    private RoadmapContent 로드맵_본문을_생성한다(final List<RoadmapNode> roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(new RoadmapNodes(roadmapNodes));
        return roadmapContent;
    }

    private Roadmap 로드맵을_생성한다(final Member creator, final RoadmapCategory category,
                              final RoadmapNodes roadmapNodes,
                              final RoadmapContent roadmapContent) {
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 30, RoadmapDifficulty.DIFFICULT,
                creator, category);
        roadmap.addContent(roadmapContent);
        roadmapRepository.save(roadmap);
        return roadmap;
    }

    private GoalRoomRoadmapNode 골룸_로드맵_노드를_생성한다(final LocalDate startDate, final LocalDate endDate,
                                                final RoadmapNode roadmapNode) {
        return new GoalRoomRoadmapNode(new Period(startDate, endDate), 1, roadmapNode);
    }

    private GoalRoom 골룸을_생성한다(final String name, final Integer limitedMemberCount, final RoadmapContent roadmapContent,
                              final GoalRoomRoadmapNodes goalRoomRoadmapNodes, final Member member) {
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName(name), new LimitedMemberCount(limitedMemberCount),
                roadmapContent, member);
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        goalRoomRepository.save(goalRoom);
        return goalRoom;
    }
}
