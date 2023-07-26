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
import co.kirikiri.domain.roadmap.RoadmapReview;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.persistence.roadmap.RoadmapReviewRepository;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.RoadmapFilterTypeRequest;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class RoadmapServiceTest {

    private final Member member = new Member(2L, new Identifier("identifier1"),
            new EncryptedPassword(new Password("password1!")),
            new MemberProfile(Gender.FEMALE, LocalDate.of(1999, 6, 8), new Nickname("닉네임"), "010-1234-5678"));

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapReviewRepository roadmapReviewRepository;

    @Mock
    private RoadmapContentRepository roadmapContentRepository;

    @Mock
    private GoalRoomMemberRepository goalRoomMemberRepository;

    @Mock
    private RoadmapCategoryRepository roadmapCategoryRepository;

    @InjectMocks
    private RoadmapService roadmapService;

    @Test
    void 존재하지_않는_카테고리를_입력하면_예외가_발생한다() {
        // given
        final RoadmapSaveRequest request = new RoadmapSaveRequest(10L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 노드1", "로드맵 노드1 설명")));

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(roadmapCategoryRepository.findById(any()))
                .willReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> roadmapService.create(request, "identifier1"))
                .isInstanceOf(NotFoundException.class);
    }

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
        final RoadmapSaveRequest request = new RoadmapSaveRequest(1L, roadmapTitle, roadmapIntroduction, roadmapContent,
                difficulty, requiredPeriod, roadmapNodes);

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
    void 로드맵에_대한_리뷰를_추가한다() {
        // given
        final Member creator = 사용자를_생성한다("크리에이터", "cokirikiri");
        final RoadmapCategory category = 로드맵_카테고리를_생성한다("운동");

        final Roadmap roadmap = 로드맵을_생성한다(creator, category);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent);
        goalRoom.complete();
        final List<GoalRoomMember> goalRoomMembers = 사용자가_참여한_특정_로드맵의_골룸_멤버_목록을_생성한다(goalRoom);

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(goalRoomMemberRepository.findByRoadmapIdAndMemberIdentifierAndGoalRoomStatus(anyLong(), any(), any()))
                .thenReturn(goalRoomMembers);
        when(roadmapReviewRepository.findByRoadmapAndMember(any(), any()))
                .thenReturn(Optional.empty());

        final RoadmapReviewSaveRequest roadmapReviewSaveRequest = new RoadmapReviewSaveRequest("최고의 로드맵이네요", 5.0);

        // expected
        assertDoesNotThrow(() -> roadmapService.createReview(1L, "cokirikiri", roadmapReviewSaveRequest));
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
        final Member creator = 사용자를_생성한다("크리에이터", "cokirikiri");
        final RoadmapCategory category = 로드맵_카테고리를_생성한다("운동");

        final Roadmap roadmap = 로드맵을_생성한다(creator, category);

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(goalRoomMemberRepository.findByRoadmapIdAndMemberIdentifierAndGoalRoomStatus(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());

        final RoadmapReviewSaveRequest roadmapReviewSaveRequest = new RoadmapReviewSaveRequest("리뷰 내용", null);

        // expected
        assertThatThrownBy(() ->
                roadmapService.createReview(1L, "cokirikiri", roadmapReviewSaveRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 로드맵_리뷰_작성시_이미_작성을_완료했으면_예외가_발생한다() {
        // given
        final Member creator = 사용자를_생성한다("크리에이터", "cokirikiri");
        final RoadmapCategory category = 로드맵_카테고리를_생성한다("운동");

        final Roadmap roadmap = 로드맵을_생성한다(creator, category);
        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent);

        final List<GoalRoomMember> goalRoomMembers = 사용자가_참여한_특정_로드맵의_골룸_멤버_목록을_생성한다(goalRoom);

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(goalRoomMemberRepository.findByRoadmapIdAndMemberIdentifierAndGoalRoomStatus(anyLong(), any(), any()))
                .thenReturn(goalRoomMembers);
        when(roadmapReviewRepository.findByRoadmapAndMember(any(), any()))
                .thenReturn(Optional.of(new RoadmapReview("로드맵 짱!", 5.0, member)));

        final RoadmapReviewSaveRequest roadmapReviewSaveRequest = new RoadmapReviewSaveRequest("최고의 로드맵이네요", 5.0);

        // expected
        assertThatThrownBy(() ->
                roadmapService.createReview(1L, "cokirikiri", roadmapReviewSaveRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 특정_아이디를_가지는_로드맵_단일_조회시_해당_로드맵의_정보를_반환한다() {
        //given
        final Member member = 사용자를_생성한다("썬샷", "identifier1");
        final RoadmapCategory category = 로드맵_카테고리를_생성한다("운동");
        final RoadmapContent content = 로드맵_컨텐츠를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(member, category);
        roadmap.addContent(content);
        final Long roadmapId = 1L;

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(any()))
                .thenReturn(Optional.of(content));

        //when
        final RoadmapResponse roadmapResponse = roadmapService.findRoadmap(roadmapId);

        //then
        final RoadmapResponse expectedResponse = new RoadmapResponse(
                roadmapId,
                new RoadmapCategoryResponse(1L, "운동"),
                "로드맵 제목",
                "로드맵 설명",
                new MemberResponse(1L, "썬샷"),
                new RoadmapContentResponse("콘텐츠 제목", Collections.emptyList()),
                "NORMAL",
                100
        );

        assertThat(roadmapResponse)
                .usingRecursiveComparison()
                .ignoringFields("roadmapId")
                .isEqualTo(expectedResponse);
    }

    @Test
    void 로드맵_단일_조회_시_로드맵_아이디가_존재하지_않는_아이디일_경우_예외를_반환한다() {
        //when
        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> roadmapService.findRoadmap(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 로드맵_목록_조회시_카테고리_아이디가_유효하지_않으면_예외가_발생한다() {
        // given
        when(roadmapCategoryRepository.findById(any()))
                .thenReturn(Optional.empty());

        final Long categoryId = 1L;
        final RoadmapFilterTypeRequest filterType = RoadmapFilterTypeRequest.LATEST;
        final CustomPageRequest pageRequest = new CustomPageRequest(1, 10);

        // expected
        assertThatThrownBy(() -> roadmapService.findRoadmapsByFilterType(categoryId, filterType, pageRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 로드맵_목록_조회_시_필터_조건이_null이면_최신순으로_조회한다() {
        // given
        final List<Roadmap> roadmaps = List.of(제목별로_로드맵을_생성한다("첫 번째 로드맵"), 제목별로_로드맵을_생성한다("두 번째 로드맵"));
        final PageImpl<Roadmap> roadmapPages = new PageImpl<>(roadmaps, PageRequest.of(0, 10), roadmaps.size());

        when(roadmapCategoryRepository.findById(any()))
                .thenReturn(Optional.of(new RoadmapCategory("여행")));
        when(roadmapRepository.findRoadmapPagesByCond(any(), any(), any()))
                .thenReturn(roadmapPages);

        final Long categoryId = 1L;
        final RoadmapFilterTypeRequest filterType = null;
        final CustomPageRequest pageRequest = new CustomPageRequest(1, 10);

        // when
        final PageResponse<RoadmapResponse> roadmapPageResponses = roadmapService.findRoadmapsByFilterType(
                categoryId, filterType, pageRequest);

        // then
        final RoadmapResponse firstRoadmapResponse = new RoadmapResponse(1L, "첫 번째 로드맵", "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(member.getId(), member.getNickname().getValue()),
                new RoadmapCategoryResponse(1, "여행"));
        final RoadmapResponse secondRoadmapResponse = new RoadmapResponse(1L, "두 번째 로드맵", "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(member.getId(), member.getNickname().getValue()),
                new RoadmapCategoryResponse(1, "여행"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1,
                List.of(firstRoadmapResponse, secondRoadmapResponse));

        assertThat(roadmapPageResponses)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_목록_조회_시_카테고리_조건이_null이면_전체_카테고리를_대상으로_최신순으로_조회한다() {
        // given
        final List<Roadmap> roadmaps = List.of(제목별로_로드맵을_생성한다("첫 번째 로드맵"), 제목별로_로드맵을_생성한다("두 번째 로드맵"));
        final PageImpl<Roadmap> roadmapPages = new PageImpl<>(roadmaps, PageRequest.of(0, 10), roadmaps.size());

        when(roadmapRepository.findRoadmapPagesByCond(any(), any(), any()))
                .thenReturn(roadmapPages);

        final Long categoryId = null;
        final RoadmapFilterTypeRequest filterType = RoadmapFilterTypeRequest.LATEST;
        final CustomPageRequest pageRequest = new CustomPageRequest(1, 10);

        // when
        final PageResponse<RoadmapResponse> roadmapPageResponses = roadmapService.findRoadmapsByFilterType(categoryId,
                filterType, pageRequest);

        // then
        final RoadmapResponse firstRoadmapResponse = new RoadmapResponse(1L, "첫 번째 로드맵", "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(member.getId(), member.getNickname().getValue()),
                new RoadmapCategoryResponse(1, "여행"));
        final RoadmapResponse secondRoadmapResponse = new RoadmapResponse(1L, "두 번째 로드맵", "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(member.getId(), member.getNickname().getValue()),
                new RoadmapCategoryResponse(1, "여행"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1,
                List.of(firstRoadmapResponse, secondRoadmapResponse));

        assertThat(roadmapPageResponses)
                .isEqualTo(expected);
    }

    @Test
    void 카테고리_아이디와_필터링_조건을_통해_로드맵_목록을_조회한다() {
        // given
        final List<Roadmap> roadmaps = List.of(제목별로_로드맵을_생성한다("첫 번째 로드맵"));
        final PageImpl<Roadmap> roadmapPages = new PageImpl<>(roadmaps, PageRequest.of(0, 10), roadmaps.size());

        when(roadmapCategoryRepository.findById(any()))
                .thenReturn(Optional.of(new RoadmapCategory("여행")));
        when(roadmapRepository.findRoadmapPagesByCond(any(), any(), any()))
                .thenReturn(roadmapPages);

        final Long categoryId = 1L;
        final RoadmapFilterTypeRequest filterType = RoadmapFilterTypeRequest.LATEST;
        final CustomPageRequest pageRequest = new CustomPageRequest(1, 10);

        // when
        final PageResponse<RoadmapResponse> roadmapPageResponses = roadmapService.findRoadmapsByFilterType(
                categoryId,
                filterType, pageRequest);

        // then
        final RoadmapResponse roadmapResponse = new RoadmapResponse(1L, "첫 번째 로드맵", "로드맵 소개글", "NORMAL", 10,
                new MemberResponse(member.getId(), member.getNickname().getValue()),
                new RoadmapCategoryResponse(1, "여행"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1, List.of(roadmapResponse));

        assertThat(roadmapPageResponses)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_전체_카테고리_리스트를_반환한다() {
        // given
        final List<RoadmapCategory> roadmapCategories = 로드맵_카테고리_리스트를_반환한다();
        when(roadmapCategoryRepository.findAll())
                .thenReturn(roadmapCategories);

        // when
        final List<RoadmapCategoryResponse> categoryResponses = roadmapService.findAllRoadmapCategories();

        // then
        final List<RoadmapCategoryResponse> expected = 로드맵_카테고리_응답_리스트를_반환한다();
        assertThat(categoryResponses)
                .isEqualTo(expected);
    }

    private Member 사용자를_생성한다(final String name, final String identifier) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1995, 9, 30),
                new Nickname(name), "010-1234-5678");
        return new Member(1L, new Identifier(identifier),
                new EncryptedPassword(new Password("password1!")), memberProfile);
    }

    private Roadmap 로드맵을_생성한다(final Member creator, final RoadmapCategory category) {
        final RoadmapContent content = 로드맵_컨텐츠를_생성한다();
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 설명", 100, RoadmapDifficulty.NORMAL, creator, category);
        roadmap.addContent(content);
        return roadmap;
    }

    RoadmapCategory 로드맵_카테고리를_생성한다(final String title) {
        return new RoadmapCategory(1L, title);
    }

    private RoadmapContent 로드맵_컨텐츠를_생성한다() {
        return new RoadmapContent("콘텐츠 제목");
    }

    private Roadmap 제목별로_로드맵을_생성한다(final String roadmapTitle) {
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 내용1");
        final RoadmapCategory category = new RoadmapCategory(1L, "여행");
        final Roadmap roadmap = new Roadmap(1L, roadmapTitle, "로드맵 소개글", 10,
                RoadmapDifficulty.NORMAL, member, category);
        roadmap.addContent(roadmapContent);
        return roadmap;
    }

    private List<RoadmapCategory> 로드맵_카테고리_리스트를_반환한다() {
        final RoadmapCategory category1 = new RoadmapCategory(1L, "어학");
        final RoadmapCategory category2 = new RoadmapCategory(2L, "IT");
        final RoadmapCategory category3 = new RoadmapCategory(3L, "시험");
        final RoadmapCategory category4 = new RoadmapCategory(4L, "운동");
        final RoadmapCategory category5 = new RoadmapCategory(5L, "게임");
        final RoadmapCategory category6 = new RoadmapCategory(6L, "음악");
        final RoadmapCategory category7 = new RoadmapCategory(7L, "라이프");
        final RoadmapCategory category8 = new RoadmapCategory(8L, "여가");
        final RoadmapCategory category9 = new RoadmapCategory(9L, "기타");
        return List.of(category1, category2, category3, category4, category5,
                category6, category7, category8, category9);
    }

    private List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트를_반환한다() {
        final RoadmapCategoryResponse category1 = new RoadmapCategoryResponse(1L, "어학");
        final RoadmapCategoryResponse category2 = new RoadmapCategoryResponse(2L, "IT");
        final RoadmapCategoryResponse category3 = new RoadmapCategoryResponse(3L, "시험");
        final RoadmapCategoryResponse category4 = new RoadmapCategoryResponse(4L, "운동");
        final RoadmapCategoryResponse category5 = new RoadmapCategoryResponse(5L, "게임");
        final RoadmapCategoryResponse category6 = new RoadmapCategoryResponse(6L, "음악");
        final RoadmapCategoryResponse category7 = new RoadmapCategoryResponse(7L, "라이프");
        final RoadmapCategoryResponse category8 = new RoadmapCategoryResponse(8L, "여가");
        final RoadmapCategoryResponse category9 = new RoadmapCategoryResponse(9L, "기타");
        return List.of(category1, category2, category3, category4, category5,
                category6, category7, category8, category9);
    }

    private GoalRoom 골룸을_생성한다(final RoadmapContent roadmapContent) {
        return new GoalRoom("골룸", 10, 5, GoalRoomStatus.RECRUITING, roadmapContent);
    }

    private List<GoalRoomMember> 사용자가_참여한_특정_로드맵의_골룸_멤버_목록을_생성한다(final GoalRoom goalRoom) {
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.of(2022, 7, 20, 17, 10), goalRoom, member);
        goalRoom.addMember(goalRoomMember);
        return List.of(goalRoomMember);
    }

}
