/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

/**
 *
 * @author Yu He
 */
import entity.Member;
import java.util.ArrayList;
import java.util.List;

public class MemberDao {

    private List<Member> members;

    public MemberDao() {
        members = new ArrayList<>();

        // Hardcoded members
        members.add(new Member("M001", "Alice", "NORMAL","000000-00-0001"));
        members.add(new Member("M002", "Bob", "VIP","000000-00-0002"));
        members.add(new Member("M003", "John", "NORMAL","000000-00-0003"));
        members.add(new Member("M004", "Sarah", "VIP","000000-00-0004"));
        members.add(new Member("M005", "David", "NORMAL","000000-00-000"));
    }

   public Member findMemberByIC(
            String icNumber) {

        if (icNumber == null) {

            return null;
        }

        // Remove accidental spaces
        icNumber = icNumber.trim();

        for (Member member : members) {

            if (member.getIcNumber()
                    .equalsIgnoreCase(icNumber)) {

                return member;
            }
        }

        return null;
    }

    // =====================================================
    // FIND MEMBER BY MEMBER ID
    // =====================================================

    public Member findMemberById(
            String memberId) {

        if (memberId == null) {

            return null;
        }

        memberId = memberId.trim();

        for (Member member : members) {

            if (member.getMemberId()
                    .equalsIgnoreCase(memberId)) {

                return member;
            }
        }

        return null;
    }

    // =====================================================
    // DISPLAY ALL MEMBERS
    // =====================================================

    public void displayAllMembers() {

        System.out.println(
                "\n===== MEMBER LIST ====="
        );

        System.out.printf(
                "%-8s %-15s %-10s %-20s%n",
                "ID",
                "Name",
                "Type",
                "IC Number"
        );

        System.out.println(
                "-------------------------------------------------------"
        );

        for (Member member : members) {

            System.out.printf(
                    "%-8s %-15s %-10s %-20s%n",
                    member.getMemberId(),
                    member.getMemberName(),
                    member.getMembershipType(),
                    member.getIcNumber()
            );
        }
    }

    public List<Member> getAllMembers() {

        return members;
    }}