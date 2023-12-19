package co.kirikiri.service;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapContents;
import co.kirikiri.roadmap.domain.RoadmapDifficulty;
import co.kirikiri.roadmap.domain.RoadmapReview;
import co.kirikiri.roadmap.persistence.RoadmapCategoryRepository;
import co.kirikiri.roadmap.persistence.RoadmapRepository;
import co.kirikiri.roadmap.persistence.RoadmapReviewRepository;
import co.kirikiri.roadmap.service.RoadmapCreateService;
import co.kirikiri.roadmap.service.RoadmapGoalRoomService;
import co.kirikiri.roadmap.service.dto.request.RoadmapCategorySaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapDifficultyType;
import co.kirikiri.roadmap.service.dto.request.RoadmapNodeSaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapReviewSaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapSaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapTagSaveRequest;
import co.kirikiri.service.exception.AuthenticationException;
import co.kirikiri.service.exception.BadRequestException;
import co.kirikiri.service.exception.ConflictException;
import co.kirikiri.service.exception.ForbiddenException;
import co.kirikiri.service.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoadmapCreateServiceTest {

    private static final Member MEMBER = new Member(1L, new Identifier("identifier1"),
            null, new EncryptedPassword(new Password("password1!")), new Nickname("닉네임"),
            null,
            new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapReviewRepository roadmapReviewRepository;

    @Mock
    private RoadmapCategoryRepository roadmapCategoryRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private RoadmapGoalRoomService roadmapGoalRoomService;

    @InjectMocks
    private RoadmapCreateService roadmapCreateService;

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
                new RoadmapNodeSaveRequest("로드맵 노드1 제목", "로드맵 노드1 설명", Collections.emptyList()));
        final List<RoadmapTagSaveRequest> roadmapTags = List.of(new RoadmapTagSaveRequest("태그 1"));
        final RoadmapSaveRequest request = new RoadmapSaveRequest(1L, roadmapTitle, roadmapIntroduction, roadmapContent,
                difficulty, requiredPeriod, roadmapNodes, roadmapTags);

        given(roadmapCategoryRepository.findById(any()))
                .willReturn(Optional.of(category));
        given(roadmapRepository.save(any()))
                .willReturn(new Roadmap(1L, roadmapTitle, roadmapIntroduction, requiredPeriod,
                        RoadmapDifficulty.valueOf(difficulty.name()), MEMBER, category));
        when(memberRepository.findByIdentifier(MEMBER.getIdentifier()))
                .thenReturn(Optional.of(MEMBER));

        // expect
        assertDoesNotThrow(() -> roadmapCreateService.create(request, "identifier1"));
    }

    @Test
    void 로드맵_생성시_존재하지_않는_회원이면_입력하면_예외가_발생한다() {
        // given
        final RoadmapSaveRequest request = new RoadmapSaveRequest(10L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 노드1", "로드맵 노드1 설명", Collections.emptyList())),
                List.of(new RoadmapTagSaveRequest("태그 1")));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> roadmapCreateService.create(request, "identifier1"))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 로드맵_생성시_존재하지_않는_카테고리를_입력하면_예외가_발생한다() {
        // given
        final RoadmapSaveRequest request = new RoadmapSaveRequest(10L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 노드1", "로드맵 노드1 설명", Collections.emptyList())),
                List.of(new RoadmapTagSaveRequest("태그 1")));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(MEMBER));
        given(roadmapCategoryRepository.findById(any()))
                .willReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> roadmapCreateService.create(request, "identifier1"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 로드맵에_대한_리뷰를_추가한다() {
        // given
        final Member follower = new Member(2L, new Identifier("identifier2"),
                null, new EncryptedPassword(new Password("password1!")), new Nickname("닉네임2"),
                null,
                new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

        final RoadmapCategory category = new RoadmapCategory(1L, "운동");

        final Roadmap roadmap = 로드맵을_생성한다(MEMBER, category);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(roadmapGoalRoomService.findCompletedGoalRoomMember(anyLong(), any()))
                .thenReturn(follower);
        when(roadmapReviewRepository.findByRoadmapAndMember(any(), any()))
                .thenReturn(Optional.empty());

        final RoadmapReviewSaveRequest roadmapReviewSaveRequest = new RoadmapReviewSaveRequest("최고의 로드맵이네요", 5.0);

        // expected
        assertDoesNotThrow(() -> roadmapCreateService.createReview(1L, "identifier2", roadmapReviewSaveRequest));
    }

    @Test
    void 로드맵_리뷰_작성시_존재하지_않는_로드맵_아이디를_받으면_예외가_발생한다() {
        // given
        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final RoadmapReviewSaveRequest roadmapReviewSaveRequest = new RoadmapReviewSaveRequest("리뷰 내용", null);

        // expected
        assertThatThrownBy(() ->
                roadmapCreateService.createReview(1L, "cokirikiri", roadmapReviewSaveRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 로드맵_리뷰_작성시_완료한_골룸이_없으면_예외가_발생한다() {
        // given
        final RoadmapCategory category = new RoadmapCategory(1L, "운동");

        final Roadmap roadmap = 로드맵을_생성한다(MEMBER, category);

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(roadmapGoalRoomService.findCompletedGoalRoomMember(anyLong(), any()))
                .thenThrow(new BadRequestException("Error Message"));

        final RoadmapReviewSaveRequest roadmapReviewSaveRequest = new RoadmapReviewSaveRequest("리뷰 내용", null);

        // expected
        assertThatThrownBy(() ->
                roadmapCreateService.createReview(1L, "cokirikiri", roadmapReviewSaveRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 로드맵_리뷰_작성시_이미_작성을_완료했으면_예외가_발생한다() {
        // given
        final Member follower = new Member(2L, new Identifier("identifier2"),
                null, new EncryptedPassword(new Password("password1!")), new Nickname("닉네임2"),
                null,
                new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

        final RoadmapCategory category = new RoadmapCategory(1L, "운동");

        final Roadmap roadmap = 로드맵을_생성한다(MEMBER, category);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(roadmapGoalRoomService.findCompletedGoalRoomMember(anyLong(), any()))
                .thenReturn(follower);
        when(roadmapReviewRepository.findByRoadmapAndMember(any(), any()))
                .thenReturn(Optional.of(new RoadmapReview("로드맵 짱!", 5.0, MEMBER)));

        final RoadmapReviewSaveRequest roadmapReviewSaveRequest = new RoadmapReviewSaveRequest("최고의 로드맵이네요", 5.0);

        // expected
        assertThatThrownBy(() -> roadmapCreateService.createReview(1L, "cokirikiri", roadmapReviewSaveRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸이_생성된_적이_없는_로드맵을_삭제한다() {
        // given
        final RoadmapCategory category = new RoadmapCategory(1L, "운동");
        final Roadmap roadmap = 로드맵을_생성한다(MEMBER, category);

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(roadmapRepository.findByIdAndMemberIdentifier(anyLong(), anyString()))
                .thenReturn(Optional.of(roadmap));
        when(roadmapGoalRoomService.hasGoalRooms(roadmap))
                .thenReturn(false);

        // when
        // then
        assertDoesNotThrow(() -> roadmapCreateService.deleteRoadmap("identifier1", 1L));
        verify(roadmapRepository, times(1)).delete(any());
    }

    @Test
    void 골룸이_생성된_적이_있는_로드맵을_삭제한다() {
        // given
        final Member follower = new Member(2L, new Identifier("identifier2"),
                null, new EncryptedPassword(new Password("password1!")), new Nickname("닉네임2"), null,
                new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

        final RoadmapCategory category = new RoadmapCategory(1L, "운동");
        final Roadmap roadmap = 로드맵을_생성한다(MEMBER, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(roadmapRepository.findByIdAndMemberIdentifier(anyLong(), anyString()))
                .thenReturn(Optional.of(roadmap));
        when(roadmapGoalRoomService.hasGoalRooms(roadmap))
                .thenReturn(true);

        // when
        // then
        assertDoesNotThrow(() -> roadmapCreateService.deleteRoadmap("identifier1", 1L));
        assertThat(roadmap.isDeleted()).isTrue();
        verify(roadmapRepository, never()).delete(any());
    }

    @Test
    void 로드맵을_삭제할_때_존재하지_않는_로드맵인_경우_예외가_발생한다() {
        // given
        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> roadmapCreateService.deleteRoadmap("identifier1", 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 로드맵입니다. roadmapId = 1");
    }

    @Test
    void 로드맵을_삭제할_때_자신이_생성한_로드맵이_아니면_예외가_발생한다() {
        // given
        final RoadmapCategory category = new RoadmapCategory(1L, "운동");
        final Roadmap roadmap = 로드맵을_생성한다(MEMBER, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(roadmapRepository.findByIdAndMemberIdentifier(anyLong(), anyString()))
                .thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> roadmapCreateService.deleteRoadmap("identifier2", 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("해당 로드맵을 생성한 사용자가 아닙니다.");
    }

    @Test
    void 정상적으로_로드맵_카테고리를_생성한다() {
        //given
        final RoadmapCategorySaveRequest category = new RoadmapCategorySaveRequest("운동");

        when(roadmapCategoryRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        //when
        //then
        assertDoesNotThrow(() -> roadmapCreateService.createRoadmapCategory(category));
    }

    @Test
    void 로드맵_카테고리_생성_시_중복될_이름일_경우_예외를_던진다() {
        //given
        final RoadmapCategorySaveRequest category = new RoadmapCategorySaveRequest("운동");

        when(roadmapCategoryRepository.findByName(anyString()))
                .thenReturn(Optional.of(new RoadmapCategory("운동")));

        //when
        //then
        assertThatThrownBy(() -> roadmapCreateService.createRoadmapCategory(category))
                .isInstanceOf(ConflictException.class);
    }

    private Roadmap 로드맵을_생성한다(final Member creator, final RoadmapCategory category) {
        final RoadmapContent content = new RoadmapContent("콘텐츠 제목");
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 설명", 100, RoadmapDifficulty.NORMAL, creator, category);
        roadmap.addContent(content);
        return roadmap;
    }
}
