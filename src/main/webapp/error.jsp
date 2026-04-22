<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Internship &amp; Examination System</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .error-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
            max-width: 500px;
            width: 90%;
            padding: 40px;
            text-align: center;
        }

        .error-icon {
            font-size: 4em;
            margin-bottom: 20px;
        }

        h1 {
            color: #c33;
            margin-bottom: 15px;
            font-size: 2em;
        }

        .error-code {
            color: #999;
            font-size: 1.2em;
            margin-bottom: 15px;
            font-family: 'Courier New', monospace;
        }

        .error-message {
            color: #666;
            font-size: 1em;
            margin-bottom: 30px;
            line-height: 1.6;
        }

        .error-details {
            background: #f8f9fa;
            border-left: 4px solid #c33;
            padding: 15px;
            margin-bottom: 30px;
            text-align: left;
            border-radius: 5px;
        }

        .error-details p {
            color: #666;
            font-size: 0.9em;
            margin: 5px 0;
            font-family: 'Courier New', monospace;
        }

        .action-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn {
            padding: 12px 25px;
            font-size: 1em;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s ease;
            display: inline-block;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }

        .btn-secondary {
            background: #f0f0f0;
            color: #333;
        }

        .btn-secondary:hover {
            background: #e0e0e0;
        }

        .footer {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #ddd;
            color: #999;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        <h1>Oops! Something Went Wrong</h1>

        <%
            Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
            String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
            Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");

            if (statusCode == null) statusCode = 500;
            if (errorMessage == null) errorMessage = "An unexpected error occurred.";
        %>

        <div class="error-code">Error Code: <%= statusCode %></div>

        <p class="error-message"><%= errorMessage %></p>

        <% if (exception != null) { %>
        <div class="error-details">
            <p><strong>Details:</strong></p>
            <p><%= exception.getMessage() != null ? exception.getMessage() : exception.getClass().getName() %></p>
        </div>
        <% } %>

        <div class="action-buttons">
            <a href="index.jsp" class="btn btn-primary">Go to Home</a>
            <a href="javascript:history.back()" class="btn btn-secondary">Go Back</a>
        </div>

        <div class="footer">
            <p>If this error persists, please contact support.</p>
            <p>Reference: <%= System.currentTimeMillis() %></p>
        </div>
    </div>
</body>
</html>
