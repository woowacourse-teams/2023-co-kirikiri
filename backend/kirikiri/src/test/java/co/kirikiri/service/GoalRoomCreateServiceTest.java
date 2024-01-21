package co.kirikiri.service;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.exception.ImageExtensionException;
import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
import co.kirikiri.domain.goalroom.GoalRoomToDoCheck;
import co.kirikiri.domain.goalroom.exception.GoalRoomException;
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
import co.kirikiri.persistence.goalroom.CheckFeedRepository;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.GoalRoomToDoCheckRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapDifficulty;
import co.kirikiri.roadmap.domain.RoadmapNode;
import co.kirikiri.roadmap.domain.RoadmapNodes;
import co.kirikiri.roadmap.domain.RoadmapStatus;
import co.kirikiri.roadmap.domain.RoadmapTags;
import co.kirikiri.roadmap.persistence.RoadmapContentRepository;
import co.kirikiri.roadmap.persistence.RoadmapRepository;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import co.kirikiri.service.exception.BadRequestException;
import co.kirikiri.service.exception.NotFoundException;
import co.kirikiri.service.goalroom.GoalRoomCreateService;
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

import static co.kirikiri.domain.goalroom.GoalRoomStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalRoomCreateServiceTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LATER = TODAY.plusDays(20);

    private static final Member MEMBER = new Member(new Identifier("identifier1"),
            new EncryptedPassword(new Password("password1!")),
            new Nickname("name1"), null,
            new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

    private static final Roadmap ROADMAP = new Roadmap(1L, "roadmap", "introduction", 30, RoadmapDifficulty.DIFFICULT,
            RoadmapStatus.CREATED, MEMBER.getId(), new RoadmapCategory("IT"), new RoadmapTags(new ArrayList<>()));

    private static final Roadmap DELETED_ROADMAP = new Roadmap(2L, "roadmap", "introduction", 30, RoadmapDifficulty.DIFFICULT,
            RoadmapStatus.DELETED, MEMBER.getId(), new RoadmapCategory("IT"), new RoadmapTags(new ArrayList<>()));

    private static final RoadmapNode roadmapNode1 = new RoadmapNode(1L, "title1", "content1");
    private static final RoadmapNode roadmapNode2 = new RoadmapNode(2L, "title2", "content2");
    private static final RoadmapContent ROADMAP_CONTENT = new RoadmapContent(1L, "content", ROADMAP.getId(), new RoadmapNodes(List.of(roadmapNode1, roadmapNode2)));
    private static final RoadmapContent DELETED_ROADMAP_CONTENT = new RoadmapContent(2L, "content2", DELETED_ROADMAP.getId(), null);

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @Mock
    private RoadmapRepository roadmapRepository;

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

    @Test
    void 정상적으로_골룸을_생성한다() {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name", 20,
                List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER),
                        new GoalRoomRoadmapNodeRequest(2L, 5, TEN_DAY_LATER.plusDays(1), TWENTY_DAY_LATER)));

        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(ROADMAP_CONTENT));
        given(roadmapRepository.findById(anyLong()))
                .willReturn(Optional.of(ROADMAP));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(MEMBER));
        given(goalRoomRepository.save(any()))
                .willReturn(new GoalRoom(1L, null, null, null, null));

        //when
        assertDoesNotThrow(() -> goalRoomCreateService.create(request, MEMBER.getIdentifier().getValue()));
    }

    @Test
    void 골룸_생성_시_삭제된_로드맵이면_예외를_던진다() {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));

        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(DELETED_ROADMAP_CONTENT));
        given(roadmapRepository.findById(anyLong()))
                .willReturn(Optional.of(DELETED_ROADMAP));

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.create(request, MEMBER.getIdentifier().getValue()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸_생성_시_존재하지_않은_로드맵_컨텐츠가_들어올때_예외를_던진다() {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));

        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.create(request, MEMBER.getIdentifier().getValue()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 로드맵 컨텐츠입니다.");
    }

    @Test
    void 골룸_생성_시_로드맵_컨텐츠의_노드사이즈와_요청의_노드사이즈가_다를때_예외를_던진다() {
        //given
        final List<GoalRoomRoadmapNodeRequest> wrongSizeGoalRoomRoadmapNodeRequest = List.of(
                new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER));
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, wrongSizeGoalRoomRoadmapNodeRequest);

        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(ROADMAP_CONTENT));
        given(roadmapRepository.findById(anyLong()))
                .willReturn(Optional.of(ROADMAP));

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.create(request, MEMBER.getIdentifier().getValue()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸_생성_시_로드맵에_존재하지_않는_노드가_요청으로_들어올때_예외를_던진다() {
        //given
        final long wrongRoadmapNodId = 3L;
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name", 20,
                List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER),
                        new GoalRoomRoadmapNodeRequest(wrongRoadmapNodId, 5, TEN_DAY_LATER, TWENTY_DAY_LATER)));

        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(ROADMAP_CONTENT));
        given(roadmapRepository.findById(anyLong()))
                .willReturn(Optional.of(ROADMAP));

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.create(request, MEMBER.getIdentifier().getValue()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸_생성_시_존재하지_않은_회원의_Identifier가_들어올때_예외를_던진다() {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name", 20,
                List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER),
                        new GoalRoomRoadmapNodeRequest(2L, 5, TEN_DAY_LATER.plusDays(1), TWENTY_DAY_LATER)));

        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(ROADMAP_CONTENT));
        given(roadmapRepository.findById(anyLong()))
                .willReturn(Optional.of(ROADMAP));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.create(request, "identifier"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸에_참가한다() {
        //given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        final Member follower = 사용자를_생성한다(2L, "identifier2", "password2@", "팔로워", "kirikiri1@email");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findGoalRoomByIdWithPessimisticLock(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        //when
        goalRoomCreateService.join(follower.getIdentifier().getValue(), goalRoom.getId());

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
        assertThatThrownBy(() -> goalRoomCreateService.join("identifier", 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 골룸_참가_요청시_유효한_골룸_아이디가_아니면_예외가_발생한다() {
        //given
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(MEMBER));
        when(goalRoomRepository.findGoalRoomByIdWithPessimisticLock(anyLong()))
                .thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> goalRoomCreateService.join("identifier1", 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 골룸입니다. goalRoomId = 1");
    }

    @Test
    void 골룸_참가_요청시_제한_인원이_가득_찼을_경우_예외가_발생한다() {
        //given
        final int limitedMemberCount = 1;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        final Member follower = 사용자를_생성한다(1L, "identifier2", "password1!", "팔로워", "kirikiri1@email");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findGoalRoomByIdWithPessimisticLock(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        //when, then
        assertThatThrownBy(() -> goalRoomCreateService.join(follower.getIdentifier().getValue(), goalRoom.getId()))
                .isInstanceOf(GoalRoomException.class)
                .hasMessage("제한 인원이 꽉 찬 골룸에는 참여할 수 없습니다.");
    }

    @Test
    void 골룸_참가_요청시_모집_중이_아닌_경우_예외가_발생한다() {
        // given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        goalRoom.start();

        final Member follower = 사용자를_생성한다(2L, "identifier2", "password2!", "팔로워", "kirikiri1@email");

        // when
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findGoalRoomByIdWithPessimisticLock(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        // then
        assertThatThrownBy(() -> goalRoomCreateService.join(follower.getIdentifier().getValue(), 1L))
                .isInstanceOf(GoalRoomException.class)
                .hasMessage("모집 중이지 않은 골룸에는 참여할 수 없습니다.");
    }

    @Test
    void 정상적으로_골룸에_투두리스트를_추가한다() {
        //given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);

        goalRoom.addGoalRoomTodo(
                new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(MEMBER));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertDoesNotThrow(() -> goalRoomCreateService.addGoalRoomTodo(1L, MEMBER.getIdentifier().getValue(), goalRoomTodoRequest));
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
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier", goalRoomTodoRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_골룸을_찾지_못할_경우_예외를_던진다() {
        //given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);

        goalRoom.addGoalRoomTodo(
                new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(MEMBER));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(2L, MEMBER.getIdentifier().getValue(), goalRoomTodoRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_종료된_골룸일_경우_예외를_던진다() {
        //given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);

        goalRoom.addGoalRoomTodo(
                new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));
        goalRoom.complete();

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(MEMBER));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, MEMBER.getIdentifier().getValue(), goalRoomTodoRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_리더가_아닐_경우_예외를_던진다() {
        //given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        final Member follower = 사용자를_생성한다(2L, "identifier2", "password2@", "팔로워", "kirikiri1@email");

        goalRoom.addGoalRoomTodo(
                new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(follower));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, follower.getIdentifier().getValue(), goalRoomTodoRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_골룸_컨텐츠가_250글자가_넘을때_예외를_던진다() {
        //given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);

        goalRoom.addGoalRoomTodo(
                new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(MEMBER));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final String goalRoomTodoContent = "a".repeat(251);
        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest(goalRoomTodoContent, TODAY,
                TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, MEMBER.getIdentifier().getValue(), goalRoomTodoRequest))
                .isInstanceOf(GoalRoomException.class);
    }

    @Test
    void 골룸을_시작한다() {
        // given
        final int limitedMemberCount = 10;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(MEMBER));
        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));

        // when
        goalRoomCreateService.startGoalRoom(MEMBER.getIdentifier().getValue(), 1L);

        // then
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
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(MEMBER));
        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomCreateService.startGoalRoom("identifier", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸을_시작하는_사용자가_골룸의_리더가_아니면_예외가_발생한다() {
        // given
        final int limitedMemberCount = 10;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        final Member follower = 사용자를_생성한다(2L, "kirikirico", "password2!", "끼리코", "kirikiri1@email");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));

        // expected
        assertThatThrownBy(() -> goalRoomCreateService.startGoalRoom(follower.getIdentifier().getValue(), 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸_시작시_시작날짜가_아직_지나지_않았으면_예외가_발생한다() {
        // given
        final int limitedMemberCount = 10;
        final GoalRoom goalRoom = 시작_날짜가_미래인_골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(MEMBER));
        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));

        // expected
        assertThatThrownBy(() -> goalRoomCreateService.startGoalRoom(MEMBER.getIdentifier().getValue(), 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 인증_피드_등록을_요청한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final int limitedMemberCount = 10;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, MEMBER);
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
        final String response = goalRoomCreateService.createCheckFeed(MEMBER.getIdentifier().getValue(), 1L, request);

        // then
        assertAll(
                () -> assertThat(goalRoomLeader.getAccomplishmentRate()).isEqualTo(100 / (double) 10),
                () -> assertThat(response).contains("originalFileName")
        );
    }

    @Test
    void 인증_피드_등록시_노드_기간에_해당하지_않으면_예외가_발생한다() {
        // given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 시작_날짜가_미래인_골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, MEMBER);
        goalRoomMemberRepository.save(goalRoomLeader);

        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));

        // when
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        // then
        assertThatThrownBy(
                () -> goalRoomCreateService.createCheckFeed(MEMBER.getIdentifier().getValue(), goalRoom.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("인증 피드는 노드 기간 내에만 작성할 수 있습니다.");
    }

    @Test
    void 하루에_두_번_이상_인증_피드_등록_요청_시_예외를_반환한다() {
        // given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, MEMBER);
        goalRoomMemberRepository.save(goalRoomLeader);

        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);
        final CheckFeed checkFeed = 인증_피드를_생성한다(goalRoomRoadmapNode, goalRoomLeader);

        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));
        when(checkFeedRepository.findByGoalRoomMemberAndDateTime(any(), any(), any()))
                .thenReturn(Optional.of(checkFeed));

        // when
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        // then
        assertThatThrownBy(
                () -> goalRoomCreateService.createCheckFeed(MEMBER.getIdentifier().getValue(), goalRoom.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이미 오늘 인증 피드를 등록하였습니다.");
    }

    @Test
    void 골룸_노드에서_허가된_인증_횟수보다_많은_인증_피드_등록_요청_시_예외를_반환한다() {
        // given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, MEMBER);

        goalRoomMemberRepository.save(goalRoomLeader);
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);

        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));
        when(checkFeedRepository.countByGoalRoomMemberAndGoalRoomRoadmapNode(any(), any()))
                .thenReturn(goalRoomRoadmapNode.getCheckCount());

        // when
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        // then
        assertThatThrownBy(
                () -> goalRoomCreateService.createCheckFeed(MEMBER.getIdentifier().getValue(), goalRoom.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이번 노드에는 최대 " + goalRoomRoadmapNode.getCheckCount() + "번만 인증 피드를 등록할 수 있습니다.");
    }

    @Test
    void 인증_피드_등록_요청_시_허용되지_않는_확장자_형식이라면_예외를_반환한다() {
        // given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, MEMBER);
        goalRoomMemberRepository.save(goalRoomLeader);

        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));

        // when
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/gif");

        // then
        assertThatThrownBy(
                () -> goalRoomCreateService.createCheckFeed(MEMBER.getIdentifier().getValue(), goalRoom.getId(), request))
                .isInstanceOf(ImageExtensionException.class)
                .hasMessage("허용되지 않는 확장자입니다.");
    }

    @Test
    void 인증_피드_등록_요청_시_존재하지_않는_골룸이라면_예외를_반환한다() {
        // given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, MEMBER);
        goalRoomMemberRepository.save(goalRoomLeader);

        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.empty());

        // when
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        // then
        assertThatThrownBy(
                () -> goalRoomCreateService.createCheckFeed(MEMBER.getIdentifier().getValue(), goalRoom.getId(), request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 골룸입니다. goalRoomId = 1");
    }

    @Test
    void 인증_피드_등록_요청_시_사용자가_참여하지_않은_골룸이라면_예외를_반환한다() {
        // given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, MEMBER);
        goalRoomMemberRepository.save(goalRoomLeader);

        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.empty());

        // when
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        // then
        assertThatThrownBy(
                () -> goalRoomCreateService.createCheckFeed("identifier", goalRoom.getId(), request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("골룸에 해당 사용자가 존재하지 않습니다. 사용자 아이디 = " + "identifier");
    }

    @Test
    void 투두리스트를_체크한다() {
        // given
        final int limitedMemberCount = 10;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        goalRoom.addGoalRoomTodo(new GoalRoomToDo(
                1L, new GoalRoomTodoContent("투두 1"), new Period(TODAY, TODAY.plusDays(3))));
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, MEMBER);

        when(goalRoomRepository.findByIdWithTodos(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomMember));
        when(goalRoomToDoCheckRepository.findByGoalRoomIdAndTodoAndMemberIdentifier(any(), any(), any()))
                .thenReturn(Optional.empty());

        // when
        final GoalRoomToDoCheckResponse checkResponse = goalRoomCreateService.checkGoalRoomTodo(goalRoom.getId(), 1L, MEMBER.getIdentifier().getValue());

        // then
        assertThat(checkResponse)
                .isEqualTo(new GoalRoomToDoCheckResponse(true));
    }

    @Test
    void 투두리스트_체크시_체크_이력이_있으면_제거한다() {
        // given
        final int limitedMemberCount = 10;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        final GoalRoomToDo goalRoomToDo = new GoalRoomToDo(
                1L, new GoalRoomTodoContent("투두 1"), new Period(TODAY, TODAY.plusDays(3)));
        goalRoom.addGoalRoomTodo(goalRoomToDo);

        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, MEMBER);
        final GoalRoomToDoCheck goalRoomToDoCheck = new GoalRoomToDoCheck(goalRoomMember, goalRoomToDo);

        when(goalRoomRepository.findByIdWithTodos(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomMember));
        when(goalRoomToDoCheckRepository.findByGoalRoomIdAndTodoAndMemberIdentifier(any(), any(), any()))
                .thenReturn(Optional.of(goalRoomToDoCheck));

        // when
        final GoalRoomToDoCheckResponse checkResponse = goalRoomCreateService.checkGoalRoomTodo(goalRoom.getId(), 1L, MEMBER.getIdentifier().getValue());

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
    void 투두리스트_체크시_해당_투두가_존재하지_않으면_예외가_발생한다() {
        // given
        final int limitedMemberCount = 10;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        goalRoom.addGoalRoomTodo(new GoalRoomToDo(
                1L, new GoalRoomTodoContent("투두 1"), new Period(TODAY, TODAY.plusDays(3))));

        when(goalRoomRepository.findByIdWithTodos(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        // expected
        assertThatThrownBy(() -> goalRoomCreateService.checkGoalRoomTodo(goalRoom.getId(), 2L, MEMBER.getIdentifier().getValue()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 투두입니다. todoId = 2");
    }

    @Test
    void 투두리스트_체크시_골룸에_사용자가_없으면_예외가_발생한다() {
        // given
        final int limitedMemberCount = 10;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);
        goalRoom.addGoalRoomTodo(new GoalRoomToDo(
                1L, new GoalRoomTodoContent("투두 1"), new Period(TODAY, TODAY.plusDays(3))));

        when(goalRoomRepository.findByIdWithTodos(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomCreateService.checkGoalRoomTodo(goalRoom.getId(), 1L, "cokirikiri"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("골룸에 사용자가 존재하지 않습니다. goalRoomId = 1 memberIdentifier = cokirikiri");
    }

    @Test
    void 골룸을_나간다() {
        // given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(MEMBER));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        // when
        // then
        assertDoesNotThrow(() -> goalRoomCreateService.leave(MEMBER.getIdentifier().getValue(), goalRoom.getId()));

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
                .willReturn(Optional.of(MEMBER));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> goalRoomCreateService.leave(MEMBER.getIdentifier().getValue(), 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸을_나갈때_골룸이_진행중이면_예외가_발생한다() {
        // given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(MEMBER));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        // when
        goalRoom.start();

        // then
        assertThatThrownBy(() -> goalRoomCreateService.leave(MEMBER.getIdentifier().getValue(), goalRoom.getId()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸을_나갈때_골룸에_남아있는_사용자가_없으면_골룸이_삭제된다() {
        // given
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, ROADMAP_CONTENT, limitedMemberCount);

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(MEMBER));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        // when
        goalRoomCreateService.leave(MEMBER.getIdentifier().getValue(), goalRoom.getId());

        // then
        verify(goalRoomRepository, times(1)).delete(goalRoom);
    }

    private Member 사용자를_생성한다(final Long memberId, final String identifier, final String password, final String nickname,
                             final String email) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, email);
        return new Member(memberId, new Identifier(identifier), null, new EncryptedPassword(new Password(password)),
                new Nickname(nickname), null, memberProfile);
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
