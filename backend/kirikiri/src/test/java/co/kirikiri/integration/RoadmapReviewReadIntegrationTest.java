package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.integration.helper.TestTransactionService;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.service.GoalRoomCreateService;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapReviewResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

class RoadmapReviewReadIntegrationTest extends RoadmapReviewCreateIntegrationTest {

    public RoadmapReviewReadIntegrationTest(final RoadmapCategoryRepository roadmapCategoryRepository,
                                            final GoalRoomRepository goalRoomRepository,
                                            final GoalRoomMemberRepository goalRoomMemberRepository,
                                            final GoalRoomCreateService goalRoomCreateService,
                                            final MemberRepository memberRepository,
                                            final TestTransactionService testTransactionService) {
        super(roadmapCategoryRepository, goalRoomRepository, goalRoomMemberRepository, goalRoomCreateService, memberRepository, testTransactionService);
    }

    @Override
    @BeforeEach
    void init() {
        super.init();
    }

    @Test
    void 로드맵에_대한_리뷰를_최신순으로_조회한다() throws IOException {
        // given
        final Member 크리에이터 = testTransactionService.findMemberById(기본_회원_아이디);

        final Long 로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);

        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);
        final RoadmapContent 로드맵_컨텐츠 = testTransactionService.findRoadmapById(로드맵_응답.content().id());

        final MemberJoinRequest 리더_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "leader", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 리더_로그인_요청 = new LoginRequest(리더_회원_가입_요청.identifier(), 리더_회원_가입_요청.password());
        final Long 리더_아이디 = 회원가입(리더_회원_가입_요청);
        final String 리더_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(리더_로그인_요청).accessToken());

        final Member 리더 = testTransactionService.findMemberById(리더_아이디);

        final MemberJoinRequest 팔로워1_회원_가입_요청 = new MemberJoinRequest("identifier3", "paswword2@",
                "follow1", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워1_로그인_요청 = new LoginRequest(팔로워1_회원_가입_요청.identifier(), 팔로워1_회원_가입_요청.password());
        final Long 팔로워1_아이디 = 회원가입(팔로워1_회원_가입_요청);
        final String 팔로워1_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워1_로그인_요청).accessToken());

        final Member 팔로워1 = testTransactionService.findMemberById(팔로워1_아이디);

        final MemberJoinRequest 팔로워2_회원_가입_요청 = new MemberJoinRequest("identifier4", "paswword2@",
                "follow2", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워2_로그인_요청 = new LoginRequest(팔로워2_회원_가입_요청.identifier(), 팔로워2_회원_가입_요청.password());
        final Long 팔로워2_아이디 = 회원가입(팔로워2_회원_가입_요청);
        final String 팔로워2_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워2_로그인_요청).accessToken());

        final Member 팔로워2 = testTransactionService.findMemberById(팔로워2_아이디);

        // TODO 임의로 완료된 골룸을 생성한다 (골룸 완료 API 추가 시 변경)
        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_컨텐츠, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 골룸, 팔로워1, 팔로워2);

        final RoadmapReviewSaveRequest 리더_로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리더 리뷰 내용", 5.0);
        final RoadmapReviewSaveRequest 팔로워1_로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("팔로워1 리뷰 내용", 1.5);
        final RoadmapReviewSaveRequest 팔로워2_로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("팔로워2 리뷰 내용", 3.0);
        리뷰를_생성한다(리더_액세스_토큰, 로드맵_아이디, 리더_로드맵_리뷰_생성_요청);
        리뷰를_생성한다(팔로워1_액세스_토큰, 로드맵_아이디, 팔로워1_로드맵_리뷰_생성_요청);
        리뷰를_생성한다(팔로워2_액세스_토큰, 로드맵_아이디, 팔로워2_로드맵_리뷰_생성_요청);

        // when
        final CustomScrollRequest 첫번째_스크롤_요청 = new CustomScrollRequest(null, 2);
        final ExtractableResponse<Response> 첫번째_로드맵_리뷰_조회_응답 = 로드맵_리뷰를_조회한다(로드맵_아이디, 첫번째_스크롤_요청);
        final List<RoadmapReviewResponse> 첫번째_로드맵_리뷰_조회_응답값 = jsonToClass(첫번째_로드맵_리뷰_조회_응답.asString(),
                new TypeReference<>() {
                });

        final CustomScrollRequest 두번째_스크롤_요청 = new CustomScrollRequest(첫번째_로드맵_리뷰_조회_응답값.get(1).id(), 2);
        final ExtractableResponse<Response> 두번째_로드맵_리뷰_조회_응답 = 로드맵_리뷰를_조회한다(로드맵_아이디, 두번째_스크롤_요청);
        final List<RoadmapReviewResponse> 두번째_로드맵_리뷰_조회_응답값 = jsonToClass(두번째_로드맵_리뷰_조회_응답.asString(),
                new TypeReference<>() {
                });

        // then
        final List<RoadmapReviewResponse> 첫번째_로드맵_리뷰_조회_요청_예상값 = List.of(
                new RoadmapReviewResponse(3L, new MemberResponse(4L, "follow2", 팔로워2.getImage().getServerFilePath()),
                        LocalDateTime.now(), "팔로워2 리뷰 내용", 3.0),
                new RoadmapReviewResponse(2L, new MemberResponse(3L, "follow1", 팔로워1.getImage().getServerFilePath()),
                        LocalDateTime.now(), "팔로워1 리뷰 내용", 1.5));

        final List<RoadmapReviewResponse> 두번째_로드맵_리뷰_조회_요청_예상값 = List.of(
                new RoadmapReviewResponse(1L, new MemberResponse(2L, "leader", 리더.getImage().getServerFilePath()),
                        LocalDateTime.now(), "리더 리뷰 내용", 5.0));

        assertAll(
                () -> assertThat(첫번째_로드맵_리뷰_조회_응답값)
                        .usingRecursiveComparison()
                        .ignoringFields("createdAt")
                        .isEqualTo(첫번째_로드맵_리뷰_조회_요청_예상값),
                () -> assertThat(두번째_로드맵_리뷰_조회_응답값)
                        .usingRecursiveComparison()
                        .ignoringFields("createdAt")
                        .isEqualTo(두번째_로드맵_리뷰_조회_요청_예상값)
        );
    }

    @Test
    void 로드맵_리뷰_조회_요청_시_작성된_리뷰가_없다면_빈_값을_반환한다() throws IOException {
        // given
        final Member 크리에이터 = testTransactionService.findMemberById(기본_회원_아이디);

        final Long 로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);

        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(로드맵_아이디);
        final RoadmapContent 로드맵_컨텐츠 = testTransactionService.findRoadmapById(로드맵_응답.content().id());

        final MemberJoinRequest 리더_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "leader", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 리더_로그인_요청 = new LoginRequest(리더_회원_가입_요청.identifier(), 리더_회원_가입_요청.password());
        final Long 리더_아이디 = 회원가입(리더_회원_가입_요청);
        final String 리더_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(리더_로그인_요청).accessToken());

        final Member 리더 = memberRepository.findById(리더_아이디).get();

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier3", "paswword2@",
                "follow1", "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        final Long 팔로워_아이디 = 회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        final Member 팔로워 = memberRepository.findById(팔로워_아이디).get();

        // TODO 임의로 완료된 골룸을 생성한다 (골룸 완료 API 추가 시 변경)
        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_컨텐츠, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 골룸, 팔로워);

        final CustomScrollRequest 스크롤_요청 = new CustomScrollRequest(null, 10);

        // when
        final ExtractableResponse<Response> 로드맵_리뷰_조회_응답 = 로드맵_리뷰를_조회한다(로드맵_아이디, 스크롤_요청);

        // then
        final List<RoadmapReviewResponse> 로드맵_리뷰_조회_응답값 = jsonToClass(로드맵_리뷰_조회_응답.asString(),
                new TypeReference<>() {
                });

        assertThat(로드맵_리뷰_조회_응답값).isEmpty();
    }

    @Test
    void 로드맵_리뷰_조회_요청_시_유효하지_않은_로드맵_아이디로_요청_시_예외를_반환한다() throws JsonProcessingException {
        //when
        final CustomScrollRequest 스크롤_요청 = new CustomScrollRequest(null, 10);

        // when
        final ExtractableResponse<Response> 로드맵_리뷰_조회_응답 = 로드맵_리뷰를_조회한다(1L, 스크롤_요청);

        // then
        final ErrorResponse 로드맵_리뷰_조회_응답값 = jsonToClass(로드맵_리뷰_조회_응답.asString(),
                new TypeReference<>() {
                });

        assertAll(
                () -> assertThat(로드맵_리뷰_조회_응답.statusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(로드맵_리뷰_조회_응답값.message())
                        .isEqualTo("존재하지 않는 로드맵입니다. roadmapId = 1")
        );
    }

    private ExtractableResponse<Response> 로드맵_리뷰를_조회한다(final Long 로드맵_아이디, final CustomScrollRequest 스크롤_요청) {
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
}
