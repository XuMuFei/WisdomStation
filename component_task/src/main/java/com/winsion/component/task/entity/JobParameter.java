package com.winsion.component.task.entity;

import java.util.List;

/**
 * Created by wyl on 2017/6/21
 * 操作作业相关参数
 */
public class JobParameter {
    private String usersId;
    private String jobsId;
    private String ssId;
    private String taskId;
    private String note;
    private String opormotId;
    private int opType;
    private List<FileEntity> fileList;

    public String getUsersId() {
        return usersId;
    }

    public void setUsersId(String usersId) {
        this.usersId = usersId;
    }

    public String getJobsId() {
        return jobsId;
    }

    public void setJobsId(String jobsId) {
        this.jobsId = jobsId;
    }

    public String getSsId() {
        return ssId;
    }

    public void setSsId(String ssId) {
        this.ssId = ssId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOpormotId() {
        return opormotId;
    }

    public void setOpormotId(String opormotId) {
        this.opormotId = opormotId;
    }

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public List<FileEntity> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileEntity> fileList) {
        this.fileList = fileList;
    }
}
