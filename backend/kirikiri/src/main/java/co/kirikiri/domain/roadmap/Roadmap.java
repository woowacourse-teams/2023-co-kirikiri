package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Roadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 200, nullable = false)
    private String introduction;

    @Column(nullable = false)
    private Integer requiredPeriod;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private RoadmapDifficulty difficulty;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 10, nullable = false)
    private RoadmapStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private RoadmapCategory category;

    @Embedded
    private RoadmapContents contents;

    public Roadmap(final String title, final String introduction, final Integer requiredPeriod,
                   final RoadmapDifficulty difficulty, final RoadmapStatus status,
                   final Member creator, final RoadmapCategory category) {
        this(null, title, introduction, requiredPeriod, difficulty, status, creator, category);
    }

    public Roadmap(final Long id, final String title, final String introduction, final Integer requiredPeriod,
                   final RoadmapDifficulty difficulty, final RoadmapStatus status,
                   final Member creator, final RoadmapCategory category) {
        this.id = id;
        this.title = title;
        this.introduction = introduction;
        this.requiredPeriod = requiredPeriod;
        this.difficulty = difficulty;
        this.status = status;
        this.creator = creator;
        this.category = category;
        this.contents = new RoadmapContents();
    }

    public void addContent(final RoadmapContent content) {
        contents.add(content);
        if (!content.isSameRoadmap(this)) {
            content.setRoadmap(this);
        }
    }

    public boolean hasContent(final RoadmapContent content) {
        return contents.hasContent(content);
    }

    public void removeContent(final RoadmapContent content) {
        contents.removeContent(content);
    }

    public Optional<RoadmapContent> getRecentContent() {
        if (contents.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(contents.getRecentContent());
    }
}
