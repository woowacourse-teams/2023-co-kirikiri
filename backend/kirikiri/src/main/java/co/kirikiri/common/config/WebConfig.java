package co.kirikiri.common.config;

import co.kirikiri.common.interceptor.AuthInterceptor;
import co.kirikiri.common.resolver.MemberIdentifierArgumentResolver;
import co.kirikiri.roadmap.resolver.RoadmapSaveArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final MemberIdentifierArgumentResolver memberIdentifierArgumentResolver;
    private final RoadmapSaveArgumentResolver roadmapSaveArgumentResolver;

    @Override
    public void addInterceptors(final InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(authInterceptor);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(roadmapSaveArgumentResolver);
        argumentResolvers.add(memberIdentifierArgumentResolver);
    }
}
