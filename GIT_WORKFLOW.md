# Git Workflow Setup Guide

## Initial Git Setup (One-time, by first developer)

### Step 1: Initialize Repository
```bash
cd InternshipExamSystem
git init
git config user.name "Your Name"
git config user.email "your.email@example.com"
```

### Step 2: Create Initial Commit
```bash
git add .
git commit -m "Initial project structure with Maven config, POJOs, and DB utilities"
```

### Step 3: Create Development Branch
```bash
git branch dev
git checkout dev
# OR: git checkout -b dev
```

### Step 4: Push to Remote Repository
First, create a repository on GitHub/GitLab/Bitbucket, then:
```bash
git remote add origin https://github.com/your-org/InternshipExamSystem.git
git branch -M main
git push -u origin main
git push -u origin dev
```

---

## For Each Developer: Feature Branch Setup

### Step 1: Clone the Repository
```bash
git clone https://github.com/your-org/InternshipExamSystem.git
cd InternshipExamSystem
```

### Step 2: Switch to Dev Branch
```bash
git checkout dev
git pull origin dev
```

### Step 3: Create Feature Branch (Based on your assignment)

**Developer 1 (Database & Auth):**
```bash
git checkout -b feature/auth-module
```

**Developer 2 (Admin & Company):**
```bash
git checkout -b feature/admin-module
```

**Developer 3 (Student Module):**
```bash
git checkout -b feature/student-module
```

**Developer 4 (Exam Engine):**
```bash
git checkout -b feature/exam-module
```

### Step 4: Work on Your Feature
```bash
# Edit files...
git status                    # Check changes
git add .                     # Stage changes
git commit -m "Added feature description"
git push origin feature/<your-branch>
```

---

## Pull Request & Merging Workflow

### Step 1: Push to Remote
```bash
git push origin feature/<branch-name>
```

### Step 2: Create Pull Request on GitHub
- Go to GitHub repo
- Click "Pull Requests" tab
- Click "New Pull Request"
- Base: `dev`, Compare: `feature/<branch-name>`
- Add description of changes
- Request review from other team members
- Wait for approval

### Step 3: Merge After Approval
On GitHub:
- Click "Merge pull request"
- Confirm merge
- Delete the feature branch

Locally:
```bash
git checkout dev
git pull origin dev
git branch -d feature/<branch-name>
```

---

## Daily Development Workflow

### At Start of Day:
```bash
git checkout dev
git pull origin dev
git checkout feature/<your-branch>
git merge dev        # Keep feature branch updated with latest dev changes
```

### Before Committing:
```bash
git status           # See what changed
git diff             # Review changes
```

### Committing Changes:
```bash
git add .
git commit -m "Clear, descriptive message about what changed"
git push origin feature/<your-branch>
```

---

## Handling Merge Conflicts

If you encounter conflicts:

```bash
# Pull the latest changes (may have conflicts)
git pull origin dev

# Edit conflicted files (look for <<<<<<, ======, >>>>>> markers)
# Resolve conflicts manually in your IDE

# After resolving:
git add .
git commit -m "Resolved merge conflicts"
git push origin feature/<your-branch>
```

---

## Useful Git Commands

```bash
# View all branches
git branch -a

# View commit history
git log --oneline

# View changes not yet staged
git diff

# View staged changes
git diff --staged

# Revert last commit (if not pushed)
git reset --soft HEAD~1

# Undo all local changes
git checkout -- .

# See who changed which line
git blame <filename>

# Stash changes temporarily
git stash
git stash pop

# View remote branches
git fetch --all
git branch -r

# Rename current branch
git branch -m new-branch-name
```

---

## Important Git Best Practices

1. **Commit Frequently**: Small, logical commits are easier to review and revert if needed
2. **Descriptive Messages**: Write clear commit messages explaining WHY, not just WHAT
3. **Pull Before Push**: Always pull latest changes before pushing to avoid conflicts
4. **Never Force Push**: Use `git push --force` only in emergency situations
5. **Review Before Committing**: Use `git diff` to review changes before commit
6. **Keep Feature Branches Clean**: Don't work on multiple features in one branch
7. **Use .gitignore**: Ensure `target/`, IDE files, credentials are in .gitignore

---

## Branch Protection Rules (Admin to Setup on GitHub)

Enforce code quality:
- Require pull request reviews before merging
- Require status checks to pass (build, tests)
- Require branches to be up to date before merging
- Dismiss stale reviews when new commits are pushed
- Restrict who can push to the branch

---

## Integration Workflow

As features are completed and merged to `dev`:

```bash
# When ready for production release:
git checkout main
git pull origin main
git merge dev --no-ff -m "Release v1.0.0"
git tag -a v1.0.0 -m "Version 1.0.0 - Feature Complete"
git push origin main
git push origin --tags
```

---

## Troubleshooting Common Git Issues

### Issue: Lost commits after reset
```bash
git reflog              # Find the commit you lost
git checkout <commit-hash>
git branch new-branch   # Save the commits
```

### Issue: Wrong branch committed to
```bash
git checkout correct-branch
git cherry-pick <commit-hash>   # Copy commit to correct branch
git checkout wrong-branch
git reset --hard HEAD~1         # Remove from wrong branch
```

### Issue: Need to undo merged changes
```bash
git revert -m 1 <merge-commit-hash>
git push
```
