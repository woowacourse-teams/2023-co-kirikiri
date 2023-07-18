package co.kirikiri.integration;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.ImageContentType;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.persistence.MemberRepository;
import co.kirikiri.persistence.RoadmapCategoryRepository;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.roadmap.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class RoadmapIntegrationTest extends IntegrationTest {

    @Autowired
    private RoadmapCategoryRepository roadmapCategoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void init() {
        // TODO: member 관련 부분 제거
        final MemberProfileImage profileImage = new MemberProfileImage(1L, "originalFileName", "serverFilePath",
                ImageContentType.JPEG);
        final MemberProfile profile = new MemberProfile(1L, Gender.FEMALE, LocalDate.of(1999, 6, 8), "nickname",
                "01011112222", profileImage);
        final Member creator = new Member(1L, "creator", "password", profile);
        memberRepository.save(creator);
        roadmapCategoryRepository.save(new RoadmapCategory("IT"));
    }

    @Nested
    class 로드맵을_생성한다 {

        @Test
        void 정상적으로_생성한다() {
            // given
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
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

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(1L, "로드맵 제목", "로드맵 소개글", 로드맵_본문,
                    RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.CREATED);
        }

        @Test
        void 존재하지_않는_카테고리_아이디를_입력한_경우_실패한다() {
            // given
            final long 카테고리_id = 2L;

            // when
            final ExtractableResponse<Response> 로드맵_생성_응답 = 로드맵_생성_요청(카테고리_id, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                    RoadmapDifficultyType.DIFFICULT, 30, List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차 내용")));

            // then
            final ErrorResponse 에러_메세지 = 로드맵_생성_응답.as(new TypeRef<>() {
            });
            응답_상태_코드_검증(로드맵_생성_응답, HttpStatus.NOT_FOUND);
            assertThat(에러_메세지.message()).isEqualTo("존재하지 않는 카테고리입니다. categoryId=2");

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
        void 제목을_입력하지_않은_경우_실패한다() throws UnsupportedEncodingException, JsonProcessingException {
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
    }

    private void 응답_상태_코드_검증(final ExtractableResponse<Response> 응답, final HttpStatus http_상태) {
        assertThat(응답.statusCode()).isEqualTo(http_상태.value());
    }

    private static Long 아이디를_반환한다(final ExtractableResponse<Response> 응답) {
        return Long.parseLong(응답.header(HttpHeaders.LOCATION).split("/")[2]);
    }
}
