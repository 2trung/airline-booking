package com.airline.service.impl;

import com.airline.dto.request.AirportRequest;
import com.airline.dto.response.AirportResponse;
import com.airline.entity.Airport;
import com.airline.entity.City;
import com.airline.mapper.AirportMapper;
import com.airline.repository.AirportRepository;
import com.airline.repository.CityRepository;
import com.airline.service.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AirportServiceImpl implements AirportService {
    private final AirportRepository airportRepository;
    private final CityRepository cityRepository;

    @Override
    public AirportResponse createAirport(AirportRequest airportRequest) throws Exception {
        if (airportRepository.existsByIataCode(airportRequest.getIataCode())) {
            throw new Exception("Airport with IATA code " + airportRequest.getIataCode() + " already exists.");
        }

        City city = cityRepository.findById(airportRequest.getCityId())
                .orElseThrow(() -> new Exception("City with id " + airportRequest.getCityId() + " not found."));

        Airport airport = AirportMapper.toEntity(airportRequest, city);
        Airport savedAirport = airportRepository.save(airport);
        return AirportMapper.toResponse(savedAirport);
    }

    @Override
    public AirportResponse getAirportById(Long id) throws Exception {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new Exception("Airport with id " + id + " not found."));
        return AirportMapper.toResponse(airport);
    }

    @Override
    public AirportResponse updateAirport(Long id, AirportRequest airportRequest) throws Exception {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new Exception("Airport with id " + id + " not found."));

        if (airportRequest.getIataCode() != null &&
            airportRepository.existsByIataCodeAndIdNot(airportRequest.getIataCode(), id)) {
            throw new Exception("Airport with IATA code " + airportRequest.getIataCode() + " already exists.");
        }

        City city = null;
        if (airportRequest.getCityId() != null) {
            city = cityRepository.findById(airportRequest.getCityId())
                    .orElseThrow(() -> new Exception("City with id " + airportRequest.getCityId() + " not found."));
        }

        AirportMapper.updateEntityFromDto(airport, airportRequest, city);
        Airport updatedAirport = airportRepository.save(airport);
        return AirportMapper.toResponse(updatedAirport);
    }

    @Override
    public void deleteAirport(Long id) throws Exception {
        airportRepository.findById(id)
                .orElseThrow(() -> new Exception("Airport with id " + id + " not found."));
        airportRepository.deleteById(id);
    }

    @Override
    public Page<AirportResponse> getAllAirports(Pageable pageable) {
        return airportRepository.findAll(pageable).map(AirportMapper::toResponse);
    }

    @Override
    public Page<AirportResponse> searchAirports(String keyword, Pageable pageable) {
        return airportRepository.searchByKeywordIgnoreCase(keyword, pageable).map(AirportMapper::toResponse);
    }

    @Override
    public Page<AirportResponse> getAirportsByCity(Long cityId, Pageable pageable) throws Exception {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new Exception("City with id " + cityId + " not found."));
        return airportRepository.findByCity(city, pageable).map(AirportMapper::toResponse);
    }

    @Override
    public boolean existsByIataCode(String iataCode) {
        return airportRepository.existsByIataCode(iataCode);
    }
}

