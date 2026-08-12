/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package entity;

/**
 *
 * @author Yu He
 */

public class Member {

    private String memberId;
    private String memberName;
    private String membershipType;
    private String icNumber;

    public Member(
            String memberId,
            String memberName,
            String membershipType,
            String icNumber) {

        this.memberId = memberId;
        this.memberName = memberName;
        this.membershipType = membershipType;
        this.icNumber = icNumber;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public String getIcNumber() {
        return icNumber;
    }

    @Override
    public String toString() {

        return memberId
                + " | "
                + memberName
                + " | "
                + membershipType
                + " | "
                + icNumber;
    }
}