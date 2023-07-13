package co.kirikiri.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.ImageContentType;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.domain.roadmap.dto.RoadmapOrderType;
import co.kirikiri.persistence.helper.RepositoryTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class RoadmapRepositoryTest extends RepositoryTest {

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapRepositoryTest(final MemberRepository memberRepository, final RoadmapRepository roadmapRepository,
                                 final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 카테고리_값이_null이라면_삭제되지_않은_전체_로드맵을_최신순으로_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap = 로드맵을_생성한다(creator, gameCategory);
        final Roadmap gameRoadmap2 = 로드맵을_생성한다(creator, gameCategory);
        final Roadmap travelRoadmap = 로드맵을_생성한다(creator, travelCategory);
        final Roadmap deletedTravelRoadmap = 삭제된_로드맵을_생성한다(creator, travelCategory);

        roadmapRepository.saveAll(List.of(gameRoadmap, deletedTravelRoadmap, gameRoadmap2, travelRoadmap));

        final RoadmapCategory category = null;
        final RoadmapOrderType orderType = RoadmapOrderType.LATEST;
        final PageRequest firstPage = PageRequest.of(0, 2);
        final PageRequest secondPage = PageRequest.of(1, 2);

        // when
        final Page<Roadmap> firstPageRoadmaps = roadmapRepository.getRoadmapPagesByCond(category, orderType, firstPage);
        final Page<Roadmap> secondPageRoadmaps = roadmapRepository.getRoadmapPagesByCond(category, orderType,
            secondPage);

        // then
        assertAll(
            () -> assertThat(firstPageRoadmaps.getTotalPages()).isEqualTo(2),
            () -> assertThat(firstPageRoadmaps.getTotalElements()).isEqualTo(3),
            () -> assertThat(firstPageRoadmaps.getContent().size()).isEqualTo(2),

            () -> assertThat(secondPageRoadmaps.getTotalPages()).isEqualTo(2),
            () -> assertThat(secondPageRoadmaps.getTotalElements()).isEqualTo(3),
            () -> assertThat(secondPageRoadmaps.getContent().size()).isEqualTo(1),

            () -> assertThat(firstPageRoadmaps.getContent()).usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(List.of(travelRoadmap, gameRoadmap2)),

            () -> assertThat(secondPageRoadmaps.getContent()).usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(List.of(gameRoadmap))
        );
    }

    @Test
    void 카테고리_값으로_1이상의_유효한_값이_들어오면_해당_카테고리의_삭제되지_않은_로드맵을_최신순으로_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap = 로드맵을_생성한다(creator, gameCategory);
        final Roadmap gameRoadmap2 = 로드맵을_생성한다(creator, gameCategory);
        final Roadmap deletedGameRoadmap = 삭제된_로드맵을_생성한다(creator, gameCategory);
        final Roadmap deletedTravelRoadmap = 삭제된_로드맵을_생성한다(creator, travelCategory);

        roadmapRepository.saveAll(List.of(gameRoadmap, deletedTravelRoadmap, gameRoadmap2, deletedGameRoadmap));

        final RoadmapOrderType orderType = RoadmapOrderType.LATEST;
        final PageRequest firstPage = PageRequest.of(0, 10);

        // when
        final Page<Roadmap> firstPageRoadmaps = roadmapRepository.getRoadmapPagesByCond(gameCategory, orderType,
            firstPage);

        // then
        assertAll(
            () -> assertThat(firstPageRoadmaps.getTotalPages()).isEqualTo(1),
            () -> assertThat(firstPageRoadmaps.getTotalElements()).isEqualTo(2),
            () -> assertThat(firstPageRoadmaps.getContent().size()).isEqualTo(2),
            () -> assertThat(firstPageRoadmaps.getContent()).usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(List.of(gameRoadmap2, gameRoadmap))
        );
    }

    private Member 크리에이터를_생성한다() {
        final MemberProfileImage memberProfileImage = new MemberProfileImage("member-profile.png",
            "member-profile-save-path", ImageContentType.PNG);
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), "코끼리",
            "010-1234-5678", memberProfileImage);
        final Member creator = new Member("cokirikiri", "password", memberProfile);
        return memberRepository.save(creator);
    }

    private RoadmapCategory 카테고리를_생성한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private Roadmap 로드맵을_생성한다(final Member creator, final RoadmapCategory category) {
        return new Roadmap("로드맵 제목", "로드맵 소개글", 10, RoadmapDifficulty.NORMAL,
            RoadmapStatus.CREATED, creator, category);
    }

    private Roadmap 삭제된_로드맵을_생성한다(final Member creator, final RoadmapCategory category) {
        return new Roadmap("로드맵 제목2", "로드맵 소개글2", 7, RoadmapDifficulty.DIFFICULT,
            RoadmapStatus.DELETED, creator, category);
    }
}

