//package com.example.todo.api.user;
//
//import com.example.todo.domain.entity.user.User;
//import com.example.todo.domain.repository.user.UserRepository;
//import com.example.todo.dto.user.request.UserJoinRequestDto;
//import com.example.todo.dto.user.request.UserLoginRequestDto;
//import com.example.todo.dto.user.request.UserUpdateRequestDto;
//import com.example.todo.service.user.UserService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//
//import java.util.ArrayList;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
//import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
//import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
//import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
////@ExtendWith({RestDocumentationExtension.class})
//@AutoConfigureMockMvc
//@AutoConfigureRestDocs
//@Transactional
//@SpringBootTest
//class UserApiControllerTest {
//
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    UserService userService;
//
//    @Autowired
//    UserRepository userRepository;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    private Authentication authentication;
//
////    @BeforeEach
////    void setUp(RestDocumentationContextProvider restDocumentation) {
////        mvc = MockMvcBuilders
////                .webAppContextSetup(context)
////                .apply(documentationConfiguration(restDocumentation)
////                        .operationPreprocessors()
////                        .withRequestDefaults(prettyPrint())
////                        .withResponseDefaults(prettyPrint()))
////                .build();
////    }
//
//    @DisplayName("회원가입 API 테스트")
//    @Test
//    void saveUser() throws Exception {
//        // given
//        final String username = "아이디";
//        final String password = "비밀번호";
//        UserJoinRequestDto joinDto = UserJoinRequestDto.builder()
//                .username(username)
//                .password(password)
//                .build();
//        String url = "http://localhost:8080/api/join";
//
//        // when
//        ResultActions perform = mvc.perform(post(url)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(joinDto)));
//
//        // then
//        perform.andExpect(status().isOk())
//                .andDo(document("회원가입",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        requestFields(
//                                fieldWithPath("username").description("아이디"),
//                                fieldWithPath("password").description("비밀번호"),
//                                fieldWithPath("phone").description("연락처")
//                        )));
////        perform.andExpect(status().isOk())
////                .andDo(document("/api/join",
////                        requestFields(
////                                fieldWithPath("username").description("아이디"),
////                                fieldWithPath("password").description("비밀번호"),
////                                fieldWithPath("phone").description("연락처")
////                        ),
////                        preprocessRequest(prettyPrint()),
////                        preprocessResponse(prettyPrint())));
//
//    }
//
//    @DisplayName("회원 정보 수정 Api 테스트")
//    @Test
//    void updateUser() throws Exception {
//        // given
//        final String username = "아이디";
//        final String password = "비밀번호";
//        User user = createUser(username, password);
//
//        final String newPassword = "변경 하는 비밀번호";
//        final String phone = "010-1234-5678";
//        final String image = "profile.jpg";
//        UserUpdateRequestDto updateDto = UserUpdateRequestDto.builder()
//                .password(newPassword)
//                .phone(phone)
//                .profileImage("profile.jpg")
//                .build();
//
//        String url = "http://localhost:8080/api/users/update";
//
//        // when
//        ResultActions perform = mvc.perform(put(url)
//                .contentType(MediaType.APPLICATION_JSON)
//                .principal(authentication)
//                .content(objectMapper.writeValueAsString(updateDto)));
//
//        // then
//        perform.andExpect(status().isOk())
//                .andDo(document("회원정보수정",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        requestFields(
//                                fieldWithPath("password").description("비밀번호"),
//                                fieldWithPath("phone").description("연락처"),
//                                fieldWithPath("profileImage").description("프로필사진")
//                        )));
//
//    }
//
//    @DisplayName("로그인 Api 테스트")
//    @Test
//    void login() throws Exception {
//        // given
//        createUser("아이디", "비밀번호");
////        UserJoinRequestDto joinDto = UserJoinRequestDto.builder()
////                .username("아이디")
////                .password("비밀번호")
////                .build();
////        userService.createUser(joinDto); // 암호화 된 계정을 만들기 위함이다. 시큐리티 로그인이기 때문에 이렇게 해야 로그인 가능함
//        UserLoginRequestDto user = UserLoginRequestDto.builder()
//                .username("아이디")
//                .password("비밀번호")
//                .build();
//        String url = "http://localhost:8080/login";
//
//        // when
//        ResultActions perform = mvc.perform(post(url)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(user)));
//
//        // then
//        perform.andExpect(status().isOk())
//                .andDo(document("로그인",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        requestFields(
//                                fieldWithPath("username").description("아이디"),
//                                fieldWithPath("password").description("비밀번호")
//                        )));
//
//    }
//
//    private User createUser(final String username, final String password) {
//        User user = userRepository.save(User.builder()
//                .username(username)
//                .password(passwordEncoder.encode(password))
//                .build());
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword(), new ArrayList<>()));
//
//        authentication = Mockito.mock(Authentication.class);
//        Mockito.when(authentication.getName()).thenReturn("" + user.getId());
//        return user;
//    }
//}