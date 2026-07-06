package SRT.test.service.Impl;

import SRT.test.common.PageResponse;
import SRT.test.dto.request.Task.CreateTaskRequest;
import SRT.test.dto.request.Task.UpdateTaskRequest;
import SRT.test.dto.response.Task.TaskResponse;
import SRT.test.entity.Task;
import SRT.test.entity.User;
import SRT.test.exception.AppException;
import SRT.test.exception.ErrorEnum;
import SRT.test.repo.ITaskRepository;
import SRT.test.repo.IUserRepository;
import SRT.test.security.CustomUserDetails;
import SRT.test.service.ITaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements ITaskService {

    private final ITaskRepository taskRepository;
    private final IUserRepository userRepository;

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        validateTitle(request.getTitle());

        User user = getAuthenticatedUser();

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setImportant(request.isImportant());
        task.setCompleted(request.isCompleted());
        task.setDeleted(false);
        task.setCreatedBy(user);
        task.setCreatedDate(new Date());
        task.setUpdatedDate(new Date());
        task.setCompletedDate(request.isCompleted() ? new Date() : null);

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(String taskId, UpdateTaskRequest request) {
        validateTitle(request.getTitle());

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND, "Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setImportant(request.isImportant());
        task.setDeleted(request.isDeleted());

        if (request.isCompleted() != task.isCompleted()) {
            task.setCompleted(request.isCompleted());
            task.setCompletedDate(request.isCompleted() ? new Date() : null);
        }

        task.setUpdatedDate(new Date());
        Task updatedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND, "Task not found"));

        task.setDeleted(true);
        task.setUpdatedDate(new Date());
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public TaskResponse restoreTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND, "Task not found"));

        task.setDeleted(false);
        task.setUpdatedDate(new Date());
        Task restoredTask = taskRepository.save(task);
        return TaskResponse.fromEntity(restoredTask);
    }

    @Override
    @Transactional
    public TaskResponse setTaskCompletion(String taskId, boolean completed) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND, "Task not found"));

        task.setCompleted(completed);
        task.setCompletedDate(completed ? new Date() : null);
        task.setUpdatedDate(new Date());

        Task updatedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(updatedTask);
    }

    @Override
    @Transactional
    public PageResponse<TaskResponse> getTasks(String userName,
                                              String title,
                                              String status,
                                              int page,
                                              int size) {
        if (!StringUtils.hasText(userName)) {
            throw new AppException(ErrorEnum.INVALID_INPUT, "userName is required");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Task> taskPage;

        String normalizedTitle = StringUtils.hasText(title) ? title.trim() : null;
        String normalizedStatus = StringUtils.hasText(status) ? status.trim().toLowerCase() : null;

        if (normalizedStatus == null) {
            if (normalizedTitle != null) {
                taskPage = taskRepository.findByCreatedBy_UserNameAndIsDeletedFalseAndIsCompletedFalseAndTitleContainingIgnoreCase(userName, normalizedTitle, pageable);
            } else {
                taskPage = taskRepository.findByCreatedBy_UserNameAndIsDeletedFalseAndIsCompletedFalse(userName, pageable);
            }
        } else {
            switch (normalizedStatus) {
                case "important" -> {
                    if (normalizedTitle != null) {
                        taskPage = taskRepository.findByCreatedBy_UserNameAndIsImportantTrueAndIsDeletedFalseAndTitleContainingIgnoreCase(userName, normalizedTitle, pageable);
                    } else {
                        taskPage = taskRepository.findByCreatedBy_UserNameAndIsImportantTrueAndIsDeletedFalse(userName, pageable);
                    }
                }
                case "deleted" -> {
                    if (normalizedTitle != null) {
                        taskPage = taskRepository.findByCreatedBy_UserNameAndIsDeletedTrueAndTitleContainingIgnoreCase(userName, normalizedTitle, pageable);
                    } else {
                        taskPage = taskRepository.findByCreatedBy_UserNameAndIsDeletedTrue(userName, pageable);
                    }
                }
                case "completed" -> {
                    if (normalizedTitle != null) {
                        taskPage = taskRepository.findByCreatedBy_UserNameAndIsCompletedTrueAndIsDeletedFalseAndTitleContainingIgnoreCase(userName, normalizedTitle, pageable);
                    } else {
                        taskPage = taskRepository.findByCreatedBy_UserNameAndIsCompletedTrueAndIsDeletedFalse(userName, pageable);
                    }
                }
                default -> throw new AppException(ErrorEnum.INVALID_INPUT, "Invalid status filter");
            }
        }

        List<TaskResponse> responseItems = taskPage.stream().map(TaskResponse::fromEntity).toList();
        return PageResponse.of(taskPage, responseItems);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            throw new AppException(ErrorEnum.UNAUTHORIZED, "Unauthorized access");
        }

        String username;
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            username = customUserDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        } else {
            throw new AppException(ErrorEnum.UNAUTHORIZED, "Unauthorized access");
        }

        return userRepository.findByUserName(username)
                .orElseThrow(() -> new AppException(ErrorEnum.USER_NOT_FOUND, "Authenticated user not found"));
    }

    private void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new AppException(ErrorEnum.INVALID_INPUT, "Title is required");
        }
    }
}
