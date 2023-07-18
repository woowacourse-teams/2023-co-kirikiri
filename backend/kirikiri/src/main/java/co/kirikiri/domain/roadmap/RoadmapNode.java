package co.kirikiri.domain.roadmap;

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
public class RoadmapNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 2200, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_content_id", nullable = false)
    private RoadmapContent roadmapContent;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "roadmap_node_id")
    private List<RoadmapNodeImage> images;

    public RoadmapNode(final String title, final String content, final List<RoadmapNodeImage> images) {
        this(null, title, content, images);
    }

    public RoadmapNode(final Long id, final String title, final String content, final List<RoadmapNodeImage> images) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.images = images;
    }

    public void setRoadmapContent(final RoadmapContent roadmapContent) {
        if (Objects.nonNull(this.roadmapContent)) {
            roadmapContent.removeNode(this);
        }
        this.roadmapContent = roadmapContent;
    }
}
