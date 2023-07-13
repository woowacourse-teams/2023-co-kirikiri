package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoadmapContent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2200)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "roadmapContent")
    private List<RoadmapNode> nodes;

    public RoadmapContent(final String content, final List<RoadmapNode> nodes) {
        this(null, content, nodes);
    }

    public RoadmapContent(final Long id, final String content, final List<RoadmapNode> nodes) {
        this.id = id;
        this.content = content;
        this.nodes = nodes;
    }

    public void updateRoadmap(final Roadmap roadmap) {
        if (this.roadmap != null) {
            this.roadmap.getContents().remove(this);
        }
        this.roadmap = roadmap;
        if (!roadmap.getContents().contains(this)) {
            roadmap.getContents().add(this);
        }
    }
}
