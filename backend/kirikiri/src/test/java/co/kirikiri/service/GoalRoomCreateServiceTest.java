package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
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
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class GoalRoomCreateServiceTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LATER = TODAY.plusDays(20);

    private static final RoadmapNode ROADMAP_NODE = new RoadmapNode(1L, "title", "content");
    private static final RoadmapContent ROADMAP_CONTENT = new RoadmapContent(1L, "content");
    private static final RoadmapNodes ROADMAP_CONTENTS = new RoadmapNodes(new ArrayList<>(List.of(ROADMAP_NODE)));

    private static Member member;

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private RoadmapContentRepository roadmapContentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @Mock
    private CheckFeedRepository checkFeedRepository;

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
        member = new Member(identifier, encryptedPassword, nickname, memberProfile);
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
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent, 20);
        final Member follower = 사용자를_생성한다(2L, "identifier2", "팔로워");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        //when
        goalRoomCreateService.join("identifier2", 1L);

        //then
        assertThat(goalRoom.getCurrentPendingMemberCount())
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
        final Member follower = 사용자를_생성한다(1L, "identifier1", "팔로워");

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
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent, 1);
        final Member follower = 사용자를_생성한다(2L, "identifier2", "팔로워");

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
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent, 20);
        final Member follower = 사용자를_생성한다(2L, "identifier2", "팔로워");
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
    void 인증_피드_등록을_요청한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        goalRoom.addAllGoalRoomMembers(List.of(goalRoomLeader));
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);
        final CheckFeed checkFeed = 인증_피드를_생성한다(goalRoomRoadmapNode, goalRoomLeader);

        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));
        when(checkFeedRepository.isMemberUploadCheckFeedToday(any(), any(), any(), any()))
                .thenReturn(false);
        when(checkFeedRepository.findCountByGoalRoomMemberAndGoalRoomRoadmapNode(any(), any()))
                .thenReturn(goalRoomRoadmapNode.getCheckCount() - 1);
        when(checkFeedRepository.save(any()))
                .thenReturn(checkFeed);
        when(checkFeedRepository.findCountByGoalRoomMember(any()))
                .thenReturn(1);

        // when
        final String response = goalRoomCreateService.createCheckFeed("identifier", 1L, request);
        테스트용으로_생성된_파일을_제거한다(response);

        // then
        assertThat(goalRoomLeader.getAccomplishmentRate()).isEqualTo(100 * 1 / (double) 10);
        assertThat(response).contains("originalFileName");
    }

    @Test
    void 하루에_두_번_이상_인증_피드_등록_요청_시_예외를_반환한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        goalRoom.addAllGoalRoomMembers(List.of(goalRoomLeader));

        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));
        when(checkFeedRepository.isMemberUploadCheckFeedToday(any(), any(), any(), any()))
                .thenReturn(true);

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

        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        goalRoom.addAllGoalRoomMembers(List.of(goalRoomLeader));
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);

        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));
        when(checkFeedRepository.findCountByGoalRoomMemberAndGoalRoomRoadmapNode(any(), any()))
                .thenReturn(goalRoomRoadmapNode.getCheckCount() + 1);

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

        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        goalRoom.addAllGoalRoomMembers(List.of(goalRoomLeader));
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getGoalRoomRoadmapNodes().getValues().get(0);

        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(any(), any()))
                .thenReturn(Optional.of(goalRoomLeader));
        when(checkFeedRepository.isMemberUploadCheckFeedToday(any(), any(), any(), any()))
                .thenReturn(false);
        when(checkFeedRepository.findCountByGoalRoomMemberAndGoalRoomRoadmapNode(any(), any()))
                .thenReturn(goalRoomRoadmapNode.getCheckCount() - 1);

        // when
        assertThatThrownBy(
                () -> goalRoomCreateService.createCheckFeed("identifier", 1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("image/gif는 요청할 수 없는 파일 확장자 형식입니다.");
    }

    @Test
    void 인증_피드_등록_요청_시_존재하지_않는_골룸이라면_예외를_반환한다() {
        // given
        final CheckFeedRequest request = 인증_피드_요청_DTO를_생성한다("image/jpeg");

        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent, 20);
        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        goalRoom.addAllGoalRoomMembers(List.of(goalRoomLeader));

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

        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(creator, targetRoadmapContent, 20);

        final GoalRoomMember goalRoomLeader = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                creator);
        goalRoom.addAllGoalRoomMembers(List.of(goalRoomLeader));

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

    private Member 크리에이터를_생성한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE,
                LocalDate.of(1990, 1, 1), "010-1234-5678");
        return new Member(new Identifier("cokirikiri"), new EncryptedPassword(new Password("password1!")),
                new Nickname("코끼리"), memberProfile);
    }

    private Member 사용자를_생성한다(final Long memberId, final String identifier, final String nickname) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE,
                LocalDate.of(1995, 9, 30), "010-1234-5678");

        return new Member(memberId, new Identifier(identifier), new EncryptedPassword(new Password("password1!")),
                new Nickname(nickname), memberProfile);
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

    private GoalRoom 골룸을_생성한다(final Member creator, final RoadmapContent roadmapContent,
                              final Integer limitedMemberCount) {
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("골룸 이름"), new LimitedMemberCount(limitedMemberCount),
                roadmapContent,
                creator);
        goalRoom.addAllGoalRoomRoadmapNodes(골룸_로드맵_노드들을_생성한다(roadmapContent.getNodes()));
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

    private void 테스트용으로_생성된_파일을_제거한다(final String filePath) {
        final File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid file path: " + filePath);
        }

        if (!file.delete()) {
            throw new RuntimeException("Failed to delete the file: " + filePath);
        }
    }
}