package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.AnswerDAO;
import com.mitwpu.lca.model.Answer;
import com.mitwpu.lca.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet for AJAX auto-save of exam answers.
 * Returns JSON response for seamless frontend integration.
 */
@WebServlet("/answer")
public class AnswerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AnswerDAO answerDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        answerDAO = new AnswerDAO();
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        if (user == null || !user.isStudent()) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Session expired or unauthorized");
            out.print(jsonResponse.toString());
            return;
        }

        // Parse JSON body for AJAX requests
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {
            JsonObject jsonRequest = gson.fromJson(sb.toString(), JsonObject.class);
            int attemptId = jsonRequest.get("attemptId").getAsInt();
            int questionId = jsonRequest.get("questionId").getAsInt();

            Answer answer = new Answer();
            answer.setAttemptId(attemptId);
            answer.setQuestionId(questionId);

            if (jsonRequest.has("selectedOptionId") && !jsonRequest.get("selectedOptionId").isJsonNull()) {
                answer.setSelectedOptionId(jsonRequest.get("selectedOptionId").getAsInt());
            }
            if (jsonRequest.has("subjectiveAnswer") && !jsonRequest.get("subjectiveAnswer").isJsonNull()) {
                answer.setSubjectiveAnswer(jsonRequest.get("subjectiveAnswer").getAsString());
            }

            boolean saved = answerDAO.saveOrUpdateAnswer(answer);
            jsonResponse.addProperty("success", saved);
            jsonResponse.addProperty("message", saved ? "Answer saved" : "Failed to save");
        } catch (Exception e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid request: " + e.getMessage());
        }

        out.print(jsonResponse.toString());
    }
}

