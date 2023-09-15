package sudyar.blps.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "Ordering")
public class Ordering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "address")
    @NonNull
    private String address;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    @NonNull
    private Integer price;

    @Column(name = "owner_login")
    private String ownerLogin;

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createdDate;


}
