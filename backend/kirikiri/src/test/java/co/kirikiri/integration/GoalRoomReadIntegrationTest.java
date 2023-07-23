package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.LimitedMemberCount;
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
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapNodeRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
class GoalRoomReadIntegrationTest extends IntegrationTest {

    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final MemberRepository memberRepository;

    public GoalRoomReadIntegrationTest(final GoalRoomRepository goalRoomRepository,
                                       final RoadmapRepository roadmapRepository,
                                       final RoadmapCategoryRepository roadmapCategoryRepository,
                                       final RoadmapContentRepository roadmapContentRepository,
                                       final RoadmapNodeRepository roadmapNodeRepository,
                                       final MemberRepository memberRepository) {
        this.goalRoomRepository = goalRoomRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.roadmapContentRepository = roadmapContentRepository;
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.memberRepository = memberRepository;
    }

    @Test
    void 사용자가_참여한_골룸_목록을_조회한다() throws UnsupportedEncodingException, JsonProcessingException {
        final Member member = 사용자를_생성한다("황시진", "010-1234-5678", "identifier1", "password1!");
        final String 토큰 = 로그인을_한다("identifier1", "password1!");

        final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("운동");
        final RoadmapNodeSaveRequest 노드1 = 로드맵_노드_요청값을_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNodeSaveRequest 노드2 = 로드맵_노드_요청값을_생성한다("로드맵 2주차", "로드맵 2주차 내용");
        final Long 로드맵_아이디 = 로드맵을_생성한다(토큰, 카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(노드1, 노드2));
        final RoadmapContent 로드맵_본문 = 로드맵으로부터_본문을_가져온다(로드맵_아이디);
        final List<RoadmapNode> 로드맵_노드들 = 로드맵_본문으로부터_노드들을_가져온다(로드맵_본문);

        // TODO: 골룸 생성 API 추가 시 수정
        final GoalRoomRoadmapNode 골룸_로드맵_노드1 = 골룸_로드맵_노드를_생성한다(LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 8), 로드맵_노드들.get(0));
        final GoalRoomRoadmapNode 골룸_로드맵_노드2 = 골룸_로드맵_노드를_생성한다(LocalDate.of(2023, 7, 9),
                LocalDate.of(2023, 7, 16), 로드맵_노드들.get(1));
        final GoalRoom 골룸1 = 골룸을_생성한다("goalroom1", 6, GoalRoomStatus.RECRUITING, 로드맵_본문,
                new GoalRoomRoadmapNodes(List.of(골룸_로드맵_노드1, 골룸_로드맵_노드2)), member);

        final GoalRoomRoadmapNode 골룸_로드맵_노드3 = 골룸_로드맵_노드를_생성한다(LocalDate.of(2023, 6, 30),
                LocalDate.of(2023, 7, 8), 로드맵_노드들.get(0));
        final GoalRoomRoadmapNode 골룸_로드맵_노드4 = 골룸_로드맵_노드를_생성한다(LocalDate.of(2023, 7, 9),
                LocalDate.of(2023, 7, 30), 로드맵_노드들.get(1));
        final GoalRoom 골룸2 = 골룸을_생성한다("goalroom2", 20, GoalRoomStatus.RUNNING, 로드맵_본문,
                new GoalRoomRoadmapNodes(List.of(골룸_로드맵_노드3, 골룸_로드맵_노드4)), member);

        final GoalRoomForListResponse 골룸1_예상_응답값 = new GoalRoomForListResponse(1L, 골룸1.getName(),
                골룸1.getCurrentMemberCount(), 골룸1.getLimitedMemberCount(), 골룸1.getCreatedAt(),
                골룸1.getGoalRoomStartDate(), 골룸1.getGoalRoomEndDate(),
                new MemberResponse(member.getId(), member.getNickname().getValue()), 골룸1.getStatus().name());
        final GoalRoomForListResponse 골룸2_예상_응답값 = new GoalRoomForListResponse(2L, 골룸2.getName(),
                골룸2.getCurrentMemberCount(), 골룸2.getLimitedMemberCount(), 골룸2.getCreatedAt(),
                골룸2.getGoalRoomStartDate(), 골룸2.getGoalRoomEndDate(),
                new MemberResponse(member.getId(), member.getNickname().getValue()), 골룸2.getStatus().name());

        final PageResponse<GoalRoomForListResponse> 사용자_골룸_목록_조회_예상_응답값 = new PageResponse<>(1, 1,
                List.of(골룸1_예상_응답값, 골룸2_예상_응답값));

        // when
        final ExtractableResponse<Response> 사용자_골룸_목록_조회_응답 = 사용자_골룸_목록을_조회한다(토큰, member.getId(), 1, 10);

        // then
        final PageResponse<GoalRoomForListResponse> 사용자_골룸_목록_조회_응답값 = jsonToClass(사용자_골룸_목록_조회_응답.asString(),
                new TypeReference<>() {
                });

        assertThat(사용자_골룸_목록_조회_응답값).usingRecursiveComparison()
                .ignoringFields("data.createdAt")
                .isEqualTo(사용자_골룸_목록_조회_예상_응답값);
    }

    @Test
    void 아무런_골룸에_참여하지_않은_사용자의_골룸_목록을_조회하면_빈_값을_반환한다() throws UnsupportedEncodingException, JsonProcessingException {
        //given
        final Member member = 사용자를_생성한다("황시진", "010-1234-5678", "identifier1", "password1!");
        final String 토큰 = 로그인을_한다("identifier1", "password1!");

        //when
        final ExtractableResponse<Response> 사용자_골룸_목록_조회_응답 = 사용자_골룸_목록을_조회한다(토큰, member.getId(), 1, 10);

        //then
        final PageResponse<GoalRoomForListResponse> 사용자_골룸_목록_조회_응답값 = jsonToClass(사용자_골룸_목록_조회_응답.asString(),
                new TypeReference<>() {
                });

        assertThat(사용자_골룸_목록_조회_응답값.data()).isEmpty();
    }

    private void 회원가입을_한다(final String 아이디, final String 비밀번호, final String 닉네임, final String 전화번호,
                          final GenderType 성별, final LocalDate 생년월일) {
        final MemberJoinRequest 회원가입_요청값 = new MemberJoinRequest(아이디, 비밀번호, 닉네임, 전화번호, 성별, 생년월일);
        회원가입_요청(회원가입_요청값);
    }

    private ExtractableResponse<Response> 회원가입_요청(final MemberJoinRequest 회원가입_요청값) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청값)
                .post(API_PREFIX + "/members/join")
                .then().log().all()
                .extract();
    }

    private String 로그인을_한다(final String 아이디, final String 비밀번호)
            throws UnsupportedEncodingException, JsonProcessingException {
        final LoginRequest 로그인_요청값 = new LoginRequest(아이디, 비밀번호);
        final ExtractableResponse<Response> 로그인_응답값 = 로그인_요청(로그인_요청값);
        return access_token을_받는다(로그인_응답값);
    }

    private ExtractableResponse<Response> 로그인_요청(final LoginRequest 로그인_요청값) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(로그인_요청값)
                .post(API_PREFIX + "/auth/login")
                .then().log().all()
                .extract();
    }

    private String access_token을_받는다(final ExtractableResponse<Response> 로그인_응답)
            throws UnsupportedEncodingException, JsonProcessingException {
        final AuthenticationResponse 토큰_응답값 = jsonToClass(로그인_응답.body().asString(), new TypeReference<>() {
        });
        return 토큰_응답값.accessToken();
    }

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String 카테고리명) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(카테고리명);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private RoadmapNodeSaveRequest 로드맵_노드_요청값을_생성한다(final String 노드_제목, final String 노드_내용) {
        return new RoadmapNodeSaveRequest(노드_제목, 노드_내용);
    }

    private Long 로드맵을_생성한다(final String 토큰, final Long 카테고리_아이디, final String 로드맵_제목, final String 로드맵_소개글,
                           final String 로드맵_본문,
                           final RoadmapDifficultyType 난이도, final int 추천_소요_기간,
                           final List<RoadmapNodeSaveRequest> 로드맵_노드들) {
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리_아이디, 로드맵_제목, 로드맵_소개글, 로드맵_본문,
                난이도, 추천_소요_기간, 로드맵_노드들);
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 토큰);
        return 아이디를_반환한다(로드맵_생성_응답값);
    }

    private RoadmapContent 로드맵으로부터_본문을_가져온다(final Long 로드맵_아이디) {
        final Roadmap 로드맵 = roadmapRepository.findById(로드맵_아이디).get();
        return roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(로드맵).get();
    }

    private List<RoadmapNode> 로드맵_본문으로부터_노드들을_가져온다(final RoadmapContent 로드맵_본문) {
        return roadmapNodeRepository.findAllByRoadmapContent(로드맵_본문);
    }

    private ExtractableResponse<Response> 로드맵_생성_요청(final RoadmapSaveRequest 로드맵_생성_요청값, final String accessToken) {
        return given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(로드맵_생성_요청값).log().all()
                .post(API_PREFIX + "/roadmaps")
                .then().log().all()
                .extract();
    }

    private Long 아이디를_반환한다(final ExtractableResponse<Response> 응답) {
        return Long.parseLong(응답.header(HttpHeaders.LOCATION).split("/")[3]);
    }

    private GoalRoomRoadmapNode 골룸_로드맵_노드를_생성한다(final LocalDate startDate, final LocalDate endDate,
                                                final RoadmapNode roadmapNode) {
        return new GoalRoomRoadmapNode(startDate, endDate, roadmapNode);
    }

    private GoalRoom 골룸을_생성한다(final String name, final Integer limitedMemberCount,
                              final GoalRoomStatus status, final RoadmapContent roadmapContent,
                              final GoalRoomRoadmapNodes goalRoomRoadmapNodes, final Member creator) {
        final GoalRoom goalRoom = new GoalRoom(name, new LimitedMemberCount(limitedMemberCount), roadmapContent,
                new GoalRoomPendingMember(creator, GoalRoomRole.LEADER));
        goalRoom.addRoadmapNodesAll(goalRoomRoadmapNodes);
        goalRoom.updateStatus(status);
        goalRoomRepository.save(goalRoom);
        return goalRoom;
    }

    private Member 사용자를_생성한다(final String nickname, final String phoneNumber, final String identifier,
                             final String password) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
                new Nickname(nickname), phoneNumber);
        final Member creator = new Member(new Identifier(identifier),
                new EncryptedPassword(new Password(password)), memberProfile);
        return memberRepository.save(creator);
    }

    private ExtractableResponse<Response> 사용자_골룸_목록을_조회한다(final String accessToken, final Long memberId, final int page,
                                                          final int size) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .param("member", memberId)
                .param("page", page)
                .param("size", size)
                .when()
                .get(API_PREFIX + "/goal-rooms")
                .then().log().all()
                .extract();
    }
}
