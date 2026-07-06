package SRT.test.dto.response.Task;

import com.fasterxml.jackson.annotation.JsonFormat;
import SRT.test.entity.Task;
import SRT.test.entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class TaskResponse {
    private String taskId;

    private String title;

    private UserInfo createdBy;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date dueDate;

    private Date createdDate;

    private Date updatedDate;

    private Date completedDate;

    private boolean isImportant;

    private boolean isCompleted;

    private boolean isDeleted;

    public static TaskResponse fromEntity(Task task) {
        TaskResponse response = new TaskResponse();
        response.setTaskId(task.getTaskId());
        response.setTitle(task.getTitle());
        response.setCreatedBy(UserInfo.fromEntity(task.getCreatedBy()));
        response.setDescription(task.getDescription());
        response.setDueDate(task.getDueDate());
        response.setCreatedDate(task.getCreatedDate());
        response.setUpdatedDate(task.getUpdatedDate());
        response.setCompletedDate(task.getCompletedDate());
        response.setImportant(task.isImportant());
        response.setCompleted(task.isCompleted());
        response.setDeleted(task.isDeleted());
        return response;
    }

    @Data
    public static class UserInfo {
        private String userName;
        private String accountName;
        private String fullName;
        private String email;

        public static UserInfo fromEntity(User user) {
            if (user == null) {
                return null;
            }
            UserInfo info = new UserInfo();
            info.setUserName(user.getUserName());
            info.setAccountName(user.getAccountName());
            info.setFullName(user.getFullName());
            info.setEmail(user.getEmail());
            return info;
        }
    }
}
