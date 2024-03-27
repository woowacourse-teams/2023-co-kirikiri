package co.kirikiri.member.service;

import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.ConflictException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.common.service.FileService;
import co.kirikiri.common.service.NumberGenerator;
import co.kirikiri.common.type.ImageContentType;
import co.kirikiri.member.domain.EncryptedPassword;
import co.kirikiri.member.domain.Gender;
import co.kirikiri.member.domain.Member;
import co.kirikiri.member.domain.MemberImage;
import co.kirikiri.member.domain.MemberProfile;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Nickname;
import co.kirikiri.member.persistence.MemberRepository;
import co.kirikiri.member.service.dto.MemberInformationDto;
import co.kirikiri.member.service.dto.MemberInformationForPublicDto;
import co.kirikiri.member.service.dto.MemberJoinDto;
import co.kirikiri.member.service.dto.OauthMemberJoinDto;
import co.kirikiri.member.service.dto.request.MemberJoinRequest;
import co.kirikiri.member.service.dto.response.MemberInformationForPublicResponse;
import co.kirikiri.member.service.dto.response.MemberInformationResponse;
import co.kirikiri.member.service.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URL;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@ExceptionConvert
public class MemberService {

    private static final String DEFAULT_ORIGINAL_FILE_NAME_PROPERTY = "image.default.originalFileName";
    private static final String DEFAULT_SERVER_FILE_PATH_PROPERTY = "image.default.serverFilePath";
    private static final String DEFAULT_IMAGE_CONTENT_TYPE_PROPERTY = "image.default.imageContentType";
    private static final String DEFAULT_EXTENSION = "image.default.extension";
    private static final int MAX_IDENTIFIER_LENGTH = 40;

    private final Environment environment;
    private final NumberGenerator numberGenerator;
    private final FileService fileService;
    private final MemberRepository memberRepository;

    @Transactional
    public Long join(final MemberJoinRequest memberJoinRequest) {
        final MemberJoinDto memberJoinDto = MemberMapper.convertToMemberJoinDto(memberJoinRequest);
        checkIdentifierDuplicate(memberJoinDto.identifier());

        final EncryptedPassword encryptedPassword = new EncryptedPassword(memberJoinDto.password());
        final MemberProfile memberProfile = new MemberProfile(memberJoinDto.gender(), memberJoinDto.email());
        final Member member = new Member(memberJoinDto.identifier(), encryptedPassword,
                memberJoinDto.nickname(), findDefaultMemberImage(), memberProfile);
        return memberRepository.save(member).getId();
    }

    private void checkIdentifierDuplicate(final Identifier identifier) {
        if (memberRepository.findByIdentifier(identifier).isPresent()) {
            throw new ConflictException("이미 존재하는 아이디입니다.");
        }
    }

    @Transactional
    public Member oauthJoin(final OauthMemberJoinDto oauthMemberJoinDto) {
        final MemberProfile memberProfile = new MemberProfile(Gender.valueOf(oauthMemberJoinDto.gender().name()),
                oauthMemberJoinDto.email());
        final Identifier identifier = makeIdentifier(oauthMemberJoinDto);
        final Nickname nickname = new Nickname(oauthMemberJoinDto.nickname());
        final Member member = new Member(identifier, oauthMemberJoinDto.oauthId(), nickname, findDefaultMemberImage(),
                memberProfile);
        return memberRepository.save(member);
    }

    private Identifier makeIdentifier(final OauthMemberJoinDto oauthMemberJoinDto) {
        final String originalIdentifier = oauthMemberJoinDto.email()
                .split("@")[0];
        final String uuid = UUID.randomUUID().toString();
        final String identifierWithUUID = originalIdentifier + uuid;
        if (identifierWithUUID.length() > MAX_IDENTIFIER_LENGTH) {
            return new Identifier(identifierWithUUID.substring(0, MAX_IDENTIFIER_LENGTH));
        }
        return new Identifier(identifierWithUUID);
    }

    private MemberImage findDefaultMemberImage() {
        final String defaultOriginalFileName = environment.getProperty(DEFAULT_ORIGINAL_FILE_NAME_PROPERTY);
        final String defaultServerFilePath = environment.getProperty(DEFAULT_SERVER_FILE_PATH_PROPERTY);
        final String defaultImageContentType = environment.getProperty(DEFAULT_IMAGE_CONTENT_TYPE_PROPERTY);
        final String defaultExtension = environment.getProperty(DEFAULT_EXTENSION);
        final int randomImageNumber = numberGenerator.generate();
        return new MemberImage(defaultOriginalFileName + randomImageNumber,
                defaultServerFilePath + randomImageNumber + defaultExtension,
                ImageContentType.valueOf(defaultImageContentType));
    }

    public MemberInformationResponse findMemberInformation(final String identifier) {
        final Member memberWithInfo = findMemberInformationByIdentifier(identifier);
        final MemberInformationDto memberInformationDto = makeMemberInformationDto(memberWithInfo);
        return MemberMapper.convertToMemberInformationResponse(memberInformationDto);
    }

    private MemberInformationDto makeMemberInformationDto(final Member member) {
        final MemberImage memberImage = member.getImage();
        final MemberProfile memberProfile = member.getMemberProfile();
        final URL imageUrl = fileService.generateUrl(memberImage.getServerFilePath(), HttpMethod.GET);
        return new MemberInformationDto(member.getId(), member.getNickname().getValue(),
                imageUrl.toExternalForm(), memberProfile.getGender().name(), member.getIdentifier().getValue(),
                memberProfile.getEmail());
    }

    private Member findMemberInformationByIdentifier(final String identifier) {
        return memberRepository.findWithMemberProfileAndImageByIdentifier(identifier)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    public MemberInformationForPublicResponse findMemberInformationForPublic(final Long memberId) {
        final Member memberWithPublicInfo = findMemberInformationByMemberId(memberId);
        final URL memberimageURl = fileService.generateUrl(memberWithPublicInfo.getImage().getServerFilePath(),
                HttpMethod.GET);
        final MemberInformationForPublicDto memberInformationForPublicDto =
                new MemberInformationForPublicDto(memberWithPublicInfo.getNickname().getValue(),
                        memberimageURl.toExternalForm(),
                        memberWithPublicInfo.getMemberProfile().getGender().name());
        return MemberMapper.convertToMemberInformationForPublicResponse(memberInformationForPublicDto);
    }

    private Member findMemberInformationByMemberId(final Long memberId) {
        return memberRepository.findWithMemberProfileAndImageById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다. memberId = " + memberId));
    }
}
