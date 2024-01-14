package co.kirikiri.integration;

import static co.kirikiri.integration.fixture.AuthenticationAPIFixture.로그인;
import static co.kirikiri.integration.fixture.CommonFixture.BEARER_TOKEN_FORMAT;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_투두리스트_조회;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_투두리스트_추가;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_투두리스트_추가후_아이디를_반환한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_투두리스트를_체크한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸을_시작한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.기본_골룸_생성;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.삼십일_후;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.십일_후;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.오늘;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.이십일_후;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_생성;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_투두_컨텐츠;
import static co.kirikiri.integration.fixture.MemberAPIFixture.DEFAULT_EMAIL;
import static co.kirikiri.integration.fixture.MemberAPIFixture.회원가입;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.로드맵_생성;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.로드맵을_아이디로_조회하고_응답객체를_반환한다;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.common.service.dto.ErrorResponse;
import co.kirikiri.integration.helper.InitIntegrationTest;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.todo.service.dto.request.GoalRoomTodoRequest;
import co.kirikiri.todo.service.dto.response.GoalRoomToDoCheckResponse;
import co.kirikiri.todo.service.dto.response.GoalRoomTodoResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

class GoalRoomToDoIntegrationTest extends InitIntegrationTest {

    @Test
    void 정상적으로_골룸에_투두리스트를_추가한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);
        final Long 골룸_아이디 = 정상적인_골룸_생성(기본_로그인_토큰, 기본_로드맵_아이디, 로드맵_응답.content().nodes().get(0).id());
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);
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
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final MemberJoinRequest 팔로워_회원가입_요청 = new MemberJoinRequest("identifier2", "password12!@#$%", "follower",
                GenderType.MALE, DEFAULT_EMAIL);
        회원가입(팔로워_회원가입_요청);
        final String 팔로워_로그인_토큰 = String.format(BEARER_TOKEN_FORMAT,
                로그인(new LoginRequest(팔로워_회원가입_요청.identifier(), 팔로워_회원가입_요청.password())).accessToken());

        final Long 골룸_아이디 = 정상적인_골룸_생성(기본_로그인_토큰, 로드맵_응답.roadmapId(), 로드맵_응답.content().nodes().get(0).id());
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);
        final GoalRoomTodoRequest 골룸_투두리스트_추가_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);

        // when
        final ExtractableResponse<Response> 골룸_투두리스트_추가 = 골룸_투두리스트_추가(팔로워_로그인_토큰, 골룸_아이디, 골룸_투두리스트_추가_요청);

        // then
        final ErrorResponse 골룸_투두리스트_추가_바디 = 골룸_투두리스트_추가.as(new TypeRef<>() {
        });

        assertThat(골룸_투두리스트_추가.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(골룸_투두리스트_추가_바디).isEqualTo(new ErrorResponse("골룸의 리더만 투두리스트를 추가할 수 있습니다."));
    }

    @Test
    void 종료된_골룸에_투두_리스트를_추가할때_예외를_던진다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 골룸_아이디 = 정상적인_골룸_생성(기본_로그인_토큰, 기본_로드맵_아이디, 로드맵_응답.content().nodes().get(0).id());
        testTransactionService.골룸을_완료시킨다(골룸_아이디);

        final GoalRoomTodoRequest 골룸_투두_리스트_추가_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);

        //when
        final ExtractableResponse<Response> 골룸_추가_응답 = 골룸_투두리스트_추가(기본_로그인_토큰, 골룸_아이디, 골룸_투두_리스트_추가_요청);

        //then
        final ErrorResponse 골룸_추가_응답_바디 = 골룸_추가_응답.as(new TypeRef<>() {
        });

        assertThat(골룸_추가_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(골룸_추가_응답_바디).isEqualTo(new ErrorResponse("이미 종료된 골룸입니다."));
    }

    @Test
    void 골룸_투두_리스트를_체크한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
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
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
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
                .isEqualTo(new ErrorResponse("존재하지 않는 골룸입니다. goalRoomId = 1"));
    }

    @Test
    void 골룸_투두리스트_체크시_사용자가_없으면_예외가_발생한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 골룸_아이디 = 정상적인_골룸_생성(기본_로그인_토큰, 기본_로드맵_아이디, 로드맵_응답.content().nodes().get(0).id());
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);
        final Long 투두_아이디 = 골룸_투두리스트_추가후_아이디를_반환한다(기본_로그인_토큰, 골룸_아이디);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
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
    void 골룸_투두리스트를_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final GoalRoomTodoRequest 골룸_투두_생성_요청 = new GoalRoomTodoRequest("content", 이십일_후, 삼십일_후);

        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);
        골룸_투두리스트_추가(기본_로그인_토큰, 기본_골룸_아이디, 골룸_투두_생성_요청);

        // when
        final List<GoalRoomTodoResponse> 골룸_투두리스트_응답값 = 골룸_투두리스트_조회(기본_골룸_아이디, 기본_로그인_토큰)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(골룸_투두리스트_응답값.get(0).startDate())
                .isEqualTo(이십일_후);
    }

    @Test
    void 골룸_투두리스트_조회시_존재하지_않은_골룸일_경우() {
        // given
        // when
        final ErrorResponse 예외_응답 = 골룸_투두리스트_조회(1L, 기본_로그인_토큰)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(예외_응답)
                .isEqualTo(new ErrorResponse("존재하지 않는 골룸입니다. goalRoomId = 1"));
    }

    @Test
    void 골룸_투두리스트_조회시_참여하지_않은_사용자일_경우() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final GoalRoomTodoRequest 골룸_투두_생성_요청 = new GoalRoomTodoRequest("content", 이십일_후, 삼십일_후);
        골룸_투두리스트_추가(기본_로그인_토큰, 기본_골룸_아이디, 골룸_투두_생성_요청);

        final MemberJoinRequest 다른_사용자_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 다른_사용자_로그인_요청 = new LoginRequest(다른_사용자_회원_가입_요청.identifier(), 다른_사용자_회원_가입_요청.password());
        회원가입(다른_사용자_회원_가입_요청);
        final String 다른_사용자_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(다른_사용자_로그인_요청).accessToken());

        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);

        // when
        final ErrorResponse 예외_응답 = 골룸_투두리스트_조회(기본_골룸_아이디, 다른_사용자_액세스_토큰)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(예외_응답)
                .isEqualTo(new ErrorResponse("골룸에 참여하지 않은 사용자입니다. goalRoomId = " + 기본_골룸_아이디 +
                        " memberIdentifier = identifier2"));
    }
}
