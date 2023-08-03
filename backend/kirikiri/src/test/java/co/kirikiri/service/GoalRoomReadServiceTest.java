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
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
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
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.CheckFeedRepository;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.service.dto.goalroom.response.CheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.member.response.MemberNameAndImageResponse;
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
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @Mock
    private GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    @Mock
    private CheckFeedRepository checkFeedRepository;

    @InjectMocks
    private GoalRoomReadService goalRoomService;

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
        final GoalRoomResponse goalRoomResponse = goalRoomService.findGoalRoom(goalRoom.getId());
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
        assertThatThrownBy(() -> goalRoomService.findGoalRoom(1L))
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
        final GoalRoomCertifiedResponse goalRoomResponse = goalRoomService.findGoalRoom(
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
        final GoalRoomCertifiedResponse goalRoomResponse = goalRoomService.findGoalRoom(
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
        assertThatThrownBy(() -> goalRoomService.findGoalRoom("cokirikiri", 1L))
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
        final List<GoalRoomMemberResponse> result = goalRoomService.findGoalRoomMembers(1L);

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
        assertThatThrownBy(() -> goalRoomService.findGoalRoomMembers(1L))
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
        final List<GoalRoomCheckFeedResponse> responses = goalRoomService.findGoalRoomCheckFeeds("cokirikiri", 1L);

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
        assertThatThrownBy(() -> goalRoomService.findGoalRoomCheckFeeds("cokirikiri", 1L))
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
        assertThatThrownBy(() -> goalRoomService.findGoalRoomCheckFeeds("cokirikiri", 1L))
                .isInstanceOf(BadRequestException.class);
    }

    private Member 크리에이터를_생성한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), "010-1234-5678");
        return new Member(new Identifier("cokirikiri"),
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
                new Period(TODAY, TEN_DAY_LATER), 10, firstRoadmapNode);

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TWENTY_DAY_LAYER, THIRTY_DAY_LATER), 2, secondRoadmapNode);

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);

        return goalRoom;
    }

    private static GoalRoomResponse 예상하는_골룸_응답을_생성한다() {
        final List<GoalRoomNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomNodeResponse("로드맵 1주차", TODAY, TEN_DAY_LATER, 10),
                new GoalRoomNodeResponse("로드맵 2주차", TWENTY_DAY_LAYER, THIRTY_DAY_LATER, 2));
        return new GoalRoomResponse("골룸", 1, 10, goalRoomNodeResponses, 31);
    }

    private static GoalRoomCertifiedResponse 예상하는_로그인된_사용자의_골룸_응답을_생성한다(final Boolean isJoined) {
        final List<GoalRoomNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomNodeResponse("로드맵 1주차", TODAY, TEN_DAY_LATER, 10),
                new GoalRoomNodeResponse("로드맵 2주차", TWENTY_DAY_LAYER, THIRTY_DAY_LATER, 2));
        return new GoalRoomCertifiedResponse("골룸", 1, 10, goalRoomNodeResponses, 31, isJoined);
    }

    private CheckFeed 인증피드를_생성한다(final String serverFilePath, final String description,
                                 final GoalRoomRoadmapNode goalRoomRoadmapNode, final GoalRoomMember goalRoomMember) {
        return new CheckFeed(serverFilePath, ImageContentType.PNG, "fileName", description, goalRoomRoadmapNode,
                goalRoomMember);
    }
}
