#!/usr/bin/env python3
import os
import stat
import subprocess
import sys
import re
from pathlib import Path

class GitHookInstallationError(Exception):
    """Custom exception for git hook installation errors."""
    pass

class GitHooksInstaller:
    def __init__(self):
        try:
            self.project_root = subprocess.check_output(
                ['git', 'rev-parse', '--show-toplevel'],
                universal_newlines=True
            ).strip()
        except subprocess.CalledProcessError:
            raise GitHookInstallationError("Not a git repository")
        self.hooks_dir = os.path.join(self.project_root, '.git', 'hooks')

    def create_pre_push_hook(self):
        pre_push_content = """#!/usr/bin/env python3
import os
import sys
import subprocess
import re
import stat
from pathlib import Path

def run_command(command, error_message):
    try:
        process = subprocess.run(command, check=True, capture_output=True, text=True)
        print(process.stdout)
    except subprocess.CalledProcessError as e:
        print(f"❌ {error_message}")
        print(f"Error output: {e.stderr}")
        sys.exit(1)

def get_changed_files():
    try:
        # Get files changed in commits being pushed
        changed_files = subprocess.check_output(
            ['git', 'diff', '--name-only', 'origin/master', '--', 'HEAD'],
            universal_newlines=True
        ).splitlines()

        # Get unique directories containing changed files
        changed_dirs = set()
        for file in changed_files:
            parts = Path(file).parts
            if len(parts) > 0:
                changed_dirs.add(parts[0])

        return changed_dirs
    except subprocess.CalledProcessError as e:
        print(f"❌ Failed to get changed files: {e}")
        return set()

def check_github_issue_reference():
    try:
        # Get current branch name
        branch_name = subprocess.check_output(
            ['git', 'rev-parse', '--abbrev-ref', 'HEAD'],
            universal_newlines=True
        ).strip()

        # Get commit messages for commits being pushed
        commit_msgs = subprocess.check_output(
            ['git', 'log', 'origin/master..HEAD', '--pretty=%B'],
            universal_newlines=True
        ).strip()
    except subprocess.CalledProcessError:
        print("❌ Failed to get branch name or commit messages")
        return False

    # Patterns to match issue references
    issue_patterns = [
        r'#\\d+',                    # #123
        r'issue-\\d+',               # issue-123
        r'feature/(?:GH-)?#?\\d+',   # feature/GH-123 or feature/#123
        r'[a-zA-Z-]+\\d+'           # issue7 or automated7
    ]

    # Check branch name
    branch_has_issue = any(re.search(pattern, branch_name) for pattern in issue_patterns)

    # Check commit messages
    commits_have_issue = any(re.search(pattern, commit_msgs) for pattern in issue_patterns)

    if not (branch_has_issue or commits_have_issue):
        print("No GitHub issue reference found in branch name or commit messages.")
        print("Please include an issue reference (e.g., #123, GH-123, issue-123)")
        print(f"Current branch: {branch_name}")
        return False

    return True

def main():
    # Get project root directory (where .git is)
    project_root = subprocess.check_output(
        ['git', 'rev-parse', '--show-toplevel'],
        universal_newlines=True
    ).strip()

    # Get files that are changed in commits being pushed
    changed_dirs = get_changed_files()
    if not changed_dirs:
        print("No changes detected to check")
        return 0

    # Find gradlew files only in changed directories
    gradlew_paths = []
    for changed_dir in changed_dirs:
        service_path = os.path.join(project_root, changed_dir)
        gradlew_path = os.path.join(service_path, 'gradlew')
        if os.path.exists(gradlew_path):
            gradlew_paths.append(gradlew_path)

    if not gradlew_paths:
        print("No Gradle projects found in changed directories.")
        sys.exit(1)

    # First check for GitHub issue reference
    if not check_github_issue_reference():
        sys.exit(1)

    print("Running pre-push checks...")

    # Run checks for each changed service with gradlew
    for gradlew_path in gradlew_paths:
        service_dir = os.path.dirname(gradlew_path)
        service_name = os.path.basename(service_dir)
        print(f"\\nRunning checks for {service_name}...")

        # Make gradlew executable
        Path(gradlew_path).chmod(Path(gradlew_path).stat().st_mode | stat.S_IEXEC)

        # Change to service directory
        os.chdir(service_dir)

        # Define all checks that need to run
        checks = [
            (
                [gradlew_path, 'test', '--stacktrace'],
                f"Unit tests failed in {service_name}. Please fix failing tests before pushing."
            ),
            (
                [gradlew_path, 'integrationTest', '--stacktrace'],
                f"Integration tests failed in {service_name}. Please fix failing tests before pushing."
            ),
            (
                [gradlew_path, 'jacocoTestReport', '--stacktrace'],
                f"JaCoCo report generation failed in {service_name}."
            ),
            (
                [gradlew_path, 'jacocoTestCoverageVerification', '--stacktrace'],
                f"Code coverage is below threshold in {service_name}. Please add more tests before pushing."
            ),
            (
                [gradlew_path, 'dependencyCheckAnalyze', '--stacktrace'],
                f"Dependency audit failed in {service_name}. Please review and fix security issues."
            ),
            (
                [gradlew_path, 'build', '--stacktrace'],
                f"Build failed in {service_name}. Please fix build issues before pushing."
            )
        ]

        # Run all checks
        for command, error_msg in checks:
            print(f"Running {command[1]}...")
            run_command(command, error_msg)

    print("All checks passed!")
    return 0

if __name__ == "__main__":
    sys.exit(main())"""

        pre_push_path = os.path.join(self.hooks_dir, 'pre-push')
        with open(pre_push_path, 'w') as f:
            f.write(pre_push_content)
        os.chmod(pre_push_path, stat.S_IRWXU | stat.S_IRGRP | stat.S_IXGRP | stat.S_IROTH | stat.S_IXOTH)

if __name__ == '__main__':
    installer = GitHooksInstaller()
    installer.create_pre_push_hook()