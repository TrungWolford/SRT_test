package SRT.test.service.Impl;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserName("uuid-user-123");
        user.setAccountName("testuser");
        user.setFullName("Test User");
        user.setEmail("test@example.com");

        task = new Task();
        task.setTaskId("uuid-task-456");
        task.setTitle("Test Task Title");
        task.setDescription("Test Task Description");
        task.setDueDate(new Date());
        task.setImportant(false);
        task.setCompleted(false);
        task.setDeleted(false);
        task.setCreatedBy(user);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthentication(User mockUser) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        CustomUserDetails userDetails = new CustomUserDetails(mockUser);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createTask_Success() {
        mockAuthentication(user);
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Test Task Title");
        request.setDescription("Test Task Description");
        request.setDueDate(task.getDueDate());

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        assertEquals("Test Task Title", response.getTitle());
        assertEquals("uuid-task-456", response.getTaskId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_ThrowsException_WhenTitleBlank() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("");

        AppException exception = assertThrows(AppException.class, () -> taskService.createTask(request));
        assertEquals(ErrorEnum.INVALID_INPUT, exception.getErrorEnum());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_Success() {
        when(taskRepository.findById(task.getTaskId())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setImportant(true);
        request.setCompleted(true);

        TaskResponse response = taskService.updateTask(task.getTaskId(), request);

        assertNotNull(response);
        verify(taskRepository, times(1)).save(task);
        assertTrue(task.isImportant());
        assertTrue(task.isCompleted());
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.findById(task.getTaskId())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.deleteTask(task.getTaskId());

        assertTrue(task.isDeleted());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void restoreTask_Success() {
        task.setDeleted(true); // Start as deleted
        when(taskRepository.findById(task.getTaskId())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.restoreTask(task.getTaskId());

        assertNotNull(response);
        assertFalse(task.isDeleted());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void setTaskCompletion_Success() {
        when(taskRepository.findById(task.getTaskId())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.setTaskCompletion(task.getTaskId(), true);

        assertNotNull(response);
        assertTrue(task.isCompleted());
        assertNotNull(task.getCompletedDate());
        verify(taskRepository, times(1)).save(task);
    }
}
