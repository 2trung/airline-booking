package com.airline.service.impl;

import com.airline.dto.request.CityRequest;
import com.airline.dto.response.CityResponse;
import com.airline.entity.City;
import com.airline.mapper.CityMapper;
import com.airline.repository.CityRepository;
import com.airline.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    @Override
    public CityResponse createCity(CityRequest cityRequest) throws Exception {
        if (cityRepository.existsByCityCode(cityRequest.getCityCode())) {
            throw new Exception("City with code " + cityRequest.getCityCode() + " already exists.");
        }
        City city = CityMapper.toEntity(cityRequest);
        City savedCity = cityRepository.save(city);
        return CityMapper.toResponse(savedCity);
    }

    @Override
    public CityResponse getCityById(Long id) throws Exception {
        City city = cityRepository.findById(id).orElseThrow(() -> new Exception("City with id " + id + " not found."));
        return CityMapper.toResponse(city);
    }

    @Override
    public CityResponse updateCity(Long id, CityRequest cityRequest) throws Exception{
        City city = cityRepository.findById(id).orElseThrow(() -> new Exception("City with id " + id + " not found."));
        if (cityRequest.getCityCode() != null && cityRepository.existsByCityCodeAndIdNot(cityRequest.getCityCode(), id)) {
            throw new Exception("City with code " + cityRequest.getCityCode() + " already exists.");
        }
        CityMapper.updateEntityFromDto(city, cityRequest);
        City updatedCity = cityRepository.save(city);
        return CityMapper.toResponse(updatedCity);
    }

    @Override
    public void deleteCity(Long id) throws Exception {
        cityRepository.findById(id).orElseThrow(() -> new Exception("City with id " + id + " not found."));
        cityRepository.deleteById(id);
    }

    @Override
    public Page<CityResponse> getAllCities(Pageable pageable) {
        return cityRepository.findAll(pageable).map(CityMapper::toResponse);
    }

    @Override
    public Page<CityResponse> searchCities(String keyword, Pageable pageable) {
        return cityRepository.searchByKeywordIgnoreCase(keyword, pageable).map(CityMapper::toResponse);
    }

    @Override
    public Page<CityResponse> getCityByCountryCode(String countryCode, Pageable pageable) {
        return cityRepository.findByCountryCodeIgnoreCase(countryCode, pageable).map(CityMapper::toResponse);
    }

    @Override
    public boolean existsByCityCode(String cityCode) {
        return cityRepository.existsByCityCode(cityCode);
    }
}
