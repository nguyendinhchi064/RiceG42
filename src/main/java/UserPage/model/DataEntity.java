package UserPage.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.util.Objects;

@Entity
@Table(name = "Database_Inf", schema = "mydatabase")
public class DataEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Data_Id")
    private long DataId;

    @Basic
    @Column(name = "DataName")
    private String DataName;

    @Basic
    @Column(name = "FileName")
    private String FileName;

    // Getters and Setters for DataName

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }
    public String getDataName() {
        return DataName;
    }

    public void setDataName(String dataName) {
        DataName = dataName;
    }

    public long getDataId() {
        return DataId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataEntity dataEntity = (DataEntity) o;

        if (DataId != dataEntity.DataId) return false;
        return Objects.equals(DataName, dataEntity.DataName);
    }
    @Override
    public int hashCode() {
        long result = DataId;
        result = 31 * result + (DataName != null ? DataName.hashCode() : 0);
        return (int) result;
    }

    @Transactional
    public static void deleteModel(int DataId) {
        DataEntity dataEntity = findById(DataId);
        if (dataEntity != null) {
            dataEntity.delete();
        }
    }


}
