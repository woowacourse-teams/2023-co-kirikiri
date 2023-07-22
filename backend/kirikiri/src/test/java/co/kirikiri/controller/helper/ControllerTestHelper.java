package co.kirikiri.controller.helper;

import co.kirikiri.common.interceptor.AuthInterceptor;
import co.kirikiri.common.resolver.MemberIdentifierArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ControllerTestHelper extends RestDocsHelper {

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

    protected List<FieldDescriptor> makeFieldDescriptor(final List<FieldDescriptionHelper.FieldDescription> descriptions) {
        return descriptions.stream()
                .map(FieldDescriptionHelper::getDescriptor)
                .toList();
    }
}
