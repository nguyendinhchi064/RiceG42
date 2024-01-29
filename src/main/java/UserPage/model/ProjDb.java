package UserPage.model;

import Login_Register.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.util.Objects;


@Entity
@Table(name = "proj_db", schema = "mydatabase")
public class ProjDb extends PanacheEntityBase {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "ProjID")
    private long projId;
    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;
    @Basic
    @Column(name = "Projname")
    public String projname;
    @Basic
    @Column(name = "Description")
    public String description;


    public long getProjId() {
        return projId;
    }

    public void setProjId(int projId) {
        this.projId = projId;
    }

    public String getProjname() {
        return projname;
    }

    public void setProjname(String projname) {
        this.projname = projname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjDb projDb = (ProjDb) o;

        if (projId != projDb.projId) return false;
        if (!Objects.equals(projname, projDb.projname)) return false;
        return Objects.equals(description, projDb.description);
    }

    @Override
    public int hashCode() {
        long result = projId;
        result = 31 * result + (projname != null ? projname.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return (int) result;
    }

    public static ProjDb findById(int id) {
        return find("projId", id).firstResult();
    }

    @Transactional
    public static ProjDb updateProject(int projId, ProjDb updatedProject) {
        ProjDb existingProject = findById(projId);

        if (existingProject != null) {
            existingProject.setProjname(updatedProject.getProjname());
            existingProject.setDescription(updatedProject.getDescription());
            existingProject.persist();
        }

        return existingProject;
    }

    @Transactional
    public static void deleteProject(int projId) {
        ProjDb project = findById(projId);
        if (project != null) {
            project.delete();
        }
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
