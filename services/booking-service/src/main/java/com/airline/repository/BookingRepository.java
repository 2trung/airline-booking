package com.airline.repository;

import com.airline.entity.Booking;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    long countByFlightInstanceId(Long flightInstanceId);

    @Query("""
    select distinct b from Booking b
    left join fetch b.passengers p
    where b.airlineId=:airlineId
    and (:searchQuery is null or lower(b.bookingReference) like lower(concat('%', :searchQuery, '%'))
        or lower(p.firstName) like lower(concat('%', :searchQuery, '%'))
        or lower(p.lastName) like lower(concat('%', :searchQuery, '%'))
        or lower(p.email) like lower(concat('%', :searchQuery, '%'))
        or lower(b.contactInfo.email) like lower(concat('%', :searchQuery, '%'))
        or lower(b.contactInfo.phone) like lower(concat('%', :searchQuery, '%'))
    )
    and (:status is null or b.status = :status)
    and (:flightInstanceId is null or b.flightInstanceId = :flightInstanceId)
""")
    List<Booking> findByAirlineWithFilter(
            @Param("airlineId") Long airlineId,
            @Param("searchQuery") String searchQuery,
            @Param("status") String status,
            @Param("flightInstanceId") Long flightInstanceId,
            Sort sort
    );

    boolean existsByBookingReference(String bookingReference);
}
