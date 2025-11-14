package kr._soong.passbook.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import kr._soong.passbook.entity.BarcodeCard;
import kr._soong.passbook.repository.BarcodeCardRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BarcodeService {
    
    private final BarcodeCardRepository barcodeCardRepository;
    
    @Transactional
    public BarcodeCard createOrUpdateBarcode(String barcodeNumber, String uniqueKey) {
        BarcodeCard card;
        
        if (uniqueKey != null && !uniqueKey.isEmpty()) {
            // 기존 카드 수정
            card = barcodeCardRepository.findByUniqueKey(uniqueKey)
                    .orElseThrow(() -> new RuntimeException("카드를 찾을 수 없습니다."));
            card.setBarcodeNumber(barcodeNumber);
        } else {
            // 새 카드 생성
            card = BarcodeCard.builder()
                    .uniqueKey(UUID.randomUUID().toString())
                    .barcodeNumber(barcodeNumber)
                    .barcodeType("CODE_128")
                    .downloadCount(0)
                    .build();
        }
        
        return barcodeCardRepository.save(card);
    }
    
    @Transactional
    public BarcodeCard incrementDownloadCount(String uniqueKey) {
        BarcodeCard card = barcodeCardRepository.findByUniqueKey(uniqueKey)
                .orElseThrow(() -> new RuntimeException("카드를 찾을 수 없습니다."));
        
        card.setDownloadCount(card.getDownloadCount() + 1);
        return barcodeCardRepository.save(card);
    }
    
    public byte[] generateBarcodeImage(String barcodeNumber) throws WriterException, IOException {
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(
                barcodeNumber, 
                BarcodeFormat.CODE_128, 
                400, 
                150
        );
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        return outputStream.toByteArray();
    }
    
    public BarcodeCard getCardByUniqueKey(String uniqueKey) {
        return barcodeCardRepository.findByUniqueKey(uniqueKey)
                .orElseThrow(() -> new RuntimeException("카드를 찾을 수 없습니다."));
    }
}