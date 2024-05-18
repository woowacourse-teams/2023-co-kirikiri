package co.kirikiri.checkfeed.domain;

import co.kirikiri.common.entity.BaseCreatedTimeEntity;
import co.kirikiri.common.type.ImageContentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckFeed extends BaseCreatedTimeEntity {

    @Column(nullable = false)
    private String serverFilePath;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ImageContentType imageContentType;

    @Column(nullable = false)
    private String originalFileName;

    private String description;

    private Long goalRoomRoadmapNodeId;

    private Long goalRoomMemberId;

    public CheckFeed(final String serverFilePath, final ImageContentType imageContentType,
                     final String originalFileName, final String description,
                     final Long goalRoomRoadmapNodeId, final Long goalRoomMemberId) {
        this(serverFilePath, imageContentType, originalFileName, description, goalRoomRoadmapNodeId, goalRoomMemberId,
                null);
    }

    public CheckFeed(final String serverFilePath, final ImageContentType imageContentType,
                     final String originalFileName, final String description,
                     final Long goalRoomRoadmapNodeId, final Long goalRoomMemberId, final LocalDateTime createdAt) {
        this.serverFilePath = serverFilePath;
        this.imageContentType = imageContentType;
        this.originalFileName = originalFileName;
        this.description = description;
        this.goalRoomRoadmapNodeId = goalRoomRoadmapNodeId;
        this.goalRoomMemberId = goalRoomMemberId;
        this.createdAt = createdAt;
    }

    public String getServerFilePath() {
        return serverFilePath;
    }

    public String getDescription() {
        return description;
    }

    public Long getGoalRoomMemberId() {
        return goalRoomMemberId;
    }
}
