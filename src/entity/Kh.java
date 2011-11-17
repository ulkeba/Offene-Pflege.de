package entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Kh {
    private long khid;

    @javax.persistence.Column(name = "KHID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getKhid() {
        return khid;
    }

    public void setKhid(long khid) {
        this.khid = khid;
    }

    private String name;

    @javax.persistence.Column(name = "Name", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String strasse;

    @javax.persistence.Column(name = "Strasse", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    private String plz;

    @javax.persistence.Column(name = "PLZ", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    private String ort;

    @javax.persistence.Column(name = "Ort", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    private String tel;

    @javax.persistence.Column(name = "Tel", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    private String fax;

    @javax.persistence.Column(name = "Fax", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Kh kh = (Kh) o;

        if (khid != kh.khid) return false;
        if (fax != null ? !fax.equals(kh.fax) : kh.fax != null) return false;
        if (name != null ? !name.equals(kh.name) : kh.name != null) return false;
        if (ort != null ? !ort.equals(kh.ort) : kh.ort != null) return false;
        if (plz != null ? !plz.equals(kh.plz) : kh.plz != null) return false;
        if (strasse != null ? !strasse.equals(kh.strasse) : kh.strasse != null) return false;
        if (tel != null ? !tel.equals(kh.tel) : kh.tel != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (khid ^ (khid >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (strasse != null ? strasse.hashCode() : 0);
        result = 31 * result + (plz != null ? plz.hashCode() : 0);
        result = 31 * result + (ort != null ? ort.hashCode() : 0);
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        return result;
    }
}
