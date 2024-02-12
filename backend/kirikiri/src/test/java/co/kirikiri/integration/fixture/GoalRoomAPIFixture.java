package co.kirikiri.integration.fixture;

import static co.kirikiri.integration.fixture.CommonFixture.API_PREFIX;
import static co.kirikiri.integration.fixture.CommonFixture.AUTHORIZATION;
import static co.kirikiri.integration.fixture.CommonFixture.LOCATION;
import static io.restassured.RestAssured.given;

import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.MemberGoalRoomResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class GoalRoomAPIFixture {

    public static final LocalDate 오늘 = LocalDate.now();
    public static final LocalDate 십일_후 = 오늘.plusDays(10L);
    public static final LocalDate 이십일_후 = 십일_후.plusDays(10L);
    public static final LocalDate 삼십일_후 = 이십일_후.plusDays(10L);
    public static final String 정상적인_골룸_이름 = "GOAL_ROOM_NAME";
    public static final int 정상적인_골룸_제한_인원 = 20;
    public static final String 정상적인_골룸_투두_컨텐츠 = "GOAL_ROOM_TO_DO_CONTENT";
    public static final int 정상적인_골룸_노드_인증_횟수 = (int) ChronoUnit.DAYS.between(오늘, 십일_후);

    public static Long 골룸을_생성하고_아이디를_반환한다(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        final String 골룸_생성_응답_Location_헤더 = 골룸_생성(골룸_생성_요청, 액세스_토큰).response().getHeader(LOCATION);
        return Long.parseLong(골룸_생성_응답_Location_헤더.substring(16));
    }

    public static ExtractableResponse<Response> 골룸_생성(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
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

    public static Long 기본_골룸_생성(final String 액세스_토큰, final RoadmapResponse 로드맵_응답) {
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.content().id(), 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 골룸_노드_별_기간_요청);
        final String Location_헤더 = 골룸_생성(골룸_생성_요청, 액세스_토큰).response().header(LOCATION);
        return Long.parseLong(Location_헤더.substring(16));
    }

    public static ExtractableResponse<Response> 골룸_참가_요청(final Long 골룸_아이디, final String 팔로워_액세스_토큰) {
        return given()
                .log().all()
                .header(AUTHORIZATION, 팔로워_액세스_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 골룸_나가기_요청(final Long 골룸_아이디, final String 골룸_참여자_액세스_토큰) {
        return given()
                .log().all()
                .header(AUTHORIZATION, 골룸_참여자_액세스_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/leave", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 골룸_목록_조회_요청(final Long roadmapId, final LocalDateTime lastValue,
                                                            final int size,
                                                            final String filterCond) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("roadmapId", roadmapId)
                .param("lastCreatedAt", lastValue)
                .param("size", size)
                .param("filterCond", filterCond)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}/goal-rooms", roadmapId)
                .then().log().all()
                .extract();
    }

    public static Long 정상적인_골룸_생성(final String 액세스_토큰, final Long 로드맵_아이디, final Long 로드맵_노드_아이디) {
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드_아이디, 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_노드_별_기간_요청);
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);
        final String Location_헤더 = 골룸_생성_응답.response().header("Location");
        return Long.parseLong(Location_헤더.substring(16));
    }

    public static ExtractableResponse<Response> 골룸_투두리스트_추가(final String 액세스_토큰, final Long 골룸_아이디,
                                                            final GoalRoomTodoRequest 골룸_투두_리스트_추가_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(골룸_투두_리스트_추가_요청)
                .header(new Header(HttpHeaders.AUTHORIZATION, 액세스_토큰))
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static Long 골룸_투두리스트_추가후_아이디를_반환한다(final String 로그인_토큰_정보, final Long 골룸_아이디) {
        final GoalRoomTodoRequest 골룸_투두리스트_추가_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final String 응답_헤더값 = 골룸_투두리스트_추가(로그인_토큰_정보, 골룸_아이디, 골룸_투두리스트_추가_요청)
                .response()
                .getHeader(LOCATION)
                .replace(API_PREFIX + "/goal-rooms/" + 골룸_아이디 + "/todos/", "");
        return Long.valueOf(응답_헤더값);
    }

    public static ExtractableResponse<Response> 골룸_투두리스트를_체크한다(final String 로그인_토큰_정보, final Long 골룸_아이디,
                                                               final Long 투두_아이디) {
        return given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos/{todoId}", 골룸_아이디, 투두_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 인증_피드_등록(final Long 골룸_아이디, final MockMultipartFile 가짜_이미지_객체,
                                                         final CheckFeedRequest 인증_피드_등록_요청, final String 로그인_토큰)
            throws IOException {
        return given().log().all()
                .multiPart(가짜_이미지_객체.getName(), 가짜_이미지_객체.getOriginalFilename(),
                        가짜_이미지_객체.getBytes(), 가짜_이미지_객체.getContentType())
                .formParam("description", 인증_피드_등록_요청.description())
                .header(AUTHORIZATION, 로그인_토큰)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .when()
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 골룸을_시작한다(final String 로그인_토큰, final Long 골룸_아이디) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(new Header(HttpHeaders.AUTHORIZATION, 로그인_토큰))
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/start", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 골룸의_사용자_정보를_정렬_기준없이_조회(final Long 골룸_아이디, final String 로그인_토큰) {
        return given().log().all()
                .header(AUTHORIZATION, 로그인_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/members", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static MemberGoalRoomResponse 사용자의_특정_골룸_정보를_조회한다(final String 로그인_토큰_정보, final Long 골룸_아이디) {
        return given().log().all()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/me", 골룸_아이디)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });
    }

    public static ExtractableResponse<Response> 인증_피드_전체_조회_요청(final String 액세스_토큰, final Long 골룸_아이디) {
        return given().log().all()
                .header(AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 골룸_아이디로_골룸을_조회(final Long 기본_골룸_아이디) {
        return given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}", 기본_골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 골룸_아이디와_토큰으로_골룸_정보를_조회(final Long 기본_골룸_아이디, final String 로그인_토큰) {
        return given()
                .header(AUTHORIZATION, 로그인_토큰)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}", 기본_골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 골룸_투두리스트_조회(final Long 기본_골룸_아이디, final String 로그인_토큰) {
        return given()
                .header(AUTHORIZATION, 로그인_토큰)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 기본_골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 사용자의_모든_골룸_조회(final String 로그인_토큰) {
        return given().log().all()
                .header(AUTHORIZATION, 로그인_토큰)
                .when()
                .get(API_PREFIX + "/goal-rooms/me")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 사용자가_참여한_골룸_중_골룸_진행_상태에_따라_목록을_조회(final String 로그인_토큰,
                                                                                  final String 골룸_진행_상태) {
        return given().log().all()
                .header(AUTHORIZATION, 로그인_토큰)
                .queryParam("statusCond", 골룸_진행_상태)
                .when()
                .get(API_PREFIX + "/goal-rooms/me")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 골룸_노드_조회(final Long 기본_골룸_아이디, final String 로그인_토큰) {
        return given()
                .header(AUTHORIZATION, 로그인_토큰)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/nodes", 기본_골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 골룸의_사용자_정보를_전체_조회(final Long 기본_골룸_아이디, final String 로그인_토큰,
                                                                  final String 정렬조건) {
        return given().log().all()
                .header(AUTHORIZATION, 로그인_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/members?sortCond={sortType}", 기본_골룸_아이디, 정렬조건)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 로드맵_아이디로_골룸_목록_조회(final String 로그인_토큰, final Long 로드맵_아이디,
                                                                  final String 골룸_정렬_조건, final Integer 사이즈) {
        return given().log().all()
                .header(AUTHORIZATION, 로그인_토큰)
                .when()
                .get(API_PREFIX
                                + "/roadmaps/{roadmapId}/goal-rooms?filterCond={orderType}&size={size}",
                        로드맵_아이디, 골룸_정렬_조건, 사이즈)
                .then()
                .log().all()
                .extract();
    }
}
