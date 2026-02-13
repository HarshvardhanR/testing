package com.piggymetrics.account.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Embeddable // This tells JPA to "flatten" these fields into the parent Account table
public class Saving {

    @NotNull
    @Column(name = "saving_amount") // Good practice to prefix to avoid conflicts
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "saving_currency")
    private Currency currency;

    @NotNull
    private BigDecimal interest;

    @NotNull
    private Boolean deposit;

    @NotNull
    private Boolean capitalization;

    // ... (Keep all your existing Getters and Setters) ...

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

	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public Boolean getDeposit() {
		return deposit;
	}

	public void setDeposit(Boolean deposit) {
		this.deposit = deposit;
	}

	public Boolean getCapitalization() {
		return capitalization;
	}

	public void setCapitalization(Boolean capitalization) {
		this.capitalization = capitalization;
	}
}
