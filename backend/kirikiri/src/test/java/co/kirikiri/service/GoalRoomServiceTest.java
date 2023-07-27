package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.GoalRoom;
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
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class GoalRoomServiceTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LAYER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    private static final RoadmapNode ROADMAP_NODE = new RoadmapNode(1L, "title", "content");
    private static final RoadmapContent ROADMAP_CONTENT = new RoadmapContent(1L, "content");
    private static final RoadmapNodes ROADMAP_CONTENTS = new RoadmapNodes(new ArrayList<>(List.of(ROADMAP_NODE)));

    private static Member member;
    private static MemberProfile memberProfile;

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private RoadmapContentRepository roadmapContentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @InjectMocks
    private GoalRoomService goalRoomService;

    @BeforeAll
    static void setUp() {
        ROADMAP_CONTENT.addNodes(ROADMAP_CONTENTS);
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final EncryptedPassword encryptedPassword = new EncryptedPassword(password);
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";
        memberProfile = new MemberProfile(Gender.MALE, TODAY, phoneNumber);
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
                .willReturn(new GoalRoom(1L, null, null, null));

        //when
        assertDoesNotThrow(() -> goalRoomService.create(request, member.getIdentifier().getValue()));
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
        assertThatThrownBy(() -> goalRoomService.create(request, member.getIdentifier().getValue()))
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
        assertThatThrownBy(() -> goalRoomService.create(request, member.getIdentifier().getValue()))
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
        assertThatThrownBy(() -> goalRoomService.create(request, member.getIdentifier().getValue()))
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
        assertThatThrownBy(() -> goalRoomService.create(request, member.getIdentifier().getValue()))
                .isInstanceOf(NotFoundException.class);
    }

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

    private Member 크리에이터를_생성한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), "010-1234-5678");
        return new Member(new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), new Nickname("코끼리"), memberProfile);
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
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10), roadmapContent);
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

        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 15, 12, 0), goalRoom, member);
        goalRoom.participate(goalRoomPendingMember);
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
}
