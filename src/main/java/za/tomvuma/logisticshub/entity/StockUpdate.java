package za.tomvuma.logisticshub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "stock_updates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int oldQuantity;
    private int newQuantity;

    private Long actorId;

    private LocalDateTime timestamp = LocalDateTime.now();

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata; // optional: reason, source, etc.
}
