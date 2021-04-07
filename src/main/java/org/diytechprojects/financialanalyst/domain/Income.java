package org.diytechprojects.financialanalyst.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Income.
 */
@Table("income")
public class Income implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Column("search_string_1")
    private String searchString1;

    @Column("search_string_2")
    private String searchString2;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Income id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Income name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearchString1() {
        return this.searchString1;
    }

    public Income searchString1(String searchString1) {
        this.searchString1 = searchString1;
        return this;
    }

    public void setSearchString1(String searchString1) {
        this.searchString1 = searchString1;
    }

    public String getSearchString2() {
        return this.searchString2;
    }

    public Income searchString2(String searchString2) {
        this.searchString2 = searchString2;
        return this;
    }

    public void setSearchString2(String searchString2) {
        this.searchString2 = searchString2;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Income)) {
            return false;
        }
        return id != null && id.equals(((Income) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Income{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", searchString1='" + getSearchString1() + "'" +
            ", searchString2='" + getSearchString2() + "'" +
            "}";
    }
}
