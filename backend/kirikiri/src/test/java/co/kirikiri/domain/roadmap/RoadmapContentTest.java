package co.kirikiri.domain.roadmap;

import static co.kirikiri.domain.roadmap.RoadmapDifficulty.DIFFICULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.member.domain.EncryptedPassword;
import co.kirikiri.member.domain.Gender;
import co.kirikiri.member.domain.Member;
import co.kirikiri.member.domain.MemberProfile;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Nickname;
import co.kirikiri.member.domain.vo.Password;
import co.kirikiri.domain.roadmap.exception.RoadmapException;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoadmapContentTest {

    @Test
    void 로드맵_본문의_길이가_2000보다_크면_예외가_발생한다() {
        // given
        final String content = "a".repeat(2001);

        // expect
        assertThatThrownBy(() -> new RoadmapContent(content))
                .isInstanceOf(RoadmapException.class);
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
                        List.of(new RoadmapNode("title1", "content1"), new RoadmapNode("title2", "content1"))));

        // then
        final RoadmapNodes nodes = content.getNodes();
        assertAll(
                () -> assertThat(nodes.getValues()).hasSize(2),
                () -> assertThat(nodes.getValues().get(0).getRoadmapContent()).isEqualTo(content),
                () -> assertThat(nodes.getValues().get(1).getRoadmapContent()).isEqualTo(content)
        );
    }

    @Test
    void 로드맵_본문에_노드를_추가할때_이름이_겹치면_예외를_던진다() {
        // given
        final RoadmapContent content = new RoadmapContent("content");

        // when
        // then
        final String title = "title";
        assertThatThrownBy(() -> content.addNodes(
                new RoadmapNodes(
                        List.of(new RoadmapNode(title, "content1"), new RoadmapNode(title, "content1")))));
    }

    @Test
    void 로드맵_본문의_로드맵인_경우_false를_반환한다() {
        // given
        final RoadmapContent content = new RoadmapContent("content");
        final MemberProfile profile = new MemberProfile(Gender.FEMALE, "kirikiri1@email.com");
        final Member creator = new Member(new Identifier("creator"),
                new EncryptedPassword(new Password("password1")), new Nickname("nickname"), null, profile);
        final RoadmapCategory category = new RoadmapCategory(1L, "여가");
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 30, DIFFICULT, creator, category);

        // when
        roadmap.addContent(content);

        // then
        assertThat(content.isNotSameRoadmap(roadmap)).isFalse();
    }
}
