package co.kirikiri.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.ForbiddenException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.common.type.ImageContentType;
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
import co.kirikiri.todo.domain.GoalRoomToDo;
import co.kirikiri.todo.domain.GoalRoomToDoCheck;
import co.kirikiri.todo.domain.vo.GoalRoomTodoContent;
import co.kirikiri.todo.domain.vo.ToDoPeriod;
import co.kirikiri.todo.persistence.GoalRoomToDoCheckRepository;
import co.kirikiri.todo.persistence.GoalRoomToDoRepository;
import co.kirikiri.todo.service.dto.request.GoalRoomTodoRequest;
import co.kirikiri.todo.service.dto.response.GoalRoomToDoCheckResponse;
import co.kirikiri.todo.service.dto.response.GoalRoomTodoResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoalRoomToDoServiceTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LATER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    private static Member member;

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GoalRoomToDoRepository goalRoomToDoRepository;

    @Mock
    private GoalRoomToDoCheckRepository goalRoomToDoCheckRepository;

    @InjectMocks
    private GoalRoomToDoService goalRoomToDoService;

    @BeforeAll
    static void setUp() {
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final EncryptedPassword encryptedPassword = new EncryptedPassword(password);
        final Nickname nickname = new Nickname("nickname");
        final String email = "kirikiri@email.com";
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, email);
        member = new Member(identifier, encryptedPassword, nickname, null, memberProfile);
    }

    @Test
    void 정상적으로_골룸에_투두리스트를_추가한다() {
        //given
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);
        final GoalRoomToDo goalRoomTodo = new GoalRoomToDo(null, 1L, new GoalRoomTodoContent("goalRoomTodoContent"),
                new ToDoPeriod(TODAY, TEN_DAY_LATER));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomMemberRepository.findAllByGoalRoom(any()))
                .willReturn(List.of(new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                        creator.getId())));
        given(goalRoomToDoRepository.save(any()))
                .willReturn(goalRoomTodo);

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertDoesNotThrow(() -> goalRoomToDoService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest));
    }

    @Test
    void 골룸에_투두리스트_추가시_회원을_찾지_못할_경우_예외를_던진다() {
        //given
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomToDoService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_골룸을_찾지_못할_경우_예외를_던진다() {
        //given
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomToDoService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_종료된_골룸일_경우_예외를_던진다() {
        //given
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);
        goalRoom.complete();

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomToDoService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_리더가_아닐_경우_예외를_던진다() {
        //given
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomMemberRepository.findAllByGoalRoom(any()))
                .willReturn(List.of(new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                        creator.getId())));

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomToDoService.addGoalRoomTodo(1L, "identifier2", goalRoomTodoRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 투두리스트를_체크한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);
        final GoalRoomToDo goalRoomToDo = new GoalRoomToDo(1L, goalRoom.getId(), new GoalRoomTodoContent("투두 1"),
                new ToDoPeriod(TODAY, TODAY.plusDays(3)));
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());

        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomToDoRepository.findById(any()))
                .thenReturn(Optional.of(goalRoomToDo));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.of(goalRoomMember));
        when(goalRoomToDoCheckRepository.findByGoalRoomToDoAndGoalRoomMemberId(any(), any()))
                .thenReturn(Optional.empty());

        // when
        final GoalRoomToDoCheckResponse checkResponse = goalRoomToDoService.checkGoalRoomTodo(goalRoom.getId(),
                goalRoomToDo.getId(), "cokirikiri");

        // then
        assertThat(checkResponse)
                .isEqualTo(new GoalRoomToDoCheckResponse(true));
    }

    @Test
    void 투두리스트_체크시_체크_이력이_있으면_제거한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);
        final GoalRoomToDo goalRoomToDo = new GoalRoomToDo(1L, goalRoom.getId(), new GoalRoomTodoContent("투두 1"),
                new ToDoPeriod(TODAY, TODAY.plusDays(3)));

        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());
        final GoalRoomToDoCheck goalRoomToDoCheck = new GoalRoomToDoCheck(goalRoomMember.getId(), goalRoomToDo);

        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomToDoRepository.findById(any()))
                .thenReturn(Optional.of(goalRoomToDo));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.of(goalRoomMember));
        when(goalRoomToDoCheckRepository.findByGoalRoomToDoAndGoalRoomMemberId(any(), any()))
                .thenReturn(Optional.of(goalRoomToDoCheck));

        // when
        final GoalRoomToDoCheckResponse checkResponse = goalRoomToDoService.checkGoalRoomTodo(goalRoom.getId(),
                goalRoomToDo.getId(), "cokirikiri");

        // then
        assertThat(checkResponse)
                .isEqualTo(new GoalRoomToDoCheckResponse(false));
    }

    @Test
    void 투두리스트_체크시_골룸이_존재하지_않으면_예외가_발생한다() {
        // given
        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomToDoService.checkGoalRoomTodo(1L, 1L, "cokirikiri"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 골룸입니다. goalRoomId = 1");
    }

    @Test
    void 투두리스트_체크시_해당_투두가_존재하지_않으면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);

        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        // expected
        assertThatThrownBy(() -> goalRoomToDoService.checkGoalRoomTodo(1L, 2L, "cokirikiri"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 투두입니다. todoId = 2");
    }

    @Test
    void 투두리스트_체크시_골룸에_사용자가_없으면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);
        final GoalRoomToDo goalRoomToDo = new GoalRoomToDo(1L, goalRoom.getId(), new GoalRoomTodoContent("투두 1"),
                new ToDoPeriod(TODAY, TODAY.plusDays(3)));

        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomToDoRepository.findById(any()))
                .thenReturn(Optional.of(goalRoomToDo));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomToDoService.checkGoalRoomTodo(1L, 1L, "cokirikiri"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("골룸에 사용자가 존재하지 않습니다. goalRoomId = 1 memberIdentifier = cokirikiri");
    }

    @Test
    void 골룸의_전체_투두리스트를_조회한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);

        final GoalRoomToDo firstGoalRoomTodo = new GoalRoomToDo(1L, goalRoom.getId(), new GoalRoomTodoContent("투두 1"),
                new ToDoPeriod(TODAY, TEN_DAY_LATER));
        final GoalRoomToDo secondGoalRoomTodo = new GoalRoomToDo(2L, goalRoom.getId(), new GoalRoomTodoContent("투두 2"),
                new ToDoPeriod(TWENTY_DAY_LATER, THIRTY_DAY_LATER));

        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());
        when(goalRoomRepository.findById(1L))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomToDoRepository.findGoalRoomToDosByGoalRoomId(anyLong()))
                .thenReturn(List.of(firstGoalRoomTodo, secondGoalRoomTodo));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomIdAndMemberId(anyLong(), any()))
                .thenReturn(Optional.of(goalRoomMember));
        when(goalRoomToDoCheckRepository.findByGoalRoomIdAndGoalRoomMemberId(anyLong(), any()))
                .thenReturn(List.of(
                        new GoalRoomToDoCheck(goalRoomMember.getId(), firstGoalRoomTodo)
                ));

        // when
        final List<GoalRoomTodoResponse> responses = goalRoomToDoService.findAllGoalRoomTodo(1L, "identifier");
        final List<GoalRoomTodoResponse> expected = List.of(
                new GoalRoomTodoResponse(1L, "투두 1", TODAY, TEN_DAY_LATER, new GoalRoomToDoCheckResponse(true)),
                new GoalRoomTodoResponse(2L, "투두 2", TWENTY_DAY_LATER, THIRTY_DAY_LATER,
                        new GoalRoomToDoCheckResponse(false)));

        // then
        assertThat(responses)
                .isEqualTo(expected);
    }

    @Test
    void 골룸의_투두리스트_조회시_존재하지_않는_골룸이면_예외가_발생한다() {
        // given
        when(goalRoomRepository.findById(1L))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomToDoService.findAllGoalRoomTodo(1L, "identifier"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸의_투두리스트_조회시_골룸에_참여하지_않은_사용자면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);

        when(goalRoomRepository.findById(1L))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomIdAndMemberId(anyLong(), any()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomToDoService.findAllGoalRoomTodo(1L, "identifier"))
                .isInstanceOf(ForbiddenException.class);
    }

    private Member 사용자를_생성한다(final Long memberId, final String identifier, final String password, final String nickname,
                             final String email) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, email);

        return new Member(memberId, new Identifier(identifier), null, new EncryptedPassword(new Password(password)),
                new Nickname(nickname), null, memberProfile);
    }

    private Roadmap 로드맵을_생성한다(final Member creator) {
        final RoadmapCategory category = new RoadmapCategory("게임");
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);
        roadmap.addContent(roadmapContent);
        return roadmap;
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

    private GoalRoom 골룸을_생성한다(final Long goalRoomId, final Member creator, final RoadmapContent roadmapContent,
                              final Integer limitedMemberCount) {
        return new GoalRoom(goalRoomId, new GoalRoomName("골룸 이름"), new LimitedMemberCount(limitedMemberCount),
                roadmapContent.getId(), 골룸_로드맵_노드들을_생성한다(roadmapContent.getNodes()));
    }

    private GoalRoomRoadmapNodes 골룸_로드맵_노드들을_생성한다(final RoadmapNodes roadmapNodes) {
        return new GoalRoomRoadmapNodes(List.of(
                new GoalRoomRoadmapNode(new Period(TODAY, TEN_DAY_LATER), 5, roadmapNodes.getValues().get(0).getId()),
                new GoalRoomRoadmapNode(new Period(TEN_DAY_LATER.plusDays(1), TWENTY_DAY_LATER), 5,
                        roadmapNodes.getValues().get(1).getId()))
        );
    }
}
