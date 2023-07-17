package co.kirikiri.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import co.kirikiri.domain.roadmap.RoadmapNodes;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RoadmapRepositoryTest extends RepositoryTest {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final MemberRepository memberRepository;

    public RoadmapRepositoryTest(final RoadmapRepository roadmapRepository,
                                 final RoadmapCategoryRepository roadmapCategoryRepository,
                                 final RoadmapContentRepository roadmapContentRepository,
                                 final RoadmapNodeRepository roadmapNodeRepository,
                                 final MemberRepository memberRepository) {
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.roadmapContentRepository = roadmapContentRepository;
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.memberRepository = memberRepository;
    }

    @Test
    void 로드맵을_저장한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory category = 카테고리를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);

        // when
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 30, RoadmapDifficulty.DIFFICULT,
                creator, category);
        roadmap.addContent(roadmapContent);

        final Roadmap savedRoadmap = roadmapRepository.save(roadmap);

        // then
        assertAll(
                () -> assertThat(savedRoadmap.getId()).isPositive(),
                () -> assertThat(roadmapContentRepository.findAll()).hasSize(1),
                () -> assertThat(roadmapNodeRepository.findAll()).hasSize(2)
        );
    }

    private Member 크리에이터를_생성한다() {
        final MemberProfileImage profileImage = new MemberProfileImage(1L, "originalFileName", "serverFilePath",
                ImageContentType.JPEG);
        final MemberProfile profile = new MemberProfile(1L, Gender.FEMALE, LocalDate.of(1999, 6, 8), "nickname",
                "01011112222", profileImage);

        final Member creator = new Member(1L, "creator", "password", profile);
        return memberRepository.save(creator);
    }

    private RoadmapCategory 카테고리를_생성한다() {
        final RoadmapCategory category = new RoadmapCategory(1L, "여가");
        return roadmapCategoryRepository.save(category);
    }

    private List<RoadmapNode> 로드맵_노드들을_생성한다() {
        return List.of(new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용"),
                new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용"));
    }

    private RoadmapContent 로드맵_본문을_생성한다(final List<RoadmapNode> roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(new RoadmapNodes(roadmapNodes));
        return roadmapContent;
    }
}
