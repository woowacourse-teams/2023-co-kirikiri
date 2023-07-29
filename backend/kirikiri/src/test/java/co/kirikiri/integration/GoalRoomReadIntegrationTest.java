package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapContents;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapNodeRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.goalroom.GoalRoomFilterTypeDto;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class GoalRoomReadIntegrationTest extends IntegrationTest {

    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";
    private static final LocalDate 오늘 = LocalDate.now();
    private static final LocalDate 십일_후 = 오늘.plusDays(10L);
    private static final LocalDate 이십일_후 = 오늘.plusDays(20);
    private static final LocalDate 삼십일_후 = 오늘.plusDays(30);

    private static final String 정상적인_골룸_이름 = "GOAL_ROOM_NAME";
    private static final int 정상적인_골룸_제한_인원 = 20;
    private static final String 정상적인_골룸_투두_컨텐츠 = "GOAL_ROOM_TO_DO_CONTENT";

    private static final String BEARER = "Bearer ";
    private static final String 카테고리_이름 = "여가";

    private static final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", "password12!@#$%", "nickname",
            "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
    private static final LoginRequest 로그인_요청 = new LoginRequest(회원가입_요청.identifier(), 회원가입_요청.password());

    private static final MemberJoinRequest 골룸_참여자1_회원가입_요청 = new MemberJoinRequest("identifier2", "password!2", "name2",
            "010-1111-2222", GenderType.FEMALE, LocalDate.now());
    private static final MemberJoinRequest 골룸_참여자2_회원가입_요청 = new MemberJoinRequest("identifier3", "password!3", "name3",
            "010-1111-3333", GenderType.FEMALE, LocalDate.now());
    private static final LoginRequest 골룸_참여자1_로그인_요청 = new LoginRequest(골룸_참여자1_회원가입_요청.identifier(),
            골룸_참여자1_회원가입_요청.password());
    private static final LoginRequest 골룸_참여자2_로그인_요청 = new LoginRequest(골룸_참여자2_회원가입_요청.identifier(),
            골룸_참여자2_회원가입_요청.password());

    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final MemberRepository memberRepository;

    public GoalRoomReadIntegrationTest(final RoadmapRepository roadmapRepository,
                                       final GoalRoomRepository goalRoomRepository,
                                       final RoadmapCategoryRepository roadmapCategoryRepository,
                                       final RoadmapNodeRepository roadmapNodeRepository,
                                       final MemberRepository memberRepository) {
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.memberRepository = memberRepository;
    }

    @Test
    void 골룸_아이디로_골룸_정보를_조회한다() {
        // given
        final Member 크리에이터 = 크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인();
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final List<RoadmapContent> 로드맵_본문_리스트 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답).getValues();
        final GoalRoom 골룸 = 골룸을_저장한다(로드맵_본문_리스트, 크리에이터);

        // when
        final GoalRoomResponse 골룸_응답값 = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}", 골룸.getId())
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        final GoalRoomResponse 예상하는_골룸_응답값 = 예상하는_골룸_응답을_생성한다();
        assertThat(골룸_응답값)
                .isEqualTo(예상하는_골룸_응답값);
    }

    @Test
    void 골룸_아이디와_사용자_아이디로_골룸_정보를_조회한다() {
        // given
        final Member 크리에이터 = 크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인();
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보, 여행_카테고리, "첫 번째 로드맵");
        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final List<RoadmapContent> 로드맵_본문_리스트 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답).getValues();
        final GoalRoom 골룸 = 골룸을_저장한다(로드맵_본문_리스트, 크리에이터);

        // when
        final GoalRoomCertifiedResponse 골룸_응답값 = given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}", 골룸.getId())
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        final GoalRoomCertifiedResponse 예상하는_골룸_응답값 = 로그인후_예상하는_골룸_응답을_생성한다();
        assertThat(골룸_응답값)
                .isEqualTo(예상하는_골룸_응답값);
    }

    @Test
    void 골룸을_최신순으로_조회한다() throws JsonProcessingException {
        // given
        final GoalRoomCreateRequest 골룸_생성_요청 = 로드맵을_생성하고_그에_따른_골룸을_생성할_요청을_만든다();
        final String 골룸1_리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(골룸_참여자1_회원가입_요청, 골룸_참여자1_로그인_요청);
        final String 골룸2_리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(골룸_참여자2_회원가입_요청, 골룸_참여자2_로그인_요청);

        final Long 골룸1_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 골룸1_리더_액세스_토큰);
        final GoalRoom 골룸1 = goalRoomRepository.findById(골룸1_아이디).get();
        final ExtractableResponse<Response> 골룸1_단일_조회_응답 = 골룸_단일_조회_요청(골룸1_리더_액세스_토큰, 골룸1_아이디);
        final GoalRoomResponse 골룸1_단일_조회_응답값 = jsonToClass(골룸1_단일_조회_응답.asString(), new TypeReference<>() {
        });

        final Long 골룸2_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 골룸2_리더_액세스_토큰);
        final GoalRoom 골룸2 = goalRoomRepository.findById(골룸2_아이디).get();
        final ExtractableResponse<Response> 골룸2_단일_조회_응답 = 골룸_단일_조회_요청(골룸2_리더_액세스_토큰, 골룸2_아이디);
        final GoalRoomResponse 골룸2_단일_조회_응답값 = jsonToClass(골룸2_단일_조회_응답.asString(), new TypeReference<>() {
        });

        final Member 골룸1에_참여한_사용자 = memberRepository.findByIdentifier(new Identifier("identifier2")).get();
        final Member 골룸2에_참여한_사용자 = memberRepository.findByIdentifier(new Identifier("identifier3")).get();

        final GoalRoomForListResponse 골룸1_예상_응답값 = new GoalRoomForListResponse(1L, 골룸1.getName().getValue(),
                골룸1_단일_조회_응답값.currentMemberCount(), 골룸1.getLimitedMemberCount().getValue(), 골룸1.getCreatedAt(),
                골룸1.getStartDate(), 골룸1.getEndDate(),
                new MemberResponse(골룸1에_참여한_사용자.getId(), 골룸1에_참여한_사용자.getNickname().getValue()));
        final GoalRoomForListResponse 골룸2_예상_응답값 = new GoalRoomForListResponse(2L, 골룸2.getName().getValue(),
                골룸2_단일_조회_응답값.currentMemberCount(), 골룸2.getLimitedMemberCount().getValue(), 골룸2.getCreatedAt(),
                골룸2.getStartDate(), 골룸2.getEndDate(),
                new MemberResponse(골룸2에_참여한_사용자.getId(), 골룸2에_참여한_사용자.getNickname().getValue()));
        final PageResponse<GoalRoomForListResponse> 최신순_골룸_목록_조회_예상_응답값 = new PageResponse<>(1, 1,
                List.of(골룸2_예상_응답값, 골룸1_예상_응답값));

        // when
        final ExtractableResponse<Response> 최신순_골룸_목록_조회_응답 = 골룸_목록을_조회한다(1, 10, GoalRoomFilterTypeDto.LATEST.name());

        // then
        final PageResponse<GoalRoomForListResponse> 최신순_골룸_목록_조회_응답값 = jsonToClass(최신순_골룸_목록_조회_응답.asString(),
                new TypeReference<>() {
                });

        assertThat(최신순_골룸_목록_조회_응답값).isEqualTo(최신순_골룸_목록_조회_예상_응답값);
    }

    @Test
    void 골룸을_참가율_순으로_조회한다() throws JsonProcessingException {
        // given
        final GoalRoomCreateRequest 골룸_생성_요청 = 로드맵을_생성하고_그에_따른_골룸을_생성할_요청을_만든다();
        final String 골룸1_리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(골룸_참여자1_회원가입_요청, 골룸_참여자1_로그인_요청);
        final String 골룸2_리더_액세스_토큰 = 회원을_생성하고_로그인을_한다(골룸_참여자2_회원가입_요청, 골룸_참여자2_로그인_요청);

        final Long 골룸1_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 골룸1_리더_액세스_토큰);
        골룸_참가_요청(골룸1_아이디, 골룸2_리더_액세스_토큰);
        final GoalRoom 골룸1 = goalRoomRepository.findById(골룸1_아이디).get();
        final ExtractableResponse<Response> 골룸1_단일_조회_응답 = 골룸_단일_조회_요청(골룸1_리더_액세스_토큰, 골룸1_아이디);
        final GoalRoomResponse 골룸1_단일_조회_응답값 = jsonToClass(골룸1_단일_조회_응답.asString(), new TypeReference<>() {
        });

        final Long 골룸2_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 골룸2_리더_액세스_토큰);
        final GoalRoom 골룸2 = goalRoomRepository.findById(골룸2_아이디).get();
        final ExtractableResponse<Response> 골룸2_단일_조회_응답 = 골룸_단일_조회_요청(골룸2_리더_액세스_토큰, 골룸2_아이디);
        final GoalRoomResponse 골룸2_단일_조회_응답값 = jsonToClass(골룸2_단일_조회_응답.asString(), new TypeReference<>() {
        });

        final Member 골룸1에_참여한_사용자 = memberRepository.findByIdentifier(new Identifier("identifier2")).get();
        final Member 골룸2에_참여한_사용자 = memberRepository.findByIdentifier(new Identifier("identifier3")).get();

        final GoalRoomForListResponse 골룸1_예상_응답값 = new GoalRoomForListResponse(1L, 골룸1.getName().getValue(),
                골룸1_단일_조회_응답값.currentMemberCount(), 골룸1.getLimitedMemberCount().getValue(), 골룸1.getCreatedAt(),
                골룸1.getStartDate(), 골룸1.getEndDate(),
                new MemberResponse(골룸1에_참여한_사용자.getId(), 골룸1에_참여한_사용자.getNickname().getValue()));
        final GoalRoomForListResponse 골룸2_예상_응답값 = new GoalRoomForListResponse(2L, 골룸2.getName().getValue(),
                골룸2_단일_조회_응답값.currentMemberCount(), 골룸2.getLimitedMemberCount().getValue(), 골룸2.getCreatedAt(),
                골룸2.getStartDate(), 골룸2.getEndDate(),
                new MemberResponse(골룸2에_참여한_사용자.getId(), 골룸2에_참여한_사용자.getNickname().getValue()));
        final PageResponse<GoalRoomForListResponse> 참가율순_골룸_목록_조회_예상_응답값 = new PageResponse<>(1, 1,
                List.of(골룸1_예상_응답값, 골룸2_예상_응답값));

        // when
        final ExtractableResponse<Response> 참가율순_골룸_목록_조회_응답 = 골룸_목록을_조회한다(1, 10,
                GoalRoomFilterTypeDto.PARTICIPATION_RATE.name());

        // then
        final PageResponse<GoalRoomForListResponse> 참가율순_골룸_목록_조회_응답값 = jsonToClass(참가율순_골룸_목록_조회_응답.asString(),
                new TypeReference<>() {
                });

        assertThat(참가율순_골룸_목록_조회_응답값).isEqualTo(참가율순_골룸_목록_조회_예상_응답값);
    }

    private Member 크리에이터를_저장한다() {
        final String 닉네임 = "코끼리";
        final String 전화번호 = "010-1234-5678";
        final LocalDate 생년월일 = LocalDate.of(2023, Month.JULY, 12);
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(IDENTIFIER, PASSWORD, 닉네임, 전화번호, GenderType.MALE, 생년월일);

        final String 저장된_크리에이터_아이디 = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청)
                .post("/api/members/join")
                .then()
                .log().all()
                .extract()
                .response()
                .getHeader(LOCATION)
                .replace("/api/members/", "");
        return new Member(Long.valueOf(저장된_크리에이터_아이디), new Identifier(IDENTIFIER),
                new EncryptedPassword(new Password(PASSWORD)), new Nickname(닉네임),
                new MemberProfile(Gender.MALE, 생년월일, 전화번호));
    }

    private String 로그인() {
        final LoginRequest 로그인_요청 = new LoginRequest(IDENTIFIER, PASSWORD);

        final AuthenticationResponse 토큰_응답 = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(로그인_요청)
                .post(API_PREFIX + "/auth/login")
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        return String.format(BEARER_TOKEN_FORMAT, 토큰_응답.accessToken());
    }

    private Long 제목별로_로드맵을_생성한다(final String 로그인_토큰_정보, final RoadmapCategory 로드맵_카테고리, final String 로드맵_제목) {
        final RoadmapSaveRequest 로드맵_저장_요청 = new RoadmapSaveRequest(로드맵_카테고리.getId(), 로드맵_제목, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(
                new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용"),
                new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용")
        ));

        final String 생성된_로드맵_아이디 = given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .body(로드맵_저장_요청)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/api/roadmaps")
                .then()
                .log().all()
                .extract()
                .response()
                .getHeader(LOCATION)
                .replace("/api/roadmaps/", "");

        return Long.valueOf(생성된_로드맵_아이디);
    }

    private RoadmapResponse 로드맵을_조회한다(final Long 로드맵_아이디) {
        return given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/roadmaps/{roadmapId}", 로드맵_아이디)
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });
    }

    private RoadmapContents 로드맵_응답으로부터_로드맵_본문을_생성한다(final Member 크리에이터, final RoadmapCategory 카테고리,
                                                    final RoadmapResponse 로드맵_응답) {
        final Roadmap 로드맵 = new Roadmap(로드맵_응답.roadmapTitle(), 로드맵_응답.introduction(),
                로드맵_응답.recommendedRoadmapPeriod(), RoadmapDifficulty.valueOf(로드맵_응답.difficulty()), 크리에이터, 카테고리);
        final RoadmapContentResponse 로드맵_본문_응답 = 로드맵_응답.content();
        final RoadmapContent 로드맵_본문 = new RoadmapContent(로드맵_본문_응답.content());
        final List<RoadmapNodeResponse> 로드맵_본문_노드_응답 = 로드맵_본문_응답.nodes();
        final List<RoadmapNode> 로드맵_노드_리스트 = 로드맵_본문_노드_응답.stream()
                .map(response -> new RoadmapNode(response.title(), response.description()))
                .toList();

        로드맵_본문.addNodes(new RoadmapNodes(로드맵_노드_리스트));
        로드맵.addContent(로드맵_본문);

        // TODO 추후 골룸 생성 API가 들어오면 제거될 로직
        final Roadmap 저장된_로드맵 = roadmapRepository.save(로드맵);
        return 저장된_로드맵.getContents();
    }

    private GoalRoom 골룸을_저장한다(final List<RoadmapContent> 로드맵_본문_리스트, final Member 크리에이터) {
        final RoadmapContent 로드맵_본문 = 로드맵_본문_리스트.get(0);
        final GoalRoom 골룸 = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10),
                로드맵_본문, 크리에이터);
        final List<RoadmapNode> 로드맵_노드_리스트 = 로드맵_본문.getNodes().getValues();

        final RoadmapNode 첫번째_로드맵_노드 = 로드맵_노드_리스트.get(0);
        final GoalRoomRoadmapNode 첫번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(오늘, 십일_후),
                10, 첫번째_로드맵_노드);

        final RoadmapNode 두번째_로드맵_노드 = 로드맵_노드_리스트.get(1);
        final GoalRoomRoadmapNode 두번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(이십일_후, 삼십일_후),
                2, 두번째_로드맵_노드);

        final GoalRoomRoadmapNodes 골룸_노드들 = new GoalRoomRoadmapNodes(List.of(첫번째_골룸_노드, 두번째_골룸_노드));
        골룸.addAllGoalRoomRoadmapNodes(골룸_노드들);
        return goalRoomRepository.save(골룸);
    }

    private GoalRoomResponse 예상하는_골룸_응답을_생성한다() {
        final List<GoalRoomNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomNodeResponse("로드맵 1주차", 오늘, 십일_후, 10),
                new GoalRoomNodeResponse("로드맵 2주차", 이십일_후, 삼십일_후, 2));
        return new GoalRoomResponse("골룸", 1, 10, goalRoomNodeResponses, 31);
    }

    private GoalRoomCertifiedResponse 로그인후_예상하는_골룸_응답을_생성한다() {
        final List<GoalRoomNodeResponse> goalRoomNodeResponses = List.of(
                new GoalRoomNodeResponse("로드맵 1주차", 오늘, 십일_후, 10),
                new GoalRoomNodeResponse("로드맵 2주차", 이십일_후, 삼십일_후, 2));
        return new GoalRoomCertifiedResponse("골룸", 1, 10, goalRoomNodeResponses, 31, true);
    }

    private GoalRoomCreateRequest 로드맵을_생성하고_그에_따른_골룸을_생성할_요청을_만든다() {
        final String 액세스_토큰 = 회원을_생성하고_로그인을_한다(회원가입_요청, 로그인_요청);
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

        return 골룸_생성_요청;
    }

    private String 회원을_생성하고_로그인을_한다(final MemberJoinRequest memberRequest, final LoginRequest loginRequest) {
        회원가입(memberRequest);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(loginRequest);
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

    private Long 로드맵을_생성하고_id를_알아낸다(final String 액세스_토큰, final RoadmapSaveRequest 로드맵_생성_요청) {
        final ExtractableResponse<Response> 로드맵_응답 = 로드맵_생성(로드맵_생성_요청, 액세스_토큰);
        final String Location_헤더 = 로드맵_응답.response().header("Location");
        final Long 로드맵_id = Long.parseLong(Location_헤더.substring(14));
        return 로드맵_id;
    }

    private RoadmapNode 로드맵_노드() {
        return roadmapNodeRepository.findAll().get(0);
    }

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String 카테고리_이름) {
        final RoadmapCategory 로드맵_카테고리 = new RoadmapCategory(카테고리_이름);
        return roadmapCategoryRepository.save(로드맵_카테고리);
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

    private ExtractableResponse<Response> 골룸_참가_요청(final Long 골룸_아이디, final String 팔로워_액세스_토큰) {
        return given()
                .log().all()
                .header(AUTHORIZATION, 팔로워_액세스_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 골룸_단일_조회_요청(final String 로그인_토큰_정보, final Long 골룸_아이디) {
        return given()
                .header(AUTHORIZATION, 로그인_토큰_정보)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}", 골룸_아이디)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 골룸_목록을_조회한다(final int page, final int size, final String filterCond) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("page", page)
                .param("size", size)
                .param("filterCond", filterCond)
                .when()
                .get(API_PREFIX + "/goal-rooms")
                .then().log().all()
                .extract();
    }
}
