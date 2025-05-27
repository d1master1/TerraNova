package org.example.terranova.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "realty")
public class Realty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String region;
    private String locality;
    private String street;
    private int rooms;
    private int floors;
    private double square;
    private String repair;
    private double cost;

    private boolean elevator;
    private boolean playground;
    private boolean trashChute;
    private boolean parking;
    private boolean balcony;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id")
    private Deal deal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Transient
    public String getFormattedCost() {
        if (cost < 100_000) {
            return cost + " ₽";
        } else if (cost < 1_000_000) {
            return ((int) (cost / 1_000)) + " тыс. ₽";
        } else if (cost < 1_000_000_000) {
            return String.format("%.1f млн. ₽", cost / 1_000_000.0);
        } else {
            return String.format("%.1f млрд ₽", cost / 1_000_000_000.0);
        }
    }
}
