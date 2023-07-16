package co.kirikiri.integration;


import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.ImageContentType;
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
import co.kirikiri.service.dto.ErrorResponse;
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
    void 로드맵_목록_조회시_유효하지_않은_카테고리_번호를_받으면_예외가_발생한다() {
        // given
        final Member 크리에이터 = 크리에이터를_생성한다();
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");

        final Roadmap 첫번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 여행_카테고리, "첫 번째 로드맵");
        final Roadmap 두번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 여행_카테고리, "두 번째 로드맵");
        roadmapRepository.save(첫번째_로드맵);
        roadmapRepository.save(두번째_로드맵);

        // when
        final ErrorResponse 에러_응답 = given()
            .log().all()
            .when()
            .get("/api/roadmaps?page=1&size=10&filterType=LATEST&categoryId=3")
            .then().log().all()
            .extract()
            .response()
            .as(new TypeRef<>() {
            });

        // then
        final ErrorResponse 예상되는_에러_응답 = new ErrorResponse("존재하지 않는 카테고리입니다. categoryId = 3");

        assertThat(에러_응답)
            .usingRecursiveComparison()
            .isEqualTo(예상되는_에러_응답);
    }

    @Test
    void 카테고리_아이디와_정렬_조건에_따라_로드맵_목록을_조회한다() {
        // given
        final Member 크리에이터 = 크리에이터를_생성한다();
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Roadmap 첫번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 여행_카테고리, "첫 번째 로드맵");
        final Roadmap 두번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 여행_카테고리, "두 번째 로드맵");
        final Roadmap 세번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 게임_카테고리, "세 번째 로드맵");
        final Roadmap 저장된_첫번째_로드맵 = roadmapRepository.save(첫번째_로드맵);
        final Roadmap 저장된_두번째_로드맵 = roadmapRepository.save(두번째_로드맵);
        roadmapRepository.save(세번째_로드맵);

        // when
        final PageResponse<RoadmapResponse> 로드맵_페이지_응답 = given()
            .log().all()
            .when()
            .get("/api/roadmaps?page=1&size=10&filterType=LATEST&categoryId=" + 여행_카테고리.getId())
            .then().log().all()
            .extract()
            .response()
            .as(new TypeRef<>() {
            });

        // then
        final RoadmapResponse 첫번째_로드맵_응답 = new RoadmapResponse(저장된_첫번째_로드맵.getId(), "첫 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"));
        final RoadmapResponse 두번째_로드맵_응답 = new RoadmapResponse(저장된_두번째_로드맵.getId(), "두 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"));
        final PageResponse<RoadmapResponse> 예상되는_로드맵_응답 = new PageResponse<>(1, 1,
            List.of(두번째_로드맵_응답, 첫번째_로드맵_응답));

        assertThat(로드맵_페이지_응답)
            .usingRecursiveComparison()
            .isEqualTo(예상되는_로드맵_응답);
    }

    @Test
    void 카테고리_아이디에_따라_로드맵_목록을_조회한다() {
        // given
        final Member 크리에이터 = 크리에이터를_생성한다();
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Roadmap 첫번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 여행_카테고리, "첫 번째 로드맵");
        final Roadmap 두번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 여행_카테고리, "두 번째 로드맵");
        final Roadmap 세번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 게임_카테고리, "세 번째 로드맵");
        final Roadmap 저장된_첫번째_로드맵 = roadmapRepository.save(첫번째_로드맵);
        final Roadmap 저장된_두번째_로드맵 = roadmapRepository.save(두번째_로드맵);
        roadmapRepository.save(세번째_로드맵);

        // when
        final PageResponse<RoadmapResponse> 로드맵_페이지_응답 = given()
            .log().all()
            .when()
            .get("/api/roadmaps?page=1&size=10&categoryId=" + 여행_카테고리.getId())
            .then().log().all()
            .extract()
            .response()
            .as(new TypeRef<>() {
            });

        // then
        final RoadmapResponse 첫번째_로드맵_응답 = new RoadmapResponse(저장된_첫번째_로드맵.getId(), "첫 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"));
        final RoadmapResponse 두번째_로드맵_응답 = new RoadmapResponse(저장된_두번째_로드맵.getId(), "두 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"));
        final PageResponse<RoadmapResponse> 예상되는_로드맵_응답 = new PageResponse<>(1, 1,
            List.of(두번째_로드맵_응답, 첫번째_로드맵_응답));

        assertThat(로드맵_페이지_응답)
            .usingRecursiveComparison()
            .isEqualTo(예상되는_로드맵_응답);
    }

    @Test
    void 정렬_조건에_따라_로드맵_목록을_조회한다() {
        // given
        final Member 크리에이터 = 크리에이터를_생성한다();
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Roadmap 첫번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 여행_카테고리, "첫 번째 로드맵");
        final Roadmap 두번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 여행_카테고리, "두 번째 로드맵");
        final Roadmap 세번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 게임_카테고리, "세 번째 로드맵");
        final Roadmap 저장된_첫번째_로드맵 = roadmapRepository.save(첫번째_로드맵);
        final Roadmap 저장된_두번째_로드맵 = roadmapRepository.save(두번째_로드맵);
        final Roadmap 저장된_세번째_로드맵 = roadmapRepository.save(세번째_로드맵);

        // when
        final PageResponse<RoadmapResponse> 로드맵_페이지_응답 = given()
            .log().all()
            .when()
            .get("/api/roadmaps?page=1&size=10&filterType=LATEST")
            .then().log().all()
            .extract()
            .response()
            .as(new TypeRef<>() {
            });

        // then
        final RoadmapResponse 첫번째_로드맵_응답 = new RoadmapResponse(저장된_첫번째_로드맵.getId(), "첫 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"));
        final RoadmapResponse 두번째_로드맵_응답 = new RoadmapResponse(저장된_두번째_로드맵.getId(), "두 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"));
        final RoadmapResponse 세번째_로드맵_응답 = new RoadmapResponse(저장된_세번째_로드맵.getId(), "세 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(게임_카테고리.getId(), "게임"));
        final PageResponse<RoadmapResponse> 예상되는_로드맵_응답 = new PageResponse<>(1, 1,
            List.of(세번째_로드맵_응답, 두번째_로드맵_응답, 첫번째_로드맵_응답));

        assertThat(로드맵_페이지_응답)
            .usingRecursiveComparison()
            .isEqualTo(예상되는_로드맵_응답);
    }

    @Test
    void 아무_조건_없이_로드맵_목록을_조회한다() {
        // given
        final Member 크리에이터 = 크리에이터를_생성한다();
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapCategory 게임_카테고리 = 로드맵_카테고리를_저장한다("게임");

        final Roadmap 첫번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 여행_카테고리, "첫 번째 로드맵");
        final Roadmap 두번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 여행_카테고리, "두 번째 로드맵");
        final Roadmap 세번째_로드맵 = 제목별로_로드맵을_생성한다(크리에이터, 게임_카테고리, "세 번째 로드맵");
        final Roadmap 저장된_첫번째_로드맵 = roadmapRepository.save(첫번째_로드맵);
        final Roadmap 저장된_두번째_로드맵 = roadmapRepository.save(두번째_로드맵);
        final Roadmap 저장된_세번째_로드맵 = roadmapRepository.save(세번째_로드맵);

        // when
        final PageResponse<RoadmapResponse> 로드맵_페이지_응답 = given()
            .log().all()
            .when()
            .get("/api/roadmaps?page=1&size=10")
            .then().log().all()
            .extract()
            .response()
            .as(new TypeRef<>() {
            });

        // then
        final RoadmapResponse 첫번째_로드맵_응답 = new RoadmapResponse(저장된_첫번째_로드맵.getId(), "첫 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"));
        final RoadmapResponse 두번째_로드맵_응답 = new RoadmapResponse(저장된_두번째_로드맵.getId(), "두 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(여행_카테고리.getId(), "여행"));
        final RoadmapResponse 세번째_로드맵_응답 = new RoadmapResponse(저장된_세번째_로드맵.getId(), "세 번째 로드맵",
            "로드맵 소개글", "NORMAL", 10,
            new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(게임_카테고리.getId(), "게임"));
        final PageResponse<RoadmapResponse> 예상되는_로드맵_응답 = new PageResponse<>(1, 1,
            List.of(세번째_로드맵_응답, 두번째_로드맵_응답, 첫번째_로드맵_응답));

        assertThat(로드맵_페이지_응답)
            .usingRecursiveComparison()
            .isEqualTo(예상되는_로드맵_응답);
    }

    @Test
    void 로드맵_카테고리_리스트를_조회한다() {
        // given
        final List<RoadmapCategory> 로드맵_카테고리들 = 로드맵_카테고리를_저장한다();

        // when
        final List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트 = given()
            .log().all()
            .when()
            .get("/api/roadmaps/categories")
            .then().log().all()
            .extract()
            .response()
            .as(new TypeRef<>() {
            });

        // then
        final List<RoadmapCategoryResponse> 예상되는_로드맵_응답 = 로드맵_카테고리_응답_리스트를_반환한다(로드맵_카테고리들);

        assertThat(로드맵_카테고리_응답_리스트)
            .usingRecursiveComparison()
            .isEqualTo(예상되는_로드맵_응답);
    }

    private Member 크리에이터를_생성한다() {
        final MemberProfileImage 크리에이터_프로필_이미지 = new MemberProfileImage("member-profile.png",
            "member-profile-save-path", ImageContentType.PNG);
        final MemberProfile 크리에이터_정보 = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
            new Nickname("코끼리"), "010-1234-5678", 크리에이터_프로필_이미지);
        final Member 크리에이터 = new Member(new Identifier("cokirikiri"),
            new EncryptedPassword(new Password("password1!")), 크리에이터_정보);
        return memberRepository.save(크리에이터);
    }

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String 카테고리_이름) {
        final RoadmapCategory 로드맵_카테고리 = new RoadmapCategory(카테고리_이름);
        return roadmapCategoryRepository.save(로드맵_카테고리);
    }

    private Roadmap 제목별로_로드맵을_생성한다(final Member 크리에이터, final RoadmapCategory 로드맵_카테고리, final String 로드맵_제목) {
        return new Roadmap(로드맵_제목, "로드맵 소개글", 10, RoadmapDifficulty.NORMAL,
            RoadmapStatus.CREATED, 크리에이터, 로드맵_카테고리);
    }

    private List<RoadmapCategory> 로드맵_카테고리를_저장한다() {
        final RoadmapCategory 어학_카테고리 = new RoadmapCategory("어학");
        final RoadmapCategory IT_카테고리 = new RoadmapCategory("IT");
        final RoadmapCategory 시험_카테고리 = new RoadmapCategory("시험");
        final RoadmapCategory 운동_카테고리 = new RoadmapCategory("운동");
        final RoadmapCategory 게임_카테고리 = new RoadmapCategory("게임");
        final RoadmapCategory 음악_카테고리 = new RoadmapCategory("음악");
        final RoadmapCategory 라이프_카테고리 = new RoadmapCategory("라이프");
        final RoadmapCategory 여가_카테고리 = new RoadmapCategory("여가");
        final RoadmapCategory 기타_카테고리 = new RoadmapCategory("기타");
        return roadmapCategoryRepository.saveAll(
            List.of(어학_카테고리, IT_카테고리, 시험_카테고리, 운동_카테고리, 게임_카테고리, 음악_카테고리, 라이프_카테고리, 여가_카테고리, 기타_카테고리));
    }

    private List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트를_반환한다(final List<RoadmapCategory> 로드맵_카테고리들) {
        final RoadmapCategoryResponse 어학_카테고리 = new RoadmapCategoryResponse(로드맵_카테고리들.get(0).getId(), "어학");
        final RoadmapCategoryResponse IT_카테고리 = new RoadmapCategoryResponse(로드맵_카테고리들.get(1).getId(), "IT");
        final RoadmapCategoryResponse 시험_카테고리 = new RoadmapCategoryResponse(로드맵_카테고리들.get(2).getId(), "시험");
        final RoadmapCategoryResponse 운동_카테고리 = new RoadmapCategoryResponse(로드맵_카테고리들.get(3).getId(), "운동");
        final RoadmapCategoryResponse 게임_카테고리 = new RoadmapCategoryResponse(로드맵_카테고리들.get(4).getId(), "게임");
        final RoadmapCategoryResponse 음악_카테고리 = new RoadmapCategoryResponse(로드맵_카테고리들.get(5).getId(), "음악");
        final RoadmapCategoryResponse 라이프_카테고리 = new RoadmapCategoryResponse(로드맵_카테고리들.get(6).getId(), "라이프");
        final RoadmapCategoryResponse 여가_카테고리 = new RoadmapCategoryResponse(로드맵_카테고리들.get(7).getId(), "여가");
        final RoadmapCategoryResponse 기타_카테고리 = new RoadmapCategoryResponse(로드맵_카테고리들.get(8).getId(), "기타");
        return List.of(어학_카테고리, IT_카테고리, 시험_카테고리, 운동_카테고리, 게임_카테고리, 음악_카테고리, 라이프_카테고리, 여가_카테고리,
            기타_카테고리);
    }
}
