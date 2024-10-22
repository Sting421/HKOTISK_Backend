package com.CSIT321.Hkotisk.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.io.Serializable;
@Entity
@Table(name = "tblstaff")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private int staffId;
    @Column(name = "id_number")
    private String idNumber;

    private String name;
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType = UserType.STAFF;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sid")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private StudentEntity student;
    @Override
    public int hashCode() {
        return 31 + staffId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaffEntity that = (StaffEntity) o;
        return staffId == that.staffId;
    }
}
