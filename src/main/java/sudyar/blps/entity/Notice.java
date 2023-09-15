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
@Table(name = "Notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NonNull
    @Column(name = "toUser")
    private String toUser;

    @Column(name = "from_user")
    private String fromUser;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "target_ordering_id",referencedColumnName = "id")
    private Ordering targetOrdering;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createdDate;


}
