package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.BaseTimeEntity;
import jakarta.persistence.CascadeType;
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
import java.util.Objects;
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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "roadmapContent")
    private List<RoadmapNode> nodes;

    public RoadmapContent(final List<RoadmapNode> nodes) {
        this(null, null, nodes);
    }

    public RoadmapContent(final String content, final List<RoadmapNode> nodes) {
        this(null, content, nodes);
    }

    public RoadmapContent(final Long id, final String content, final List<RoadmapNode> nodes) {
        this.id = id;
        this.content = content;
        setNodes(nodes);
    }

    public void setRoadmap(final Roadmap roadmap) {
        if (Objects.nonNull(this.roadmap)) {
            this.roadmap.removeContent(this);
        }
        this.roadmap = roadmap;
        if (!roadmap.hasContent(this)) {
            roadmap.addContent(this);
        }
    }

    public void setNodes(final List<RoadmapNode> nodes) {
        this.nodes = nodes;
        nodes.forEach(node -> node.setRoadmapContent(this));
    }

    public void removeNode(final RoadmapNode roadmapNode) {
        nodes.remove(roadmapNode);
    }

    public boolean isSameRoadmap(final Roadmap roadmap) {
        return Objects.equals(this.roadmap, roadmap);
    }
}

