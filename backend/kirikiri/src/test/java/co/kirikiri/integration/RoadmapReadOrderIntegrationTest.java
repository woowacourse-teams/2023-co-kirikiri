package co.kirikiri.integration;

import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸_참가_요청;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸을_생성하고_아이디를_반환한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸을_시작한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.십일_후;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.오늘;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_노드_인증_횟수;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_이름;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_제한_인원;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_투두_컨텐츠;
import static co.kirikiri.integration.fixture.MemberAPIFixture.사용자를_추가하고_토큰을_조회한다;
import static co.kirikiri.integration.fixture.MemberAPIFixture.요청을_받는_사용자_자신의_정보_조회_요청;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.로드맵_생성;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.로드맵을_아이디로_조회하고_응답객체를_반환한다;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.리뷰를_생성한다;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.정렬된_로드맵_리스트_조회;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.정렬된_카테고리별_로드맵_리스트_조회;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.integration.helper.InitIntegrationTest;
import co.kirikiri.persistence.dto.RoadmapOrderType;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.member.response.MemberInformationResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import io.restassured.common.mapper.TypeRef;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoadmapReadOrderIntegrationTest extends InitIntegrationTest {

    @Test
    void 특정_카테고리의_로드맵_목록을_최신순으로_조회한다() throws IOException {
        // given
        // 기본, 두 번째 로드맵 - 여행, 세 번째 로드맵 - 여가
        // 첫 번째 로드맵 생성
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);

        // 두 번째 로드맵 생성
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 어드민_로그인_토큰);

        // 다른 카테고리의 세 번째 로드맵 생성
        final RoadmapCategory 다른_카테고리 = testTransactionService.로드맵_카테고리를_저장한다("여가");
        final RoadmapSaveRequest 세번째_로드맵_생성_요청 = new RoadmapSaveRequest(다른_카테고리.getId(), "third roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        로드맵_생성(세번째_로드맵_생성_요청, 어드민_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 정렬된_카테고리별_로드맵_리스트_조회(RoadmapOrderType.LATEST, 기본_카테고리.getId(), 10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }

    @Test
    void 특정_카테고리의_로드맵_목록을_리뷰_평점순으로_조회한다() throws IOException {
        // given
        // 기본, 두 번째 로드맵 - 여행, 세 번째 로드맵 - 여가
        // 첫 번째 로드맵 생성
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        // 사용자 추가
        final String 팔로워_액세스_토큰1 = 사용자를_추가하고_토큰을_조회한다("identifier2", "닉네임2");
        골룸을_생성하고_참여자에_사용자를_추가한다(팔로워_액세스_토큰1, 로드맵_응답);

        // 첫 번째 로드맵의 리뷰 추가
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 4.0);
        리뷰를_생성한다(팔로워_액세스_토큰1, 기본_로드맵_아이디, 로드맵_리뷰_생성_요청);

        // 두 번째 로드맵 생성
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 두번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(두번째_로드맵_아이디);

        // 사용자 추가
        final String 팔로워_액세스_토큰2 = 사용자를_추가하고_토큰을_조회한다("identifier3", "닉네임3");
        골룸을_생성하고_참여자에_사용자를_추가한다(팔로워_액세스_토큰2, 두번째_로드맵_응답);

        // 두 번째 로드맵의 리뷰 추가
        final RoadmapReviewSaveRequest 두번째_로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);
        리뷰를_생성한다(팔로워_액세스_토큰2, 두번째_로드맵_아이디, 두번째_로드맵_리뷰_생성_요청);

        // 다른 카테고리의 세 번째 로드맵 생성
        final RoadmapCategory 다른_카테고리 = testTransactionService.로드맵_카테고리를_저장한다("여가");
        final RoadmapSaveRequest 세번째_로드맵_생성_요청 = new RoadmapSaveRequest(다른_카테고리.getId(), "third roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        로드맵_생성(세번째_로드맵_생성_요청, 어드민_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 정렬된_카테고리별_로드맵_리스트_조회(RoadmapOrderType.REVIEW_RATE, 기본_카테고리.getId(),
                10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }

    @Test
    void 특정_카테고리의_로드맵_목록을_골룸_개수순으로_조회한다() throws IOException {
        // given
        // 기본, 두 번째 로드맵 - 여행, 세 번째 로드맵 - 여가
        // 첫 번째 로드맵 생성
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);

        // 두 번째 로드맵 생성
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 두번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(두번째_로드맵_아이디);

        // 두 번째 로드맵에 대한 골룸 생성
        로드맵에_대한_골룸을_생성한다(두번째_로드맵_응답);

        // 다른 카테고리의 세 번째 로드맵 생성
        final RoadmapCategory 다른_카테고리 = testTransactionService.로드맵_카테고리를_저장한다("여가");
        final RoadmapSaveRequest 세번째_로드맵_생성_요청 = new RoadmapSaveRequest(다른_카테고리.getId(), "third roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        로드맵_생성(세번째_로드맵_생성_요청, 어드민_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 정렬된_카테고리별_로드맵_리스트_조회(RoadmapOrderType.GOAL_ROOM_COUNT,
                기본_카테고리.getId(), 10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }

    @Test
    void 특정_카테고리의_로드맵_목록을_참여자수_순으로_조회한다() throws IOException {
        // given
        // 기본, 두 번째 로드맵 - 여행, 세 번째 로드맵 - 여가
        // 첫 번째 로드맵 생성
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);

        // 두 번째 로드맵 생성
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 두번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(두번째_로드맵_아이디);

        // 두 번째 로드맵에 대한 골룸 생성 및 참가 신청
        final Long 두번째_로드맵의_골룸_아이디 = 로드맵에_대한_골룸을_생성한다(두번째_로드맵_응답);
        final String 팔로워_액세스_토큰 = 사용자를_추가하고_토큰을_조회한다("identifier2", "닉네임2");
        골룸_참가_요청(두번째_로드맵의_골룸_아이디, 팔로워_액세스_토큰);
        골룸을_시작한다(어드민_로그인_토큰, 두번째_로드맵의_골룸_아이디);

        // 다른 카테고리의 세 번째 로드맵 생성
        final RoadmapCategory 다른_카테고리 = testTransactionService.로드맵_카테고리를_저장한다("여가");
        final RoadmapSaveRequest 세번째_로드맵_생성_요청 = new RoadmapSaveRequest(다른_카테고리.getId(), "third roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        로드맵_생성(세번째_로드맵_생성_요청, 어드민_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 정렬된_카테고리별_로드맵_리스트_조회(RoadmapOrderType.PARTICIPANT_COUNT,
                기본_카테고리.getId(), 10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }

    @Test
    void 전체_로드맵_목록을_최신순으로_조회한다() throws IOException {
        // given
        // 첫 번째 로드맵 생성
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);

        // 두 번째 로드맵 생성
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 어드민_로그인_토큰);

        // 세 번째 로드맵 생성
        final RoadmapCategory 다른_카테고리 = testTransactionService.로드맵_카테고리를_저장한다("여가");
        final RoadmapSaveRequest 세번째_로드맵_생성_요청 = new RoadmapSaveRequest(다른_카테고리.getId(), "third roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번째_로드맵_생성_요청, 어드민_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 정렬된_로드맵_리스트_조회(RoadmapOrderType.LATEST, 10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(세번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(2).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }

    @Test
    void 전체_카테고리의_로드맵_목록을_리뷰_평점순으로_조회한다() throws IOException {
        // given
        // 기본, 두 번째 로드맵 - 여행, 세 번째 로드맵 - 여가
        // 첫 번째 로드맵 생성
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        // 사용자 추가
        final String 팔로워_액세스_토큰1 = 사용자를_추가하고_토큰을_조회한다("identifier2", "닉네임2");
        골룸을_생성하고_참여자에_사용자를_추가한다(팔로워_액세스_토큰1, 로드맵_응답);

        // 첫 번째 로드맵의 리뷰 추가
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 4.0);
        리뷰를_생성한다(팔로워_액세스_토큰1, 기본_로드맵_아이디, 로드맵_리뷰_생성_요청);

        // 두 번째 로드맵 생성
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 두번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(두번째_로드맵_아이디);

        // 사용자 추가
        final String 팔로워_액세스_토큰2 = 사용자를_추가하고_토큰을_조회한다("identifier3", "닉네임3");
        골룸을_생성하고_참여자에_사용자를_추가한다(팔로워_액세스_토큰2, 두번째_로드맵_응답);

        // 두 번째 로드맵의 리뷰 추가
        final RoadmapReviewSaveRequest 두번째_로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);
        리뷰를_생성한다(팔로워_액세스_토큰2, 두번째_로드맵_아이디, 두번째_로드맵_리뷰_생성_요청);

        // 세 번째 로드맵 생성
        final RoadmapCategory 다른_카테고리 = testTransactionService.로드맵_카테고리를_저장한다("여가");
        final RoadmapSaveRequest 세번째_로드맵_생성_요청 = new RoadmapSaveRequest(다른_카테고리.getId(), "third roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번째_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 세번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(세번째_로드맵_아이디);

        // 사용자 추가
        final String 팔로워_액세스_토큰3 = 사용자를_추가하고_토큰을_조회한다("identifier4", "닉네임4");
        골룸을_생성하고_참여자에_사용자를_추가한다(팔로워_액세스_토큰3, 세번째_로드맵_응답);

        // 세 번째 로드맵의 리뷰 추가
        final RoadmapReviewSaveRequest 세번째_로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 3.5);
        리뷰를_생성한다(팔로워_액세스_토큰3, 세번째_로드맵_아이디, 세번째_로드맵_리뷰_생성_요청);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 정렬된_로드맵_리스트_조회(RoadmapOrderType.REVIEW_RATE, 10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(기본_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(2).roadmapId()).isEqualTo(세번째_로드맵_아이디);
    }

    @Test
    void 전체_카테고리의_로드맵_목록을_골룸_개수순으로_조회한다() throws IOException {
        // given
        // 기본, 두 번째 로드맵 - 여행, 세 번째 로드맵 - 여가
        // 첫 번째 로드맵 생성
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);

        // 두 번째 로드맵 생성
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 두번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(두번째_로드맵_아이디);

        // 두 번째 로드맵에 대한 골룸 생성
        로드맵에_대한_골룸을_생성한다(두번째_로드맵_응답);

        // 세 번째 로드맵 생성
        final RoadmapCategory 다른_카테고리 = testTransactionService.로드맵_카테고리를_저장한다("여가");
        final RoadmapSaveRequest 세번째_로드맵_생성_요청 = new RoadmapSaveRequest(다른_카테고리.getId(), "third roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번째_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 세번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(세번째_로드맵_아이디);

        // 세 번째 로드맵에 대한 골룸 생성
        로드맵에_대한_골룸을_생성한다(세번째_로드맵_응답);
        로드맵에_대한_골룸을_생성한다(세번째_로드맵_응답);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 정렬된_로드맵_리스트_조회(RoadmapOrderType.GOAL_ROOM_COUNT, 10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(세번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(2).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }

    @Test
    void 전체_카테고리의_로드맵_목록을_참여자수_순으로_조회한다() throws IOException {
        // given
        // 기본, 두 번째 로드맵 - 여행, 세 번째 로드맵 - 여가
        // 첫 번째 로드맵 생성
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 첫번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        // 첫 번째 로드맵에 대한 골룸 생성 및 참가 신청
        final Long 첫번째_로드맵의_골룸_아이디 = 로드맵에_대한_골룸을_생성한다(첫번째_로드맵_응답);
        final String 팔로워_액세스_토큰 = 사용자를_추가하고_토큰을_조회한다("identifier2", "닉네임2");
        골룸_참가_요청(첫번째_로드맵의_골룸_아이디, 팔로워_액세스_토큰);
        골룸을_시작한다(어드민_로그인_토큰, 첫번째_로드맵의_골룸_아이디);

        // 두 번째 로드맵 생성
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 어드민_로그인_토큰);

        // 세 번째 로드맵 생성
        final RoadmapCategory 다른_카테고리 = testTransactionService.로드맵_카테고리를_저장한다("여가");
        final RoadmapSaveRequest 세번째_로드맵_생성_요청 = new RoadmapSaveRequest(다른_카테고리.getId(), "third roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번째_로드맵_생성_요청, 어드민_로그인_토큰);
        final RoadmapResponse 세번째_로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(세번째_로드맵_아이디);

        // 세 번째 로드맵에 대한 골룸 생성 및 참가 신청
        final Long 세번째_로드맵의_골룸_아이디 = 로드맵에_대한_골룸을_생성한다(세번째_로드맵_응답);
        final String 팔로워_액세스_토큰2 = 사용자를_추가하고_토큰을_조회한다("identifier3", "닉네임3");
        골룸_참가_요청(세번째_로드맵의_골룸_아이디, 팔로워_액세스_토큰2);
        골룸을_시작한다(어드민_로그인_토큰, 세번째_로드맵의_골룸_아이디);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 정렬된_로드맵_리스트_조회(RoadmapOrderType.PARTICIPANT_COUNT, 10)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(기본_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(세번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(2).roadmapId()).isEqualTo(두번째_로드맵_아이디);
    }

    private Long 로드맵에_대한_골룸을_생성한다(final RoadmapResponse 로드맵_응답) {
        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);
        return 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 어드민_로그인_토큰);
    }

    private void 골룸을_생성하고_참여자에_사용자를_추가한다(final String 액세스_토큰, final RoadmapResponse 로드맵_응답) {
        final MemberInformationResponse 팔로워_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(액세스_토큰).as(new TypeRef<>() {
        });

        final MemberInformationResponse 리더_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(어드민_로그인_토큰).as(new TypeRef<>() {
        });
        final GoalRoom 골룸 = testTransactionService.완료한_골룸을_생성한다(로드맵_응답);
        testTransactionService.골룸에_대한_참여자_리스트를_생성한다(리더_정보, 골룸, 팔로워_정보);
    }
}
