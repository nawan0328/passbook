package kr._soong.passbook.controller;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.WriterException;

import kr._soong.passbook.dto.BarcodeCreateRequest;
import kr._soong.passbook.dto.BarcodeResponse;
import kr._soong.passbook.entity.BarcodeCard;
import kr._soong.passbook.service.BarcodeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/barcode")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BarcodeController {
    
    private final BarcodeService barcodeService;
    
    @PostMapping("/create")
    public ResponseEntity<BarcodeResponse> createBarcode(@RequestBody BarcodeCreateRequest request) {
        try {
            BarcodeCard card = barcodeService.createOrUpdateBarcode(
                    request.getBarcodeNumber(),
                    request.getUniqueKey()
            );
            
            BarcodeResponse response = new BarcodeResponse();
            response.setUniqueKey(card.getUniqueKey());
            response.setBarcodeNumber(card.getBarcodeNumber());
            response.setDownloadCount(card.getDownloadCount());
            response.setMessage(request.getUniqueKey() != null ? "바코드가 수정되었습니다." : "바코드가 생성되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BarcodeResponse errorResponse = new BarcodeResponse();
            errorResponse.setMessage("오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/download/{uniqueKey}")
    public ResponseEntity<byte[]> downloadBarcode(@PathVariable("uniqueKey") String uniqueKey) {
        try {
            BarcodeCard card = barcodeService.getCardByUniqueKey(uniqueKey);
            byte[] barcodeImage = barcodeService.generateBarcodeImage(card.getBarcodeNumber());
            
            // 다운로드 카운트 증가
            barcodeService.incrementDownloadCount(uniqueKey);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "barcode_" + card.getBarcodeNumber() + ".png");
            
            return new ResponseEntity<>(barcodeImage, headers, HttpStatus.OK);
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/info/{uniqueKey}")
    public ResponseEntity<BarcodeResponse> getBarcodeInfo(@PathVariable("uniqueKey") String uniqueKey) {
        try {
            BarcodeCard card = barcodeService.getCardByUniqueKey(uniqueKey);
            
            BarcodeResponse response = new BarcodeResponse();
            response.setUniqueKey(card.getUniqueKey());
            response.setBarcodeNumber(card.getBarcodeNumber());
            response.setDownloadCount(card.getDownloadCount());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}