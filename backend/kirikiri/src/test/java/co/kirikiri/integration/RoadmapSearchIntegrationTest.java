package co.kirikiri.integration;

import co.kirikiri.integration.helper.InitIntegrationTest;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberInformationResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponses;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static co.kirikiri.integration.fixture.AuthenticationAPIFixture.로그인;
import static co.kirikiri.integration.fixture.CommonFixture.BEARER_TOKEN_FORMAT;
import static co.kirikiri.integration.fixture.MemberAPIFixture.*;
import static co.kirikiri.integration.fixture.RoadmapAPIFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RoadmapSearchIntegrationTest extends InitIntegrationTest {

    @Test
    void 로드맵을_제목을_기준으로_검색한다() throws IOException {
        // given
        로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 세번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "third roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 제목으로_최신순_정렬된_로드맵을_검색한다(10, "roadmap")
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(세번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(두번째_로드맵_아이디);
    }

    @Test
    void 이전_검색때_마지막으로_조회된_로드맵_아이디를_전달하여_로드맵을_제목을_기준으로_검색한다() throws IOException {
        // given
        로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 세번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "third roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 첫번째_로드맵_리스트_응답 = 제목으로_최신순_정렬된_로드맵을_검색한다(1, "roadmap")
                .response()
                .as(new TypeRef<>() {
                });

        final RoadmapForListResponses 두번째_로드맵_리스트_응답 = 이전_검색_결과를_전달받아_제목으로_최신순_정렬된_로드맵을_검색한다(
                1, 첫번째_로드맵_리스트_응답.responses().get(0).roadmapId(), "roadmap")
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(첫번째_로드맵_리스트_응답.hasNext()).isTrue(),
                () -> assertThat(첫번째_로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(세번째_로드맵_아이디),

                () -> assertThat(두번째_로드맵_리스트_응답.hasNext()).isFalse(),
                () -> assertThat(두번째_로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디)
        );
    }

    @Test
    void 로드맵을_크리에이터_닉네임_기준으로_검색한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final MemberInformationResponse 사용자_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(기본_로그인_토큰).as(new TypeRef<>() {
        });
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 크리에이터_닉네임으로_정렬된_로드맵을_검색한다(10, 사용자_정보.nickname())
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(기본_로드맵_아이디);
    }

    @Test
    void 크리에이터_닉네임_기준으로_로드맵_검색_시_크리에이터명이_검색어로_시작하는_모든_로드맵을_조회한다() throws IOException {
        // given
        final MemberJoinRequest 닉네임_시작이_다른_회원_가입_요청 = new MemberJoinRequest("identifier3", "paswword3#",
                "a" + DEFAULT_NICKNAME, GenderType.MALE, DEFAULT_EMAIL);
        final LoginRequest 닉네임_시작이_다른_사용자_로그인_요청 = new LoginRequest(닉네임_시작이_다른_회원_가입_요청.identifier(),
                닉네임_시작이_다른_회원_가입_요청.password());
        회원가입(닉네임_시작이_다른_회원_가입_요청);
        final String 닉네임_시작이_다른_사용자_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT,
                로그인(닉네임_시작이_다른_사용자_로그인_요청).accessToken());

        final MemberJoinRequest 닉네임_시작이_같은_사용자_회원_가입_요청 = new MemberJoinRequest("identifier2", "paswword2@",
                DEFAULT_NICKNAME + "a", GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 닉네임_시작이_같은_사용자_로그인_요청 = new LoginRequest(닉네임_시작이_같은_사용자_회원_가입_요청.identifier(),
                닉네임_시작이_같은_사용자_회원_가입_요청.password());
        회원가입(닉네임_시작이_같은_사용자_회원_가입_요청);
        final String 닉네임_시작이_같은_사용자_액세스_토큰 = String.format(BEARER_TOKEN_FORMAT,
                로그인(닉네임_시작이_같은_사용자_로그인_요청).accessToken());

        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "third roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.NORMAL, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그2")));
        로드맵_생성(두번째_로드맵_생성_요청, 닉네임_시작이_다른_사용자_액세스_토큰);

        final RoadmapSaveRequest 세번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번째_로드맵_생성_요청, 닉네임_시작이_같은_사용자_액세스_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 크리에이터_닉네임으로_정렬된_로드맵을_검색한다(6, DEFAULT_NICKNAME)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(기본_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(세번째_로드맵_아이디);
    }

    @Test
    void 이전_검색때_마지막으로_조회된_로드맵_아이디를_전달하여_로드맵을_크리에이터_닉네임_기준으로_검색한다() throws IOException {
        // given
        final Long 기본_로드맵_아이디 = 로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final MemberInformationResponse 사용자_정보 = 요청을_받는_사용자_자신의_정보_조회_요청(기본_로그인_토큰).as(new TypeRef<>() {
        });
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("다른 태그1")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 첫번째_로드맵_리스트_응답 = 크리에이터_닉네임으로_정렬된_로드맵을_검색한다(1, 사용자_정보.nickname())
                .response()
                .as(new TypeRef<>() {
                });

        final RoadmapForListResponses 두번째_로드맵_리스트_응답 = 이전_검색_결과를_전달받아_크리에이터_닉네임으로_정렬된_로드맵을_검색한다(
                1, 첫번째_로드맵_리스트_응답.responses().get(0).roadmapId(), 사용자_정보.nickname())
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(첫번째_로드맵_리스트_응답.hasNext()).isTrue(),
                () -> assertThat(첫번째_로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(두번째_로드맵_아이디),

                () -> assertThat(두번째_로드맵_리스트_응답.hasNext()).isFalse(),
                () -> assertThat(두번째_로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(기본_로드맵_아이디)
        );
    }

    @Test
    void 로드맵을_태그_이름을_기준으로_검색한다() throws IOException {
        // given
        final String 태그_이름 = "tag name";

        로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);
        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest(태그_이름)));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 세번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "세번쨰 로드맵", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest(태그_이름)));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 로드맵_리스트_응답 = 태그_이름으로_최신순_정렬된_로드맵을_검색한다(10, 태그_이름)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(로드맵_리스트_응답.hasNext()).isFalse();
        assertThat(로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(세번째_로드맵_아이디);
        assertThat(로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(두번째_로드맵_아이디);
    }

    @Test
    void 태그_기준으로_로드맵_검색_시_태그_이름이_검색어로_시작하는_모든_로드맵을_조회한다() throws IOException {
        // given
        final String 태그_이름 = "java";

        로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 첫번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "세번쨰 로드맵", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest(태그_이름 + "eq")));
        final Long 첫번째_로드맵_아이디 = 로드맵_생성(첫번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest(태그_이름),
                        new RoadmapTagSaveRequest("testTag")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 세번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "세번쨰 로드맵", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest(태그_이름)));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 네번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "세번쨰 로드맵", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest(태그_이름 + "왕")));
        final Long 네번째_로드맵_아이디 = 로드맵_생성(네번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 다섯번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "세번쨰 로드맵", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("왕" + 태그_이름)));
        로드맵_생성(다섯번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 첫번째_로드맵_리스트_응답 = 태그_이름으로_최신순_정렬된_로드맵을_검색한다(4, 태그_이름)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(첫번째_로드맵_리스트_응답.hasNext()).isFalse(),
                () -> assertThat(첫번째_로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(세번째_로드맵_아이디),
                () -> assertThat(첫번째_로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(두번째_로드맵_아이디),
                () -> assertThat(첫번째_로드맵_리스트_응답.responses().get(2).roadmapId()).isEqualTo(네번째_로드맵_아이디),
                () -> assertThat(첫번째_로드맵_리스트_응답.responses().get(3).roadmapId()).isEqualTo(첫번째_로드맵_아이디)
        );
    }

    @Test
    void 이전_검색때_마지막으로_조회된_로드맵_아이디를_전달하여_로드맵을_태그_기준으로_검색한다() throws IOException {
        // given
        final String 태그_이름 = "java";

        로드맵_생성(기본_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 첫번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "세번쨰 로드맵", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest(태그_이름 + "eq")));
        final Long 첫번째_로드맵_아이디 = 로드맵_생성(첫번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 두번째_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "second roadmap", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest(태그_이름),
                        new RoadmapTagSaveRequest("testTag")));
        final Long 두번째_로드맵_아이디 = 로드맵_생성(두번째_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 세번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "세번쨰 로드맵", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest(태그_이름)));
        final Long 세번째_로드맵_아이디 = 로드맵_생성(세번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 네번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "세번쨰 로드맵", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest(태그_이름 + "왕")));
        final Long 네번째_로드맵_아이디 = 로드맵_생성(네번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        final RoadmapSaveRequest 다섯번쨰_로드맵_생성_요청 = new RoadmapSaveRequest(기본_카테고리.getId(), "세번쨰 로드맵", "다른 로드맵 소개글",
                "다른 로드맵 본문", RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("다른 로드맵 1주차", "다른 로드맵 1주차 내용", null)),
                List.of(new RoadmapTagSaveRequest("왕" + 태그_이름)));
        로드맵_생성(다섯번쨰_로드맵_생성_요청, 기본_로그인_토큰);

        // when
        final RoadmapForListResponses 첫번째_로드맵_리스트_응답 = 태그_이름으로_최신순_정렬된_로드맵을_검색한다(3, 태그_이름)
                .response()
                .as(new TypeRef<>() {
                });

        final RoadmapForListResponses 두번째_로드맵_리스트_응답 = 이전_검색_결과를_전달받아_태그_이름으로_최신순_정렬된_로드맵을_검색한다(
                3, 첫번째_로드맵_리스트_응답.responses().get(2).roadmapId(), 태그_이름)
                .response()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(첫번째_로드맵_리스트_응답.hasNext()).isTrue(),
                () -> assertThat(첫번째_로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(세번째_로드맵_아이디),
                () -> assertThat(첫번째_로드맵_리스트_응답.responses().get(1).roadmapId()).isEqualTo(두번째_로드맵_아이디),
                () -> assertThat(첫번째_로드맵_리스트_응답.responses().get(2).roadmapId()).isEqualTo(네번째_로드맵_아이디),

                () -> assertThat(두번째_로드맵_리스트_응답.hasNext()).isFalse(),
                () -> assertThat(두번째_로드맵_리스트_응답.responses().get(0).roadmapId()).isEqualTo(첫번째_로드맵_아이디)
        );

    }
}
