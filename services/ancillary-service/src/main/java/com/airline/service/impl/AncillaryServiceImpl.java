package com.airline.service.impl;

import com.airline.dto.request.AncillaryRequest;
import com.airline.dto.response.AncillaryResponse;
import com.airline.entity.Ancillary;
import com.airline.mapper.AncillaryMapper;
import com.airline.repository.AncillaryRepository;
import com.airline.service.AncillaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AncillaryServiceImpl implements AncillaryService {
    private final AncillaryRepository ancillaryRepository;
    @Override
    public AncillaryResponse createAncillary(Long airlineId, AncillaryRequest ancillaryRequest) {
        Ancillary ancillary = Ancillary
                .builder()
                .name(ancillaryRequest.getName())
                .type(ancillaryRequest.getType())
                .subType(ancillaryRequest.getSubType())
                .rfisc(ancillaryRequest.getRfisc())
                .displayOrder(ancillaryRequest.getDisplayOrder())
                .airlineId(airlineId)
                .metadata(ancillaryRequest.getMetadata())
                .description(ancillaryRequest.getDescription())
                .build();
        ancillaryRepository.save(ancillary);
        return AncillaryMapper.toResponse(ancillary, null);
    }

    @Override
    public AncillaryResponse getAncillaryById(Long id) throws Exception {
        Ancillary ancillary = ancillaryRepository.findById(id).orElseThrow(
                () -> new Exception("Ancillary not found")
        );
        // todo: fetch coverages
        return AncillaryMapper.toResponse(ancillary, null);
    }

    @Override
    public List<AncillaryResponse> getByAirlineId(Long airlineId) {

        // todo: fetch coverages
        return ancillaryRepository.findByAirlineId(airlineId).stream()
                .map(ancillary -> AncillaryMapper.toResponse(ancillary, null))
                .toList();
    }

    @Override
    public AncillaryResponse updateAncillary(Long id, AncillaryRequest ancillaryRequest) {
            Ancillary ancillary = ancillaryRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Ancillary not found")
            );

            ancillary.setName(ancillaryRequest.getName());
            ancillary.setType(ancillaryRequest.getType());
            ancillary.setSubType(ancillaryRequest.getSubType());
            ancillary.setRfisc(ancillaryRequest.getRfisc());
            ancillary.setDisplayOrder(ancillaryRequest.getDisplayOrder());
            ancillary.setMetadata(ancillaryRequest.getMetadata());
            ancillary.setDescription(ancillaryRequest.getDescription());
            ancillaryRepository.save(ancillary);
        // todo: fetch coverages
        return AncillaryMapper.toResponse(ancillary, null);
    }

    @Override
    public void deleteAncillary(Long id) {
        Ancillary ancillary = ancillaryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Ancillary not found")
        );
        ancillaryRepository.delete(ancillary);
    }
}
