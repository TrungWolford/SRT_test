package SRT.test.controller;

import SRT.test.common.ApiResponse;
import SRT.test.common.PageResponse;
import SRT.test.dto.request.Task.CreateTaskRequest;
import SRT.test.dto.request.Task.UpdateTaskRequest;
import SRT.test.dto.response.Task.TaskResponse;
import SRT.test.service.ITaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private final ITaskService taskService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TaskResponse>>> getTasks(
            @RequestParam String userName,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<TaskResponse> response = taskService.getTasks(userName, title, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @RequestBody @Valid CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable String taskId,
            @RequestBody @Valid UpdateTaskRequest request) {
        TaskResponse response = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok(ApiResponse.success(null, "Task deleted successfully"));
    }

    @PatchMapping("/{taskId}/restore")
    public ResponseEntity<ApiResponse<TaskResponse>> restoreTask(@PathVariable String taskId) {
        TaskResponse response = taskService.restoreTask(taskId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{taskId}/completion")
    public ResponseEntity<ApiResponse<TaskResponse>> setTaskCompletion(
            @PathVariable String taskId,
            @RequestParam boolean completed) {
        TaskResponse response = taskService.setTaskCompletion(taskId, completed);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
