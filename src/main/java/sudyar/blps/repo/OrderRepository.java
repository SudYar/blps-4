package sudyar.blps.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sudyar.blps.entity.Ordering;

import java.util.List;

public interface OrderRepository extends JpaRepository<Ordering, Integer> {
    List<Ordering> findByOwnerLogin(String ownerLogin);

//    void deleteByOwnerLogin(String ownerLogin);


    @Override
    void deleteById(Integer id);

}
