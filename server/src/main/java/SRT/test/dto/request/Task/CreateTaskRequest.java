package SRT.test.dto.request.Task;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date dueDate;
    private boolean isImportant;
    private boolean isCompleted;
    private boolean isDeleted;
}
