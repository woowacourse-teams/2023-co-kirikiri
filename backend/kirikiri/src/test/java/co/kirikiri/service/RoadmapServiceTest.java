package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.ImageContentType;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.RoadmapCategoryRepository;
import co.kirikiri.persistence.RoadmapRepository;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapFilterType;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import java.time.LocalDate;
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

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapCategoryRepository roadmapCategoryRepository;

    @InjectMocks
    private RoadmapService roadmapService;

    @Test
    void 로드맵_목록_조회시_카테고리_아이디가_유효하지_않으면_예외가_발생한다() {
        // given
        when(roadmapCategoryRepository.findById(any()))
            .thenThrow(new NotFoundException("존재하지 않는 카테고리입니다. categoryId = 1L"));

        final Long categoryId = 1L;
        final RoadmapFilterType filterType = RoadmapFilterType.LATEST;
        final PageRequest pageRequest = PageRequest.of(1, 10);

        // when, then
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
        final RoadmapFilterType filterType = null;
        final PageRequest pageRequest = PageRequest.of(1, 10);

        // when
        final PageResponse<RoadmapResponse> roadmapPageResponses = roadmapService.findRoadmapsByFilterType(categoryId,
            filterType, pageRequest);

        // then
        final RoadmapResponse firstRoadmapResponse = new RoadmapResponse(1L, "첫 번째 로드맵", "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(1, "여행"));
        final RoadmapResponse secondRoadmapResponse = new RoadmapResponse(1L, "두 번째 로드맵", "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(1, "여행"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1,
            List.of(firstRoadmapResponse, secondRoadmapResponse));

        assertThat(roadmapPageResponses)
            .usingRecursiveComparison()
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
        final RoadmapFilterType filterType = RoadmapFilterType.LATEST;
        final PageRequest pageRequest = PageRequest.of(1, 10);

        // when
        final PageResponse<RoadmapResponse> roadmapPageResponses = roadmapService.findRoadmapsByFilterType(categoryId,
            filterType, pageRequest);

        // then
        final RoadmapResponse roadmapResponse = new RoadmapResponse(1L, "첫 번째 로드맵", "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(1, "여행"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1, List.of(roadmapResponse));

        assertThat(roadmapPageResponses)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private Roadmap 제목별로_로드맵을_생성한다(final String roadmapTitle) {
        final MemberProfileImage memberProfileImage = new MemberProfileImage("member-profile.png",
            "member-profile-save-path", ImageContentType.PNG);
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), "코끼리",
            "010-1234-5678", memberProfileImage);
        final Member creator = new Member(1L, "cokirikiri", "password", memberProfile);

        final RoadmapContent roadmapContent = new RoadmapContent(1L, "로드맵 내용1", Collections.emptyList());
        final RoadmapCategory category = new RoadmapCategory(1L, "여행");
        final Roadmap roadmap = new Roadmap(1L, roadmapTitle, "로드맵 소개글", 10, RoadmapDifficulty.NORMAL,
            RoadmapStatus.CREATED, creator, category);
        roadmap.addContent(roadmapContent);

        return roadmap;
    }
}
