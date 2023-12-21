package co.kirikiri.todo.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.common.helper.RepositoryTest;
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
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNode;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNodes;
import co.kirikiri.goalroom.domain.GoalRoomRole;
import co.kirikiri.goalroom.domain.vo.GoalRoomName;
import co.kirikiri.goalroom.domain.vo.LimitedMemberCount;
import co.kirikiri.goalroom.domain.vo.Period;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.todo.domain.GoalRoomToDo;
import co.kirikiri.todo.domain.vo.GoalRoomTodoContent;
import co.kirikiri.todo.domain.vo.ToDoPeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

@RepositoryTest
class GoalRoomToDoRepositoryTest {

    private static final LocalDate TODAY = LocalDate.now();

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final GoalRoomToDoRepository goalRoomToDoRepository;

    public GoalRoomToDoRepositoryTest(final MemberRepository memberRepository,
                                      final RoadmapRepository roadmapRepository,
                                      final GoalRoomRepository goalRoomRepository,
                                      final GoalRoomMemberRepository goalRoomMemberRepository,
                                      final RoadmapCategoryRepository roadmapCategoryRepository,
                                      final GoalRoomToDoRepository goalRoomToDoRepository) {
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.goalRoomToDoRepository = goalRoomToDoRepository;
    }

    @Test
    void 골룸_아이디와_사용자_아이디로_골룸_투두_체크_현황을_반환한다() {
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
        final GoalRoom savedGoalRoom = goalRoomRepository.save(goalRoom);

        final LocalDate today = LocalDate.now();
        final LocalDate threeDaysAfter = today.plusDays(3);
        final GoalRoomToDo firstGoalRoomTodo = new GoalRoomToDo(null, savedGoalRoom.getId(),
                new GoalRoomTodoContent("투두1"), new ToDoPeriod(today, threeDaysAfter));
        final GoalRoomToDo secondGoalRoomTodo = new GoalRoomToDo(null, savedGoalRoom.getId(),
                new GoalRoomTodoContent("투두2"), new ToDoPeriod(today, threeDaysAfter));
        final GoalRoomToDo savedFirstGoalRoomToDo = goalRoomToDoRepository.save(firstGoalRoomTodo);
        final GoalRoomToDo savedSecondGoalRoomToDo = goalRoomToDoRepository.save(secondGoalRoomTodo);

        final GoalRoomMember goalRoomMember = new GoalRoomMember(
                GoalRoomRole.LEADER, LocalDateTime.now(), savedGoalRoom, creator.getId());
        final GoalRoomMember savedGoalRoomMember = goalRoomMemberRepository.save(goalRoomMember);

        // when
        final List<GoalRoomToDo> findGoalRoomToDos = goalRoomToDoRepository.findGoalRoomToDosByGoalRoomId(
                savedGoalRoom.getId());

        // then
        assertThat(findGoalRoomToDos)
                .usingRecursiveComparison()
                .isEqualTo(List.of(savedFirstGoalRoomToDo, savedSecondGoalRoomToDo));
    }

    private RoadmapCategory 카테고리를_저장한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private Member 사용자를_생성한다(final String nickname, final String phoneNumber, final String identifier,
                             final String password) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, "kirikiri1@email.com");
        final Member creator = new Member(new Identifier(identifier),
                new EncryptedPassword(new Password(password)), new Nickname(nickname), null, memberProfile);
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
                              final RoadmapContent roadmapContent) {
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 30, RoadmapDifficulty.DIFFICULT,
                creator, category);
        roadmap.addContent(roadmapContent);
        return roadmapRepository.save(roadmap);
    }

    private GoalRoomRoadmapNode 골룸_로드맵_노드를_생성한다(final LocalDate startDate, final LocalDate endDate,
                                                final RoadmapNode roadmapNode) {
        return new GoalRoomRoadmapNode(new Period(startDate, endDate), 1, roadmapNode.getId());
    }

    private GoalRoom 골룸을_생성한다(final String name, final Integer limitedMemberCount, final RoadmapContent roadmapContent,
                              final GoalRoomRoadmapNodes goalRoomRoadmapNodes, final Member member) {
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName(name), new LimitedMemberCount(limitedMemberCount),
                roadmapContent.getId(), member.getId());
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        return goalRoom;
    }
}
