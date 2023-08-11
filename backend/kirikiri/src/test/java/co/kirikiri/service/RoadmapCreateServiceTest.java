package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
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
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapContents;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapReview;
import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.persistence.roadmap.RoadmapReviewRepository;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
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
class RoadmapCreateServiceTest {

    private final Member member = new Member(1L, new Identifier("identifier1"),
            new EncryptedPassword(new Password("password1!")), new Nickname("닉네임"),
            null,
            new MemberProfile(Gender.FEMALE, LocalDate.of(1999, 6, 8), "010-1234-5678"));

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapReviewRepository roadmapReviewRepository;

    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @Mock
    private RoadmapCategoryRepository roadmapCategoryRepository;

    @InjectMocks
    private RoadmapCreateService roadmapService;

    @Test
    void 로드맵을_생성한다() {
        // given
        final String roadmapTitle = "로드맵 제목";
        final String roadmapIntroduction = "로드맵 소개글";
        final String roadmapContent = "로드맵 본문";
        final RoadmapDifficultyType difficulty = RoadmapDifficultyType.DIFFICULT;
        final int requiredPeriod = 30;
        final RoadmapCategory category = new RoadmapCategory(1L, "여가");

        final List<RoadmapNodeSaveRequest> roadmapNodes = List.of(
                new RoadmapNodeSaveRequest("로드맵 노드1 제목", "로드맵 노드1 설명"));
        final List<RoadmapTagSaveRequest> roadmapTags = List.of(new RoadmapTagSaveRequest("태그 1"));
        final RoadmapSaveRequest request = new RoadmapSaveRequest(1L, roadmapTitle, roadmapIntroduction, roadmapContent,
                difficulty, requiredPeriod, roadmapNodes, roadmapTags);

        given(roadmapCategoryRepository.findById(any()))
                .willReturn(Optional.of(category));
        given(roadmapRepository.save(any()))
                .willReturn(new Roadmap(1L, roadmapTitle, roadmapIntroduction, requiredPeriod,
                        RoadmapDifficulty.valueOf(difficulty.name()), member, category));
        when(memberRepository.findByIdentifier(member.getIdentifier()))
                .thenReturn(Optional.of(member));

        // expect
        assertThat(roadmapService.create(request, "identifier1"))
                .isEqualTo(1L);
    }

    @Test
    void 로드맵_생성시_존재하지_않는_회원이면_입력하면_예외가_발생한다() {
        // given
        final RoadmapSaveRequest request = new RoadmapSaveRequest(10L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 노드1", "로드맵 노드1 설명")),
                List.of(new RoadmapTagSaveRequest("태그 1")));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> roadmapService.create(request, "identifier1"))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 로드맵_생성시_존재하지_않는_카테고리를_입력하면_예외가_발생한다() {
        // given
        final RoadmapSaveRequest request = new RoadmapSaveRequest(10L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 노드1", "로드맵 노드1 설명")),
                List.of(new RoadmapTagSaveRequest("태그 1")));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(roadmapCategoryRepository.findById(any()))
                .willReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> roadmapService.create(request, "identifier1"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 로드맵에_대한_리뷰를_추가한다() {
        // given
        final Member follower = new Member(2L, new Identifier("identifier2"),
                new EncryptedPassword(new Password("password1!")), new Nickname("닉네임2"),
                null,
                new MemberProfile(Gender.FEMALE, LocalDate.of(1999, 6, 8), "010-1234-5678"));

        final RoadmapCategory category = new RoadmapCategory(1L, "운동");

        final Roadmap roadmap = 로드맵을_생성한다(member, category);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(member, targetRoadmapContent);

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(goalRoomMemberRepository.findByRoadmapIdAndMemberIdentifierAndGoalRoomStatus(anyLong(), any(), any()))
                .thenReturn(Optional.of(
                        new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom, follower)));
        when(roadmapReviewRepository.findByRoadmapAndMember(any(), any()))
                .thenReturn(Optional.empty());

        final RoadmapReviewSaveRequest roadmapReviewSaveRequest = new RoadmapReviewSaveRequest("최고의 로드맵이네요", 5.0);

        // expected
        assertDoesNotThrow(() -> roadmapService.createReview(1L, "identifier2", roadmapReviewSaveRequest));
    }

    @Test
    void 로드맵_리뷰_작성시_존재하지_않는_로드맵_아이디를_받으면_예외가_발생한다() {
        // given
        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final RoadmapReviewSaveRequest roadmapReviewSaveRequest = new RoadmapReviewSaveRequest("리뷰 내용", null);

        // expected
        assertThatThrownBy(() ->
                roadmapService.createReview(1L, "cokirikiri", roadmapReviewSaveRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 로드맵_리뷰_작성시_완료한_골룸이_없으면_예외가_발생한다() {
        // given
        final RoadmapCategory category = new RoadmapCategory(1L, "운동");

        final Roadmap roadmap = 로드맵을_생성한다(member, category);

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(goalRoomMemberRepository.findByRoadmapIdAndMemberIdentifierAndGoalRoomStatus(anyLong(), any(), any()))
                .thenReturn(Optional.empty());

        final RoadmapReviewSaveRequest roadmapReviewSaveRequest = new RoadmapReviewSaveRequest("리뷰 내용", null);

        // expected
        assertThatThrownBy(() ->
                roadmapService.createReview(1L, "cokirikiri", roadmapReviewSaveRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 로드맵_리뷰_작성시_이미_작성을_완료했으면_예외가_발생한다() {
        // given
        final Member follower = new Member(2L, new Identifier("identifier2"),
                new EncryptedPassword(new Password("password1!")), new Nickname("닉네임2"),
                null,
                new MemberProfile(Gender.FEMALE, LocalDate.of(1999, 6, 8), "010-1234-5678"));

        final RoadmapCategory category = new RoadmapCategory(1L, "운동");

        final Roadmap roadmap = 로드맵을_생성한다(member, category);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(member, targetRoadmapContent);

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(goalRoomMemberRepository.findByRoadmapIdAndMemberIdentifierAndGoalRoomStatus(anyLong(), any(), any()))
                .thenReturn(Optional.of(
                        new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), goalRoom, follower)));
        when(roadmapReviewRepository.findByRoadmapAndMember(any(), any()))
                .thenReturn(Optional.of(new RoadmapReview("로드맵 짱!", 5.0, member)));

        final RoadmapReviewSaveRequest roadmapReviewSaveRequest = new RoadmapReviewSaveRequest("최고의 로드맵이네요", 5.0);

        // expected
        assertThatThrownBy(() ->
                roadmapService.createReview(1L, "cokirikiri", roadmapReviewSaveRequest))
                .isInstanceOf(BadRequestException.class);
    }

    private Roadmap 로드맵을_생성한다(final Member creator, final RoadmapCategory category) {
        final RoadmapContent content = new RoadmapContent("콘텐츠 제목");
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 설명", 100, RoadmapDifficulty.NORMAL, creator, category);
        roadmap.addContent(content);
        return roadmap;
    }

    private GoalRoom 골룸을_생성한다(final Member member, final RoadmapContent roadmapContent) {
        return new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10), roadmapContent, member);
    }
}
