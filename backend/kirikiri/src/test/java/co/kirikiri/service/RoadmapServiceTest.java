package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.ImageContentType;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImage;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.RoadmapNodeResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoadmapServiceTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @InjectMocks
    private RoadmapService roadmapService;

    @Test
    void 특정_아이디를_가지는_로드맵_단일_조회시_해당_로드맵의_정보를_반환한다() {
        //given
        final Member member = 사용자를_생성한다();
        final RoadmapCategory category = 로드맵_카테고리를_생성한다("운동");
        final Roadmap roadmap = 로드맵을_생성한다(member, category);
        final Long roadmapId = 1L;

        when(roadmapRepository.findById(anyLong()))
                .thenReturn(Optional.of(roadmap));

        //when
        final RoadmapResponse roadmapResponse = roadmapService.findRoadmap(roadmapId);

        //then
        final RoadmapResponse expectedResponse = new RoadmapResponse(
                roadmapId,
                new RoadmapCategoryResponse(1L, "운동"),
                "로드맵 제목",
                "로드맵 설명",
                new MemberResponse(1L, "썬샷"),
                null,
                "NORMAL",
                100,
                List.of(
                        new RoadmapNodeResponse("1단계", "준비운동", List.of("node-image1-save-path")),
                        new RoadmapNodeResponse("2단계", "턱걸이", List.of("node-image1-save-path"))
                )
        );

        assertThat(roadmapResponse)
                .usingRecursiveComparison()
                .ignoringFields("roadmapId")
                .isEqualTo(expectedResponse);
    }

    @Test
    void 로드맵_단일_조회_시_로드맵_아이디가_존재하지_않는_아이디일_경우_예외를_반환한다() {
        //given

        //when
        when(roadmapRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = 1L"));

        //then
        assertThatThrownBy(() -> roadmapService.findRoadmap(1L))
                .isInstanceOf(NotFoundException.class);
    }

    private Roadmap 로드맵을_생성한다(final Member creator, final RoadmapCategory category) {
        final RoadmapContent content = new RoadmapContent(로드맵_노드들을_생성한다());

        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 설명", 100,
                RoadmapDifficulty.NORMAL, RoadmapStatus.CREATED, creator, category);
        roadmap.addContent(content);

        return roadmap;
    }

    private Member 사용자를_생성한다() {
        final MemberProfileImage profileImage = new MemberProfileImage("sunshot_image.webp",
                "sunshot-profile-save-path", ImageContentType.WEBP);
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1995, 9, 30),
                "썬샷", "01083004367", profileImage);

        return new Member(1L, "아이디", "패스워드", memberProfile);
    }

    private RoadmapCategory 로드맵_카테고리를_생성한다(final String title) {
        return new RoadmapCategory(1L, title);
    }

    private List<RoadmapNode> 로드맵_노드들을_생성한다() {
        return List.of(
                new RoadmapNode("1단계", "준비운동", 노드_이미지들을_생성한다()),
                new RoadmapNode("2단계", "턱걸이", 노드_이미지들을_생성한다())
        );
    }

    private List<RoadmapNodeImage> 노드_이미지들을_생성한다() {
        return List.of(
                new RoadmapNodeImage("node-image1.png", "node-image1-save-path",
                        ImageContentType.PNG)
        );
    }
}