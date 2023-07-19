package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.member.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.roadmap.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class RoadmapCreateIntegrationTest extends IntegrationTest {

    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";

    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final MemberRepository memberRepository;

    public RoadmapCreateIntegrationTest(final MemberRepository memberRepository,
                                        final RoadmapRepository roadmapRepository,
                                        final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    private static Long 아이디를_반환한다(final ExtractableResponse<Response> 응답) {
        return Long.parseLong(응답.header(HttpHeaders.LOCATION).split("/")[2]);
    }

    private void 응답_상태_코드_검증(final ExtractableResponse<Response> 응답, final HttpStatus http_상태) {
        assertThat(응답.statusCode()).isEqualTo(http_상태.value());
    }

    private ExtractableResponse<Response> 회원가입을_한다(final MemberJoinRequest 회원가입_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청)
                .post("/api/members/join")
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

    private Member 크리에이터를_생성한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
                new Nickname("코끼리"), "010-1234-5678");
        final Member creator = new Member(new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), memberProfile);
        return memberRepository.save(creator);
    }

    private RoadmapCategory 로드맵_카테고리를_저장한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private ExtractableResponse<Response> 로드맵_생성_요청(final Long 카테고리_ID, final String 로드맵_제목, final String 로드맵_소개글,
                                                    final String 로드맵_본문, final RoadmapDifficultyType 로드맵_난이도,
                                                    final Integer 추천_소요_기간,
                                                    final List<RoadmapNodeSaveRequest> 로드맵_노드들) {
        final RoadmapSaveRequest request = new RoadmapSaveRequest(카테고리_ID, 로드맵_제목, 로드맵_소개글, 로드맵_본문, 로드맵_난이도,
                추천_소요_기간, 로드맵_노드들);

        return RestAssured.given()
                .body(request).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/api/roadmaps")
                .then().log().all()
                .extract();
    }

    @Nested
    class 로드맵을_생성한다 {

        @Test
        void 정상적으로_생성한다() {
            // given
            final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", "password12!@#$%", "nickname",
                    "010-1234-5678",
                    GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
            회원가입을_한다(회원가입_요청);

            final LoginRequest 로그인_요청 = new LoginRequest(IDENTIFIER, PASSWORD);
            final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);

            final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("여행");
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(카테고리.getId(), "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // expect
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.CREATED);
            final Long 로드맵_ID = 아이디를_반환한다(로드맵_생성_응답);
            assertThat(로드맵_ID).isEqualTo(1L);
        }

        @Test
        void 본문의_값이_없는_로드맵이_정상적으로_생성한다() {
            // given
            final String 로드맵_본문 = null;
            크리에이터를_생성한다();
            final RoadmapCategory 카테고리 = 로드맵_카테고리를_저장한다("여행");

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(카테고리.getId(), "로드맵 제목", "로드맵 소개글", 로드맵_본문,
                    RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.CREATED);
        }

        @Test
        void 존재하지_않는_카테고리_아이디를_입력한_경우_실패한다() {
            // given
            final long 카테고리_id = 2L;
            크리에이터를_생성한다();

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(카테고리_id, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            final ErrorResponse 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.NOT_FOUND);
            assertThat(에러_메세지.message()).isEqualTo("존재하지 않는 카테고리입니다. categoryId = 2");

        }

        @Test
        void 카테고리를_입력하지_않은_경우_실패한다() {
            // given
            final Long 카테고리_id = null;

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(카테고리_id, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            final List<ErrorResponse> 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.get(0).message()).isEqualTo("카테고리를 입력해주세요.");
        }

        @Test
        void 제목의_길이가_40보다_크면_실패한다() {
            // given
            final String 로드맵_제목 = "a".repeat(41);
            로드맵_카테고리를_저장한다("여행");

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, 로드맵_제목, "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            final ErrorResponse 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.message()).isEqualTo("로드맵 제목의 길이는 최소 1글자, 최대 40글자입니다.");
        }

        @Test
        void 제목을_입력하지_않은_경우_실패한다() {
            // given
            final String 로드맵_제목 = null;

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, 로드맵_제목, "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            final List<ErrorResponse> 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.get(0).message()).isEqualTo("로드맵의 제목을 입력해주세요.");
        }

        @Test
        void 소개글의_길이가_150보다_크면_실패한다() {
            // given
            final String 로드맵_소개글 = "a".repeat(151);
            로드맵_카테고리를_저장한다("여행");

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", 로드맵_소개글, "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            final ErrorResponse 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.message()).isEqualTo("로드맵 소개글의 길이는 최소 1글자, 최대 150글자입니다.");
        }

        @Test
        void 소개글을_입력하지_않은_경우_실패한다() {
            // given
            final String 로드맵_소개글 = null;

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", 로드맵_소개글, "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            final List<ErrorResponse> 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.get(0).message()).isEqualTo("로드맵의 소개글을 입력해주세요.");
        }

        @Test
        void 본문의_길이가_150보다_크면_실패한다() {
            // given
            final String 로드맵_본문 = "a".repeat(151);
            로드맵_카테고리를_저장한다("여행");

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", "로드맵 소개글", 로드맵_본문,
                    RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            final ErrorResponse 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.message()).isEqualTo("로드맵 본문의 길이는 최대 150글자 입니다.");
        }

        @Test
        void 난이도를_입력하지_않은_경우_실패한다() {
            // given
            final RoadmapDifficultyType 로드맵_난이도 = null;
            로드맵_카테고리를_저장한다("여행");

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                    로드맵_난이도, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            final List<ErrorResponse> 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.get(0).message()).isEqualTo("난이도를 입력해주세요.");
        }

        @Test
        void 추천_소요_기간을_입력하지_않은_경우_실패한다() {
            // given
            final Integer 추천_소요_기간 = null;

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 추천_소요_기간,
                    List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            final List<ErrorResponse> 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.get(0).message()).isEqualTo("추천 소요 기간을 입력해주세요.");
        }

        @Test
        void 추천_소요_기간이_0보다_작으면_실패한다() {
            // given
            final Integer 추천_소요_기간 = -1;
            로드맵_카테고리를_저장한다("여행");

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 추천_소요_기간,
                    List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            final ErrorResponse 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.message()).isEqualTo("로드맵 추천 소요 기간은 최소 0일, 최대 1000일입니다.");
        }

        @Test
        void 로드맵_노드의_제목의_길이가_40보다_크면_실패한다() {
            // given
            final String 로드맵_노드_제목 = "a".repeat(41);
            final List<RoadmapNodeSaveRequest> 로드맵_노드들 = List.of(new RoadmapNodeSaveRequest(로드맵_노드_제목, "로드맵 1주차 내용"));
            로드맵_카테고리를_저장한다("여행");

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, 로드맵_노드들);

            // then
            final ErrorResponse 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.message()).isEqualTo("로드맵 노드의 제목의 길이는 최소 1글자, 최대 40글자입니다.");
        }

        @Test
        void 로드맵_노드의_제목을_입력하지_않으면_실패한다() {
            // given
            final String 로드맵_노드_제목 = null;
            final List<RoadmapNodeSaveRequest> 로드맵_노드들 = List.of(new RoadmapNodeSaveRequest(로드맵_노드_제목, "로드맵 1주차 내용"));

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, 로드맵_노드들);

            // then
            final List<ErrorResponse> 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.get(0).message()).isEqualTo("로드맵 노드의 제목을 입력해주세요.");
        }

        @Test
        void 로드맵_노드의_설명의_길이가_200보다_크면_실패한다() {
            // given
            final String 로드맵_노드_설명 = "a".repeat(201);
            final List<RoadmapNodeSaveRequest> 로드맵_노드들 = List.of(new RoadmapNodeSaveRequest("로드맵 노드 제목", 로드맵_노드_설명));
            로드맵_카테고리를_저장한다("여행");

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, 로드맵_노드들);

            // then
            final ErrorResponse 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.message()).isEqualTo("로드맵 노드의 설명의 길이는 최소 1글자, 최대 200글자입니다.");
        }

        @Test
        void 로드맵_노드의_설명을_입력하지_않으면_실패한다() {
            // given
            final String 로드맵_노드_설명 = null;
            final List<RoadmapNodeSaveRequest> 로드맵_노드들 = List.of(new RoadmapNodeSaveRequest("로드맵 노드 제목", 로드맵_노드_설명));

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, 로드맵_노드들);

            // then
            final List<ErrorResponse> 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.get(0).message()).isEqualTo("로드맵 노드의 설명을 입력해주세요.");
        }

        @Test
        void 로드맵_노드를_입력하지_않으면_실패한다() {
            // given
            final String 로드맵_노드_설명 = null;
            final List<RoadmapNodeSaveRequest> 로드맵_노드들 = List.of(new RoadmapNodeSaveRequest("로드맵 노드 제목", 로드맵_노드_설명));

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, null);

            // then
            final List<ErrorResponse> 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.BAD_REQUEST);
            assertThat(에러_메세지.get(0).message()).isEqualTo("로드맵의 첫 번째 단계를 입력해주세요.");
        }

    }
}
