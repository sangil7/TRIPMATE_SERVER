package vc.voyageconnect.login.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// memberDTO 클래스 정의
@Data
public class MemberDTO {

    private String id;
    private String nickName;

    public MemberDTO(String id, String nickName) {
        this.id = id;
        this.nickName = nickName;
    }
}
