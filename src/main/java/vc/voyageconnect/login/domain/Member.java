package vc.voyageconnect.login.domain;

import lombok.Getter;

import java.util.List;

/**
 * @Entity
 */
@Getter
public class Member {

    /**
     * @Id
     * @GenerateValue
     */
    private Long id;

    private String userId;
    private String NickName;
    private TravelTendency tendency;
    private String sex;
    private String age;

}
