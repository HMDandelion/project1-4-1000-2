package com.hmdandelion.project_1410002.production.presentation;

import com.hmdandelion.project_1410002.common.paging.Pagination;
import com.hmdandelion.project_1410002.common.paging.PagingButtonInfo;
import com.hmdandelion.project_1410002.common.paging.PagingResponse;
import com.hmdandelion.project_1410002.production.domain.type.ProductionStatusType;
import com.hmdandelion.project_1410002.production.dto.request.DefectDetailCreateRequest;
import com.hmdandelion.project_1410002.production.dto.request.ProductionDetailCreateRequest;
import com.hmdandelion.project_1410002.production.dto.request.ReportCreateRequest;
import com.hmdandelion.project_1410002.production.dto.response.production.DefectDetailResponse;
import com.hmdandelion.project_1410002.production.dto.response.production.ProductionDetailResponse;
import com.hmdandelion.project_1410002.production.dto.response.production.ProductionReportResponse;
import com.hmdandelion.project_1410002.production.service.ProductionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductionManagerController {

    private final ProductionService productionService;


    /*조회*/
    @GetMapping("/production/reports")
    public ResponseEntity<PagingResponse> getProductionReports(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Long productionStatusCode,
            @RequestParam(required = false) ProductionStatusType productionStatusType,
            @RequestParam(required = false) LocalDateTime startAt,
            @RequestParam(required = false) LocalDateTime completedAt) {

        if (page == null || page <= 0) {
            page = 1;
        }
        final Page<ProductionReportResponse> productionReportResponses = productionService.getProductionReportRecords(page, productionStatusCode, productionStatusType, startAt, completedAt);
        final PagingButtonInfo pagingButtonInfo = Pagination.getPagingButtonInfo(productionReportResponses);
        final PagingResponse pagingResponse = PagingResponse.of(productionReportResponses.getContent(), pagingButtonInfo);
        return ResponseEntity.ok(pagingResponse);
    }

    /* 상세 목록 조회 */
    @GetMapping("/production/reports/{productionStatusCode}/detail")
    public ResponseEntity<List<ProductionDetailResponse>> getProductionDetails(@PathVariable Long productionStatusCode) {
        List<ProductionDetailResponse> productionDetails = productionService.getProductionDetails(productionStatusCode);
        return ResponseEntity.ok(productionDetails);
    }

    /* 불량상세 조회 */
    @GetMapping("/production/reports/{productionDetailCode}/defects")
    public ResponseEntity<List<DefectDetailResponse>> getDefectDetails(@PathVariable Long productionDetailCode) {
        List<DefectDetailResponse> defectDetailResponses = productionService.getDefectDetails(productionDetailCode);
        return ResponseEntity.ok(defectDetailResponses);
    }

    @PostMapping("/production/reports")
    public ResponseEntity<Long> createReport(@RequestBody ReportCreateRequest reportCreateRequest) {
        Long id = productionService.reportSave(reportCreateRequest);
        return ResponseEntity.ok(id);
    }
}

//        // 총 생산량 계산
//        int totalProductionQuantity = productionService.calculateTotalProductionQuantity();

//
//    /* 보고서 수정 */
//    @PutMapping("production/reports/{productionStatusCode}/modify")
//    public ResponseEntity<Void> modify(@PathVariable final Long productionStatusCode, @RequestBody final ProductionReportUpdateRequest productionReportUpdateRequest) {
//        productionService.modifyReport(productionStatusCode, productionReportUpdateRequest);
//
//        return ResponseEntity.created(URI.create("/api/v1/production/reports/" + productionStatusCode)).build();
//
//    }
//
//    @DeleteMapping("production/reports/{productionStatusCode}/delete")
//    public ResponseEntity<Void> response(@PathVariable final Long productionStatusCode) {
//        productionService.removeReport(productionStatusCode);
//        return ResponseEntity.noContent().build();
//    }
//}