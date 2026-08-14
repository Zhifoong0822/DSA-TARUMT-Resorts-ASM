/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

/**
 *
 * @author Yu He
 */
import adt.CustomList;
import entity.Member;

public class MemberDao {

    private CustomList<Member> members;

    public MemberDao() {

        members = new CustomList<>();

        members.add(new Member("M001", "Alice", "NORMAL", "000000-00-0001"));
        members.add(new Member("M002", "Bob", "VIP", "000000-00-0002"));
        members.add(new Member("M003", "John", "NORMAL", "000000-00-0003"));
        members.add(new Member("M004", "Sarah", "VIP", "000000-00-0004"));
        members.add(new Member("M005", "David", "NORMAL", "000000-00-0005"));
    }

    public Member findMemberByIC(String icNumber) {

        if (icNumber == null) {
            return null;
        }

        icNumber = icNumber.trim();

        for (int i = 0; i < members.size(); i++) {

            Member member = members.get(i);

            if (member.getIcNumber()
                    .equalsIgnoreCase(icNumber)) {

                return member;
            }
        }

        return null;
    }

    public Member findMemberById(String memberId) {

        if (memberId == null) {
            return null;
        }

        memberId = memberId.trim();

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            if (member.getMemberId()
                    .equalsIgnoreCase(memberId)) {
                return member;
            }
        }
        return null;
    }

    public void displayAllMembers() {
        System.out.println(
                "\n===== MEMBER LIST ====="
        );
        System.out.printf(
                "%-8s %-15s %-10s %-20s%n","ID","Name","Type","IC Number");
        System.out.println("-------------------------------------------------------");
        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            System.out.printf(
                    "%-8s %-15s %-10s %-20s%n",
                    member.getMemberId(),
                    member.getMemberName(),
                    member.getMembershipType(),
                    member.getIcNumber()
            );
        }
    }

    public CustomList<Member> getAllMembers() {
        return members;
    }
}