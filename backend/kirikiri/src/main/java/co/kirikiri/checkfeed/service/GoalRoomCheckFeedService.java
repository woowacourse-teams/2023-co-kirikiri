package co.kirikiri.checkfeed.service;

import co.kirikiri.checkfeed.domain.CheckFeed;
import co.kirikiri.checkfeed.persistence.CheckFeedRepository;
import co.kirikiri.checkfeed.service.dto.CheckFeedDto;
import co.kirikiri.checkfeed.service.dto.CheckFeedMemberDto;
import co.kirikiri.checkfeed.service.dto.GoalRoomCheckFeedDto;
import co.kirikiri.checkfeed.service.dto.request.CheckFeedRequest;
import co.kirikiri.checkfeed.service.dto.response.GoalRoomCheckFeedResponse;
import co.kirikiri.checkfeed.service.mapper.CheckFeedMapper;
import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.ForbiddenException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.common.service.FilePathGenerator;
import co.kirikiri.common.service.FileService;
import co.kirikiri.common.service.dto.FileInformation;
import co.kirikiri.common.type.ImageContentType;
import co.kirikiri.common.type.ImageDirType;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNode;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class GoalRoomCheckFeedService {

    private final CheckFeedRepository checkFeedRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;
    private final FilePathGenerator filePathGenerator;

    public String createCheckFeed(final String identifier, final Long goalRoomId,
                                  final CheckFeedRequest checkFeedRequest) {
        final MultipartFile checkFeedImage = checkFeedRequest.image();
        validateEmptyImage(checkFeedImage);

        final GoalRoom goalRoom = findGoalRoomWithNodesById(goalRoomId);
        final GoalRoomMember goalRoomMember = findGoalRoomMemberByGoalRoomAndIdentifier(goalRoom, identifier);
        final GoalRoomRoadmapNode currentNode = getNodeByDate(goalRoom);
        final int currentMemberCheckCount = checkFeedRepository.countByGoalRoomMemberIdAndGoalRoomRoadmapNodeId(
                goalRoomMember.getId(), currentNode.getId());
        validateCheckCount(currentMemberCheckCount, goalRoomMember, currentNode);
        updateAccomplishmentRate(goalRoom, goalRoomMember, currentMemberCheckCount);

        final String path = filePathGenerator.makeFilePath(ImageDirType.CHECK_FEED,
                checkFeedImage.getOriginalFilename());
        saveCheckFeed(checkFeedRequest, checkFeedImage, goalRoomMember, currentNode, path);

        final FileInformation fileInformation = FileInformation.from(checkFeedImage);
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

    private GoalRoom findGoalRoomWithNodesById(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithNodes(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    private GoalRoomMember findGoalRoomMemberByGoalRoomAndIdentifier(final GoalRoom goalRoom, final String identifier) {
        final Member member = findMemberByIdentifier(identifier);
        return goalRoomMemberRepository.findByGoalRoomAndMemberId(goalRoom, member.getId())
                .orElseThrow(() -> new NotFoundException("골룸에 해당 사용자가 존재하지 않습니다. 사용자 아이디 = " + identifier));
    }

    private Member findMemberByIdentifier(final String identifier) {
        return memberRepository.findByIdentifier(new Identifier(identifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다. identifier = " + identifier));
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

    private void validateTodayCheckCount(final GoalRoomMember goalRoomMember) {
        final LocalDate today = LocalDate.now();
        final LocalDateTime todayStart = today.atStartOfDay();
        final LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        if (checkFeedRepository.findByGoalRoomMemberIdAndDateTime(goalRoomMember.getId(), todayStart, todayEnd)
                .isPresent()) {
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
                        checkFeedImage.getOriginalFilename(), checkFeedRequest.description(), currentNode.getId(),
                        goalRoomMember.getId()));
    }

    @Transactional(readOnly = true)
    public List<GoalRoomCheckFeedResponse> findGoalRoomCheckFeeds(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomWithNodesById(goalRoomId);
        validateJoinedMemberInRunningGoalRoom(goalRoom, identifier);
        final Optional<GoalRoomRoadmapNode> currentGoalRoomRoadmapNode = findCurrentGoalRoomNode(goalRoom);
        final List<CheckFeed> checkFeeds = findCheckFeedsByGoalRoomRoadmapNode(goalRoom, currentGoalRoomRoadmapNode);
        final List<GoalRoomCheckFeedDto> goalRoomCheckFeedDtos = makeGoalRoomCheckFeedDtos(checkFeeds);
        return CheckFeedMapper.convertToGoalRoomCheckFeedResponses(goalRoomCheckFeedDtos);
    }

    private void validateJoinedMemberInRunningGoalRoom(final GoalRoom goalRoom, final String identifier) {
        final Member member = findMemberByIdentifier(identifier);
        if (goalRoomMemberRepository.findByGoalRoomAndMemberId(goalRoom, member.getId())
                .isEmpty()) {
            throw new ForbiddenException("골룸에 참여하지 않은 회원입니다.");
        }
    }

    private Optional<GoalRoomRoadmapNode> findCurrentGoalRoomNode(final GoalRoom goalRoom) {
        return goalRoom.findNodeByDate(LocalDate.now());
    }

    private List<CheckFeed> findCheckFeedsByGoalRoomRoadmapNode(final GoalRoom goalRoom,
                                                                final Optional<GoalRoomRoadmapNode> currentGoalRoomRoadmapNode) {
        if (goalRoom.isCompleted()) {
            return checkFeedRepository.findByGoalRoomIdOrderByCreatedAtDesc(goalRoom.getId());
        }
        if (goalRoom.isRunning() && currentGoalRoomRoadmapNode.isPresent()) {
            return checkFeedRepository.findByGoalRoomRoadmapNodeIdOrderByCreatedAtDesc(
                    currentGoalRoomRoadmapNode.get().getId());
        }
        return Collections.emptyList();
    }

    private List<GoalRoomCheckFeedDto> makeGoalRoomCheckFeedDtos(final List<CheckFeed> checkFeeds) {
        return checkFeeds.stream()
                .map(this::makeGoalRoomCheckFeedDto)
                .toList();
    }

    private GoalRoomCheckFeedDto makeGoalRoomCheckFeedDto(final CheckFeed checkFeed) {
        final GoalRoomMember goalRoomMember = findGoalRoomMemberById(checkFeed.getGoalRoomMemberId());
        final Member member = findMemberWithProfileAndImageById(goalRoomMember.getMemberId());

        final URL memberImageUrl = fileService.generateUrl(member.getImage().getServerFilePath(), HttpMethod.GET);

        return new GoalRoomCheckFeedDto(new CheckFeedMemberDto(member.getId(), member.getNickname().getValue(),
                memberImageUrl.toExternalForm()), makeCheckFeedDto(checkFeed));
    }

    private GoalRoomMember findGoalRoomMemberById(final Long goalRoomMemberId) {
        return goalRoomMemberRepository.findById(goalRoomMemberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸 멤버입니다. goalRoomMemberId = " + goalRoomMemberId));
    }

    private Member findMemberWithProfileAndImageById(final Long memberId) {
        return memberRepository.findWithMemberProfileAndImageById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다. memberId = " + memberId));
    }

    private CheckFeedDto makeCheckFeedDto(final CheckFeed checkFeed) {
        final URL checkFeedImageUrl = fileService.generateUrl(checkFeed.getServerFilePath(), HttpMethod.GET);
        return new CheckFeedDto(checkFeed.getId(), checkFeedImageUrl.toExternalForm(),
                checkFeed.getDescription(), checkFeed.getCreatedAt());
    }
}
