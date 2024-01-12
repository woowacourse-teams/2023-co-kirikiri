package co.kirikiri.integration;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.integration.helper.InitIntegrationTest;
import co.kirikiri.roadmap.service.dto.request.RoadmapReviewSaveRequest;
import co.kirikiri.roadmap.service.dto.response.RoadmapResponse;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberInformationResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;

import static co.kirikiri.integration.fixture.AuthenticationAPIFixture.로그인;
import static co.kirikiri.integration.fixture.CommonFixture.BEARER_TOKEN_FORMAT;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.*;
import static co.kirikiri.integration.fixture.MemberAPIFixture.*;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class RoadmapReviewCreateIntegrationTest extends InitIntegrationTest {

    @Test
    void 로드맵_리뷰를_생성한다() throws IOException {
        // given
        final Long 로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());
        final MemberInformationResponse 팔로워_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(팔로워_액세스_토큰).as(new TypeRef<>() {
        });

        final MemberInformationResponse 리더_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(기본_로그인_토큰).as(new TypeRef<>() {
        });
        final GoalRoom 골룸 = testTransactionService.완료한_골룸을_생성한다(로드맵_응답);
        testTransactionService.골룸에_대한_참여자_리스트를_생성한다(리더_정보, 골룸, 팔로워_정보);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // then
        assertThat(리뷰_생성_요청_결과.statusCode())
                .isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 로드맵_리뷰_생성시_별점이_null이면_예외가_발생한다() throws IOException {
        // given
        final Long 로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());
        final MemberInformationResponse 팔로워_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(팔로워_액세스_토큰).as(new TypeRef<>() {
        });

        final MemberInformationResponse 사용자_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(기본_로그인_토큰).as(new TypeRef<>() {
        });
        final GoalRoom 골룸 = testTransactionService.완료한_골룸을_생성한다(로드맵_응답);
        testTransactionService.골룸에_대한_참여자_리스트를_생성한다(사용자_정보, 골룸, 팔로워_정보);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest(" ", null);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 1L, 로드맵_리뷰_생성_요청);

        // then
        final List<ErrorResponse> 예외_응답 = 리뷰_생성_요청_결과.as(new TypeRef<>() {
        });
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.get(0).message()).isEqualTo("별점을 입력해 주세요.");
    }

    @Test
    void 로드맵_리뷰_생성시_별점이_잘못된_값이면_예외가_발생한다() throws IOException {
        // given
        final Long 로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());
        final MemberInformationResponse 팔로워_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(팔로워_액세스_토큰).as(new TypeRef<>() {
        });

        final MemberInformationResponse 사용자_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(기본_로그인_토큰).as(new TypeRef<>() {
        });
        final GoalRoom 골룸 = testTransactionService.완료한_골룸을_생성한다(로드맵_응답);
        testTransactionService.골룸에_대한_참여자_리스트를_생성한다(사용자_정보, 골룸, 팔로워_정보);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 2.4);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("별점은 0부터 5까지 0.5 단위로 설정할 수 있습니다.");
    }

    @Test
    void 로드맵_리뷰_생성시_내용이_1000자가_넘으면_예외가_발생한다() throws IOException {
        // given
        final Long 로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());
        final MemberInformationResponse 팔로워_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(팔로워_액세스_토큰).as(new TypeRef<>() {
        });

        final MemberInformationResponse 사용자_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(기본_로그인_토큰).as(new TypeRef<>() {
        });
        final GoalRoom 골룸 = testTransactionService.완료한_골룸을_생성한다(로드맵_응답);
        testTransactionService.골룸에_대한_참여자_리스트를_생성한다(사용자_정보, 골룸, 팔로워_정보);

        final String 엄청_긴_리뷰_내용 = "a".repeat(1001);
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest(엄청_긴_리뷰_내용, 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("리뷰는 최대 1000글자까지 입력할 수 있습니다.");
    }

    @Test
    void 로드맵_리뷰_생성시_존재하지_않은_로드맵이면_예외가_발생한다() {
        // given
        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 1L, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(예외_응답.message()).isEqualTo("존재하지 않는 로드맵입니다. roadmapId = 1");
    }

    @Test
    void 로드맵_리뷰_생성시_완료한_골룸이_없다면_예외가_발생한다() throws IOException {
        // given
        final Long 로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_노드_별_기간_요청);
        골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("로드맵에 대해서 완료된 골룸이 존재하지 않습니다. roadmapId = " + 로드맵_아이디 +
                " memberIdentifier = identifier2");
    }

    @Test
    void 로드맵_리뷰_생성시_로드맵_생성자가_리뷰를_달려고_하면_예외가_발생한다() throws IOException {
        // given
        final Long 로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());
        final MemberInformationResponse 팔로워_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(팔로워_액세스_토큰).as(new TypeRef<>() {
        });

        final MemberInformationResponse 사용자_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(기본_로그인_토큰).as(new TypeRef<>() {
        });
        final GoalRoom 골룸 = testTransactionService.완료한_골룸을_생성한다(로드맵_응답);
        testTransactionService.골룸에_대한_참여자_리스트를_생성한다(사용자_정보, 골룸, 팔로워_정보);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(기본_로그인_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("로드맵 생성자는 리뷰를 달 수 없습니다. roadmapId = " + 로드맵_아이디 +
                " memberId = " + 기본_회원_아이디);
    }

    @Test
    void 로드맵_리뷰_생성시_이미_리뷰를_단적이_있으면_예외가_발생한다() throws IOException {
        // given
        final Long 로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final Long 팔로워_아이디 = 회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());
        final MemberInformationResponse 팔로워_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(팔로워_액세스_토큰).as(new TypeRef<>() {
        });

        final MemberInformationResponse 사용자_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(기본_로그인_토큰).as(new TypeRef<>() {
        });
        final GoalRoom 골룸 = testTransactionService.완료한_골룸을_생성한다(로드맵_응답);
        testTransactionService.골룸에_대한_참여자_리스트를_생성한다(사용자_정보, 골룸, 팔로워_정보);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        리뷰를_생성한다(팔로워_액세스_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // when
        final ExtractableResponse<Response> 두번째_리뷰_생성_요청결과 = 리뷰를_생성한다(팔로워_액세스_토큰, 로드맵_아이디, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 두번째_리뷰_생성_요청결과.as(ErrorResponse.class);
        assertThat(두번째_리뷰_생성_요청결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("이미 작성한 리뷰가 존재합니다. roadmapId = " + 로드맵_아이디 +
                " memberId = " + 팔로워_아이디);
    }
}
