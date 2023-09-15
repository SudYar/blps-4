package sudyar.blps.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sudyar.blps.entity.Notice;
import sudyar.blps.entity.Ordering;
import sudyar.blps.entity.Status;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    List<Notice> findByToUser(String toUser);
    List<Notice> findByFromUser(String fromUser);
    List<Notice> findByToUserAndAndStatus(String toUser, Status status);
    boolean existsByToUserAndFromUserAndTargetOrderingAndStatus(String toUser, String fromUser, Ordering targetOrdering, Status status);
    boolean existsByTargetOrderingAndStatus(Ordering targetOrdering, Status status);
    List<Notice> findByTargetOrderingAndStatus(Ordering ordering, Status status);
    List<Notice> findByTargetOrdering(Ordering ordering);


}