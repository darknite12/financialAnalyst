package org.diytechprojects.financialanalyst.domain;

import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Transaction.
 */
@Table("transaction")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("account_type")
    private String accountType;

    @Column("transaction_date")
    private Instant transactionDate;

    @Column("cheque_number")
    private String chequeNumber;

    @Column("description_1")
    private String description1;

    @Column("description_2")
    private String description2;

    @Column("amount_cad")
    private Long amountCAD;

    @Column("amount_usd")
    private Long amountUSD;

    @Column("is_tracked")
    private Boolean isTracked;

    private Long incomeId;

    @Transient
    private Income income;

    private Long expenseId;

    @Transient
    private Expense expense;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transaction id(Long id) {
        this.id = id;
        return this;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public Transaction accountType(String accountType) {
        this.accountType = accountType;
        return this;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Instant getTransactionDate() {
        return this.transactionDate;
    }

    public Transaction transactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
        return this;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getChequeNumber() {
        return this.chequeNumber;
    }

    public Transaction chequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
        return this;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public String getDescription1() {
        return this.description1;
    }

    public Transaction description1(String description1) {
        this.description1 = description1;
        return this;
    }

    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    public String getDescription2() {
        return this.description2;
    }

    public Transaction description2(String description2) {
        this.description2 = description2;
        return this;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public Long getAmountCAD() {
        return this.amountCAD;
    }

    public Transaction amountCAD(Long amountCAD) {
        this.amountCAD = amountCAD;
        return this;
    }

    public void setAmountCAD(Long amountCAD) {
        this.amountCAD = amountCAD;
    }

    public Long getAmountUSD() {
        return this.amountUSD;
    }

    public Transaction amountUSD(Long amountUSD) {
        this.amountUSD = amountUSD;
        return this;
    }

    public void setAmountUSD(Long amountUSD) {
        this.amountUSD = amountUSD;
    }

    public Boolean getIsTracked() {
        return this.isTracked;
    }

    public Transaction isTracked(Boolean isTracked) {
        this.isTracked = isTracked;
        return this;
    }

    public void setIsTracked(Boolean isTracked) {
        this.isTracked = isTracked;
    }

    public Income getIncome() {
        return this.income;
    }

    public Transaction income(Income income) {
        this.setIncome(income);
        this.incomeId = income != null ? income.getId() : null;
        return this;
    }

    public void setIncome(Income income) {
        this.income = income;
        this.incomeId = income != null ? income.getId() : null;
    }

    public Long getIncomeId() {
        return this.incomeId;
    }

    public void setIncomeId(Long income) {
        this.incomeId = income;
    }

    public Expense getExpense() {
        return this.expense;
    }

    public Transaction expense(Expense expense) {
        this.setExpense(expense);
        this.expenseId = expense != null ? expense.getId() : null;
        return this;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
        this.expenseId = expense != null ? expense.getId() : null;
    }

    public Long getExpenseId() {
        return this.expenseId;
    }

    public void setExpenseId(Long expense) {
        this.expenseId = expense;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return id != null && id.equals(((Transaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", accountType='" + getAccountType() + "'" +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", chequeNumber='" + getChequeNumber() + "'" +
            ", description1='" + getDescription1() + "'" +
            ", description2='" + getDescription2() + "'" +
            ", amountCAD=" + getAmountCAD() +
            ", amountUSD=" + getAmountUSD() +
            ", isTracked='" + getIsTracked() + "'" +
            "}";
    }
}
