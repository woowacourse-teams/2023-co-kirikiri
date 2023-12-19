package co.kirikiri.service.goalroom;

import co.kirikiri.domain.ImageContentType;
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
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapNode;
import co.kirikiri.persistence.goalroom.CheckFeedRepository;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.GoalRoomToDoCheckRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.roadmap.persistence.RoadmapContentRepository;
import co.kirikiri.service.FilePathGenerator;
import co.kirikiri.service.FileService;
import co.kirikiri.service.ImageDirType;
import co.kirikiri.service.aop.ExceptionConvert;
import co.kirikiri.service.dto.FileInformation;
import co.kirikiri.service.dto.goalroom.GoalRoomCreateDto;
import co.kirikiri.service.dto.goalroom.GoalRoomRoadmapNodeDto;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import co.kirikiri.service.exception.BadRequestException;
import co.kirikiri.service.exception.NotFoundException;
import co.kirikiri.service.mapper.GoalRoomMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class GoalRoomCreateService {

    private final FileService fileService;
    private final FilePathGenerator filePathGenerator;
    private final MemberRepository memberRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final GoalRoomToDoCheckRepository goalRoomToDoCheckRepository;
    private final CheckFeedRepository checkFeedRepository;

    public Long create(final GoalRoomCreateRequest goalRoomCreateRequest, final String memberIdentifier) {
        final GoalRoomCreateDto goalRoomCreateDto = GoalRoomMapper.convertToGoalRoomCreateDto(goalRoomCreateRequest);
        final RoadmapContent roadmapContent = findRoadmapContentById(goalRoomCreateDto.roadmapContentId());
        validateDeletedRoadmap(roadmapContent);
        validateNodeSizeEqual(roadmapContent.nodesSize(), goalRoomCreateDto.goalRoomRoadmapNodeDtosSize());
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = makeGoalRoomRoadmapNodes(
                goalRoomCreateDto.goalRoomRoadmapNodeDtos(), roadmapContent);
        final Member leader = findMemberByIdentifier(memberIdentifier);

        final GoalRoom goalRoom = new GoalRoom(goalRoomCreateDto.goalRoomName(), goalRoomCreateDto.limitedMemberCount(),
                roadmapContent, leader);
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        return goalRoomRepository.save(goalRoom).getId();
    }

    private RoadmapContent findRoadmapContentById(final Long roadmapContentId) {
        return roadmapContentRepository.findByIdWithRoadmap(roadmapContentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다."));
    }

    private void validateDeletedRoadmap(final RoadmapContent roadmapContent) {
        final Roadmap roadmap = roadmapContent.getRoadmap();
        if (roadmap.isDeleted()) {
            throw new BadRequestException("삭제된 로드맵에 대해 골룸을 생성할 수 없습니다.");
        }
    }

    private void validateNodeSizeEqual(final int roadmapNodesSize, final int goalRoomRoadmapNodeDtosSize) {
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
        final GoalRoom goalRoom = findGoalRoomByIdWithPessimisticLock(goalRoomId);
        goalRoom.join(member);
    }

    private GoalRoom findGoalRoomByIdWithPessimisticLock(final Long goalRoomId) {
        return goalRoomRepository.findGoalRoomByIdWithPessimisticLock(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

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

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
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

    public String createCheckFeed(final String identifier, final Long goalRoomId,
                                  final CheckFeedRequest checkFeedRequest) {
        final MultipartFile checkFeedImage = checkFeedRequest.image();
        validateEmptyImage(checkFeedImage);
        final FileInformation fileInformation = GoalRoomMapper.convertToFileInformation(checkFeedImage);

        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        final GoalRoomMember goalRoomMember = findGoalRoomMemberByGoalRoomAndIdentifier(goalRoom, identifier);
        final GoalRoomRoadmapNode currentNode = getNodeByDate(goalRoom);
        final int currentMemberCheckCount = checkFeedRepository.countByGoalRoomMemberAndGoalRoomRoadmapNode(
                goalRoomMember, currentNode);
        validateCheckCount(currentMemberCheckCount, goalRoomMember, currentNode);
        updateAccomplishmentRate(goalRoom, goalRoomMember, currentMemberCheckCount);

        final String path = filePathGenerator.makeFilePath(ImageDirType.CHECK_FEED,
                fileInformation.originalFileName());
        saveCheckFeed(checkFeedRequest, checkFeedImage, goalRoomMember, currentNode, path);
        fileService.save(path, fileInformation);
        return fileService.generateUrl(path, HttpMethod.GET).toExternalForm();
    }

    private void validateEmptyImage(final MultipartFile image) {
        if (image.isEmpty()) {
            throw new BadRequestException("인증 피드 등록 시 이미지가 반드시 포함되어야 합니다.");
        }

        if (image.getOriginalFilename() == null) {
            throw new BadRequestException("파일 이름은 반드시 포함되어야 합니다.");
        }
    }

    private GoalRoomMember findGoalRoomMemberByGoalRoomAndIdentifier(final GoalRoom goalRoom, final String identifier) {
        return goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, new Identifier(identifier))
                .orElseThrow(() -> new NotFoundException("골룸에 해당 사용자가 존재하지 않습니다. 사용자 아이디 = " + identifier));
    }

    private GoalRoomRoadmapNode getNodeByDate(final GoalRoom goalRoom) {
        return goalRoom.findNodeByDate(LocalDate.now())
                .orElseThrow(() -> new BadRequestException("인증 피드는 노드 기간 내에만 작성할 수 있습니다."));
    }

    private void validateCheckCount(final int memberCheckCount, final GoalRoomMember member,
                                    final GoalRoomRoadmapNode goalRoomRoadmapNode) {
        validateNodeCheckCount(memberCheckCount, goalRoomRoadmapNode);
        validateTodayCheckCount(member);
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

    private void saveCheckFeed(final CheckFeedRequest checkFeedRequest, final MultipartFile checkFeedImage,
                               final GoalRoomMember goalRoomMember, final GoalRoomRoadmapNode currentNode,
                               final String path) {
        checkFeedRepository.save(
                new CheckFeed(path, ImageContentType.findImageContentType(checkFeedImage.getContentType()),
                        checkFeedImage.getOriginalFilename(),
                        checkFeedRequest.description(), currentNode, goalRoomMember));
    }

    public void startGoalRoom(final String memberIdentifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(memberIdentifier);
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        checkGoalRoomLeader(member, goalRoom, "골룸의 리더만 골룸을 시작할 수 있습니다.");
        validateGoalRoomStart(goalRoom);
        final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoom.getGoalRoomPendingMembers().getValues();
        saveGoalRoomMemberFromPendingMembers(goalRoomPendingMembers, goalRoom);
        goalRoom.start();
    }

    private void validateGoalRoomStart(final GoalRoom goalRoom) {
        if (goalRoom.cannotStart()) {
            throw new BadRequestException("골룸의 시작 날짜가 되지 않았습니다.");
        }
    }

    private void saveGoalRoomMemberFromPendingMembers(final List<GoalRoomPendingMember> goalRoomPendingMembers,
                                                      final GoalRoom goalRoom) {
        final List<GoalRoomMember> goalRoomMembers = makeGoalRoomMembers(goalRoomPendingMembers);
        goalRoom.addAllGoalRoomMembers(goalRoomMembers);
        goalRoom.deleteAllPendingMembers();
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
