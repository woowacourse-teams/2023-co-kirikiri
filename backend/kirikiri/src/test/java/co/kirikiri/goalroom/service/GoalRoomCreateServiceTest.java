package co.kirikiri.goalroom.service;

import static co.kirikiri.goalroom.domain.GoalRoomStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kirikiri.checkfeed.persistence.CheckFeedRepository;
import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.common.service.FilePathGenerator;
import co.kirikiri.common.service.FileService;
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
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.goalroom.domain.GoalRoom;
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
import co.kirikiri.goalroom.service.dto.request.GoalRoomCreateRequest;
import co.kirikiri.goalroom.service.dto.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.todo.persistence.GoalRoomToDoCheckRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class GoalRoomCreateServiceTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LATER = TODAY.plusDays(20);

    private static final RoadmapNode ROADMAP_NODE = new RoadmapNode(1L, "title", "content");
    private static final RoadmapContent ROADMAP_CONTENT = new RoadmapContent(1L, "content");
    private static final RoadmapContent DELETED_ROADMAP_CONTENT = new RoadmapContent(2L, "content2");
    private static final RoadmapNodes ROADMAP_CONTENTS = new RoadmapNodes(new ArrayList<>(List.of(ROADMAP_NODE)));

    private static final Member MEMBER = new Member(1L, new Identifier("identifier2"), null,
            new EncryptedPassword(new Password("password!2")),
            new Nickname("name2"), null,
            new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

    private static final Roadmap ROADMAP = new Roadmap("roadmap", "introduction", 30, RoadmapDifficulty.DIFFICULT,
            MEMBER, new RoadmapCategory("IT"));

    private static final Roadmap DELETED_ROADMAP = new Roadmap("roadmap", "introduction", 30,
            RoadmapDifficulty.DIFFICULT, RoadmapStatus.DELETED, MEMBER, new RoadmapCategory("IT"));

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

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private GoalRoomCreateService goalRoomCreateService;

    @BeforeAll
    static void setUp() {
        ROADMAP_CONTENT.addNodes(ROADMAP_CONTENTS);
        ROADMAP.addContent(ROADMAP_CONTENT);
        DELETED_ROADMAP.addContent(DELETED_ROADMAP_CONTENT);
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final EncryptedPassword encryptedPassword = new EncryptedPassword(password);
        final Nickname nickname = new Nickname("nickname");
        final String email = "kirikiri@email.com";
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, email);
        member = new Member(identifier, encryptedPassword, nickname, null, memberProfile);
    }

    @Test
    void 정상적으로_골룸을_생성한다() {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));
        final List<GoalRoomRoadmapNode> goalRoomRoadmapNodes = List.of(
                new GoalRoomRoadmapNode(new Period(TODAY, TEN_DAY_LATER), 10, 1L));

        given(roadmapContentRepository.findByIdWithRoadmap(anyLong()))
                .willReturn(Optional.of(ROADMAP_CONTENT));
        given(goalRoomRepository.save(any()))
                .willReturn(new GoalRoom(1L, new GoalRoomName("name"), new LimitedMemberCount(20), 1L,
                        new GoalRoomRoadmapNodes(goalRoomRoadmapNodes)));

        //when
        assertDoesNotThrow(() -> goalRoomCreateService.create(request, member.getIdentifier().getValue()));
    }

    @Test
    void 골룸_생성_시_삭제된_로드맵이면_예외를_던진다() {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));

        given(roadmapContentRepository.findByIdWithRoadmap(anyLong()))
                .willReturn(Optional.of(DELETED_ROADMAP_CONTENT));

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.create(request, member.getIdentifier().getValue()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸_생성_시_존재하지_않은_로드맵_컨텐츠가_들어올때_예외를_던진다() {
        //given
        final GoalRoomCreateRequest request = new GoalRoomCreateRequest(1L, "name",
                20, new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(1L, 10, TODAY, TEN_DAY_LATER))));

        given(roadmapContentRepository.findByIdWithRoadmap(anyLong()))
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
                20, wrongSizeGoalRoomRoadmapNodeRequest);

        given(roadmapContentRepository.findByIdWithRoadmap(anyLong()))
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
                20,
                new ArrayList<>(List.of(new GoalRoomRoadmapNodeRequest(wrongRoadmapNodId, 10, TODAY, TEN_DAY_LATER))));

        given(roadmapContentRepository.findByIdWithRoadmap(anyLong()))
                .willReturn(Optional.of(ROADMAP_CONTENT));

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.create(request, member.getIdentifier().getValue()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸에_참가한다() {
        //given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "시진이", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);
        final Member follower = 사용자를_생성한다(2L, "identifier2", "password1!", "팔로워", "kirikiri1@email");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findGoalRoomByIdWithPessimisticLock(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomPendingMemberRepository.findByGoalRoom(any()))
                .thenReturn(List.of(new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom, creator.getId())));
        when(goalRoomPendingMemberRepository.save(any()))
                .thenReturn(new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom, follower.getId()));

        //when
        //then
        assertDoesNotThrow(() -> goalRoomCreateService.join("identifier2", 1L));
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
        final Member follower = 사용자를_생성한다(1L, "identifier1", "password1!", "팔로워", "kirikiri1@email");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
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
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final int limitedMemberCount = 1;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, limitedMemberCount);
        final Member follower = 사용자를_생성한다(1L, "identifier2", "password1!", "팔로워", "kirikiri1@email");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findGoalRoomByIdWithPessimisticLock(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomPendingMemberRepository.findByGoalRoom(any()))
                .thenReturn(List.of(new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom, creator.getId())));

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
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "kirikiri1@email");
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, roadmapContent, limitedMemberCount);
        final Member follower = 사용자를_생성한다(2L, "identifier2", "password2!", "팔로워", "kirikiri1@email");
        goalRoom.start();

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findGoalRoomByIdWithPessimisticLock(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomPendingMemberRepository.findByGoalRoom(any()))
                .thenReturn(List.of(new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom, creator.getId())));

        //when, then
        assertThatThrownBy(() -> goalRoomCreateService.join("identifier2", 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("모집 중이지 않은 골룸에는 참여할 수 없습니다.");
    }

    @Test
    void 골룸_참가_요청시_이미_챰가한_골룸인_경우_예외가_발생한다() {
        //given
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "kirikiri1@email");
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, roadmapContent, limitedMemberCount);

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomRepository.findGoalRoomByIdWithPessimisticLock(anyLong()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomPendingMemberRepository.findByGoalRoom(any()))
                .thenReturn(List.of(new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom, creator.getId())));

        //when, then
        assertThatThrownBy(() -> goalRoomCreateService.join("identifier1", 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이미 참여한 골룸에는 참여할 수 없습니다.");
    }

    @Test
    void 골룸을_시작한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(creator));
        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomPendingMemberRepository.findByGoalRoom(any()))
                .thenReturn(List.of(new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom, creator.getId())));

        // when
        goalRoomCreateService.startGoalRoom("cokirikiri", 1L);

        // then
        verify(goalRoomMemberRepository, times(1)).saveAllInBatch(any());
        verify(goalRoomPendingMemberRepository, times(1)).deleteAllInBatch(any());

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
        final Member member = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");

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
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
        final Member follower = 사용자를_생성한다(2L, "kirikirico", "password2!", "끼리코", "kirikiri1@email");
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(1L, creator, targetRoadmapContent, 10);

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(any()))
                .thenReturn(Optional.of(goalRoom));
        when(goalRoomPendingMemberRepository.findByGoalRoom(any()))
                .thenReturn(List.of(new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom, creator.getId())));

        // expected
        assertThatThrownBy(() -> goalRoomCreateService.startGoalRoom("identifier", 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸_시작시_시작날짜가_아직_지나지_않았으면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다(1L, "cokirikiri", "password1!", "코끼리", "kirikiri1@email");
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
    void 골룸을_나간다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("골룸"), new LimitedMemberCount(3), 1L,
                골룸_로드맵_노드들을_생성한다());

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(MEMBER));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomPendingMemberRepository.findByGoalRoom(any()))
                .willReturn(List.of(new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom, MEMBER.getId())));

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
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("골룸"), new LimitedMemberCount(3), 1L,
                골룸_로드맵_노드들을_생성한다());

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

    private GoalRoom 시작_날짜가_미래인_골룸을_생성한다(final Long goalRoomId, final Member creator,
                                         final RoadmapContent roadmapContent, final Integer limitedMemberCount) {
        final GoalRoomRoadmapNode goalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TEN_DAY_LATER, TWENTY_DAY_LATER), 5, roadmapContent.getNodes().getValues().get(0).getId());
        return new GoalRoom(goalRoomId, new GoalRoomName("골룸 이름"), new LimitedMemberCount(limitedMemberCount),
                roadmapContent.getId(), new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode)));
    }

    private GoalRoomRoadmapNodes 골룸_로드맵_노드들을_생성한다(final RoadmapNodes roadmapNodes) {
        return new GoalRoomRoadmapNodes(List.of(
                new GoalRoomRoadmapNode(new Period(TODAY, TEN_DAY_LATER), 5, roadmapNodes.getValues().get(0).getId()),
                new GoalRoomRoadmapNode(new Period(TEN_DAY_LATER.plusDays(1), TWENTY_DAY_LATER), 5,
                        roadmapNodes.getValues().get(1).getId()))
        );
    }

    private GoalRoomRoadmapNodes 골룸_로드맵_노드들을_생성한다() {
        return new GoalRoomRoadmapNodes(List.of(
                new GoalRoomRoadmapNode(new Period(TODAY, TEN_DAY_LATER), 5, 1L))
        );
    }
}
