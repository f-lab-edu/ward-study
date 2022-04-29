package com.dsg.wardstudy.integration.studyGroup;

import com.dsg.wardstudy.controller.studyGroup.StudyGroupController;
import com.dsg.wardstudy.domain.studyGroup.StudyGroup;
import com.dsg.wardstudy.dto.studyGroup.StudyGroupRequest;
import com.dsg.wardstudy.dto.studyGroup.StudyGroupResponse;
import com.dsg.wardstudy.repository.studyGroup.StudyGroupRepository;
import com.dsg.wardstudy.service.studyGroup.StudyGroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class StudyGroupControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private StudyGroup studyGroup;

    @BeforeEach
    void setup() {
        studyGroup = StudyGroup.builder()
                .title("testSG")
                .content("인원 4명의 스터디그룹을 모집합니다.")
                .build();
    }

    @Test
    public void create() throws Exception {
        // given - precondition or setup
        StudyGroupRequest studyGroupRequest = StudyGroupRequest.builder()
                .title(studyGroup.getTitle())
                .content(studyGroup.getContent())
                .build();

        // when - action or the behaviour that we are going test
        ResultActions resultActions = mockMvc.perform(post("/study-group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studyGroupRequest)));

        // then - verify the output
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(studyGroup.getTitle())))
                .andExpect(jsonPath("$.content", is(studyGroup.getContent())));

    }

    @Test
    public void getAll() throws Exception {
        // given - precondition or setup
        int length = 3;
        IntStream.rangeClosed(1, length).forEach(i -> {
            StudyGroup studyGroup = StudyGroup.builder()
                    .title("sg_dsg"+"_"+i)
                    .content("spring_study"+"_"+i)
                    .build();
            studyGroupRepository.save(studyGroup);
        });

        // when - action or the behaviour that we are going test
        // then - verify the output
        mockMvc.perform(get("/study-group"))
                .andDo(print())
                .andExpect(status().isOk());


    }

    @Test
    public void getById() throws Exception {
        // given - precondition or setup
        StudyGroup savedStudyGroup = studyGroupRepository.save(studyGroup);

        // when - action or the behaviour that we are going test
        // then - verify the output
        mockMvc.perform(get("/study-group/" + savedStudyGroup.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(studyGroup.getTitle())))
                .andExpect(jsonPath("$.content", is(studyGroup.getContent())));

    }

    @Test
    public void getById_ThrowException() throws Exception {
        Long studyGroupId = 100L;
        // given - precondition or setup
        studyGroupRepository.save(studyGroup);

        // when - action or the behaviour that we are going test
        // then - verify the output
        mockMvc.perform(get("/study-group/" + studyGroupId))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    public void getAllByUserId() throws Exception {
        // given - precondition or setup

        // when - action or the behaviour that we are going test

        // then - verify the output

    }

    @Test
    public void updateById() throws Exception {
        // given - precondition or setup
        StudyGroup savedStudyGroup = studyGroupRepository.save(studyGroup);

        StudyGroupRequest updateStudyGroupRequest = StudyGroupRequest.builder()
                .title("Jasi")
                .content("springboot_study!!")
                .build();


        // when - action or the behaviour that we are going test
        // then - verify the output
        mockMvc.perform(put("/study-group/{id}", savedStudyGroup.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStudyGroupRequest)))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void deleteById() throws Exception {
        // given - precondition or setup
        StudyGroup savedStudyGroup = studyGroupRepository.save(studyGroup);

        // when - action or the behaviour that we are going test
        // then - verify the output
        mockMvc.perform(delete("/study-group/{id}", savedStudyGroup.getId()))
                .andDo(print())
                .andExpect(status().isOk());

    }
}