package SRT.test.repo;

import SRT.test.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaskRepository extends JpaRepository<Task, String> {

    Page<Task> findByCreatedBy_UserNameAndIsDeletedFalseAndIsCompletedFalse(String userName, Pageable pageable);

    Page<Task> findByCreatedBy_UserNameAndIsDeletedFalseAndIsCompletedFalseAndTitleContainingIgnoreCase(String userName, String title, Pageable pageable);

    Page<Task> findByCreatedBy_UserNameAndIsImportantTrueAndIsDeletedFalse(String userName, Pageable pageable);

    Page<Task> findByCreatedBy_UserNameAndIsImportantTrueAndIsDeletedFalseAndTitleContainingIgnoreCase(String userName, String title, Pageable pageable);

    Page<Task> findByCreatedBy_UserNameAndIsDeletedTrue(String userName, Pageable pageable);

    Page<Task> findByCreatedBy_UserNameAndIsDeletedTrueAndTitleContainingIgnoreCase(String userName, String title, Pageable pageable);

    Page<Task> findByCreatedBy_UserNameAndIsCompletedTrueAndIsDeletedFalse(String userName, Pageable pageable);

    Page<Task> findByCreatedBy_UserNameAndIsCompletedTrueAndIsDeletedFalseAndTitleContainingIgnoreCase(String userName, String title, Pageable pageable);
}
