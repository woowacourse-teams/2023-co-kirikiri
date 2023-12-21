package co.kirikiri.checkfeed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import co.kirikiri.checkfeed.domain.CheckFeed;
import co.kirikiri.checkfeed.persistence.CheckFeedRepository;
import co.kirikiri.checkfeed.service.dto.request.CheckFeedRequest;
import co.kirikiri.checkfeed.service.dto.response.CheckFeedMemberResponse;
import co.kirikiri.checkfeed.service.dto.response.CheckFeedResponse;
import co.kirikiri.checkfeed.service.dto.response.GoalRoomCheckFeedResponse;
import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.ForbiddenException;
import co.kirikiri.common.exception.ImageExtensionException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.common.service.FilePathGenerator;
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
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class GoalRoomCheckFeedServiceTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LATER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @Mock
    private CheckFeedRepository checkFeedRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FileService fileService;

    @Mock
    private FilePathGenerator filePathGenerator;

    @InjectMocks
    private GoalRoomCheckFeedService goalRoomCheckFeedService;

    @Test
    void 인증_피드_등록을_요청한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());
        goalRoomMemberRepository.save(goalRoomLeader);
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);
        final CheckFeed checkFeed = 인증_피드를_생성한다(goalRoomRoadmapNode, goalRoomLeader);

        when(goalRoomRepository.findByIdWithNodes(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));
        when(checkFeedRepository.countByGoalRoomMemberIdAndGoalRoomRoadmapNodeId(any(), any()))
                .thenReturn(0);
        when(checkFeedRepository.findByGoalRoomMemberIdAndDateTime(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(checkFeedRepository.save(any()))
                .thenReturn(checkFeed);
        when(filePathGenerator.makeFilePath(any(), any()))
                .thenReturn("originalFileName.jpeg");
        when(fileService.generateUrl(anyString(), any()))
                .thenReturn(makeUrl("originalFileName.jpeg"));

        // when
        final String response = goalRoomCheckFeedService.createCheckFeed("cokirikiri", 1L, request);

        // then
        assertAll(
                () -> assertThat(goalRoomLeader.getAccomplishmentRate()).isEqualTo(100 / (double) 10),
                () -> assertThat(response).contains("originalFileName")
        );
    }

    @Test
    void 인증_피드_등록시_노드_기간에_해당하지_않으면_예외가_발생한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 시작_날짜가_미래인_골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());
        goalRoomMemberRepository.save(goalRoomLeader);

        when(goalRoomRepository.findByIdWithNodes(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));

        // expected
        assertThatThrownBy(
                () -> goalRoomCheckFeedService.createCheckFeed("cokirikiri", 1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("인증 피드는 노드 기간 내에만 작성할 수 있습니다.");
    }

    @Test
    void 하루에_두_번_이상_인증_피드_등록_요청_시_예외를_반환한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());
        goalRoomMemberRepository.save(goalRoomLeader);
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);
        final CheckFeed checkFeed = 인증_피드를_생성한다(goalRoomRoadmapNode, goalRoomLeader);

        when(goalRoomRepository.findByIdWithNodes(any()))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));
        when(checkFeedRepository.findByGoalRoomMemberIdAndDateTime(any(), any(), any()))
                .thenReturn(Optional.of(checkFeed));

        //expect
        assertThatThrownBy(
                () -> goalRoomCheckFeedService.createCheckFeed("cokirikiri", 1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이미 오늘 인증 피드를 등록하였습니다.");
    }

    @Test
    void 골룸_노드에서_허가된_인증_횟수보다_많은_인증_피드_등록_요청_시_예외를_반환한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());
        goalRoomMemberRepository.save(goalRoomLeader);
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);

        when(goalRoomRepository.findByIdWithNodes(any()))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));
        when(checkFeedRepository.countByGoalRoomMemberIdAndGoalRoomRoadmapNodeId(any(), any()))
                .thenReturn(goalRoomRoadmapNode.getCheckCount());

        //expect
        assertThatThrownBy(
                () -> goalRoomCheckFeedService.createCheckFeed("cokirikiri", 1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이번 노드에는 최대 " + goalRoomRoadmapNode.getCheckCount() + "번만 인증 피드를 등록할 수 있습니다.");
    }

    @Test
    void 인증_피드_등록_요청_시_허용되지_않는_확장자_형식이라면_예외를_반환한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/gif");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());
        goalRoomMemberRepository.save(goalRoomLeader);
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);

        when(goalRoomRepository.findByIdWithNodes(any()))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));

        // when
        assertThatThrownBy(
                () -> goalRoomCheckFeedService.createCheckFeed("cokirikiri", 1L, request))
                .isInstanceOf(ImageExtensionException.class)
                .hasMessage("허용되지 않는 확장자입니다.");
    }

    @Test
    void 인증_피드_등록_요청_시_존재하지_않는_골룸이라면_예외를_반환한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());
        goalRoomMemberRepository.save(goalRoomLeader);

        when(goalRoomRepository.findByIdWithNodes(any()))
                .thenReturn(Optional.empty());

        //expect
        assertThatThrownBy(
                () -> goalRoomCheckFeedService.createCheckFeed("cokirikiri", 1L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 골룸입니다. goalRoomId = 1");
    }

    @Test
    void 인증_피드_등록_요청_시_사용자가_참여하지_않은_골룸이라면_예외를_반환한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);

        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());
        goalRoomMemberRepository.save(goalRoomLeader);

        when(goalRoomRepository.findByIdWithNodes(any()))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.empty());

        //expect
        assertThatThrownBy(
                () -> goalRoomCheckFeedService.createCheckFeed("identifier", 1L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("골룸에 해당 사용자가 존재하지 않습니다. 사용자 아이디 = " + "identifier");
    }

    @Test
    void 진행중인_골룸의_인증피드를_전체_조회한다() throws MalformedURLException {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri1", "password1!", "코끼리1", "kirikiri1@email");
        final Member follower = 사용자를_생성한다(2L, "cokirikiri2", "password2!", "코끼리2", "kirikiri2@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        goalRoom.start();

        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(1L, GoalRoomRole.LEADER, LocalDateTime.now(),
                goalRoom, creator.getId());
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(2L, GoalRoomRole.FOLLOWER, LocalDateTime.now(),
                goalRoom, follower.getId());
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);

        final CheckFeed checkFeed1 = 인증피드를_생성한다("serverFilePath1", "description1", goalRoomRoadmapNode,
                goalRoomMember1);
        final CheckFeed checkFeed2 = 인증피드를_생성한다("serverFilePath2", "description2", goalRoomRoadmapNode,
                goalRoomMember1);
        final CheckFeed checkFeed3 = 인증피드를_생성한다("serverFilePath3", "description3", goalRoomRoadmapNode,
                goalRoomMember2);

        when(goalRoomRepository.findByIdWithNodes(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.of(goalRoomMember1));
        when(goalRoomMemberRepository.findById(1L))
                .thenReturn(Optional.of(goalRoomMember1));
        when(goalRoomMemberRepository.findById(2L))
                .thenReturn(Optional.of(goalRoomMember2));
        when(memberRepository.findWithMemberProfileAndImageById(goalRoomMember1.getMemberId()))
                .thenReturn(Optional.of(creator));
        when(memberRepository.findWithMemberProfileAndImageById(goalRoomMember2.getMemberId()))
                .thenReturn(Optional.of(follower));
        when(checkFeedRepository.findByGoalRoomRoadmapNodeIdOrderByCreatedAtDesc(any()))
                .thenReturn(List.of(checkFeed3, checkFeed2, checkFeed1));
        when(fileService.generateUrl(anyString(), any()))
                .thenReturn(new URL("http://example.com/serverFilePath"));

        // when
        final List<GoalRoomCheckFeedResponse> responses = goalRoomCheckFeedService.findGoalRoomCheckFeeds("cokirikiri",
                1L);

        // then
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse1 = new GoalRoomCheckFeedResponse(
                new CheckFeedMemberResponse(1L, "코끼리1", "http://example.com/serverFilePath"),
                new CheckFeedResponse(1L, "http://example.com/serverFilePath", "description1", LocalDate.now()));
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse2 = new GoalRoomCheckFeedResponse(
                new CheckFeedMemberResponse(1L, "코끼리1", "http://example.com/serverFilePath"),
                new CheckFeedResponse(2L, "http://example.com/serverFilePath", "description2", LocalDate.now()));
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse3 = new GoalRoomCheckFeedResponse(
                new CheckFeedMemberResponse(2L, "코끼리2", "http://example.com/serverFilePath"),
                new CheckFeedResponse(3L, "http://example.com/serverFilePath", "description3", LocalDate.now()));
        final List<GoalRoomCheckFeedResponse> expected = List.of(goalRoomCheckFeedResponse3,
                goalRoomCheckFeedResponse2, goalRoomCheckFeedResponse1);

        assertThat(responses).usingRecursiveComparison()
                .ignoringFields("checkFeed.id", "checkFeed.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 모집중인_골룸의_인증피드를_조회시_빈_값을_반환한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri1", "password1!", "코끼리1", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());

        when(goalRoomRepository.findByIdWithNodes(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.of(goalRoomMember));

        // when
        final List<GoalRoomCheckFeedResponse> responses = goalRoomCheckFeedService.findGoalRoomCheckFeeds("cokirikiri",
                1L);

        // then
        final List<GoalRoomCheckFeedResponse> expected = Collections.emptyList();

        assertThat(responses).isEqualTo(expected);
    }

    @Test
    void 종료된_골룸의_인증피드를_전체_조회시_모든_기간의_인증피드를_대상으로_반환한다() throws MalformedURLException {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri1", "password1!", "코끼리1", "kirikiri1@email");
        final Member follower = 사용자를_생성한다(2L, "cokirikiri2", "password2!", "코끼리2", "kirikiri2@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);
        goalRoom.complete();

        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(1L, GoalRoomRole.LEADER, LocalDateTime.now(),
                goalRoom,
                creator.getId());
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(2L, GoalRoomRole.FOLLOWER, LocalDateTime.now(),
                goalRoom,
                follower.getId());
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);

        final CheckFeed checkFeed1 = 인증피드를_생성한다("serverFilePath1", "description1", goalRoomRoadmapNode,
                goalRoomMember1);
        final CheckFeed checkFeed2 = 인증피드를_생성한다("serverFilePath2", "description2", goalRoomRoadmapNode,
                goalRoomMember1);
        final CheckFeed checkFeed3 = 인증피드를_생성한다("serverFilePath3", "description3", goalRoomRoadmapNode,
                goalRoomMember2);

        when(goalRoomRepository.findByIdWithNodes(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.of(goalRoomMember1));
        when(goalRoomMemberRepository.findById(1L))
                .thenReturn(Optional.of(goalRoomMember1));
        when(goalRoomMemberRepository.findById(2L))
                .thenReturn(Optional.of(goalRoomMember2));
        when(memberRepository.findWithMemberProfileAndImageById(goalRoomMember1.getMemberId()))
                .thenReturn(Optional.of(creator));
        when(memberRepository.findWithMemberProfileAndImageById(goalRoomMember2.getMemberId()))
                .thenReturn(Optional.of(follower));
        given(checkFeedRepository.findByGoalRoomIdOrderByCreatedAtDesc(any()))
                .willReturn(List.of(checkFeed3, checkFeed2, checkFeed1));
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        // when
        final List<GoalRoomCheckFeedResponse> responses = goalRoomCheckFeedService.findGoalRoomCheckFeeds("cokirikiri",
                1L);

        // then
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse1 = new GoalRoomCheckFeedResponse(
                new CheckFeedMemberResponse(1L, "코끼리1", "http://example.com/serverFilePath"),
                new CheckFeedResponse(1L, "http://example.com/serverFilePath", "description1", LocalDate.now()));
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse2 = new GoalRoomCheckFeedResponse(
                new CheckFeedMemberResponse(1L, "코끼리1", "http://example.com/serverFilePath"),
                new CheckFeedResponse(2L, "http://example.com/serverFilePath", "description2", LocalDate.now()));
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse3 = new GoalRoomCheckFeedResponse(
                new CheckFeedMemberResponse(2L, "코끼리2", "http://example.com/serverFilePath"),
                new CheckFeedResponse(3L, "http://example.com/serverFilePath", "description3", LocalDate.now()));
        final List<GoalRoomCheckFeedResponse> expected = List.of(goalRoomCheckFeedResponse3,
                goalRoomCheckFeedResponse2, goalRoomCheckFeedResponse1);

        assertThat(responses).usingRecursiveComparison()
                .ignoringFields("checkFeed.id", "checkFeed.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 골룸의_인증피드를_전체_조회시_현재_진행중인_노드가_없으면_빈_리스트를_반환한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri1", "password1!", "코끼리1", "kirikiri1@email");
        final Member follower = 사용자를_생성한다(2L, "cokirikiri2", "password2!", "코끼리2", "kirikiri2@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 진행중인_노드가_없는_골룸을_생성한다(creator, roadmap.getContents().getValues().get(0));
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator.getId());
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom,
                follower.getId());
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);

        인증피드를_생성한다("serverFilePath1", "description1", goalRoomRoadmapNode, goalRoomMember1);
        인증피드를_생성한다("serverFilePath2", "description2", goalRoomRoadmapNode, goalRoomMember1);
        인증피드를_생성한다("serverFilePath3", "description3", goalRoomRoadmapNode, goalRoomMember2);

        when(goalRoomRepository.findByIdWithNodes(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberId(any(), any()))
                .thenReturn(Optional.of(goalRoomMember1));

        // when
        final List<GoalRoomCheckFeedResponse> responses = goalRoomCheckFeedService.findGoalRoomCheckFeeds("cokirikiri",
                1L);

        // then
        assertThat(responses).isEmpty();
    }

    @Test
    void 골룸의_인증피드를_전체_조회할_때_존재하지_않는_골룸이면_예외가_발생한다() {
        // given
        when(goalRoomRepository.findByIdWithNodes(anyLong()))
                .thenThrow(new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = 1"));

        // when
        // then
        assertThatThrownBy(() -> goalRoomCheckFeedService.findGoalRoomCheckFeeds("cokirikiri", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸의_인증피드를_전체_조회할_때_골룸에_참여하지_않은_회원이면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri1", "password1!", "코끼리1", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 20);

        when(goalRoomRepository.findByIdWithNodes(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomRepository.findByIdWithNodes(anyLong()))
                .thenThrow(new ForbiddenException("골룸에 참여하지 않은 회원입니다."));

        // when
        // then
        assertThatThrownBy(() -> goalRoomCheckFeedService.findGoalRoomCheckFeeds("cokirikiri", 1L))
                .isInstanceOf(ForbiddenException.class);
    }

    private Member 사용자를_생성한다(final Long memberId, final String identifier, final String password, final String nickname,
                             final String email) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, email);
        final MemberImage memberImage = new MemberImage("orifinalFileName", "serveFilePath", ImageContentType.PNG);
        return new Member(memberId, new Identifier(identifier), null, new EncryptedPassword(new Password(password)),
                new Nickname(nickname), memberImage, memberProfile);
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
                new LimitedMemberCount(limitedMemberCount), roadmapContent.getId(), creator.getId());
        goalRoom.addAllGoalRoomRoadmapNodes(골룸_로드맵_노드들을_생성한다(roadmapContent.getNodes()));
        return goalRoom;
    }

    private GoalRoom 시작_날짜가_미래인_골룸을_생성한다(final Long goalRoomId, final Member creator,
                                         final RoadmapContent roadmapContent, final Integer limitedMemberCount) {
        final GoalRoom goalRoom = new GoalRoom(goalRoomId, new GoalRoomName("골룸 이름"),
                new LimitedMemberCount(limitedMemberCount), roadmapContent.getId(), creator.getId());
        final GoalRoomRoadmapNode goalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TEN_DAY_LATER, TWENTY_DAY_LATER), 5, roadmapContent.getNodes().getValues().get(0).getId());
        goalRoom.addAllGoalRoomRoadmapNodes(
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode)));
        return goalRoom;
    }

    private GoalRoom 진행중인_노드가_없는_골룸을_생성한다(final Member member, final RoadmapContent roadmapContent) {
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10),
                roadmapContent.getId(), member.getId());
        final List<RoadmapNode> roadmapNodes = roadmapContent.getNodes().getValues();

        final RoadmapNode firstRoadmapNode = roadmapNodes.get(0);
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                1L, new Period(TEN_DAY_LATER, TWENTY_DAY_LATER), 10, firstRoadmapNode.getId());

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                2L, new Period(THIRTY_DAY_LATER, THIRTY_DAY_LATER.plusDays(10)), 2, secondRoadmapNode.getId());

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        return goalRoom;
    }

    private GoalRoomRoadmapNodes 골룸_로드맵_노드들을_생성한다(final RoadmapNodes roadmapNodes) {
        return new GoalRoomRoadmapNodes(List.of(
                new GoalRoomRoadmapNode(new Period(TODAY, TEN_DAY_LATER), 5, roadmapNodes.getValues().get(0).getId()),
                new GoalRoomRoadmapNode(new Period(TEN_DAY_LATER.plusDays(1), TWENTY_DAY_LATER), 5,
                        roadmapNodes.getValues().get(1).getId()))
        );
    }

    private CheckFeedRequest 인증_피드_요청_DTO를_생성한다(final String contentType) {
        return new CheckFeedRequest(
                new MockMultipartFile("image", "originalFileName.jpeg", contentType,
                        "test image".getBytes()), "인증 피드 설명");
    }

    private CheckFeed 인증_피드를_생성한다(final GoalRoomRoadmapNode goalRoomRoadmapNode, final GoalRoomMember joinedMember) {
        return new CheckFeed("src/test/resources/testImage/originalFileName.jpeg", ImageContentType.JPEG,
                "originalFileName.jpeg", "인증 피드 설명", goalRoomRoadmapNode.getId(), joinedMember.getId());
    }

    private CheckFeed 인증피드를_생성한다(final String serverFilePath, final String description,
                                 final GoalRoomRoadmapNode goalRoomRoadmapNode, final GoalRoomMember goalRoomMember) {
        return new CheckFeed(serverFilePath, ImageContentType.PNG, "fileName", description, goalRoomRoadmapNode.getId(),
                goalRoomMember.getId(), LocalDateTime.now());
    }

    private URL makeUrl(final String path) {
        try {
            return new URL("http://example.com/" + path);
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
