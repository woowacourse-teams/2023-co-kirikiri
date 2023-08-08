package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
import co.kirikiri.domain.goalroom.GoalRoomToDoCheck;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.GoalRoomTodoContent;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberImage;
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
import co.kirikiri.exception.ForbiddenException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.CheckFeedRepository;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.GoalRoomToDoCheckRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.goalroom.request.GoalRoomStatusTypeRequest;
import co.kirikiri.service.dto.goalroom.response.CheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodesResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomForListResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
import co.kirikiri.service.dto.member.response.MemberNameAndImageResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoalRoomReadServiceTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LAYER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @Mock
    private GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    @Mock
    private GoalRoomToDoCheckRepository goalRoomToDoCheckRepository;

    @Mock
    private CheckFeedRepository checkFeedRepository;

    @InjectMocks
    private GoalRoomReadService goalRoomReadService;

    @Test
    void 골룸_아이디로_골룸_정보를_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent);

        when(goalRoomRepository.findByIdWithRoadmapContent(any()))
                .thenReturn(Optional.of(goalRoom));

        // when
        final GoalRoomResponse goalRoomResponse = goalRoomReadService.findGoalRoom(goalRoom.getId());
        final GoalRoomResponse expected = 예상하는_골룸_응답을_생성한다();

        // then
        assertThat(goalRoomResponse)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_조회시_골룸_아이디가_유효하지_않으면_예외가_발생한다() {
        // given
        when(goalRoomRepository.findByIdWithRoadmapContent(any()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomReadService.findGoalRoom(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 모집중인_골룸에_대해서_골룸_아이디와_사용자_아이디로_골룸_대기_목록_조회시_참여하는_사용자면_참여여부가_true로_반환된다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent);

        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), goalRoom, creator);

        when(goalRoomRepository.findByIdWithRoadmapContent(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomPendingMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomPendingMember));

        // when
        final GoalRoomCertifiedResponse goalRoomResponse = goalRoomReadService.findGoalRoom(
                creator.getIdentifier().getValue(), goalRoom.getId());
        final GoalRoomCertifiedResponse expected = 예상하는_로그인된_사용자의_골룸_응답을_생성한다(true);

        // then
        assertThat(goalRoomResponse)
                .isEqualTo(expected);
    }

    @Test
    void 모집중인_골룸에_대해서_골룸_아이디와_사용자_아이디로_골룸_대기_목록_조회시_참여하지_않는_사용자면_참여여부가_false로_반환된다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent);

        when(goalRoomRepository.findByIdWithRoadmapContent(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomPendingMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.empty());

        // when
        final GoalRoomCertifiedResponse goalRoomResponse = goalRoomReadService.findGoalRoom(
                creator.getIdentifier().getValue(), goalRoom.getId());
        final GoalRoomCertifiedResponse expected = 예상하는_로그인된_사용자의_골룸_응답을_생성한다(false);

        // then
        assertThat(goalRoomResponse)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_아이디와_사용자_아이디로_골룸_대기_목록_조회시_골룸_아이디가_유효하지_않으면_예외가_발생한다() {
        // given
        when(goalRoomRepository.findByIdWithRoadmapContent(any()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomReadService.findGoalRoom("cokirikiri", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 정상적으로_골룸에_참여자를_조회한다() {
        //given
        final Member creator = 사용자를_생성한다(1L);
        final Member follower = 사용자를_생성한다(2L);

        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmap.getContents().getValues().get(0));

        final GoalRoomMember goalRoomMemberCreator = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(),
                goalRoom, creator);
        final GoalRoomMember goalRoomMemberFollower = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(),
                goalRoom, follower);

        given(goalRoomMemberRepository.findByGoalRoomIdOrderByAccomplishmentRateDesc(anyLong()))
                .willReturn(List.of(goalRoomMemberCreator, goalRoomMemberFollower));

        //when
        final List<GoalRoomMemberResponse> result = goalRoomReadService.findGoalRoomMembers(1L);

        //then
        final GoalRoomMemberResponse expectedGoalRoomMemberResponse1 = new GoalRoomMemberResponse(1L, "name1",
                "serverFilePath", 0.0);
        final GoalRoomMemberResponse expectedGoalRoomMemberResponse2 = new GoalRoomMemberResponse(2L, "name1",
                "serverFilePath", 0.0);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(List.of(expectedGoalRoomMemberResponse1, expectedGoalRoomMemberResponse2));
    }

    @Test
    void 존재하지_않는_골룸일_경우_예외를_던진다() {
        //given
        given(goalRoomMemberRepository.findByGoalRoomIdOrderByAccomplishmentRateDesc(anyLong()))
                .willReturn(Collections.emptyList());

        //when
        //then
        assertThatThrownBy(() -> goalRoomReadService.findGoalRoomMembers(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸의_전체_투두리스트를_조회한다() {
        // given
        final Member creator = 사용자를_생성한다(1L);
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmap.getContents().getValues().get(0));

        final GoalRoomToDo firstGoalRoomTodo = new GoalRoomToDo(1L, new GoalRoomTodoContent("투두 1"),
                new Period(TODAY, TEN_DAY_LATER));
        final GoalRoomToDo secondGoalRoomTodo = new GoalRoomToDo(2L, new GoalRoomTodoContent("투두 2"),
                new Period(TWENTY_DAY_LAYER, THIRTY_DAY_LATER));
        goalRoom.addGoalRoomTodo(firstGoalRoomTodo);
        goalRoom.addGoalRoomTodo(secondGoalRoomTodo);

        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        when(goalRoomRepository.findGoalRoomMember(anyLong(), any()))
                .thenReturn(Optional.of(goalRoomMember));
        when(goalRoomRepository.findByIdWithTodos(1L))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomToDoCheckRepository.findByGoalRoomIdAndMemberIdentifier(anyLong(), any()))
                .thenReturn(List.of(
                        new GoalRoomToDoCheck(goalRoomMember, firstGoalRoomTodo)
                ));

        // when
        final List<GoalRoomTodoResponse> responses = goalRoomReadService.findAllGoalRoomTodo(1L, "identifier");
        final List<GoalRoomTodoResponse> expected = List.of(
                new GoalRoomTodoResponse(1L, "투두 1", TODAY, TEN_DAY_LATER, new GoalRoomToDoCheckResponse(true)),
                new GoalRoomTodoResponse(2L, "투두 2", TWENTY_DAY_LAYER, THIRTY_DAY_LATER,
                        new GoalRoomToDoCheckResponse(false)));

        // then
        assertThat(responses)
                .isEqualTo(expected);
    }

    @Test
    void 골룸의_투두리스트_조회시_골룸에_참여하지_않은_사용자면_예외가_발생한다() {
        // given
        when(goalRoomRepository.findGoalRoomMember(anyLong(), any()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomReadService.findAllGoalRoomTodo(1L, "identifier"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 골룸의_투두리스트_조회시_존재하지_않는_골룸이면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다(1L);
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmap.getContents().getValues().get(0));
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        when(goalRoomRepository.findGoalRoomMember(anyLong(), any()))
                .thenReturn(Optional.of(goalRoomMember));
        when(goalRoomRepository.findByIdWithTodos(1L))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomReadService.findAllGoalRoomTodo(1L, "identifier"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 사용자_단일_골룸을_조회한다() {
        // given
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapNode roadmapNode3 = new RoadmapNode("로드맵 3주차", "로드맵 3주차 내용");
        final RoadmapNode roadmapNode4 = new RoadmapNode("로드맵 4주차", "로드맵 4주차 내용");
        final RoadmapNodes roadmapNodes = new RoadmapNodes(
                List.of(roadmapNode1, roadmapNode2, roadmapNode3, roadmapNode4));
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(roadmapNodes);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = new GoalRoomRoadmapNode(
                new Period(TODAY, TODAY.plusDays(10)), 5, roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = new GoalRoomRoadmapNode(
                new Period(TODAY.plusDays(11), TODAY.plusDays(20)), 5, roadmapNode2);
        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = new GoalRoomRoadmapNode(
                new Period(TODAY.plusDays(21), TODAY.plusDays(30)), 5, roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = new GoalRoomRoadmapNode(
                new Period(TODAY.plusDays(31), TODAY.plusDays(40)), 5, roadmapNode2);

        final Member member = 사용자를_생성한다(1L);
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("goalroom"), new LimitedMemberCount(10),
                roadmapContent, member);
        goalRoom.addAllGoalRoomRoadmapNodes(
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)));

        final List<CheckFeed> checkFeeds = 인증_피드_목록을_생성한다(goalRoomRoadmapNode1, member, goalRoom);
        given(goalRoomRepository.findByIdWithContentAndNodesAndTodos(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(checkFeedRepository.findByGoalRoomRoadmapNode(any()))
                .willReturn(checkFeeds);

        final MemberGoalRoomResponse expected = new MemberGoalRoomResponse(goalRoom.getName().getValue(),
                goalRoom.getStatus().name(), member.getId(), goalRoom.getCurrentMemberCount(),
                goalRoom.getLimitedMemberCount().getValue(), goalRoom.getStartDate(), goalRoom.getEndDate(),
                roadmapContent.getId(), new GoalRoomRoadmapNodesResponse(false, true,
                List.of(
                        new GoalRoomRoadmapNodeResponse(goalRoomRoadmapNode1.getId(), roadmapNode1.getTitle(),
                                goalRoomRoadmapNode1.getStartDate(),
                                goalRoomRoadmapNode1.getEndDate(), goalRoomRoadmapNode1.getCheckCount()),
                        new GoalRoomRoadmapNodeResponse(goalRoomRoadmapNode2.getId(), roadmapNode2.getTitle(),
                                goalRoomRoadmapNode2.getStartDate(),
                                goalRoomRoadmapNode2.getEndDate(), goalRoomRoadmapNode2.getCheckCount())
                )), null,
                List.of(
                        new CheckFeedResponse(1L, "filePath1", "인증 피드 설명", LocalDateTime.now()),
                        new CheckFeedResponse(2L, "filePath2", "인증 피드 설명", LocalDateTime.now()),
                        new CheckFeedResponse(3L, "filePath3", "인증 피드 설명", LocalDateTime.now()),
                        new CheckFeedResponse(4L, "filePath4", "인증 피드 설명", LocalDateTime.now())
                ));

        //when
        final MemberGoalRoomResponse response = goalRoomReadService.findMemberGoalRoom("identifier1", 1L);

        //then
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("goalRoomTodos", "checkFeeds.id", "checkFeeds.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 사용자_단일_목록_조회_시_유효하지_않은_골룸_아이디일_경우_예외를_반환한다() {
        //given
        when(goalRoomRepository.findByIdWithContentAndNodesAndTodos(anyLong()))
                .thenThrow(new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = 1"));

        //when, then
        assertThatThrownBy(() -> goalRoomReadService.findMemberGoalRoom("identifier1", 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("골룸 정보가 존재하지 않습니다. goalRoomId = 1");

    }

    @Test
    void 사용자_단일_목록_조회_시_유효하지_않은_아이디일_경우_예외를_반환한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent);

        when(goalRoomRepository.findByIdWithContentAndNodesAndTodos(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenThrow(new NotFoundException("존재하지 않는 회원입니다."));

        // when, then
        assertThatThrownBy(() -> goalRoomReadService.findMemberGoalRoom("identifier2", 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 사용자_단일_목록_조회_시_사용자가_참여하지_않은_골룸일_경우_예외를_반환한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final Member member = 사용자를_생성한다(2L);
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent);

        when(goalRoomRepository.findByIdWithContentAndNodesAndTodos(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(member));

        // when, then
        assertThatThrownBy(() -> goalRoomReadService.findMemberGoalRoom("identifier2", 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("해당 골룸에 참여하지 않은 사용자입니다.");
    }

    @Test
    void 사용자_골룸_목록을_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(creator, targetRoadmapContent);
        골룸을_생성한다(creator, targetRoadmapContent);
        final GoalRoom goalRoom3 = 골룸을_생성한다(creator, targetRoadmapContent);
        골룸을_생성한다(creator, targetRoadmapContent);

        final Member member = 사용자를_생성한다(2L);
        goalRoom1.join(member);
        goalRoom3.join(member);

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(member));
        when(goalRoomRepository.findByMember(any()))
                .thenReturn(List.of(goalRoom1, goalRoom3));

        final List<MemberGoalRoomForListResponse> expected = List.of(
                new MemberGoalRoomForListResponse(1L, "골룸", "RECRUITING", 2, 10, LocalDateTime.now(), TODAY,
                        THIRTY_DAY_LATER, new MemberResponse(creator.getId(), creator.getNickname().getValue())),
                new MemberGoalRoomForListResponse(2L, "골룸", "RECRUITING", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue()))
        );

        //when
        final List<MemberGoalRoomForListResponse> response = goalRoomReadService.findMemberGoalRooms("identifier1");

        //then
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("goalRoomId", "createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 사용자_골룸_목룩_조회_중_참여한_골룸이없으면_빈_리스트를_반환한다() {
        // given
        final Member creator = 크리에이터를_생성한다();

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomRepository.findByMember(any()))
                .thenReturn(Collections.emptyList());

        // when
        final List<MemberGoalRoomForListResponse> response = goalRoomReadService.findMemberGoalRooms("identifier1");

        // then
        assertThat(response).isEmpty();
    }

    @Test
    void 사용자_골룸_목록_중_모집_중인_상태만_조회한다() {
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(creator, targetRoadmapContent);
        final GoalRoom goalRoom2 = 골룸을_생성한다(creator, targetRoadmapContent);
        final GoalRoom goalRoom3 = 골룸을_생성한다(creator, targetRoadmapContent);
        final GoalRoom goalRoom4 = 골룸을_생성한다(creator, targetRoadmapContent);

        final Member member = 사용자를_생성한다(2L);
        goalRoom1.join(member);
        goalRoom2.join(member);
        goalRoom3.join(member);
        goalRoom4.join(member);

        goalRoom3.updateStatus(GoalRoomStatus.RUNNING);
        goalRoom4.updateStatus(GoalRoomStatus.COMPLETED);

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(member));
        when(goalRoomRepository.findByMemberAndStatus(any(), any()))
                .thenReturn(List.of(goalRoom1, goalRoom2));

        final List<MemberGoalRoomForListResponse> expected = List.of(
                new MemberGoalRoomForListResponse(1L, "골룸", "RECRUITING", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue())),
                new MemberGoalRoomForListResponse(2L, "골룸", "RECRUITING", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue()))
        );

        //when
        final List<MemberGoalRoomForListResponse> response = goalRoomReadService.findMemberGoalRoomsByStatusType(
                "identifier2", GoalRoomStatusTypeRequest.RECRUITING);

        //then
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("goalRoomId", "createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 사용자_골룸_목록_중_진행_중인_상태만_조회한다() {
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(creator, targetRoadmapContent);
        final GoalRoom goalRoom2 = 골룸을_생성한다(creator, targetRoadmapContent);
        final GoalRoom goalRoom3 = 골룸을_생성한다(creator, targetRoadmapContent);
        final GoalRoom goalRoom4 = 골룸을_생성한다(creator, targetRoadmapContent);

        final Member member = 사용자를_생성한다(2L);
        goalRoom1.join(member);
        goalRoom2.join(member);
        goalRoom3.join(member);
        goalRoom4.join(member);

        goalRoom3.updateStatus(GoalRoomStatus.RUNNING);
        goalRoom4.updateStatus(GoalRoomStatus.RUNNING);

        goalRoom3.addAllGoalRoomMembers(List.of(
                new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom3, creator),
                new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom3, member)));
        goalRoom4.addAllGoalRoomMembers(List.of(
                new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom3, creator),
                new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom3, member)));

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(member));
        when(goalRoomRepository.findByMemberAndStatus(any(), any()))
                .thenReturn(List.of(goalRoom3, goalRoom4));

        final List<MemberGoalRoomForListResponse> expected = List.of(
                new MemberGoalRoomForListResponse(3L, "골룸", "RUNNING", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue())),
                new MemberGoalRoomForListResponse(4L, "골룸", "RUNNING", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue()))
        );

        //when
        final List<MemberGoalRoomForListResponse> response = goalRoomReadService.findMemberGoalRoomsByStatusType(
                "identifier2", GoalRoomStatusTypeRequest.RUNNING);

        //then
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("goalRoomId", "createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 사용자_골룸_목록_중_종료된_상태만_조회한다() {
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(creator, targetRoadmapContent);
        final GoalRoom goalRoom2 = 골룸을_생성한다(creator, targetRoadmapContent);
        final GoalRoom goalRoom3 = 골룸을_생성한다(creator, targetRoadmapContent);
        final GoalRoom goalRoom4 = 골룸을_생성한다(creator, targetRoadmapContent);

        final Member member = 사용자를_생성한다(2L);
        goalRoom1.join(member);
        goalRoom2.join(member);
        goalRoom3.join(member);
        goalRoom4.join(member);

        goalRoom3.updateStatus(GoalRoomStatus.COMPLETED);
        goalRoom4.updateStatus(GoalRoomStatus.COMPLETED);

        goalRoom3.addAllGoalRoomMembers(List.of(
                new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom3, creator),
                new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom3, member)));
        goalRoom4.addAllGoalRoomMembers(List.of(
                new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom3, creator),
                new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom3, member)));

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(member));
        when(goalRoomRepository.findByMemberAndStatus(any(), any()))
                .thenReturn(List.of(goalRoom3, goalRoom4));

        final List<MemberGoalRoomForListResponse> expected = List.of(
                new MemberGoalRoomForListResponse(3L, "골룸", "COMPLETED", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue())),
                new MemberGoalRoomForListResponse(4L, "골룸", "COMPLETED", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue()))
        );

        //when
        final List<MemberGoalRoomForListResponse> response = goalRoomReadService.findMemberGoalRoomsByStatusType(
                "identifier2", GoalRoomStatusTypeRequest.COMPLETED);

        //then
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("goalRoomId", "createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 골룸의_전체_노드를_조회한다() {
        // given
        final Member creator = 사용자를_생성한다(1L);
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmap.getContents().getValues().get(0));

        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        when(goalRoomRepository.findGoalRoomMember(anyLong(), any()))
                .thenReturn(Optional.of(goalRoomMember));
        when(goalRoomRepository.findByIdWithNodes(1L))
                .thenReturn(Optional.of(goalRoom));

        // when
        final List<GoalRoomRoadmapNodeResponse> responses = goalRoomReadService.findAllGoalRoomNodes(1L, "identifier");
        final List<GoalRoomRoadmapNodeResponse> expected = List.of(
                new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", TODAY, TEN_DAY_LATER, 10),
                new GoalRoomRoadmapNodeResponse(2L, "로드맵 2주차", TWENTY_DAY_LAYER, THIRTY_DAY_LATER, 2)
        );

        // then
        assertThat(responses)
                .isEqualTo(expected);
    }

    @Test
    void 골룸의_노드_조회시_골룸에_참여하지_않은_사용자면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다(1L);
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmap.getContents().getValues().get(0));

        when(goalRoomRepository.findByIdWithNodes(1L))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomRepository.findGoalRoomMember(anyLong(), any()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomReadService.findAllGoalRoomNodes(1L, "identifier"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 골룸의_노드_조회시_존재하지_않는_골룸이면_예외가_발생한다() {
        // given
        when(goalRoomRepository.findByIdWithNodes(1L))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomReadService.findAllGoalRoomNodes(1L, "identifier"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸의_인증피드를_전체_조회한다() {
        // given
        final Member creator = 사용자를_생성한다(1L);
        final Member follower = 사용자를_생성한다(2L);

        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmap.getContents().getValues().get(0));
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom,
                follower);
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);

        final CheckFeed checkFeed1 = 인증피드를_생성한다("serverFilePath1", "description1", goalRoomRoadmapNode,
                goalRoomMember1);
        final CheckFeed checkFeed2 = 인증피드를_생성한다("serverFilePath2", "description2", goalRoomRoadmapNode,
                goalRoomMember1);
        final CheckFeed checkFeed3 = 인증피드를_생성한다("serverFilePath3", "description3", goalRoomRoadmapNode,
                goalRoomMember2);

        given(goalRoomRepository.findByIdWithNodes(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .willReturn(Optional.of(goalRoomMember1));
        given(checkFeedRepository.findByGoalRoomRoadmapNodeWithGoalRoomMemberAndMemberImage(any()))
                .willReturn(List.of(checkFeed3, checkFeed2, checkFeed1));

        // when
        final List<GoalRoomCheckFeedResponse> responses = goalRoomReadService.findGoalRoomCheckFeeds("cokirikiri", 1L);

        // then
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse1 = new GoalRoomCheckFeedResponse(
                new MemberNameAndImageResponse(1L, "name1", "serverFilePath"),
                new CheckFeedResponse(1L, "serverFilePath1", "description1", LocalDateTime.now()));
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse2 = new GoalRoomCheckFeedResponse(
                new MemberNameAndImageResponse(1L, "name1", "serverFilePath"),
                new CheckFeedResponse(2L, "serverFilePath2", "description2", LocalDateTime.now()));
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse3 = new GoalRoomCheckFeedResponse(
                new MemberNameAndImageResponse(2L, "name1", "serverFilePath"),
                new CheckFeedResponse(3L, "serverFilePath3", "description3", LocalDateTime.now()));
        final List<GoalRoomCheckFeedResponse> expected = List.of(goalRoomCheckFeedResponse3,
                goalRoomCheckFeedResponse2, goalRoomCheckFeedResponse1);

        assertThat(responses).usingRecursiveComparison()
                .ignoringFields("checkFeed.id", "checkFeed.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 골룸의_인증피드를_전체_조회할_때_존재하지_않는_골룸이면_예외가_발생한다() {
        // given
        given(goalRoomRepository.findByIdWithNodes(anyLong()))
                .willThrow(new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = 1"));

        // when
        // then
        assertThatThrownBy(() -> goalRoomReadService.findGoalRoomCheckFeeds("cokirikiri", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸의_인증피드를_전체_조회할_때_골룸에_참여하지_않은_회원이면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다(1L);

        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmap.getContents().getValues().get(0));

        given(goalRoomRepository.findByIdWithNodes(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomRepository.findByIdWithNodes(anyLong()))
                .willThrow(new BadRequestException("골룸에 참여하지 않은 회원입니다."));

        // when
        // then
        assertThatThrownBy(() -> goalRoomReadService.findGoalRoomCheckFeeds("cokirikiri", 1L))
                .isInstanceOf(BadRequestException.class);
    }

    private Member 크리에이터를_생성한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), "010-1234-5678");
        return new Member(1L, new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), new Nickname("코끼리"), memberProfile);
    }

    private Member 사용자를_생성한다(final Long id) {
        return new Member(id, new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1")), new Nickname("name1"),
                new MemberImage("originalFileName", "serverFilePath", ImageContentType.JPEG),
                new MemberProfile(Gender.FEMALE, LocalDate.of(2000, 7, 20),
                        "010-1111-1111"));
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

    private GoalRoom 골룸을_생성한다(final Member member, final RoadmapContent roadmapContent) {
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10),
                roadmapContent, member);
        final List<RoadmapNode> roadmapNodes = roadmapContent.getNodes().getValues();

        final RoadmapNode firstRoadmapNode = roadmapNodes.get(0);
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                1L, new Period(TODAY, TEN_DAY_LATER), 10, firstRoadmapNode);

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                2L, new Period(TWENTY_DAY_LAYER, THIRTY_DAY_LATER), 2, secondRoadmapNode);

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        return goalRoom;
    }

    private static GoalRoomResponse 예상하는_골룸_응답을_생성한다() {
        final List<GoalRoomRoadmapNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", TODAY, TEN_DAY_LATER, 10),
                new GoalRoomRoadmapNodeResponse(2L, "로드맵 2주차", TWENTY_DAY_LAYER, THIRTY_DAY_LATER, 2));
        return new GoalRoomResponse("골룸", 1, 10, goalRoomNodeResponses, 31);
    }

    private static GoalRoomCertifiedResponse 예상하는_로그인된_사용자의_골룸_응답을_생성한다(final Boolean isJoined) {
        final List<GoalRoomRoadmapNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", TODAY, TEN_DAY_LATER, 10),
                new GoalRoomRoadmapNodeResponse(2L, "로드맵 2주차", TWENTY_DAY_LAYER, THIRTY_DAY_LATER, 2));
        return new GoalRoomCertifiedResponse("골룸", 1, 10, goalRoomNodeResponses, 31, isJoined);
    }

    private CheckFeed 인증피드를_생성한다(final String serverFilePath, final String description,
                                 final GoalRoomRoadmapNode goalRoomRoadmapNode, final GoalRoomMember goalRoomMember) {
        return new CheckFeed(serverFilePath, ImageContentType.PNG, "fileName", description, goalRoomRoadmapNode,
                goalRoomMember);
    }

    private List<CheckFeed> 인증_피드_목록을_생성한다(final GoalRoomRoadmapNode node, final Member member,
                                           final GoalRoom goalRoom) {
        return List.of(
                new CheckFeed("filePath1", ImageContentType.JPEG, "originalFileName1", "인증 피드 설명", node,
                        new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, member)),
                new CheckFeed("filePath2", ImageContentType.JPEG, "originalFileName2", "인증 피드 설명", node,
                        new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, member)),
                new CheckFeed("filePath3", ImageContentType.JPEG, "originalFileName3", "인증 피드 설명", node,
                        new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, member)),
                new CheckFeed("filePath4", ImageContentType.JPEG, "originalFileName4", "인증 피드 설명", node,
                        new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, member)),
                new CheckFeed("filePath5", ImageContentType.JPEG, "originalFileName5", "인증 피드 설명", node,
                        new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, member))
        );
    }
}
