package kr._soong.passbook.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "barcode_cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarcodeCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 36)
    private String uniqueKey;
    
    @Column(nullable = false)
    private String barcodeNumber;
    
    @Column(nullable = false)
    private String barcodeType;
    
    @Column(nullable = false)
    private Integer downloadCount;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        if (downloadCount == null) {
            downloadCount = 0;
        }
    }
}