package co.kirikiri.service;

import static co.kirikiri.domain.goalroom.GoalRoomStatus.RECRUITING;
import static co.kirikiri.domain.goalroom.GoalRoomStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
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
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoalRoomServiceTest {

    private static final LocalDate TODAY = LocalDate.now();

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @Mock
    private GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    @InjectMocks
    private GoalRoomService goalRoomService;

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
    void 골룸의_시작날짜가_되면_골룸의_상태가_진행중으로_변경된다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom1 = 골룸을_생성한다(targetRoadmapContent, TODAY);
        final GoalRoom goalRoom2 = 골룸을_생성한다(targetRoadmapContent, TODAY.plusDays(10));

        final Member follower1 = 사용자를_생성한다("identifier1", "password2!", "name1", "010-1111-1111");
        final Member follower2 = 사용자를_생성한다("identifier2", "password3!", "name2", "010-1111-1112");
        final Member follower3 = 사용자를_생성한다("identifier3", "password4!", "name3", "010-1111-1113");

        final GoalRoomPendingMember goalRoomPendingMember1 = 골룸_대기자를_생성한다(goalRoom1, follower1);
        final GoalRoomPendingMember goalRoomPendingMember2 = 골룸_대기자를_생성한다(goalRoom1, follower2);
        final GoalRoomPendingMember goalRoomPendingMember3 = 골룸_대기자를_생성한다(goalRoom2, follower3);

        goalRoom1.joinGoalRoom(goalRoomPendingMember1);
        goalRoom1.joinGoalRoom(goalRoomPendingMember2);
        goalRoom2.joinGoalRoom(goalRoomPendingMember3);

        when(goalRoomRepository.findAllByStartDateNow())
                .thenReturn(List.of(goalRoom1));
        when(goalRoomPendingMemberRepository.findAllByGoalRoom(any()))
                .thenReturn(List.of(goalRoomPendingMember1, goalRoomPendingMember2));

        // when
        goalRoomService.startGoalRooms();

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
        final GoalRoom goalRoom1 = 골룸을_생성한다(targetRoadmapContent, TODAY.plusDays(10));
        final GoalRoom goalRoom2 = 골룸을_생성한다(targetRoadmapContent, TODAY.plusDays(10));

        final Member follower1 = 사용자를_생성한다("identifier1", "password2!", "name1", "010-1111-1111");
        final Member follower2 = 사용자를_생성한다("identifier2", "password3!", "name2", "010-1111-1112");
        final Member follower3 = 사용자를_생성한다("identifier3", "password4!", "name3", "010-1111-1113");

        final GoalRoomPendingMember goalRoomPendingMember1 = 골룸_대기자를_생성한다(goalRoom1, follower1);
        final GoalRoomPendingMember goalRoomPendingMember2 = 골룸_대기자를_생성한다(goalRoom1, follower2);
        final GoalRoomPendingMember goalRoomPendingMember3 = 골룸_대기자를_생성한다(goalRoom2, follower3);

        goalRoom1.joinGoalRoom(goalRoomPendingMember1);
        goalRoom1.joinGoalRoom(goalRoomPendingMember2);
        goalRoom2.joinGoalRoom(goalRoomPendingMember3);

        when(goalRoomRepository.findAllByStartDateNow())
                .thenReturn(List.of());

        // when
        goalRoomService.startGoalRooms();

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
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
                new Nickname("코끼리"), "010-1234-5678");
        return new Member(new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), memberProfile);
    }

    private Member 사용자를_생성한다(final String identifier, final String password, final String nickname,
                             final String phoneNumber) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
                new Nickname(nickname), phoneNumber);
        return new Member(new Identifier(identifier),
                new EncryptedPassword(new Password(password)), memberProfile);
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
        final GoalRoom goalRoom = new GoalRoom("골룸", 10, GoalRoomStatus.RECRUITING, roadmapContent);
        final List<RoadmapNode> roadmapNodes = roadmapContent.getNodes().getValues();

        final RoadmapNode firstRoadmapNode = roadmapNodes.get(0);
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                LocalDate.of(2023, 7, 19),
                LocalDate.of(2023, 7, 30), 10, firstRoadmapNode);

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                LocalDate.of(2023, 8, 1),
                LocalDate.of(2023, 8, 5), 2, secondRoadmapNode);

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));
        goalRoom.addGoalRoomRoadmapNodes(goalRoomRoadmapNodes);

        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 15, 12, 0), goalRoom, member);
        goalRoom.joinGoalRoom(goalRoomPendingMember);
        return goalRoom;
    }

    private GoalRoom 골룸을_생성한다(final RoadmapContent roadmapContent, final LocalDate startDate) {
        final GoalRoom goalRoom = new GoalRoom("골룸", 10, RECRUITING, roadmapContent);
        final List<RoadmapNode> roadmapNodes = roadmapContent.getNodes().getValues();

        final RoadmapNode firstRoadmapNode = roadmapNodes.get(0);
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                startDate, startDate.plusDays(10), 10, firstRoadmapNode);

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                startDate.plusDays(11), startDate.plusDays(20), 2, secondRoadmapNode);

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));
        goalRoom.addGoalRoomRoadmapNodes(goalRoomRoadmapNodes);

        return goalRoom;
    }

    private GoalRoomPendingMember 골룸_대기자를_생성한다(final GoalRoom goalRoom, final Member follower) {
        return new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), goalRoom, follower);
    }

    private static GoalRoomResponse 예상하는_골룸_응답을_생성한다() {
        final List<GoalRoomNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomNodeResponse("로드맵 1주차", LocalDate.of(2023, 7, 19),
                        LocalDate.of(2023, 7, 30), 10),
                new GoalRoomNodeResponse("로드맵 2주차", LocalDate.of(2023, 8, 1),
                        LocalDate.of(2023, 8, 5), 2));
        return new GoalRoomResponse("골룸", 1, 10, goalRoomNodeResponses, 17);
    }

    private static GoalRoomCertifiedResponse 예상하는_로그인된_사용자의_골룸_응답을_생성한다(final Boolean isJoined) {
        final List<GoalRoomNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomNodeResponse("로드맵 1주차", LocalDate.of(2023, 7, 19),
                        LocalDate.of(2023, 7, 30), 10),
                new GoalRoomNodeResponse("로드맵 2주차", LocalDate.of(2023, 8, 1),
                        LocalDate.of(2023, 8, 5), 2));
        return new GoalRoomCertifiedResponse("골룸", 1, 10, goalRoomNodeResponses, 17, isJoined);
    }
}
