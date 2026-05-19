package com.insurance.reporting.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "accounts")
public class AccountRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "acc_key", nullable = false, length = 18)
    private String accKey;

    @Column(name = "branch", nullable = false, length = 3)
    private String branch;

    @Column(name = "agent", nullable = false, length = 6)
    private String agent;

    @Column(name = "policy", nullable = false, length = 9)
    private String policy;

    @Column(name = "entry_type", length = 1)
    private String entryType;

    @Column(name = "entry_date")
    private Integer entryDate;

    @Column(name = "deb_cred_amt")
    private Long debCredAmt;

    @Column(name = "comm_amount")
    private Long commAmount;

    @Column(name = "cash_amt")
    private Long cashAmt;

    @Column(name = "cash_date")
    private Integer cashDate;

    @Column(name = "method_coll", length = 2)
    private String methodColl;

    @Column(name = "process_mkrs", length = 2)
    private String processMkrs;

    public AccountRecord() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccKey() { return accKey; }
    public void setAccKey(String accKey) { this.accKey = accKey; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getAgent() { return agent; }
    public void setAgent(String agent) { this.agent = agent; }

    public String getPolicy() { return policy; }
    public void setPolicy(String policy) { this.policy = policy; }

    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }

    public Integer getEntryDate() { return entryDate; }
    public void setEntryDate(Integer entryDate) { this.entryDate = entryDate; }

    public Long getDebCredAmt() { return debCredAmt; }
    public void setDebCredAmt(Long debCredAmt) { this.debCredAmt = debCredAmt; }

    public Long getCommAmount() { return commAmount; }
    public void setCommAmount(Long commAmount) { this.commAmount = commAmount; }

    public Long getCashAmt() { return cashAmt; }
    public void setCashAmt(Long cashAmt) { this.cashAmt = cashAmt; }

    public Integer getCashDate() { return cashDate; }
    public void setCashDate(Integer cashDate) { this.cashDate = cashDate; }

    public String getMethodColl() { return methodColl; }
    public void setMethodColl(String methodColl) { this.methodColl = methodColl; }

    public String getProcessMkrs() { return processMkrs; }
    public void setProcessMkrs(String processMkrs) { this.processMkrs = processMkrs; }
}
