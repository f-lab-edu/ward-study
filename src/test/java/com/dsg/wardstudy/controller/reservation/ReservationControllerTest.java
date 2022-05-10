package com.dsg.wardstudy.controller.reservation;

import com.dsg.wardstudy.domain.reservation.Reservation;
import com.dsg.wardstudy.domain.reservation.Room;
import com.dsg.wardstudy.domain.studyGroup.StudyGroup;
import com.dsg.wardstudy.domain.user.User;
import com.dsg.wardstudy.domain.user.UserGroup;
import com.dsg.wardstudy.dto.reservation.ReservationCreateRequest;
import com.dsg.wardstudy.dto.reservation.ReservationDetails;
import com.dsg.wardstudy.dto.reservation.ReservationUpdateRequest;
import com.dsg.wardstudy.exception.ErrorCode;
import com.dsg.wardstudy.exception.ResourceNotFoundException;
import com.dsg.wardstudy.service.reservation.ReservationService;
import com.dsg.wardstudy.type.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
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

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Reservation reservation;
    private User user;
    private UserGroup userGroup;
    private StudyGroup studyGroup;
    private Room room;

    private ReservationCreateRequest createRequest;
    private ReservationUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .build();

        userGroup = UserGroup.builder()
                .id(1L)
                .user(user)
                .userType(UserType.L)
                .studyGroup(studyGroup)
                .build();

        studyGroup = StudyGroup.builder()
                .id(1L)
                .build();

        room = Room.builder()
                .id(1L)
                .build();

        reservation = Reservation.builder()
                .id("1||2019-11-03 06:30:00")
                .user(user)
                .studyGroup(studyGroup)
                .room(room)
                .startTime(LocalDateTime.of(2019, Month.NOVEMBER, 3, 6, 30))
                .endTime(LocalDateTime.of(2019, Month.NOVEMBER, 3, 7, 30))
                .build();
    }

    @Test
    @DisplayName("예약 등록")
    void create() throws Exception {

        // LocalDateTime -> String 으로 변환
        String sTime = reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String eTime = reservation.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        createRequest = ReservationCreateRequest.builder()
                .userId(user.getId())
                .startTime(sTime)
                .endTime(eTime)
                .build();

        ReservationDetails reservationDetails = ReservationDetails.builder()
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .user(user)
                .studyGroup(studyGroup)
                .room(room)
                .build();

        given(reservationService.create(createRequest, studyGroup.getId(), room.getId()))
                .willReturn(reservationDetails);

        mockMvc.perform(post("/study-group/{studyGroupId}/room/{roomId}/reservation", studyGroup.getId(), room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startTime", is(sTime)))
                .andExpect(jsonPath("$.endTime", is(eTime)));

    }


    @Test
    @DisplayName("등록한 예약 상세 보기")
    void getByIds() throws Exception {

        // LocalDateTime -> String 으로 변환
        String sTime = reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String eTime = reservation.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ReservationDetails reservationDetails = ReservationDetails.builder()
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .user(user)
                .studyGroup(studyGroup)
                .room(room)
                .build();
        given(reservationService.getByRoomIdAndReservationId(room.getId(), reservation.getId()))
                .willReturn(reservationDetails);

        mockMvc.perform(get("/room/{roomId}/reservation/{reservationId}", room.getId(), reservation.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime", is(sTime)))
                .andExpect(jsonPath("$.endTime", is(eTime)));
    }


    @Test
    @DisplayName("해당 룸 예약 조회 startTime & endTime(o)")
    void getByRoomIdAndTimePeriod() throws Exception {

        List<ReservationDetails> detailsList = new ArrayList<>();
        ReservationDetails reservationDetails = ReservationDetails.builder()
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .user(user)
                .studyGroup(studyGroup)
                .room(room)
                .build();

        IntStream.rangeClosed(1, 5).forEach(i -> {
            detailsList.add(reservationDetails);
        });

        // LocalDateTime -> String 으로 변환
        String sTime = reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String eTime = reservation.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        given(reservationService.getByRoomIdAndTimePeriod(room.getId(), sTime, eTime))
                .willReturn(detailsList);

        mockMvc.perform(get("/room/{roomId}/reservation/query", room.getId())
                        .param("startTime", sTime)
                        .param("endTime", eTime))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(5)))
                .andExpect(jsonPath("$.[0].startTime", is(sTime)))
                .andExpect(jsonPath("$.[0].endTime", is(eTime)));


    }


    @Test
    @DisplayName("해당 룸 예약 조회")
    void getByRoomId() throws Exception {
        List<ReservationDetails> detailsList = new ArrayList<>();
        ReservationDetails reservationDetails = ReservationDetails.builder()
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .user(user)
                .studyGroup(studyGroup)
                .room(room)
                .build();

        IntStream.rangeClosed(1, 5).forEach(i -> {
            detailsList.add(reservationDetails);
        });

        // LocalDateTime -> String 으로 변환
        String sTime = reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String eTime = reservation.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        given(reservationService.getByRoomId(room.getId()))
                .willReturn(detailsList);

        mockMvc.perform(get("/room/{roomId}/reservation", room.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(5)))
                .andExpect(jsonPath("$.[0].startTime", is(sTime)))
                .andExpect(jsonPath("$.[0].endTime", is(eTime)));

    }

    @Test
    @DisplayName("해당 룸 예약 조회 404에러")
    public void getByRoomId_ThrowException() throws Exception {

        Long roomId = 100L;
        given(reservationService.getByRoomId(roomId)).willThrow(new ResourceNotFoundException(ErrorCode.NO_TARGET));

        mockMvc.perform(get("/room/{roomId}/reservation", roomId))
                .andDo(print())
                .andExpect(status().isNotFound());

    }


    @Test
    @DisplayName("해당 유저 예약  조회")
    void getAllByUserId() throws Exception {
        List<ReservationDetails> detailsList = new ArrayList<>();
        ReservationDetails reservationDetails = ReservationDetails.builder()
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .user(user)
                .studyGroup(studyGroup)
                .room(room)
                .build();

        IntStream.rangeClosed(1, 5).forEach(i -> {
            detailsList.add(reservationDetails);
        });

        // LocalDateTime -> String 으로 변환
        String sTime = reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String eTime = reservation.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        given(reservationService.getAllByUserId(user.getId()))
                .willReturn(detailsList);

        mockMvc.perform(get("/user/{userId}/reservation", user.getId()))
                .andDo(print())
                .andExpect(jsonPath("$.length()", is(5)))
                .andExpect(jsonPath("$.[0].startTime", is(sTime)))
                .andExpect(jsonPath("$.[0].endTime", is(eTime)));

    }

    @Test
    @DisplayName("예약 수정")
    void updateById() throws Exception {

        String updateSTime = LocalDateTime.of(2022, Month.NOVEMBER, 3, 6, 30)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String updateETime = LocalDateTime.of(2022, Month.NOVEMBER, 3, 6, 30)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        updateRequest = ReservationUpdateRequest.builder()
                .userId(user.getId())
                .studyGroupId(studyGroup.getId())
                .startTime(updateSTime)
                .endTime(updateETime)
                .build();

        given(reservationService.updateById(room.getId(), reservation.getId(), updateRequest))
                .willReturn(reservation.getId());

        mockMvc.perform(put("/room/{roomId}/reservation/{reservationId}", room.getId(), reservation.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk());

    }


    @Test
    @DisplayName("예약 삭제")
    void deleteById() throws Exception {
        willDoNothing().given(reservationService).deleteById(reservation.getId());

        mockMvc.perform(delete("/reservation/{reservationId}", reservation.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}