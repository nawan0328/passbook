package kr._soong.passbook.repository;

import kr._soong.passbook.entity.BarcodeCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarcodeCardRepository extends JpaRepository<BarcodeCard, Long> {
    Optional<BarcodeCard> findByUniqueKey(String uniqueKey);
}