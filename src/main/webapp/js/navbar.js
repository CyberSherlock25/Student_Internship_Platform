(function () {
    function applyTheme(theme) {
        var root = document.documentElement;
        root.setAttribute('data-theme', theme === 'dark' ? 'dark' : 'light');
        var themeBtn = document.getElementById('themeToggleBtn');
        if (themeBtn) {
            var icon = themeBtn.querySelector('i');
            if (icon) {
                icon.className = theme === 'dark' ? 'fa-regular fa-sun' : 'fa-regular fa-moon';
            }
        }
    }

    function getPageKeyFromPath() {
        var path = window.location.pathname.toLowerCase();
        if (path.indexOf('dashboard') >= 0) return 'dashboard';
        if (path.indexOf('browse-internships') >= 0 || path.indexOf('internships') >= 0) return 'internships';
        if (path.indexOf('applications') >= 0) return 'applications';
        if (path.indexOf('exam') >= 0) return 'exams';
        if (path.indexOf('profile') >= 0) return 'profile';
        if (path.indexOf('companies') >= 0) return 'companies';
        return '';
    }

    function activateCurrentLink() {
        var page = document.body ? document.body.getAttribute('data-page') : '';
        if (!page) {
            page = getPageKeyFromPath();
        }
        document.querySelectorAll('.app-nav-link').forEach(function (link) {
            link.classList.toggle('active', link.getAttribute('data-page') === page);
        });
    }

    function toggleSidebar() {
        var isMobile = window.matchMedia('(max-width: 992px)').matches;
        if (isMobile) {
            document.body.classList.toggle('sidebar-open');
            return;
        }
        var collapsed = document.body.classList.toggle('sidebar-collapsed');
        localStorage.setItem('nav-collapsed', collapsed ? '1' : '0');
    }

    document.addEventListener('DOMContentLoaded', function () {
        var theme = localStorage.getItem('theme') || 'light';
        applyTheme(theme);

        if (!window.matchMedia('(max-width: 992px)').matches && localStorage.getItem('nav-collapsed') === '1') {
            document.body.classList.add('sidebar-collapsed');
        }

        activateCurrentLink();

        var toggleBtn = document.getElementById('navToggleBtn');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', toggleSidebar);
        }

        var backdrop = document.getElementById('appSidebarBackdrop');
        if (backdrop) {
            backdrop.addEventListener('click', function () {
                document.body.classList.remove('sidebar-open');
            });
        }

        var themeBtn = document.getElementById('themeToggleBtn');
        if (themeBtn) {
            themeBtn.addEventListener('click', function () {
                var current = document.documentElement.getAttribute('data-theme') || 'light';
                var next = current === 'dark' ? 'light' : 'dark';
                localStorage.setItem('theme', next);
                applyTheme(next);
            });
        }

        window.addEventListener('resize', function () {
            if (!window.matchMedia('(max-width: 992px)').matches) {
                document.body.classList.remove('sidebar-open');
            }
        });
    });
})();
