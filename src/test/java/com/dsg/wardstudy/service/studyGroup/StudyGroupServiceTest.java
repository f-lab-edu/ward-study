package com.dsg.wardstudy.service.studyGroup;

import com.dsg.wardstudy.domain.studyGroup.StudyGroup;
import com.dsg.wardstudy.dto.studyGroup.StudyGroupRequest;
import com.dsg.wardstudy.dto.studyGroup.StudyGroupResponse;
import com.dsg.wardstudy.repository.studyGroup.StudyGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class StudyGroupServiceTest {

    @Mock
    private StudyGroupRepository studyGroupRepository;

    @InjectMocks
    private StudyGroupService studyGroupService;

    private StudyGroup studyGroup;

    private StudyGroupRequest studyGroupRequest;

    @BeforeEach
    void setup() {
        studyGroup = StudyGroup.builder()
                .id(1L)
                .title("testSG")
                .content("인원 4명의 스터디그룹을 모집합니다.")
                .build();
    }

    @Test
    public void create(){
        // given - precondition or setup
        when(studyGroupRepository.save(any(StudyGroup.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        studyGroupRequest = StudyGroupRequest.builder()
                .title(studyGroup.getTitle())
                .content(studyGroup.getContent())
                .build();
        // when - action or the behaviour that we are going test
        StudyGroupResponse studyGroupResponse = studyGroupService.create(studyGroupRequest);
        log.info("studyGroupResponse: {}", studyGroupResponse);

        // then - verify the output
        assertThat(studyGroupResponse).isNotNull();
        assertThat(studyGroupResponse.getTitle()).isEqualTo("testSG");

    }

    @Test
    public void getById(){
        // given - precondition or setup
        Optional<StudyGroup> studyGroup = Optional.of(this.studyGroup);
        given(studyGroupRepository.findById(1L))
                .willReturn(studyGroup);
        // when - action or the behaviour that we are going test
        StudyGroupResponse studyGroupResponse = studyGroupService.getById(1L);
        log.info("studyGroupResponse: {}", studyGroupResponse);

        // then - verify the output
        assertThat(studyGroupResponse.getTitle()).isEqualTo("testSG");
        assertThat(studyGroupResponse.getContent()).isEqualTo("인원 4명의 스터디그룹을 모집합니다.");

    }

    @Test
    public void getById_ThrowsException(){
        // given - precondition or setup
        // when - action or the behaviour that we are going test
        given(studyGroupRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        // then - verify the output
        assertThatThrownBy(() -> {
            studyGroupService.getById(1L);
        }).isInstanceOf(ResponseStatusException.class);
    }


    @Test
    public void getAll(){
        // given - precondition or setup
        StudyGroup studyGroup1 = StudyGroup.builder()
                .id(100L)
                .title("testSG2")
                .content("인원 6명의 스터디그룹을 모집합니다.")
                .build();
        given(studyGroupRepository.findAll())
                .willReturn(List.of(studyGroup, studyGroup1));
        // when - action or the behaviour that we are going test
        List<StudyGroupResponse> studyGroupResponses = studyGroupService.getAll();
        log.info("studyGroupResponses: {}", studyGroupResponses);
        // then - verify the output
        assertThat(studyGroupResponses).isNotNull();
        assertThat(studyGroupResponses.size()).isEqualTo(2);

    }

    @Test
    public void getAll_negative(){
        // given - precondition or setup
        StudyGroup studyGroup1 = StudyGroup.builder()
                .id(2L)
                .title("testSG2")
                .content("인원 6명의 스터디그룹을 모집합니다.")
                .build();
        given(studyGroupRepository.findAll())
                .willReturn(Collections.emptyList());
        // when - action or the behaviour that we are going test
        List<StudyGroupResponse> studyGroupResponses = studyGroupService.getAll();
        log.info("studyGroupResponses: {}", studyGroupResponses);
        // then - verify the output
        assertThat(studyGroupResponses).isEmpty();
        assertThat(studyGroupResponses.size()).isEqualTo(0);

    }

    @Test
    public void updateById(){
        // given - precondition or setup
        given(studyGroupRepository.findById(anyLong()))
                .willReturn(Optional.of(studyGroup));

        studyGroupRequest = StudyGroupRequest.builder()
                .title("kkk")
                .content("kkk갑니다.")
                .build();

        // when - action or the behaviour that we are going test
        Long updateId = studyGroupService.updateById(studyGroup.getId(), studyGroupRequest);
        log.info("updateId: {}", updateId);

        // then - verify the output
        assertThat(studyGroup.getId()).isEqualTo(updateId);

    }

    @Test
    public void deleteById(){
        // given - precondition or setup
        Long studyGroupId = 1L;
        willDoNothing().given(studyGroupRepository).deleteById(studyGroupId);

        // when - action or the behaviour that we are going test
        studyGroupService.deleteById(studyGroupId);

        // then - verify the output
        verify(studyGroupRepository).deleteById(studyGroupId);

    }

}