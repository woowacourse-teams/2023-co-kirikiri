package co.kirikiri.domain.roadmap;

import static co.kirikiri.domain.roadmap.RoadmapDifficulty.DIFFICULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.ImageContentType;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.exception.BadRequestException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RoadmapContentTest {

    @Test
    void 로드맵_본문의_길이가_150보다_크면_예외가_발생한다() {
        // given
        final String content = "a".repeat(151);

        // expect
        assertThatThrownBy(() -> new RoadmapContent(content))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 로드맵_본문은_null값을_허용한다() {
        // given
        final String content = null;

        // expect
        assertDoesNotThrow(() -> new RoadmapContent(content));
    }

    @Test
    void 로드맵_본문에_노드들을_추가한다() {
        // given
        final RoadmapContent content = new RoadmapContent("content");

        // when
        content.addNodes(
                new RoadmapNodes(
                        List.of(new RoadmapNode("title1", "content1"), new RoadmapNode("title1", "content1"))));

        // then
        final RoadmapNodes nodes = content.getNodes();
        assertAll(
                () -> assertThat(nodes.getRoadmapNodes()).hasSize(2),
                () -> assertThat(nodes.getRoadmapNodes().get(0).getRoadmapContent()).isEqualTo(content),
                () -> assertThat(nodes.getRoadmapNodes().get(1).getRoadmapContent()).isEqualTo(content)
        );
    }

    @Test
    void 로드맵_본문의_로드맵인_경우_false를_반환한다() {
        // given
        final RoadmapContent content = new RoadmapContent("content");
        final MemberProfileImage profileImage = new MemberProfileImage(1L, "originalFileName", "serverFilePath",
                ImageContentType.JPEG);
        final MemberProfile profile = new MemberProfile(1L, Gender.FEMALE, LocalDate.of(1999, 6, 8), "nickname",
                "01011112222", profileImage);
        final Member creator = new Member(1L, "creator", "password", profile);
        final RoadmapCategory category = new RoadmapCategory(1L, "여가");
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 30, DIFFICULT, creator, category);

        // when
        roadmap.addContent(content);

        // then
        assertThat(content.isNotSameRoadmap(roadmap)).isFalse();
    }
}
