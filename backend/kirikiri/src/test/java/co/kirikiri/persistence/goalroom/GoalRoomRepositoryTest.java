package co.kirikiri.persistence.goalroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.GoalRoomTodoContent;
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
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom savedGoalRoom = goalRoomRepository.save(goalRoom);

        // when
        final GoalRoom findGoalRoom = goalRoomRepository.findByIdWithRoadmapContent(savedGoalRoom.getId()).get();

        // then
        assertThat(findGoalRoom)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(goalRoom);
    }

    @Test
    void 골룸_아이디로_골룸과_로드맵컨텐츠_골룸노드_투두_정보를_조회한다() {
        final Member creator = 크리에이터를_저장한다();
        final Member member = 사용자를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom1 = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoomToDo goalRoomToDo1 = new GoalRoomToDo(new GoalRoomTodoContent("할 일 목록"),
                new Period(TODAY, TEN_DAY_LATER));
        goalRoom1.addGoalRoomTodo(goalRoomToDo1);
        final GoalRoom savedGoalRoom1 = goalRoomRepository.save(goalRoom1);

        final GoalRoom goalRoom2 = 골룸을_생성한다(targetRoadmapContent, member);
        final GoalRoomToDo goalRoomToDo2 = new GoalRoomToDo(new GoalRoomTodoContent("우리만의 투두"),
                new Period(TEN_DAY_LATER, TWENTY_DAY_LAYER));
        goalRoom2.addGoalRoomTodo(goalRoomToDo2);
        final GoalRoom savedGoalRoom2 = goalRoomRepository.save(goalRoom2);

        // when
        final GoalRoom findGoalRoom1 = goalRoomRepository.findByIdWithContentAndNodesAndTodos(savedGoalRoom1.getId())
                .get();
        final GoalRoom findGoalRoom2 = goalRoomRepository.findByIdWithContentAndNodesAndTodos(savedGoalRoom2.getId())
                .get();

        //then
        assertAll(
                () -> assertThat(findGoalRoom1)
                        .usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(goalRoom1),
                () -> assertThat(findGoalRoom2)
                        .usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(goalRoom2)
        );
    }

    @Test
    void 사용자가_참가한_모든_골룸들을_조회한다() {
        //given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom1 = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom goalRoom2 = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom goalRoom3 = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom goalRoom4 = 골룸을_생성한다(targetRoadmapContent, creator);

        final Member member = 사용자를_저장한다();
        goalRoom1.join(member);
        goalRoom2.join(member);
        goalRoom3.join(member);
        goalRoom2.updateStatus(GoalRoomStatus.RUNNING);
        goalRoom3.updateStatus(GoalRoomStatus.COMPLETED);

        goalRoomRepository.save(goalRoom1);
        goalRoomRepository.save(goalRoom2);
        goalRoomRepository.save(goalRoom3);
        goalRoomRepository.save(goalRoom4);

        //when
        final List<GoalRoom> creatorMemberGoalRooms = goalRoomRepository.findByMember(creator);
        final List<GoalRoom> followerMemberGoalRooms = goalRoomRepository.findByMember(member);

        //then
        Assertions.assertAll(
                () -> assertThat(creatorMemberGoalRooms)
                        .usingRecursiveComparison()
                        .isEqualTo(List.of(goalRoom1, goalRoom2, goalRoom3, goalRoom4)),
                () -> assertThat(followerMemberGoalRooms)
                        .usingRecursiveComparison()
                        .isEqualTo(List.of(goalRoom1, goalRoom2, goalRoom3))
        );
    }

    @Test
    void 사용자가_참가한_골룸들을_상태에_따라_조회한다() {
        //given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom1 = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom goalRoom2 = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom goalRoom3 = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom goalRoom4 = 골룸을_생성한다(targetRoadmapContent, creator);

        final Member member = 사용자를_저장한다();
        goalRoom1.join(member);
        goalRoom2.join(member);
        goalRoom3.join(member);
        goalRoom4.join(member);
        goalRoom2.updateStatus(GoalRoomStatus.RUNNING);
        goalRoom3.updateStatus(GoalRoomStatus.COMPLETED);

        goalRoomRepository.save(goalRoom1);
        goalRoomRepository.save(goalRoom2);
        goalRoomRepository.save(goalRoom3);
        goalRoomRepository.save(goalRoom4);

        //when
        final List<GoalRoom> memberRecruitingGoalRooms = goalRoomRepository.findByMemberAndStatus(member,
                GoalRoomStatus.RECRUITING);
        final List<GoalRoom> memberRunningGoalRooms = goalRoomRepository.findByMemberAndStatus(member,
                GoalRoomStatus.RUNNING);
        final List<GoalRoom> memberCompletedGoalRooms = goalRoomRepository.findByMemberAndStatus(member,
                GoalRoomStatus.COMPLETED);

        //then
        assertAll(
                () -> assertThat(memberRecruitingGoalRooms)
                        .usingRecursiveComparison()
                        .isEqualTo(List.of(goalRoom1, goalRoom4)),
                () -> assertThat(memberRunningGoalRooms)
                        .usingRecursiveComparison()
                        .isEqualTo(List.of(goalRoom2)),
                () -> assertThat(memberCompletedGoalRooms)
                        .usingRecursiveComparison()
                        .isEqualTo(List.of(goalRoom3))
        );
    }

    private Member 크리에이터를_저장한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE,
                LocalDate.of(1990, 1, 1), "010-1234-5678");
        final Member creator = new Member(new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), new Nickname("코끼리"), memberProfile);
        return memberRepository.save(creator);
    }

    private Member 사용자를_저장한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE,
                LocalDate.of(1990, 1, 1), "010-1234-5678");
        final Member creator = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1!")), new Nickname("참여자"), memberProfile);
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

    private GoalRoom 골룸을_생성한다(final RoadmapContent roadmapContent, final Member member) {
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
        return goalRoom;
    }
}
