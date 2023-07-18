package co.kirikiri.controller;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.ImageContentType;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.domain.member.vo.EncryptedPassword;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import java.time.LocalDate;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthPrincipal.class) &&
                parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        // TODO: 제거
//        final MemberProfileImage profileImage = new MemberProfileImage(1L, "originalFileName", "serverFilePath",
//                ImageContentType.JPEG);
//        final MemberProfile profile = new MemberProfile(1L, Gender.FEMALE, LocalDate.of(1999, 6, 8),
//                new Nickname("nickname"), "01011112222", profileImage);
//        return new Member(1L, new Identifier("creator"), new EncryptedPassword(new Password("password1")), profile);
        final MemberProfileImage memberProfileImage = new MemberProfileImage("member-profile.png",
                "member-profile-save-path", ImageContentType.PNG);
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
                new Nickname("코끼리"), "010-1234-5678", memberProfileImage);
        return new Member(1L, new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), memberProfile);
    }
}
