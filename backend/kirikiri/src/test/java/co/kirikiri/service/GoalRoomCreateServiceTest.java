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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
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
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
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
class GoalRoomCreateServiceTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);

    private static final RoadmapNode ROADMAP_NODE = new RoadmapNode(1L, "title", "content");
    private static final RoadmapContent ROADMAP_CONTENT = new RoadmapContent(1L, "content");
    private static final RoadmapNodes ROADMAP_CONTENTS = new RoadmapNodes(new ArrayList<>(List.of(ROADMAP_NODE)));

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
        final RoadmapContent roadmapContent = new RoadmapContent("컨텐츠 본문");
        final Member creator = 사용자를_생성한다(1L, "identifier1", "시진이");
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent, limitedMemberCount);
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
        final RoadmapContent roadmapContent = new RoadmapContent("컨텐츠 본문");
        final Member creator = 사용자를_생성한다(1L, "identifier1", "시진이");
        final int limitedMemberCount = 1;
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent, limitedMemberCount);
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
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Member creator = 사용자를_생성한다(1L, "identifier1", "password1!", "시진이", "010-1111-1111");
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(creator, limitedMemberCount, roadmapContent, TODAY);
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
        final RoadmapContent roadmapContent = new RoadmapContent("컨텐츠 본문");
        final Member creator = 사용자를_생성한다(1L, "identifier1", "시진이");
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent, 20);

        goalRoom.addGoalRoomTodo(new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY, TEN_DAY_LATER);

        //when
        //then
        assertDoesNotThrow(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest));
    }

    @Test
    void 골룸에_투두리스트_추가시_회원을_찾지_못할_경우_예외를_던진다() {
        //given
        final RoadmapContent roadmapContent = new RoadmapContent("컨텐츠 본문");
        final Member creator = 사용자를_생성한다(1L, "identifier1", "시진이");

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY, TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_골룸을_찾지_못할_경우_예외를_던진다() {
        //given
        final RoadmapContent roadmapContent = new RoadmapContent("컨텐츠 본문");
        final Member creator = 사용자를_생성한다(1L, "identifier1", "시진이");
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent, 20);

        goalRoom.addGoalRoomTodo(new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY, TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_종료된_골룸일_경우_예외를_던진다() {
        //given
        final RoadmapContent roadmapContent = new RoadmapContent("컨텐츠 본문");
        final Member creator = 사용자를_생성한다(1L, "identifier1", "시진이");
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent, 20);

        goalRoom.addGoalRoomTodo(new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));
        goalRoom.complete();

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY, TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_리더가_아닐_경우_예외를_던진다() {
        //given
        final RoadmapContent roadmapContent = new RoadmapContent("컨텐츠 본문");
        final Member creator = 사용자를_생성한다(1L, "identifier1", "시진이");
        final Member member = 사용자를_생성한다(2L, "identifier2", "멤버");
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent, 20);

        goalRoom.addGoalRoomTodo(new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest("goalRoomContent", TODAY, TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier2", goalRoomTodoRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸에_투두리스트_추가시_골룸_컨텐츠가_250글자가_넘을때_예외를_던진다() {
        //given
        final RoadmapContent roadmapContent = new RoadmapContent("컨텐츠 본문");
        final Member creator = 사용자를_생성한다(1L, "identifier1", "시진이");
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent, 20);

        goalRoom.addGoalRoomTodo(new GoalRoomToDo(new GoalRoomTodoContent("goalRoomTodoContent"), new Period(TODAY, TEN_DAY_LATER)));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(creator));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        final String goalRoomTodoContent = "a".repeat(251);
        final GoalRoomTodoRequest goalRoomTodoRequest = new GoalRoomTodoRequest(goalRoomTodoContent, TODAY, TEN_DAY_LATER);

        //when
        //then
        assertThatThrownBy(() -> goalRoomCreateService.addGoalRoomTodo(1L, "identifier1", goalRoomTodoRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸의_시작날짜가_되면_골룸의_상태가_진행중으로_변경된다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(creator, 10, targetRoadmapContent, TODAY);
        final GoalRoom goalRoom2 = 골룸을_생성한다(creator, 10, targetRoadmapContent, TODAY.plusDays(10));

        final Member follower1 = 사용자를_생성한다(1L, "identifier1", "password2!", "name1", "010-1111-1111");
        final Member follower2 = 사용자를_생성한다(2L, "identifier2", "password3!", "name2", "010-1111-1112");
        final Member follower3 = 사용자를_생성한다(3L, "identifier3", "password4!", "name3", "010-1111-1113");

        final GoalRoomPendingMember goalRoomPendingMember = 골룸_대기자를_생성한다(goalRoom2, creator, GoalRoomRole.LEADER);
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
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(creator, 10, targetRoadmapContent, TODAY.plusDays(10));
        final GoalRoom goalRoom2 = 골룸을_생성한다(creator, 10, targetRoadmapContent, TODAY.plusDays(10));

        final Member follower1 = 사용자를_생성한다(1L, "identifier1", "password2!", "name1", "010-1111-1111");
        final Member follower2 = 사용자를_생성한다(2L, "identifier2", "password3!", "name2", "010-1111-1112");
        final Member follower3 = 사용자를_생성한다(3L, "identifier3", "password4!", "name3", "010-1111-1113");

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

    private Member 크리에이터를_생성한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), "010-1234-5678");
        return new Member(new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), new Nickname("코끼리"), memberProfile);
    }

    private Member 사용자를_생성한다(final Long memberId, final String identifier, final String password, final String nickname,
                             final String phoneNumber) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE,
                LocalDate.of(1995, 9, 30), phoneNumber);

        return new Member(memberId, new Identifier(identifier), new EncryptedPassword(new Password(password)),
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

    private GoalRoom 골룸을_생성한다(final Member member, final int limitedMemberCount, final RoadmapContent roadmapContent,
                              final LocalDate startDate) {
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(limitedMemberCount),
                roadmapContent,
                member);
        final List<RoadmapNode> roadmapNodes = roadmapContent.getNodes().getValues();

        final RoadmapNode firstRoadmapNode = roadmapNodes.get(0);
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(startDate, startDate.plusDays(10)), 10, firstRoadmapNode);

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(startDate.plusDays(11), startDate.plusDays(20)), 2, secondRoadmapNode);

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);

        return goalRoom;
    }

    private GoalRoomPendingMember 골룸_대기자를_생성한다(final GoalRoom goalRoom, final Member follower,
                                               final GoalRoomRole role) {
        return new GoalRoomPendingMember(role, LocalDateTime.of(2023, 7, 19, 12, 0, 0), goalRoom, follower);
    }

    private Member 사용자를_생성한다(final Long memberId, final String identifier, final String nickname) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE,
                LocalDate.of(1995, 9, 30), "010-1234-5678");

        return new Member(memberId, new Identifier(identifier), new EncryptedPassword(new Password("password1!")),
                new Nickname(nickname), memberProfile);
    }

    private GoalRoom 골룸을_생성한다(final Member creator, final RoadmapContent roadmapContent,
                              final Integer limitedMemberCount) {
        return new GoalRoom(new GoalRoomName("골룸 이름"), new LimitedMemberCount(limitedMemberCount), roadmapContent,
                creator);
    }
}
