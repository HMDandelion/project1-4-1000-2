package com.hmdandelion.project_1410002.inventory.domian.repository.stock;

import com.hmdandelion.project_1410002.inventory.domian.entity.product.Product;
import com.hmdandelion.project_1410002.inventory.domian.entity.product.QProduct;
import com.hmdandelion.project_1410002.inventory.domian.entity.stock.QStock;
import com.hmdandelion.project_1410002.inventory.domian.entity.stock.QStorage;
import com.hmdandelion.project_1410002.inventory.domian.entity.stock.Stock;
import com.hmdandelion.project_1410002.inventory.domian.entity.stock.Storage;
import com.hmdandelion.project_1410002.inventory.domian.repository.stock.StorageRepoCustom;
import com.hmdandelion.project_1410002.inventory.dto.stock.response.StockProduct;
import com.hmdandelion.project_1410002.inventory.dto.stock.response.StorageFilterResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class StorageRepoCustomImpl implements StorageRepoCustom {

    private final JPAQueryFactory queryFactory;

    public StorageRepoCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<StorageFilterResponse> searchStorages(Pageable pageable, Long productCode, Long minQuantity, Long maxQuantity, Long startDate, Long endDate, Boolean quantitySort, Boolean dateSort) {

        QStock stock = QStock.stock;
        QStorage storage = QStorage.storage;
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        if (productCode != null) {
            builder.and(stock.product.productCode.eq(productCode));
        }
        if (minQuantity != null) {
            builder.and(storage.actualQuantity.goe(minQuantity));
        }
        if (maxQuantity != null) {
            builder.and(storage.actualQuantity.loe(maxQuantity));
        }






//        if (assignmentStatus != null) {
//            builder.and(stock.assignmentStatus.eq(assignmentStatus));
//        }


        builder.and(storage.isDelete.eq(false));

        JPAQuery<Storage> query = queryFactory
                .selectFrom(storage)
                .join(storage.stock,stock)
                .join(storage.stock.product, product)
                .where(builder);
//
        // 정렬 조건 추가
        if (Boolean.TRUE.equals(quantitySort)) {
            query.orderBy(storage.actualQuantity.asc());
        } else if (Boolean.FALSE.equals(quantitySort)) {
            query.orderBy(storage.actualQuantity.desc());
        }

        if (Boolean.TRUE.equals(dateSort)) {
            query.orderBy(storage.createdAt.asc());
        } else if (Boolean.FALSE.equals(dateSort)) {
            query.orderBy(storage.createdAt.desc());
        }

        QueryResults<Storage> queryResults = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<StorageFilterResponse> results = queryResults.getResults().stream()
                .map(storageEntity -> {
                    StorageFilterResponse storageFilterResponse = new StorageFilterResponse(storageEntity);
                    storageFilterResponse.setIsToday(storageEntity.getCreatedAt().toLocalDate().isEqual(LocalDate.now()));
                    return storageFilterResponse;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, queryResults.getTotal());
    }
}
