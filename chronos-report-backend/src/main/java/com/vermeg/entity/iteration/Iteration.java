package com.vermeg.entity.iteration;
import com.vermeg.entity.lot.Lot;


import jakarta.persistence.*;

@Entity
@Table(name = "iteration")
public class Iteration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "lot_id")
    private Lot lot;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Lot getLot() { return lot; }
    public void setLot(Lot lot) { this.lot = lot; }
}
