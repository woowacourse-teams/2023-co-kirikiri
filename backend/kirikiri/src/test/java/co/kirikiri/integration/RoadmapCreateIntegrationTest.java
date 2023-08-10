package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
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
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapNodeRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class RoadmapCreateIntegrationTest extends IntegrationTest {

    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";
    private static final String NICKNAME = "nickname";

    private String 로그인_토큰;
    private RoadmapCategory 카테고리;

    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapCreateIntegrationTest(final RoadmapRepository roadmapRepository,
                                        final GoalRoomRepository goalRoomRepository,
                                        final RoadmapNodeRepository roadmapNodeRepository,
                                        final GoalRoomMemberRepository goalRoomMemberRepository,
                                        final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @BeforeEach
    void init() {
        회원가입(NICKNAME, IDENTIFIER);
        로그인_토큰 = 로그인(IDENTIFIER);
        카테고리 = 로드맵_카테고리를_저장한다("여행");
    }

    @Test
    void 정상적으로_로드맵을_생성한다() {
        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")),
                List.of(new RoadmapTagSaveRequest("태그1")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // expect
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.CREATED);
        final Long 로드맵_아이디 = 아이디를_반환한다(로드맵_생성_응답값);
        assertThat(로드맵_아이디).isEqualTo(1L);
    }

    @Test
    void 본문의_값이_없는_로드맵이_정상적으로_생성한다() {
        // given
        final String 로드맵_본문 = null;

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", 로드맵_본문,
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")),
                List.of(new RoadmapTagSaveRequest("태그1")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.CREATED);
        final Long 로드맵_아이디 = 아이디를_반환한다(로드맵_생성_응답값);
        assertThat(로드맵_아이디).isEqualTo(1L);
    }

    @Test
    void 로드맵_생성시_잘못된_빈값을_넘기면_실패한다() {
        // given
        final Long 카테고리_아이디 = null;
        final String 로드맵_제목 = null;
        final String 로드맵_소개글 = null;
        final RoadmapDifficultyType 로드맵_난이도 = null;
        final Integer 추천_소요_기간 = null;
        final String 로드맵_노드_제목 = null;
        final String 로드맵_노드_설명 = null;

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리_아이디, 로드맵_제목, 로드맵_소개글, "로드맵 본문",
                로드맵_난이도, 추천_소요_기간,
                List.of(new RoadmapNodeSaveRequest(로드맵_노드_제목, 로드맵_노드_설명)),
                List.of(new RoadmapTagSaveRequest("태그1")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        final List<ErrorResponse> 에러_메시지들 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메시지들)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(
                        new ErrorResponse("카테고리를 입력해주세요."),
                        new ErrorResponse("로드맵의 제목을 입력해주세요."),
                        new ErrorResponse("로드맵의 소개글을 입력해주세요."),
                        new ErrorResponse("난이도를 입력해주세요."),
                        new ErrorResponse("추천 소요 기간을 입력해주세요."),
                        new ErrorResponse("로드맵 노드의 제목을 입력해주세요."),
                        new ErrorResponse("로드맵 노드의 설명을 입력해주세요.")));
    }

    @Test
    void 존재하지_않는_카테고리_아이디를_입력한_경우_실패한다() {
        // given
        final long 카테고리_아이디 = 2L;

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리_아이디, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")),
                List.of(new RoadmapTagSaveRequest("태그1")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.NOT_FOUND);
        assertThat(에러_메세지.message()).isEqualTo("존재하지 않는 카테고리입니다. categoryId = 2");

    }

    @Test
    void 제목의_길이가_40보다_크면_실패한다() {
        // given
        final String 로드맵_제목 = "a".repeat(41);

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), 로드맵_제목, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")),
                List.of(new RoadmapTagSaveRequest("태그")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("로드맵 제목의 길이는 최소 1글자, 최대 40글자입니다.");
    }

    @Test
    void 소개글의_길이가_150보다_크면_실패한다() {
        // given
        final String 로드맵_소개글 = "a".repeat(151);

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", 로드맵_소개글, "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")),
                List.of(new RoadmapTagSaveRequest("태그1")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("로드맵 소개글의 길이는 최소 1글자, 최대 150글자입니다.");
    }

    @Test
    void 본문의_길이가_2000보다_크면_실패한다() {
        // given
        final String 로드맵_본문 = "a".repeat(2001);

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", 로드맵_본문,
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")),
                List.of(new RoadmapTagSaveRequest("태그1")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("로드맵 본문의 길이는 최대 2000글자입니다.");
    }

    @Test
    void 추천_소요_기간이_0보다_작으면_실패한다() {
        // given
        final Integer 추천_소요_기간 = -1;

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 추천_소요_기간,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")),
                List.of(new RoadmapTagSaveRequest("태그1")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("로드맵 추천 소요 기간은 최소 0일, 최대 1000일입니다.");
    }

    @Test
    void 로드맵_노드를_입력하지_않으면_실패한다() {
        // given
        final List<RoadmapNodeSaveRequest> 로드맵_노드들 = null;

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30, 로드맵_노드들, List.of(new RoadmapTagSaveRequest("태그1")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        final List<ErrorResponse> 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.get(0).message()).isEqualTo("로드맵의 첫 번째 단계를 입력해주세요.");
    }

    @Test
    void 로드맵_노드의_제목의_길이가_40보다_크면_실패한다() {
        // given
        final String 로드맵_노드_제목 = "a".repeat(41);
        final List<RoadmapNodeSaveRequest> 로드맵_노드들 = List.of(
                new RoadmapNodeSaveRequest(로드맵_노드_제목, "로드맵 1주차 내용"));

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30, 로드맵_노드들, List.of(new RoadmapTagSaveRequest("태그1")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("로드맵 노드의 제목의 길이는 최소 1글자, 최대 40글자입니다.");
    }

    @Test
    void 로드맵_노드의_설명의_길이가_2000보다_크면_실패한다() {
        // given
        final String 로드맵_노드_설명 = "a".repeat(2001);
        final List<RoadmapNodeSaveRequest> 로드맵_노드들 = List.of(
                new RoadmapNodeSaveRequest("로드맵 노드 제목", 로드맵_노드_설명));
        로드맵_카테고리를_저장한다("여행");

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글",
                "로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30, 로드맵_노드들, List.of(new RoadmapTagSaveRequest("태그1")));
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("로드맵 노드의 설명의 길이는 최소 1글자, 최대 2000글자입니다.");
    }

    @Test
    void 로드맵_리뷰를_생성한다() {
        // given
        final Member 크리에이터 = 회원가입("크리에이터", "creator");
        final Member 리더 = 회원가입("리더", "leader");
        final Member 팔로워 = 회원가입("팔로워", "follower");

        final String 크리에이터_토큰_정보 = 로그인(크리에이터.getIdentifier().getValue());
        final String 팔로워_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapSaveRequest 로드맵_생성_요청값 = 로드맵_저장_요청을_생성한다();
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 크리에이터_토큰_정보);
        final Long 로드맵_아이디 = 아이디를_반환한다(로드맵_생성_응답값);

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Roadmap 저장된_로드맵 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답);
        final List<RoadmapContent> 로드맵_본문_리스트 = 저장된_로드맵.getContents().getValues();

        // TODO 임의로 완료된 골룸을 생성한다 (골룸 완료 API 추가 시 변경)
        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_본문_리스트, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 팔로워, 골룸);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);

        // then
        assertThat(리뷰_생성_요청_결과.statusCode())
                .isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 로드맵_리뷰_생성시_별점이_null이면_예외가_발생한다() {
        // given
        final Member 팔로워 = 회원가입("팔로워", "follower");
        final String 팔로워_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest(" ", null);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_토큰_정보, 1L, 로드맵_리뷰_생성_요청);

        // then
        final List<ErrorResponse> 예외_응답 = 리뷰_생성_요청_결과.as(new TypeRef<>() {
        });
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.get(0).message()).isEqualTo("별점을 입력해 주세요.");
    }

    @Test
    void 로드맵_리뷰_생성시_별점이_잘못된_값이면_예외가_발생한다() {
        // given
        final Member 크리에이터 = 회원가입("크리에이터", "creator");
        final Member 리더 = 회원가입("리더", "leader");
        final Member 팔로워 = 회원가입("팔로워", "follower");

        final String 크리에이터_토큰_정보 = 로그인(크리에이터.getIdentifier().getValue());
        final String 팔로워_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapSaveRequest 로드맵_생성_요청값 = 로드맵_저장_요청을_생성한다();
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 크리에이터_토큰_정보);
        final Long 로드맵_아이디 = 아이디를_반환한다(로드맵_생성_응답값);

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Roadmap 저장된_로드맵 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답);
        final List<RoadmapContent> 로드맵_본문_리스트 = 저장된_로드맵.getContents().getValues();

        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_본문_리스트, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 팔로워, 골룸);
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 2.4);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("별점은 0부터 5까지 0.5 단위로 설정할 수 있습니다.");
    }

    @Test
    void 로드맵_리뷰_생성시_내용이_1000자가_넘으면_예외가_발생한다() {
        // given
        final Member 크리에이터 = 회원가입("크리에이터", "creator");
        final Member 리더 = 회원가입("리더", "leader");
        final Member 팔로워 = 회원가입("팔로워", "follower");

        final String 크리에이터_토큰_정보 = 로그인(크리에이터.getIdentifier().getValue());
        final String 팔로워_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapSaveRequest 로드맵_생성_요청값 = 로드맵_저장_요청을_생성한다();
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 크리에이터_토큰_정보);
        final Long 로드맵_아이디 = 아이디를_반환한다(로드맵_생성_응답값);

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Roadmap 저장된_로드맵 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답);
        final List<RoadmapContent> 로드맵_본문_리스트 = 저장된_로드맵.getContents().getValues();
        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_본문_리스트, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 팔로워, 골룸);

        final String 엄청_긴_리뷰_내용 = "a".repeat(1001);
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest(엄청_긴_리뷰_내용, 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("리뷰는 최대 1000글자까지 입력할 수 있습니다.");
    }

    @Test
    void 로드맵_리뷰_생성시_존재하지_않은_로드맵이면_예외가_발생한다() {
        // given
        final Member 팔로워 = 회원가입("팔로워", "follower");
        final String 팔로워_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());
        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워_토큰_정보, 1L, 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(예외_응답.message()).isEqualTo("존재하지 않는 로드맵입니다. roadmapId = 1");
    }

    @Test
    void 로드맵_리뷰_생성시_완료한_골룸이_없다면_예외가_발생한다() {
        // given
        final Member 크리에이터 = 회원가입("크리에이터", "creator");
        final Member 리더 = 회원가입("리더", "leader");
        final Member 팔로워 = 회원가입("팔로워", "follower");

        final String 크리에이터_토큰_정보 = 로그인(크리에이터.getIdentifier().getValue());
        final String 팔로워2_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapSaveRequest 로드맵_생성_요청값 = 로드맵_저장_요청을_생성한다();
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 크리에이터_토큰_정보);
        final Long 로드맵_아이디 = 아이디를_반환한다(로드맵_생성_응답값);

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Roadmap 저장된_로드맵 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답);
        final List<RoadmapContent> 로드맵_본문_리스트 = 저장된_로드맵.getContents().getValues();
        final GoalRoom 골룸 = 진행중인_골룸을_생성한다(로드맵_본문_리스트, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 팔로워, 골룸);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(팔로워2_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("로드맵에 대해서 완료된 골룸이 존재하지 않습니다. roadmapId = " + 저장된_로드맵.getId() +
                " memberIdentifier = follower");
    }

    @Test
    void 로드맵_리뷰_생성시_로드맵_생성자가_리뷰를_달려고_하면_예외가_발생한다() {
        // given
        final Member 크리에이터 = 회원가입("크리에이터", "creator");
        final Member 리더 = 회원가입("리더", "leader");
        final Member 팔로워 = 회원가입("팔로워", "follower");

        final String 크리에이터_토큰_정보 = 로그인(크리에이터.getIdentifier().getValue());

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapSaveRequest 로드맵_생성_요청값 = 로드맵_저장_요청을_생성한다();
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 크리에이터_토큰_정보);
        final Long 로드맵_아이디 = 아이디를_반환한다(로드맵_생성_응답값);

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Roadmap 저장된_로드맵 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답);
        final List<RoadmapContent> 로드맵_본문_리스트 = 저장된_로드맵.getContents().getValues();
        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_본문_리스트, 리더);
        골룸에_대한_참여자_리스트를_생성한다(크리에이터, 팔로워, 골룸);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = 리뷰를_생성한다(크리에이터_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 리뷰_생성_요청_결과.as(ErrorResponse.class);
        assertThat(리뷰_생성_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("로드맵 생성자는 리뷰를 달 수 없습니다. roadmapId = " + 저장된_로드맵.getId() +
                " memberId = " + 크리에이터.getId());
    }

    @Test
    void 로드맵_리뷰_생성시_이미_리뷰를_단적이_있으면_예외가_발생한다() {
        // given
        final Member 크리에이터 = 회원가입("크리에이터", "creator");
        final Member 리더 = 회원가입("리더", "leader");
        final Member 팔로워 = 회원가입("팔로워", "follower");

        final String 크리에이터_토큰_정보 = 로그인(크리에이터.getIdentifier().getValue());
        final String 팔로워_토큰_정보 = 로그인(팔로워.getIdentifier().getValue());

        final RoadmapCategory 여행_카테고리 = 로드맵_카테고리를_저장한다("여행");
        final RoadmapSaveRequest 로드맵_생성_요청값 = 로드맵_저장_요청을_생성한다();
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 크리에이터_토큰_정보);
        final Long 로드맵_아이디 = 아이디를_반환한다(로드맵_생성_응답값);

        final RoadmapResponse 로드맵_응답 = 로드맵을_조회한다(로드맵_아이디);
        final Roadmap 저장된_로드맵 = 로드맵_응답으로부터_로드맵_본문을_생성한다(크리에이터, 여행_카테고리, 로드맵_응답);
        final List<RoadmapContent> 로드맵_본문_리스트 = 저장된_로드맵.getContents().getValues();
        final GoalRoom 골룸 = 완료한_골룸을_생성한다(로드맵_본문_리스트, 리더);
        골룸에_대한_참여자_리스트를_생성한다(리더, 팔로워, 골룸);

        final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청 = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);

        // when
        리뷰를_생성한다(팔로워_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);
        final ExtractableResponse<Response> 두번째_리뷰_생성_요청결과 = 리뷰를_생성한다(팔로워_토큰_정보, 저장된_로드맵.getId(), 로드맵_리뷰_생성_요청);

        // then
        final ErrorResponse 예외_응답 = 두번째_리뷰_생성_요청결과.as(ErrorResponse.class);
        assertThat(두번째_리뷰_생성_요청결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(예외_응답.message()).isEqualTo("이미 작성한 리뷰가 존재합니다. roadmapId = " + 저장된_로드맵.getId() +
                " memberId = " + 팔로워.getId());
    }

    @Test
    void 로드맵_태그_이름이_중복되면_예외가_발생한다() {
        // given
        final List<RoadmapTagSaveRequest> 태그_저장_요청 = List.of(new RoadmapTagSaveRequest("태그"),
                new RoadmapTagSaveRequest("태그"));

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 노드 제목", "로드맵 노드 설명")), 태그_저장_요청);
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("태그 이름은 중복될 수 없습니다.");
    }

    @Test
    void 로드맵_태그_개수가_5개_초과면_예외가_발생한다() {
        // given
        final List<RoadmapTagSaveRequest> 태그_저장_요청 = List.of(new RoadmapTagSaveRequest("태그1"),
                new RoadmapTagSaveRequest("태그2"), new RoadmapTagSaveRequest("태그3"),
                new RoadmapTagSaveRequest("태그4"), new RoadmapTagSaveRequest("태그5"),
                new RoadmapTagSaveRequest("태그6"));

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 노드 제목", "로드맵 노드 설명")), 태그_저장_요청);
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("태그의 개수는 최대 5개까지 가능합니다.");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    void 로드맵_태그_이름의_길이가_1자_미만_10자_초과면_예외가_발생한다(final int nameLength) {
        // given
        final String 태그_이름 = "a".repeat(nameLength);
        final List<RoadmapTagSaveRequest> 태그_저장_요청 = List.of(new RoadmapTagSaveRequest(태그_이름));

        // when
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 노드 제목", "로드맵 노드 설명")), 태그_저장_요청);
        final ExtractableResponse<Response> 로드맵_생성_응답값 = 로드맵_생성_요청(로드맵_생성_요청값, 로그인_토큰);

        // then
        final ErrorResponse 에러_메세지 = 로드맵_생성_응답값.as(new TypeRef<>() {
        });
        응답_상태_코드_검증(로드맵_생성_응답값, HttpStatus.BAD_REQUEST);
        assertThat(에러_메세지.message()).isEqualTo("태그 이름은 최소 1자부터 최대 10자까지 가능합니다.");
    }

    private Member 회원가입(final String 닉네임, final String 아이디) {
        final String 전화번호 = "010-1234-5678";
        final LocalDate 생년월일 = LocalDate.of(2023, Month.JULY, 12);
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(아이디, PASSWORD, 닉네임, 전화번호, GenderType.MALE, 생년월일);

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
                new EncryptedPassword(new Password(PASSWORD)),
                new Nickname(닉네임),
                null,
                new MemberProfile(Gender.MALE, 생년월일, 전화번호));
    }

    private String 로그인(final String 아이디) {
        final LoginRequest 로그인_요청 = new LoginRequest(아이디, PASSWORD);

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

    private ExtractableResponse<Response> 로드맵_생성_요청(final RoadmapSaveRequest 로드맵_생성_요청값,
                                                    final String accessToken) {
        return given().log().all()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(로드맵_생성_요청값).log().all()
                .post(API_PREFIX + "/roadmaps")
                .then().log().all()
                .extract();
    }

    private void 응답_상태_코드_검증(final ExtractableResponse<Response> 응답, final HttpStatus http_상태) {
        assertThat(응답.statusCode()).isEqualTo(http_상태.value());
    }

    private Long 아이디를_반환한다(final ExtractableResponse<Response> 응답) {
        return Long.parseLong(응답.header(HttpHeaders.LOCATION).split("/")[3]);
    }

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String 카테고리_이름) {
        final RoadmapCategory 로드맵_카테고리 = new RoadmapCategory(카테고리_이름);
        return roadmapCategoryRepository.save(로드맵_카테고리);
    }

    private RoadmapSaveRequest 로드맵_저장_요청을_생성한다() {
        final RoadmapSaveRequest 로드맵_생성_요청값 = new RoadmapSaveRequest(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용"),
                        new RoadmapNodeSaveRequest("로드맵 2주차", "로드맵 2주차 내용")),
                List.of(new RoadmapTagSaveRequest("태그")));
        return 로드맵_생성_요청값;
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

    private Roadmap 로드맵_응답으로부터_로드맵_본문을_생성한다(final Member 크리에이터, final RoadmapCategory 카테고리,
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

        // TODO 추후 골룸 생성 API가 들어오면 제거될 로직 (리뷰 생성 시에는 골룸이 완료되어 있어야 하기 때문에 골룸 완료 API가 있어야 제거 가능한 로직)
        return roadmapRepository.save(로드맵);
    }

    private GoalRoom 완료한_골룸을_생성한다(final List<RoadmapContent> 로드맵_본문_리스트, final Member 리더) {
        final RoadmapContent 로드맵_본문 = 로드맵_본문_리스트.get(0);
        final GoalRoom 골룸 = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10), 로드맵_본문, 리더);
        final List<RoadmapNode> 로드맵_노드_리스트 = 로드맵_본문.getNodes().getValues();

        final RoadmapNode 첫번째_로드맵_노드 = 로드맵_노드_리스트.get(0);
        final GoalRoomRoadmapNode 첫번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5)), 3, 첫번째_로드맵_노드);

        final RoadmapNode 두번째_로드맵_노드 = 로드맵_노드_리스트.get(1);
        final GoalRoomRoadmapNode 두번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(LocalDate.now().plusDays(6),
                        LocalDate.now().plusDays(10)), 2, 두번째_로드맵_노드);

        final GoalRoomRoadmapNodes 골룸_노드들 = new GoalRoomRoadmapNodes(List.of(첫번째_골룸_노드, 두번째_골룸_노드));
        골룸.addAllGoalRoomRoadmapNodes(골룸_노드들);
        골룸.complete();
        return goalRoomRepository.save(골룸);
    }

    private GoalRoom 진행중인_골룸을_생성한다(final List<RoadmapContent> 로드맵_본문_리스트, final Member 리더) {
        final RoadmapContent 로드맵_본문 = 로드맵_본문_리스트.get(0);
        final GoalRoom 골룸 = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10), 로드맵_본문, 리더);
        final List<RoadmapNode> 로드맵_노드_리스트 = 로드맵_본문.getNodes().getValues();

        final RoadmapNode 첫번째_로드맵_노드 = 로드맵_노드_리스트.get(0);
        final GoalRoomRoadmapNode 첫번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5)), 3, 첫번째_로드맵_노드);

        final RoadmapNode 두번째_로드맵_노드 = 로드맵_노드_리스트.get(1);
        final GoalRoomRoadmapNode 두번째_골룸_노드 = new GoalRoomRoadmapNode(
                new Period(LocalDate.now().plusDays(6),
                        LocalDate.now().plusDays(10)), 2, 두번째_로드맵_노드);

        final GoalRoomRoadmapNodes 골룸_노드들 = new GoalRoomRoadmapNodes(List.of(첫번째_골룸_노드, 두번째_골룸_노드));
        골룸.addAllGoalRoomRoadmapNodes(골룸_노드들);
        return goalRoomRepository.save(골룸);
    }

    private void 골룸에_대한_참여자_리스트를_생성한다(final Member 리더, final Member 팔로워, final GoalRoom 골룸) {
        final GoalRoomMember 골룸_멤버_리더 = new GoalRoomMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 1, 12, 0), 골룸, 리더);
        final GoalRoomMember 골룸_멤버_팔로워 = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.of(2023, 7, 5, 18, 0), 골룸, 팔로워);
        goalRoomMemberRepository.saveAll(List.of(골룸_멤버_리더, 골룸_멤버_팔로워));
    }

    private ExtractableResponse<Response> 리뷰를_생성한다(final String 팔로워_토큰_정보, final Long 로드맵_아이디,
                                                   final RoadmapReviewSaveRequest 로드맵_리뷰_생성_요청) {
        final ExtractableResponse<Response> 리뷰_생성_요청_결과 = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(AUTHORIZATION, 팔로워_토큰_정보)
                .body(로드맵_리뷰_생성_요청)
                .post("/api/roadmaps/" + 로드맵_아이디 + "/reviews")
                .then()
                .log().all()
                .extract();
        return 리뷰_생성_요청_결과;
    }
}
