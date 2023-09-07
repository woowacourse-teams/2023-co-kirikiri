package co.kirikiri.integration.helper;

import static co.kirikiri.integration.fixture.AuthenticationAPIFixture.기본_로그인;
import static co.kirikiri.integration.fixture.AuthenticationAPIFixture.기본_어드민_로그인;
import static co.kirikiri.integration.fixture.CommonFixture.BEARER_TOKEN_FORMAT;
import static co.kirikiri.integration.fixture.MemberAPIFixture.기본_회원가입;
import static co.kirikiri.integration.fixture.MemberAPIFixture.어드민_회원가입;

import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;

public class InitIntegrationTest extends IntegrationTest {

    protected static Long 기본_회원_아이디;
    protected static Long 어드민_회원_아이디;
    protected static String 기본_로그인_토큰;
    protected static String 어드민_로그인_토큰;
    protected static String 기본_재발행_토큰;
    protected static RoadmapCategory 기본_카테고리;
    protected static RoadmapSaveRequest 기본_로드맵_생성_요청;

    @BeforeEach
    void init() {
        기본_회원_아이디 = 기본_회원가입();
        기본_로그인_토큰 = String.format(BEARER_TOKEN_FORMAT, 기본_로그인().accessToken());
        기본_재발행_토큰 = 기본_로그인().refreshToken();
        어드민_회원_아이디 = 어드민_회원가입();
        어드민_로그인_토큰 = String.format(BEARER_TOKEN_FORMAT, 기본_어드민_로그인().accessToken());
        기본_카테고리 = testTransactionService.로드맵_카테고리를_저장한다("여행");
        기본_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("roadmap 1st week", "로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));
    }
}
