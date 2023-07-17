package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.ImageContentType;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.RoadmapCategoryRepository;
import co.kirikiri.persistence.RoadmapRepository;
import co.kirikiri.service.dto.roadmap.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RoadmapServiceTest {

    @InjectMocks
    private RoadmapService roadmapService;

    @Mock
    private RoadmapCategoryRepository roadmapCategoryRepository;

    @Mock
    private RoadmapRepository roadmapRepository;

    @Test
    void 존재하지_않는_카테고리를_입력하면_예외가_발생한다() {
        // given
        final RoadmapSaveRequest request = new RoadmapSaveRequest(10L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 노드1", "로드맵 노드1 설명")));
        final Member creator = new Member(1L, "아이디", "비밀번호",
                new MemberProfile(1L, Gender.FEMALE, LocalDate.of(1999, 6, 8), "닉네임", "01011112222",
                        new MemberProfileImage(1L, "파일명", "서버 파일 경로", ImageContentType.PNG)));
        given(roadmapCategoryRepository.findById(any()))
                .willReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> roadmapService.create(request, creator))
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
        final List<RoadmapNodeSaveRequest> roadmapNodes = List.of(
                new RoadmapNodeSaveRequest("로드맵 노드1 제목", "로드맵 노드1 설명"));
        final Member creator = new Member(1L, "아이디", "비밀번호",
                new MemberProfile(1L, Gender.FEMALE, LocalDate.of(1999, 6, 8), "닉네임", "01011112222",
                        new MemberProfileImage(1L, "파일명", "서버 파일 경로", ImageContentType.PNG)));

        final RoadmapSaveRequest request = new RoadmapSaveRequest(1L, roadmapTitle, roadmapIntroduction, roadmapContent,
                difficulty, requiredPeriod, roadmapNodes);
        final RoadmapCategory category = new RoadmapCategory(1L, "여가");

        given(roadmapCategoryRepository.findById(any()))
                .willReturn(Optional.of(category));
        given(roadmapRepository.save(any()))
                .willReturn(new Roadmap(1L, roadmapTitle, roadmapIntroduction, requiredPeriod,
                        RoadmapDifficulty.valueOf(difficulty.name()),
                        RoadmapStatus.CREATED, creator, category, any()));

        // expect
        assertThat(roadmapService.create(request, creator)).isEqualTo(1L);
    }
}
