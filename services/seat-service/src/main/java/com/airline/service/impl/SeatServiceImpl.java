package com.airline.service.impl;

import com.airline.dto.request.SeatRequest;
import com.airline.dto.response.SeatResponse;
import com.airline.entity.Seat;
import com.airline.entity.SeatMap;
import com.airline.enums.SeatType;
import com.airline.mapper.SeatMapper;
import com.airline.repository.SeatMapRepository;
import com.airline.repository.SeatRepository;
import com.airline.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;
    private final SeatMapRepository seatMapRepository;

    @Override
    public void generateSeats(Long seatMapId) {
        boolean isExists = seatRepository.existsBySeatMapId(seatMapId);
        if (isExists) {
            throw new RuntimeException("Seats already generated for this seat map");
        }
        SeatMap seatMap = seatMapRepository.findById(seatMapId).orElseThrow(() -> new RuntimeException("Seat map not found"));

        int leftSeatsPerRow = seatMap.getLeftSeatsPerRow();
        int rightSeatsPerRow = seatMap.getRightSeatsPerRow();
        int totalRows = seatMap.getTotalRows();
        int seatPerRow = leftSeatsPerRow + rightSeatsPerRow;

        List<Seat> seats = new ArrayList<>();

        for (int row = 1; row <= totalRows; row++) {
            for (int col = 1; col <= seatPerRow; col++) {
                String seatNumber = getSeatLetter(col) + row;
                SeatType seatType = getSeatType(col, leftSeatsPerRow, rightSeatsPerRow);
                Seat seat = Seat.builder().seatNumber(seatNumber).seatRow(row).columnLetter(getSeatLetter(col).charAt(0)).seatType(seatType).seatMap(seatMap).build();
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
    }

    private String getSeatLetter(int col) {
        StringBuilder sb = new StringBuilder();
        while (col >= 0) {
            sb.insert(0, (char) ('A' + col % 26));
            col /= 26;
        }
        return sb.toString();
    }

    private SeatType getSeatType(int col, int leftSeatsPerRows, int rightSeatsPerRows) {
        int totalSeats = leftSeatsPerRows + rightSeatsPerRows;
        if (col == 0 || col == totalSeats - 1) return SeatType.WINDOW;
        if (col == leftSeatsPerRows - 1) return SeatType.AISLE;
        if (col == leftSeatsPerRows) return SeatType.AISLE;
        return SeatType.MIDDLE;
    }

    @Override
    public SeatResponse updateSeats(Long seatId, SeatRequest seatRequest) {
        return null;
    }

    @Override
    public List<SeatResponse> getAll() {
        return seatRepository.findAll().stream().map(SeatMapper::toResponse).toList();
    }
}
