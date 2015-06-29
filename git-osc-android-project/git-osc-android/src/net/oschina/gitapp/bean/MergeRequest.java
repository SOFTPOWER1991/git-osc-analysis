package net.oschina.gitapp.bean;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("serial")
public class MergeRequest extends Entity  {
    public static final String URL = "/merge_requests";

    private Integer _iid;
    private String _title;
    private String _state;
    private boolean _closed;
    private boolean _merged;
    private User _author;
    private User _assignee;

    @JsonProperty("target_branch")
    private String _targetBranch;

    @JsonProperty("source_branch")
    private String _sourceBranch;

    @JsonProperty("project_id")
    private Integer _projectId;

    @JsonProperty("source_project_id")
    private Integer _sourceProjectId;

    public Integer getIid() {
        return _iid;
    }

    public void setIid(Integer iid) {
        _iid = iid;
    }

    public String getTargetBranch() {
        return _targetBranch;
    }

    public void setTargetBranch(String targetBranch) {
        _targetBranch = targetBranch;
    }

    public String getSourceBranch() {
        return _sourceBranch;
    }

    public void setSourceBranch(String sourceBranch) {
        _sourceBranch = sourceBranch;
    }

    public Integer getProjectId() {
        return _projectId;
    }

    public void setProjectId(Integer projectId) {
        _projectId = projectId;
    }

    public Integer getSourceProjectId() {
        return _sourceProjectId;
    }

    public void setSourceProjectId(Integer sourceProjectId) {
        _sourceProjectId = sourceProjectId;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public boolean isClosed() {
        return _closed;
    }

    public void setClosed(boolean closed) {
        _closed = closed;
    }

    public boolean isMerged() {
        return _merged;
    }

    public void setMerged(boolean merged) {
        _merged = merged;
    }

    public User getAuthor() {
        return _author;
    }

    public void setAuthor(User author) {
        _author = author;
    }

    public User getAssignee() {
        return _assignee;
    }

    public void setAssignee(User assignee) {
        _assignee = assignee;
    }

    public String getState() {
        return _state;
    }

    public void setState(String state) {
        _state = state;
        if(state != null) {
            _closed = state.equals("closed");
            _merged = state.equals("merged");
        }
    }
}
