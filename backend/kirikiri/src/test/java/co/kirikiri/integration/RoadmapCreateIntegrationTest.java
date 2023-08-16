package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class RoadmapCreateIntegrationTest extends MemberReadIntegrationTest {

    protected RoadmapCategory 기본_카테고리;
    protected RoadmapSaveRequest 기본_로드맵_생성_요청;

    protected final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapCreateIntegrationTest(final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Override
    @BeforeEach
    void init() {
        super.init();
        기본_카테고리 = 로드맵_카테고리를_저장한다("여행");
        기본_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));
    }

    @Test
    void 정상적으로_로드맵을_생성한다() throws IOException {
        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);

        // expect
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.CREATED);
        final Long 로드맵_아이디 = 아이디를_반환한다(로드맵_생성_응답값);
        assertThat(로드맵_아이디).isEqualTo(1L);
    }

    @Test
    void 본문의_값이_없는_로드맵이_정상적으로_생성한다() throws IOException {
        // given
        final String 로드맵_본문 = null;
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", "로드맵 소개글", 로드맵_본문,
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용", Collections.emptyList())),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.CREATED);
        final Long 로드맵_아이디 = 아이디를_반환한다(로드맵_생성_응답값);
        assertThat(로드맵_아이디).isEqualTo(1L);
    }

    @Test
    void 로드맵_생성시_잘못된_빈값을_넘기면_실패한다() throws IOException {
        // given
        final Long 카테고리_아이디 = null;
        final String 로드맵_제목 = null;
        final String 로드맵_소개글 = null;
        final RoadmapDifficultyType 로드맵_난이도 = null;
        final Integer 추천_소요_기간 = null;
        final String 로드맵_노드_제목 = null;
        final String 로드맵_노드_설명 = null;

        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리_아이디, 로드맵_제목, 로드맵_소개글, "로드맵 본문",
                로드맵_난이도, 추천_소요_기간,
                List.of(new RoadmapNodeSaveRequest(로드맵_노드_제목, 로드맵_노드_설명, Collections.emptyList())),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        final List<ErrorResponse> 에러_메시지들 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메시지들)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(
                        new ErrorResponse("카테고리를 입력해주세요."),
                        new ErrorResponse("로드맵의 제목을 입력해주세요."),
                        new ErrorResponse("로드맵의 소개글을 입력해주세요."),
                        new ErrorResponse("난이도를 입력해주세요."),
                        new ErrorResponse("추천 소요 기간을 입력해주세요."),
                        new ErrorResponse("로드맵 노드의 제목을 입력해주세요."),
                        new ErrorResponse("로드맵 노드의 설명을 입력해주세요.")));
    }

    @Test
    void 존재하지_않는_카테고리_아이디를_입력한_경우_실패한다() throws IOException {
        // given
        final long 카테고리_아이디 = 2L;

        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리_아이디, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용", Collections.emptyList())),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.NOT_FOUND);
        assertThat(에러_메세지.message()).isEqualTo("존재하지 않는 카테고리입니다. categoryId = 2");

    }

    @Test
    void 제목의_길이가_40보다_크면_실패한다() throws IOException {
        // given
        final String 로드맵_제목 = "a".repeat(41);

        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(기본_카테고리.getId(), 로드맵_제목, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용", Collections.emptyList())),
                List.of(new RoadmapTagSaveRequest("태그")));

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("로드맵 제목의 길이는 최소 1글자, 최대 40글자입니다.");
    }

    @Test
    void 소개글의_길이가_150보다_크면_실패한다() throws IOException {
        // given
        final String 로드맵_소개글 = "a".repeat(151);

        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", 로드맵_소개글, "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용", Collections.emptyList())),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("로드맵 소개글의 길이는 최소 1글자, 최대 150글자입니다.");
    }

    @Test
    void 본문의_길이가_2000보다_크면_실패한다() throws IOException {
        // given
        final String 로드맵_본문 = "a".repeat(2001);

        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", "로드맵 소개글", 로드맵_본문,
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용", Collections.emptyList())),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("로드맵 본문의 길이는 최대 2000글자입니다.");
    }

    @Test
    void 추천_소요_기간이_0보다_작으면_실패한다() throws IOException {
        // given
        final Integer 추천_소요_기간 = -1;

        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 추천_소요_기간,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용", Collections.emptyList())),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("로드맵 추천 소요 기간은 최소 0일, 최대 1000일입니다.");
    }

    @Test
    void 로드맵_노드를_입력하지_않으면_실패한다() throws IOException {
        // given
        final List<RoadmapNodeSaveRequest> 로드맵_노드들 = null;

        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30, 로드맵_노드들, List.of(new RoadmapTagSaveRequest("태그1")));

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        final List<ErrorResponse> 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.get(0).message()).isEqualTo("로드맵의 첫 번째 단계를 입력해주세요.");
    }

    @Test
    void 로드맵_노드의_제목의_길이가_40보다_크면_실패한다() throws IOException {
        // given
        final String 로드맵_노드_제목 = "a".repeat(41);
        final List<RoadmapNodeSaveRequest> 로드맵_노드들 = List.of(
                new RoadmapNodeSaveRequest(로드맵_노드_제목, "로드맵 1주차 내용", Collections.emptyList()));

        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30, 로드맵_노드들, List.of(new RoadmapTagSaveRequest("태그1")));

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("로드맵 노드의 제목의 길이는 최소 1글자, 최대 40글자입니다.");
    }

    @Test
    void 로드맵_노드의_설명의_길이가_2000보다_크면_실패한다() throws IOException {
        // given
        final String 로드맵_노드_설명 = "a".repeat(2001);
        final List<RoadmapNodeSaveRequest> 로드맵_노드들 = List.of(
                new RoadmapNodeSaveRequest("로드맵 노드 제목", 로드맵_노드_설명, Collections.emptyList()));
        로드맵_카테고리를_저장한다("여행");

        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30, 로드맵_노드들, List.of(new RoadmapTagSaveRequest("태그1")));

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("로드맵 노드의 설명의 길이는 최소 1글자, 최대 2000글자입니다.");
    }

    @Test
    void 로드맵_태그_이름이_중복되면_예외가_발생한다() throws IOException {
        // given
        final List<RoadmapTagSaveRequest> 태그_저장_요청 = List.of(new RoadmapTagSaveRequest("태그"),
                new RoadmapTagSaveRequest("태그"));

        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 노드 제목", "로드맵 노드 설명", Collections.emptyList())), 태그_저장_요청);

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("태그 이름은 중복될 수 없습니다.");
    }

    @Test
    void 로드맵_태그_개수가_5개_초과면_예외가_발생한다() throws IOException {
        // given
        final List<RoadmapTagSaveRequest> 태그_저장_요청 = List.of(new RoadmapTagSaveRequest("태그1"),
                new RoadmapTagSaveRequest("태그2"), new RoadmapTagSaveRequest("태그3"),
                new RoadmapTagSaveRequest("태그4"), new RoadmapTagSaveRequest("태그5"),
                new RoadmapTagSaveRequest("태그6"));

        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 노드 제목", "로드맵 노드 설명", Collections.emptyList())), 태그_저장_요청);

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("태그의 개수는 최대 5개까지 가능합니다.");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    void 로드맵_태그_이름의_길이가_1자_미만_10자_초과면_예외가_발생한다(final int nameLength) throws IOException {
        // given
        final String 태그_이름 = "a".repeat(nameLength);
        final List<RoadmapTagSaveRequest> 태그_저장_요청 = List.of(new RoadmapTagSaveRequest(태그_이름));

        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 노드 제목", "로드맵 노드 설명", Collections.emptyList())), 태그_저장_요청);

        // when
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청값, 기본_로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("태그 이름은 최소 1자부터 최대 10자까지 가능합니다.");
    }

    private ExtractableResponse<Response> 요청을_받는_이미지가_포함된_로드맵_생성(final RoadmapSaveRequest 로드맵_생성_요청값,
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

    private RequestSpecification makeRequestSpecification(final RoadmapSaveRequest 로드맵_생성_요청값,
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

    protected Long 로드맵_생성(final RoadmapSaveRequest 로드맵_생성_요청, final String 액세스_토큰) throws IOException {
        if (기본_카테고리 == null) {
            기본_카테고리 = 로드맵_카테고리를_저장한다("여행");
        }
        final Response 응답 = 요청을_받는_이미지가_포함된_로드맵_생성(로드맵_생성_요청, 액세스_토큰).response();
        return Long.parseLong(응답.header(HttpHeaders.LOCATION).split("/")[3]);
    }

    protected Long 기본_로드맵_생성(final String 액세스_토큰) throws IOException {
        if (기본_카테고리 == null) {
            기본_카테고리 = 로드맵_카테고리를_저장한다("여행");
        }
        기본_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("roadmap 1st week", "로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));
        return 로드맵_생성(기본_로드맵_생성_요청, 액세스_토큰);
    }

    protected RoadmapCategory 로드맵_카테고리를_저장한다(final String 카테고리_이름) {
        final RoadmapCategory 로드맵_카테고리 = new RoadmapCategory(카테고리_이름);
        return roadmapCategoryRepository.save(로드맵_카테고리);
    }

    private void 응답_상태_코드_검증(final ExtractableResponse<Response> 응답, final HttpStatus http_상태) {
        assertThat(응답.statusCode()).isEqualTo(http_상태.value());
    }

    private Long 아이디를_반환한다(final ExtractableResponse<Response> 응답) {
        return Long.parseLong(응답.header(HttpHeaders.LOCATION).split("/")[3]);
    }
}
