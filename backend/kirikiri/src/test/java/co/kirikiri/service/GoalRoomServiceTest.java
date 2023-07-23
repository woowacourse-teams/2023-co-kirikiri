package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

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
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class GoalRoomServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @InjectMocks
    private GoalRoomService goalRoomService;

    @Test
    void 사용자_단일_골룸을_조회한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "크리에이터", "identifier1");
        final RoadmapCategory category = new RoadmapCategory("운동");
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(roadmapNode1, roadmapNode2));
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(roadmapNodes);
        final Roadmap roadmap = new Roadmap(1L, "로드맵 제목", "로드맵 소개", 20, RoadmapDifficulty.NORMAL,
                RoadmapStatus.CREATED, creator, category);
        roadmap.addContent(roadmapContent);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = new GoalRoomRoadmapNode(LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10), roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = new GoalRoomRoadmapNode(LocalDate.of(2023, 7, 11),
                LocalDate.of(2023, 7, 20), roadmapNode2);
        final GoalRoomToDo goalRoomTodo1 = new GoalRoomToDo("첫번째 투두", LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10));
        final GoalRoomToDo goalRoomTodo2 = new GoalRoomToDo("두번째 투두", LocalDate.of(2023, 7, 11),
                LocalDate.of(2023, 7, 20));

        final GoalRoom goalRoom = new GoalRoom(1L, "goalroom1", new LimitedMemberCount(10),
                roadmapContent, new GoalRoomPendingMember(creator, GoalRoomRole.LEADER));
        goalRoom.addRoadmapNodesAll(new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)));
        goalRoom.addGoalRoomTodo(goalRoomTodo1);
        goalRoom.addGoalRoomTodo(goalRoomTodo2);
        goalRoom.updateStatus(GoalRoomStatus.RECRUITING);

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findByIdWithMember(anyLong(), any()))
                .willReturn(Optional.of(goalRoom));
        given(roadmapRepository.findByGoalRoomId(anyLong()))
                .willReturn(Optional.of(roadmap));

        //when
        final GoalRoomResponse response = goalRoomService.findMemberGoalRoom(1L, 1L);

        final GoalRoomResponse expected = new GoalRoomResponse("goalroom1", "로드맵 제목", GoalRoomStatus.RECRUITING.name(),
                List.of(new GoalRoomNodeResponse("로드맵 1주차", LocalDate.of(2023, 7, 1),
                                LocalDate.of(2023, 7, 10), 0),
                        new GoalRoomNodeResponse("로드맵 2주차", LocalDate.of(2023, 7, 11),
                                LocalDate.of(2023, 7, 20), 0)),
                List.of(new GoalRoomTodoResponse("첫번째 투두", LocalDate.of(2023, 7, 1),
                                LocalDate.of(2023, 7, 10)),
                        new GoalRoomTodoResponse("두번째 투두", LocalDate.of(2023, 7, 11),
                                LocalDate.of(2023, 7, 20))),
                20, null);

        //then
        assertThat(response).isEqualTo(expected);
    }

    @Test
    void 사용자의_골룸_목록을_조회한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "크리에이터", "identifier1");
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(roadmapNode1, roadmapNode2));
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(roadmapNodes);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = new GoalRoomRoadmapNode(LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10), roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = new GoalRoomRoadmapNode(LocalDate.of(2023, 7, 11),
                LocalDate.of(2023, 7, 20), roadmapNode2);
        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = new GoalRoomRoadmapNode(LocalDate.of(2023, 8, 1),
                LocalDate.of(2023, 8, 10), roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = new GoalRoomRoadmapNode(LocalDate.of(2023, 8, 11),
                LocalDate.of(2023, 8, 20), roadmapNode2);

        final GoalRoom goalRoom1 = new GoalRoom(1L, "goalroom1", new LimitedMemberCount(10),
                roadmapContent, new GoalRoomPendingMember(creator, GoalRoomRole.LEADER));
        goalRoom1.addRoadmapNodesAll(new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)));
        goalRoom1.updateStatus(GoalRoomStatus.RECRUITING);

        final GoalRoom goalRoom2 = new GoalRoom(2L, "goalroom2", new LimitedMemberCount(20),
                roadmapContent, new GoalRoomPendingMember(creator, GoalRoomRole.LEADER));
        goalRoom2.addRoadmapNodesAll(new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3, goalRoomRoadmapNode4)));
        goalRoom2.updateStatus(GoalRoomStatus.RUNNING);

        final PageImpl<GoalRoom> goalRoomsPage = new PageImpl<>(List.of(goalRoom1, goalRoom2),
                PageRequest.of(0, 10), 1);

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findGoalRoomsPageByMember(any(), any()))
                .willReturn(goalRoomsPage);

        // when
        final PageResponse<GoalRoomForListResponse> result = goalRoomService.findMemberGoalRooms(
                creator.getId(), new CustomPageRequest(1, 10));

        final PageResponse<GoalRoomForListResponse> expected = new PageResponse<>(1, 1,
                List.of(
                        new GoalRoomForListResponse(1L, "goalroom1", 1, 10, LocalDateTime.now(),
                                LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 20),
                                new MemberResponse(creator.getId(), creator.getNickname().getValue()),
                                GoalRoomStatus.RECRUITING.name()),
                        new GoalRoomForListResponse(2L, "goalroom2", 1, 20, LocalDateTime.now(),
                                LocalDate.of(2023, 8, 1), LocalDate.of(2023, 8, 20),
                                new MemberResponse(creator.getId(), creator.getNickname().getValue()),
                                GoalRoomStatus.RUNNING.name())
                )
        );

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("data.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 잘못된_사용자_아이디가_들어오면_예외가_발생한다() {
        //given
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> goalRoomService.findMemberGoalRooms(1L, new CustomPageRequest(1, 10)))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    private Member 사용자를_생성한다(final Long id, final String nickname, final String identifier) {
        return new Member(id, new Identifier(identifier),
                new EncryptedPassword(new Password("password1")),
                new MemberProfile(Gender.FEMALE, LocalDate.of(2000, 7, 20), new Nickname(nickname),
                        "010-1111-1111"));
    }
}
