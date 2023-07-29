package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
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

@ExtendWith(MockitoExtension.class)
class GoalRoomCreateServiceTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);

    private static final RoadmapNode ROADMAP_NODE = new RoadmapNode(1L, "title", "content");
    private static final RoadmapContent ROADMAP_CONTENT = new RoadmapContent(1L, "content");
    private static final RoadmapNodes ROADMAP_CONTENTS = new RoadmapNodes(new ArrayList<>(List.of(ROADMAP_NODE)));

    private static final Member GOAL_ROOM_MEMBER1 = new Member(new Identifier("identifier2"),
            new EncryptedPassword(new Password("password!2")),
            new Nickname("name2"),
            new MemberProfile(Gender.FEMALE, LocalDate.now(), "010-1111-2222"));
    private static final Member GOAL_ROOM_MEMBER2 = new Member(new Identifier("identifier3"),
            new EncryptedPassword(new Password("password!3")),
            new Nickname("name3"),
            new MemberProfile(Gender.FEMALE, LocalDate.now(), "010-1111-3333"));

    private static Member member;

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @Mock
    private RoadmapContentRepository roadmapContentRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private GoalRoomCreateService goalRoomService;

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
        goalRoomService.join("identifier2", 1L);

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
        assertThatThrownBy(() -> goalRoomService.join("identifier2", 1L))
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
        assertThatThrownBy(() -> goalRoomService.join("identifier1", 1L))
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
        assertThatThrownBy(() -> goalRoomService.join("identifier2", 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("제한 인원이 꽉 찬 골룸에는 참여할 수 없습니다.");
    }

    @Test
    void 골룸_참가_요청시_모집_중이_아닌_경우_예외가_발생한다() {
        //given
        final RoadmapContent roadmapContent = new RoadmapContent("컨텐츠 본문");
        final Member creator = 사용자를_생성한다(1L, "identifier1", "시진이");
        final int limitedMemberCount = 20;
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent, limitedMemberCount);
        final Member follower = 사용자를_생성한다(2L, "identifier2", "팔로워");
        goalRoom.start();

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        //when, then
        assertThatThrownBy(() -> goalRoomService.join("identifier2", 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("모집 중이지 않은 골룸에는 참여할 수 없습니다.");
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
        given(goalRoomPendingMemberRepository.findByGoalRoom(goalRoom))
                .willReturn(List.of(
                        new GoalRoomPendingMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                                GOAL_ROOM_MEMBER1),
                        new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom,
                                GOAL_ROOM_MEMBER2)));

        // when
        goalRoomService.leave("identifier2", 1L);

        // then
        verify(goalRoomPendingMemberRepository, times(1)).delete(any());
    }

    @Test
    void 골룸을_나갈때_존재하지_않는_회원일_경우_예외가_발생한다() {
        // given
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> goalRoomService.leave("identifier2", 1L))
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
        assertThatThrownBy(() -> goalRoomService.leave("identifier2", 1L))
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
        assertThatThrownBy(() -> goalRoomService.leave("identifier2", 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸을_나갈때_골룸이_모집중이면_골룸_대기자_명단에서_제외된다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("골룸"), new LimitedMemberCount(3),
                new RoadmapContent("content"), GOAL_ROOM_MEMBER1);

        final List<GoalRoomPendingMember> goalRoomPendingMembers = List.of(
                new GoalRoomPendingMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                        GOAL_ROOM_MEMBER1),
                new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom,
                        GOAL_ROOM_MEMBER2));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomPendingMemberRepository.findByGoalRoom(goalRoom))
                .willReturn(new ArrayList<>(goalRoomPendingMembers));

        // when
        goalRoomService.leave("identifier2", 1L);

        // then
        verify(goalRoomMemberRepository, never()).findByGoalRoom(goalRoom);
        verify(goalRoomPendingMemberRepository, times(1)).delete(any());
    }

    @Test
    void 골룸을_나갈때_골룸이_완료되었으면_골룸_사용자_명단에서_제외된다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("골룸"), new LimitedMemberCount(3),
                new RoadmapContent("content"), GOAL_ROOM_MEMBER1);
        goalRoom.complete();

        final List<GoalRoomMember> goalRoomMembers = List.of(
                new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                        GOAL_ROOM_MEMBER1),
                new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom,
                        GOAL_ROOM_MEMBER2));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomMemberRepository.findByGoalRoom(goalRoom))
                .willReturn(new ArrayList<>(goalRoomMembers));

        // when
        goalRoomService.leave("identifier2", 1L);

        // then
        verify(goalRoomPendingMemberRepository, never()).findByGoalRoom(goalRoom);
        verify(goalRoomMemberRepository, times(1)).delete(any());
    }

    @Test
    void 모집중인_골룸을_나갈때_골룸에_남아있는_사용자가_없으면_골룸이_삭제된다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("골룸"), new LimitedMemberCount(3),
                new RoadmapContent("content"), GOAL_ROOM_MEMBER1);

        final List<GoalRoomPendingMember> goalRoomPendingMembers = List.of(
                new GoalRoomPendingMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                        GOAL_ROOM_MEMBER1));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomPendingMemberRepository.findByGoalRoom(goalRoom))
                .willReturn(new ArrayList<>(goalRoomPendingMembers));

        // when
        goalRoomService.leave("identifier2", 1L);

        // then
        verify(goalRoomMemberRepository, never()).findByGoalRoom(goalRoom);
        verify(goalRoomMemberRepository, never()).delete(any());
        verify(goalRoomRepository, times(1)).delete(goalRoom);
    }

    @Test
    void 완료된_골룸을_나갈때_골룸에_남아있는_사용자가_없으면_골룸이_삭제된다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("골룸"), new LimitedMemberCount(3),
                new RoadmapContent("content"), GOAL_ROOM_MEMBER1);
        goalRoom.complete();

        final List<GoalRoomMember> goalRoomPendingMembers = List.of(
                new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom,
                        GOAL_ROOM_MEMBER1));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));
        given(goalRoomMemberRepository.findByGoalRoom(goalRoom))
                .willReturn(new ArrayList<>(goalRoomPendingMembers));

        // when
        goalRoomService.leave("identifier2", 1L);

        // then
        verify(goalRoomPendingMemberRepository, never()).findByGoalRoom(goalRoom);
        verify(goalRoomPendingMemberRepository, never()).delete(any());
        verify(goalRoomRepository, times(1)).delete(any());
        verify(goalRoomMemberRepository, times(1)).delete(any());
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
