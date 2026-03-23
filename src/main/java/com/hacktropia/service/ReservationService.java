package com.hacktropia.service;

import com.hacktropia.payload.dto.ReservationDTO;
import com.hacktropia.payload.request.ReservationRequest;
import com.hacktropia.payload.request.ReservationSearchRequest;
import com.hacktropia.payload.response.PageResponse;


public interface ReservationService {

    ReservationDTO createReservation(ReservationRequest reservationRequest) throws Exception;

    ReservationDTO createReservationForUser(ReservationRequest reservationRequest, Long userId) throws Exception;

    ReservationDTO cancelReservation(Long reservationId) throws Exception;
    ReservationDTO fulfillReservation(Long reservationId) throws Exception;

    PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest) throws Exception;
    PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest);
}
