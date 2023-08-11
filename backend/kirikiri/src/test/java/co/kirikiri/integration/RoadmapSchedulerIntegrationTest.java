package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.integration.helper.IntegrationTestHelper;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.RoadmapScheduler;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class RoadmapSchedulerIntegrationTest extends IntegrationTest {

    private static final LocalDate 오늘 = LocalDate.now();
    private static final LocalDate 십일_후 = 오늘.plusDays(10);
    private static final LocalDate 이십일_후 = 오늘.plusDays(20);
    private static final LocalDate 삼십일_후 = 오늘.plusDays(30);
    private static final int 오늘부터_십일까지_인증_횟수 = (int) ChronoUnit.DAYS.between(오늘, 십일_후);
    private static final int 이십일부터_삼십일까지_인증_횟수 = (int) ChronoUnit.DAYS.between(이십일_후, 삼십일_후);

    private final IntegrationTestHelper testHelper;
    private final RoadmapScheduler roadmapScheduler;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapSchedulerIntegrationTest(final IntegrationTestHelper testHelper,
                                           final RoadmapScheduler roadmapScheduler,
                                           final RoadmapRepository roadmapRepository,
                                           final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.testHelper = testHelper;
        this.roadmapScheduler = roadmapScheduler;
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 삭제된_상태의_로드맵을_삭제한다() {
        // given
        회원가입을_한다("creator1", "password1!", "creator", "010-1111-1000", GenderType.FEMALE, LocalDate.now());
        final String 로그인_토큰 = 로그인을_하고_토큰을_받는다("creator1", "password1!");

        final RoadmapCategory 로드맵_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디1 = 로드맵을_생성하고_아이디를_받는다(로그인_토큰, 로드맵_카테고리.getId(), "로드맵 제목1", "로드맵 소개글1", "로드맵 본문1",
                RoadmapDifficultyType.DIFFICULT, 60, List.of(
                        new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용"),
                        new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용")));
        final RoadmapResponse 로드맵1_응답 = 로드맵을_조회한다(로드맵_아이디1);
        final Long 로드맵1_첫번째_노드_아이디 = 로드맵1_응답.content().nodes().get(0).id();
        final Long 로드맵1_두번째_노드_아이디 = 로드맵1_응답.content().nodes().get(1).id();
        final Long 로드맵_아이디2 = 로드맵을_생성하고_아이디를_받는다(로그인_토큰, 로드맵_카테고리.getId(), "로드맵 제목2", "로드맵 소개글2", "로드맵 본문2",
                RoadmapDifficultyType.DIFFICULT, 60, List.of(
                        new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용"),
                        new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용")));
        final RoadmapResponse 로드맵2_응답 = 로드맵을_조회한다(로드맵_아이디2);
        final Long 로드맵2_첫번째_노드_아이디 = 로드맵2_응답.content().nodes().get(0).id();
        final Long 로드맵2_두번째_노드_아이디 = 로드맵2_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest("골룸 투두", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 로드맵1에_대한_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵1_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵1_두번째_노드_아이디, 이십일부터_삼십일까지_인증_횟수, 이십일_후, 삼십일_후));
        final GoalRoomCreateRequest 로드맵1에_대한_골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디1, "골룸", 10, 골룸_투두_요청,
                로드맵1에_대한_골룸_노드_별_기간_요청);
        final List<GoalRoomRoadmapNodeRequest> 로드맵2에_대한_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵2_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵2_두번째_노드_아이디, 이십일부터_삼십일까지_인증_횟수, 이십일_후, 삼십일_후));
        final GoalRoomCreateRequest 로드맵2에_대한_골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디2, "골룸", 10, 골룸_투두_요청,
                로드맵2에_대한_골룸_노드_별_기간_요청);
        final Long 골룸_아이디1 = 골룸을_생성하고_아이디를_알아낸다(로드맵1에_대한_골룸_생성_요청, 로그인_토큰);
        final Long 골룸_아이디2 = 골룸을_생성하고_아이디를_알아낸다(로드맵1에_대한_골룸_생성_요청, 로그인_토큰);
        final Long 골룸_아이디3 = 골룸을_생성하고_아이디를_알아낸다(로드맵2에_대한_골룸_생성_요청, 로그인_토큰);

        로드맵을_삭제한다(로드맵_아이디1, 로그인_토큰);
        로드맵을_삭제한다(로드맵_아이디2, 로그인_토큰);

        testHelper.골룸을_완료상태로_변경하고_종료날짜를_변경한다(골룸_아이디1, LocalDate.now().minusMonths(4));
        testHelper.골룸을_완료상태로_변경하고_종료날짜를_변경한다(골룸_아이디2, LocalDate.now().minusMonths(4));
        testHelper.골룸을_완료상태로_변경하고_종료날짜를_변경한다(골룸_아이디3, LocalDate.now().minusMonths(4));

        // when
        roadmapScheduler.deleteRoadmaps();

        // then
        // 로드맵 목록 조회 API는 CREATED 상태의 로드맵만 조회하기 때문에 사용할 수 없음
        assertThat(roadmapRepository.findAll()).hasSize(0);
    }

    @Test
    void 삭제된_상태의_로드맵_삭제시_종료되지_않은_골룸이_있으면_삭제되지_않는다() {
        // given
        회원가입을_한다("creator1", "password1!", "creator", "010-1111-1000", GenderType.FEMALE, LocalDate.now());
        final String 로그인_토큰 = 로그인을_하고_토큰을_받는다("creator1", "password1!");

        final RoadmapCategory 로드맵_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디1 = 로드맵을_생성하고_아이디를_받는다(로그인_토큰, 로드맵_카테고리.getId(), "로드맵 제목1", "로드맵 소개글1", "로드맵 본문1",
                RoadmapDifficultyType.DIFFICULT, 60, List.of(
                        new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용"),
                        new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용")));
        final RoadmapResponse 로드맵1_응답 = 로드맵을_조회한다(로드맵_아이디1);
        final Long 로드맵1_첫번째_노드_아이디 = 로드맵1_응답.content().nodes().get(0).id();
        final Long 로드맵1_두번째_노드_아이디 = 로드맵1_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest("골룸 투두", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 로드맵1에_대한_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵1_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵1_두번째_노드_아이디, 이십일부터_삼십일까지_인증_횟수, 이십일_후, 삼십일_후));
        final GoalRoomCreateRequest 로드맵1에_대한_골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디1, "골룸", 10, 골룸_투두_요청,
                로드맵1에_대한_골룸_노드_별_기간_요청);

        final Long 골룸_아이디1 = 골룸을_생성하고_아이디를_알아낸다(로드맵1에_대한_골룸_생성_요청, 로그인_토큰);
        골룸을_생성하고_아이디를_알아낸다(로드맵1에_대한_골룸_생성_요청, 로그인_토큰);

        로드맵을_삭제한다(로드맵_아이디1, 로그인_토큰);

        testHelper.골룸을_완료상태로_변경하고_종료날짜를_변경한다(골룸_아이디1, LocalDate.now().minusMonths(4));

        // when
        roadmapScheduler.deleteRoadmaps();

        // then
        assertThat(roadmapRepository.findAll()).hasSize(1);
    }

    @Test
    void 삭제된_상태의_로드맵_삭제시_종료된지_3개월이_지나지_않은_골룸이_있으면_삭제되지_않는다() {
        // given
        회원가입을_한다("creator1", "password1!", "creator", "010-1111-1000", GenderType.FEMALE, LocalDate.now());
        final String 로그인_토큰 = 로그인을_하고_토큰을_받는다("creator1", "password1!");

        final RoadmapCategory 로드맵_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디1 = 로드맵을_생성하고_아이디를_받는다(로그인_토큰, 로드맵_카테고리.getId(), "로드맵 제목1", "로드맵 소개글1", "로드맵 본문1",
                RoadmapDifficultyType.DIFFICULT, 60, List.of(
                        new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용"),
                        new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용")));
        final RoadmapResponse 로드맵1_응답 = 로드맵을_조회한다(로드맵_아이디1);
        final Long 로드맵1_첫번째_노드_아이디 = 로드맵1_응답.content().nodes().get(0).id();
        final Long 로드맵1_두번째_노드_아이디 = 로드맵1_응답.content().nodes().get(1).id();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest("골룸 투두", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 로드맵1에_대한_골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵1_첫번째_노드_아이디, 오늘부터_십일까지_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵1_두번째_노드_아이디, 이십일부터_삼십일까지_인증_횟수, 이십일_후, 삼십일_후));
        final GoalRoomCreateRequest 로드맵1에_대한_골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디1, "골룸", 10, 골룸_투두_요청,
                로드맵1에_대한_골룸_노드_별_기간_요청);

        final Long 골룸_아이디1 = 골룸을_생성하고_아이디를_알아낸다(로드맵1에_대한_골룸_생성_요청, 로그인_토큰);
        골룸을_생성하고_아이디를_알아낸다(로드맵1에_대한_골룸_생성_요청, 로그인_토큰);

        로드맵을_삭제한다(로드맵_아이디1, 로그인_토큰);

        testHelper.골룸을_완료상태로_변경하고_종료날짜를_변경한다(골룸_아이디1, LocalDate.now().minusMonths(0));

        // when
        roadmapScheduler.deleteRoadmaps();

        // then
        assertThat(roadmapRepository.findAll()).hasSize(1);
    }

    private void 회원가입을_한다(final String 아이디, final String 비밀번호, final String 닉네임, final String 전화번호, final GenderType 성별,
                          final LocalDate 생년월일) {
        final MemberJoinRequest 회원가입_요청값 = new MemberJoinRequest(아이디, 비밀번호, 닉네임, 전화번호, 성별, 생년월일);
        회원가입_요청(회원가입_요청값);
    }

    private ExtractableResponse<Response> 회원가입_요청(final MemberJoinRequest 회원가입_요청값) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청값)
                .post(API_PREFIX + "/members/join")
                .then().log().all()
                .extract();
    }

    private String 로그인을_하고_토큰을_받는다(final String 아이디, final String 비밀번호) {
        final LoginRequest 로그인_요청값 = new LoginRequest(아이디, 비밀번호);
        final ExtractableResponse<Response> 로그인_응답값 = 로그인_요청(로그인_요청값);
        return 액세스_토큰을_받는다(로그인_응답값);
    }

    private String 액세스_토큰을_받는다(final ExtractableResponse<Response> 로그인_응답) {
        final AuthenticationResponse 토큰_응답값 = 로그인_응답.as(new TypeRef<>() {
        });
        return String.format(BEARER_TOKEN_FORMAT, 토큰_응답값.accessToken());
    }

    private ExtractableResponse<Response> 로그인_요청(final LoginRequest 로그인_요청값) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(로그인_요청값)
                .post(API_PREFIX + "/auth/login")
                .then().log().all()
                .extract();
    }

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String 카테고리_이름) {
        final RoadmapCategory 로드맵_카테고리 = new RoadmapCategory(카테고리_이름);
        return roadmapCategoryRepository.save(로드맵_카테고리);
    }

    private Long 로드맵을_생성하고_아이디를_받는다(final String 토큰, final Long 카테고리_아이디, final String 로드맵_제목, final String 로드맵_소개글,
                                    final String 로드맵_본문, final RoadmapDifficultyType 난이도, final int 추천_소요_기간,
                                    final List<RoadmapNodeSaveRequest> 로드맵_노드들) {
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리_아이디, 로드맵_제목, 로드맵_소개글, 로드맵_본문,
                난이도, 추천_소요_기간, 로드맵_노드들, List.of(new RoadmapTagSaveRequest("태그")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 토큰);
        return Long.parseLong(로드맵_생성_응답값.header(HttpHeaders.LOCATION).split("/")[3]);
    }

    private ExtractableResponse<Response> 로드맵_생성_요청(final RoadmapSaveRequest 로드맵_생성_요청값, final String accessToken) {
        return given().log().all()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(로드맵_생성_요청값).log().all()
                .post(API_PREFIX + "/roadmaps")
                .then().log().all()
                .extract();
    }

    private RoadmapResponse 로드맵을_조회한다(final Long 로드맵_아이디) {
        return given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}", 로드맵_아이디)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });
    }

    private Long 골룸을_생성하고_아이디를_알아낸다(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        final ExtractableResponse<Response> 골룸_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);
        final String Location_헤더 = 골룸_응답.response().header("Location");
        return Long.parseLong(Location_헤더.substring(16));
    }

    private ExtractableResponse<Response> 골룸_생성(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(골룸_생성_요청)
                .header(HttpHeaders.AUTHORIZATION, 액세스_토큰)
                .post(API_PREFIX + "/goal-rooms")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 로드맵을_삭제한다(final Long 삭제할_로드맵_아이디, final String 로그인_토큰) {
        return given().log().all()
                .header(HttpHeaders.AUTHORIZATION, 로그인_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .delete(API_PREFIX + "/roadmaps/{roadmapId}", 삭제할_로드맵_아이디)
                .then().log().all()
                .extract();
    }
}
