#!/usr/bin/env python3
import os
import stat
import subprocess
import sys

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
import stat
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
       # Get staged changes
       staged_files = subprocess.check_output(
           ['git', 'diff', '--name-only', '--cached'],
           universal_newlines=True
       ).splitlines()
       
       # Get unstaged changes
       unstaged_files = subprocess.check_output(
           ['git', 'diff', '--name-only'],
           universal_newlines=True
       ).splitlines()
       
       # Get untracked files
       untracked_files = subprocess.check_output(
           ['git', 'ls-files', '--others', '--exclude-standard'],
           universal_newlines=True
       ).splitlines()

       # Combine all changes
       all_changed_files = staged_files + unstaged_files + untracked_files

       # Get unique directories containing changes
       changed_dirs = set()
       for file in all_changed_files:
           parts = Path(file).parts
           if len(parts) > 0:
               changed_dirs.add(parts[0])

       return changed_dirs
   except subprocess.CalledProcessError:
       print("❌ Failed to get changed files")
       return set()

def check_github_issue_reference():
   try:
       # Get current branch name
       branch_name = subprocess.check_output(
           ['git', 'rev-parse', '--abbrev-ref', 'HEAD'],
           universal_newlines=True
       ).strip()

       # Get last commit message
       commit_msg = subprocess.check_output(
           ['git', 'log', '-1', '--pretty=%B'],
           universal_newlines=True
       ).strip()
   except subprocess.CalledProcessError:
       print("❌ Failed to get branch name or commit message")
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
   
   # Check commit message
   commit_has_issue = any(re.search(pattern, commit_msg) for pattern in issue_patterns)

   if not (branch_has_issue or commit_has_issue):
       print("❌ No GitHub issue reference found in branch name or commit message.")
       print("Please include an issue reference (e.g., #123, GH-123, issue-123)")
       print(f"Current branch: {branch_name}")
       print(f"Commit message: {commit_msg}")
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

       # Build check first
       print(f"Building {service_name}...")
       build_command = ([gradlew_path, 'build', '-x', 'test', '-x', 'integrationTest', '-x', 'jacocoTestReport', 
                        '-x', 'jacocoTestCoverageVerification', '-x', 'dependencyCheckAnalyze'], 
                       f"Build failed in {service_name}. Please fix build issues before pushing.")
       run_command(*build_command)

       # Define test checks
       test_checks = [
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

       # Run test checks
       for command, error_msg in test_checks:
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