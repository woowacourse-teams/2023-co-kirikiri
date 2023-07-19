package co.kirikiri.persistence.goalroom;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapContents;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImage;
import co.kirikiri.domain.roadmap.RoadmapNodeImages;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

@RepositoryTest
class GoalRoomPendingMemberRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    public GoalRoomPendingMemberRepositoryTest(final EntityManager entityManager,
                                               final MemberRepository memberRepository,
                                               final RoadmapRepository roadmapRepository,
                                               final GoalRoomRepository goalRoomRepository,
                                               final RoadmapCategoryRepository roadmapCategoryRepository,
                                               final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository) {
        this.entityManager = entityManager;
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.goalRoomPendingMemberRepository = goalRoomPendingMemberRepository;
    }

    @Test
    void 골룸과_사용자_아이디로_골룸_사용자_대기_목록을_조회한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent);
        final GoalRoom savedGoalRoom = goalRoomRepository.save(goalRoom);

        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), savedGoalRoom, creator);
        final GoalRoomPendingMember expected = goalRoomPendingMemberRepository.save(goalRoomPendingMember);

        // when
        final Optional<GoalRoomPendingMember> findGoalRoomPendingMember = goalRoomPendingMemberRepository.findByGoalRoomAndMemberIdentifier(
                new Identifier("cokirikiri"), savedGoalRoom);

        // then
        assertThat(findGoalRoomPendingMember.get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    void 골룸과_사용자_아이디로_골룸_사용자_대기_목록_조회시_없으면_빈값을_반환한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent);
        final GoalRoom savedGoalRoom = goalRoomRepository.save(goalRoom);

        // when
        final Optional<GoalRoomPendingMember> findGoalRoomPendingMember = goalRoomPendingMemberRepository.findByGoalRoomAndMemberIdentifier(
                new Identifier("cokirikiri2"), savedGoalRoom);

        // then
        assertThat(findGoalRoomPendingMember)
                .isEmpty();

    }

    private Member 크리에이터를_저장한다() {
        final MemberProfileImage memberProfileImage = new MemberProfileImage("member-profile.png",
                "member-profile-save-path", ImageContentType.PNG);
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
                new Nickname("코끼리"), "010-1234-5678");
        memberProfile.updateMemberProfileImage(memberProfileImage);

        final Member creator = new Member(new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), memberProfile);
        return memberRepository.save(creator);
    }

    private RoadmapCategory 카테고리를_저장한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private Roadmap 로드맵을_저장한다(final Member creator, final RoadmapCategory category) {
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);
        roadmap.addContent(roadmapContent);
        return roadmapRepository.save(roadmap);
    }

    private List<RoadmapNode> 로드맵_노드들을_생성한다() {
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        roadmapNode1.addImages(new RoadmapNodeImages(노드_이미지들을_생성한다()));
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        return List.of(roadmapNode1, roadmapNode2);
    }

    private RoadmapContent 로드맵_본문을_생성한다(final List<RoadmapNode> roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(new RoadmapNodes(roadmapNodes));
        return roadmapContent;
    }

    private List<RoadmapNodeImage> 노드_이미지들을_생성한다() {
        return List.of(
                new RoadmapNodeImage("node-image1.png", "node-image1-save-path", ImageContentType.PNG),
                new RoadmapNodeImage("node-image2.png", "node-image2-save-path", ImageContentType.PNG)
        );
    }

    private GoalRoom 골룸을_생성한다(final RoadmapContent roadmapContent) {
        final GoalRoom goalRoom = new GoalRoom("골룸", 10, 5, GoalRoomStatus.RECRUITING, roadmapContent);
        final List<RoadmapNode> roadmapNodes = roadmapContent.getNodes().getValues();

        final RoadmapNode firstRoadmapNode = roadmapNodes.get(0);
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                LocalDate.of(2023, 7, 19),
                LocalDate.of(2023, 7, 30), 10, firstRoadmapNode);

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                LocalDate.of(2023, 8, 1),
                LocalDate.of(2023, 8, 5), 10, secondRoadmapNode);

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));
        goalRoom.addGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        return goalRoom;
    }
}