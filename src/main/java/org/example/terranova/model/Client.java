package org.example.terranova.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String passport;
    private String name;
    private String surname;
    private String patronymic;
    private String phone;
    private String ownerType;
    private String company;
    private String license;

    // Связь OneToOne с User — владелец клиента
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    // Вспомогательный метод для отображения полного имени клиента
    public String getFullName() {
        return surname + " " + name + (patronymic != null ? " " + patronymic : "");
    }
}
