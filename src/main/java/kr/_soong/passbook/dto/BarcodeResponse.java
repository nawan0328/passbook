package kr._soong.passbook.dto;

import lombok.Data;

@Data
public class BarcodeResponse {
    private String uniqueKey;
    private String barcodeNumber;
    private Integer downloadCount;
    private String message;
}