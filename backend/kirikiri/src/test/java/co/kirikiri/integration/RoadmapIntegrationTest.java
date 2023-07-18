package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.ImageContentType;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImage;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.RoadmapNodeResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class RoadmapIntegrationTest extends IntegrationTest {

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
    void 존재하는_로드맵_아이디로_요청했을_때_단일_로드맵_정보를_조회를_성공한다() {
        //given
        final Member 사용자 = 사용자를_생성한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_생성한다("운동");
        로드맵을_생성한다(사용자, 카테고리);

        //when
        final ExtractableResponse<Response> 단일_로드맵_조회_요청에_대한_응답 = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}", 1)
                .then()
                .log().all()
                .extract();

        //then
        final RoadmapResponse 단일_로드맵_응답 = 단일_로드맵_조회_요청에_대한_응답.as(new TypeRef<>() {
        });

        final RoadmapResponse 예상되는_단일_로드맵_응답 = new RoadmapResponse(
                1L,
                new RoadmapCategoryResponse(1L, "운동"),
                "로드맵 제목",
                "로드맵 설명",
                new MemberResponse(1L, "썬샷"),
                null,
                "NORMAL",
                100,
                List.of(
                        new RoadmapNodeResponse("1단계", "준비운동", List.of("node-image1-save-path")),
                        new RoadmapNodeResponse("2단계", "턱걸이", List.of("node-image1-save-path"))
                )
        );

        assertThat(단일_로드맵_조회_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(단일_로드맵_응답)
                .usingRecursiveComparison()
                .isEqualTo(예상되는_단일_로드맵_응답);
    }

    @Test
    void 존재하지_않는_로드맵_아이디로_요청했을_때_조회를_실패한다() {
        //given
        final Long 존재하지_않는_로드맵_아이디 = 1L;

        //when
        final ExtractableResponse<Response> 요청에_대한_응답 = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}", 존재하지_않는_로드맵_아이디)
                .then()
                .log().all()
                .extract();

        //then
        final String 예외_메시지 = 요청에_대한_응답.asString();

        assertAll(
                () -> assertThat(요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(예외_메시지).contains("존재하지 않는 로드맵입니다. roadmapId = " + 존재하지_않는_로드맵_아이디)
        );
    }

    private Roadmap 로드맵을_생성한다(final Member creator, final RoadmapCategory category) {
        final RoadmapContent 로드맵_내용 = new RoadmapContent(로드맵_노드들을_생성한다());

        final Roadmap 로드맵 = new Roadmap("로드맵 제목", "로드맵 설명", 100,
                RoadmapDifficulty.NORMAL, RoadmapStatus.CREATED, creator, category);
        로드맵.addContent(로드맵_내용);

        return roadmapRepository.save(로드맵);
    }

    private Member 사용자를_생성한다() {
        final MemberProfileImage 프로필_이미지 = new MemberProfileImage("sunshot_image.webp",
                "sunshot-profile-save-path", ImageContentType.WEBP);
        final MemberProfile 사용자_프로필 = new MemberProfile(Gender.MALE, LocalDate.of(1995, 9, 30),
                "썬샷", "01083004367", 프로필_이미지);
        final Member 사용자 = new Member("아이디", "패스워드", 사용자_프로필);

        return memberRepository.save(사용자);
    }

    private RoadmapCategory 로드맵_카테고리를_생성한다(final String title) {
        final RoadmapCategory 카테고리 = new RoadmapCategory(title);
        return roadmapCategoryRepository.save(카테고리);
    }

    private List<RoadmapNode> 로드맵_노드들을_생성한다() {
        return List.of(
                new RoadmapNode("1단계", "준비운동", 노드_이미지들을_생성한다()),
                new RoadmapNode("2단계", "턱걸이", 노드_이미지들을_생성한다())
        );
    }

    private List<RoadmapNodeImage> 노드_이미지들을_생성한다() {
        return List.of(
                new RoadmapNodeImage("node-image1.png", "node-image1-save-path",
                        ImageContentType.PNG)
        );
    }
}
