package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.domain.member.vo.EncryptedPassword;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import io.restassured.common.mapper.TypeRef;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RoadmapIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoadmapRepository roadmapRepository;
    @Autowired
    private RoadmapCategoryRepository roadmapCategoryRepository;

    @Test
    void 카테고리_아이디와_정렬_조건에_따라_로드맵_목록을_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory travelCategory = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory gameCategory = 로드맵_카테고리를_저장한다("게임");

        final Roadmap firstRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "첫 번째 로드맵");
        final Roadmap secondRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "두 번째 로드맵");
        final Roadmap thirdRoadmap = 제목별로_로드맵을_생성한다(creator, gameCategory, "세 번째 로드맵");
        final Roadmap savedFirstRoadmap = roadmapRepository.save(firstRoadmap);
        final Roadmap savedSecondRoadmap = roadmapRepository.save(secondRoadmap);
        roadmapRepository.save(thirdRoadmap);

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
        final RoadmapResponse firstRoadmapResponse = new RoadmapResponse(savedFirstRoadmap.getId(), "첫 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final RoadmapResponse secondRoadmapResponse = new RoadmapResponse(savedSecondRoadmap.getId(), "두 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1,
            List.of(secondRoadmapResponse, firstRoadmapResponse));

        assertThat(pageResponse)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    void 카테고리_아이디에_따라_로드맵_목록을_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory travelCategory = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory gameCategory = 로드맵_카테고리를_저장한다("게임");

        final Roadmap firstRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "첫 번째 로드맵");
        final Roadmap secondRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "두 번째 로드맵");
        final Roadmap thirdRoadmap = 제목별로_로드맵을_생성한다(creator, gameCategory, "세 번째 로드맵");
        final Roadmap savedFirstRoadmap = roadmapRepository.save(firstRoadmap);
        final Roadmap savedSecondRoadmap = roadmapRepository.save(secondRoadmap);
        roadmapRepository.save(thirdRoadmap);

        // when
        final PageResponse<RoadmapResponse> pageResponse = given()
            .log().all()
            .when()
            .get("/api/roadmaps?page=1&size=10&categoryId=" + travelCategory.getId())
            .then().log().all()
            .extract()
            .response()
            .as(new TypeRef<>() {
            });

        // then
        final RoadmapResponse firstRoadmapResponse = new RoadmapResponse(savedFirstRoadmap.getId(), "첫 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final RoadmapResponse secondRoadmapResponse = new RoadmapResponse(savedSecondRoadmap.getId(), "두 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1,
            List.of(secondRoadmapResponse, firstRoadmapResponse));

        assertThat(pageResponse)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    void 정렬_조건에_따라_로드맵_목록을_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory travelCategory = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory gameCategory = 로드맵_카테고리를_저장한다("게임");

        final Roadmap firstRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "첫 번째 로드맵");
        final Roadmap secondRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "두 번째 로드맵");
        final Roadmap thirdRoadmap = 제목별로_로드맵을_생성한다(creator, gameCategory, "세 번째 로드맵");
        final Roadmap savedFirstRoadmap = roadmapRepository.save(firstRoadmap);
        final Roadmap savedSecondRoadmap = roadmapRepository.save(secondRoadmap);
        final Roadmap thirdSecondRoadmap = roadmapRepository.save(thirdRoadmap);

        // when
        final PageResponse<RoadmapResponse> pageResponse = given()
            .log().all()
            .when()
            .get("/api/roadmaps?page=1&size=10&filterType=LATEST")
            .then().log().all()
            .extract()
            .response()
            .as(new TypeRef<>() {
            });

        // then
        final RoadmapResponse firstRoadmapResponse = new RoadmapResponse(savedFirstRoadmap.getId(), "첫 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final RoadmapResponse secondRoadmapResponse = new RoadmapResponse(savedSecondRoadmap.getId(), "두 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final RoadmapResponse thirdRoadmapResponse = new RoadmapResponse(thirdSecondRoadmap.getId(), "세 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(gameCategory.getId(), "게임"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1,
            List.of(thirdRoadmapResponse, secondRoadmapResponse, firstRoadmapResponse));

        assertThat(pageResponse)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    void 아무_조건_없이_로드맵_목록을_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory travelCategory = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory gameCategory = 로드맵_카테고리를_저장한다("게임");

        final Roadmap firstRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "첫 번째 로드맵");
        final Roadmap secondRoadmap = 제목별로_로드맵을_생성한다(creator, travelCategory, "두 번째 로드맵");
        final Roadmap thirdRoadmap = 제목별로_로드맵을_생성한다(creator, gameCategory, "세 번째 로드맵");
        final Roadmap savedFirstRoadmap = roadmapRepository.save(firstRoadmap);
        final Roadmap savedSecondRoadmap = roadmapRepository.save(secondRoadmap);
        final Roadmap thirdSecondRoadmap = roadmapRepository.save(thirdRoadmap);

        // when
        final PageResponse<RoadmapResponse> pageResponse = given()
            .log().all()
            .when()
            .get("/api/roadmaps?page=1&size=10")
            .then().log().all()
            .extract()
            .response()
            .as(new TypeRef<>() {
            });

        // then
        final RoadmapResponse firstRoadmapResponse = new RoadmapResponse(savedFirstRoadmap.getId(), "첫 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final RoadmapResponse secondRoadmapResponse = new RoadmapResponse(savedSecondRoadmap.getId(), "두 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(travelCategory.getId(), "여행"));
        final RoadmapResponse thirdRoadmapResponse = new RoadmapResponse(thirdSecondRoadmap.getId(), "세 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(gameCategory.getId(), "게임"));
        final PageResponse<RoadmapResponse> expected = new PageResponse<>(1, 1,
            List.of(thirdRoadmapResponse, secondRoadmapResponse, firstRoadmapResponse));

        assertThat(pageResponse)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    void 로드맵_카테고리_리스트를_조회한다() {
        // given
        final List<RoadmapCategory> roadmapCategories = 로드맵_카테고리를_저장한다();

        // when
        final List<RoadmapCategoryResponse> roadmapCategoryResponses = given()
            .log().all()
            .when()
            .get("/api/roadmaps/categories")
            .then().log().all()
            .extract()
            .response()
            .as(new TypeRef<>() {
            });

        // then
        final List<RoadmapCategoryResponse> expected = 로드맵_카테고리_응답_리스트를_반환한다(roadmapCategories);

        assertThat(roadmapCategoryResponses)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private Member 크리에이터를_생성한다() {
        final MemberProfileImage memberProfileImage = new MemberProfileImage("member-profile.png",
            "member-profile-save-path", ImageContentType.PNG);
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
            new Nickname("코끼리"), "010-1234-5678", memberProfileImage);
        final Member creator = new Member(new Identifier("cokirikiri"),
            new EncryptedPassword(new Password("password1!")), memberProfile);
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

    private List<RoadmapCategory> 로드맵_카테고리를_저장한다() {
        final RoadmapCategory category1 = new RoadmapCategory("어학");
        final RoadmapCategory category2 = new RoadmapCategory("IT");
        final RoadmapCategory category3 = new RoadmapCategory("시험");
        final RoadmapCategory category4 = new RoadmapCategory("운동");
        final RoadmapCategory category5 = new RoadmapCategory("게임");
        final RoadmapCategory category6 = new RoadmapCategory("음악");
        final RoadmapCategory category7 = new RoadmapCategory("라이프");
        final RoadmapCategory category8 = new RoadmapCategory("여가");
        final RoadmapCategory category9 = new RoadmapCategory("기타");
        return roadmapCategoryRepository.saveAll(
            List.of(category1, category2, category3, category4, category5, category6, category7, category8, category9));
    }

    private List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트를_반환한다(final List<RoadmapCategory> roadmapCategories) {
        final RoadmapCategoryResponse category1 = new RoadmapCategoryResponse(roadmapCategories.get(0).getId(), "어학");
        final RoadmapCategoryResponse category2 = new RoadmapCategoryResponse(roadmapCategories.get(1).getId(), "IT");
        final RoadmapCategoryResponse category3 = new RoadmapCategoryResponse(roadmapCategories.get(2).getId(), "시험");
        final RoadmapCategoryResponse category4 = new RoadmapCategoryResponse(roadmapCategories.get(3).getId(), "운동");
        final RoadmapCategoryResponse category5 = new RoadmapCategoryResponse(roadmapCategories.get(4).getId(), "게임");
        final RoadmapCategoryResponse category6 = new RoadmapCategoryResponse(roadmapCategories.get(5).getId(), "음악");
        final RoadmapCategoryResponse category7 = new RoadmapCategoryResponse(roadmapCategories.get(6).getId(), "라이프");
        final RoadmapCategoryResponse category8 = new RoadmapCategoryResponse(roadmapCategories.get(7).getId(), "여가");
        final RoadmapCategoryResponse category9 = new RoadmapCategoryResponse(roadmapCategories.get(8).getId(), "기타");
        return List.of(category1, category2, category3, category4, category5, category6, category7, category8,
            category9);
    }
}
