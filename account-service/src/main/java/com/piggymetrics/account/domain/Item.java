package com.piggymetrics.account.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.math.BigDecimal;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // JPA needs a unique ID for each row in the items table

    @NotNull
    @Length(min = 1, max = 20)
    private String title;

    @NotNull
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING) // Saves the name of the enum (e.g., "USD") instead of a number
    private Currency currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TimePeriod period;

    @NotNull
    private String icon;

    // Getters and Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    // (Keep your existing getters/setters for the other fields)

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public TimePeriod getPeriod() {
		return period;
	}

	public void setPeriod(TimePeriod period) {
		this.period = period;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
