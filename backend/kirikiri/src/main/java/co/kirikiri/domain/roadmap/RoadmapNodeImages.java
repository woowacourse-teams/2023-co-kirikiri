package co.kirikiri.domain.roadmap;

import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class RoadmapNodeImages {

    private static final int MAX_SIZE = 2;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "roadmap_node_id", nullable = false, updatable = false)
    private final List<RoadmapNodeImage> values = new ArrayList<>();

    public RoadmapNodeImages(final List<RoadmapNodeImage> images) {
        validateSize(images);
        this.values.addAll(new ArrayList<>(images));
    }

    private void validateSize(final List<RoadmapNodeImage> images) {
        if (images.size() > MAX_SIZE) {
            throw new BadRequestException("한 로드맵 노드에 사진은 최대 2개까지 가능합니다.");
        }
    }

    public void addAll(final RoadmapNodeImages images) {
        this.values.addAll(new ArrayList<>(images.values));
        validateSize(this.values);
    }

    public void add(final RoadmapNodeImage roadmapNodeImage) {
        this.values.add(roadmapNodeImage);
        validateSize(this.values);
    }

    public List<RoadmapNodeImage> getValues() {
        return new ArrayList<>(values);
    }
}
