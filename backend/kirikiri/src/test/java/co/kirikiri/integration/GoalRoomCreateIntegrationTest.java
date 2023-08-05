package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapNodeRepository;
import co.kirikiri.service.dto.ErrorResponse;
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
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

class GoalRoomCreateIntegrationTest extends IntegrationTest {

    private static final String 정상적인_골룸_이름 = "GOAL_ROOM_NAME";
    private static final int 정상적인_골룸_제한_인원 = 20;
    private static final String 정상적인_골룸_투두_컨텐츠 = "GOAL_ROOM_TO_DO_CONTENT";
    private static final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", "password12!@#$%", "nickname",
            "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
    private static final LoginRequest 로그인_요청 = new LoginRequest(회원가입_요청.identifier(), 회원가입_요청.password());
    private static final String BEARER = "Bearer ";
    private static final String 카테고리_이름 = "여가";
    private static final LocalDate 오늘 = LocalDate.now();
    private static final LocalDate 십일_후 = 오늘.plusDays(10L);
    private static final int 정상적인_골룸_노드_인증_횟수 = (int) ChronoUnit.DAYS.between(오늘, 십일_후);

    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final GoalRoomRepository goalRoomRepository;

    public GoalRoomCreateIntegrationTest(final RoadmapCategoryRepository roadmapCategoryRepository,
                                         final RoadmapNodeRepository roadmapNodeRepository,
                                         final GoalRoomRepository goalRoomRepository) {
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.goalRoomRepository = goalRoomRepository;
    }

    @Test
    void 정상적으로_골룸을_생성한다() throws IOException {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final Long 로드맵_id = 로드맵_생성(액세스_토큰, 카테고리);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(골룸_생성_응답.response().header("Location")).contains("/api/goal-rooms/");
    }

    @Test
    void 골룸_생성_시_컨텐츠_id가_빈값일_경우() throws IOException {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));
        로드맵_생성(로드맵_생성_요청, 액세스_토큰);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 20, 오늘, 십일_후));
        final GoalRoomCreateRequest 로드맵_id가_빈값인_골룸_생성_요청 = new GoalRoomCreateRequest(null, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(로드맵_id가_빈값인_골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final List<ErrorResponse> 골룸_생성_응답_바디 = jsonToClass(골룸_생성_응답.asString(), new TypeReference<>() {
        });
        assertThat(골룸_생성_응답_바디).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(new ErrorResponse("로드맵 컨텐츠 아이디는 빈 값일 수 없습니다.")));
    }

    @Test
    void 골룸_생성_시_골룸_이름이_빈값일_경우() throws IOException {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));
        로드맵_생성(로드맵_생성_요청, 액세스_토큰);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 20, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_이름이_빈값인_골룸_생성_요청 = new GoalRoomCreateRequest(1L, null, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_이름이_빈값인_골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final List<ErrorResponse> 골룸_생성_응답_바디 = jsonToClass(골룸_생성_응답.asString(), new TypeReference<>() {
        });
        assertThat(골룸_생성_응답_바디).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(new ErrorResponse("골룸 이름을 빈 값일 수 없습니다.")));
    }

    @Test
    void 골룸_생성_시_골룸_제한_인원이_빈값일_경우() throws IOException {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));
        로드맵_생성(로드맵_생성_요청, 액세스_토큰);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 20, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_제한_인원이_빈값인_골룸_생성_요청 = new GoalRoomCreateRequest(1L, 정상적인_골룸_이름, null, 골룸_투두_요청,
                골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_제한_인원이_빈값인_골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final List<ErrorResponse> 골룸_생성_응답_바디 = jsonToClass(골룸_생성_응답.asString(), new TypeReference<>() {
        });
        assertThat(골룸_생성_응답_바디).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(new ErrorResponse("골룸 제한 인원은 빈 값일 수 없습니다.")));
    }

    @Test
    void 골룸_생성_시_골룸_이름이_40자_초과인_경우() throws IOException {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final Long 로드맵_id = 로드맵_생성(액세스_토큰, 카테고리);

        final String 적절하지_않은_길이의_골룸_이름 = "a".repeat(41);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 20, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 적절하지_않은_길이의_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("골룸 이름의 길이가 적절하지 않습니다.");
    }

    @Test
    void 골룸_생성_시_노드_별_기간_수와_로드맵_노드의_수가_맞지_않을때() throws IOException {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용", null),
                        new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));
        final Long 로드맵_id = 로드맵을_생성하고_id를_알아낸다(액세스_토큰, 로드맵_생성_요청);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 20, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("모든 노드에 대해 기간이 설정돼야 합니다.");
    }

    @Test
    void 골룸_생성_시_제한_인원이_20명_초과일때() throws IOException {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final Long 로드맵_id = 로드맵_생성(액세스_토큰, 카테고리);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 20, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 21, 골룸_투두_요청,
                골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("제한 인원 수가 적절하지 않습니다.");
    }

    //    @Test
//    void 골룸에_참가_요청을_보낸다() {
//        //given
//        final Member 크리에이터 = 크리에이터를_저장한다();
//        final String 크리에이터_로그인_토큰_정보 = 로그인(IDENTIFIER);
//        final RoadmapCategory 놀라운_카테고리 = 로드맵_카테고리를_저장한다("놀라운 카테고리");
//        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(크리에이터_로그인_토큰_정보, 놀라운_카테고리, "획기적인 로드맵");
//        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
//        final List<RoadmapContent> 로드맵_본문_리스트 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 놀라운_카테고리, 로드맵_응답).getValues();
//        final GoalRoom 골룸 = 골룸을_저장한다(로드맵_본문_리스트, 크리에이터, 5, GoalRoomStatus.RECRUITING);
//
//        final String 팔로워_아이디 = "identifier2";
//        팔로워를_저장한다(팔로워_아이디, "끼리코");
//        final String 팔로워_로그인_토큰_정보 = 로그인(팔로워_아이디);
//
//        //when
//        final ExtractableResponse<Response> 참가_요청에_대한_응답 = given()
//                .log().all()
//                .header(AUTHORIZATION, 팔로워_로그인_토큰_정보)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", 골룸.getId())
//                .then()
//                .log().all()
//                .extract();
//
//        //then
//        assertThat(참가_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
//    }
    @Test
    void 골룸에_참가_요청을_보낸다() throws IOException {
        //given
        final String 리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final Long 로드맵_id = 로드맵_생성(리더_액세스_토큰, 카테고리);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 리더_액세스_토큰);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final String 팔로워_액세스_토큰 = 회원을_생성하고_로그인을_한다(팔로워_회원_가입_요청, 팔로워_로그인_요청);

        // when
        final ExtractableResponse<Response> 골룸_참가_요청_응답 = 골룸_참가_요청(골룸_아이디, 팔로워_액세스_토큰);

        //then
        assertThat(골룸_참가_요청_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 존재하지_않는_골룸_아이디로_참가_요청을_보내면_예외가_발생한다() {
        //given
        final Long 존재하지_않는_골룸_아이디 = 1L;
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);

        //when
        final ExtractableResponse<Response> 골룸_참가_요청_응답 = 골룸_참가_요청(존재하지_않는_골룸_아이디, 액세스_토큰);

        //then
        final String 예외_메시지 = 골룸_참가_요청_응답.asString();

        assertAll(
                () -> assertThat(골룸_참가_요청_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(예외_메시지).contains("존재하지 않는 골룸입니다. goalRoomId = " + 1)
        );
    }

    @Test
    void 인원이_가득_찬_골룸에_참가_요청을_보내면_예외가_발생한다() throws IOException {
        //given
        final String 리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final Long 로드맵_id = 로드맵_생성(리더_액세스_토큰, 카테고리);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 1, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 리더_액세스_토큰);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final String 팔로워_액세스_토큰 = 회원을_생성하고_로그인을_한다(팔로워_회원_가입_요청, 팔로워_로그인_요청);

        //when
        final ExtractableResponse<Response> 참가_요청에_대한_응답 = 골룸_참가_요청(골룸_아이디, 팔로워_액세스_토큰);

        //then
        final String 예외_메시지 = 참가_요청에_대한_응답.asString();

        assertAll(
                () -> assertThat(참가_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메시지).contains("제한 인원이 꽉 찬 골룸에는 참여할 수 없습니다.")
        );
    }

    @Test
    void 모집_중이지_않은_골룸에_참가_요청을_보내면_예외가_발생한다() throws IOException {
        //given
        final String 리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final Long 로드맵_id = 로드맵_생성(리더_액세스_토큰, 카테고리);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 리더_액세스_토큰);
        final GoalRoom 골룸 = goalRoomRepository.findById(골룸_아이디).get();
        골룸.start();
        goalRoomRepository.save(골룸);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final String 팔로워_액세스_토큰 = 회원을_생성하고_로그인을_한다(팔로워_회원_가입_요청, 팔로워_로그인_요청);

        //when
        final ExtractableResponse<Response> 참가_요청에_대한_응답 = 골룸_참가_요청(골룸_아이디, 팔로워_액세스_토큰);

        //then
        final String 예외_메시지 = 참가_요청에_대한_응답.asString();

        assertAll(
                () -> assertThat(참가_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메시지).contains("모집 중이지 않은 골룸에는 참여할 수 없습니다.")
        );
    }

    @Test
    void 이미_참여한_골룸에_참가_요청을_보내면_예외가_발생한다() throws IOException {
        //given
        final String 리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final Long 로드맵_id = 로드맵_생성(리더_액세스_토큰, 카테고리);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 리더_액세스_토큰);

        //when
        final ExtractableResponse<Response> 참가_요청에_대한_응답 = 골룸_참가_요청(골룸_아이디, 리더_액세스_토큰);

        //then
        final String 예외_메시지 = 참가_요청에_대한_응답.asString();

        assertAll(
                () -> assertThat(참가_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메시지).contains("이미 참여한 골룸에는 참여할 수 없습니다.")
        );
    }

    @Test
    void 정상적으로_골룸에_투두리스트를_추가한다() throws IOException {
        // given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final Long 로드맵_id = 로드맵_생성(액세스_토큰, 카테고리);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();
        final Long 골룸_id = 정상적인_골룸_생성(액세스_토큰, 로드맵_id, 로드맵_노드);

        final GoalRoomTodoRequest 골룸_추가_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);

        // when
        final ExtractableResponse<Response> 골룸_추가_응답 = 골룸_추가(액세스_토큰, 골룸_id, 골룸_추가_요청);

        // then
        assertThat(골룸_추가_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        final String header = 골룸_추가_응답.response()
                .header(HttpHeaders.LOCATION);
        assertThat(header).contains("/api/goal-rooms/1/todos/" + header.substring(24));
    }

    @Test
    void 골룸에_팔로워가_투두_리스트를_추가할때_예외를_던진다() throws IOException {
        // given
        final String 골룸_리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final MemberJoinRequest 팔로워_회원가입_요청 = new MemberJoinRequest("identifier1", "password12!@#$%", "follower",
                "010-2345-6789", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        final String 골룸_팔로워_액세스_토큰 = 회원을_생성하고_로그인을_한다(팔로워_회원가입_요청,
                new LoginRequest(팔로워_회원가입_요청.identifier(), 팔로워_회원가입_요청.password()));
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final Long 로드맵_id = 로드맵_생성(골룸_리더_액세스_토큰, 카테고리);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();
        final Long 골룸_id = 정상적인_골룸_생성(골룸_리더_액세스_토큰, 로드맵_id, 로드맵_노드);

        final GoalRoomTodoRequest 골룸_추가_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);

        // when
        final ExtractableResponse<Response> 골룸_추가_응답 = 골룸_추가(골룸_팔로워_액세스_토큰, 골룸_id, 골룸_추가_요청);

        // then
        final ErrorResponse 골룸_추가_응답_바디 = jsonToClass(골룸_추가_응답.asString(), new TypeReference<>() {
        });
        assertThat(골룸_추가_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(골룸_추가_응답_바디).isEqualTo(new ErrorResponse("골룸의 리더만 투드리스트를 추가할 수 있습니다."));
    }

    @Test
    void 종료된_골룸에_투두_리스트를_추가할때_예외를_던진다() throws IOException {
        //given
        final String 골룸_리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final Long 로드맵_id = 로드맵_생성(골룸_리더_액세스_토큰, 카테고리);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();
        final Long 골룸_id = 정상적인_골룸_생성(골룸_리더_액세스_토큰, 로드맵_id, 로드맵_노드);
        final GoalRoom 골룸 = goalRoomRepository.findById(골룸_id).get();
        골룸.complete();
        goalRoomRepository.save(골룸);

        final GoalRoomTodoRequest 골룸_추가_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);

        //when
        final ExtractableResponse<Response> 골룸_추가_응답 = 골룸_추가(골룸_리더_액세스_토큰, 골룸_id, 골룸_추가_요청);

        //then
        final ErrorResponse 골룸_추가_응답_바디 = jsonToClass(골룸_추가_응답.asString(), new TypeReference<>() {
        });
        assertThat(골룸_추가_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(골룸_추가_응답_바디).isEqualTo(new ErrorResponse("이미 종료된 골룸입니다."));
    }

    private ExtractableResponse<Response> 로드맵_생성(final RoadmapSaveRequest 로드맵_생성_요청값, final String 액세스_토큰)
            throws IOException {
        final String jsonRequest = objectMapper.writeValueAsString(로드맵_생성_요청값);

        RequestSpecification requestSpecification = given().log().all()
                .header(HttpHeaders.AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("jsonData", "jsonData.json", jsonRequest, MediaType.APPLICATION_JSON_VALUE);

        requestSpecification = makeRequestSpecification(로드맵_생성_요청값, requestSpecification);

        return requestSpecification
                .log().all()
                .post(API_PREFIX + "/roadmaps")
                .then().log().all()
                .extract();
    }

    private RequestSpecification makeRequestSpecification(final RoadmapSaveRequest 로드맵_생성_요청값, RequestSpecification requestSpecification) throws IOException {
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

    private Long 골룸을_생성하고_아이디를_반환한다(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        final String 골룸_생성_응답_Location_헤더 = 골룸_생성(골룸_생성_요청, 액세스_토큰).response().getHeader(LOCATION);
        return Long.parseLong(골룸_생성_응답_Location_헤더.substring(16));
    }

    private RoadmapNode 로드맵_노드() {
        return roadmapNodeRepository.findAll().get(0);
    }

    private String 회원을_생성하고_로그인을_한다(final MemberJoinRequest memberRequest, final LoginRequest loginRequest) {
        회원가입(memberRequest);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(loginRequest);
        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        final String 액세스_토큰 = BEARER + 로그인_응답_바디.accessToken();
        return 액세스_토큰;
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

    private Long 로드맵을_생성하고_id를_알아낸다(final String 액세스_토큰, final RoadmapSaveRequest 로드맵_생성_요청) throws IOException {
        final ExtractableResponse<Response> 로드맵_응답 = 로드맵_생성(로드맵_생성_요청, 액세스_토큰);
        final String Location_헤더 = 로드맵_응답.response().header("Location");
        return Long.parseLong(Location_헤더.substring(14));
    }

    private ExtractableResponse<Response> 골룸_참가_요청(final Long 골룸_아이디, final String 팔로워_액세스_토큰) {
        return given()
                .log().all()
                .header(AUTHORIZATION, 팔로워_액세스_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String 카테고리_이름) {
        final RoadmapCategory 로드맵_카테고리 = new RoadmapCategory(카테고리_이름);
        return roadmapCategoryRepository.save(로드맵_카테고리);
    }

    private Long 로드맵_생성(final String 액세스_토큰, final RoadmapCategory 카테고리) throws IOException {
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));
        return 로드맵을_생성하고_id를_알아낸다(액세스_토큰, 로드맵_생성_요청);
    }

    private Long 정상적인_골룸_생성(final String 액세스_토큰, final Long 로드맵_id, final RoadmapNode 로드맵_노드) {
        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);
        final String Location_헤더 = 골룸_생성_응답.response().header("Location");
        return Long.parseLong(Location_헤더.substring(16));
    }

    private ExtractableResponse<Response> 골룸_추가(final String 액세스_토큰, final Long 골룸_id,
                                                final GoalRoomTodoRequest 골룸_추가_요청) {
        final ExtractableResponse<Response> 골룸_추가_응답 = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(골룸_추가_요청)
                .header(new Header(HttpHeaders.AUTHORIZATION, 액세스_토큰))
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/todos", 골룸_id)
                .then()
                .log().all()
                .extract();
        return 골룸_추가_응답;
    }
}
