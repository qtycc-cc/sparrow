package com.example.sparrow.configservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * use millisecond
     */
    @Column(name = "time_create", nullable = false)
    private Long timeCreate;

    /**
     * use millisecond
     */
    @Column(name = "time_update", nullable = false)
    private Long timeUpdate;

    @PrePersist
    protected void prePersist() {
        final long currentMilliSecond = System.currentTimeMillis();
        if (this.timeCreate == null) {
            this.timeCreate = currentMilliSecond;
        }
        if (this.timeUpdate == null) {
            this.timeUpdate = currentMilliSecond;
        }
    }

    @PreUpdate
    protected void preUpdate() {
        this.timeUpdate = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BaseEntity that = (BaseEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}