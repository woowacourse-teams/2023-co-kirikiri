package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.member.ImageContentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapNodeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String serverFilePath;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 10, nullable = false)
    private ImageContentType imageContentType;

    public RoadmapNodeImage(final String originalFileName, final String serverFilePath,
                            final ImageContentType imageContentType) {
        this(null, originalFileName, serverFilePath, imageContentType);
    }

    public RoadmapNodeImage(final Long id, final String originalFileName, final String serverFilePath,
                            final ImageContentType imageContentType) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.serverFilePath = serverFilePath;
        this.imageContentType = imageContentType;
    }

    public String getServerFilePath() {
        return serverFilePath;
    }
}
