package co.kirikiri.integration;

import static co.kirikiri.integration.fixture.AuthenticationAPIFixture.로그인;
import static co.kirikiri.integration.fixture.CommonFixture.BEARER_TOKEN_FORMAT;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_참가_요청;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸을_생성하고_아이디를_반환한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸을_시작한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸의_사용자_정보를_정렬_기준없이_조회;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.기본_골룸_생성;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.십일_후;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.오늘;
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
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.checkfeed.service.dto.request.CheckFeedRequest;
import co.kirikiri.checkfeed.service.dto.response.GoalRoomCheckFeedResponse;
import co.kirikiri.common.service.dto.ErrorResponse;
import co.kirikiri.goalroom.service.dto.request.GoalRoomCreateRequest;
import co.kirikiri.goalroom.service.dto.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.goalroom.service.dto.response.GoalRoomMemberResponse;
import co.kirikiri.integration.helper.InitIntegrationTest;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

class GoalRoomCheckFeedIntegrationTest extends InitIntegrationTest {

    @Test
    void 인증_피드_등록을_요청한다() throws IOException {
        //given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");

        //when
        final ExtractableResponse<Response> 인증_피드_등록_응답 = 인증_피드_등록(골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청, 기본_로그인_토큰);

        //then
        assertThat(인증_피드_등록_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 인증용_사진이_없는_경우_인증_피드_등록이_실패한다() throws IOException {
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);
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
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);
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
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);
        골룸을_시작한다(기본_로그인_토큰, 골룸_아이디);

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/webp", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청 = new CheckFeedRequest(가짜_이미지_객체, "image description");
        인증_피드_등록(골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청, 기본_로그인_토큰);

        //when
        final ExtractableResponse<Response> 인증_피드_등록_응답 = 인증_피드_등록(골룸_아이디, 가짜_이미지_객체, 인증_피드_등록_요청, 기본_로그인_토큰);
        final ErrorResponse 예외_메세지 = 인증_피드_등록_응답.as(ErrorResponse.class);

        //then
        final List<GoalRoomMemberResponse> 골룸_사용자_응답 = 골룸의_사용자_정보를_정렬_기준없이_조회(골룸_아이디, 기본_로그인_토큰).as(new TypeRef<>() {
        });

        assertAll(
                () -> assertThat(인증_피드_등록_응답.statusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(예외_메세지.message()).isEqualTo("이미 오늘 인증 피드를 등록하였습니다."),
                () -> assertThat(골룸_사용자_응답.get(0).accomplishmentRate())
                        .isEqualTo(100 / (double) 정상적인_골룸_노드_인증_횟수)
        );
    }

    @Test
    void 진행_중인_노드의_허용된_인증_횟수_이상_요청할_경우_실패한다() throws IOException {
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 1, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(기본_로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);
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
}
