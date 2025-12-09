package com.example.demo.Entity;

import java.io.Serializable;
import java.util.Objects;

public class MengajarId implements Serializable {
    private String emailDosen;
    private Long idMk;

    public MengajarId() {}

    public MengajarId(String emailDosen, Long idMk) {
        this.emailDosen = emailDosen;
        this.idMk = idMk;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        MengajarId that = (MengajarId) o;
        return Objects.equals(emailDosen, that.emailDosen) &&
                Objects.equals(idMk, that.idMk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailDosen, idMk);
    }
}
