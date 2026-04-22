<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Internship &amp; Examination Management System</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        :root {
            --primary-color: #2563eb;
            --primary-dark: #1e40af;
            --primary-light: #3b82f6;
            --secondary-color: #7c3aed;
            --accent-color: #f59e0b;
            --success-color: #10b981;
            --dark-bg: #111827;
            --light-bg: #f9fafb;
            --text-primary: #1f2937;
            --text-secondary: #6b7280;
            --border-color: #e5e7eb;
        }

        html {
            scroll-behavior: smooth;
        }

        body {
            font-family: 'Inter', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: var(--text-primary);
            line-height: 1.6;
            overflow-x: hidden;
        }

        /* Navigation Bar */
        nav {
            position: fixed;
            top: 0;
            width: 100%;
            background: white;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            z-index: 1000;
            padding: 1rem 0;
        }

        .nav-container {
            max-width: 1200px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 2rem;
        }

        .logo {
            font-size: 1.5rem;
            font-weight: 700;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            text-decoration: none;
        }

        .nav-links {
            display: flex;
            gap: 2rem;
            list-style: none;
        }

        .nav-links a {
            text-decoration: none;
            color: var(--text-primary);
            font-weight: 500;
            transition: color 0.3s ease;
        }

        .nav-links a:hover {
            color: var(--primary-color);
        }

        .nav-buttons {
            display: flex;
            gap: 1rem;
        }

        /* Hero Section */
        .hero {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            color: white;
            padding: 120px 2rem 80px;
            text-align: center;
            margin-top: 60px;
            position: relative;
            overflow: hidden;
        }

        .hero::before {
            content: '';
            position: absolute;
            top: -50%;
            right: -10%;
            width: 500px;
            height: 500px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
            z-index: 0;
        }

        .hero-content {
            max-width: 800px;
            margin: 0 auto;
            position: relative;
            z-index: 1;
        }

        .hero h1 {
            font-size: 3.5rem;
            margin-bottom: 1.5rem;
            font-weight: 800;
            line-height: 1.2;
        }

        .hero p {
            font-size: 1.3rem;
            margin-bottom: 2rem;
            opacity: 0.95;
        }

        .cta-buttons {
            display: flex;
            gap: 1.5rem;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn {
            padding: 14px 32px;
            font-size: 1rem;
            font-weight: 600;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s ease;
            display: inline-block;
            text-align: center;
        }

        .btn-primary {
            background: white;
            color: var(--primary-color);
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
        }

        .btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
        }

        .btn-secondary {
            background: transparent;
            color: white;
            border: 2px solid white;
        }

        .btn-secondary:hover {
            background: white;
            color: var(--primary-color);
        }

        /* Features Section */
        .features-section {
            padding: 80px 2rem;
            background: var(--light-bg);
        }

        .section-title {
            text-align: center;
            font-size: 2.5rem;
            margin-bottom: 3rem;
            color: var(--text-primary);
        }

        .features-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 2rem;
            max-width: 1200px;
            margin: 0 auto;
        }

        .feature-card {
            background: white;
            padding: 2.5rem;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            transition: all 0.3s ease;
            border-top: 4px solid var(--primary-color);
        }

        .feature-card:nth-child(2) {
            border-top-color: var(--secondary-color);
        }

        .feature-card:nth-child(3) {
            border-top-color: var(--accent-color);
        }

        .feature-card:nth-child(4) {
            border-top-color: var(--success-color);
        }

        .feature-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12);
        }

        .feature-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
        }

        .feature-card h3 {
            font-size: 1.3rem;
            margin-bottom: 0.8rem;
            color: var(--text-primary);
        }

        .feature-card p {
            color: var(--text-secondary);
            line-height: 1.7;
        }

        /* Stats Section */
        .stats-section {
            padding: 80px 2rem;
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            color: white;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 3rem;
            max-width: 1200px;
            margin: 0 auto;
            text-align: center;
        }

        .stat {
            padding: 1rem;
        }

        .stat-number {
            font-size: 3rem;
            font-weight: 800;
            margin-bottom: 0.5rem;
        }

        .stat-label {
            font-size: 1.1rem;
            opacity: 0.9;
        }

        /* Benefits Section */
        .benefits-section {
            padding: 80px 2rem;
            background: white;
        }

        .benefits-container {
            max-width: 1200px;
            margin: 0 auto;
        }

        .benefits-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 4rem;
            align-items: center;
        }

        .benefits-list {
            list-style: none;
        }

        .benefit-item {
            margin-bottom: 1.5rem;
            display: flex;
            gap: 1rem;
        }

        .benefit-check {
            color: var(--success-color);
            font-size: 1.5rem;
            font-weight: bold;
            flex-shrink: 0;
        }

        .benefit-item p {
            color: var(--text-secondary);
        }

        .benefits-image {
            background: var(--light-bg);
            padding: 3rem;
            border-radius: 12px;
            text-align: center;
            font-size: 4rem;
        }

        /* CTA Section */
        .cta-section {
            padding: 80px 2rem;
            background: var(--dark-bg);
            color: white;
            text-align: center;
        }

        .cta-content {
            max-width: 600px;
            margin: 0 auto;
        }

        .cta-section h2 {
            font-size: 2.5rem;
            margin-bottom: 1.5rem;
        }

        .cta-section p {
            font-size: 1.1rem;
            opacity: 0.9;
            margin-bottom: 2rem;
        }

        /* Footer */
        footer {
            background: var(--dark-bg);
            color: var(--text-secondary);
            padding: 3rem 2rem;
            text-align: center;
            border-top: 1px solid var(--border-color);
        }

        .footer-content {
            max-width: 1200px;
            margin: 0 auto;
        }

        .footer-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 2rem;
            margin-bottom: 2rem;
            text-align: left;
        }

        .footer-section h4 {
            color: white;
            margin-bottom: 1rem;
        }

        .footer-section ul {
            list-style: none;
        }

        .footer-section a {
            color: var(--text-secondary);
            text-decoration: none;
            transition: color 0.3s ease;
        }

        .footer-section a:hover {
            color: var(--primary-light);
        }

        .footer-bottom {
            padding-top: 2rem;
            border-top: 1px solid var(--border-color);
        }

        /* Responsive Design */
        @media (max-width: 768px) {
            .hero h1 {
                font-size: 2.5rem;
            }

            .hero p {
                font-size: 1rem;
            }

            .section-title {
                font-size: 2rem;
            }

            .nav-links {
                display: none;
            }

            .benefits-grid {
                grid-template-columns: 1fr;
                gap: 2rem;
            }

            .hero {
                padding: 100px 1rem 60px;
            }

            .cta-section h2 {
                font-size: 1.8rem;
            }
        }

        /* Animation */
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .feature-card {
            animation: fadeInUp 0.6s ease-out;
        }

        .feature-card:nth-child(2) {
            animation-delay: 0.1s;
        }

        .feature-card:nth-child(3) {
            animation-delay: 0.2s;
        }

        .feature-card:nth-child(4) {
            animation-delay: 0.3s;
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav>
        <div class="nav-container">
            <a href="index.jsp" class="logo">🎓 InternshipHub</a>
            <ul class="nav-links">
                <li><a href="#features">Features</a></li>
                <li><a href="#about">About</a></li>
                <li><a href="#contact">Contact</a></li>
            </ul>
            <div class="nav-buttons">
                <a href="login.jsp" class="btn btn-secondary" style="border: 2px solid var(--primary-color); color: var(--primary-color); background: white;">Login</a>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero">
        <div class="hero-content">
            <h1>Transform Your Internship &amp; Exam Journey</h1>
            <p>Connect students with leading companies, manage internships seamlessly, and conduct secure online examinations all in one platform.</p>
            <div class="cta-buttons">
                <a href="login.jsp" class="btn btn-primary">Get Started</a>
                <a href="#features" class="btn btn-secondary">Learn More</a>
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="features-section" id="features">
        <h2 class="section-title">Powerful Features</h2>
        <div class="features-grid">
            <div class="feature-card">
                <div class="feature-icon">📋</div>
                <h3>Company Management</h3>
                <p>Post internship opportunities, manage applications, and connect with talented students. Track application status and build your talent pipeline efficiently.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">👥</div>
                <h3>Student Portal</h3>
                <p>Browse curated internship opportunities, submit applications, and track your application status. View detailed company and internship information.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">🎯</div>
                <h3>Online Examinations</h3>
                <p>Conduct secure online exams with auto-save functionality. Multiple question types, timer management, and instant result generation.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">📊</div>
                <h3>Analytics &amp; Reports</h3>
                <p>Generate comprehensive reports on internship placements, exam performance, and student progress. Data-driven insights for better decision making.</p>
            </div>
        </div>
    </section>

    <!-- Stats Section -->
    <section class="stats-section" id="about">
        <div class="stats-grid">
            <div class="stat">
                <div class="stat-number">500+</div>
                <div class="stat-label">Active Users</div>
            </div>
            <div class="stat">
                <div class="stat-number">100+</div>
                <div class="stat-label">Partner Companies</div>
            </div>
            <div class="stat">
                <div class="stat-number">95%</div>
                <div class="stat-label">Placement Rate</div>
            </div>
            <div class="stat">
                <div class="stat-number">24/7</div>
                <div class="stat-label">System Uptime</div>
            </div>
        </div>
    </section>

    <!-- Benefits Section -->
    <section class="benefits-section">
        <div class="benefits-container">
            <div class="benefits-grid">
                <div>
                    <h2 class="section-title" style="text-align: left;">Why Choose Us?</h2>
                    <ul class="benefits-list">
                        <li class="benefit-item">
                            <div class="benefit-check">✓</div>
                            <p><strong>Seamless Integration</strong> - All-in-one platform for internships and exams</p>
                        </li>
                        <li class="benefit-item">
                            <div class="benefit-check">✓</div>
                            <p><strong>Real-time Updates</strong> - Stay informed with instant notifications and updates</p>
                        </li>
                        <li class="benefit-item">
                            <div class="benefit-check">✓</div>
                            <p><strong>Secure &amp; Reliable</strong> - Enterprise-grade security for all user data</p>
                        </li>
                        <li class="benefit-item">
                            <div class="benefit-check">✓</div>
                            <p><strong>Easy to Use</strong> - Intuitive interface designed for all skill levels</p>
                        </li>
                        <li class="benefit-item">
                            <div class="benefit-check">✓</div>
                            <p><strong>24/7 Support</strong> - Dedicated support team ready to assist you</p>
                        </li>
                        <li class="benefit-item">
                            <div class="benefit-check">✓</div>
                            <p><strong>Mobile Friendly</strong> - Access from any device, anywhere, anytime</p>
                        </li>
                    </ul>
                </div>
                <div class="benefits-image">
                    🚀
                    <p style="font-size: 1rem; margin-top: 1rem; color: var(--text-secondary);">Ready to launch your career?</p>
                </div>
            </div>
        </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section" id="contact">
        <div class="cta-content">
            <h2>Ready to Get Started?</h2>
            <p>Join thousands of students and companies already using InternshipHub to achieve their goals.</p>
            <a href="login.jsp" class="btn btn-primary">Login Now</a>
        </div>
    </section>

    <!-- Footer -->
    <footer>
        <div class="footer-content">
            <div class="footer-grid">
                <div class="footer-section">
                    <h4>Company</h4>
                    <ul>
                        <li><a href="#about">About Us</a></li>
                        <li><a href="#features">Features</a></li>
                        <li><a href="#">Careers</a></li>
                        <li><a href="#">Blog</a></li>
                    </ul>
                </div>
                <div class="footer-section">
                    <h4>Product</h4>
                    <ul>
                        <li><a href="#">For Students</a></li>
                        <li><a href="#">For Companies</a></li>
                        <li><a href="#">For Admins</a></li>
                        <li><a href="#">Pricing</a></li>
                    </ul>
                </div>
                <div class="footer-section">
                    <h4>Support</h4>
                    <ul>
                        <li><a href="#">Help Center</a></li>
                        <li><a href="#">Contact Us</a></li>
                        <li><a href="#">Status</a></li>
                        <li><a href="#">Privacy Policy</a></li>
                    </ul>
                </div>
                <div class="footer-section">
                    <h4>Connect With Us</h4>
                    <ul>
                        <li><a href="#">Twitter</a></li>
                        <li><a href="#">LinkedIn</a></li>
                        <li><a href="#">Facebook</a></li>
                        <li><a href="#">Instagram</a></li>
                    </ul>
                </div>
            </div>
            <div class="footer-bottom">
                <p>&copy; 2026 MIT World Peace University. All Rights Reserved. | InternshipHub v1.0.0</p>
            </div>
        </div>
    </footer>
</body>
</html>
