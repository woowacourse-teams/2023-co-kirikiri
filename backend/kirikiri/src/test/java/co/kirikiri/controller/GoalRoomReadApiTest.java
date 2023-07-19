package co.kirikiri.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.exception.UnauthorizedException;
import co.kirikiri.service.GoalRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@WebMvcTest(GoalRoomController.class)
class GoalRoomReadApiTest extends ControllerTestHelper {

    private static final String IDENTIFIER = "identifier1";
    private static final Long goalRoomId = 1L;

    @MockBean
    private GoalRoomService goalRoomService;

    @Test
    void 골룸_참가_요청을_성공한다() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", goalRoomId)
                        .header("Authorization", "Bearer <AccessToken>")
                        .contextPath(API_PREFIX))
                .andDo(documentationResultHandler.document(
                        pathParameters(
                                parameterWithName("goalRoomId").description("골룸 아이디")
                        )))
                .andExpect(status().isOk());
    }

    @Test
    void 비로그인_상태에서_골룸_참가_요청은_실패한다() throws Exception {
        //given
        doThrow(new UnauthorizedException("비로그인 상태에서는 골룸에 참여할 수 없습니다."))
                .when(goalRoomService)
                .join(anyString(), anyLong());

        //when
        //given
        mockMvc.perform(
                        post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", goalRoomId)
                                .header("Authorization", "Bearer <AccessToken>")
                                .content(MediaType.APPLICATION_JSON_VALUE)
                                .contextPath(API_PREFIX))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("비로그인 상태에서는 골룸에 참여할 수 없습니다."))
                .andDo(documentationResultHandler.document(
                        pathParameters(
                                parameterWithName("goalRoomId").description("골룸 아이디")
                        ),
                        responseFields(
                                fieldWithPath("message").description("예외 메세지")
                        )));
    }

    @Test
    void 존재하지_않는_골룸에_대한_참가_요청은_실패한다() throws Exception {
        //given
        doThrow(new NotFoundException("존재하지 않는 골룸입니다. roadmapId = 1"))
                .when(goalRoomService)
                .join(anyString(), anyLong());

        //when
        //given
        mockMvc.perform(
                        post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", goalRoomId)
                                .header("Authorization", "Bearer <AccessToken>")
                                .content(MediaType.APPLICATION_JSON_VALUE)
                                .contextPath(API_PREFIX))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 골룸입니다. roadmapId = 1"))
                .andDo(documentationResultHandler.document(
                        pathParameters(
                                parameterWithName("goalRoomId").description("골룸 아이디")
                        ),
                        responseFields(
                                fieldWithPath("message").description("예외 메세지")
                        )));
    }

    @Test
    void 이미_참여한_골룸에_대한_참가_요청은_실패한다() throws Exception {
        //given
        doThrow(new BadRequestException("이미 참가되어 있는 골룸입니다."))
                .when(goalRoomService)
                .join(anyString(), anyLong());

        //when
        //then
        mockMvc.perform(
                        post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", goalRoomId)
                                .header("Authorization", "Bearer <AccessToken>")
                                .content(MediaType.APPLICATION_JSON_VALUE)
                                .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 참가되어 있는 골룸입니다."))
                .andDo(documentationResultHandler.document(
                        pathParameters(
                                parameterWithName("goalRoomId").description("골룸 아이디")
                        ),
                        responseFields(
                                fieldWithPath("message").description("예외 메세지")
                        )));
    }

    @Test
    void 제한_인원이_가득_찬_골룸에_대한_참가_요청은_실패한다() throws Exception {
        //given
        doThrow(new BadRequestException("제한 인원이 가득 찬 골룸에는 참가할 수 없습니다."))
                .when(goalRoomService)
                .join(anyString(), anyLong());

        //when
        mockMvc.perform(
                        post(API_PREFIX + "/goal-rooms/{goalRoomId}/join", goalRoomId)
                                .header("Authorization", "Bearer <AccessToken>")
                                .content(MediaType.APPLICATION_JSON_VALUE)
                                .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("제한 인원이 가득 찬 골룸에는 참가할 수 없습니다."))
                .andDo(documentationResultHandler.document(
                        pathParameters(
                                parameterWithName("goalRoomId").description("골룸 아이디")
                        ),
                        responseFields(
                                fieldWithPath("message").description("예외 메세지")
                        )));
    }
}