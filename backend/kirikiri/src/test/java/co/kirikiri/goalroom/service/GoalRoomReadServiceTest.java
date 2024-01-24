package co.kirikiri.goalroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import co.kirikiri.common.exception.ForbiddenException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.common.service.FileService;
import co.kirikiri.common.type.ImageContentType;
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
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImage;
import co.kirikiri.domain.roadmap.RoadmapNodeImages;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.domain.GoalRoomPendingMember;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNode;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNodes;
import co.kirikiri.goalroom.domain.GoalRoomRole;
import co.kirikiri.goalroom.domain.vo.GoalRoomName;
import co.kirikiri.goalroom.domain.vo.LimitedMemberCount;
import co.kirikiri.goalroom.domain.vo.Period;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomPendingMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import co.kirikiri.goalroom.service.dto.GoalRoomMemberSortTypeDto;
import co.kirikiri.goalroom.service.dto.request.GoalRoomStatusTypeRequest;
import co.kirikiri.goalroom.service.dto.response.DashBoardCheckFeedResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomCertifiedResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomMemberResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomRoadmapNodeDetailResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomRoadmapNodeResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomRoadmapNodesResponse;
import co.kirikiri.goalroom.service.dto.response.MemberGoalRoomForListResponse;
import co.kirikiri.goalroom.service.dto.response.MemberGoalRoomResponse;
import co.kirikiri.goalroom.service.dto.response.MemberResponse;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapNodeRepository;
import java.net.MalformedURLException;
import java.net.URL;
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
    private RoadmapNodeRepository roadmapNodeRepository;

    @Mock
    private RoadmapContentRepository roadmapContentRepository;

    @Mock
    private FileService fileService;

    @Mock
    private DashBoardCheckFeedService dashBoardCheckFeedService;

    @Mock
    private DashBoardToDoService dashBoardToDoService;

    @InjectMocks
    private GoalRoomReadService goalRoomReadService;

    @Test
    void 골룸_아이디로_골룸_정보를_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent);

        given(goalRoomRepository.findById(any()))
                .willReturn(Optional.of(goalRoom));
        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(roadmapContent));
        given(roadmapNodeRepository.findAllByRoadmapContent(any()))
                .willReturn(roadmapNodes);
        given(goalRoomPendingMemberRepository.findByGoalRoom(any()))
                .willReturn(List.of(new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom, creator.getId())));

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
        given(goalRoomRepository.findById(any()))
                .willReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomReadService.findGoalRoom(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 모집중인_골룸에_대해서_골룸_아이디와_사용자_아이디로_골룸_조회시_참여하는_사용자면_참여여부가_true로_반환된다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent);

        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(1L, GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), goalRoom, creator.getId());

        given(goalRoomRepository.findById(any()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomPendingMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .willReturn(Optional.of(goalRoomPendingMember));
        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(roadmapContent));
        given(roadmapNodeRepository.findAllByRoadmapContent(any()))
                .willReturn(roadmapNodes);
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomPendingMemberRepository.findByGoalRoom(any()))
                .willReturn(List.of(new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom, creator.getId())));

        // when
        final GoalRoomCertifiedResponse goalRoomResponse = goalRoomReadService.findGoalRoom(
                creator.getIdentifier().getValue(), goalRoom.getId());
        final GoalRoomCertifiedResponse expected = 예상하는_로그인된_사용자의_골룸_응답을_생성한다(true, 1);

        // then
        assertThat(goalRoomResponse)
                .isEqualTo(expected);
    }

    @Test
    void 모집중인_골룸에_대해서_골룸_아이디와_사용자_아이디로_골룸_조회시_참여하지_않는_사용자면_참여여부가_false로_반환된다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent);

        given(goalRoomRepository.findById(any()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomPendingMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .willReturn(Optional.empty());
        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(roadmapContent));
        given(roadmapNodeRepository.findAllByRoadmapContent(any()))
                .willReturn(roadmapNodes);
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomPendingMemberRepository.findByGoalRoom(any()))
                .willReturn(List.of(new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom, creator.getId())));

        // when
        final GoalRoomCertifiedResponse goalRoomResponse = goalRoomReadService.findGoalRoom(
                creator.getIdentifier().getValue(), goalRoom.getId());
        final GoalRoomCertifiedResponse expected = 예상하는_로그인된_사용자의_골룸_응답을_생성한다(false, 1);

        // then
        assertThat(goalRoomResponse)
                .isEqualTo(expected);
    }

    @Test
    void 모집중이지_않은_골룸에_대해서_골룸_아이디와_사용자_아이디로_골룸_조회시_참여하는_사용자면_참여여부가_true로_반환된다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent);
        goalRoom.start();

        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), goalRoom, creator.getId());

        given(goalRoomRepository.findById(any()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .willReturn(Optional.of(goalRoomMember));
        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(roadmapContent));
        given(roadmapNodeRepository.findAllByRoadmapContent(any()))
                .willReturn(roadmapNodes);
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));

        // when
        final GoalRoomCertifiedResponse goalRoomResponse = goalRoomReadService.findGoalRoom(
                creator.getIdentifier().getValue(), goalRoom.getId());
        final GoalRoomCertifiedResponse expected = 예상하는_로그인된_사용자의_골룸_응답을_생성한다(true, 0);

        // then
        assertThat(goalRoomResponse)
                .isEqualTo(expected);
    }

    @Test
    void 모집중이지_않은_골룸에_대해서_골룸_아이디와_사용자_아이디로_골룸_조회시_참여하지_않는_사용자면_참여여부가_false로_반환된다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent);
        goalRoom.start();

        given(goalRoomRepository.findById(any()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .willReturn(Optional.empty());
        given(roadmapContentRepository.findById(anyLong()))
                .willReturn(Optional.of(roadmapContent));
        given(roadmapNodeRepository.findAllByRoadmapContent(any()))
                .willReturn(roadmapNodes);
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));

        // when
        final GoalRoomCertifiedResponse goalRoomResponse = goalRoomReadService.findGoalRoom(
                creator.getIdentifier().getValue(), goalRoom.getId());
        final GoalRoomCertifiedResponse expected = 예상하는_로그인된_사용자의_골룸_응답을_생성한다(false, 0);

        // then
        assertThat(goalRoomResponse)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_아이디와_사용자_아이디로_골룸_대기_목록_조회시_골룸_아이디가_유효하지_않으면_예외가_발생한다() {
        // given
        given(goalRoomRepository.findById(any()))
                .willReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomReadService.findGoalRoom("cokirikiri", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 정상적으로_진행중인_골룸의_참여자를_조회한다() throws MalformedURLException {
        //given
        final Member creator = 사용자를_생성한다(1L);
        final Member follower = 사용자를_생성한다(2L);

        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent);
        goalRoom.start();

        final GoalRoomMember goalRoomMemberCreator = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(),
                goalRoom, creator.getId());
        final GoalRoomMember goalRoomMemberFollower = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(),
                goalRoom, follower.getId());

        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomMemberRepository.findByGoalRoomIdOrderedBySortType(anyLong(), any()))
                .willReturn(List.of(goalRoomMemberCreator, goalRoomMemberFollower));
        given(memberRepository.findWithMemberImageById(1L))
                .willReturn(Optional.of(creator));
        given(memberRepository.findWithMemberImageById(2L))
                .willReturn(Optional.of(follower));
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        //when
        final List<GoalRoomMemberResponse> result = goalRoomReadService.findGoalRoomMembers(1L,
                GoalRoomMemberSortTypeDto.ACCOMPLISHMENT_RATE);

        //then
        final GoalRoomMemberResponse expectedGoalRoomMemberResponse1 = new GoalRoomMemberResponse(1L, "name1",
                "http://example.com/serverFilePath", 0.0);
        final GoalRoomMemberResponse expectedGoalRoomMemberResponse2 = new GoalRoomMemberResponse(2L, "name1",
                "http://example.com/serverFilePath", 0.0);
        assertThat(result)
                .isEqualTo(List.of(expectedGoalRoomMemberResponse1, expectedGoalRoomMemberResponse2));
        verify(goalRoomPendingMemberRepository, never()).findByGoalRoomIdOrderedBySortType(anyLong(), any());
    }

    @Test
    void 정상적으로_완료된_골룸의_참여자를_조회한다() throws MalformedURLException {
        //given
        final Member creator = 사용자를_생성한다(1L);
        final Member follower = 사용자를_생성한다(2L);

        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmap.getContents().getValues().get(0));
        goalRoom.complete();

        final GoalRoomMember goalRoomMemberCreator = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(),
                goalRoom, creator.getId());
        final GoalRoomMember goalRoomMemberFollower = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(),
                goalRoom, follower.getId());

        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomMemberRepository.findByGoalRoomIdOrderedBySortType(anyLong(), any()))
                .willReturn(List.of(goalRoomMemberCreator, goalRoomMemberFollower));
        given(memberRepository.findWithMemberImageById(1L))
                .willReturn(Optional.of(creator));
        given(memberRepository.findWithMemberImageById(2L))
                .willReturn(Optional.of(follower));
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        //when
        final List<GoalRoomMemberResponse> result = goalRoomReadService.findGoalRoomMembers(1L,
                GoalRoomMemberSortTypeDto.ACCOMPLISHMENT_RATE);

        //then
        final GoalRoomMemberResponse expectedGoalRoomMemberResponse1 = new GoalRoomMemberResponse(1L, "name1",
                "http://example.com/serverFilePath", 0.0);
        final GoalRoomMemberResponse expectedGoalRoomMemberResponse2 = new GoalRoomMemberResponse(2L, "name1",
                "http://example.com/serverFilePath", 0.0);
        assertThat(result)
                .isEqualTo(List.of(expectedGoalRoomMemberResponse1, expectedGoalRoomMemberResponse2));
        verify(goalRoomPendingMemberRepository, never()).findByGoalRoomIdOrderedBySortType(anyLong(), any());
    }

    @Test
    void 정상적으로_모집중인_골룸의_참여자를_조회한다() throws MalformedURLException {
        //given
        final Member creator = 사용자를_생성한다(1L);
        final Member follower = 사용자를_생성한다(2L);

        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmap.getContents().getValues().get(0));

        final GoalRoomPendingMember goalRoomMemberCreator = new GoalRoomPendingMember(1L, GoalRoomRole.LEADER,
                LocalDateTime.now(), goalRoom, creator.getId());
        final GoalRoomPendingMember goalRoomMemberFollower = new GoalRoomPendingMember(2L, GoalRoomRole.LEADER,
                LocalDateTime.now(), goalRoom, follower.getId());

        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomPendingMemberRepository.findByGoalRoomIdOrderedBySortType(anyLong(), any()))
                .willReturn(List.of(goalRoomMemberCreator, goalRoomMemberFollower));
        given(memberRepository.findWithMemberImageById(1L))
                .willReturn(Optional.of(creator));
        given(memberRepository.findWithMemberImageById(2L))
                .willReturn(Optional.of(follower));
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        //when
        final List<GoalRoomMemberResponse> result = goalRoomReadService.findGoalRoomMembers(1L,
                GoalRoomMemberSortTypeDto.ACCOMPLISHMENT_RATE);

        //then
        final GoalRoomMemberResponse expectedGoalRoomMemberResponse1 = new GoalRoomMemberResponse(1L, "name1",
                "http://example.com/serverFilePath", 0.0);
        final GoalRoomMemberResponse expectedGoalRoomMemberResponse2 = new GoalRoomMemberResponse(2L, "name1",
                "http://example.com/serverFilePath", 0.0);

        assertThat(result)
                .isEqualTo(List.of(expectedGoalRoomMemberResponse1, expectedGoalRoomMemberResponse2));
        verify(goalRoomMemberRepository, never()).findByGoalRoomIdOrderedBySortType(anyLong(), any());
    }

    @Test
    void 존재하지_않는_골룸일_경우_예외를_던진다() {
        //given
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> goalRoomReadService.findGoalRoomMembers(1L,
                GoalRoomMemberSortTypeDto.ACCOMPLISHMENT_RATE))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 진행중인_사용자_단일_골룸을_조회한다() throws MalformedURLException {
        // given
        final RoadmapNode roadmapNode1 = new RoadmapNode(1L, "로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = new RoadmapNode(2L, "로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapNode roadmapNode3 = new RoadmapNode(3L, "로드맵 3주차", "로드맵 3주차 내용");
        final RoadmapNode roadmapNode4 = new RoadmapNode(4L, "로드맵 4주차", "로드맵 4주차 내용");
        final RoadmapNodes roadmapNodes = new RoadmapNodes(
                List.of(roadmapNode1, roadmapNode2, roadmapNode3, roadmapNode4));
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(roadmapNodes);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = new GoalRoomRoadmapNode(
                new Period(TODAY, TODAY.plusDays(10)), 5, roadmapNode1.getId());
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = new GoalRoomRoadmapNode(
                new Period(TODAY.plusDays(11), TODAY.plusDays(20)), 5, roadmapNode2.getId());
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2));

        final Member member = 사용자를_생성한다(1L);
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("goalroom"), new LimitedMemberCount(10),
                roadmapContent.getId(), goalRoomRoadmapNodes);
        goalRoom.start();

        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                member.getId());

        final List<DashBoardCheckFeedResponse> dashBoardCheckFeedResponses = 대시보드_인증_피드_목록_응답을_생성한다();
        given(goalRoomRepository.findByIdWithNodes(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .willReturn(Optional.of(goalRoomLeader));
        given(roadmapContentRepository.findById(any()))
                .willReturn(Optional.of(roadmapContent));
        given(roadmapNodeRepository.findAllByRoadmapContent(any()))
                .willReturn(List.of(roadmapNode1, roadmapNode2, roadmapNode3, roadmapNode4));
        given(dashBoardCheckFeedService.findCheckFeedsByNodeAndGoalRoomStatus(any()))
                .willReturn(dashBoardCheckFeedResponses);
        given(dashBoardToDoService.findMemberCheckedGoalRoomToDoIds(any(), any()))
                .willReturn(Collections.emptyList());
        given(goalRoomMemberRepository.findByGoalRoom(any()))
                .willReturn(List.of(goalRoomLeader));
        given(goalRoomMemberRepository.findLeaderByGoalRoomAndRole(any(), any()))
                .willReturn(Optional.of(goalRoomLeader));

        final MemberGoalRoomResponse expected = new MemberGoalRoomResponse(goalRoom.getName().getValue(),
                goalRoom.getStatus().name(), member.getId(), 1,
                goalRoom.getLimitedMemberCount().getValue(), goalRoom.getStartDate(), goalRoom.getEndDate(),
                roadmapContent.getId(), new GoalRoomRoadmapNodesResponse(false, true,
                List.of(
                        new GoalRoomRoadmapNodeResponse(goalRoomRoadmapNode1.getId(), roadmapNode1.getTitle(),
                                goalRoomRoadmapNode1.getStartDate(),
                                goalRoomRoadmapNode1.getEndDate(), goalRoomRoadmapNode1.getCheckCount()),
                        new GoalRoomRoadmapNodeResponse(goalRoomRoadmapNode2.getId(), roadmapNode2.getTitle(),
                                goalRoomRoadmapNode2.getStartDate(),
                                goalRoomRoadmapNode2.getEndDate(), goalRoomRoadmapNode2.getCheckCount())
                )), Collections.emptyList(),
                List.of(
                        new DashBoardCheckFeedResponse(1L, "http://example.com/serverFilePath", "인증 피드 설명",
                                LocalDate.now()),
                        new DashBoardCheckFeedResponse(2L, "http://example.com/serverFilePath", "인증 피드 설명",
                                LocalDate.now()),
                        new DashBoardCheckFeedResponse(3L, "http://example.com/serverFilePath", "인증 피드 설명",
                                LocalDate.now()),
                        new DashBoardCheckFeedResponse(4L, "http://example.com/serverFilePath", "인증 피드 설명",
                                LocalDate.now())
                ));

        //when
        final MemberGoalRoomResponse response = goalRoomReadService.findMemberGoalRoom("identifier1", 1L);

        //then
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("checkFeeds.id", "checkFeeds.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 모집중인_사용자_단일_골룸_조회시_인증피드가_빈_응답을_반환한다() {
        // given
        final RoadmapNode roadmapNode1 = new RoadmapNode(1L, "로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = new RoadmapNode(2L, "로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapNode roadmapNode3 = new RoadmapNode(3L, "로드맵 3주차", "로드맵 3주차 내용");
        final RoadmapNode roadmapNode4 = new RoadmapNode(4L, "로드맵 4주차", "로드맵 4주차 내용");
        final RoadmapNodes roadmapNodes = new RoadmapNodes(
                List.of(roadmapNode1, roadmapNode2, roadmapNode3, roadmapNode4));
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(roadmapNodes);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = new GoalRoomRoadmapNode(
                new Period(TODAY, TODAY.plusDays(10)), 5, roadmapNode1.getId());
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = new GoalRoomRoadmapNode(
                new Period(TODAY.plusDays(11), TODAY.plusDays(20)), 5, roadmapNode2.getId());
        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = new GoalRoomRoadmapNode(
                new Period(TODAY.plusDays(21), TODAY.plusDays(30)), 5, roadmapNode1.getId());
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = new GoalRoomRoadmapNode(
                new Period(TODAY.plusDays(31), TODAY.plusDays(40)), 5, roadmapNode2.getId());
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2, goalRoomRoadmapNode3, goalRoomRoadmapNode4));

        final Member member = 사용자를_생성한다(1L);
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("goalroom"), new LimitedMemberCount(10),
                roadmapContent.getId(), goalRoomRoadmapNodes);
        final GoalRoomPendingMember goalRoomLeader = new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom,
                member.getId());

        given(goalRoomRepository.findByIdWithNodes(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomPendingMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .willReturn(Optional.of(goalRoomLeader));
        given(roadmapContentRepository.findById(any()))
                .willReturn(Optional.of(roadmapContent));
        given(roadmapNodeRepository.findAllByRoadmapContent(any()))
                .willReturn(List.of(roadmapNode1, roadmapNode2, roadmapNode3, roadmapNode4));
        given(dashBoardCheckFeedService.findCheckFeedsByNodeAndGoalRoomStatus(any()))
                .willReturn(Collections.emptyList());
        given(dashBoardToDoService.findMemberCheckedGoalRoomToDoIds(any(), any()))
                .willReturn(Collections.emptyList());
        given(goalRoomPendingMemberRepository.findByGoalRoom(any()))
                .willReturn(List.of(goalRoomLeader));
        given(goalRoomPendingMemberRepository.findLeaderByGoalRoomAndRole(any(), any()))
                .willReturn(Optional.of(goalRoomLeader));

        final MemberGoalRoomResponse expected = new MemberGoalRoomResponse(goalRoom.getName().getValue(),
                goalRoom.getStatus().name(), member.getId(), 1,
                goalRoom.getLimitedMemberCount().getValue(), goalRoom.getStartDate(), goalRoom.getEndDate(),
                roadmapContent.getId(), new GoalRoomRoadmapNodesResponse(false, true,
                List.of(
                        new GoalRoomRoadmapNodeResponse(goalRoomRoadmapNode1.getId(), roadmapNode1.getTitle(),
                                goalRoomRoadmapNode1.getStartDate(),
                                goalRoomRoadmapNode1.getEndDate(), goalRoomRoadmapNode1.getCheckCount()),
                        new GoalRoomRoadmapNodeResponse(goalRoomRoadmapNode2.getId(), roadmapNode2.getTitle(),
                                goalRoomRoadmapNode2.getStartDate(),
                                goalRoomRoadmapNode2.getEndDate(), goalRoomRoadmapNode2.getCheckCount())
                )), Collections.emptyList(), Collections.emptyList());

        //when
        final MemberGoalRoomResponse response = goalRoomReadService.findMemberGoalRoom("identifier1", 1L);

        //then
        assertThat(response).isEqualTo(expected);
    }

    @Test
    void 종료된_사용자_단일_골룸을_조회시_전체_인증피드를_대상으로_반환한다() throws MalformedURLException {
        // given
        final RoadmapNode roadmapNode1 = new RoadmapNode(1L, "로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = new RoadmapNode(2L, "로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapNode roadmapNode3 = new RoadmapNode(3L, "로드맵 3주차", "로드맵 3주차 내용");
        final RoadmapNode roadmapNode4 = new RoadmapNode(4L, "로드맵 4주차", "로드맵 4주차 내용");
        final RoadmapNodes roadmapNodes = new RoadmapNodes(
                List.of(roadmapNode1, roadmapNode2, roadmapNode3, roadmapNode4));
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(roadmapNodes);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = new GoalRoomRoadmapNode(
                new Period(TODAY, TODAY.plusDays(10)), 5, roadmapNode1.getId());
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = new GoalRoomRoadmapNode(
                new Period(TODAY.plusDays(11), TODAY.plusDays(20)), 5, roadmapNode2.getId());
        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = new GoalRoomRoadmapNode(
                new Period(TODAY.plusDays(21), TODAY.plusDays(30)), 5, roadmapNode1.getId());
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = new GoalRoomRoadmapNode(
                new Period(TODAY.plusDays(31), TODAY.plusDays(40)), 5, roadmapNode2.getId());
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2));

        final Member member = 사용자를_생성한다(1L);
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("goalroom"), new LimitedMemberCount(10),
                roadmapContent.getId(), goalRoomRoadmapNodes);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                member.getId());

        goalRoom.start();
        goalRoom.complete();

        final List<DashBoardCheckFeedResponse> dashBoardCheckFeedResponses = 대시보드_인증_피드_목록_응답을_생성한다();
        given(goalRoomRepository.findByIdWithNodes(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .willReturn(Optional.of(goalRoomLeader));
        given(roadmapContentRepository.findById(any()))
                .willReturn(Optional.of(roadmapContent));
        given(roadmapNodeRepository.findAllByRoadmapContent(any()))
                .willReturn(List.of(roadmapNode1, roadmapNode2, roadmapNode3, roadmapNode4));
        given(dashBoardCheckFeedService.findCheckFeedsByNodeAndGoalRoomStatus(any()))
                .willReturn(dashBoardCheckFeedResponses);
        given(dashBoardToDoService.findMemberCheckedGoalRoomToDoIds(any(), any()))
                .willReturn(Collections.emptyList());
        given(goalRoomMemberRepository.findByGoalRoom(any()))
                .willReturn(List.of(goalRoomLeader));
        given(goalRoomMemberRepository.findLeaderByGoalRoomAndRole(any(), any()))
                .willReturn(Optional.of(goalRoomLeader));

        final MemberGoalRoomResponse expected = new MemberGoalRoomResponse(goalRoom.getName().getValue(),
                goalRoom.getStatus().name(), member.getId(), 1,
                goalRoom.getLimitedMemberCount().getValue(), goalRoom.getStartDate(), goalRoom.getEndDate(),
                roadmapContent.getId(), new GoalRoomRoadmapNodesResponse(false, true,
                List.of(
                        new GoalRoomRoadmapNodeResponse(goalRoomRoadmapNode1.getId(), roadmapNode1.getTitle(),
                                goalRoomRoadmapNode1.getStartDate(),
                                goalRoomRoadmapNode1.getEndDate(), goalRoomRoadmapNode1.getCheckCount()),
                        new GoalRoomRoadmapNodeResponse(goalRoomRoadmapNode2.getId(), roadmapNode2.getTitle(),
                                goalRoomRoadmapNode2.getStartDate(),
                                goalRoomRoadmapNode2.getEndDate(), goalRoomRoadmapNode2.getCheckCount())
                )), Collections.emptyList(),
                List.of(
                        new DashBoardCheckFeedResponse(1L, "http://example.com/serverFilePath", "인증 피드 설명",
                                LocalDate.now()),
                        new DashBoardCheckFeedResponse(2L, "http://example.com/serverFilePath", "인증 피드 설명",
                                LocalDate.now()),
                        new DashBoardCheckFeedResponse(3L, "http://example.com/serverFilePath", "인증 피드 설명",
                                LocalDate.now()),
                        new DashBoardCheckFeedResponse(4L, "http://example.com/serverFilePath", "인증 피드 설명",
                                LocalDate.now())
                ));

        //when
        final MemberGoalRoomResponse response = goalRoomReadService.findMemberGoalRoom("identifier1", 1L);

        //then
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("checkFeeds.id", "checkFeeds.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 사용자_단일_목록_조회_시_유효하지_않은_골룸_아이디일_경우_예외를_반환한다() {
        //given
        given(goalRoomRepository.findByIdWithNodes(anyLong()))
                .willThrow(new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = 1"));

        //when, then
        assertThatThrownBy(() -> goalRoomReadService.findMemberGoalRoom("identifier1", 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("골룸 정보가 존재하지 않습니다. goalRoomId = 1");

    }

    @Test
    void 사용자_단일_목록_조회_시_유효하지_않은_아이디일_경우_예외를_반환한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent);

        given(goalRoomRepository.findByIdWithNodes(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(memberRepository.findByIdentifier(any()))
                .willThrow(new NotFoundException("존재하지 않는 회원입니다."));

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
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent);

        given(goalRoomRepository.findByIdWithNodes(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));

        // when, then
        assertThatThrownBy(() -> goalRoomReadService.findMemberGoalRoom("identifier2", 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("해당 골룸에 참여하지 않은 사용자입니다.");
    }

    @Test
    void 사용자_골룸_목록을_조회한다() throws MalformedURLException {
        // given
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom1 = 골룸을_생성한다(creator, roadmapContent);
        골룸을_생성한다(creator, roadmapContent);
        final GoalRoom goalRoom3 = 골룸을_생성한다(creator, roadmapContent);
        골룸을_생성한다(creator, roadmapContent);

        final Member member = 사용자를_생성한다(2L);
        final GoalRoomPendingMember goalRoom1Leader = new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom1,
                creator.getId());
        final GoalRoomPendingMember goalRoom3Leader = new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom3,
                creator.getId());
        final GoalRoomPendingMember goalRoom1Member = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom1,
                member.getId());
        final GoalRoomPendingMember goalRoom3Member = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom3,
                member.getId());

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findByMemberId(any()))
                .willReturn(List.of(goalRoom1, goalRoom3));
        given(goalRoomPendingMemberRepository.findLeaderByGoalRoomAndRole(goalRoom1, GoalRoomRole.LEADER))
                .willReturn(Optional.of(goalRoom1Leader));
        given(goalRoomPendingMemberRepository.findLeaderByGoalRoomAndRole(goalRoom3, GoalRoomRole.LEADER))
                .willReturn(Optional.of(goalRoom3Leader));
        given(memberRepository.findWithMemberProfileAndImageById(1L))
                .willReturn(Optional.of(creator));
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));
        given(goalRoomPendingMemberRepository.findByGoalRoom(goalRoom1))
                .willReturn(List.of(goalRoom1Leader, goalRoom1Member));
        given(goalRoomPendingMemberRepository.findByGoalRoom(goalRoom3))
                .willReturn(List.of(goalRoom3Leader, goalRoom3Member));

        final List<MemberGoalRoomForListResponse> expected = List.of(
                new MemberGoalRoomForListResponse(1L, "골룸", "RECRUITING", 2, 10, LocalDateTime.now(), TODAY,
                        THIRTY_DAY_LATER, new MemberResponse(creator.getId(), creator.getNickname().getValue(),
                        "http://example.com/serverFilePath")),
                new MemberGoalRoomForListResponse(2L, "골룸", "RECRUITING", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue(),
                                "http://example.com/serverFilePath"))
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

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findByMemberId(any()))
                .willReturn(Collections.emptyList());

        // when
        final List<MemberGoalRoomForListResponse> response = goalRoomReadService.findMemberGoalRooms("identifier1");

        // then
        assertThat(response).isEmpty();
    }

    @Test
    void 사용자_골룸_목록_중_모집_중인_상태만_조회한다() throws MalformedURLException {
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom1 = 골룸을_생성한다(creator, roadmapContent);
        final GoalRoom goalRoom2 = 골룸을_생성한다(creator, roadmapContent);
        final GoalRoom goalRoom3 = 골룸을_생성한다(creator, roadmapContent);
        final GoalRoom goalRoom4 = 골룸을_생성한다(creator, roadmapContent);

        final Member member = 사용자를_생성한다(2L);
        final Long memberId = member.getId();

        final GoalRoomPendingMember goalRoom1Leader = new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom1,
                creator.getId());
        final GoalRoomPendingMember goalRoom2Leader = new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom2,
                creator.getId());
        final GoalRoomPendingMember goalRoom1Member = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom1,
                member.getId());
        final GoalRoomPendingMember goalRoom2Member = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom2,
                member.getId());

        goalRoom3.start();
        goalRoom4.complete();

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findByMemberAndStatus(any(), any()))
                .willReturn(List.of(goalRoom1, goalRoom2));
        given(goalRoomPendingMemberRepository.findLeaderByGoalRoomAndRole(goalRoom1, GoalRoomRole.LEADER))
                .willReturn(Optional.of(goalRoom1Leader));
        given(goalRoomPendingMemberRepository.findLeaderByGoalRoomAndRole(goalRoom3, GoalRoomRole.LEADER))
                .willReturn(Optional.of(goalRoom2Leader));
        given(memberRepository.findWithMemberProfileAndImageById(1L))
                .willReturn(Optional.of(creator));
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));
        given(goalRoomPendingMemberRepository.findByGoalRoom(goalRoom1))
                .willReturn(List.of(goalRoom1Leader, goalRoom1Member));
        given(goalRoomPendingMemberRepository.findByGoalRoom(goalRoom2))
                .willReturn(List.of(goalRoom2Leader, goalRoom2Member));

        final List<MemberGoalRoomForListResponse> expected = List.of(
                new MemberGoalRoomForListResponse(1L, "골룸", "RECRUITING", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue(),
                                "http://example.com/serverFilePath")),
                new MemberGoalRoomForListResponse(2L, "골룸", "RECRUITING", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue(),
                                "http://example.com/serverFilePath"))
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
    void 사용자_골룸_목록_중_진행_중인_상태만_조회한다() throws MalformedURLException {
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom1 = 골룸을_생성한다(creator, roadmapContent);
        final GoalRoom goalRoom2 = 골룸을_생성한다(creator, roadmapContent);
        final GoalRoom goalRoom3 = 골룸을_생성한다(creator, roadmapContent);
        final GoalRoom goalRoom4 = 골룸을_생성한다(creator, roadmapContent);

        final Member member = 사용자를_생성한다(2L);

        goalRoom3.start();
        goalRoom4.start();

        final GoalRoomMember goalRoom3Leader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom3,
                creator.getId());
        final GoalRoomMember goalRoom3Member = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom3,
                member.getId());
        final GoalRoomMember goalRoom4Leader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom4,
                creator.getId());
        final GoalRoomMember goalRoom4Member = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom4,
                member.getId());

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findByMemberAndStatus(any(), any()))
                .willReturn(List.of(goalRoom3, goalRoom4));
        given(memberRepository.findWithMemberProfileAndImageById(1L))
                .willReturn(Optional.of(creator));
        given(goalRoomMemberRepository.findLeaderByGoalRoomAndRole(goalRoom3, GoalRoomRole.LEADER))
                .willReturn(Optional.of(goalRoom3Leader));
        given(goalRoomMemberRepository.findLeaderByGoalRoomAndRole(goalRoom4, GoalRoomRole.LEADER))
                .willReturn(Optional.of(goalRoom4Leader));
        given(goalRoomMemberRepository.findByGoalRoom(goalRoom3))
                .willReturn(List.of(goalRoom3Leader, goalRoom3Member));
        given(goalRoomMemberRepository.findByGoalRoom(goalRoom4))
                .willReturn(List.of(goalRoom4Leader, goalRoom4Member));
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        final List<MemberGoalRoomForListResponse> expected = List.of(
                new MemberGoalRoomForListResponse(3L, "골룸", "RUNNING", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue(),
                                "http://example.com/serverFilePath")),
                new MemberGoalRoomForListResponse(4L, "골룸", "RUNNING", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue(),
                                "http://example.com/serverFilePath"))
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
    void 사용자_골룸_목록_중_종료된_상태만_조회한다() throws MalformedURLException {
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom1 = 골룸을_생성한다(creator, roadmapContent);
        final GoalRoom goalRoom2 = 골룸을_생성한다(creator, roadmapContent);
        final GoalRoom goalRoom3 = 골룸을_생성한다(creator, roadmapContent);
        final GoalRoom goalRoom4 = 골룸을_생성한다(creator, roadmapContent);

        final Member member = 사용자를_생성한다(2L);

        final GoalRoomMember goalRoom3Leader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom3,
                creator.getId());
        final GoalRoomMember goalRoom3Member = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom3,
                member.getId());
        final GoalRoomMember goalRoom4Leader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom4,
                creator.getId());
        final GoalRoomMember goalRoom4Member = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom4,
                member.getId());

        goalRoom3.complete();
        goalRoom4.complete();

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findByMemberAndStatus(any(), any()))
                .willReturn(List.of(goalRoom3, goalRoom4));
        given(memberRepository.findWithMemberProfileAndImageById(1L))
                .willReturn(Optional.of(creator));
        given(goalRoomMemberRepository.findLeaderByGoalRoomAndRole(goalRoom3, GoalRoomRole.LEADER))
                .willReturn(Optional.of(goalRoom3Leader));
        given(goalRoomMemberRepository.findLeaderByGoalRoomAndRole(goalRoom4, GoalRoomRole.LEADER))
                .willReturn(Optional.of(goalRoom4Leader));
        given(goalRoomMemberRepository.findByGoalRoom(goalRoom3))
                .willReturn(List.of(goalRoom3Leader, goalRoom3Member));
        given(goalRoomMemberRepository.findByGoalRoom(goalRoom4))
                .willReturn(List.of(goalRoom4Leader, goalRoom4Member));
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        final List<MemberGoalRoomForListResponse> expected = List.of(
                new MemberGoalRoomForListResponse(3L, "골룸", "COMPLETED", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue(),
                                "http://example.com/serverFilePath")),
                new MemberGoalRoomForListResponse(4L, "골룸", "COMPLETED", 2,
                        10, LocalDateTime.now(), TODAY, THIRTY_DAY_LATER,
                        new MemberResponse(creator.getId(), creator.getNickname().getValue(),
                                "http://example.com/serverFilePath"))
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
    void 골룸의_전체_노드를_조회한다() throws MalformedURLException {
        // given
        final Member creator = 사용자를_생성한다(1L);
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmap.getContents().getValues().get(0));
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());
        given(goalRoomRepository.findByIdWithNodes(1L))
                .willReturn(Optional.of(goalRoom));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomMemberRepository.findByGoalRoomIdAndMemberId(anyLong(), any()))
                .willReturn(Optional.of(goalRoomMember));
        given(roadmapNodeRepository.findById(1L))
                .willReturn(Optional.of(roadmapNodes.get(0)));
        given(roadmapNodeRepository.findById(2L))
                .willReturn(Optional.of(roadmapNodes.get(1)));
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        // when
        final List<GoalRoomRoadmapNodeDetailResponse> responses = goalRoomReadService.findAllGoalRoomNodes(1L,
                "identifier");
        final List<GoalRoomRoadmapNodeDetailResponse> expected = List.of(
                new GoalRoomRoadmapNodeDetailResponse(1L, "로드맵 1주차", "로드맵 1주차 내용",
                        List.of("http://example.com/serverFilePath", "http://example.com/serverFilePath"), TODAY,
                        TEN_DAY_LATER, 10),
                new GoalRoomRoadmapNodeDetailResponse(2L, "로드맵 2주차", "로드맵 2주차 내용",
                        Collections.emptyList(), TWENTY_DAY_LAYER, THIRTY_DAY_LATER, 2)
        );

        // then
        assertThat(responses)
                .isEqualTo(expected);
    }

    @Test
    void 골룸의_노드_조회시_골룸에_참여하지_않은_사용자면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다(1L);
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = 로드맵을_생성한다(creator, roadmapContent);

        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmap.getContents().getValues().get(0));

        given(goalRoomRepository.findByIdWithNodes(1L))
                .willReturn(Optional.of(goalRoom));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomMemberRepository.findByGoalRoomIdAndMemberId(anyLong(), any()))
                .willReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomReadService.findAllGoalRoomNodes(1L, "identifier"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 골룸의_노드_조회시_존재하지_않는_골룸이면_예외가_발생한다() {
        // given
        given(goalRoomRepository.findByIdWithNodes(1L))
                .willReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> goalRoomReadService.findAllGoalRoomNodes(1L, "identifier"))
                .isInstanceOf(NotFoundException.class);
    }

    private Member 크리에이터를_생성한다() {
        final MemberImage memberImage = new MemberImage("originalFileName", "default-member-image",
                ImageContentType.JPG);
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, "kirikiri@email.com");
        return new Member(1L, new Identifier("cokirikiri"), null, new EncryptedPassword(new Password("password1!")),
                new Nickname("코끼리"), memberImage, memberProfile);
    }

    private Member 사용자를_생성한다(final Long id) {
        return new Member(id, new Identifier("identifier1"),
                null, new EncryptedPassword(new Password("password1")), new Nickname("name1"),
                new MemberImage("originalFileName", "serverFilePath", ImageContentType.JPEG),
                new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));
    }

    private Roadmap 로드맵을_생성한다(final Member creator, final RoadmapContent roadmapContent) {
        final RoadmapCategory category = new RoadmapCategory("게임");
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);
        roadmap.addContent(roadmapContent);
        return roadmap;
    }

    private List<RoadmapNode> 로드맵_노드들을_생성한다() {
        final RoadmapNode roadmapNode1 = new RoadmapNode(1L, "로드맵 1주차", "로드맵 1주차 내용");
        roadmapNode1.addImages(new RoadmapNodeImages(노드_이미지들을_생성한다()));
        final RoadmapNode roadmapNode2 = new RoadmapNode(2L, "로드맵 2주차", "로드맵 2주차 내용");
        return List.of(roadmapNode1, roadmapNode2);
    }

    private RoadmapContent 로드맵_본문을_생성한다(final List<RoadmapNode> roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent(1L, "로드맵 본문");
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
        final List<RoadmapNode> roadmapNodes = roadmapContent.getNodes().getValues();

        final RoadmapNode firstRoadmapNode = roadmapNodes.get(0);
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                1L, new Period(TODAY, TEN_DAY_LATER), 10, firstRoadmapNode.getId());

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                2L, new Period(TWENTY_DAY_LAYER, THIRTY_DAY_LATER), 2, secondRoadmapNode.getId());

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));

        return new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10), roadmapContent.getId(),
                goalRoomRoadmapNodes);
    }

    private static GoalRoomResponse 예상하는_골룸_응답을_생성한다() {
        final List<GoalRoomRoadmapNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", TODAY, TEN_DAY_LATER, 10),
                new GoalRoomRoadmapNodeResponse(2L, "로드맵 2주차", TWENTY_DAY_LAYER, THIRTY_DAY_LATER, 2));
        return new GoalRoomResponse("골룸", 1, 10, goalRoomNodeResponses, 31);
    }

    private static GoalRoomCertifiedResponse 예상하는_로그인된_사용자의_골룸_응답을_생성한다(final Boolean isJoined,
                                                                        final int currentMemberCount) {
        final List<GoalRoomRoadmapNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomRoadmapNodeResponse(1L, "로드맵 1주차", TODAY, TEN_DAY_LATER, 10),
                new GoalRoomRoadmapNodeResponse(2L, "로드맵 2주차", TWENTY_DAY_LAYER, THIRTY_DAY_LATER, 2));
        return new GoalRoomCertifiedResponse("골룸", currentMemberCount, 10, goalRoomNodeResponses, 31, isJoined);
    }

    private List<DashBoardCheckFeedResponse> 대시보드_인증_피드_목록_응답을_생성한다() {
        return List.of(
                new DashBoardCheckFeedResponse(1L, "http://example.com/serverFilePath", "인증 피드 설명",
                        LocalDate.now()),
                new DashBoardCheckFeedResponse(2L, "http://example.com/serverFilePath", "인증 피드 설명",
                        LocalDate.now()),
                new DashBoardCheckFeedResponse(3L, "http://example.com/serverFilePath", "인증 피드 설명",
                        LocalDate.now()),
                new DashBoardCheckFeedResponse(4L, "http://example.com/serverFilePath", "인증 피드 설명",
                        LocalDate.now())
        );
    }
}
