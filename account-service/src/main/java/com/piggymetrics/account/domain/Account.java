package com.piggymetrics.account.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*; // Use Jakarta for Spring Boot 3
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import java.util.List;

@Entity 
@Table(name = "accounts")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    @Id
    private String name; // Note: In JPA, String IDs work fine, but aren't auto-generated

    private Date lastSeen;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_name")
    @Valid
    private List<Item> incomes;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_name")
    @Valid
    private List<Item> expenses;

    // Assuming Saving is another class, it needs @Embedded or @OneToOne
    @Embedded 
    @Valid
    @NotNull
    private Saving saving;

    @Length(min = 0, max = 20000)
    private String note;

    // ... Getters and Setters (Same as before) ...

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(Date lastSeen) {
		this.lastSeen = lastSeen;
	}

	public List<Item> getIncomes() {
		return incomes;
	}

	public void setIncomes(List<Item> incomes) {
		this.incomes = incomes;
	}

	public List<Item> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<Item> expenses) {
		this.expenses = expenses;
	}

	public Saving getSaving() {
		return saving;
	}

	public void setSaving(Saving saving) {
		this.saving = saving;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
