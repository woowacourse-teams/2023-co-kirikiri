package co.kirikiri.controller.helper;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ActiveProfiles("test")
@Import(TestObjectMapperConfig.class)
@ExtendWith(RestDocumentationExtension.class)
public class RestDocsHelper {

    protected static final String API_PREFIX = "/api";

    @Autowired
    protected ObjectMapper objectMapper;
    protected MockMvc mockMvc;
    protected RestDocumentationResultHandler documentationResultHandler;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext,
               final RestDocumentationContextProvider restDocumentationContextProvider) {
        this.documentationResultHandler = MockMvcRestDocumentation.document(
            "{class-name}/{method-name}",
            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));

        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .addFilter(new CharacterEncodingFilter("UTF-8", true))
            .alwaysDo(MockMvcResultHandlers.print())
            .alwaysDo(documentationResultHandler)
            .apply(documentationConfiguration(restDocumentationContextProvider))
            .build();
    }
}
