package com.example.sparrow.configservice.entity;

import com.example.sparrow.configservice.enums.Format;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "app", schema = "sparrow")
public class App extends BaseEntity {
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private Format format;
    @Column(name = "config_file", nullable = false, columnDefinition = "text")
    private String configFile;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        App app = (App) o;
        return getId() != null && Objects.equals(getId(), app.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}