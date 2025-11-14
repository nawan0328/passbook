package kr._soong.passbook.dto;

import lombok.Data;

@Data
public class BarcodeCreateRequest {
    private String barcodeNumber;
    private String uniqueKey; // 수정시 사용
}