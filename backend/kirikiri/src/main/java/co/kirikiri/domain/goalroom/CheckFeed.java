package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseCreatedTimeEntity;
import co.kirikiri.domain.ImageContentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckFeed extends BaseCreatedTimeEntity {

    @Column(nullable = false)
    private String serverFilePath;

    @Enumerated(value = EnumType.STRING)
    private ImageContentType imageContentType;

    private String originalFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_room_roadmap_node_id", nullable = false)
    private GoalRoomRoadmapNode goalRoomRoadmapNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_room_member_id", nullable = false)
    private GoalRoomMember goalRoomMember;
}
