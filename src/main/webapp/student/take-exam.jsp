<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.mitwpu.lca.model.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Take Exam</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .exam-container { max-width: 900px; margin: 0 auto; }
        .timer-box { position: fixed; top: 20px; right: 20px; z-index: 1000; }
        .question-nav { display: flex; flex-wrap: wrap; gap: 5px; margin-bottom: 20px; }
        .question-nav .btn { width: 40px; height: 40px; padding: 0; line-height: 40px; }
        .question-card { display: none; }
        .question-card.active { display: block; }
        .marked { background-color: #ffc107 !important; color: #000 !important; }
        .answered { background-color: #198754 !important; color: #fff !important; }
        .violation-alert { position: fixed; bottom: 20px; right: 20px; z-index: 1000; display: none; }
    </style>
</head>
<body>
    <% Exam exam = (Exam) request.getAttribute("exam");
       List<Question> questions = (List<Question>) request.getAttribute("questions");
       ExamAttempt attempt = (ExamAttempt) request.getAttribute("attempt");
       List<Answer> savedAnswers = (List<Answer>) request.getAttribute("savedAnswers");
       if (exam == null || questions == null || attempt == null) {
           response.sendRedirect(request.getContextPath() + "/student/exams.jsp?error=Invalid+exam+session");
           return;
       }
       // Build map of saved answers by questionId
       Map<Integer, Answer> answerMap = new HashMap<>();
       if (savedAnswers != null) {
           for (Answer a : savedAnswers) answerMap.put(a.getQuestionId(), a);
       }
       int durationSeconds = exam.getDurationMinutes() * 60;
    %>

    <!-- Timer -->
    <div class="timer-box">
        <div class="card border-danger">
            <div class="card-body text-center">
                <h5 class="card-title text-danger">Time Remaining</h5>
                <h2 id="timer" class="text-danger fw-bold">--:--:--</h2>
            </div>
        </div>
    </div>

    <!-- Violation Alert -->
    <div id="violationAlert" class="alert alert-danger violation-alert">
        <strong>Warning!</strong> Tab switch detected! This has been logged.
    </div>

    <div class="container exam-container mt-4">
        <div class="card shadow">
            <div class="card-header bg-primary text-white">
                <h4 class="mb-0"><%= exam.getExamTitle() %></h4>
                <small>Duration: <%= exam.getDurationMinutes() %> minutes | Total Marks: <%= exam.getTotalMarks() %></small>
            </div>
            <div class="card-body">
                <!-- Question Navigation -->
                <div class="question-nav">
                    <% for (int i = 0; i < questions.size(); i++) {
                        Question q = questions.get(i);
                        Answer saved = answerMap.get(q.getQuestionId());
                        String btnClass = "btn-outline-secondary";
                        if (saved != null && (saved.getSelectedOptionId() != null || saved.getSubjectiveAnswer() != null)) {
                            btnClass = "btn-success answered";
                        }
                    %>
                    <button type="button" class="btn <%= btnClass %>" onclick="showQuestion(<%= i %>)"><%= i + 1 %></button>
                    <% } %>
                </div>

                <form id="examForm" action="${pageContext.request.contextPath}/attempt" method="post">
                    <input type="hidden" name="action" value="submit">
                    <input type="hidden" name="attemptId" value="<%= attempt.getAttemptId() %>">
                    <input type="hidden" name="examId" value="<%= exam.getExamId() %>">

                    <% for (int i = 0; i < questions.size(); i++) {
                        Question q = questions.get(i);
                        Answer saved = answerMap.get(q.getQuestionId());
                    %>
                    <div class="question-card <%= i == 0 ? "active" : "" %>" data-index="<%= i %>" data-question-id="<%= q.getQuestionId() %>">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h5>Question <%= q.getQuestionNumber() %> <span class="badge bg-info"><%= q.getQuestionType() %></span></h5>
                            <span class="badge bg-secondary"><%= q.getMarks() %> marks</span>
                        </div>
                        <p class="lead"><%= q.getQuestionText() %></p>

                        <% if (q.isMCQ() && q.getOptions() != null) { %>
                            <div class="list-group">
                                <% for (Option opt : q.getOptions()) {
                                    boolean isSelected = saved != null && saved.getSelectedOptionId() != null
                                            && saved.getSelectedOptionId() == opt.getOptionId();
                                %>
                                <label class="list-group-item">
                                    <input class="form-check-input me-2" type="radio"
                                           name="q_<%= q.getQuestionId() %>"
                                           value="<%= opt.getOptionId() %>"
                                           <%= isSelected ? "checked" : "" %>
                                           onchange="saveAnswer(<%= q.getQuestionId() %>, <%= opt.getOptionId() %>, null)">
                                    <%= opt.getOptionText() %>
                                </label>
                                <% } %>
                            </div>
                        <% } else { %>
                            <textarea class="form-control" rows="5" id="txt_<%= q.getQuestionId() %>"
                                      onblur="saveAnswer(<%= q.getQuestionId() %>, null, this.value)"
                                      placeholder="Type your answer here..."><%= saved != null && saved.getSubjectiveAnswer() != null ? saved.getSubjectiveAnswer() : "" %></textarea>
                        <% } %>

                        <div class="mt-3 d-flex justify-content-between">
                            <button type="button" class="btn btn-outline-warning btn-sm" onclick="markForReview(<%= i %>)">
                                Mark for Review
                            </button>
                            <div>
                                <% if (i > 0) { %>
                                <button type="button" class="btn btn-outline-secondary btn-sm" onclick="showQuestion(<%= i - 1 %>)">Previous</button>
                                <% } %>
                                <% if (i < questions.size() - 1) { %>
                                <button type="button" class="btn btn-primary btn-sm" onclick="showQuestion(<%= i + 1 %>)">Next</button>
                                <% } %>
                            </div>
                        </div>
                    </div>
                    <% } %>

                    <div class="mt-4 text-center">
                        <button type="submit" class="btn btn-success btn-lg" onclick="return confirm('Are you sure you want to submit?')">
                            Submit Exam
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        const attemptId = <%= attempt.getAttemptId() %>;
        let timeRemaining = <%= durationSeconds %>;
        const timerEl = document.getElementById('timer');

        // Timer countdown
        function updateTimer() {
            const hours = Math.floor(timeRemaining / 3600);
            const minutes = Math.floor((timeRemaining % 3600) / 60);
            const seconds = timeRemaining % 60;
            timerEl.textContent = String(hours).padStart(2,'0') + ':' + String(minutes).padStart(2,'0') + ':' + String(seconds).padStart(2,'0');

            if (timeRemaining <= 300) { // Last 5 minutes
                timerEl.classList.add('text-danger');
                timerEl.parentElement.parentElement.classList.add('border-danger');
            }

            if (timeRemaining <= 0) {
                alert('Time is up! Your exam will be submitted automatically.');
                document.getElementById('examForm').submit();
                return;
            }
            timeRemaining--;
        }
        setInterval(updateTimer, 1000);
        updateTimer();

        // Question navigation
        function showQuestion(index) {
            document.querySelectorAll('.question-card').forEach((el, i) => {
                el.classList.toggle('active', i === index);
            });
        }

        // Mark for review
        function markForReview(index) {
            const navBtn = document.querySelectorAll('.question-nav .btn')[index];
            navBtn.classList.toggle('marked');
        }

        // AJAX auto-save answer
        function saveAnswer(questionId, optionId, textAnswer) {
            const payload = {
                attemptId: attemptId,
                questionId: questionId,
                selectedOptionId: optionId,
                subjectiveAnswer: textAnswer
            };

            fetch('${pageContext.request.contextPath}/answer', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
            .then(r => r.json())
            .then(data => {
                if (data.success) {
                    // Update nav button to show answered
                    const card = document.querySelector('[data-question-id="' + questionId + '"]');
                    const idx = parseInt(card.dataset.index);
                    document.querySelectorAll('.question-nav .btn')[idx].classList.remove('btn-outline-secondary');
                    document.querySelectorAll('.question-nav .btn')[idx].classList.add('btn-success', 'answered');
                }
            })
            .catch(err => console.error('Auto-save failed:', err));
        }

        // Anti-cheating: Tab switch detection
        let violationCount = 0;
        document.addEventListener('visibilitychange', function() {
            if (document.hidden) {
                violationCount++;
                document.getElementById('violationAlert').style.display = 'block';
                setTimeout(() => document.getElementById('violationAlert').style.display = 'none', 5000);

                // Log violation via AJAX
                fetch('${pageContext.request.contextPath}/violation', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'type=TAB_SWITCH&attemptId=' + attemptId + '&count=' + violationCount
                });
            }
        });

        // Prevent right-click
        document.addEventListener('contextmenu', e => e.preventDefault());

        // Prevent copy-paste
        document.addEventListener('copy', e => e.preventDefault());
        document.addEventListener('paste', e => e.preventDefault());
        document.addEventListener('cut', e => e.preventDefault());
    </script>
</body>
</html>

