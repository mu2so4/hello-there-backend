package ru.nsu.ccfit.muratov.hello.there.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.RegistrationRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.security.TokenBlacklist;
import ru.nsu.ccfit.muratov.hello.there.service.JwtService;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AuthenticationController.class)
public class AuthenticationControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserEntityService userService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private TokenBlacklist tokenBlacklist;

    private RegistrationRequestDto dto;
    private UserEntity user;
    private Date registrationTime;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

        dto = createRegistrationRequest();
        registrationTime = new Date();
        user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setRegistrationTime(registrationTime);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setBirthday(dto.getBirthday());
        user.setId(1);
        user.setPassword("Very Difficult Password");
    }

    @Test
    public void registerUser() throws Exception {
        given(userService.registerUser(any(RegistrationRequestDto.class))).willReturn(user);

        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)));

        response
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(dto.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(dto.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(dto.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", CoreMatchers.is(user.getId())));
                //.andExpect(MockMvcResultMatchers.jsonPath("$.birthday", CoreMatchers.is(user.getBirthday())))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.registrationTime", CoreMatchers.is(registrationTime)));
    }

    private static RegistrationRequestDto createRegistrationRequest() throws ParseException {
        RegistrationRequestDto dto = new RegistrationRequestDto();
        dto.setUsername("mu2so4");
        dto.setPassword("veryDifficultPassword");
        dto.setFirstName("Maxim");
        dto.setLastName("Muratov");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        Date date = parser.parse("2002-01-01");
        dto.setBirthday(date);
        return dto;
    }
}
