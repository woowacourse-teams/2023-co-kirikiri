package co.kirikiri.persistence.goalroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
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
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsFilterType;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@RepositoryTest
class GoalRoomRepositoryTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LAYER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public GoalRoomRepositoryTest(final MemberRepository memberRepository,
                                  final RoadmapRepository roadmapRepository,
                                  final GoalRoomRepository goalRoomRepository,
                                  final GoalRoomMemberRepository goalRoomMemberRepository,
                                  final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 골룸_아이디로_골룸_정보를_조회한다() {
        //given
        final Member creator = 사용자를_생성한다("name1", "01011111111", "identifier1", "password!1");
        final RoadmapCategory category = 카테고리를_저장한다("여가");
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = 로드맵_노드를_생성한다("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1, roadmapNode2));
        로드맵을_생성한다(creator, category, roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNode2);
        final Member goalRoomPendingMember1 = 사용자를_생성한다("name2", "01011112222", "identifier2", "password!2");
        final GoalRoom goalRoom = 골룸을_생성한다("goalroom1", 6, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)), goalRoomPendingMember1);
        goalRoomRepository.save(goalRoom);

        // when
        final GoalRoom findGoalRoom = goalRoomRepository.findByIdWithRoadmapContent(goalRoom.getId())
                .get();

        // then
        assertThat(findGoalRoom)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
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
        final Roadmap roadmap = 로드맵을_생성한다(creator, category, roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNode2);
        final Member goalRoomPendingMember1 = 사용자를_생성한다("name2", "01011112222", "identifier2", "password!2");
        final GoalRoom goalRoom1 = 골룸을_생성한다("goalroom1", 6, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)), goalRoomPendingMember1);
        goalRoomRepository.save(goalRoom1);

        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNode2);
        final Member goalRoomPendingMember2 = 사용자를_생성한다("name3", "01011113333", "identifier3", "password!3");
        final GoalRoom goalRoom2 = 골룸을_생성한다("goalroom2", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3, goalRoomRoadmapNode4)), goalRoomPendingMember2);
        goalRoomRepository.save(goalRoom2);

        // when
        final List<GoalRoom> goalRooms = goalRoomRepository.findGoalRoomsWithPendingMembersByRoadmapAndCond(roadmap,
                RoadmapGoalRoomsFilterType.LATEST, null, 10);

        assertThat(goalRooms).isEqualTo(List.of(goalRoom2, goalRoom1));
    }

    @Test
    void 골룸을_참여율_순으로_조회한다() {
        //given
        final Member creator = 사용자를_생성한다("name1", "01011111111", "identifier1", "password!1");
        final RoadmapCategory category = 카테고리를_저장한다("여가");
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = 로드맵_노드를_생성한다("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1, roadmapNode2));
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(roadmapNode1, roadmapNode2));
        final Roadmap roadmap = 로드맵을_생성한다(creator, category, roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNodes.getValues().get(0));
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNodes.getValues().get(1));
        final Member goalRoomPendingMember1 = 사용자를_생성한다("name2", "01011112222", "identifier2", "password!2");
        final GoalRoom goalRoom1 = 골룸을_생성한다("goalroom1", 6, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)), goalRoomPendingMember1);
        goalRoomRepository.save(goalRoom1);

        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNode2);
        final Member goalRoomPendingMember2 = 사용자를_생성한다("name3", "01011113333", "identifier3", "password!3");
        final GoalRoom goalRoom2 = 골룸을_생성한다("goalroom2", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3, goalRoomRoadmapNode4)), goalRoomPendingMember2);
        goalRoomRepository.save(goalRoom2);

        // when
        final List<GoalRoom> goalRoomsPage = goalRoomRepository.findGoalRoomsWithPendingMembersByRoadmapAndCond(roadmap,
                RoadmapGoalRoomsFilterType.PARTICIPATION_RATE, null, 10);

        // then
        assertThat(goalRoomsPage).isEqualTo(List.of(goalRoom1, goalRoom2));
    }

    @Test
    void 골룸의_노드의_시작날짜가_오늘인_골룸을_조회한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1));
        로드맵을_생성한다(creator, category, roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(10), TODAY.plusDays(20),
                roadmapNode1);

        final GoalRoom goalRoom1 = 골룸을_생성한다("goalroom1", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1)), creator);
        final GoalRoom goalRoom2 = 골룸을_생성한다("goalroom2", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode2)), creator);
        final GoalRoom goalRoom3 = 골룸을_생성한다("goalroom3", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3)), creator);

        final GoalRoom savedGoalRoom1 = goalRoomRepository.save(goalRoom1);
        final GoalRoom savedGoalRoom2 = goalRoomRepository.save(goalRoom2);
        goalRoomRepository.save(goalRoom3);

        // when
        final List<GoalRoom> findGoalRooms = goalRoomRepository.findAllByStartDateNow();

        // then
        assertThat(findGoalRooms)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(List.of(savedGoalRoom1, savedGoalRoom2));
    }

    @Test
    void 투두리스트와_함께_골룸을_조회한다() {
        final Member creator = 사용자를_생성한다("name1", "01011111111", "identifier1", "password!1");
        final RoadmapCategory category = 카테고리를_저장한다("여가");
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = 로드맵_노드를_생성한다("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1, roadmapNode2));
        로드맵을_생성한다(creator, category, roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNode2);
        final Member goalRoomPendingMember = 사용자를_생성한다("name2", "01011112222", "identifier2", "password!2");
        final GoalRoom goalRoom = 골룸을_생성한다("goalroom1", 6, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)), goalRoomPendingMember);

        final LocalDate today = LocalDate.now();
        final LocalDate threeDaysAfter = today.plusDays(3);
        goalRoom.addGoalRoomTodo(new GoalRoomToDo(
                new GoalRoomTodoContent("투두1"), new Period(today, threeDaysAfter)
        ));
        goalRoom.addGoalRoomTodo(new GoalRoomToDo(
                new GoalRoomTodoContent("투두2"), new Period(today, threeDaysAfter)
        ));
        goalRoomRepository.save(goalRoom);

        // when
        final GoalRoom findGoalRoom = goalRoomRepository.findByIdWithTodos(goalRoom.getId()).get();

        // then
        assertThat(findGoalRoom)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(goalRoom);
    }

    @Test
    void 골룸_아이디와_사용자_아이디로_골룸_멤버를_조회한다() {
        // given
        final Member creator = 사용자를_생성한다("name1", "01011111111", "identifier1", "password!1");
        final RoadmapCategory category = 카테고리를_저장한다("여가");
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = 로드맵_노드를_생성한다("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1, roadmapNode2));
        로드맵을_생성한다(creator, category, roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNode2);
        final Member goalRoomPendingMember = 사용자를_생성한다("name2", "01011112222", "identifier2", "password!2");
        final GoalRoom goalRoom = 골룸을_생성한다("goalroom1", 6, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)), goalRoomPendingMember);
        goalRoomRepository.save(goalRoom);

        final GoalRoomMember goalRoomMember = new GoalRoomMember(
                GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, creator
        );
        goalRoomMemberRepository.save(goalRoomMember);

        // when
        final GoalRoomMember findGoalRoomMember = goalRoomRepository.findGoalRoomMember(goalRoom.getId(),
                creator.getIdentifier()).get();

        // then
        Assertions.assertThat(findGoalRoomMember)
                .usingRecursiveComparison()
                .ignoringFields("joinedAt")
                .isEqualTo(goalRoomMember);
    }

    @Test
    void 골룸_아이디로_골룸과_로드맵컨텐츠_골룸노드_투두_정보를_조회한다() {
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final RoadmapNode roadmapNode = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode));
        로드맵을_생성한다(creator, category, roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode);

        final GoalRoom goalRoom1 = 골룸을_생성한다("goalroom1", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1)), creator);
        final GoalRoomToDo goalRoomToDo1 = new GoalRoomToDo(new GoalRoomTodoContent("할 일 목록"),
                new Period(TODAY, TEN_DAY_LATER));
        goalRoom1.addGoalRoomTodo(goalRoomToDo1);
        final GoalRoom savedGoalRoom1 = goalRoomRepository.save(goalRoom1);

        final GoalRoom goalRoom2 = 골룸을_생성한다("goalroom2", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode2)), creator);
        final GoalRoomToDo goalRoomToDo2 = new GoalRoomToDo(new GoalRoomTodoContent("우리만의 투두"),
                new Period(TEN_DAY_LATER, TWENTY_DAY_LAYER));
        goalRoom2.addGoalRoomTodo(goalRoomToDo2);
        final GoalRoom savedGoalRoom2 = goalRoomRepository.save(goalRoom2);

        // when
        final GoalRoom findGoalRoom1 = goalRoomRepository.findByIdWithContentAndTodos(goalRoom1.getId())
                .get();
        final GoalRoom findGoalRoom2 = goalRoomRepository.findByIdWithContentAndTodos(goalRoom2.getId())
                .get();

        //then
        assertAll(
                () -> assertThat(findGoalRoom1)
                        .usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(savedGoalRoom1),
                () -> assertThat(findGoalRoom2)
                        .usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(savedGoalRoom2)
        );
    }

    @Test
    void 사용자가_참가한_모든_골룸들을_조회한다() {
        //given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1));
        로드맵을_생성한다(creator, category, roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(10), TODAY.plusDays(20),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(10), TODAY.plusDays(20),
                roadmapNode1);

        final GoalRoom goalRoom1 = 골룸을_생성한다("goalroom1", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1)), creator);
        final GoalRoom goalRoom2 = 골룸을_생성한다("goalroom2", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode2)), creator);
        final GoalRoom goalRoom3 = 골룸을_생성한다("goalroom3", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3)), creator);
        final GoalRoom goalRoom4 = 골룸을_생성한다("goalroom3", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3)), creator);

        final Member member = 사용자를_생성한다("팔로워", "010-111-1111", "identifier2", "password2@");
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
        assertAll(
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
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1));
        로드맵을_생성한다(creator, category, roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(10), TODAY.plusDays(20),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(10), TODAY.plusDays(20),
                roadmapNode1);

        final GoalRoom goalRoom1 = 골룸을_생성한다("goalroom1", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1)), creator);
        final GoalRoom goalRoom2 = 골룸을_생성한다("goalroom2", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode2)), creator);
        final GoalRoom goalRoom3 = 골룸을_생성한다("goalroom3", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3)), creator);
        final GoalRoom goalRoom4 = 골룸을_생성한다("goalroom3", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3)), creator);

        final Member member = 사용자를_생성한다("팔로워", "010-111-1111", "identifier2", "password2@");
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

    @Test
    void 노드와_함께_골룸을_조회한다() {
        final Member creator = 사용자를_생성한다("name1", "01011111111", "identifier1", "password!1");
        final RoadmapCategory category = 카테고리를_저장한다("여가");
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = 로드맵_노드를_생성한다("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1, roadmapNode2));
        로드맵을_생성한다(creator, category, roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TODAY.plusDays(10),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TODAY.plusDays(11), TODAY.plusDays(20),
                roadmapNode2);
        final Member goalRoomPendingMember = 사용자를_생성한다("name2", "01011112222", "identifier2", "password!2");
        final GoalRoom goalRoom = 골룸을_생성한다("goalroom1", 6, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)), goalRoomPendingMember);
        goalRoomRepository.save(goalRoom);

        // when
        final GoalRoom findGoalRoom = goalRoomRepository.findByIdWithNodes(goalRoom.getId()).get();

        // then
        assertThat(findGoalRoom)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(goalRoom);
    }

    @Test
    void 로드맵에_생성된_모든_골룸을_조회한다() {
        //given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final RoadmapNode roadmapNode = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode));
        final Roadmap roadmap = 로드맵을_생성한다(creator, category, roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(TODAY, TEN_DAY_LATER,
                roadmapNode);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(TEN_DAY_LATER, TWENTY_DAY_LAYER,
                roadmapNode);

        final GoalRoom goalRoom1 = 골룸을_생성한다("goalroom1", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1)), creator);
        final GoalRoom goalRoom2 = 골룸을_생성한다("goalroom2", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode2)), creator);
        final GoalRoom goalRoom3 = 골룸을_생성한다("goalroom3", 20, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1)), creator);
        goalRoomRepository.save(goalRoom1);
        goalRoomRepository.save(goalRoom2);
        goalRoomRepository.save(goalRoom3);

        // when
        final List<GoalRoom> goalRooms = goalRoomRepository.findByRoadmap(roadmap);

        // then
        assertThat(goalRooms)
                .isEqualTo(List.of(goalRoom1, goalRoom2, goalRoom3));
    }

    private Member 크리에이터를_저장한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE,
                LocalDate.of(1990, 1, 1), "010-1234-5678");
        final Member creator = new Member(new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), new Nickname("코끼리"), memberProfile);
        return memberRepository.save(creator);
    }

    private Member 사용자를_생성한다(final String nickname, final String phoneNumber, final String identifier,
                             final String password) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
                phoneNumber);
        final Member creator = new Member(new Identifier(identifier),
                new EncryptedPassword(new Password(password)), new Nickname(nickname), memberProfile);
        return memberRepository.save(creator);
    }

    private RoadmapCategory 카테고리를_저장한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
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
                              final RoadmapContent roadmapContent) {
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 30, RoadmapDifficulty.DIFFICULT,
                creator, category);
        roadmap.addContent(roadmapContent);
        return roadmapRepository.save(roadmap);
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
        return goalRoom;
    }
}
