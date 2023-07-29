package co.kirikiri.service;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.exception.ServerException;
import co.kirikiri.persistence.goalroom.CheckFeedRepository;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.dto.goalroom.GoalRoomCreateDto;
import co.kirikiri.service.dto.goalroom.GoalRoomRoadmapNodeDto;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.mapper.GoalRoomMapper;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class GoalRoomCreateService {

    private final MemberRepository memberRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final CheckFeedRepository checkFeedRepository;

    public Long create(final GoalRoomCreateRequest goalRoomCreateRequest, final String memberIdentifier) {
        final GoalRoomCreateDto goalRoomCreateDto = GoalRoomMapper.convertToGoalRoomCreateDto(goalRoomCreateRequest);
        final RoadmapContent roadmapContent = findRoadmapContentById(goalRoomCreateDto.roadmapContentId());
        checkNodeSizeEqual(roadmapContent.nodesSize(), goalRoomCreateDto.goalRoomRoadmapNodeDtosSize());
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = makeGoalRoomRoadmapNodes(
                goalRoomCreateDto.goalRoomRoadmapNodeDtos(), roadmapContent);
        final Member leader = findMemberByIdentifier(memberIdentifier);

        final GoalRoom goalRoom = new GoalRoom(goalRoomCreateDto.goalRoomName(), goalRoomCreateDto.limitedMemberCount(),
                roadmapContent, leader);
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        goalRoom.addGoalRoomTodo(goalRoomCreateDto.goalRoomToDo());
        return goalRoomRepository.save(goalRoom).getId();
    }

    private RoadmapContent findRoadmapContentById(final Long roadmapContentId) {
        return roadmapContentRepository.findById(roadmapContentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다."));
    }

    private void checkNodeSizeEqual(final int roadmapNodesSize, final int goalRoomRoadmapNodeDtosSize) {
        if (roadmapNodesSize != goalRoomRoadmapNodeDtosSize) {
            throw new BadRequestException("모든 노드에 대해 기간이 설정돼야 합니다.");
        }
    }

    private GoalRoomRoadmapNodes makeGoalRoomRoadmapNodes(final List<GoalRoomRoadmapNodeDto> goalRoomRoadmapNodeDtos,
                                                          final RoadmapContent roadmapContent) {
        final List<GoalRoomRoadmapNode> goalRoomRoadmapNodes = goalRoomRoadmapNodeDtos.stream()
                .map(it -> makeGoalRoomRoadmapNode(roadmapContent, it))
                .toList();
        return new GoalRoomRoadmapNodes(goalRoomRoadmapNodes);
    }

    private GoalRoomRoadmapNode makeGoalRoomRoadmapNode(final RoadmapContent roadmapContent,
                                                        final GoalRoomRoadmapNodeDto it) {
        return new GoalRoomRoadmapNode(new Period(it.startDate(), it.endDate()), it.checkCount(),
                findRoadmapNode(roadmapContent, it.roadmapNodeId()));
    }

    private RoadmapNode findRoadmapNode(final RoadmapContent roadmapContent, final Long roadmapNodeId) {
        return roadmapContent.findRoadmapNodeById(roadmapNodeId)
                .orElseThrow(() -> new NotFoundException("로드맵에 존재하지 않는 노드입니다."));
    }

    private Member findMemberByIdentifier(final String memberIdentifier) {
        return memberRepository.findByIdentifier(new Identifier(memberIdentifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    public void join(final String identifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(identifier);
        final GoalRoom goalRoom = findById(goalRoomId);
        goalRoom.join(member);
    }

    private GoalRoom findById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    @Transactional
    public String createCheckFeed(final String identifier, final Long goalRoomId,
                                  final CheckFeedRequest checkFeedRequest) {
        validateEmptyImage(checkFeedRequest.image());
        final GoalRoom goalRoom = findById(goalRoomId);
        final GoalRoomMember goalRoomMember = findGoalRoomMemberByGoalRoomAndIdentifier(goalRoom, identifier);
        final GoalRoomRoadmapNode currentNode = goalRoom.getNodeByDate(LocalDate.now());
        validateCheckCount(goalRoomMember, currentNode);

        try {
            final String serverFilePath = uploadFileAndReturnAddress(checkFeedRequest, goalRoomMember, currentNode);
            updateAccomplishmentRate(goalRoom, goalRoomMember);
            return serverFilePath;
        } catch (final IOException e) {
            e.printStackTrace();
            throw new ServerException("이미지 업로드에 실패했습니다.");
        }
    }

    private String uploadFileAndReturnAddress(final CheckFeedRequest checkFeedRequest,
                                              final GoalRoomMember goalRoomMember,
                                              final GoalRoomRoadmapNode currentNode) throws IOException {
        // TODO : 이미지가 저장될 경로는 반드시 추후에 다시 확인
        final String uploadFilePath = "C:/";
        final MultipartFile checkFeedImage = checkFeedRequest.image();
        final String fileName = System.currentTimeMillis() + "_" + checkFeedImage.getOriginalFilename();
        final String serverFilePath = uploadFilePath + fileName;
        final ImageContentType imageType = getImageContentType(checkFeedImage);
        final File dest = new File(serverFilePath);
        checkFeedImage.transferTo(dest);

        final CheckFeed checkFeed = checkFeedRepository.save(
                new CheckFeed(serverFilePath, imageType,
                        checkFeedImage.getOriginalFilename(), currentNode, goalRoomMember));
        checkFeed.addDescription(checkFeedRequest.description());
        return serverFilePath;
    }

    private void updateAccomplishmentRate(final GoalRoom goalRoom, final GoalRoomMember goalRoomMember) {
        final int wholeCheckCount = goalRoom.getAllCheckCount();
        final int memberCheckCount = checkFeedRepository.findCountByGoalRoomMember(goalRoomMember);
        final Double accomplishmentRate = 100 * memberCheckCount / (double) wholeCheckCount;
        goalRoomMember.updateAccomplishmentRate(accomplishmentRate);
    }

    private void validateEmptyImage(final MultipartFile image) {
        if (image.isEmpty()) {
            throw new BadRequestException("인증 피드 등록 시 이미지가 반드시 포함되어야 합니다.");
        }
    }

    private GoalRoomMember findGoalRoomMemberByGoalRoomAndIdentifier(final GoalRoom goalRoom, final String identifier) {
        return goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, new Identifier(identifier))
                .orElseThrow(() -> new NotFoundException("골룸에 해당 사용자가 존재하지 않습니다. 사용자 아이디 = " + identifier));
    }

    private void validateCheckCount(final GoalRoomMember member, final GoalRoomRoadmapNode goalRoomRoadmapNode) {
        if (checkFeedRepository.findCountByGoalRoomMemberAndGoalRoomRoadmapNode(member, goalRoomRoadmapNode)
                >= goalRoomRoadmapNode.getCheckCount()) {
            throw new BadRequestException(
                    "이번 노드에는 최대 " + goalRoomRoadmapNode.getCheckCount() + "번만 인증 피드를 등록할 수 있습니다.");
        }

        if (checkFeedRepository.isMemberUploadCheckFeedToday(member, goalRoomRoadmapNode,
                LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay())) {
            throw new BadRequestException("이미 오늘 인증 피드를 등록하였습니다.");
        }
    }

    private ImageContentType getImageContentType(final MultipartFile checkFeedImage) {
        return ImageContentType.of(checkFeedImage.getContentType())
                .orElseThrow(() -> new BadRequestException(checkFeedImage.getContentType()
                        + "는 요청할 수 없는 파일 확장자 형식입니다."));
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void startGoalRooms() {
        final List<GoalRoom> goalRoomsToStart = goalRoomRepository.findAllByStartDateWithGoalRoomRoadmapNode();
        for (final GoalRoom goalRoom : goalRoomsToStart) {
            final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoomPendingMemberRepository.findAllByGoalRoom(
                    goalRoom);
            final List<GoalRoomMember> goalRoomMembers = makeGoalRoomMembers(goalRoomPendingMembers);
            goalRoomMemberRepository.saveAll(goalRoomMembers);
            goalRoomPendingMemberRepository.deleteAll(goalRoomPendingMembers);
            goalRoom.updateStatus(GoalRoomStatus.RUNNING);
        }
    }

    private List<GoalRoomMember> makeGoalRoomMembers(final List<GoalRoomPendingMember> goalRoomPendingMembers) {
        return goalRoomPendingMembers.stream()
                .map(this::makeGoalRoomMember)
                .toList();
    }

    private GoalRoomMember makeGoalRoomMember(final GoalRoomPendingMember goalRoomPendingMember) {
        return new GoalRoomMember(goalRoomPendingMember.getRole(),
                goalRoomPendingMember.getJoinedAt(), goalRoomPendingMember.getGoalRoom(),
                goalRoomPendingMember.getMember());
    }
}