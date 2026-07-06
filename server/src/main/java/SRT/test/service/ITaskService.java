package SRT.test.service;

import SRT.test.common.PageResponse;
import SRT.test.dto.request.Task.CreateTaskRequest;
import SRT.test.dto.request.Task.UpdateTaskRequest;
import SRT.test.dto.response.Task.TaskResponse;

public interface ITaskService {

    PageResponse<TaskResponse> getTasks(String userName,
                                       String title,
                                       String status,
                                       int page,
                                       int size);

    TaskResponse createTask(CreateTaskRequest request);

    TaskResponse updateTask(String taskId, UpdateTaskRequest request);

    void deleteTask(String taskId);

    TaskResponse restoreTask(String taskId);

    TaskResponse setTaskCompletion(String taskId, boolean completed);
}
