package co.kirikiri.integration;

import static co.kirikiri.integration.fixture.AuthenticationAPIFixture.로그인;
import static co.kirikiri.integration.fixture.CommonFixture.BEARER_TOKEN_FORMAT;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_노드_조회;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_아이디로_골룸을_조회;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_아이디와_토큰으로_골룸_정보를_조회;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_참가_요청;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_투두리스트_조회;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_투두리스트_추가;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸을_생성하고_아이디를_반환한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸을_시작한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸의_사용자_정보를_전체_조회;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸의_사용자_정보를_정렬_기준없이_조회;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.기본_골룸_생성;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.로드맵_아이디로_골룸_목록_조회;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.사용자가_참여한_골룸_중_골룸_진행_상태에_따라_목록을_조회;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.사용자의_모든_골룸_조회;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.사용자의_특정_골룸_정보를_조회한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.삼십일_후;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.십일_후;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.오늘;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.이십일_후;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.인증_피드_등록;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.인증_피드_전체_조회_요청;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_노드_인증_횟수;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_이름;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_제한_인원;
import static co.kirikiri.integration.fixture.MemberAPIFixture.DEFAULT_EMAIL;
import static co.kirikiri.integration.fixture.MemberAPIFixture.회원가입;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.로드맵_생성;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.로드맵을_아이디로_조회하고_응답객체를_반환한다;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.integration.helper.InitIntegrationTest;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsOrderType;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.goalroom.GoalRoomMemberSortTypeDto;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.CheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodesResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberGoalRoomForListResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

class GoalRoomReadIntegrationTest extends InitIntegrationTest {

    @Test
    void 골룸_아이디로_골룸_정보를_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        // when
        final GoalRoomResponse 골룸_응답값 = 골룸_아이디로_골룸을_조회(기본_골룸_아이디)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(골룸_응답값.name()).isEqualTo(정상적인_골룸_이름);
    }

    @Test
    void 골룸_아이디와_사용자_아이디로_골룸_정보를_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        // when
        final GoalRoomCertifiedResponse 골룸_응답값 = 골룸_아이디와_토큰으로_골룸_정보를_조회(기본_골룸_아이디, 기본_로그인_토큰)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(골룸_응답값.name()).isEqualTo(정상적인_골룸_이름);
    }

    @Test
    void 골룸_투두리스트를_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final GoalRoomTodoRequest 골룸_투두_생성_요청 = new GoalRoomTodoRequest("content", 이십일_후, 삼십일_후);
        골룸_투두리스트_추가(기본_로그인_토큰, 기본_골룸_아이디, 골룸_투두_생성_요청);

        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);

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

    @Test
    void 진행중인_사용자_단일_골룸을_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        골룸_참가_요청(기본_골룸_아이디, 팔로워_액세스_토큰);
        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/webp", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");
        인증_피드_등록(기본_골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청, 기본_로그인_토큰);
        인증_피드_등록(기본_골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청, 팔로워_액세스_토큰);

        // when
        final MemberGoalRoomResponse 요청_응답값 = 사용자의_특정_골룸_정보를_조회한다(기본_로그인_토큰, 기본_골룸_아이디);

        // then
        final MemberGoalRoomResponse 예상되는_응답값 = new MemberGoalRoomResponse(정상적인_골룸_이름, "RUNNING", 기본_회원_아이디,
                2, 정상적인_골룸_제한_인원, 오늘, 십일_후, 로드맵_응답.content().id(),
                new GoalRoomRoadmapNodesResponse(false, false,
                        List.of(new GoalRoomRoadmapNodeResponse(로드맵_응답.content().nodes().get(0).id(),
                                "roadmap 1st week", 오늘, 십일_후, 정상적인_골룸_노드_인증_횟수))),
                List.of(),
                List.of(new CheckFeedResponse(2L, "default-image-path", "image description", LocalDate.now()),
                        new CheckFeedResponse(1L, "default-image-path", "image description", LocalDate.now())));

        assertThat(요청_응답값)
                .usingRecursiveComparison()
                .ignoringFields("checkFeeds.imageUrl", "checkFeeds.createdAt")
                .isEqualTo(예상되는_응답값);
    }

    @Test
    void 골룸_시작_전에_사용자_단일_골룸_조회_시_인증_피드가_빈_응답을_반환한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        //when
        final MemberGoalRoomResponse 요청_응답값 = 사용자의_특정_골룸_정보를_조회한다(기본_로그인_토큰, 기본_골룸_아이디);

        //then
        assertThat(요청_응답값.checkFeeds()).isEmpty();
    }

    @Test
    void 사용자의_모든_골룸_목록을_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "두번째_로드맵 제목", "두번째_로드맵 소개글",
                "두번째_로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("두번째_로드맵 1주차", "두번째_로드맵 1주차 내용", null)), null);
        로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 두번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 두번째_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 두번째_로드맵_응답);

        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);
        골룸을_시작한다(기본_로그인_토큰, 두번째_골룸_아이디);

        // when
        final List<MemberGoalRoomForListResponse> 요청_응답값 = 사용자의_모든_골룸_조회(기본_로그인_토큰)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(요청_응답값.get(0).goalRoomId()).isEqualTo(기본_골룸_아이디);
        assertThat(요청_응답값.get(1).goalRoomId()).isEqualTo(두번째_골룸_아이디);
    }

    @Test
    void 사용자가_참여한_골룸_중_모집_중인_골룸_목록을_조회한다() throws IOException {
        // given
        final Long 첫번쨰_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 첫번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(첫번쨰_로드맵_아이디);

        final Long 첫번째_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 첫번째_로드맵_응답);

        final Long 두번째_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 두번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(두번째_로드맵_아이디);

        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(두번째_로드맵_응답.content().nodes().get(0).id(), 10, 십일_후, 이십일_후));
        final GoalRoomCreateRequest 두번째_골룸_생성_요청 = new GoalRoomCreateRequest(두번째_로드맵_응답.content().id(), 정상적인_골룸_이름,
                20, 골룸_노드_별_기간_요청);

        final Long 두번째_골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(두번째_골룸_생성_요청, 기본_로그인_토큰);

        골룸을_시작한다(기본_로그인_토큰, 첫번째_골룸_아이디);
        골룸을_시작한다(기본_로그인_토큰, 두번째_골룸_아이디);

        // when
        final List<MemberGoalRoomForListResponse> 요청_응답값 = 사용자가_참여한_골룸_중_골룸_진행_상태에_따라_목록을_조회(기본_로그인_토큰, "RECRUITING")
                .as(new TypeRef<>() {
                });

        // then
        assertThat(요청_응답값.get(0).goalRoomId()).isEqualTo(두번째_골룸_아이디);
    }

    @Test
    void 사용자가_참여한_골룸_중_진행_중인_골룸_목록을_조회한다() throws IOException {
        // given
        final Long 첫번쨰_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 첫번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(첫번쨰_로드맵_아이디);

        final Long 첫번째_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 첫번째_로드맵_응답);

        final Long 두번째_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 두번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(두번째_로드맵_아이디);

        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(두번째_로드맵_응답.content().nodes().get(0).id(), 10, 십일_후, 이십일_후));
        final GoalRoomCreateRequest 두번째_골룸_생성_요청 = new GoalRoomCreateRequest(두번째_로드맵_응답.content().id(), 정상적인_골룸_이름,
                20, 골룸_노드_별_기간_요청);

        final Long 두번째_골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(두번째_골룸_생성_요청, 기본_로그인_토큰);

        골룸을_시작한다(기본_로그인_토큰, 첫번째_골룸_아이디);
        골룸을_시작한다(기본_로그인_토큰, 두번째_골룸_아이디);

        // when
        final List<MemberGoalRoomForListResponse> 요청_응답값 = 사용자가_참여한_골룸_중_골룸_진행_상태에_따라_목록을_조회(기본_로그인_토큰, "RUNNING")
                .as(new TypeRef<>() {
                });

        // then
        assertThat(요청_응답값.get(0).goalRoomId()).isEqualTo(첫번째_골룸_아이디);
    }

    @Test
    void 골룸_노드를_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        골룸_참가_요청(기본_골룸_아이디, 기본_로그인_토큰);
        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);

        // when
        final List<GoalRoomRoadmapNodeResponse> 골룸_노드_응답값 = 골룸_노드_조회(기본_골룸_아이디, 기본_로그인_토큰)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(골룸_노드_응답값.get(0).title()).isEqualTo("roadmap 1st week");
    }

    @Test
    void 골룸_노드_조회시_존재하지_않은_골룸일_경우_예외가_발생한다() {
        // given
        // when
        final ErrorResponse 예외_응답값 = 골룸_노드_조회(1L, 기본_로그인_토큰)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(예외_응답값)
                .isEqualTo(new ErrorResponse("존재하지 않는 골룸입니다. goalRoomId = 1"));
    }

    @Test
    void 골룸_노드_조회시_참여하지_않은_사용자일_경우_예외가_발생한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 다른_사용자_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);

        // when
        final ErrorResponse 예외_응답값 = 골룸_노드_조회(기본_골룸_아이디, 다른_사용자_액세스_토큰)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(예외_응답값)
                .isEqualTo(new ErrorResponse("골룸에 참여하지 않은 사용자입니다. goalRoomId = 1 memberIdentifier = identifier2"));
    }

    @Test
    void 골룸의_인증피드를_전체_조회한다() throws IOException {
        // given
        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        final String 팔로워_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());

        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        골룸_참가_요청(기본_골룸_아이디, 팔로워_액세스_토큰);
        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청1 = new CheckFeedRequest(가짜_이미지_객체, "image description1");
        final CheckFeedRequest 인증_피드_등록_요청2 = new CheckFeedRequest(가짜_이미지_객체, "image description2");

        인증_피드_등록(기본_골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청1, 기본_로그인_토큰);
        인증_피드_등록(기본_골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청2, 팔로워_액세스_토큰);

        //when
        final List<GoalRoomCheckFeedResponse> 인증_피드_전체_조회_요청에_대한_응답 = 인증_피드_전체_조회_요청(팔로워_액세스_토큰, 기본_골룸_아이디)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(인증_피드_전체_조회_요청에_대한_응답.get(0).checkFeed().description()).isEqualTo(인증_피드_등록_요청2.description());
        assertThat(인증_피드_전체_조회_요청에_대한_응답.get(1).checkFeed().description()).isEqualTo(인증_피드_등록_요청1.description());
    }

    @Test
    void 골룸의_인증피드를_전체_조회시_존재하지_않는_골룸인_경우_예외가_발생한다() throws IOException {
        // given
        //when
        final Long 존재하지_않는_골룸_아이디 = 1L;
        final ExtractableResponse<Response> 인증_피드_전체_조회_요청에_대한_응답 = 인증_피드_전체_조회_요청(기본_로그인_토큰, 존재하지_않는_골룸_아이디);

        // then
        final ErrorResponse 인증_피드_전체_조회_응답_바디 = jsonToClass(인증_피드_전체_조회_요청에_대한_응답.asString(), new TypeReference<>() {
        });
        assertThat(인증_피드_전체_조회_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(인증_피드_전체_조회_응답_바디).isEqualTo(new ErrorResponse("존재하지 않는 골룸입니다. goalRoomId = 1"));
    }

    @Test
    void 골룸의_인증피드를_전체_조회시_골룸에_참여하지_않은_사용자면_예외가_발생한다() throws IOException {
        // given
        final MemberJoinRequest 다른_회원_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follower", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 다른_회원_로그인_요청 = new LoginRequest(다른_회원_회원_가입_요청.identifier(), 다른_회원_회원_가입_요청.password());
        회원가입(다른_회원_회원_가입_요청);
        final String 다른_회원_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(다른_회원_로그인_요청).accessToken());

        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);
        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청1 = new CheckFeedRequest(가짜_이미지_객체, "image description1");

        인증_피드_등록(기본_골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청1, 기본_로그인_토큰);

        //when
        final ExtractableResponse<Response> 인증_피드_전체_조회_요청에_대한_응답 = 인증_피드_전체_조회_요청(다른_회원_액세스_토큰, 기본_골룸_아이디);

        // then
        final ErrorResponse 인증_피드_전체_조회_응답_바디 = jsonToClass(인증_피드_전체_조회_요청에_대한_응답.asString(), new TypeReference<>() {
        });
        assertThat(인증_피드_전체_조회_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(인증_피드_전체_조회_응답_바디).isEqualTo(new ErrorResponse("골룸에 참여하지 않은 회원입니다."));
    }

    @Test
    void 골룸의_사용자_정보를_달성률순으로_전체_조회한다() throws IOException {
        // given
        final MemberJoinRequest 팔로워1_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follow1", GenderType.FEMALE, DEFAULT_EMAIL);
        final MemberJoinRequest 팔로워2_회원_가입_요청 = new MemberJoinRequest("identifier3", "paswword2@",
                "follow2", GenderType.FEMALE, DEFAULT_EMAIL);
        final Long 팔로워1_아이디 = 회원가입(팔로워1_회원_가입_요청);
        final Long 팔로워2_아이디 = 회원가입(팔로워2_회원_가입_요청);

        final LoginRequest 팔로워1_로그인_요청 = new LoginRequest(팔로워1_회원_가입_요청.identifier(), 팔로워1_회원_가입_요청.password());
        final LoginRequest 팔로워2_로그인_요청 = new LoginRequest(팔로워2_회원_가입_요청.identifier(), 팔로워2_회원_가입_요청.password());

        final String 팔로워1_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워1_로그인_요청).accessToken());
        final String 팔로워2_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워2_로그인_요청).accessToken());

        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        골룸_참가_요청(기본_골룸_아이디, 팔로워1_액세스_토큰);
        골룸_참가_요청(기본_골룸_아이디, 팔로워2_액세스_토큰);
        골룸을_시작한다(기본_로그인_토큰, 기본_골룸_아이디);

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청1 = new CheckFeedRequest(가짜_이미지_객체, "image description1");

        인증_피드_등록(기본_골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청1, 팔로워1_액세스_토큰);

        //when
        final List<GoalRoomMemberResponse> 골룸_사용자_응답 = 골룸의_사용자_정보를_전체_조회(기본_골룸_아이디, 기본_로그인_토큰,
                GoalRoomMemberSortTypeDto.ACCOMPLISHMENT_RATE.name()).as(new TypeRef<>() {
        });

        // then
        assertThat(골룸_사용자_응답.get(0).memberId()).isEqualTo(팔로워1_아이디);
        assertThat(골룸_사용자_응답.get(1).memberId()).isEqualTo(기본_회원_아이디);
        assertThat(골룸_사용자_응답.get(2).memberId()).isEqualTo(팔로워2_아이디);
    }

    @Test
    void 모집중인_골룸의_사용자_정보를_참가한_최신순으로_전체_조회한다() throws IOException {
        // given
        final MemberJoinRequest 팔로워1_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follow1", GenderType.FEMALE, DEFAULT_EMAIL);
        final MemberJoinRequest 팔로워2_회원_가입_요청 = new MemberJoinRequest("identifier3", "paswword2@",
                "follow2", GenderType.FEMALE, DEFAULT_EMAIL);
        final Long 팔로워1_아이디 = 회원가입(팔로워1_회원_가입_요청);
        final Long 팔로워2_아이디 = 회원가입(팔로워2_회원_가입_요청);

        final LoginRequest 팔로워1_로그인_요청 = new LoginRequest(팔로워1_회원_가입_요청.identifier(), 팔로워1_회원_가입_요청.password());
        final LoginRequest 팔로워2_로그인_요청 = new LoginRequest(팔로워2_회원_가입_요청.identifier(), 팔로워2_회원_가입_요청.password());

        final String 팔로워1_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워1_로그인_요청).accessToken());
        final String 팔로워2_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워2_로그인_요청).accessToken());

        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        골룸_참가_요청(기본_골룸_아이디, 팔로워1_액세스_토큰);
        골룸_참가_요청(기본_골룸_아이디, 팔로워2_액세스_토큰);

        //when
        final List<GoalRoomMemberResponse> 골룸_사용자_응답 = 골룸의_사용자_정보를_전체_조회(기본_골룸_아이디, 기본_로그인_토큰,
                GoalRoomMemberSortTypeDto.JOINED_DESC.name()).as(new TypeRef<>() {
        });

        // then
        assertThat(골룸_사용자_응답.get(0).memberId()).isEqualTo(팔로워2_아이디);
        assertThat(골룸_사용자_응답.get(1).memberId()).isEqualTo(팔로워1_아이디);
        assertThat(골룸_사용자_응답.get(2).memberId()).isEqualTo(기본_회원_아이디);
    }

    @Test
    void 모집중인_골룸의_사용자_정보_조회시_정렬기준을_입력하지_않으면_참여한지_오래된순으로_정렬한다() throws IOException {
        // given
        final MemberJoinRequest 팔로워1_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                "follow1", GenderType.FEMALE, DEFAULT_EMAIL);
        final MemberJoinRequest 팔로워2_회원_가입_요청 = new MemberJoinRequest("identifier3", "paswword2@",
                "follow2", GenderType.FEMALE, DEFAULT_EMAIL);
        final Long 팔로워1_아이디 = 회원가입(팔로워1_회원_가입_요청);
        final Long 팔로워2_아이디 = 회원가입(팔로워2_회원_가입_요청);

        final LoginRequest 팔로워1_로그인_요청 = new LoginRequest(팔로워1_회원_가입_요청.identifier(), 팔로워1_회원_가입_요청.password());
        final LoginRequest 팔로워2_로그인_요청 = new LoginRequest(팔로워2_회원_가입_요청.identifier(), 팔로워2_회원_가입_요청.password());

        final String 팔로워1_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워1_로그인_요청).accessToken());
        final String 팔로워2_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워2_로그인_요청).accessToken());

        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        골룸_참가_요청(기본_골룸_아이디, 팔로워1_액세스_토큰);
        골룸_참가_요청(기본_골룸_아이디, 팔로워2_액세스_토큰);

        //when
        final List<GoalRoomMemberResponse> 골룸_사용자_응답 = 골룸의_사용자_정보를_정렬_기준없이_조회(기본_골룸_아이디, 기본_로그인_토큰)
                .as(new TypeRef<>() {
                });

        // then
        assertThat(골룸_사용자_응답.get(0).memberId()).isEqualTo(기본_회원_아이디);
        assertThat(골룸_사용자_응답.get(1).memberId()).isEqualTo(팔로워1_아이디);
        assertThat(골룸_사용자_응답.get(2).memberId()).isEqualTo(팔로워2_아이디);
    }

    @Test
    void 골룸의_사용자_정보_조회시_존재하지_않는_골룸이면_예외가_발생한다() {
        // given
        // when
        final ErrorResponse 예외_응답 = 골룸의_사용자_정보를_전체_조회(1L, 기본_로그인_토큰,
                GoalRoomMemberSortTypeDto.ACCOMPLISHMENT_RATE.name()).as(new TypeRef<>() {
        });

        // then
        assertThat(예외_응답.message()).isEqualTo("존재하지 않는 골룸입니다. goalRoomId = 1");
    }

    @Test
    void 로드맵의_골룸_목록을_마감임박순으로_조회한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final Long 기본_골룸_아이디 = 기본_골룸_생성(기본_로그인_토큰, 로드맵_응답);

        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 10, 십일_후, 이십일_후));
        final GoalRoomCreateRequest 두번째_골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.content().id(), 정상적인_골룸_이름,
                20, 골룸_노드_별_기간_요청);

        final Long 두번째_골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(두번째_골룸_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapGoalRoomResponses 로드맵_아이디로_골룸_목록_조회_응답1 = 로드맵_아이디로_골룸_목록_조회(기본_로그인_토큰, 기본_로드맵_아이디,
                RoadmapGoalRoomsOrderType.CLOSE_TO_DEADLINE.name(), 10).as(
                new TypeRef<>() {
                });

        // then
        assertThat(로드맵_아이디로_골룸_목록_조회_응답1.responses().get(0).goalRoomId()).isEqualTo(기본_골룸_아이디);
        assertThat(로드맵_아이디로_골룸_목록_조회_응답1.responses().get(1).goalRoomId()).isEqualTo(두번째_골룸_아이디);
    }
}
