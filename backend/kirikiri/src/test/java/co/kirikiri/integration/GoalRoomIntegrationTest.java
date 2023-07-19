package co.kirikiri.integration;

import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodePeriodRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.member.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class GoalRoomIntegrationTest extends IntegrationTest {

    private static final String 정상적인_골룸_이름 = "GOAL_ROOM_NAME";
    private static final int 정상적인_골룸_제한_인원 = 20;
    private static final String 정상적인_골룸_투두_컨텐츠 = "GOAL_ROOM_TO_DO_CONTENT";
    private static final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", "password12!@#$%", "nickname", "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
    private static final LoginRequest 로그인_요청 = new LoginRequest(회원가입_요청.identifier(), 회원가입_요청.password());
    private static final String BEARER = "BEARER ";
    private static final String 카테고리_이름 = "여가";
    private static final LocalDate 오늘 = LocalDate.now();
    private static final LocalDate 십일_후 = 오늘.plusDays(10L);

    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public GoalRoomIntegrationTest(final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 정상적으로_골룸을_생성한다() {
        //given
        회원가입(회원가입_요청);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);
        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        final String 액세스_토큰 = BEARER + 로그인_응답_바디.accessToken();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final ExtractableResponse<Response> 로드맵_응답 = 로드맵_생성(로드맵_생성_요청, 액세스_토큰);
        final String Location_헤더 = 로드맵_응답.response().header("Location");
        final Long 로드맵_id = Long.parseLong(Location_헤더.substring(10));


        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodePeriodRequest> 골룸_노드_별_기간_요청 = List.of(new GoalRoomRoadmapNodePeriodRequest(오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(골룸_생성_응답.response().header("Location")).isNotNull();
    }

    @Test
    void 골룸_생성_시_컨테츠id가_빈값일_경우() {
        //given
        회원가입(회원가입_요청);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);
        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        final String 액세스_토큰 = BEARER + 로그인_응답_바디.accessToken();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodePeriodRequest> 골룸_노드_별_기간_요청 = List.of(new GoalRoomRoadmapNodePeriodRequest(오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(null, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("컨텐츠 아이디는 빈 값일 수 없습니다.");
    }

    @Test
    void 골룸_생성_시_골름_이름이_빈값일_경우() {
        //given
        회원가입(회원가입_요청);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);
        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        final String 액세스_토큰 = BEARER + 로그인_응답_바디.accessToken();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodePeriodRequest> 골룸_노드_별_기간_요청 = List.of(new GoalRoomRoadmapNodePeriodRequest(오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(1L, null, 정상적인_골룸_제한_인원, 골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("골룸 이름을 빈 값일 수 없습니다.");
    }

    @Test
    void 골룸_생성_시_골룸_제한_인원이_빈값일_경우() {
        //given
        회원가입(회원가입_요청);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);
        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        final String 액세스_토큰 = BEARER + 로그인_응답_바디.accessToken();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodePeriodRequest> 골룸_노드_별_기간_요청 = List.of(new GoalRoomRoadmapNodePeriodRequest(오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(1L, 정상적인_골룸_이름, null, 골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("골룸 제한 인원은 빈 값일 수 없습니다.");
    }

    @Test
    void 골룸_생성_시_최초_todo_빈값일_경우() {
        //given
        회원가입(회원가입_요청);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);
        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        final String 액세스_토큰 = BEARER + 로그인_응답_바디.accessToken();

        final List<GoalRoomRoadmapNodePeriodRequest> 골룸_노드_별_기간_요청 = List.of(new GoalRoomRoadmapNodePeriodRequest(오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(1L, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, null, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("최초 todo는 필수 값입니다.");
    }

    @Test
    void 골룸_생성_시_각_노드에_대한_기간_빈값일_경우() {
        //given
        회원가입(회원가입_요청);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);
        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        final String 액세스_토큰 = BEARER + 로그인_응답_바디.accessToken();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodePeriodRequest> 골룸_노드_별_기간_요청 = List.of(new GoalRoomRoadmapNodePeriodRequest(오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(1L, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청, null);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("각 노드에 대한 기간은 필수 값입니다.");
    }

    @Test
    void 골룸_생성_시_골룸_이름이_40자_초과인_경우() {
        //given
        회원가입(회원가입_요청);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);
        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        final String 액세스_토큰 = BEARER + 로그인_응답_바디.accessToken();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final ExtractableResponse<Response> 로드맵_응답 = 로드맵_생성(로드맵_생성_요청, 액세스_토큰);
        final String Location_헤더 = 로드맵_응답.response().header("Location");
        final Long 로드맵_id = Long.parseLong(Location_헤더.substring(10));

        final String 적절하지_않은_길이의_골룸_이름 = "a".repeat(41);
        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodePeriodRequest> 골룸_노드_별_기간_요청 = List.of(new GoalRoomRoadmapNodePeriodRequest(오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 적절하지_않은_길이의_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("골룸 이름의 길이가 적절하지 않습니다.");
    }

    @Test
    void 골룸_생성_시_노드_별_기간_수와_로드맵_노드의_수가_맞지_않을때() {
        //given
        회원가입(회원가입_요청);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);
        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        final String 액세스_토큰 = BEARER + 로그인_응답_바디.accessToken();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final ExtractableResponse<Response> 로드맵_응답 = 로드맵_생성(로드맵_생성_요청, 액세스_토큰);
        final String Location_헤더 = 로드맵_응답.response().header("Location");
        final Long 로드맵_id = Long.parseLong(Location_헤더.substring(10));


        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodePeriodRequest> 골룸_노드_별_기간_요청 = List.of(new GoalRoomRoadmapNodePeriodRequest(오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("노드 별 기간의 수가 적절하지 않습니다.");
    }

    @Test
    void 골룸_생성_시_제한_인원이_20명_초과일때() {
        //given
        회원가입(회원가입_요청);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);
        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        final String 액세스_토큰 = BEARER + 로그인_응답_바디.accessToken();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final ExtractableResponse<Response> 로드맵_응답 = 로드맵_생성(로드맵_생성_요청, 액세스_토큰);
        final String Location_헤더 = 로드맵_응답.response().header("Location");
        final Long 로드맵_id = Long.parseLong(Location_헤더.substring(10));


        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodePeriodRequest> 골룸_노드_별_기간_요청 = List.of(new GoalRoomRoadmapNodePeriodRequest(오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 21, 골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("제한 인원 수가 적절하지 않습니다.");
    }

    private ExtractableResponse<Response> 회원가입(final MemberJoinRequest 회원가입_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청)
                .post(API_PREFIX + "/members/join")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 로그인(final LoginRequest 로그인_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(로그인_요청)
                .post(API_PREFIX + "/auth/login")
                .then()
                .log().all()
                .extract();
    }

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String 로드맵_카테고리_이름) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(로드맵_카테고리_이름);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private ExtractableResponse<Response> 로드맵_생성(final RoadmapSaveRequest 로드맵_생성_요청, final String 액세스_토큰) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(로드맵_생성_요청)
                .header(new Header(HttpHeaders.AUTHORIZATION, 액세스_토큰))
                .post(API_PREFIX + "/roadmaps")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 골룸_생성(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(골룸_생성_요청)
                .header(new Header(HttpHeaders.AUTHORIZATION, 액세스_토큰))
                .post(API_PREFIX + "/goal-rooms")
                .then()
                .log().all()
                .extract();
    }
}
