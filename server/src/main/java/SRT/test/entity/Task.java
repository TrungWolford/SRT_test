package SRT.test.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;


@Entity
@Table(name = "tasks")
@Data
public class Task {
    @Id
    @Column(name = "task_id")
    private String taskId;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "description")
    private String description;

    @Column(name = "dueDate")
    private Date dueDate;

    @Column(name = "createdDate")
    private Date createdDate;

    @Column(name = "updatedDate")
    private Date updatedDate;

    @Column(name = "completedDate")
    private Date completedDate;

    @Column(name = "isImportant")
    private boolean isImportant;

    @Column(name = "isCompleted")
    private boolean isCompleted;

    @Column(name = "isDeleted")
    private boolean isDeleted;

    @PrePersist
    public void prePersist() {
        if (this.taskId == null) {
            this.taskId = UUID.randomUUID().toString();
        }

    }
}
