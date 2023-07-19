package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.ConflictException;
import co.kirikiri.service.AuthService;
import co.kirikiri.service.MemberService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.member.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends ControllerTestHelper {

    @MockBean
    private MemberService memberService;
    @MockBean
    private AuthService authService;

    @Test
    void 정상적으로_회원가입에_성공한다() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("identifier1", "password1!",
                "nickname", "010-1234-5678", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        //then
        회원가입(jsonRequest, status().isCreated())
                .andDo(
                        documentationResultHandler.document(
                                requestFields(
                                        fieldWithPath("identifier").description("사용자 아이디")
                                                .attributes(new Attributes.Attribute(RESTRICT,
                                                        "- 길이 : 4 ~ 20  +" + "\n" +
                                                                "- 영어 소문자, 숫자 가능")),
                                        fieldWithPath("password").description("사용자 비밀번호")
                                                .attributes(new Attributes.Attribute(RESTRICT,
                                                        "- 길이 : 8 ~ 15  +" + "\n" +
                                                                "- 영어 소문자, 숫자, 특수문자  +" + "\n" +
                                                                "- 특수문자[!,@,#,$,%,^,&,*,(,),~] 사용 가능")),
                                        fieldWithPath("nickname").description("회원 닉네임")
                                                .attributes(new Attributes.Attribute(RESTRICT, "- 길이 : 2 ~ 8")),
                                        fieldWithPath("phoneNumber").description("회원 휴대폰 번호")
                                                .attributes(new Attributes.Attribute(RESTRICT,
                                                        "- 길이 : 13  +" + "\n" +
                                                                "- 번호 형식 : 010-xxxx-xxxx")),
                                        fieldWithPath("genderType").description("회원 성별")
                                                .attributes(new Attributes.Attribute(RESTRICT,
                                                        "- 길이 : 4 , 6  +" + "\n" +
                                                                "- MALE, FEMALE")),
                                        fieldWithPath("birthday").description("회원 생년월일")
                                                .attributes(new Attributes.Attribute(RESTRICT,
                                                        "- 길이 : 6  +" + "\n" +
                                                                "- yyMMdd"))
                                )
                        )
                );
    }

    private ResultActions 회원가입(final String jsonRequest, final ResultMatcher result) throws Exception {
        return mockMvc.perform(post(API_PREFIX + "/members/join")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(result)
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
        회원가입(jsonRequest, status().isBadRequest());
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
        회원가입(jsonRequest, status().isBadRequest());
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
        회원가입(jsonRequest, status().isBadRequest());
    }

    @Test
    void 회원가입_시_전화번호_형식에_맞지않을때() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("identifier1", "password1!",
                "nickname", "010-1234-56789", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        //then
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/members/join")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        final ErrorResponse expectedResponse = new ErrorResponse("전화번호 형식이 맞지 않습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison().isEqualTo(List.of(expectedResponse));
    }

    @Test
    void 회원가입_시_아이디에_빈값이_들어올_때() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("", "password1!",
                "nickname", "010-1234-5678", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        //then
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/members/join")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        final ErrorResponse expectedResponse = new ErrorResponse("아이디는 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison().isEqualTo(List.of(expectedResponse));
    }

    @Test
    void 회원가입_시_비밀번호에_빈값이_들어올_때() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("identifier", "",
                "nickname", "010-1234-5678", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        //then
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/members/join")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        final ErrorResponse expectedResponse = new ErrorResponse("비밀번호는 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison().isEqualTo(List.of(expectedResponse));
    }

    @Test
    void 회원가입_시_닉네임에_빈값이_들어올_때() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("identifier", "identifier1!",
                "", "010-1234-5678", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        //then
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/members/join")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        final ErrorResponse expectedResponse = new ErrorResponse("닉네임은 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison().isEqualTo(List.of(expectedResponse));
    }

    @Test
    void 회원가입_시_전화번호에_빈값이_들어올_때() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("identifier", "password1!",
                "nickname", "", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        //then
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/members/join")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        final ErrorResponse blankResponse = new ErrorResponse("전화번호는 빈 값일 수 없습니다.");
        final ErrorResponse patternResponse = new ErrorResponse("전화번호 형식이 맞지 않습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(patternResponse, blankResponse));
    }

    @Test
    void 회원가입_시_아이디_비밀번호_닉네임_전화번호_필드에_빈값이_들어올_때() throws Exception {
        //given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest("", "",
                "", "", GenderType.MALE, LocalDate.now());
        final String jsonRequest = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        //then
        final MvcResult mvcResult = mockMvc.perform(post(API_PREFIX + "/members/join")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        final ErrorResponse identifierResponse = new ErrorResponse("아이디는 빈 값일 수 없습니다.");
        final ErrorResponse passwordResponse = new ErrorResponse("비밀번호는 빈 값일 수 없습니다.");
        final ErrorResponse nicknameResponse = new ErrorResponse("닉네임은 빈 값일 수 없습니다.");
        final ErrorResponse phoneNumberBlankResponse = new ErrorResponse("전화번호는 빈 값일 수 없습니다.");
        final ErrorResponse phoneNumberPatternResponse = new ErrorResponse("전화번호 형식이 맞지 않습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(identifierResponse, passwordResponse, nicknameResponse, phoneNumberBlankResponse,
                        phoneNumberPatternResponse));
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
        회원가입(jsonRequest, status().isConflict());
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
        회원가입(jsonRequest, status().isConflict());
    }
}