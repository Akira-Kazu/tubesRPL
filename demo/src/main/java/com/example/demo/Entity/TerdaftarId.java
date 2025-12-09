
package com.example.demo.Entity;

import java.io.Serializable;
import java.util.Objects;

public class TerdaftarId implements Serializable {
    private String email;
    private Long idMk;

    public TerdaftarId() {}

    public TerdaftarId(String email, Long idMk) {
        this.email = email;
        this.idMk = idMk;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        TerdaftarId that = (TerdaftarId) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(idMk, that.idMk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, idMk);
    }
}
