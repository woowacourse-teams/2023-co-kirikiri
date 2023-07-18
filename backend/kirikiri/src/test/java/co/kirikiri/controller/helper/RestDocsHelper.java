package co.kirikiri.controller.helper;

import co.kirikiri.controller.GlobalExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.UnsupportedEncodingException;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ActiveProfiles("test")
@Import(TestObjectMapperConfig.class)
@ExtendWith(RestDocumentationExtension.class)
public class RestDocsHelper {

    protected static final String API_PREFIX = "/api";
    protected static final String RESTRICT = "restrict";

    protected MockMvc mockMvc;
    protected RestDocumentationResultHandler documentationResultHandler;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

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

    protected <T> T jsonToClass(final MvcResult mvcResult, final TypeReference<T> typeReference) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), typeReference);
    }
}
