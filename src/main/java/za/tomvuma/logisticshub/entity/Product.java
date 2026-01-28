package za.tomvuma.logisticshub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;   // product description
    private String unit;          // e.g. "kg", "box", "litre"
    private double price;         // product price
    private int quantity;

    private String category;      // NEW: product category

    private boolean archived = false; // soft-delete flag

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private User supplier;
}
