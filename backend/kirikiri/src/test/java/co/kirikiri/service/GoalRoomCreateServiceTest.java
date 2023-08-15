package co.kirikiri.service;

import static co.kirikiri.domain.goalroom.GoalRoomStatus.RECRUITING;
import static co.kirikiri.domain.goalroom.GoalRoomStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
import co.kirikiri.domain.goalroom.GoalRoomToDoCheck;
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
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.CheckFeedRepository;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.GoalRoomToDoCheckRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class GoalRoomCreateServiceTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LATER = TODAY.plusDays(20);

    private static final RoadmapNode ROADMAP_NODE = new RoadmapNode(1L, "title", "content");
    private static final RoadmapContent ROADMAP_CONTENT = new RoadmapContent(1L, "content");
    private static final RoadmapNodes ROADMAP_CONTENTS = new RoadmapNodes(new ArrayList<>(List.of(ROADMAP_NODE)));

    private static final Member GOAL_ROOM_MEMBER1 = new Member(new Identifier("identifier2"),
            new EncryptedPassword(new Password("password!2")),
            new Nickname("name2"), null,
            new MemberProfile(Gender.FEMALE, LocalDate.now(), "010-1111-2222"));

    private static Member member;

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @Mock
    private GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    @Mock
    private RoadmapContentRepository roadmapContentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GoalRoomToDoCheckRepository goalRoomToDoCheckRepository;

    @Mock
    private CheckFeedRepository checkFeedRepository;

    @Mock
    private FileService fileService;

    @Mock
    private FilePathGenerator filePathGenerator;

    @InjectMocks
    private GoalRoomCreateService goalRoomCreateService;

    @BeforeAll
    static void setUp() {
        ROADMAP_CONTENT.addNodes(ROADMAP_CONTENTS);
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final EncryptedPassword encryptedPassword = new EncryptedPassword(password);
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, TODAY, phoneNumber);
        member = new Member(identifier, encryptedPassword, nickname, null, memberProfile);
    }

    @Test
    void 정상적으로_골룸을_생성한다() {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));

        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(ROADMAP_CONTENT));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.save(any()))
                .willReturn(new GoalRoom(1L, null, null, null, null));

        //when
        assertDoesNotThrow(() -> goalRoomCreateService.create(request, member.getIdentifier().getValue()));
    }

    @Test
    void 골룸_생성_시_존재하지_않은_로드맵_컨텐츠가_들어올때_예외를_던진다() {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));

        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.create(request, member.getIdentifier().getValue()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸_생성_시_로드맵_컨텐츠의_노드사이즈와_요청의_노드사이즈가_다를때_예외를_던진다() {
        //given
        final List<GoalRoomRoadmapNodeRequest> wrongSizeGoalRoomRoadmapNodeRequest = new ArrayList<>(List.of(
                new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER),
                new GoalRoomRoadmapNodeRequest(2L, 10, TODAY, TEN_DAY_LATER)));
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                wrongSizeGoalRoomRoadmapNodeRequest);

        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(ROADMAP_CONTENT));

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.create(request, member.getIdentifier().getValue()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸_생성_시_로드맵에_존재하지_않는_노드가_요청으로_들어올때_예외를_던진다() {
        //given
        final long wrongRoadmapNodId = 2L;
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(wrongRoadmapNodId, 10, TODAY, TEN_DAY_LATER))));

        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(ROADMAP_CONTENT));

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.create(request, member.getIdentifier().getValue()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸_생성_시_존재하지_않은_회원의_Identifier가_들어올때_예외를_던진다() {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new GoalRoomTodoRequest("content", TODAY, TEN_DAY_LATER),
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));

        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(ROADMAP_CONTENT));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.create(request, member.getIdentifier().getValue()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸에_참가한다() {
        //given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "시진이", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);
        final Member follower = 사용자를_생성한다(2L, "identifier2", "password1!", "팔로워", "010-1234-5678");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        //when
        goalRoomCreateService.join("identifier2", 1L);

        //then
        assertThat(goalRoom.getCurrentMemberCount())
                .isEqualTo(2);
    }

    @Test
    void 골룸_참가_요청시_유효한_사용자_아이디가_아니면_예외가_발생한다() {
        //given
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> goalRoomCreateService.join("identifier2", 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 골룸_참가_요청시_유효한_골룸_아이디가_아니면_예외가_발생한다() {
        //given
        final Member follower = 사용자를_생성한다(1L, "identifier1", "password1!", "팔로워", "010-1234-5678");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> goalRoomCreateService.join("identifier1", 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 골룸입니다. goalRoomId = 1");
    }

    @Test
    void 골룸_참가_요청시_제한_인원이_가득_찼을_경우_예외가_발생한다() {
        //given
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 1;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);
        final Member follower = 사용자를_생성한다(1L, "identifier2", "password1!", "팔로워", "010-1234-5678");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        //when, then
        assertThatThrownBy(() -> goalRoomCreateService.join("identifier2", 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("제한 인원이 꽉 찬 골룸에는 참여할 수 없습니다.");
    }

    @Test
    void 골룸_참가_요청시_모집_중이_아닌_경우_예외가_발생한다() {
        //given
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "010-1111-1111");
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, roadmapContent, limitedMemberCount);
        final Member follower = 사용자를_생성한다(2L, "identifier2", "password2!", "팔로워", "010-1111-2222");
        goalRoom.start();

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        //when, then
        assertThatThrownBy(() -> goalRoomCreateService.join("identifier2", 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("모집 중이지 않은 골룸에는 참여할 수 없습니다.");
    }

    @Test
    void 정상적으로_골룸에_투두리스트를_추가한다() {
        //given
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);

        goalRoom.addGoalRoomTodo(
                new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertDoesNotThrow(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest));
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
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_골룸을_찾지_못할_경우_예외를_던진다() {
        //given
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);

        goalRoom.addGoalRoomTodo(
                new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_종료된_골룸일_경우_예외를_던진다() {
        //given
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);

        goalRoom.addGoalRoomTodo(
                new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));
        goalRoom.complete();

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_리더가_아닐_경우_예외를_던진다() {
        //given
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);

        goalRoom.addGoalRoomTodo(
                new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier2", goalRoomTodoRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_골룸_컨텐츠가_250글자가_넘을때_예외를_던진다() {
        //given
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);

        goalRoom.addGoalRoomTodo(
                new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final String goalRoomTodoContent = "a".repeat(251);
        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest(goalRoomTodoContent, TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸을_시작한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));

        // when
        goalRoomCreateService.startGoalRoom("cokirikiri", 1L);

        // then
        verify(goalRoomMemberRepository, times(1)).saveAll(anyList());
        verify(goalRoomPendingMemberRepository, times(1)).deleteAll(anyList());
        assertThat(goalRoom.getStatus()).isEqualTo(RUNNING);
    }

    @Test
    void 골룸_시작시_존재하지_않는_사용자면_예외가_발생한다() {
        // given
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomCreateService.startGoalRoom("identifier", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸_시작시_존재하지_않는_골룸이면_예외가_발생한다() {
        // given
        final Member member = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(member));
        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomCreateService.startGoalRoom("identifier", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸을_시작하는_사용자가_골룸의_리더가_아니면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Member follower = 사용자를_생성한다(2L, "kirikirico", "password2!", "끼리코", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));

        // expected
        assertThatThrownBy(() -> goalRoomCreateService.startGoalRoom("identifier", 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸_시작시_시작날짜가_아직_지나지_않았으면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 시작_날짜가_미래인_골룸을_생성한다(1L, creator, targetRoadmapContent, 10);

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));

        // expected
        assertThatThrownBy(() -> goalRoomCreateService.startGoalRoom("cokirikiri", 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸의_시작날짜가_되면_골룸의_상태가_진행중으로_변경된다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);
        final GoalRoom goalRoom2 = 골룸을_생성한다(2L, creator, targetRoadmapContent, 10);

        final Member follower1 = 사용자를_생성한다(2L, "identifier1", "password2!", "name1", "010-1111-1111");
        final Member follower2 = 사용자를_생성한다(3L, "identifier2", "password3!", "name2", "010-1111-1112");
        final Member follower3 = 사용자를_생성한다(4L, "identifier3", "password4!", "name3", "010-1111-1113");

        final GoalRoomPendingMember goalRoomPendingMember = 골룸_대기자를_생성한다(goalRoom2, creator, GoalRoomRole.FOLLOWER);
        final GoalRoomPendingMember goalRoomPendingMember1 = 골룸_대기자를_생성한다(goalRoom1, follower1, GoalRoomRole.FOLLOWER);
        final GoalRoomPendingMember goalRoomPendingMember2 = 골룸_대기자를_생성한다(goalRoom1, follower2, GoalRoomRole.FOLLOWER);

        goalRoom1.join(follower1);
        goalRoom1.join(follower2);
        goalRoom2.join(follower3);

        when(goalRoomRepository.findAllByStartDateNow())
                .thenReturn(List.of(goalRoom1));
        when(goalRoomPendingMemberRepository.findAllByGoalRoom(any()))
                .thenReturn(List.of(goalRoomPendingMember, goalRoomPendingMember1, goalRoomPendingMember2));

        // when
        goalRoomCreateService.startGoalRooms();

        // then
        verify(goalRoomMemberRepository, times(1)).saveAll(anyList());
        verify(goalRoomPendingMemberRepository, times(1)).deleteAll(anyList());

        assertAll(
                () -> assertThat(goalRoom1.getStatus()).isEqualTo(RUNNING),
                () -> assertThat(goalRoom2.getStatus()).isEqualTo(RECRUITING)
        );
    }

    @Test
    void 골룸의_시작날짜가_아직_지나지_않았다면_골룸의_상태가_변경되지_않는다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);
        final GoalRoom goalRoom2 = 골룸을_생성한다(2L, creator, targetRoadmapContent, 10);

        final Member follower1 = 사용자를_생성한다(2L, "identifier1", "password2!", "name1", "010-1111-1111");
        final Member follower2 = 사용자를_생성한다(3L, "identifier2", "password3!", "name2", "010-1111-1112");
        final Member follower3 = 사용자를_생성한다(4L, "identifier3", "password4!", "name3", "010-1111-1113");

        goalRoom1.join(follower1);
        goalRoom1.join(follower2);
        goalRoom2.join(follower3);

        when(goalRoomRepository.findAllByStartDateNow())
                .thenReturn(List.of());

        // when
        goalRoomCreateService.startGoalRooms();

        // then
        verify(goalRoomPendingMemberRepository, times(0)).findAllByGoalRoom(any());
        verify(goalRoomMemberRepository, times(0)).saveAll(anyList());
        verify(goalRoomPendingMemberRepository, times(0)).deleteAll(anyList());

        assertAll(
                () -> assertThat(goalRoom1.getStatus()).isEqualTo(RECRUITING),
                () -> assertThat(goalRoom2.getStatus()).isEqualTo(RECRUITING)
        );
    }

    @Test
    void 인증_피드_등록을_요청한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        goalRoomMemberRepository.save(goalRoomLeader);
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);
        final CheckFeed checkFeed = 인증_피드를_생성한다(goalRoomRoadmapNode, goalRoomLeader);

        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));
        when(checkFeedRepository.findByGoalRoomMemberAndDateTime(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(checkFeedRepository.countByGoalRoomMemberAndGoalRoomRoadmapNode(any(), any()))
                .thenReturn(0);
        when(checkFeedRepository.save(any()))
                .thenReturn(checkFeed);
        when(filePathGenerator.makeFilePath(any(), any()))
                .thenReturn("originalFileName.jpeg");
        when(fileService.generateUrl(anyString(), any()))
                .thenReturn(makeUrl("originalFileName.jpeg"));

        // when
        final String response = goalRoomCreateService.createCheckFeed("identifier", 1L, request);

        // then
        assertAll(
                () -> assertThat(goalRoomLeader.getAccomplishmentRate()).isEqualTo(100 / (double) 10),
                () -> assertThat(response).contains("originalFileName")
        );
    }

    @Test
    void 하루에_두_번_이상_인증_피드_등록_요청_시_예외를_반환한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        goalRoomMemberRepository.save(goalRoomLeader);
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);
        final CheckFeed checkFeed = 인증_피드를_생성한다(goalRoomRoadmapNode, goalRoomLeader);

        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));
        when(checkFeedRepository.findByGoalRoomMemberAndDateTime(any(), any(), any()))
                .thenReturn(Optional.of(checkFeed));

        //expect
        assertThatThrownBy(
                () -> goalRoomCreateService.createCheckFeed("identifier", 1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이미 오늘 인증 피드를 등록하였습니다.");
    }

    @Test
    void 골룸_노드에서_허가된_인증_횟수보다_많은_인증_피드_등록_요청_시_예외를_반환한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        goalRoomMemberRepository.save(goalRoomLeader);
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);

        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));
        when(checkFeedRepository.countByGoalRoomMemberAndGoalRoomRoadmapNode(any(), any()))
                .thenReturn(goalRoomRoadmapNode.getCheckCount());

        //expect
        assertThatThrownBy(
                () -> goalRoomCreateService.createCheckFeed("identifier", 1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이번 노드에는 최대 " + goalRoomRoadmapNode.getCheckCount() + "번만 인증 피드를 등록할 수 있습니다.");
    }

    @Test
    void 인증_피드_등록_요청_시_허용되지_않는_확장자_형식이라면_예외를_반환한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/gif");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        goalRoomMemberRepository.save(goalRoomLeader);
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);

        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));

        // when
        assertThatThrownBy(
                () -> goalRoomCreateService.createCheckFeed("identifier", 1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("허용되지 않는 확장자입니다.");
    }

    @Test
    void 인증_피드_등록_요청_시_존재하지_않는_골룸이라면_예외를_반환한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        goalRoomMemberRepository.save(goalRoomLeader);

        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.empty());

        //expect
        assertThatThrownBy(
                () -> goalRoomCreateService.createCheckFeed("identifier", 1L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 골룸입니다. goalRoomId = 1");
    }

    @Test
    void 인증_피드_등록_요청_시_사용자가_참여하지_않은_골룸이라면_예외를_반환한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);

        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        goalRoomMemberRepository.save(goalRoomLeader);

        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.empty());

        //expect
        assertThatThrownBy(
                () -> goalRoomCreateService.createCheckFeed("identifier", 1L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("골룸에 해당 사용자가 존재하지 않습니다. 사용자 아이디 = " + "identifier");
    }

    @Test
    void 투두리스트를_체크한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);
        goalRoom.addGoalRoomTodo(new GoalRoomToDo(
                1L, new GoalRoomTodoContent("투두 1"), new Period(TODAY, TODAY.plusDays(3))
        ));
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);

        when(goalRoomRepository.findByIdWithTodos(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomMember));

        when(goalRoomToDoCheckRepository.findByGoalRoomIdAndTodoAndMemberIdentifier(any(), any(), any()))
                .thenReturn(Optional.empty());

        // when
        final GoalRoomToDoCheckResponse checkResponse = goalRoomCreateService.checkGoalRoomTodo(1L, 1L, "cokirikiri");

        // then
        assertThat(checkResponse)
                .isEqualTo(new GoalRoomToDoCheckResponse(true));
    }

    @Test
    void 투두리스트_체크시_체크_이력이_있으면_제거한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);
        final GoalRoomToDo goalRoomToDo = new GoalRoomToDo(
                1L, new GoalRoomTodoContent("투두 1"), new Period(TODAY, TODAY.plusDays(3)));
        goalRoom.addGoalRoomTodo(goalRoomToDo);

        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        final GoalRoomToDoCheck goalRoomToDoCheck = new GoalRoomToDoCheck(goalRoomMember, goalRoomToDo);

        when(goalRoomRepository.findByIdWithTodos(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomMember));

        when(goalRoomToDoCheckRepository.findByGoalRoomIdAndTodoAndMemberIdentifier(any(), any(), any()))
                .thenReturn(Optional.of(goalRoomToDoCheck));

        // when
        final GoalRoomToDoCheckResponse checkResponse = goalRoomCreateService.checkGoalRoomTodo(1L, 1L, "cokirikiri");

        // then
        assertThat(checkResponse)
                .isEqualTo(new GoalRoomToDoCheckResponse(false));
    }

    @Test
    void 투두리스트_체크시_골룸이_존재하지_않으면_예외가_발생한다() {
        // given
        when(goalRoomRepository.findByIdWithTodos(anyLong()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomCreateService.checkGoalRoomTodo(1L, 1L, "cokirikiri"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("골룸이 존재하지 않습니다. goalRoomId = 1");
    }

    @Test
    void 투두리스트_체크시_골룸에_사용자가_없으면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "010-1234-5678");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);
        goalRoom.addGoalRoomTodo(new GoalRoomToDo(
                1L, new GoalRoomTodoContent("투두 1"), new Period(TODAY, TODAY.plusDays(3))));

        when(goalRoomRepository.findByIdWithTodos(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomCreateService.checkGoalRoomTodo(1L, 1L, "cokirikiri"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("골룸에 사용자가 존재하지 않습니다. goalRoomId = 1 memberIdentifier = cokirikiri");
    }

    @Test
    void 골룸을_나간다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("골룸"), new LimitedMemberCount(3),
                new RoadmapContent("content"), GOAL_ROOM_MEMBER1);

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(GOAL_ROOM_MEMBER1));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        // when
        // then
        assertDoesNotThrow(() -> goalRoomCreateService.leave("identifier2", 1L));

    }

    @Test
    void 골룸을_나갈때_존재하지_않는_회원일_경우_예외가_발생한다() {
        // given
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> goalRoomCreateService.leave("identifier2", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸을_나갈때_존재하지_않는_골룸일_경우_예외가_발생한다() {
        // given
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> goalRoomCreateService.leave("identifier2", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸을_나갈때_골룸이_진행중이면_예외가_발생한다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("골룸"), new LimitedMemberCount(3),
                new RoadmapContent("content"), GOAL_ROOM_MEMBER1);

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        // when
        goalRoom.start();

        // then
        assertThatThrownBy(() -> goalRoomCreateService.leave("identifier2", 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸을_나갈때_골룸에_남아있는_사용자가_없으면_골룸이_삭제된다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("골룸"), new LimitedMemberCount(3),
                new RoadmapContent("content"), GOAL_ROOM_MEMBER1);

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        // when
        goalRoomCreateService.leave("identifier2", 1L);

        // then
        verify(goalRoomRepository, times(1)).delete(goalRoom);
    }

    private Member 사용자를_생성한다(final Long memberId, final String identifier, final String password, final String nickname,
                             final String phoneNumber) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE,
                LocalDate.of(1995, 9, 30), phoneNumber);

        return new Member(memberId, new Identifier(identifier), new EncryptedPassword(new Password(password)),
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
        final GoalRoom goalRoom = new GoalRoom(goalRoomId, new GoalRoomName("골룸 이름"),
                new LimitedMemberCount(limitedMemberCount), roadmapContent, creator);
        goalRoom.addAllGoalRoomRoadmapNodes(골룸_로드맵_노드들을_생성한다(roadmapContent.getNodes()));
        return goalRoom;
    }

    private GoalRoom 시작_날짜가_미래인_골룸을_생성한다(final Long goalRoomId, final Member creator,
                                        final RoadmapContent roadmapContent, final Integer limitedMemberCount) {
        final GoalRoom goalRoom = new GoalRoom(goalRoomId, new GoalRoomName("골룸 이름"),
                new LimitedMemberCount(limitedMemberCount), roadmapContent, creator);
        final GoalRoomRoadmapNode goalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TEN_DAY_LATER, TWENTY_DAY_LATER), 5, roadmapContent.getNodes().getValues().get(0));
        goalRoom.addAllGoalRoomRoadmapNodes(
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode)));
        return goalRoom;
    }

    private GoalRoomPendingMember 골룸_대기자를_생성한다(final GoalRoom goalRoom, final Member follower,
                                               final GoalRoomRole role) {
        return new GoalRoomPendingMember(role, LocalDateTime.of(2023, 7, 19, 12, 0, 0), goalRoom, follower);
    }

    private GoalRoomRoadmapNodes 골룸_로드맵_노드들을_생성한다(final RoadmapNodes roadmapNodes) {
        return new GoalRoomRoadmapNodes(List.of(
                new GoalRoomRoadmapNode(new Period(TODAY, TEN_DAY_LATER), 5, roadmapNodes.getValues().get(0)),
                new GoalRoomRoadmapNode(new Period(TEN_DAY_LATER.plusDays(1), TWENTY_DAY_LATER), 5,
                        roadmapNodes.getValues().get(1)))
        );
    }

    private CheckFeedRequest 인증_피드_요청_DTO를_생성한다(final String contentType) {
        return new CheckFeedRequest(
                new MockMultipartFile("image", "originalFileName.jpeg", contentType,
                        "test image".getBytes()), "인증 피드 설명");
    }

    private CheckFeed 인증_피드를_생성한다(final GoalRoomRoadmapNode goalRoomRoadmapNode, final GoalRoomMember joinedMember) {
        return new CheckFeed("src/test/resources/testImage/originalFileName.jpeg", ImageContentType.JPEG,
                "originalFileName.jpeg", "인증 피드 설명", goalRoomRoadmapNode, joinedMember);
    }

    private URL makeUrl(final String path) {
        try {
            return new URL("http://example.com/" + path);
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
