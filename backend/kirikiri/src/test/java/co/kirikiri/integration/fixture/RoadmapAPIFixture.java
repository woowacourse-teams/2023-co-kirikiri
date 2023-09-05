package co.kirikiri.integration.fixture;

import static co.kirikiri.integration.fixture.CommonFixture.API_PREFIX;
import static co.kirikiri.integration.fixture.CommonFixture.AUTHORIZATION;
import static io.restassured.RestAssured.given;

import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.persistence.dto.RoadmapOrderType;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapCategorySaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoadmapAPIFixture {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static Long 로드맵_생성(final RoadmapSaveRequest 로드맵_생성_요청, final String 액세스_토큰) throws IOException {
        final Response 응답 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청, 액세스_토큰).response();
        return Long.parseLong(응답.header(HttpHeaders.LOCATION).split("/")[3]);
    }

    public static ExtractableResponse<Response> 요청을_받는_이미지가_포함된_로드맵_생성(final RoadmapSaveRequest 로드맵_생성_요청값,
                                                                       final String accessToken)
            throws IOException {
        final String jsonRequest = objectMapper.writeValueAsString(로드맵_생성_요청값);

        RequestSpecification requestSpecification = given().log().all()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE + "; charset=utf-8")
                .multiPart("jsonData", "jsonData.json", jsonRequest, MediaType.APPLICATION_JSON_VALUE);

        requestSpecification = makeRequestSpecification(로드맵_생성_요청값, requestSpecification);

        return requestSpecification
                .log().all()
                .post(API_PREFIX + "/roadmaps")
                .then().log().all()
                .extract();
    }

    private static RequestSpecification makeRequestSpecification(final RoadmapSaveRequest 로드맵_생성_요청값,
                                                                 RequestSpecification requestSpecification)
            throws IOException {
        if (로드맵_생성_요청값.roadmapNodes() == null) {
            return requestSpecification;
        }
        for (final RoadmapNodeSaveRequest roadmapNode : 로드맵_생성_요청값.roadmapNodes()) {
            final String 로드맵_노드_제목 = roadmapNode.getTitle() != null ? roadmapNode.getTitle() : "name";
            final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile(로드맵_노드_제목, "originalFileName.jpeg",
                    "image/jpeg", "tempImage".getBytes());
            requestSpecification = requestSpecification
                    .multiPart(가짜_이미지_객체.getName(), 가짜_이미지_객체.getOriginalFilename(),
                            가짜_이미지_객체.getBytes(), 가짜_이미지_객체.getContentType());
        }
        return requestSpecification;
    }

    public static ExtractableResponse<Response> 로드맵_삭제(final Long 삭제할_로드맵_아이디, final String 로그인_토큰) {
        return given().log().all()
                .header(HttpHeaders.AUTHORIZATION, 로그인_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .delete(API_PREFIX + "/roadmaps/{roadmapId}", 삭제할_로드맵_아이디)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 로드맵을_아이디로_조회한다(final Long 로드맵_아이디) {
        return given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}", 로드맵_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static RoadmapResponse 로드맵을_아이디로_조회하고_응답객체를_반환한다(final Long 로드맵_아이디) {
        return 로드맵을_아이디로_조회한다(로드맵_아이디)
                .response()
                .as(RoadmapResponse.class);
    }

    public static ExtractableResponse<Response> 정렬된_카테고리별_로드맵_리스트_조회(final RoadmapOrderType 정렬_조건,
                                                                     final Long 검색할_카테고리_아이디, final int 페이지_크기) {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps?size=" + 페이지_크기 + "&filterCond=" + 정렬_조건.name() + "&categoryId=" + 검색할_카테고리_아이디)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 로그인한_사용자가_생성한_로드맵을_이전에_받은_로드맵의_제일마지막_아이디_이후의_조건으로_조회한다(
            final String 로그인_토큰_정보, final int 페이지_사이즈, final Long 마지막_로드맵_아이디) {
        return given()
                .log().all()
                .when()
                .header(HttpHeaders.AUTHORIZATION, 로그인_토큰_정보)
                .get("/api/roadmaps/me?lastId=" + 마지막_로드맵_아이디 + "&size=" + 페이지_사이즈)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 로그인한_사용자가_생성한_로드맵을_조회한다(final String 로그인_토큰_정보, final int 페이지_사이즈) {
        return given()
                .log().all()
                .when()
                .header(HttpHeaders.AUTHORIZATION, 로그인_토큰_정보)
                .get("/api/roadmaps/me?size=" + 페이지_사이즈)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 모든_카테고리를_조회한다() {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps/categories")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 사이즈별로_로드맵을_조회한다(final Integer size) {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps?size=" + size)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 사이즈_없이_로드맵을_조회한다() {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 리뷰를_생성한다(final String 팔로워_토큰_정보, final Long 로드맵_아이디,
                                                         final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(AUTHORIZATION, 팔로워_토큰_정보)
                .body(로드맵_리뷰_생성_요청)
                .post("/api/roadmaps/" + 로드맵_아이디 + "/reviews")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 로드맵_리뷰를_조회한다(final Long 로드맵_아이디, final CustomScrollRequest 스크롤_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .param("lastId", 스크롤_요청.lastId())
                .param("size", 스크롤_요청.size())
                .get("/api/roadmaps/{roadmapId}/reviews", 로드맵_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 정렬된_로드맵_리스트_조회(final RoadmapOrderType 정렬_조건, final int 페이지_크기) {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps?size=" + 페이지_크기 + "&filterCond=" + 정렬_조건.name())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 태그_이름으로_최신순_정렬된_로드맵을_검색한다(final int 페이지_사이즈, final String 태그) {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps/search?size=" + 페이지_사이즈 + "&tagName=" + 태그)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 크리에이터_닉네임으로_정렬된_로드맵을_생성한다(final int 페이지_사이즈, final String 크리에이터_닉네임) {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps/search?size=" + 페이지_사이즈 + "&creatorName=" + 크리에이터_닉네임)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 제목으로_최신순_정렬된_로드맵을_검색한다(final int 페이지_사이즈, final String 로드맵_제목) {
        return given()
                .log().all()
                .when()
                .get("/api/roadmaps/search?size=" + 페이지_사이즈 + "&roadmapTitle=" + 로드맵_제목)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 로드맵_카테고리를_생성한다(final String 로그인_토큰_정보, final RoadmapCategorySaveRequest 카테고리_생성_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .body(카테고리_생성_요청)
                .post("/api/roadmaps/categories")
                .then()
                .log().all()
                .extract();
    }

    public static RoadmapCategory 카테고리_생성(final String 로그인_토큰_정보, final String 카테고리_이름) {
        로드맵_카테고리를_생성한다(로그인_토큰_정보, new RoadmapCategorySaveRequest(카테고리_이름));
        return new RoadmapCategory(1L, 카테고리_이름);
    }

    public static List<RoadmapCategory> 카테고리들_생성(final String 로그인_토큰_정보, final String... 카테고리_이름들) {
        final List<RoadmapCategory> 카테고리들 = new ArrayList<>();
        for (final String 카테고리_이름 : 카테고리_이름들) {
            final RoadmapCategory 카테고리 = 카테고리_생성(로그인_토큰_정보, 카테고리_이름);
            카테고리들.add(카테고리);
        }
        return 카테고리들;
    }
}
