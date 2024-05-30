package com.hmdandelion.project_1410002.inventory.domian.entity.stock;

import com.hmdandelion.project_1410002.inventory.domian.entity.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_storage")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE tbl_stock SET is_delete = 1 WHERE storage_code = ?")
@ToString
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storageCode;
    private Long initialQuantity;
    private Long destroyQuantity=0L;
    private Boolean isDelete = false;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name="stock_code")
    private Stock stock;
    @ManyToOne
    @JoinColumn(name="warehouse_code")
    private Warehouse warehouse;
    private Long actualCode;
    @CreatedDate
    private LocalDateTime createdAt;

    public Storage(Long initialQuantity, Stock stock, Warehouse warehouse) {
        this.initialQuantity = initialQuantity;
        this.stock = stock;
        this.warehouse = warehouse;
    }

    public static Storage of(Warehouse warehouse, Long initialQuantity, Stock stock) {
        return new Storage(
                initialQuantity,
                stock,
                warehouse
        );
    }
}
