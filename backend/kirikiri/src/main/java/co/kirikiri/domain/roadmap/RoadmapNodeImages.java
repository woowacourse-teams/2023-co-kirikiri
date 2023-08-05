package co.kirikiri.domain.roadmap;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@NoArgsConstructor
public class RoadmapNodeImages {

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "roadmap_node_id", nullable = false, updatable = false)
    private final List<RoadmapNodeImage> values = new ArrayList<>();

    public RoadmapNodeImages(final List<RoadmapNodeImage> images) {
        this.values.addAll(new ArrayList<>(images));
    }

    public void addAll(final RoadmapNodeImages images) {
        this.values.addAll(new ArrayList<>(images.values));
    }

    public void add(final RoadmapNodeImage roadmapNodeImage) {
        this.values.add(roadmapNodeImage);
    }

    public List<RoadmapNodeImage> getValues() {
        return new ArrayList<>(values);
    }
}
