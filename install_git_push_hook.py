#!/usr/bin/env python3
import os
import stat
import subprocess
import re
import sys
from pathlib import Path

class GitHookInstallationError(Exception):
    """Custom exception for git hook installation errors."""
    pass
class GitHooksInstaller:
    def __init__(self):
        # Get git root directory
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
from pathlib import Path

def run_command(command, error_message):
    try:
        subprocess.run(command, check=True)
    except subprocess.CalledProcessError:
        print(f"❌ {error_message}")
        sys.exit(1)

def get_changed_directories():
    \"""Get directories with changes that are being pushed.\"""
    try:
        # Get list of changed files that are being pushed
        changed_files = subprocess.check_output(
            ['git', 'diff', '--name-only', '--cached', 'HEAD'],
            universal_newlines=True
        ).splitlines()

        # Get unique directories containing changes
        changed_dirs = set()
        for file in changed_files:
            # Get the root directory of the change
            parts = Path(file).parts
            if len(parts) > 0:
                changed_dirs.add(parts[0])

        return changed_dirs
    except subprocess.CalledProcessError:
        print("❌ Failed to get changed files")
        return set()

def check_github_issue_reference():
    # Get current branch name
    try:
        branch_name = subprocess.check_output(
            ['git', 'rev-parse', '--abbrev-ref', 'HEAD'],
            universal_newlines=True
        ).strip()
    except subprocess.CalledProcessError:
        print("❌ Failed to get current branch name")
        return False

    # Get commit message (for commits)
    try:
        commit_msg_file = sys.argv[1] if len(sys.argv) > 1 else None
        if commit_msg_file and os.path.exists(commit_msg_file):
            with open(commit_msg_file, 'r') as f:
                commit_msg = f.read()
        else:
            commit_msg = ""
    except Exception:
        commit_msg = ""

    # Patterns to match issue references
    issue_patterns = [
        r'#\\d+',                    # #123
        r'GH-\\d+',                  # GH-123
        r'issue-\\d+',               # issue-123
        r'feature/(?:GH-)?#?\\d+'    # feature/GH-123 or feature/#123
    ]

    # Check branch name
    branch_has_issue = any(re.search(pattern, branch_name) for pattern in issue_patterns)

    # Check commit message
    commit_has_issue = any(re.search(pattern, commit_msg) for pattern in issue_patterns)

    if not (branch_has_issue or commit_has_issue):
        print("❌ No GitHub issue reference found in branch name or commit message.")
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

    # Get directories with changes
    changed_dirs = get_changed_directories()
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
        print("❌ No Gradle projects found in changed directories.")
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

        # Define checks as tuples of (command, error message)
        checks = [
            (
                [gradlew_path, 'checkstyleMain', 'checkstyleTest'],
                f"Checkstyle failed in {service_name}. Please fix style issues before pushing."
            ),
            (
                [gradlew_path, 'test'],
                f"Unit tests failed in {service_name}. Please fix failing tests before pushing."
            ),
            (
                [gradlew_path, 'integrationTest'],
                f"Integration tests failed in {service_name}. Please fix failing tests before pushing."
            ),
            (
                [gradlew_path, 'jacocoTestCoverageVerification'],
                f"Code coverage is below threshold in {service_name}. Please add more tests before pushing."
            ),
            (
                [gradlew_path, 'dependencyCheckAnalyze'],
                f"Dependency audit failed in {service_name}. Please review and fix security issues."
            )
        ]

        # Run all checks
        for command, error_msg in checks:
            print(f"Running {command[1]}...")
            run_command(command, error_msg)

    print("✅ All checks passed!")
    return 0

if __name__ == '__main__':
    sys.exit(main())
"""
        return pre_push_content

    def install_hooks(self):
        """Install git hooks."""
        try:
            # Create hooks directory if it doesn't exist
            os.makedirs(self.hooks_dir, exist_ok=True)

            # Write pre-push hook
            pre_push_path = os.path.join(self.hooks_dir, 'pre-push')
            with open(pre_push_path, 'w') as f:
                f.write(self.create_pre_push_hook())

            # Make pre-push hook executable
            pre_push_stats = os.stat(pre_push_path)
            os.chmod(pre_push_path, pre_push_stats.st_mode | stat.S_IEXEC)

            print("Git hooks installed successfully!")

        except Exception as e:
            print(f"Error installing git hooks: {str(e)}")
            sys.exit(1)

if __name__ == '__main__':
    installer = GitHooksInstaller()
    installer.install_hooks()