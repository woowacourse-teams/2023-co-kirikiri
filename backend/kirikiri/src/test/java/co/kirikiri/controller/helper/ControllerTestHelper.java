package co.kirikiri.controller.helper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.kirikiri.common.interceptor.AuthInterceptor;
import co.kirikiri.common.resolver.MemberIdentifierArgumentResolver;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;

public class ControllerTestHelper extends RestDocsHelper {

    protected final String AUTHORIZATION = "Authorization";
    protected final String BEARER_TOKEN_FORMAT = "Bearer %s";

    @MockBean
    protected AuthInterceptor authInterceptor;

    @MockBean
    private MemberIdentifierArgumentResolver memberIdentifierArgumentResolver;

    @BeforeEach
    void setUp() {
        when(authInterceptor.preHandle(any(), any(), any()))
                .thenReturn(true);
        when(memberIdentifierArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn("Bearer Token");
        when(memberIdentifierArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
    }

    protected List<FieldDescriptor> makeFieldDescriptor(
            final List<FieldDescriptionHelper.FieldDescription> descriptions) {
        return descriptions.stream()
                .map(FieldDescriptionHelper::getDescriptor)
                .toList();
    }
}
