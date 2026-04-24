package com.mitwpu.lca.servlet;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

import com.mitwpu.lca.util.DBConnection;
import com.mitwpu.lca.model.User;

@WebServlet("/student/profile-stats")
public class ProfileStatsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        response.setContentType("application/json");

        if (user == null) {
            response.getWriter().write("{\"success\":false}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            // get student_id
            PreparedStatement ps1 = conn.prepareStatement(
                "SELECT student_id FROM students WHERE user_id=?"
            );
            ps1.setInt(1, user.getUserId());
            ResultSet rs1 = ps1.executeQuery();

            int studentId = 0;
            if (rs1.next()) {
                studentId = rs1.getInt("student_id");
            }

            int total = 0, shortlisted = 0, selected = 0;

            PreparedStatement ps2 = conn.prepareStatement(
                "SELECT status, COUNT(*) as count FROM applications WHERE student_id=? GROUP BY status"
            );
            ps2.setInt(1, studentId);
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
                String status = rs2.getString("status");
                int count = rs2.getInt("count");

                total += count;

                if ("SHORTLISTED".equals(status)) shortlisted = count;
                if ("ACCEPTED".equals(status)) selected = count;
            }

            String json = "{"
                + "\"success\":true,"
                + "\"total\":" + total + ","
                + "\"shortlisted\":" + shortlisted + ","
                + "\"selected\":" + selected
                + "}";

            response.getWriter().write(json);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false}");
        }
    }
}