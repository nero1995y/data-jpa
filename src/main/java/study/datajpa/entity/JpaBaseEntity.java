package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
/**
 * 진짜 상속 관계는 아니고 속성만 내려서 사용하는 거 기본편 참고 !
 *
 * */
@Getter
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable = false)  // update 업데이트를 막는옵션 insertable=false있음
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    /**
     * method 설명 저장하기전에 이벤트 발생하는데
     * updateDate = now;  null로 두지 않는 것은 쿼리 날릴때 편하다. 지저분해지지 않게 하기위함
     * */
    @PrePersist // JPA 제공
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        //this.createDate = now; 디스 써도되고 안써도되고 영한쌤은 강조하고 싶을때 사용
        createDate = now;
        updateDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }



}
