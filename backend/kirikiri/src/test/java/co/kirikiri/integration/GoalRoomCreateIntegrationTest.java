package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.goalroom.GoalRoomFilterTypeDto;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class GoalRoomCreateIntegrationTest extends RoadmapReadIntegrationTest {

    private static final LocalDate 오늘 = LocalDate.now();
    private static final LocalDate 십일_후 = 오늘.plusDays(10L);
    private static final LocalDate 이십일_후 = 십일_후.plusDays(10L);
    protected static final String 정상적인_골룸_이름 = "GOAL_ROOM_NAME";
    private static final int 정상적인_골룸_제한_인원 = 20;
    private static final int 인증_횟수_제한_1 = 1;
    private static final String 정상적인_골룸_투두_컨텐츠 = "GOAL_ROOM_TO_DO_CONTENT";
    private static final int 정상적인_골룸_노드_인증_횟수 = (int) ChronoUnit.DAYS.between(오늘, 십일_후);

    protected final GoalRoomRepository goalRoomRepository;
    protected final GoalRoomMemberRepository goalRoomMemberRepository;

    public GoalRoomCreateIntegrationTest(final RoadmapCategoryRepository roadmapCategoryRepository,
                                         final GoalRoomRepository goalRoomRepository,
                                         final GoalRoomMemberRepository goalRoomMemberRepository) {
        super(roadmapCategoryRepository);
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
    }

    @Override
    @BeforeEach
    void init() {
        super.init();
    }

    @Test
    void 정상적으로_골룸을_생성한다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 기본_로그인_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(골룸_생성_응답.response().header("Location")).contains("/api/goal-rooms/");
    }

    @Test
    void 골룸_생성_시_컨텐츠_id가_빈값일_경우() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 로드맵_아이디가_빈값인_골룸_생성_요청 = new GoalRoomCreateRequest(null, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(로드맵_아이디가_빈값인_골룸_생성_요청, 기본_로그인_토큰);

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
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_이름이_빈값인_골룸_생성_요청 = new GoalRoomCreateRequest(1L, null, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_이름이_빈값인_골룸_생성_요청, 기본_로그인_토큰);

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
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_제한_인원이_빈값인_골룸_생성_요청 = new GoalRoomCreateRequest(1L, 정상적인_골룸_이름, null, 골룸_투두_요청,
                골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_제한_인원이_빈값인_골룸_생성_요청, 기본_로그인_토큰);

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
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final String 적절하지_않은_길이의_골룸_이름 = "a".repeat(41);
        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 적절하지_않은_길이의_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 기본_로그인_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("골룸 이름의 길이가 적절하지 않습니다.");
    }

    @Test
    void 골룸_생성_시_노드_별_기간_수와_로드맵_노드의_수가_맞지_않을때() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = Collections.emptyList();
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 기본_로그인_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("모든 노드에 대해 기간이 설정돼야 합니다.");
    }

    @Test
    void 골룸_생성_시_제한_인원이_20명_초과일때() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));

        final int 초과된_제한인원 = 30;
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 초과된_제한인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        //when
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 기본_로그인_토큰);

        //then
        assertThat(골룸_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = 골룸_생성_응답.as(ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("제한 인원 수가 적절하지 않습니다.");
    }

    @Test
    void 골룸에_참가_요청을_보낸다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        // when
        final ExtractableResponse<Response> 골룸_참가_요청_응답 = 골룸_참가_요청(골룸_아이디, 팔로워_액세스_토큰);

        //then
        assertThat(골룸_참가_요청_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 존재하지_않는_골룸_아이디로_참가_요청을_보내면_예외가_발생한다() {
        //given
        final Long 존재하지_않는_골룸_아이디 = 1L;

        //when
        final ExtractableResponse<Response> 골룸_참가_요청_응답 = 골룸_참가_요청(존재하지_않는_골룸_아이디, 기본_로그인_토큰);

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
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 1, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

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
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

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
    void 인증_피드_등록을_요청한다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 기본_로그인_토큰);
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");

        //when
        final ExtractableResponse<Response> 인증_피드_등록_응답 = 인증_피드_등록(골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청, 기본_로그인_토큰);

        //then
        assertAll(
                () -> assertThat(인증_피드_등록_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(인증_피드_등록_응답.response().header("Location"))
                        .contains("originalFileName.jpeg")
        );
    }

    @Test
    void 인증용_사진이_없는_경우_인증_피드_등록이_실패한다() throws IOException {
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 기본_로그인_토큰);
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        final MockMultipartFile 빈_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(빈_이미지_객체, "image description");

        //when
        final ExtractableResponse<Response> 인증_피드_등록_응답 = 인증_피드_등록(골룸_아이디, 빈_이미지_객체,
                인증_피드_등록_요청, 기본_로그인_토큰);
        final ErrorResponse 예외_메세지 = 인증_피드_등록_응답.as(ErrorResponse.class);

        //then
        assertAll(
                () -> assertThat(인증_피드_등록_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메세지.message()).isEqualTo("인증 피드 등록 시 이미지가 반드시 포함되어야 합니다.")
        );
    }

    @Test
    void 인증용_사진의_확장자가_허용되지_않는_경우_인증_피드_등록이_실패한다() throws IOException {
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 기본_로그인_토큰);
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.gif",
                "image/gif", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");

        //when
        final ExtractableResponse<Response> 인증_피드_등록_응답 = 인증_피드_등록(골룸_아이디, 가짜_이미지_객체,
                인증_피드_등록_요청, 기본_로그인_토큰);

        final ErrorResponse 예외_메세지 = 인증_피드_등록_응답.as(ErrorResponse.class);

        //then
        assertAll(
                () -> assertThat(인증_피드_등록_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메세지.message()).isEqualTo("허용되지 않는 확장자입니다.")
        );
    }

    @Test
    void 하루에_두_번_이상_인증_피드_등록을_요청하는_경우_실패한다() throws IOException {
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 기본_로그인_토큰);
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/webp", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");
        인증_피드_등록(골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청, 기본_로그인_토큰);

        //when
        final ExtractableResponse<Response> 인증_피드_등록_응답 = 인증_피드_등록(골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청, 기본_로그인_토큰);
        final ErrorResponse 예외_메세지 = 인증_피드_등록_응답.as(ErrorResponse.class);

        //then
        // TODO : 달성률 업데이트 확인 테스트는 사용자 골룸 기능 구현 때 옮기기
        final GoalRoomMember joinedMember = goalRoomMemberRepository.findById(1L).get();

        assertAll(
                () -> assertThat(인증_피드_등록_응답.statusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메세지.message()).isEqualTo("이미 오늘 인증 피드를 등록하였습니다."),
                () -> assertThat(joinedMember.getAccomplishmentRate())
                        .isEqualTo(100 / (double) 정상적인_골룸_노드_인증_횟수)
        );
    }

    @Test
    void 진행_중인_노드의_허용된_인증_횟수_이상_요청할_경우_실패한다() throws IOException {
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 인증_횟수_제한_1, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_알아낸다(골룸_생성_요청, 기본_로그인_토큰);
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/webp", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");
        인증_피드_등록(골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청, 기본_로그인_토큰);

        //when
        final ExtractableResponse<Response> 인증_피드_등록_응답 = 인증_피드_등록(골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청, 기본_로그인_토큰);

        final ErrorResponse 예외_메세지 = 인증_피드_등록_응답.as(ErrorResponse.class);

        //then
        assertAll(
                () -> assertThat(인증_피드_등록_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메세지.message()).isEqualTo("이번 노드에는 최대 " + 1 + "번만 인증 피드를 등록할 수 있습니다.")
        );
    }

    @Test
    void 이미_참여한_골룸에_참가_요청을_보내면_예외가_발생한다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        //when
        골룸_참가_요청(골룸_아이디, 기본_로그인_토큰);
        final ExtractableResponse<Response> 참가_요청에_대한_응답 = 골룸_참가_요청(골룸_아이디, 기본_로그인_토큰);

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
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);
        final Long 골룸_아이디 = 정상적인_골룸_생성(기본_로그인_토큰, 기본_로드맵_아이디, 로드맵_응답.content().nodes().get(0).id());

        final GoalRoomTodoRequest 골룸_투두리스트_추가_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);

        // when
        final ExtractableResponse<Response> 골룸_투두리스트_추가 = 골룸_투두리스트_추가(기본_로그인_토큰, 골룸_아이디, 골룸_투두리스트_추가_요청);

        // then
        assertThat(골룸_투두리스트_추가.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        final String header = 골룸_투두리스트_추가.response()
                .header(HttpHeaders.LOCATION);
        assertThat(header).contains("/api/goal-rooms/1/todos/" + header.substring(24));
    }

    @Test
    void 골룸에_팔로워가_투두_리스트를_추가할때_예외를_던진다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final MemberJoinRequest 팔로워_회원가입_요청 = new MemberJoinRequest("identifier2", "password12!@#$%", "follower",
                "010-2345-6789", GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        회원가입(팔로워_회원가입_요청);
        final String 팔로워_로그인_토큰 = String.format(BEARER_TOKEN_FORMAT,
                로그인(new LoginRequest(팔로워_회원가입_요청.identifier(), 팔로워_회원가입_요청.password())).accessToken());

        final Long 골룸_아이디 = 정상적인_골룸_생성(기본_로그인_토큰, 로드맵_응답.roadmapId(), 로드맵_응답.content().nodes().get(0).id());

        final GoalRoomTodoRequest 골룸_투두리스트_추가_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);

        // when
        final ExtractableResponse<Response> 골룸_투두리스트_추가 = 골룸_투두리스트_추가(팔로워_로그인_토큰, 골룸_아이디, 골룸_투두리스트_추가_요청);

        // then
        final ErrorResponse 골룸_투두리스트_추가_바디 = jsonToClass(골룸_투두리스트_추가.asString(), new TypeReference<>() {
        });
        assertThat(골룸_투두리스트_추가.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(골룸_투두리스트_추가_바디).isEqualTo(new ErrorResponse("골룸의 리더만 투두리스트를 추가할 수 있습니다."));
    }

    @Test
    void 종료된_골룸에_투두_리스트를_추가할때_예외를_던진다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 골룸_아이디 = 정상적인_골룸_생성(기본_로그인_토큰, 기본_로드맵_아이디, 로드맵_응답.content().nodes().get(0).id());

        final GoalRoom 골룸 = goalRoomRepository.findById(골룸_아이디).get();
        골룸.complete();
        goalRoomRepository.save(골룸);

        final GoalRoomTodoRequest 골룸_투두_리스트_추가_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);

        //when
        final ExtractableResponse<Response> 골룸_추가_응답 = 골룸_투두리스트_추가(기본_로그인_토큰, 골룸_아이디, 골룸_투두_리스트_추가_요청);

        //then
        final ErrorResponse 골룸_추가_응답_바디 = jsonToClass(골룸_추가_응답.asString(), new TypeReference<>() {
        });
        assertThat(골룸_추가_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(골룸_추가_응답_바디).isEqualTo(new ErrorResponse("이미 종료된 골룸입니다."));
    }

    @Test
    void 골룸_투두리스트를_체크한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 골룸_아이디 = 정상적인_골룸_생성(기본_로그인_토큰, 기본_로드맵_아이디, 로드맵_응답.content().nodes().get(0).id());
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);
        final Long 투두_아이디 = 골룸_투두리스트_추가후_아이디를_반환한다(기본_로그인_토큰, 골룸_아이디);

        // when
        final GoalRoomToDoCheckResponse 골룸_투두리스트_체크_응답값 = 골룸_투두리스트를_체크한다(기본_로그인_토큰, 골룸_아이디, 투두_아이디)
                .as(new TypeRef<>() {
                });

        // then
        final GoalRoomToDoCheckResponse 예상하는_골룸_투두리스트_체크_응답값 = new GoalRoomToDoCheckResponse(true);
        assertThat(골룸_투두리스트_체크_응답값)
                .isEqualTo(예상하는_골룸_투두리스트_체크_응답값);
    }

    @Test
    void 골룸_투두리스트_체크를_해제한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 골룸_아이디 = 정상적인_골룸_생성(기본_로그인_토큰, 기본_로드맵_아이디, 로드맵_응답.content().nodes().get(0).id());
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);
        final Long 투두_아이디 = 골룸_투두리스트_추가후_아이디를_반환한다(기본_로그인_토큰, 골룸_아이디);

        골룸_투두리스트를_체크한다(기본_로그인_토큰, 골룸_아이디, 투두_아이디);

        // when
        final GoalRoomToDoCheckResponse 두번째_골룸_투두리스트_체크_응답값 = 골룸_투두리스트를_체크한다(기본_로그인_토큰, 골룸_아이디, 투두_아이디)
                .as(new TypeRef<>() {
                });

        // then
        final GoalRoomToDoCheckResponse 예상하는_골룸_투두리스트_체크_응답값 = new GoalRoomToDoCheckResponse(false);
        assertThat(두번째_골룸_투두리스트_체크_응답값)
                .isEqualTo(예상하는_골룸_투두리스트_체크_응답값);
    }

    @Test
    void 골룸_투두리스트_체크시_골룸이_존재하지_않으면_예외가_발생한다() {
        // given
        // when
        final ErrorResponse 에러_응답 = 골룸_투두리스트를_체크한다(기본_로그인_토큰, 1L, 1L)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(에러_응답)
                .isEqualTo(new ErrorResponse("골룸이 존재하지 않습니다. goalRoomId = 1"));
    }

    @Test
    void 골룸_투두리스트_체크시_사용자가_없으면_예외가_발생한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 골룸_아이디 = 정상적인_골룸_생성(기본_로그인_토큰, 기본_로드맵_아이디, 로드맵_응답.content().nodes().get(0).id());
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);
        final Long 투두_아이디 = 골룸_투두리스트_추가후_아이디를_반환한다(기본_로그인_토큰, 골룸_아이디);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        // when
        final ErrorResponse 에러_응답 = 골룸_투두리스트를_체크한다(팔로워_액세스_토큰, 골룸_아이디, 투두_아이디)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(에러_응답)
                .isEqualTo(new ErrorResponse("골룸에 사용자가 존재하지 않습니다. goalRoomId = " + 골룸_아이디 +
                        " memberIdentifier = " + 팔로워_회원_가입_요청.identifier()));
    }

    @Test
    void 정상적으로_모집중인_골룸을_나간다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        골룸_참가_요청(골룸_아이디, 팔로워_액세스_토큰);

        // when
        final ExtractableResponse<Response> 골룸_나가기_요청에_대한_응답 = 골룸_나가기_요청(골룸_아이디, 기본_로그인_토큰);

        // then
        assertThat(골룸_나가기_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 정상적으로_완료된_골룸을_나간다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        골룸_참가_요청(골룸_아이디, 팔로워_액세스_토큰);
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        // TODO: 골룸 종료 기능 추가 시 수정
        final GoalRoom 골룸 = goalRoomRepository.findById(골룸_아이디).get();
        골룸.complete();
        goalRoomRepository.save(골룸);

        // when
        final ExtractableResponse<Response> 골룸_나가기_요청에_대한_응답 = 골룸_나가기_요청(골룸_아이디, 기본_로그인_토큰);

        // then
        assertThat(골룸_나가기_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // TODO: 사용자 골룸 목록 조회 API 추가 시 내부 값 비교
    }

    @Test
    void 골룸을_나갈때_존재하지_않는_골룸일_경우_예외가_발생한다() throws JsonProcessingException {
        //given
        final Long 존재하지_않는_골룸_아이디 = 1L;

        // when
        final ExtractableResponse<Response> 골룸_나가기_요청에_대한_응답 = 골룸_나가기_요청(존재하지_않는_골룸_아이디, 기본_로그인_토큰);

        // then
        final ErrorResponse 골룸_생성_응답_바디 = jsonToClass(골룸_나가기_요청에_대한_응답.asString(), new TypeReference<>() {
        });
        assertThat(골룸_나가기_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(골룸_생성_응답_바디.message()).isEqualTo("존재하지 않는 골룸입니다. goalRoomId = 1");
    }

    @Test
    void 모집중인_골룸을_나갈때_참여하지_않은_골룸일_경우_예외가_발생한다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청,
                골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final Long 팔로워_아이디 = 회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        // when
        final ExtractableResponse<Response> 골룸_나가기_요청에_대한_응답 = 골룸_나가기_요청(골룸_아이디, 팔로워_액세스_토큰);

        // then
        final ErrorResponse 골룸_생성_응답_바디 = jsonToClass(골룸_나가기_요청에_대한_응답.asString(), new TypeReference<>() {
        });
        assertThat(골룸_나가기_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(골룸_생성_응답_바디.message()).isEqualTo("골룸에 참여한 사용자가 아닙니다. memberId = " + 팔로워_아이디);
    }

    @Test
    void 완료된_골룸을_나갈때_참여하지_않은_골룸일_경우_예외가_발생한다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final Long 팔로워_아이디 = 회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        // TODO: 골룸 종료 기능 추가 시 수정
        final GoalRoom 골룸 = goalRoomRepository.findById(골룸_아이디).get();
        골룸.complete();
        goalRoomRepository.save(골룸);

        // when

        final ExtractableResponse<Response> 골룸_나가기_요청에_대한_응답 = 골룸_나가기_요청(골룸_아이디, 팔로워_액세스_토큰);

        // then
        final ErrorResponse 골룸_생성_응답_바디 = jsonToClass(골룸_나가기_요청에_대한_응답.asString(), new TypeReference<>() {
        });
        assertThat(골룸_나가기_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(골룸_생성_응답_바디.message()).isEqualTo("골룸에 참여한 사용자가 아닙니다. memberId = " + 팔로워_아이디);
    }

    @Test
    void 골룸을_나갈때_골룸이_진행중이면_예외가_발생한다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        // when
        final ExtractableResponse<Response> 골룸_나가기_요청에_대한_응답 = 골룸_나가기_요청(골룸_아이디, 기본_로그인_토큰);

        // then
        final ErrorResponse 골룸_생성_응답_바디 = jsonToClass(골룸_나가기_요청에_대한_응답.asString(), new TypeReference<>() {
        });
        assertThat(골룸_나가기_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(골룸_생성_응답_바디.message()).isEqualTo("진행중인 골룸에서는 나갈 수 없습니다.");
    }

    @Test
    void 모집중인_골룸을_나갈때_리더가_나가면_다음으로_들어온_사용자가_리더가_된다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final MemberJoinRequest 팔로워1_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follow1", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워1_로그인_요청 = new LoginRequest(팔로워1_회원_가입_요청.identifier(), 팔로워1_회원_가입_요청.password());
        회원가입(팔로워1_회원_가입_요청);
        final String 팔로워1_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워1_로그인_요청).accessToken());

        final MemberJoinRequest 팔로워2_회원_가입_요청 = new MemberJoinRequest("identifier3", "paswword2@",
                "follow2", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워2_로그인_요청 = new LoginRequest(팔로워2_회원_가입_요청.identifier(), 팔로워2_회원_가입_요청.password());
        회원가입(팔로워2_회원_가입_요청);
        final String 팔로워2_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워2_로그인_요청).accessToken());

        골룸_참가_요청(골룸_아이디, 팔로워1_액세스_토큰);
        골룸_참가_요청(골룸_아이디, 팔로워2_액세스_토큰);

        // when
        final ExtractableResponse<Response> 골룸_나가기_요청에_대한_응답 = 골룸_나가기_요청(골룸_아이디, 기본_로그인_토큰);

        // then
        assertThat(골룸_나가기_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        final ExtractableResponse<Response> 골룸_목록_조회_요청에_대한_응답 = 골룸_목록_조회_요청(1L, null, 2,
                GoalRoomFilterTypeDto.LATEST.name());
        final RoadmapGoalRoomResponses 골룸_목록 = jsonToClass(골룸_목록_조회_요청에_대한_응답.asString(),
                new TypeReference<>() {
                });
        assertThat(골룸_목록.responses().get(0).currentMemberCount()).isEqualTo(2);
        assertThat(골룸_목록.responses().get(0).goalRoomLeader().name()).isEqualTo("follow1");
    }

    @Test
    void 완료된_골룸을_나갈때_리더가_나가면_다음으로_들어온_사용자가_리더가_된다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final MemberJoinRequest 팔로워1_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follow1", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워1_로그인_요청 = new LoginRequest(팔로워1_회원_가입_요청.identifier(), 팔로워1_회원_가입_요청.password());
        회원가입(팔로워1_회원_가입_요청);
        final String 팔로워1_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워1_로그인_요청).accessToken());

        final MemberJoinRequest 팔로워2_회원_가입_요청 = new MemberJoinRequest("identifier3", "paswword2@",
                "follow2", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워2_로그인_요청 = new LoginRequest(팔로워2_회원_가입_요청.identifier(), 팔로워2_회원_가입_요청.password());
        회원가입(팔로워2_회원_가입_요청);
        final String 팔로워2_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워2_로그인_요청).accessToken());

        골룸_참가_요청(골룸_아이디, 팔로워1_액세스_토큰);
        골룸_참가_요청(골룸_아이디, 팔로워2_액세스_토큰);

        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        // TODO: 골룸 종료 기능 추가 시 수정
        final GoalRoom 골룸 = goalRoomRepository.findById(골룸_아이디).get();
        골룸.complete();
        goalRoomRepository.save(골룸);

        // when

        final ExtractableResponse<Response> 골룸_나가기_요청에_대한_응답 = 골룸_나가기_요청(골룸_아이디, 기본_로그인_토큰);

        // then
        assertThat(골룸_나가기_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // TODO: 사용자 골룸 목록 조회 API 추가 시 내부 값 비교
    }

    @Test
    void 모집중인_골룸을_나갈때_팔로워가_나가면_리더는_변하지_않는다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        골룸_참가_요청(골룸_아이디, 팔로워_액세스_토큰);

        // when
        final ExtractableResponse<Response> 골룸_나가기_요청에_대한_응답 = 골룸_나가기_요청(골룸_아이디, 팔로워_액세스_토큰);

        // then
        assertThat(골룸_나가기_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        final ExtractableResponse<Response> 골룸_목록_조회_요청에_대한_응답 = 골룸_목록_조회_요청(1L, null, 1,
                GoalRoomFilterTypeDto.LATEST.name());
        final RoadmapGoalRoomResponses 골룸_목록 = jsonToClass(골룸_목록_조회_요청에_대한_응답.asString(),
                new TypeReference<>() {
                });
        assertThat(골룸_목록.responses().get(0).currentMemberCount()).isEqualTo(1);
        assertThat(골룸_목록.responses().get(0).goalRoomLeader().id()).isEqualTo(기본_회원_아이디);
    }

    @Test
    void 완료된_골룸을_나갈때_팔로워가_나가면_리더는_변하지_않는다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        골룸_참가_요청(골룸_아이디, 팔로워_액세스_토큰);
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        // TODO: 골룸 종료 기능 추가 시 수정
        final GoalRoom 골룸 = goalRoomRepository.findById(골룸_아이디).get();
        골룸.complete();
        goalRoomRepository.save(골룸);

        // when
        final ExtractableResponse<Response> 골룸_나가기_요청에_대한_응답 = 골룸_나가기_요청(골룸_아이디, 팔로워_액세스_토큰);

        // then
        assertThat(골룸_나가기_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // TODO: 사용자 골룸 목록 조회 API 추가 시 내부 값 비교
    }

    @Test
    void 모집중인_골룸을_나갈때_남은_사용자가_없으면_골룸은_삭제된다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        // when
        final ExtractableResponse<Response> 골룸_나가기_요청에_대한_응답 = 골룸_나가기_요청(골룸_아이디, 기본_로그인_토큰);

        // then
        assertThat(골룸_나가기_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        final ExtractableResponse<Response> 골룸_목록_조회_요청에_대한_응답 = 골룸_목록_조회_요청(1L, null, 1,
                GoalRoomFilterTypeDto.LATEST.name());
        final RoadmapGoalRoomResponses 골룸_목록 = jsonToClass(골룸_목록_조회_요청에_대한_응답.asString(),
                new TypeReference<>() {
                });
        assertThat(골룸_목록.responses()).hasSize(0);
    }

    @Test
    void 완료된_골룸을_나갈때_남은_사용자가_없으면_골룸은_삭제된다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        // TODO: 골룸 종료 기능 추가 시 수정
        final GoalRoom 골룸 = goalRoomRepository.findById(골룸_아이디).get();
        골룸.complete();
        goalRoomRepository.save(골룸);

        // when

        final ExtractableResponse<Response> 골룸_나가기_요청에_대한_응답 = 골룸_나가기_요청(골룸_아이디, 기본_로그인_토큰);

        // then
        assertThat(골룸_나가기_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        final ExtractableResponse<Response> 골룸_목록_조회_요청에_대한_응답 = 골룸_목록_조회_요청(1L, null, 1,
                GoalRoomFilterTypeDto.LATEST.name());
        final RoadmapGoalRoomResponses 골룸_목록 = jsonToClass(골룸_목록_조회_요청에_대한_응답.asString(),
                new TypeReference<>() {
                });
        assertThat(골룸_목록.responses()).hasSize(0);
    }

    @Test
    void 골룸을_정상적으로_시작한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        // when
        final ExtractableResponse<Response> 골룸_시작_요청_응답 = 골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        // then
        assertThat(골룸_시작_요청_응답.statusCode())
                .isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 골룸을_시작하는_사용자가_골룸의_리더가_아니면_예외가_발생한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final MemberJoinRequest 다른_사용자_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        회원가입(다른_사용자_회원_가입_요청);
        final LoginRequest 다른_사용자_로그인_요청 = new LoginRequest(다른_사용자_회원_가입_요청.identifier(), 다른_사용자_회원_가입_요청.password());
        final String 다른_사용자_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(다른_사용자_로그인_요청).accessToken());

        // when
        final ExtractableResponse<Response> 골룸_시작_요청_응답 = 골룸을_시작한다(다른_사용자_액세스_토큰, 골룸_아이디);

        // then
        final ErrorResponse errorResponse = 골룸_시작_요청_응답.as(ErrorResponse.class);
        assertThat(골룸_시작_요청_응답.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.message())
                .isEqualTo("골룸의 리더만 골룸을 시작할 수 있습니다.");
    }

    @Test
    void 골룸_시작시_골룸의_시작날짜가_미래라면_예외가_발생한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 십일_후, 이십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(),
                        (int) ChronoUnit.DAYS.between(십일_후, 이십일_후), 십일_후, 이십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        // when
        final ExtractableResponse<Response> 골룸_시작_요청_응답 = 골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        // then
        final ErrorResponse errorResponse = 골룸_시작_요청_응답.as(ErrorResponse.class);
        assertThat(골룸_시작_요청_응답.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.message())
                .isEqualTo("골룸의 시작 날짜가 되지 않았습니다.");
    }

    private Long 골룸을_생성하고_아이디를_반환한다(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        final String 골룸_생성_응답_Location_헤더 = 골룸_생성(골룸_생성_요청, 액세스_토큰).response().getHeader(LOCATION);
        return Long.parseLong(골룸_생성_응답_Location_헤더.substring(16));
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

    protected Long 기본_골룸_생성(final String 액세스_토큰, final RoadmapResponse 로드맵_응답) {
        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.content().id(), 정상적인_골룸_이름,
                정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final String Location_헤더 = 골룸_생성(골룸_생성_요청, 액세스_토큰).response().header(LOCATION);
        return Long.parseLong(Location_헤더.substring(16));
    }

    protected ExtractableResponse<Response> 골룸_참가_요청(final Long 골룸_아이디, final String 팔로워_액세스_토큰) {
        return given()
                .log().all()
                .header(AUTHORIZATION, 팔로워_액세스_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 골룸_나가기_요청(final Long 골룸_아이디, final String 골룸_참여자_액세스_토큰) {
        return given()
                .log().all()
                .header(AUTHORIZATION, 골룸_참여자_액세스_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/leave", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 골룸_목록_조회_요청(final Long roadmapId, final LocalDateTime lastValue,
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

    private Long 정상적인_골룸_생성(final String 액세스_토큰, final Long 로드맵_아이디, final Long 로드맵_노드_아이디) {
        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드_아이디, 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);
        final ExtractableResponse<Response> 골룸_생성_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);
        final String Location_헤더 = 골룸_생성_응답.response().header("Location");
        return Long.parseLong(Location_헤더.substring(16));
    }

    protected ExtractableResponse<Response> 골룸_투두리스트_추가(final String 액세스_토큰, final Long 골룸_아이디,
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

    private Long 골룸_투두리스트_추가후_아이디를_반환한다(final String 로그인_토큰_정보, final Long 골룸_아이디) {
        final GoalRoomTodoRequest 골룸_투두리스트_추가_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final String 응답_헤더값 = 골룸_투두리스트_추가(로그인_토큰_정보, 골룸_아이디, 골룸_투두리스트_추가_요청)
                .response()
                .getHeader(LOCATION)
                .replace(API_PREFIX + "/goal-rooms/" + 골룸_아이디 + "/todos/", "");
        return Long.valueOf(응답_헤더값);
    }

    protected ExtractableResponse<Response> 골룸_투두리스트를_체크한다(final String 로그인_토큰_정보, final Long 골룸_아이디,
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

    protected Long 골룸을_생성하고_아이디를_알아낸다(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        final ExtractableResponse<Response> 골룸_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);
        final String Location_헤더 = 골룸_응답.response().header("Location");
        return Long.parseLong(Location_헤더.substring(16));
    }

    protected ExtractableResponse<Response> 인증_피드_등록(final Long 골룸_아이디, final MockMultipartFile 가짜_이미지_객체,
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

    protected ExtractableResponse<Response> 골룸을_시작한다(final String 로그인_토큰, final Long 골룸_아이디) {
        final ExtractableResponse<Response> 골룸_시작_요청_응답 = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(new Header(HttpHeaders.AUTHORIZATION, 로그인_토큰))
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/start", 골룸_아이디)
                .then()
                .log().all()
                .extract();
        return 골룸_시작_요청_응답;
    }
}
