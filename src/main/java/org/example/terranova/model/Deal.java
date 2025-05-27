package org.example.terranova.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deal")
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;

    private String status;

    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realty_id")
    private Realty realty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Transient
    public String getDateForInput() {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }

    @Transient
    public String getFormattedDate() {
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru"));
            return date.format(formatter);
        }
        return "—";
    }

    @Transient
    public String getFormattedCost() {
        if (amount == null) return "—";

        long value = amount;
        if (value < 100_000) {
            return value + " ₽";
        } else if (value < 1_000_000) {
            return (value / 1_000) + " тыс. ₽";
        } else if (value < 1_000_000_000) {
            return String.format("%.1f млн. ₽", value / 1_000_000.0);
        } else {
            return String.format("%.1f млрд ₽", value / 1_000_000_000.0);
        }
    }
}
