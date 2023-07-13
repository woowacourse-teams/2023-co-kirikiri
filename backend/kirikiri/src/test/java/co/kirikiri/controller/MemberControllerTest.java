package co.kirikiri.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.RestDocsHelper;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.ConflictException;
import co.kirikiri.service.dto.member.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.member.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@WebMvcTest(MemberController.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
class MemberControllerTest extends RestDocsHelper {

    private final ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;


    MemberControllerTest(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Test
    void 정상적으로_회원가입에_성공한다() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("identifier1", "password1!",
            "nickname", "010-1234-5678", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        //then
        mockMvc.perform(post(API_PREFIX + "/members/join")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .contextPath(API_PREFIX))
            .andExpect(status().isCreated())
            .andDo(print());
    }

    @Test
    void 회원가입_시_아이디가_형식에_맞지않을때() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("identifier1@!#!@#", "password1!",
            "nickname", "010-1234-5678", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        doThrow(new BadRequestException("제약 조건에 맞지 않는 아이디입니다."))
            .when(memberService)
            .join(any());

        //then
        mockMvc.perform(post(API_PREFIX + "/members/join")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .contextPath(API_PREFIX))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void 회원가입_시_비밀번호가_형식에_맞지않을때() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("identifier1", "password1!₩",
            "nickname", "010-1234-5678", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        doThrow(new BadRequestException("제약 조건에 맞지 않는 비밀번호입니다."))
            .when(memberService)
            .join(any());

        //then
        mockMvc.perform(post(API_PREFIX + "/members/join")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .contextPath(API_PREFIX))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void 회원가입_시_닉네임이_형식에_맞지않을때() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("identifier1", "password1!",
            "a", "010-1234-5678", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        doThrow(new BadRequestException("제약 조건에 맞지 않는 닉네임입니다."))
            .when(memberService)
            .join(any());

        //then
        mockMvc.perform(post(API_PREFIX + "/members/join")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .contextPath(API_PREFIX))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void 회원가입_시_전화번호_형식에_맞지않을때() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("identifier1", "password1!",
            "nickname", "010-1234-56789", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        //then
        mockMvc.perform(post(API_PREFIX + "/members/join")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .contextPath(API_PREFIX))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void 회원가입_시_중복된_아이디일_때() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("identifier1", "password1!",
            "nickname", "010-1234-5678", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        doThrow(new ConflictException("이미 존재하는 아이디입니다."))
            .when(memberService)
            .join(any());

        //then
        mockMvc.perform(post(API_PREFIX + "/members/join")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .contextPath(API_PREFIX))
            .andExpect(status().isConflict())
            .andDo(print());
    }

    @Test
    void 회원가입_시_중복된_닉네임일_때() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("identifier1", "password1!",
            "nickname", "010-1234-5678", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        doThrow(new ConflictException("이미 존재하는 닉네임입니다."))
            .when(memberService)
            .join(any());

        //then
        mockMvc.perform(post(API_PREFIX + "/members/join")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .contextPath(API_PREFIX))
            .andExpect(status().isConflict())
            .andDo(print());
    }
}
