package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.ImageContentType;
import jakarta.persistence.*;
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
}
