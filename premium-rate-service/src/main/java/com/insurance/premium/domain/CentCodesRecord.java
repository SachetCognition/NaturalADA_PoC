package com.insurance.premium.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cent_codes")
public class CentCodesRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_type", nullable = false)
    private Short tableType;

    @Column(name = "date_key", nullable = false)
    private Integer dateKey;

    @Column(name = "tp_premium_1")
    private Integer tpPremium1;

    @Column(name = "tp_premium_2")
    private Integer tpPremium2;

    @Column(name = "tp_premium_3")
    private Integer tpPremium3;

    @Column(name = "tp_premium_4")
    private Integer tpPremium4;

    @Column(name = "tp_premium_5")
    private Integer tpPremium5;

    @Column(name = "tp_premium_6")
    private Integer tpPremium6;

    @Column(name = "tp_premium_7")
    private Integer tpPremium7;

    @Column(name = "tp_premium_8")
    private Integer tpPremium8;

    @Column(name = "tp_premium_9")
    private Integer tpPremium9;

    @Column(name = "tp_premium_10")
    private Integer tpPremium10;

    @Column(name = "cover_code_super", nullable = false, length = 16)
    private String coverCodeSuper;

    @Column(name = "premium", nullable = false)
    private Integer premium;

    @Column(name = "cov_grp_key", length = 16)
    private String covGrpKey;

    @Column(name = "inv_type", length = 8)
    private String invType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public CentCodesRecord() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Short getTableType() { return tableType; }
    public void setTableType(Short tableType) { this.tableType = tableType; }

    public Integer getDateKey() { return dateKey; }
    public void setDateKey(Integer dateKey) { this.dateKey = dateKey; }

    public Integer getTpPremium1() { return tpPremium1; }
    public void setTpPremium1(Integer tpPremium1) { this.tpPremium1 = tpPremium1; }

    public Integer getTpPremium2() { return tpPremium2; }
    public void setTpPremium2(Integer tpPremium2) { this.tpPremium2 = tpPremium2; }

    public Integer getTpPremium3() { return tpPremium3; }
    public void setTpPremium3(Integer tpPremium3) { this.tpPremium3 = tpPremium3; }

    public Integer getTpPremium4() { return tpPremium4; }
    public void setTpPremium4(Integer tpPremium4) { this.tpPremium4 = tpPremium4; }

    public Integer getTpPremium5() { return tpPremium5; }
    public void setTpPremium5(Integer tpPremium5) { this.tpPremium5 = tpPremium5; }

    public Integer getTpPremium6() { return tpPremium6; }
    public void setTpPremium6(Integer tpPremium6) { this.tpPremium6 = tpPremium6; }

    public Integer getTpPremium7() { return tpPremium7; }
    public void setTpPremium7(Integer tpPremium7) { this.tpPremium7 = tpPremium7; }

    public Integer getTpPremium8() { return tpPremium8; }
    public void setTpPremium8(Integer tpPremium8) { this.tpPremium8 = tpPremium8; }

    public Integer getTpPremium9() { return tpPremium9; }
    public void setTpPremium9(Integer tpPremium9) { this.tpPremium9 = tpPremium9; }

    public Integer getTpPremium10() { return tpPremium10; }
    public void setTpPremium10(Integer tpPremium10) { this.tpPremium10 = tpPremium10; }

    public String getCoverCodeSuper() { return coverCodeSuper; }
    public void setCoverCodeSuper(String coverCodeSuper) { this.coverCodeSuper = coverCodeSuper; }

    public Integer getPremium() { return premium; }
    public void setPremium(Integer premium) { this.premium = premium; }

    public String getCovGrpKey() { return covGrpKey; }
    public void setCovGrpKey(String covGrpKey) { this.covGrpKey = covGrpKey; }

    public String getInvType() { return invType; }
    public void setInvType(String invType) { this.invType = invType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
