package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapNodeRepository;
import co.kirikiri.service.GoalRoomCreateService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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

    private final GoalRoomCreateService goalRoomCreateService;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    public GoalRoomCreateIntegrationTest(final GoalRoomCreateService goalRoomCreateService,
                                         final RoadmapCategoryRepository roadmapCategoryRepository,
                                         final RoadmapNodeRepository roadmapNodeRepository,
                                         final GoalRoomMemberRepository goalRoomMemberRepository) {
        this.goalRoomCreateService = goalRoomCreateService;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
    }

    @Test
    void 정상적으로_골룸을_생성한다() {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final Long 로드맵_id = 로드맵을_생성하고_id를_알아낸다(액세스_토큰, 로드맵_생성_요청);
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
        assertThat(골룸_생성_응답.response().header("Location")).isNotNull();
    }

    @Test
    void 골룸_생성_시_컨테츠id가_빈값일_경우() throws JsonProcessingException {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
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
    void 골룸_생성_시_골룸_이름이_빈값일_경우() throws JsonProcessingException {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
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
    void 골룸_생성_시_골룸_제한_인원이_빈값일_경우() throws JsonProcessingException {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
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
    void 골룸_생성_시_골룸_이름이_40자_초과인_경우() {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final Long 로드맵_id = 로드맵을_생성하고_id를_알아낸다(액세스_토큰, 로드맵_생성_요청);

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
    void 골룸_생성_시_노드_별_기간_수와_로드맵_노드의_수가_맞지_않을때() {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용"),
                        new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용")));
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
    void 골룸_생성_시_제한_인원이_20명_초과일때() {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final Long 로드맵_id = 로드맵을_생성하고_id를_알아낸다(액세스_토큰, 로드맵_생성_요청);
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

    @Test
    void 인증_피드_등록을_요청한다() throws IOException {
        //given
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final Long 로드맵_id = 로드맵을_생성하고_id를_알아낸다(액세스_토큰, 로드맵_생성_요청);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_id = 골룸을_생성하고_id를_알아낸다(골룸_생성_요청, 액세스_토큰);
        goalRoomCreateService.startGoalRooms();

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");

        //when
        final ExtractableResponse<Response> 인증_피드_등록_응답 = given().log().all()
                .multiPart(가짜_이미지_객체.getName(), 가짜_이미지_객체.getOriginalFilename(),
                        가짜_이미지_객체.getBytes(), 가짜_이미지_객체.getContentType())
                .formParam("description", 인증_피드_등록_요청.description())
                .header(AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .when()
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_id)
                .then()
                .log().all()
                .extract();

        final String imageUrl = 인증_피드_등록_응답.response().header("Location");
        테스트용으로_생성된_파일을_제거한다(imageUrl);

        //then
        assertAll(
                () -> assertThat(인증_피드_등록_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(인증_피드_등록_응답.response().header("Location")).isNotNull()
        );
    }

    @Test
    void 인증용_사진이_없는_경우_인증_피드_등록이_실패한다() throws IOException {
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final Long 로드맵_id = 로드맵을_생성하고_id를_알아낸다(액세스_토큰, 로드맵_생성_요청);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_id = 골룸을_생성하고_id를_알아낸다(골룸_생성_요청, 액세스_토큰);
        goalRoomCreateService.startGoalRooms();

        final MockMultipartFile 빈_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(빈_이미지_객체, "image description");

        //when
        final ExtractableResponse<Response> 인증_피드_등록_응답 = given().log().all()
                .multiPart(빈_이미지_객체.getName(), 빈_이미지_객체.getOriginalFilename(),
                        빈_이미지_객체.getBytes(), 빈_이미지_객체.getContentType())
                .formParam("description", 인증_피드_등록_요청.description())
                .header(AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .when()
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_id)
                .then()
                .log().all()
                .extract();

        final ErrorResponse 예외_메세지 = 인증_피드_등록_응답.as(ErrorResponse.class);

        //then
        assertAll(
                () -> assertThat(인증_피드_등록_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메세지.message()).isEqualTo("인증 피드 등록 시 이미지가 반드시 포함되어야 합니다.")
        );
    }

    @Test
    void 인증용_사진의_확장자가_허용되지_않는_경우_인증_피드_등록이_실패한다() throws IOException {
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final Long 로드맵_id = 로드맵을_생성하고_id를_알아낸다(액세스_토큰, 로드맵_생성_요청);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_id = 골룸을_생성하고_id를_알아낸다(골룸_생성_요청, 액세스_토큰);
        goalRoomCreateService.startGoalRooms();

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/gif", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");

        //when
        final ExtractableResponse<Response> 인증_피드_등록_응답 = given().log().all()
                .multiPart(가짜_이미지_객체.getName(), 가짜_이미지_객체.getOriginalFilename(),
                        가짜_이미지_객체.getBytes(), 가짜_이미지_객체.getContentType())
                .formParam("description", 인증_피드_등록_요청.description())
                .header(AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .when()
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_id)
                .then()
                .log().all()
                .extract();

        final ErrorResponse 예외_메세지 = 인증_피드_등록_응답.as(ErrorResponse.class);

        //then
        assertAll(
                () -> assertThat(인증_피드_등록_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메세지.message()).isEqualTo(가짜_이미지_객체.getContentType() + "는 요청할 수 없는 파일 확장자 형식입니다.")
        );
    }

    @Test
    void 하루에_두_번_이상_인증_피드_등록을_요청하는_경우_실패한다() throws IOException {
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final Long 로드맵_id = 로드맵을_생성하고_id를_알아낸다(액세스_토큰, 로드맵_생성_요청);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_id = 골룸을_생성하고_id를_알아낸다(골룸_생성_요청, 액세스_토큰);
        goalRoomCreateService.startGoalRooms();

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/webp", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");
        인증_피드_등록을_요청한다(인증_피드_등록_요청, 액세스_토큰, 골룸_id);

        //when
        final ExtractableResponse<Response> 인증_피드_등록_응답 = given().log().all()
                .multiPart(가짜_이미지_객체.getName(), 가짜_이미지_객체.getOriginalFilename(),
                        가짜_이미지_객체.getBytes(), 가짜_이미지_객체.getContentType())
                .formParam("description", 인증_피드_등록_요청.description())
                .header(AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .when()
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_id)
                .then()
                .log().all()
                .extract();

        final ErrorResponse 예외_메세지 = 인증_피드_등록_응답.as(ErrorResponse.class);

        //then
        // TODO : 달성률 업데이트 확인 테스트는 사용자 골룸 기능 구현 때 옮기기
        final GoalRoomMember joinedMember = goalRoomMemberRepository.findById(1L).get();

        assertAll(
                () -> assertThat(인증_피드_등록_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메세지.message()).isEqualTo("이미 오늘 인증 피드를 등록하였습니다."),
                () -> assertThat(joinedMember.getAccomplishmentRate()).isEqualTo(100 * 1 / (double) 10)
        );
    }

    @Test
    void 진행_중인_노드의_허용된_인증_횟수_이상_요청할_경우_실패한다() throws IOException {
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다();
        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다(카테고리_이름);
        final RoadmapSaveRequest 로드맵_생성_요청 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));
        final Long 로드맵_id = 로드맵을_생성하고_id를_알아낸다(액세스_토큰, 로드맵_생성_요청);
        final RoadmapNode 로드맵_노드 = 로드맵_노드();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드.getId(), 1, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_id, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_id = 골룸을_생성하고_id를_알아낸다(골룸_생성_요청, 액세스_토큰);
        goalRoomCreateService.startGoalRooms();

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/webp", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");
        인증_피드_등록을_요청한다(인증_피드_등록_요청, 액세스_토큰, 골룸_id);

        //when
        final ExtractableResponse<Response> 인증_피드_등록_응답 = given().log().all()
                .multiPart(가짜_이미지_객체.getName(), 가짜_이미지_객체.getOriginalFilename(),
                        가짜_이미지_객체.getBytes(), 가짜_이미지_객체.getContentType())
                .formParam("description", 인증_피드_등록_요청.description())
                .header(AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .when()
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_id)
                .then()
                .log().all()
                .extract();

        final ErrorResponse 예외_메세지 = 인증_피드_등록_응답.as(ErrorResponse.class);

        //then
        assertAll(
                () -> assertThat(인증_피드_등록_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메세지.message()).isEqualTo("이번 노드에는 최대 " + 1 + "번만 인증 피드를 등록할 수 있습니다.")
        );
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

    private RoadmapNode 로드맵_노드() {
        return roadmapNodeRepository.findAll().get(0);
    }

    private String 회원을_생성하고_로그인을_한다() {
        회원가입(회원가입_요청);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);
        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        final String 액세스_토큰 = BEARER + 로그인_응답_바디.accessToken();
        return 액세스_토큰;
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

    private Long 로드맵을_생성하고_id를_알아낸다(final String 액세스_토큰, final RoadmapSaveRequest 로드맵_생성_요청) {
        final ExtractableResponse<Response> 로드맵_응답 = 로드맵_생성(로드맵_생성_요청, 액세스_토큰);
        final String Location_헤더 = 로드맵_응답.response().header("Location");
        final Long 로드맵_id = Long.parseLong(Location_헤더.substring(14));
        return 로드맵_id;
    }

    private Long 골룸을_생성하고_id를_알아낸다(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        final ExtractableResponse<Response> 골룸_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);
        final String Location_헤더 = 골룸_응답.response().header("Location");
        final Long 골룸_id = Long.parseLong(Location_헤더.substring(16));
        return 골룸_id;
    }

    private void 테스트용으로_생성된_파일을_제거한다(final String filePath) {
        final File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid file path: " + filePath);
        }

        if (!file.delete()) {
            throw new RuntimeException("Failed to delete the file: " + filePath);
        }
    }

    private ExtractableResponse<Response> 인증_피드_등록을_요청한다(final CheckFeedRequest 인증_피드_등록_요청,
                                                         final String 액세스_토큰, final Long 골룸_id) throws IOException {
        final MultipartFile 가짜_이미지_객체 = 인증_피드_등록_요청.image();

        final ExtractableResponse<Response> 인증_피드_등록_응답 = given().log().all()
                .multiPart(가짜_이미지_객체.getName(), 가짜_이미지_객체.getOriginalFilename(),
                        가짜_이미지_객체.getBytes(), 가짜_이미지_객체.getContentType())
                .formParam("description", 인증_피드_등록_요청.description())
                .header(AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .when()
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_id)
                .then()
                .log().all()
                .extract();

        final String imageUrl = 인증_피드_등록_응답.response().header("Location");
        테스트용으로_생성된_파일을_제거한다(imageUrl);

        return 인증_피드_등록_응답;
    }
}
