package com.lechner.budgetclient;

public class KontoFavorits {
    private Integer id;
    private Integer konto;
    private Integer hits;



    public KontoFavorits(Integer id, Integer konto, Integer hits) {
        super();
        this.id = id;
        this.konto = konto;
        this.hits = hits;
    }

    public KontoFavorits() {

    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getKonto() {
        return konto;
    }
    public void setKonto(Integer konto) {
        this.konto = konto;
    }
    public Integer getHits() {
        return hits;
    }
    public void setHits(Integer hits) {
        this.hits = hits;
    }

}
