package sudyar.blps.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "Feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "address")
    private String address;

    @Column(name = "description")
    private String description;

    @Column(name = "stars")
    private Integer stars;

    @Column(name = "emplyer")
    private String loginEmployer;

    @Column(name = "executor")
    private String loginExecutor;

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createdDate;



}
