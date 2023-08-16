package co.kirikiri.integration;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.integration.helper.TestTransactionQuery;
import co.kirikiri.integration.helper.TestTransactionService;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.RoadmapScheduler;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RoadmapSchedulerIntegrationTest extends GoalRoomCreateIntegrationTest {

    private static final LocalDate 현재부터_3개월_1일_전 = 오늘.minusMonths(3).minusDays(1);

    private final TestTransactionQuery testTransactionQuery;
    private final RoadmapScheduler roadmapScheduler;
    private final RoadmapRepository roadmapRepository;

    public RoadmapSchedulerIntegrationTest(final RoadmapCategoryRepository roadmapCategoryRepository,
                                           final TestTransactionService testTransactionService,
                                           final TestTransactionQuery testTransactionQuery,
                                           final RoadmapScheduler roadmapScheduler,
                                           final RoadmapRepository roadmapRepository) {
        super(roadmapCategoryRepository, testTransactionService);
        this.testTransactionQuery = testTransactionQuery;
        this.roadmapScheduler = roadmapScheduler;
        this.roadmapRepository = roadmapRepository;
    }

    @Override
    @BeforeEach
    void init() {
        super.init();
    }

    @Test
    void 삭제된_상태의_로드맵을_삭제시_모든_골룸이_종료된지_3개월이_지났으면_정상적으로_삭제한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디1 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final GoalRoomTodoRequest 골룸_투두_요청2 = new GoalRoomTodoRequest("골룸 투두", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청2 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청2 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청2, 골룸_노드_별_기간_요청2);

        final Long 골룸_아이디2 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청2, 기본_로그인_토큰);

        로드맵_삭제(기본_로드맵_아이디, 기본_로그인_토큰);

        testTransactionQuery.골룸의_상태와_종료날짜를_변경한다(골룸_아이디1, GoalRoomStatus.COMPLETED, 현재부터_3개월_1일_전);
        testTransactionQuery.골룸의_상태와_종료날짜를_변경한다(골룸_아이디2, GoalRoomStatus.COMPLETED, 현재부터_3개월_1일_전);

        // when
        roadmapScheduler.deleteRoadmaps();

        // then
        assertThat(roadmapRepository.findAll()).hasSize(0);
    }

    @Test
    void 삭제된_상태의_로드맵_삭제시_종료되지_않은_골룸이_있으면_삭제되지_않는다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디1 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final GoalRoomTodoRequest 골룸_투두_요청2 = new GoalRoomTodoRequest("골룸 투두", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청2 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청2 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청2, 골룸_노드_별_기간_요청2);

        final Long 골룸_아이디2 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청2, 기본_로그인_토큰);

        로드맵_삭제(기본_로드맵_아이디, 기본_로그인_토큰);

        testTransactionQuery.골룸의_상태와_종료날짜를_변경한다(골룸_아이디1, GoalRoomStatus.COMPLETED, 현재부터_3개월_1일_전);

        // when
        roadmapScheduler.deleteRoadmaps();

        // then
        assertThat(roadmapRepository.findAll()).hasSize(1);
    }

    @Test
    void 삭제된_상태의_로드맵_삭제시_종료된지_3개월이_지나지_않은_골룸이_있으면_삭제되지_않는다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 기본_로드맵_생성(기본_로그인_토큰);
        final RoadmapResponse 로드맵_응답 = 로드맵을_아이디로_조회하고_응답객체를_반환한다(기본_로드맵_아이디);

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청, 골룸_노드_별_기간_요청);

        final Long 골룸_아이디1 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);

        final GoalRoomTodoRequest 골룸_투두_요청2 = new GoalRoomTodoRequest("골룸 투두", 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청2 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청2 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_투두_요청2, 골룸_노드_별_기간_요청2);

        final Long 골룸_아이디2 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청2, 기본_로그인_토큰);

        로드맵_삭제(기본_로드맵_아이디, 기본_로그인_토큰);

        testTransactionQuery.골룸의_상태와_종료날짜를_변경한다(골룸_아이디1, GoalRoomStatus.COMPLETED, 현재부터_3개월_1일_전);
        testTransactionQuery.골룸의_상태와_종료날짜를_변경한다(골룸_아이디2, GoalRoomStatus.COMPLETED, 오늘);

        // when
        roadmapScheduler.deleteRoadmaps();

        // then
        assertThat(roadmapRepository.findAll()).hasSize(1);
    }
}
