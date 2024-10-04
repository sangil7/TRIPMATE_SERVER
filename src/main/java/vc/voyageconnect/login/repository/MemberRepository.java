package vc.voyageconnect.login.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vc.voyageconnect.login.domain.Member;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    //private final EntityManager em;

    /**
     * 멤버 저장
     */
    public void save(Member member) {

        //em.persistent(member);
    }

    /**
     * 한명 찾기
     */
    /*public Member findOne(Long id) {
        return em.find(Member.class, id);
    }*/


    /**
     * List 찾기
     */
    /*public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }*/


}
