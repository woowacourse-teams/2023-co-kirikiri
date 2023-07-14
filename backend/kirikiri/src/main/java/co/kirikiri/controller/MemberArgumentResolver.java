package co.kirikiri.controller;

import co.kirikiri.domain.member.Member;
import co.kirikiri.persistence.MemberRepository;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    public MemberArgumentResolver(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthPrincipal.class) &&
                parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        // TODO: 제거
        return memberRepository.findAll().get(0);
    }
}
