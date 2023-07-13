package co.kirikiri.integration;


import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.ImageContentType;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.MemberRepository;
import co.kirikiri.persistence.RoadmapCategoryRepository;
import co.kirikiri.persistence.RoadmapRepository;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import io.restassured.common.mapper.TypeRef;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RoadmapIntegrationTest extends IntegrationTest {

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapIntegrationTest(final MemberRepository memberRepository, final RoadmapRepository roadmapRepository,
                                  final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 로드맵_목록을_필터링하여_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory travelCategory = 로드맵_카테고리를_저장한다("여행");

        final Roadmap firstRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "첫 번째 로드맵");
        final Roadmap secondRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "두 번째 로드맵");
        final Roadmap savedFirstRoadmap = roadmapRepository.save(firstRoadmap);
        final Roadmap savedSecondRoadmap = roadmapRepository.save(secondRoadmap);

        // when
        final PageResponse<RoadmapResponse> pageResponse = given()
            .log().all()
            .when()
            .get("/api/roadmaps?page=1&size=10&filterType=LATEST&categoryId=" + travelCategory.getId())
            .then().log().all()
            .extract()
            .response()
            .as(new TypeRef<>() {
            });

        // then
        final RoadmapResponse firstRoadmapResponse = new RoadmapResponse(savedSecondRoadmap.getId(), "두 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(1, "여행"));
        final RoadmapResponse secondRoadmapResponse = new RoadmapResponse(savedFirstRoadmap.getId(), "첫 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(1, "여행"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1,
            List.of(firstRoadmapResponse, secondRoadmapResponse));

        assertThat(pageResponse)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private Member 크리에이터를_생성한다() {
        final MemberProfileImage memberProfileImage = new MemberProfileImage("member-profile.png",
            "member-profile-save-path", ImageContentType.PNG);
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), "코끼리",
            "010-1234-5678", memberProfileImage);
        final Member creator = new Member("cokirikiri", "password", memberProfile);
        return memberRepository.save(creator);
    }

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private Roadmap 제목별로_로드맵을_생성한다(final Member creator, final RoadmapCategory category, final String roadmapTitle) {
        return new Roadmap(roadmapTitle, "로드맵 소개글", 10, RoadmapDifficulty.NORMAL,
            RoadmapStatus.CREATED, creator, category);
    }
}
