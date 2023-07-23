package co.kirikiri.persistence.goalroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
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
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
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

    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final MemberRepository memberRepository;

    public GoalRoomRepositoryTest(final GoalRoomRepository goalRoomRepository,
                                  final RoadmapRepository roadmapRepository,
                                  final RoadmapCategoryRepository roadmapCategoryRepository,
                                  final MemberRepository memberRepository) {
        this.goalRoomRepository = goalRoomRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.memberRepository = memberRepository;
    }

    @Test
    void 사용자의_단일_골룸을_조회한다() {
        //given
        final Member creator = 사용자를_생성한다("황시진", "010-1234-5678",
                "identifier1", "password1!");
        final RoadmapCategory category = 카테고리를_생성한다("운동");
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(new RoadmapNodes(List.of(roadmapNode1, roadmapNode2)));
        로드맵을_생성한다(creator, category, new RoadmapNodes(List.of(roadmapNode1, roadmapNode2)),
                roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 8),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(LocalDate.of(2023, 7, 9),
                LocalDate.of(2023, 7, 16),
                roadmapNode2);
        final GoalRoom goalRoom = 골룸을_생성한다("goalroom1", 6, GoalRoomStatus.RECRUITING, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)),
                new GoalRoomPendingMember(creator, GoalRoomRole.LEADER));

        final GoalRoomToDo goalRoomTodo1 = 골룸_투두를_생성한다("첫번째 투두", LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 8));
        final GoalRoomToDo goalRoomTodo2 = 골룸_투두를_생성한다("두번째 투두", LocalDate.of(2023, 7, 9),
                LocalDate.of(2023, 7, 16));
        goalRoom.addGoalRoomTodo(goalRoomTodo1);
        goalRoom.addGoalRoomTodo(goalRoomTodo2);

        //when
        final GoalRoom findGoalRoom = goalRoomRepository.findByIdWithMember(1L, creator)
                .get();

        //then
        assertThat(findGoalRoom).isEqualTo(goalRoom);
    }

    @Test
    void 사용자의_골룸_목록을_조회한다() {
        //given
        final Member creator = 사용자를_생성한다("황시진", "010-1234-5678",
                "identifier1", "password1!");
        final RoadmapCategory category = 카테고리를_생성한다("운동");
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(new RoadmapNodes(List.of(roadmapNode1, roadmapNode2)));
        final Roadmap roadmap = 로드맵을_생성한다(creator, category, new RoadmapNodes(List.of(roadmapNode1, roadmapNode2)),
                roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 8),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(LocalDate.of(2023, 7, 9),
                LocalDate.of(2023, 7, 16),
                roadmapNode2);
        final GoalRoom goalRoom1 = 골룸을_생성한다("goalroom1", 6, GoalRoomStatus.RECRUITING, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)),
                new GoalRoomPendingMember(creator, GoalRoomRole.LEADER));

        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = 골룸_로드맵_노드를_생성한다(LocalDate.of(2023, 6, 30),
                LocalDate.of(2023, 7, 8),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = 골룸_로드맵_노드를_생성한다(LocalDate.of(2023, 7, 9),
                LocalDate.of(2023, 7, 30),
                roadmapNode2);
        final GoalRoom goalRoom2 = 골룸을_생성한다("goalroom2", 20, GoalRoomStatus.RUNNING, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3, goalRoomRoadmapNode4)),
                new GoalRoomPendingMember(creator, GoalRoomRole.LEADER));

        final GoalRoomRoadmapNode goalRoomRoadmapNode5 = 골룸_로드맵_노드를_생성한다(LocalDate.of(2023, 5, 15),
                LocalDate.of(2023, 5, 17),
                roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode6 = 골룸_로드맵_노드를_생성한다(LocalDate.of(2023, 3, 21),
                LocalDate.of(2023, 4, 30),
                roadmapNode2);
        final GoalRoom goalRoom3 = 골룸을_생성한다("goalroom3", 20, GoalRoomStatus.COMPLETED, roadmapContent,
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode5, goalRoomRoadmapNode6)),
                new GoalRoomPendingMember(creator, GoalRoomRole.LEADER));

        final Member member = 사용자를_생성한다("사용자", "010-1111-2222",
                "identifier2", "password2@");
        goalRoom2.addMember(new GoalRoomPendingMember(member, GoalRoomRole.FOLLOWER));
        goalRoom1.addMember(new GoalRoomPendingMember(member, GoalRoomRole.FOLLOWER));

        // when
        final Page<GoalRoom> creatorGoalRoomsPage = goalRoomRepository.findGoalRoomsPageByMember(
                creator, PageRequest.of(0, 10));

        final Page<GoalRoom> memberGoalRoomsPage = goalRoomRepository.findGoalRoomsPageByMember(
                member, PageRequest.of(0, 10));

        // then
        assertAll(
                () -> assertThat(creatorGoalRoomsPage.getTotalPages()).isEqualTo(1),
                () -> assertThat(creatorGoalRoomsPage.getContent()).hasSize(3),
                () -> assertThat(creatorGoalRoomsPage.getContent()).isEqualTo(List.of(goalRoom1, goalRoom2, goalRoom3)),
                () -> assertThat(memberGoalRoomsPage.getTotalPages()).isEqualTo(1),
                () -> assertThat(memberGoalRoomsPage.getContent()).hasSize(2),
                () -> assertThat(memberGoalRoomsPage.getContent()).isEqualTo(List.of(goalRoom1, goalRoom2))
        );
    }

    private Member 사용자를_생성한다(final String nickname, final String phoneNumber, final String identifier,
                             final String password) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
                new Nickname(nickname), phoneNumber);
        final Member creator = new Member(new Identifier(identifier),
                new EncryptedPassword(new Password(password)), memberProfile);
        return memberRepository.save(creator);
    }

    private RoadmapCategory 카테고리를_생성한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private Roadmap 로드맵을_생성한다(final Member creator, final RoadmapCategory category, final RoadmapNodes roadmapNodes,
                              final RoadmapContent roadmapContent) {
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 30, RoadmapDifficulty.DIFFICULT,
                creator, category);
        roadmap.addContent(roadmapContent);
        roadmapRepository.save(roadmap);
        return roadmap;
    }

    private RoadmapContent 로드맵_본문을_생성한다(final RoadmapNodes roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(roadmapNodes);
        return roadmapContent;
    }

    private GoalRoomRoadmapNode 골룸_로드맵_노드를_생성한다(final LocalDate startDate, final LocalDate endDate,
                                                final RoadmapNode roadmapNode) {
        return new GoalRoomRoadmapNode(startDate, endDate, roadmapNode);
    }

    private GoalRoomToDo 골룸_투두를_생성한다(final String content, final LocalDate startDate, final LocalDate endDate) {
        return new GoalRoomToDo(content, startDate, endDate);
    }

    private GoalRoom 골룸을_생성한다(final String name, final Integer limitedMemberCount,
                              final GoalRoomStatus status, final RoadmapContent roadmapContent,
                              final GoalRoomRoadmapNodes goalRoomRoadmapNodes, final GoalRoomPendingMember creator) {
        final GoalRoom goalRoom = new GoalRoom(name, new LimitedMemberCount(limitedMemberCount), roadmapContent,
                creator);
        goalRoom.addRoadmapNodesAll(goalRoomRoadmapNodes);
        goalRoomRepository.save(goalRoom);
        return goalRoom;
    }
}
