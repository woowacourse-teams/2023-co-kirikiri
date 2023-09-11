package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.ImageContentType;
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
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.domain.roadmap.RoadmapReview;
import co.kirikiri.domain.roadmap.RoadmapTag;
import co.kirikiri.domain.roadmap.RoadmapTags;
import co.kirikiri.domain.roadmap.vo.RoadmapTagName;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsOrderType;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.persistence.roadmap.RoadmapReviewRepository;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomsOrderTypeDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapOrderTypeRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSearchRequest;
import co.kirikiri.service.dto.roadmap.response.MemberRoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.MemberRoadmapResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapReviewResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapTagResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RoadmapReadServiceTest {

    private static final LocalDate TODAY = LocalDate.now();

    private final Member member = new Member(1L, new Identifier("identifier1"),
            new EncryptedPassword(new Password("password1!")), new Nickname("닉네임"),
            new MemberImage("originalFileName", "default-member-image", ImageContentType.JPG),
            new MemberProfile(Gender.FEMALE, "kirikiri1@email.com"));
    private final LocalDateTime now = LocalDateTime.now();

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapCategoryRepository roadmapCategoryRepository;

    @Mock
    private RoadmapContentRepository roadmapContentRepository;

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RoadmapReviewRepository roadmapReviewRepository;

    @Mock
    private FileService fileService;

    @InjectMocks
    private RoadmapReadService roadmapService;

    @Test
    void 특정_아이디를_가지는_로드맵_단일_조회시_해당_로드맵의_정보를_반환한다() throws MalformedURLException {
        //given
        final Member member = 사용자를_생성한다(1L, "identifier1", "코끼리");
        final RoadmapCategory category = 로드맵_카테고리를_생성한다(1L, "운동");
        final RoadmapContent content = 로드맵_컨텐츠를_생성한다(1L, "콘텐츠 내용");
        final Roadmap roadmap = 로드맵을_생성한다("로드맵 제목", category);
        roadmap.addContent(content);
        final Long roadmapId = 1L;

        final List<GoalRoom> goalRooms = 상태별_골룸_목록을_생성한다();

        when(roadmapRepository.findRoadmapById(anyLong()))
                .thenReturn(Optional.of(roadmap));
        when(roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(any()))
                .thenReturn(Optional.of(roadmap.getContents().getValues().get(0)));
        when(goalRoomRepository.findByRoadmap(any()))
                .thenReturn(goalRooms);
        when(fileService.generateUrl(anyString(), any()))
                .thenReturn(new URL("http://example.com/serverFilePath"));

        //when
        final RoadmapResponse roadmapResponse = roadmapService.findRoadmap(roadmapId);

        //then
        final RoadmapResponse expectedResponse = new RoadmapResponse(
                roadmapId, new RoadmapCategoryResponse(1L, "운동"), "로드맵 제목", "로드맵 소개글",
                new MemberResponse(1L, "닉네임", "http://example.com/serverFilePath"),
                new RoadmapContentResponse(1L, "로드맵 본문", List.of(
                        new RoadmapNodeResponse(1L, "로드맵 노드1 제목", "로드맵 노드1 설명", Collections.emptyList())
                )), "DIFFICULT", 30, now,
                List.of(new RoadmapTagResponse(1L, "태그1"),
                        new RoadmapTagResponse(2L, "태그2")),
                2L, 2L, 2L
        );

        assertThat(roadmapResponse)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expectedResponse);
    }

    @Test
    void 로드맵_단일_조회_시_로드맵_아이디가_존재하지_않는_아이디일_경우_예외를_반환한다() {
        //when
        when(roadmapRepository.findRoadmapById(anyLong()))
                .thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> roadmapService.findRoadmap(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 로드맵_목록_조회시_카테고리_아이디가_유효하지_않으면_예외가_발생한다() {
        // given
        when(roadmapCategoryRepository.findById(any()))
                .thenReturn(Optional.empty());

        final Long categoryId = 1L;
        final RoadmapOrderTypeRequest filterType = RoadmapOrderTypeRequest.LATEST;
        final CustomScrollRequest scrollRequest = new CustomScrollRequest(null, 10);

        // expected
        assertThatThrownBy(() -> roadmapService.findRoadmapsByOrderType(categoryId, filterType, scrollRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 로드맵_목록_조회_시_필터_조건이_null이면_최신순으로_조회한다() throws MalformedURLException {
        // given
        final RoadmapCategory category = new RoadmapCategory(1L, "여행");
        final List<Roadmap> roadmaps = List.of(
                로드맵을_생성한다("첫 번째 로드맵", category),
                로드맵을_생성한다("두 번째 로드맵", category));

        when(roadmapCategoryRepository.findById(any()))
                .thenReturn(Optional.of(category));
        when(roadmapRepository.findRoadmapsByCategory(any(), any(), any(), anyInt()))
                .thenReturn(roadmaps);
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        final Long categoryId = 1L;
        final RoadmapOrderTypeRequest filterType = null;
        final CustomScrollRequest scrollRequest = new CustomScrollRequest(null, 10);

        // when
        final RoadmapForListResponses roadmapResponses = roadmapService.findRoadmapsByOrderType(
                categoryId, filterType, scrollRequest);

        // then
        final RoadmapForListResponse firstRoadmapResponse = new RoadmapForListResponse(1L, "첫 번째 로드맵", "로드맵 소개글",
                "DIFFICULT", 30, LocalDateTime.now(),
                new MemberResponse(1L, "닉네임", "http://example.com/serverFilePath"),
                new RoadmapCategoryResponse(1, "여행"),
                List.of(
                        new RoadmapTagResponse(1L, "태그1"),
                        new RoadmapTagResponse(2L, "태그2")));

        final RoadmapForListResponse secondRoadmapResponse = new RoadmapForListResponse(1L, "두 번째 로드맵", "로드맵 소개글",
                "DIFFICULT", 30, LocalDateTime.now(),
                new MemberResponse(1L, "닉네임", "http://example.com/serverFilePath"),
                new RoadmapCategoryResponse(1, "여행"),
                List.of(
                        new RoadmapTagResponse(1L, "태그1"),
                        new RoadmapTagResponse(2L, "태그2")));

        final List<RoadmapForListResponse> responses = List.of(firstRoadmapResponse, secondRoadmapResponse);
        final RoadmapForListResponses expected = new RoadmapForListResponses(responses, false);

        assertThat(roadmapResponses)
                .usingRecursiveComparison()
                .ignoringFields("responses.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_목록_조회시_다음_요소가_존재하면_true로_반환한다() throws MalformedURLException {
        // given
        final RoadmapCategory category = new RoadmapCategory(1L, "여행");
        final List<Roadmap> roadmaps = List.of(
                로드맵을_생성한다("첫 번째 로드맵", category),
                로드맵을_생성한다("두 번째 로드맵", category));

        when(roadmapCategoryRepository.findById(any()))
                .thenReturn(Optional.of(category));
        when(roadmapRepository.findRoadmapsByCategory(any(), any(), any(), anyInt()))
                .thenReturn(roadmaps);
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        final Long categoryId = 1L;
        final RoadmapOrderTypeRequest filterType = null;
        final CustomScrollRequest scrollRequest = new CustomScrollRequest(null, 1);

        // when
        final RoadmapForListResponses roadmapResponses = roadmapService.findRoadmapsByOrderType(
                categoryId, filterType, scrollRequest);

        // then
        final RoadmapForListResponse firstRoadmapResponse = new RoadmapForListResponse(
                1L, "첫 번째 로드맵", "로드맵 소개글", "DIFFICULT", 30, LocalDateTime.now(),
                new MemberResponse(1L, "닉네임", "http://example.com/serverFilePath"),
                new RoadmapCategoryResponse(1, "여행"),
                List.of(
                        new RoadmapTagResponse(1L, "태그1"),
                        new RoadmapTagResponse(2L, "태그2")));

        final List<RoadmapForListResponse> responses = List.of(firstRoadmapResponse);
        final RoadmapForListResponses expected = new RoadmapForListResponses(responses, true);

        assertThat(roadmapResponses)
                .usingRecursiveComparison()
                .ignoringFields("responses.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_목록_조회_시_카테고리_조건이_null이면_전체_카테고리를_대상으로_최신순으로_조회한다() throws MalformedURLException {
        // given
        final RoadmapCategory category = new RoadmapCategory(1L, "여행");
        final List<Roadmap> roadmaps = List.of(로드맵을_생성한다("첫 번째 로드맵", category), 로드맵을_생성한다("두 번째 로드맵", category));

        when(roadmapRepository.findRoadmapsByCategory(any(), any(), any(), anyInt()))
                .thenReturn(roadmaps);
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        final Long categoryId = null;
        final RoadmapOrderTypeRequest filterType = RoadmapOrderTypeRequest.LATEST;
        final CustomScrollRequest scrollRequest = new CustomScrollRequest(null, 10);

        // when
        final RoadmapForListResponses roadmapResponses = roadmapService.findRoadmapsByOrderType(
                categoryId, filterType, scrollRequest);

        // then
        final RoadmapForListResponse firstRoadmapResponse = new RoadmapForListResponse(1L, "첫 번째 로드맵", "로드맵 소개글",
                "DIFFICULT", 30, LocalDateTime.now(),
                new MemberResponse(1L, "닉네임", "http://example.com/serverFilePath"),
                new RoadmapCategoryResponse(1, "여행"),
                List.of(
                        new RoadmapTagResponse(1L, "태그1"),
                        new RoadmapTagResponse(2L, "태그2")));

        final RoadmapForListResponse secondRoadmapResponse = new RoadmapForListResponse(1L, "두 번째 로드맵", "로드맵 소개글",
                "DIFFICULT", 30, LocalDateTime.now(),
                new MemberResponse(1L, "닉네임", "http://example.com/serverFilePath"),
                new RoadmapCategoryResponse(1, "여행"),
                List.of(
                        new RoadmapTagResponse(1L, "태그1"),
                        new RoadmapTagResponse(2L, "태그2")));

        final RoadmapForListResponses expected = new RoadmapForListResponses(
                List.of(firstRoadmapResponse, secondRoadmapResponse), false);

        assertThat(roadmapResponses)
                .usingRecursiveComparison()
                .ignoringFields("responses.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 카테고리_아이디와_필터링_조건을_통해_로드맵_목록을_조회한다() throws MalformedURLException {
        // given
        final RoadmapCategory category = new RoadmapCategory(1L, "여행");
        final List<Roadmap> roadmaps = List.of(로드맵을_생성한다("첫 번째 로드맵", category));

        when(roadmapCategoryRepository.findById(any()))
                .thenReturn(Optional.of(new RoadmapCategory("여행")));
        when(roadmapRepository.findRoadmapsByCategory(any(), any(), any(), anyInt()))
                .thenReturn(roadmaps);
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        final Long categoryId = 1L;
        final RoadmapOrderTypeRequest filterType = RoadmapOrderTypeRequest.LATEST;
        final CustomScrollRequest scrollRequest = new CustomScrollRequest(null, 10);

        // when
        final RoadmapForListResponses roadmapResponses = roadmapService.findRoadmapsByOrderType(
                categoryId, filterType, scrollRequest);

        // then
        final RoadmapForListResponse roadmapResponse = new RoadmapForListResponse(1L, "첫 번째 로드맵", "로드맵 소개글",
                "DIFFICULT", 30, LocalDateTime.now(),
                new MemberResponse(1L, "닉네임", "http://example.com/serverFilePath"),
                new RoadmapCategoryResponse(1, "여행"),
                List.of(
                        new RoadmapTagResponse(1L, "태그1"),
                        new RoadmapTagResponse(2L, "태그2")));

        final RoadmapForListResponses expected = new RoadmapForListResponses(List.of(roadmapResponse), false);

        assertThat(roadmapResponses)
                .usingRecursiveComparison()
                .ignoringFields("responses.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_전체_카테고리_리스트를_반환한다() {
        // given
        final List<RoadmapCategory> roadmapCategories = 로드맵_카테고리_리스트를_반환한다();
        when(roadmapCategoryRepository.findAll())
                .thenReturn(roadmapCategories);

        // when
        final List<RoadmapCategoryResponse> categoryResponses = roadmapService.findAllRoadmapCategories();

        // then
        final List<RoadmapCategoryResponse> expected = 로드맵_카테고리_응답_리스트를_반환한다();
        assertThat(categoryResponses)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵을_검색한다() throws MalformedURLException {
        // given
        final RoadmapCategory category = new RoadmapCategory(1L, "여행");
        final List<Roadmap> roadmaps = List.of(
                로드맵을_생성한다("첫 번째 로드맵", category),
                로드맵을_생성한다("두 번째 로드맵", category));

        when(roadmapRepository.findRoadmapsByCond(any(), any(), any(), anyInt()))
                .thenReturn(roadmaps);
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        final RoadmapSearchRequest roadmapSearchRequest = new RoadmapSearchRequest("로드맵", "닉네임", "태그");
        final RoadmapOrderTypeRequest filterType = RoadmapOrderTypeRequest.LATEST;
        final CustomScrollRequest scrollRequest = new CustomScrollRequest(null, 10);

        // when
        final RoadmapForListResponses roadmapResponses = roadmapService.search(
                filterType, roadmapSearchRequest, scrollRequest);

        // then
        final RoadmapForListResponse firstRoadmapResponse = new RoadmapForListResponse(1L, "첫 번째 로드맵", "로드맵 소개글",
                "DIFFICULT", 30, LocalDateTime.now(),
                new MemberResponse(1L, "닉네임", "http://example.com/serverFilePath"),
                new RoadmapCategoryResponse(1, "여행"),
                List.of(
                        new RoadmapTagResponse(1L, "태그1"),
                        new RoadmapTagResponse(2L, "태그2")));

        final RoadmapForListResponse secondRoadmapResponse = new RoadmapForListResponse(1L, "두 번째 로드맵", "로드맵 소개글",
                "DIFFICULT", 30, LocalDateTime.now(),
                new MemberResponse(1L, "닉네임", "http://example.com/serverFilePath"),
                new RoadmapCategoryResponse(1, "여행"),
                List.of(
                        new RoadmapTagResponse(1L, "태그1"),
                        new RoadmapTagResponse(2L, "태그2")));

        final RoadmapForListResponses expected = new RoadmapForListResponses(
                List.of(firstRoadmapResponse, secondRoadmapResponse), false);

        assertThat(roadmapResponses)
                .usingRecursiveComparison()
                .ignoringFields("responses.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 사용자가_생성한_로드맵을_조회한다() {
        // given
        final Member member = 사용자를_생성한다(1L, "identifier1", "코끼리");
        final RoadmapCategory category1 = 로드맵_카테고리를_생성한다(1L, "운동");
        final RoadmapCategory category2 = 로드맵_카테고리를_생성한다(2L, "여가");
        final Roadmap roadmap1 = 로드맵을_생성한다("로드맵1", category1);
        final Roadmap roadmap2 = 로드맵을_생성한다("로드맵2", category2);

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(member));
        when(roadmapRepository.findRoadmapsWithCategoryByMemberOrderByLatest(any(), any(), anyInt()))
                .thenReturn(List.of(roadmap2, roadmap1));

        // when
        final MemberRoadmapResponses memberRoadmapResponse = roadmapService.findAllMemberRoadmaps(
                "identifier1", new CustomScrollRequest(null, 10));

        // then
        final MemberRoadmapResponses expected = new MemberRoadmapResponses(List.of(
                new MemberRoadmapResponse(2L, "로드맵2", RoadmapDifficulty.DIFFICULT.name(), LocalDateTime.now(),
                        new RoadmapCategoryResponse(2L, "여가")),
                new MemberRoadmapResponse(1L, "로드맵1", RoadmapDifficulty.DIFFICULT.name(), LocalDateTime.now(),
                        new RoadmapCategoryResponse(1L, "운동"))), false);

        assertThat(memberRoadmapResponse)
                .usingRecursiveComparison()
                .ignoringFields("responses.roadmapId", "responses.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 사용자가_생성한_로드맵을_조회할때_존재하지_않는_회원이면_예외가_발생한다() {
        // given
        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> roadmapService.findAllMemberRoadmaps("identifier1",
                new CustomScrollRequest(null, 10))).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
    }

    @Test
    void 로드맵의_골룸_목록을_최신순으로_조회한다() throws MalformedURLException {
        // given
        final Member member1 = 사용자를_생성한다(1L, "identifier1", "name1");
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(roadmapNode1, roadmapNode2));
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(roadmapNodes);
        final Roadmap roadmap = new Roadmap(1L, "로드맵 제목", "로드맵 설명", 100, RoadmapDifficulty.DIFFICULT, member1,
                new RoadmapCategory("it"));

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = new GoalRoomRoadmapNode(new Period(TODAY, TODAY.plusDays(10)),
                1, roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = new GoalRoomRoadmapNode(
                new Period(TODAY.plusDays(11), TODAY.plusDays(20)), 1, roadmapNode2);
        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = new GoalRoomRoadmapNode(new Period(TODAY, TODAY.plusDays(10)),
                1, roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = new GoalRoomRoadmapNode(
                new Period(TODAY.plusDays(11), TODAY.plusDays(20)), 1, roadmapNode2);

        final Member member2 = 사용자를_생성한다(2L, "identifier2", "name2");
        final GoalRoom goalRoom1 = new GoalRoom(1L, new GoalRoomName("goalroom1"), new LimitedMemberCount(10),
                roadmapContent, member2);
        goalRoom1.addAllGoalRoomRoadmapNodes(
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)));

        final Member member3 = 사용자를_생성한다(3L, "identifier2", "name3");
        final GoalRoom goalRoom2 = new GoalRoom(2L, new GoalRoomName("goalroom2"), new LimitedMemberCount(10),
                roadmapContent, member3);
        goalRoom2.addAllGoalRoomRoadmapNodes(
                new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3, goalRoomRoadmapNode4)));

        final List<GoalRoom> goalRooms = List.of(goalRoom2, goalRoom1);
        given(roadmapRepository.findRoadmapById(anyLong()))
                .willReturn(Optional.of(roadmap));
        given(goalRoomRepository.findGoalRoomsWithPendingMembersByRoadmapAndCond(roadmap,
                RoadmapGoalRoomsOrderType.LATEST, null, 10))
                .willReturn(goalRooms);
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        // when
        final RoadmapGoalRoomResponses result = roadmapService.findRoadmapGoalRoomsByOrderType(1L,
                RoadmapGoalRoomsOrderTypeDto.LATEST, new CustomScrollRequest(null, 10));

        final RoadmapGoalRoomResponses expected =
                new RoadmapGoalRoomResponses(List.of(
                        new RoadmapGoalRoomResponse(2L, "goalroom2", 1, 10, LocalDateTime.now(),
                                TODAY, TODAY.plusDays(20),
                                new MemberResponse(member3.getId(), member3.getNickname().getValue(),
                                        "http://example.com/serverFilePath")),
                        new RoadmapGoalRoomResponse(1L, "goalroom1", 1, 10, LocalDateTime.now(),
                                TODAY, TODAY.plusDays(20),
                                new MemberResponse(member2.getId(), member2.getNickname().getValue(),
                                        "http://example.com/serverFilePath"))), false);

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("responses.createdAt")
                .isEqualTo(expected);
    }

    @Test
    void 로드맵의_골룸_목록을_조회할때_존재하지_않는_로드맵이면_예외가_발생한다() {
        // given
        given(roadmapRepository.findRoadmapById(anyLong()))
                .willThrow(new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = 1"));

        // when
        // then
        assertThatThrownBy(
                () -> roadmapService.findRoadmapGoalRoomsByOrderType(1L,
                        RoadmapGoalRoomsOrderTypeDto.LATEST, new CustomScrollRequest(null, 10)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 로드맵입니다. roadmapId = 1");
    }

    @Test
    void 로드맵의_리뷰_목록을_최신순으로_조회한다() throws MalformedURLException {
        // given
        final Member member1 = 사용자를_생성한다(1L, "identifier1", "리뷰어1");
        final Member member2 = 사용자를_생성한다(2L, "identifier2", "리뷰어2");
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(roadmapNode1, roadmapNode2));
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(roadmapNodes);
        final Roadmap roadmap = new Roadmap(1L, "로드맵 제목", "로드맵 설명", 100, RoadmapDifficulty.DIFFICULT, member1,
                new RoadmapCategory("it"));

        final RoadmapReview roadmapReview1 = new RoadmapReview("리뷰 내용", 5.0, member1);
        final RoadmapReview roadmapReview2 = new RoadmapReview("리뷰 내용", 4.5, member2);
        roadmapReview1.updateRoadmap(roadmap);
        roadmapReview2.updateRoadmap(roadmap);

        when(roadmapRepository.findRoadmapById(anyLong())).thenReturn(Optional.of(roadmap));
        when(roadmapReviewRepository.findRoadmapReviewWithMemberByRoadmapOrderByLatest(any(), any(), anyInt()))
                .thenReturn(List.of(roadmapReview2, roadmapReview1));
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        // when
        final List<RoadmapReviewResponse> response = roadmapService.findRoadmapReviews(1L,
                new CustomScrollRequest(null, 10));

        final List<RoadmapReviewResponse> expect = List.of(
                new RoadmapReviewResponse(2L, new MemberResponse(2L, "리뷰어2", "http://example.com/serverFilePath"),
                        LocalDateTime.now(), "리뷰 내용", 4.5),
                new RoadmapReviewResponse(1L, new MemberResponse(1L, "리뷰어1", "http://example.com/serverFilePath"),
                        LocalDateTime.now(), "리뷰 내용", 5.0));

        // then
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("id", "member.imageUrl", "createdAt")
                .isEqualTo(expect);
    }

    @Test
    void 로드맵_리뷰_조회_시_유효하지_않은_로드맵_아이디라면_예외를_반환한다() {
        // given
        when(roadmapRepository.findRoadmapById(anyLong()))
                .thenThrow(new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = 1"));

        // when, then
        assertThatThrownBy(() -> roadmapService.findRoadmapReviews(1L, new CustomScrollRequest(null, 2)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 로드맵입니다. roadmapId = 1");
    }

    private Member 사용자를_생성한다(final Long id, final String identifier, final String nickname) {
        return new Member(id, new Identifier(identifier),
                new EncryptedPassword(new Password("password1!")),
                new Nickname(nickname),
                new MemberImage("originalFileName", "default-profile-image", ImageContentType.JPG),
                new MemberProfile(Gender.FEMALE, "kirikiri1@email.com"));
    }

    private Roadmap 로드맵을_생성한다(final String roadmapTitle, final RoadmapCategory category) {
        final Roadmap roadmap = new Roadmap(1L, roadmapTitle, "로드맵 소개글", 30,
                RoadmapDifficulty.valueOf("DIFFICULT"), member, category);

        final RoadmapTags roadmapTags = new RoadmapTags(
                List.of(new RoadmapTag(1L, new RoadmapTagName("태그1")),
                        new RoadmapTag(2L, new RoadmapTagName("태그2"))));
        roadmap.addTags(roadmapTags);

        final RoadmapContent roadmapContent = new RoadmapContent(1L, "로드맵 본문");
        final RoadmapNodes roadmapNodes = new RoadmapNodes(
                List.of(new RoadmapNode(1L, "로드맵 노드1 제목", "로드맵 노드1 설명")));
        roadmapContent.addNodes(roadmapNodes);
        roadmap.addContent(roadmapContent);

        return roadmap;
    }

    private RoadmapCategory 로드맵_카테고리를_생성한다(final Long id, final String title) {
        return new RoadmapCategory(id, title);
    }

    private RoadmapContent 로드맵_컨텐츠를_생성한다(final Long id, final String content) {
        return new RoadmapContent(id, content);
    }

    private List<RoadmapCategory> 로드맵_카테고리_리스트를_반환한다() {
        final RoadmapCategory category1 = new RoadmapCategory(1L, "어학");
        final RoadmapCategory category2 = new RoadmapCategory(2L, "IT");
        final RoadmapCategory category3 = new RoadmapCategory(3L, "시험");
        final RoadmapCategory category4 = new RoadmapCategory(4L, "운동");
        final RoadmapCategory category5 = new RoadmapCategory(5L, "게임");
        final RoadmapCategory category6 = new RoadmapCategory(6L, "음악");
        final RoadmapCategory category7 = new RoadmapCategory(7L, "라이프");
        final RoadmapCategory category8 = new RoadmapCategory(8L, "여가");
        final RoadmapCategory category9 = new RoadmapCategory(9L, "기타");
        return List.of(category1, category2, category3, category4, category5,
                category6, category7, category8, category9);
    }

    private List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트를_반환한다() {
        final RoadmapCategoryResponse category1 = new RoadmapCategoryResponse(1L, "어학");
        final RoadmapCategoryResponse category2 = new RoadmapCategoryResponse(2L, "IT");
        final RoadmapCategoryResponse category3 = new RoadmapCategoryResponse(3L, "시험");
        final RoadmapCategoryResponse category4 = new RoadmapCategoryResponse(4L, "운동");
        final RoadmapCategoryResponse category5 = new RoadmapCategoryResponse(5L, "게임");
        final RoadmapCategoryResponse category6 = new RoadmapCategoryResponse(6L, "음악");
        final RoadmapCategoryResponse category7 = new RoadmapCategoryResponse(7L, "라이프");
        final RoadmapCategoryResponse category8 = new RoadmapCategoryResponse(8L, "여가");
        final RoadmapCategoryResponse category9 = new RoadmapCategoryResponse(9L, "기타");
        return List.of(category1, category2, category3, category4, category5,
                category6, category7, category8, category9);
    }

    private List<GoalRoom> 상태별_골룸_목록을_생성한다() {
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 내용");
        final GoalRoom recruitedGoalRoom1 = new GoalRoom(new GoalRoomName("모집 중 골룸 1"),
                new LimitedMemberCount(20), roadmapContent, member);
        final GoalRoom recruitedGoalRoom2 = new GoalRoom(new GoalRoomName("모집 중 골룸 2"),
                new LimitedMemberCount(20), roadmapContent, member);
        final GoalRoom runningGoalRoom1 = new GoalRoom(new GoalRoomName("진행 중 골룸 1"),
                new LimitedMemberCount(20), roadmapContent, member);
        final GoalRoom runningGoalRoom2 = new GoalRoom(new GoalRoomName("진행 중 골룸 2"),
                new LimitedMemberCount(20), roadmapContent, member);
        final GoalRoom completedGoalRoom1 = new GoalRoom(new GoalRoomName("완료된 골룸 1"),
                new LimitedMemberCount(20), roadmapContent, member);
        final GoalRoom completedGoalRoom2 = new GoalRoom(new GoalRoomName("완료된 골룸 2"),
                new LimitedMemberCount(20), roadmapContent, member);

        runningGoalRoom1.start();
        runningGoalRoom2.start();
        completedGoalRoom1.complete();
        completedGoalRoom2.complete();

        return List.of(recruitedGoalRoom1, recruitedGoalRoom2, runningGoalRoom1, runningGoalRoom2,
                completedGoalRoom1, completedGoalRoom2);
    }
}
