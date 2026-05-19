package com.insurance.reporting.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "agent_controls")
public class AgentControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "main_agt_key", nullable = false, length = 12)
    private String mainAgtKey;

    @Column(name = "status", nullable = false, length = 1)
    private String status;

    @Column(name = "inspect_no")
    private Short inspectNo;

    @Column(name = "name", length = 25)
    private String name;

    public AgentControl() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMainAgtKey() { return mainAgtKey; }
    public void setMainAgtKey(String mainAgtKey) { this.mainAgtKey = mainAgtKey; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Short getInspectNo() { return inspectNo; }
    public void setInspectNo(Short inspectNo) { this.inspectNo = inspectNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
