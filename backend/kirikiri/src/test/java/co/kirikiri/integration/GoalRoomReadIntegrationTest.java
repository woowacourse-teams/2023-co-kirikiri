package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.ImageDirType;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberImage;
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
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapNodeRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.FilePathGenerator;
import co.kirikiri.service.GoalRoomCreateService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.CheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberNameAndImageResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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
    private static final int 정상적인_골룸_노드_인증_횟수 = (int) ChronoUnit.DAYS.between(오늘, 십일_후);

    private final String storageLocation;
    private final String serverPathPrefix;
    private final FilePathGenerator filePathGenerator;
    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final GoalRoomCreateService goalRoomCreateService;

    public GoalRoomReadIntegrationTest(@Value("${file.upload-dir}") final String storageLocation,
                                       @Value("${file.server-path}") final String serverPathPrefix,
                                       final FilePathGenerator filePathGenerator,
                                       final RoadmapRepository roadmapRepository,
                                       final GoalRoomRepository goalRoomRepository,
                                       final RoadmapCategoryRepository roadmapCategoryRepository,
                                       final RoadmapNodeRepository roadmapNodeRepository,
                                       final GoalRoomCreateService goalRoomCreateService) {
        this.storageLocation = storageLocation;
        this.serverPathPrefix = serverPathPrefix;
        this.filePathGenerator = filePathGenerator;
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.goalRoomCreateService = goalRoomCreateService;
    }

    @Test
    void 골룸_아이디로_골룸_정보를_조회한다() {
        // given
        final Member 크리에이터 = 크리에이터를_저장한다();
        final String 로그인_토큰_정보 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));
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
        final String 로그인_토큰_정보 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));
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
    void 골룸의_인증피드를_전체_조회한다() throws IOException {
        // given
        사용자를_저장한다("identifier1", "password1!", "name1", "010-1111-2222");
        사용자를_저장한다("identifier2", "password2!", "name2", "010-1111-3333");
        final String 로그인_토큰_정보1 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));
        final String 로그인_토큰_정보2 = 로그인(new LoginRequest("identifier2", "password2!"));
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보1, 여행_카테고리, "첫 번째 로드맵");
        final List<RoadmapNode> 로드맵_노드들 = 로드맵_노드들();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드들.get(0).getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_노드들.get(1).getId(), 정상적인_골룸_노드_인증_횟수, 십일_후.plusDays(1),
                        십일_후.plusDays(20)));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_id = 골룸을_생성하고_id를_알아낸다(골룸_생성_요청, 로그인_토큰_정보1);
        goalRoomCreateService.join("identifier2", 골룸_id);
        goalRoomCreateService.startGoalRooms();

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청1 = new CheckFeedRequest(가짜_이미지_객체, "image description1");
        final CheckFeedRequest 인증_피드_등록_요청2 = new CheckFeedRequest(가짜_이미지_객체, "image description2");

        인증_피드_등록_요청(로그인_토큰_정보1, 골룸_id, 인증_피드_등록_요청1);
        인증_피드_등록_요청(로그인_토큰_정보2, 골룸_id, 인증_피드_등록_요청2);

        //when
        final ExtractableResponse<Response> 인증_피드_전체_조회_요청에_대한_응답 = 인증_피드_전체_조회_요청(로그인_토큰_정보2, 골룸_id);

        // then
        final List<GoalRoomCheckFeedResponse> 인증_피드_전체_조회_응답_바디 = jsonToClass(인증_피드_전체_조회_요청에_대한_응답.asString(),
                new TypeReference<>() {
                });

        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse1 = new GoalRoomCheckFeedResponse(
                new MemberNameAndImageResponse(1L, "name1", "serverFilePath"),
                new CheckFeedResponse(1L, serverPathPrefix + filePathGenerator.makeFilePath(1L, ImageDirType.CHECK_FEED)
                        + "originalFileName.jpeg", "image description1",
                        LocalDateTime.now()));
        final GoalRoomCheckFeedResponse goalRoomCheckFeedResponse2 = new GoalRoomCheckFeedResponse(
                new MemberNameAndImageResponse(2L, "name2", "serverFilePath"),
                new CheckFeedResponse(2L, serverPathPrefix + filePathGenerator.makeFilePath(1L, ImageDirType.CHECK_FEED)
                        + "originalFileName.jpeg", "image description2",
                        LocalDateTime.now()));

        final List<GoalRoomCheckFeedResponse> 예상하는_응답값 = List.of(goalRoomCheckFeedResponse2,
                goalRoomCheckFeedResponse1);

        assertThat(인증_피드_전체_조회_응답_바디).usingRecursiveComparison()
                .ignoringFields("member.imageUrl", "checkFeed.imageUrl", "checkFeed.createdAt")
                .isEqualTo(예상하는_응답값);
    }

    @Test
    void 골룸의_인증피드를_전체_조회시_존재하지_않는_골룸인_경우_예외가_발생한다() throws IOException {
        // given
        사용자를_저장한다("identifier1", "password1!", "name1", "010-1111-2222");
        사용자를_저장한다("identifier2", "password2!", "name2", "010-1111-3333");
        final String 로그인_토큰_정보1 = 로그인(new LoginRequest("identifier1", "password1!"));
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        제목별로_로드맵을_생성한다(로그인_토큰_정보1, 여행_카테고리, "첫 번째 로드맵");

        //when
        final Long 존재하지_않는_골룸_아이디 = 1L;
        final ExtractableResponse<Response> 인증_피드_전체_조회_요청에_대한_응답 = 인증_피드_전체_조회_요청(로그인_토큰_정보1, 존재하지_않는_골룸_아이디);

        // then
        final ErrorResponse 인증_피드_전체_조회_응답_바디 = jsonToClass(인증_피드_전체_조회_요청에_대한_응답.asString(), new TypeReference<>() {
        });
        assertThat(인증_피드_전체_조회_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(인증_피드_전체_조회_응답_바디).isEqualTo(new ErrorResponse("존재하지 않는 골룸입니다. goalRoomId = 1"));
    }

    @Test
    void 골룸의_인증피드를_전체_조회시_골룸에_참여하지_않은_사용자이면_예외가_발생한다() throws IOException {
        // given
        사용자를_저장한다("identifier1", "password1!", "name1", "010-1111-2222");
        사용자를_저장한다("identifier2", "password2!", "name2", "010-1111-3333");
        final String 로그인_토큰_정보1 = 로그인(new LoginRequest(IDENTIFIER, PASSWORD));
        final String 로그인_토큰_정보2 = 로그인(new LoginRequest("identifier2", "password2!"));
        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final Long 로드맵_아이디 = 제목별로_로드맵을_생성한다(로그인_토큰_정보1, 여행_카테고리, "첫 번째 로드맵");
        final List<RoadmapNode> 로드맵_노드들 = 로드맵_노드들();

        final GoalRoomTodoRequest 골룸_투두_요청 = new GoalRoomTodoRequest(정상적인_골룸_투두_컨텐츠, 오늘, 십일_후);
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_노드들.get(0).getId(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후),
                new GoalRoomRoadmapNodeRequest(로드맵_노드들.get(1).getId(), 정상적인_골룸_노드_인증_횟수, 십일_후.plusDays(1),
                        십일_후.plusDays(20)));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_아이디, 정상적인_골룸_이름, 정상적인_골룸_제한_인원, 골룸_투두_요청,
                골룸_노드_별_기간_요청);
        final Long 골룸_id = 골룸을_생성하고_id를_알아낸다(골룸_생성_요청, 로그인_토큰_정보1);
        goalRoomCreateService.startGoalRooms();

        final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile("image", "originalFileName.jpeg",
                "image/jpeg", "tempImage".getBytes());
        final CheckFeedRequest 인증_피드_등록_요청1 = new CheckFeedRequest(가짜_이미지_객체, "image description1");

        인증_피드_등록_요청(로그인_토큰_정보1, 골룸_id, 인증_피드_등록_요청1);

        //when
        final ExtractableResponse<Response> 인증_피드_전체_조회_요청에_대한_응답 = 인증_피드_전체_조회_요청(로그인_토큰_정보2, 골룸_id);

        // then
        final ErrorResponse 인증_피드_전체_조회_응답_바디 = jsonToClass(인증_피드_전체_조회_요청에_대한_응답.asString(), new TypeReference<>() {
        });
        assertThat(인증_피드_전체_조회_요청에_대한_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(인증_피드_전체_조회_응답_바디).isEqualTo(new ErrorResponse("골룸에 참여하지 않은 회원입니다."));
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
                new MemberImage("originalFileName", "serverFilePath", ImageContentType.JPEG),
                new MemberProfile(Gender.MALE, 생년월일, 전화번호));
    }

    private Member 사용자를_저장한다(final String 아이디, final String 비밀번호, final String 닉네임, final String 전화번호) {
        final LocalDate 생년월일 = LocalDate.of(2023, Month.JULY, 12);
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(아이디, 비밀번호, 닉네임, 전화번호, GenderType.MALE, 생년월일);

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

        return new Member(Long.valueOf(저장된_크리에이터_아이디), new Identifier(아이디),
                new EncryptedPassword(new Password(비밀번호)), new Nickname(닉네임),
                new MemberImage("originalFileName", "serverFilePath", ImageContentType.JPEG),
                new MemberProfile(Gender.MALE, 생년월일, 전화번호));
    }

    private String 로그인(final LoginRequest 로그인_요청) {
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
        final RoadmapSaveRequest 로드맵_저장_요청 = new RoadmapSaveRequest(
                로드맵_카테고리.getId(), 로드맵_제목, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(
                new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용"),
                new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용")),
                List.of(new RoadmapTagSaveRequest("태그")));

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

    private List<RoadmapNode> 로드맵_노드들() {
        return roadmapNodeRepository.findAll();
    }

    private Long 골룸을_생성하고_id를_알아낸다(final GoalRoomCreateRequest 골룸_생성_요청, final String 액세스_토큰) {
        final ExtractableResponse<Response> 골룸_응답 = 골룸_생성(골룸_생성_요청, 액세스_토큰);
        final String Location_헤더 = 골룸_응답.response().header("Location");
        final Long 골룸_id = Long.parseLong(Location_헤더.substring(16));
        return 골룸_id;
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

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String 카테고리_이름) {
        final RoadmapCategory 로드맵_카테고리 = new RoadmapCategory(카테고리_이름);
        return roadmapCategoryRepository.save(로드맵_카테고리);
    }

    private ExtractableResponse<Response> 인증_피드_등록_요청(final String 액세스_토큰, final Long 골룸_id,
                                                      final CheckFeedRequest 인증_피드_등록_요청) throws IOException {
        final MultipartFile 인증피드_가짜_이미지_객체 = 인증_피드_등록_요청.image();

        return given().log().all()
                .multiPart(인증피드_가짜_이미지_객체.getName(), 인증피드_가짜_이미지_객체.getOriginalFilename(),
                        인증피드_가짜_이미지_객체.getBytes(), 인증피드_가짜_이미지_객체.getContentType())
                .formParam("description", 인증_피드_등록_요청.description())
                .header(AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .when()
                .post(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_id)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 인증_피드_전체_조회_요청(final String 액세스_토큰, final Long 골룸_id) throws IOException {
        return given().log().all()
                .header(AUTHORIZATION, 액세스_토큰)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PREFIX + "/goal-rooms/{goalRoomId}/checkFeeds", 골룸_id)
                .then()
                .log().all()
                .extract();
    }
}
