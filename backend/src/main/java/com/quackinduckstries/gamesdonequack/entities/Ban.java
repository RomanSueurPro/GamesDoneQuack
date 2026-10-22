package com.quackinduckstries.gamesdonequack.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "bans")
public class Ban {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JsonBackReference
    @JoinColumn(
    	name = "id_user", nullable = false, referencedColumnName = "id")
	private User user;
	
	@ManyToOne
	@JsonBackReference
    @JoinColumn(
    	name = "id_moderator", nullable = false, referencedColumnName = "id")
	private User moderator;
	
	private String reason;
	
	@Column(nullable = false)
	private LocalDate startDate;
	
	@Column(nullable = true)
	private LocalDate endDate;
}
