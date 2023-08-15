package co.kirikiri.service;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.ImageDirType;
import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
import co.kirikiri.domain.goalroom.GoalRoomToDoCheck;
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
import co.kirikiri.persistence.goalroom.GoalRoomToDoCheckRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.dto.goalroom.GoalRoomCreateDto;
import co.kirikiri.service.dto.goalroom.GoalRoomRoadmapNodeDto;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private final FileService fileService;
    private final MemberRepository memberRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final GoalRoomToDoCheckRepository goalRoomToDoCheckRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
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
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵 노드입니다."));
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
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        goalRoom.join(member);
    }

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    @Transactional
    public Long addGoalRoomTodo(final Long goalRoomId, final String identifier,
                                final GoalRoomTodoRequest goalRoomTodoRequest) {
        final Member member = findMemberByIdentifier(identifier);
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        checkGoalRoomCompleted(goalRoom);
        checkGoalRoomLeader(member, goalRoom, "골룸의 리더만 투두리스트를 추가할 수 있습니다.");
        final GoalRoomToDo goalRoomToDo = GoalRoomMapper.convertToGoalRoomTodo(goalRoomTodoRequest);
        goalRoom.addGoalRoomTodo(goalRoomToDo);
        goalRoomRepository.save(goalRoom);
        return goalRoom.findLastGoalRoomTodo().getId();
    }

    private void checkGoalRoomCompleted(final GoalRoom goalRoom) {
        if (goalRoom.isCompleted()) {
            throw new BadRequestException("이미 종료된 골룸입니다.");
        }
    }

    private void checkGoalRoomLeader(final Member member, final GoalRoom goalRoom, final String errorMessage) {
        if (goalRoom.isNotLeader(member)) {
            throw new BadRequestException(errorMessage);
        }
    }

    public GoalRoomToDoCheckResponse checkGoalRoomTodo(final Long goalRoomId, final Long todoId,
                                                       final String identifier) {
        final Identifier memberIdentifier = new Identifier(identifier);
        final GoalRoom goalRoom = findGoalRoomWithTodos(goalRoomId);
        final GoalRoomToDo goalRoomToDo = findGoalRoomTodoById(todoId, goalRoom);
        final GoalRoomMember goalRoomMember = findGoalRoomMember(memberIdentifier, goalRoom);

        final boolean isAlreadyChecked = goalRoomToDoCheckRepository.findByGoalRoomIdAndTodoAndMemberIdentifier(
                goalRoomId, goalRoomToDo, memberIdentifier).isPresent();
        if (isAlreadyChecked) {
            goalRoomToDoCheckRepository.deleteByGoalRoomMemberAndToDoId(goalRoomMember, todoId);
            return new GoalRoomToDoCheckResponse(false);
        }
        final GoalRoomToDoCheck goalRoomToDoCheck = new GoalRoomToDoCheck(goalRoomMember, goalRoomToDo);
        goalRoomToDoCheckRepository.save(goalRoomToDoCheck);
        return new GoalRoomToDoCheckResponse(true);
    }

    private GoalRoom findGoalRoomWithTodos(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithTodos(goalRoomId)
                .orElseThrow(() -> new NotFoundException("골룸이 존재하지 않습니다. goalRoomId = " + goalRoomId));
    }

    private GoalRoomToDo findGoalRoomTodoById(final Long todoId, final GoalRoom goalRoom) {
        return goalRoom.findGoalRoomTodoByTodoId(todoId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 투두입니다. todoId = " + todoId));
    }

    private GoalRoomMember findGoalRoomMember(final Identifier memberIdentifier, final GoalRoom goalRoom) {
        return goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, memberIdentifier)
                .orElseThrow(() -> new NotFoundException(
                        "골룸에 사용자가 존재하지 않습니다. goalRoomId = " + goalRoom.getId() + " memberIdentifier = "
                                + memberIdentifier.getValue()));
    }

    @Transactional
    public String createCheckFeed(final String identifier, final Long goalRoomId,
                                  final CheckFeedRequest checkFeedRequest) {
        final MultipartFile checkFeedImage = checkFeedRequest.image();
        validateEmptyImage(checkFeedImage);
        final ImageContentType imageType = getImageContentType(checkFeedImage);

        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        final GoalRoomMember goalRoomMember = findGoalRoomMemberByGoalRoomAndIdentifier(goalRoom, identifier);
        final GoalRoomRoadmapNode currentNode = getNodeByDate(goalRoom);
        final int currentMemberCheckCount = checkFeedRepository.countByGoalRoomMemberAndGoalRoomRoadmapNode(
                goalRoomMember, currentNode);
        validateCheckCount(currentMemberCheckCount, goalRoomMember, currentNode);
        updateAccomplishmentRate(goalRoom, goalRoomMember, currentMemberCheckCount);

        try {
            final String imageUrl = fileService.uploadFileAndReturnPath(checkFeedImage, ImageDirType.CHECK_FEED,
                    goalRoomId);
            checkFeedRepository.save(new CheckFeed(imageUrl, imageType, checkFeedImage.getOriginalFilename(),
                    checkFeedRequest.description(), currentNode, goalRoomMember));
            return imageUrl;
        } catch (final IOException e) {
            throw new ServerException("이미지 업로드에 실패했습니다.");
        }
    }

    private void validateEmptyImage(final MultipartFile image) {
        if (image.isEmpty()) {
            throw new BadRequestException("인증 피드 등록 시 이미지가 반드시 포함되어야 합니다.");
        }

        if (image.getOriginalFilename() == null) {
            throw new BadRequestException("파일 이름은 반드시 포함되어야 합니다.");
        }
    }

    private ImageContentType getImageContentType(final MultipartFile checkFeedImage) {
        return ImageContentType.of(checkFeedImage.getContentType());
    }

    private GoalRoomMember findGoalRoomMemberByGoalRoomAndIdentifier(final GoalRoom goalRoom, final String identifier) {
        return goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, new Identifier(identifier))
                .orElseThrow(() -> new NotFoundException("골룸에 해당 사용자가 존재하지 않습니다. 사용자 아이디 = " + identifier));
    }

    private GoalRoomRoadmapNode getNodeByDate(final GoalRoom goalRoom) {
        return goalRoom.getNodeByDate(LocalDate.now())
                .orElseThrow(() -> new BadRequestException("인증 피드는 노드 기간 내에만 작성할 수 있습니다."));
    }

    private int validateCheckCount(final int memberCheckCount, final GoalRoomMember member,
                                   final GoalRoomRoadmapNode goalRoomRoadmapNode) {

        validateNodeCheckCount(memberCheckCount, goalRoomRoadmapNode);
        validateTodayCheckCount(member);
        return memberCheckCount;
    }

    private void validateNodeCheckCount(final int memberCheckCount,
                                        final GoalRoomRoadmapNode goalRoomRoadmapNode) {
        if (memberCheckCount >= goalRoomRoadmapNode.getCheckCount()) {
            throw new BadRequestException(
                    "이번 노드에는 최대 " + goalRoomRoadmapNode.getCheckCount() + "번만 인증 피드를 등록할 수 있습니다.");
        }
    }

    private void validateTodayCheckCount(final GoalRoomMember member) {
        final LocalDate today = LocalDate.now();
        final LocalDateTime todayStart = today.atStartOfDay();
        final LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        if (checkFeedRepository.findByGoalRoomMemberAndDateTime(member, todayStart, todayEnd).isPresent()) {
            throw new BadRequestException("이미 오늘 인증 피드를 등록하였습니다.");
        }
    }

    private void updateAccomplishmentRate(final GoalRoom goalRoom, final GoalRoomMember goalRoomMember,
                                          final int pastCheckCount) {
        final int wholeCheckCount = goalRoom.getAllCheckCount();
        final int memberCheckCount = pastCheckCount + 1;
        final Double accomplishmentRate = 100 * memberCheckCount / (double) wholeCheckCount;
        goalRoomMember.updateAccomplishmentRate(accomplishmentRate);
    }

    public void startGoalRoom(final String memberIdentifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(memberIdentifier);
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        checkGoalRoomLeader(member, goalRoom, "골룸의 리더만 골룸을 시작할 수 있습니다.");
        validateGoalRoomStart(goalRoom);
        final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoom.getGoalRoomPendingMembers().getValues();
        saveGoalRoomMemberFromPendingMembers(goalRoomPendingMembers);
        goalRoom.start();
    }

    private void validateGoalRoomStart(final GoalRoom goalRoom) {
        if (goalRoom.cannotStart()) {
            throw new BadRequestException("골룸의 시작 날짜가 되지 않았습니다.");
        }
    }

    private void saveGoalRoomMemberFromPendingMembers(final List<GoalRoomPendingMember> goalRoomPendingMembers) {
        final List<GoalRoomMember> goalRoomMembers = makeGoalRoomMembers(goalRoomPendingMembers);
        goalRoomMemberRepository.saveAll(goalRoomMembers);
        goalRoomPendingMemberRepository.deleteAll(goalRoomPendingMembers);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void startGoalRooms() {
        final List<GoalRoom> goalRoomsToStart = goalRoomRepository.findAllByStartDateNow();
        for (final GoalRoom goalRoom : goalRoomsToStart) {
            final List<GoalRoomPendingMember> pendingMembers = goalRoomPendingMemberRepository.findAllByGoalRoom(
                    goalRoom);
            saveGoalRoomMemberFromPendingMembers(pendingMembers);
            goalRoom.start();
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

    public void leave(final String identifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(identifier);
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        validateStatus(goalRoom);
        goalRoom.leave(member);
        if (goalRoom.isEmptyGoalRoom()) {
            goalRoomRepository.delete(goalRoom);
        }
    }

    private void validateStatus(final GoalRoom goalRoom) {
        if (goalRoom.isRunning()) {
            throw new BadRequestException("진행중인 골룸에서는 나갈 수 없습니다.");
        }
    }
}
