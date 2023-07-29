package co.kirikiri.persistence.goalroom;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

@RepositoryTest
class CheckFeedRepositoryTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LAYER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    private final MemberRepository memberRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final CheckFeedRepository checkFeedRepository;

    public CheckFeedRepositoryTest(final MemberRepository memberRepository,
                                   final RoadmapCategoryRepository roadmapCategoryRepository,
                                   final RoadmapRepository roadmapRepository,
                                   final GoalRoomRepository goalRoomRepository,
                                   final CheckFeedRepository checkFeedRepository) {
        this.memberRepository = memberRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.checkFeedRepository = checkFeedRepository;
    }

    @Test
    void 사용자가_해당_골룸에서_오늘_올린_피드의_존재유무를_확인한다() {
        //given
        final Member creator = 사용자를_저장한다("cokiri", "코끼리");
        final RoadmapCategory category = 카테고리를_저장한다("여가");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final Member member = 사용자를_저장한다("participant", "참여자");
        final GoalRoom goalRoom = 골룸을_저장한다(targetRoadmapContent, member);

        final GoalRoomMember joinedMember = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(),
                goalRoom, member);
        goalRoom.addAllGoalRoomMembers(List.of(
                new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, creator), joinedMember));

        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);
        인증_피드를_저장한다(goalRoomRoadmapNode, joinedMember);

        //when
        final boolean isUpdateToday = checkFeedRepository.isMemberUploadCheckFeedToday(joinedMember,
                goalRoomRoadmapNode, LocalDate.now().atStartOfDay(), LocalDate.now().atStartOfDay().plusDays(1));

        final boolean isUpdateTomorrow = checkFeedRepository.isMemberUploadCheckFeedToday(joinedMember,
                goalRoomRoadmapNode, LocalDate.now().atStartOfDay().plusDays(1),
                LocalDate.now().atStartOfDay().plusDays(1));

        //then
        assertAll(
                () -> assertThat(isUpdateToday).isTrue(),
                () -> assertThat(isUpdateTomorrow).isFalse()
        );
    }

    @Test
    void 사용자가_현재_진행중인_노드에서_인증한_횟수를_확인한다() {
        //given
        final Member creator = 사용자를_저장한다("cokiri", "코끼리");
        final RoadmapCategory category = 카테고리를_저장한다("여가");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final Member member = 사용자를_저장한다("participant", "참여자");
        final GoalRoom goalRoom = 골룸을_저장한다(targetRoadmapContent, member);

        final GoalRoomMember joinedMember = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom,
                member);
        goalRoom.addAllGoalRoomMembers(List.of(
                new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, creator), joinedMember));

        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);
        인증_피드를_저장한다(goalRoomRoadmapNode, joinedMember);
        인증_피드를_저장한다(goalRoomRoadmapNode, joinedMember);
        인증_피드를_저장한다(goalRoomRoadmapNode, joinedMember);
        인증_피드를_저장한다(goalRoomRoadmapNode, joinedMember);

        //when
        final int checkCount = checkFeedRepository.findCountByGoalRoomMemberAndGoalRoomRoadmapNode(joinedMember,
                goalRoomRoadmapNode);

        //then
        assertThat(checkCount).isEqualTo(4);
    }

    private Member 사용자를_저장한다(final String identifier, final String nickname) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE,
                LocalDate.of(1990, 1, 1), "010-1234-5678");
        final Member creator = new Member(new Identifier(identifier), new EncryptedPassword(new Password("password1!")),
                new Nickname(nickname), memberProfile);
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

    private GoalRoom 골룸을_저장한다(final RoadmapContent roadmapContent, final Member member) {
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10),
                roadmapContent, member);
        final List<RoadmapNode> roadmapNodes = roadmapContent.getNodes().getValues();

        final RoadmapNode firstRoadmapNode = roadmapNodes.get(0);
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TODAY, TEN_DAY_LATER),
                10, firstRoadmapNode);

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TWENTY_DAY_LAYER, THIRTY_DAY_LATER),
                10, secondRoadmapNode);

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        return goalRoomRepository.save(goalRoom);
    }

    private CheckFeed 인증_피드를_저장한다(final GoalRoomRoadmapNode goalRoomRoadmapNode, final GoalRoomMember joinedMember) {
        return checkFeedRepository.save(new CheckFeed("src/test/resources/testImage",
                ImageContentType.JPEG, "image.jpeg", goalRoomRoadmapNode, joinedMember));
    }
}
